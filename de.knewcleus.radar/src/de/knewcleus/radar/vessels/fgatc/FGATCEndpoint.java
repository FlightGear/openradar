package de.knewcleus.radar.vessels.fgatc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodesicUtils;
import de.knewcleus.fgfs.location.GeodesicUtils.GeodesicInformation;
import de.knewcleus.radar.vessels.IPositionDataProvider;
import de.knewcleus.radar.vessels.IPositionUpdateListener;
import de.knewcleus.radar.vessels.PositionUpdate;
import de.knewcleus.radar.vessels.SSRMode;

public class FGATCEndpoint implements Runnable, IPositionDataProvider {
	protected final static Logger logger=Logger.getLogger("de.knewcleus.radar.aircraft.fgatc");
	protected final static GeodesicUtils geodesicUtils=new GeodesicUtils(Ellipsoid.WGS84);
	protected final DatagramSocket datagramSocket;
	protected final static int receiveBufferLength=1024;
	protected final Set<IPositionUpdateListener> listeners=new HashSet<IPositionUpdateListener>();
	protected final Map<Object, ClientStatus> clients=new HashMap<Object, ClientStatus>();
	protected final int timeoutMillis=5000;
	protected long nextRadarUpdate;

	protected class ClientStatus {
		public double positionTime;
		public long expiryTime;
		public double longitude;
		public double latitude;
		public double groundSpeed;
		public double trueCourse;
		public boolean ssrActive;
		public boolean encoderActive;
		public String ssrCode;
		public double pressureAltitude;
	}
	
	public FGATCEndpoint() throws IOException {
		datagramSocket=new DatagramSocket();
		datagramSocket.setSoTimeout(getSecondsBetweenUpdates()*1000);
	}
	
	public FGATCEndpoint(int port) throws IOException {
		datagramSocket=new DatagramSocket(port);
		datagramSocket.setSoTimeout(getSecondsBetweenUpdates()*1000);
	}
	
	@Override
	public void run() {
		Thread myThread=Thread.currentThread();
		nextRadarUpdate=System.currentTimeMillis()+getSecondsBetweenUpdates()*1000;
		while (!myThread.isInterrupted()) {
			try {
				receivePacket();
			} catch (SocketTimeoutException e) {
				/* ignore */
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			expireClients();
			if (System.currentTimeMillis()>=nextRadarUpdate) {
				sendTargetUpdate();
				nextRadarUpdate+=1000*getSecondsBetweenUpdates();
			}
		}
	}
	
	protected void receivePacket() throws IOException {
		byte[] buf=new byte[receiveBufferLength];
		DatagramPacket datagramPacket=new DatagramPacket(buf,buf.length);
		datagramSocket.receive(datagramPacket);
		
		String dataLine=new String(buf, Charset.forName("US-ASCII"));
		dataLine=dataLine.trim();

		ClientAddress address=new ClientAddress(datagramPacket.getAddress(),datagramPacket.getPort());
		ClientStatus clientStatus=new ClientStatus();
		
		logger.finest("Received datagram '"+dataLine+"'");
		
		for (String element: dataLine.split("\\s+")) {
			int eqIndex=element.indexOf('=');
			if (eqIndex==-1) {
				logger.severe("Invalid input packet "+dataLine);
				continue;
			}
			String name=element.substring(0, eqIndex).trim();
			String value=element.substring(eqIndex+1).trim();
			
			if (eqIndex==-1) {
				name=element;
				value="1";
			} else {
				name=element.substring(0, eqIndex).trim();
				value=element.substring(eqIndex+1).trim();
			}
			
			if (name.equals("TIME")) {
				clientStatus.positionTime=Double.parseDouble(value)*Units.SEC;
			} else if (name.equals("LON")) {
				clientStatus.longitude=Double.parseDouble(value)*Units.DEG;
			} else if (name.equals("LAT")) {
				clientStatus.latitude=Double.parseDouble(value)*Units.DEG;
			} else if (name.equals("SSR_SRV")) {
				clientStatus.ssrActive=(Integer.parseInt(value)!=0);
			} else if (name.equals("SSR_CODE")) {
				clientStatus.ssrCode=value;
			} else if (name.equals("ENC_SRV")) {
				clientStatus.encoderActive=(Integer.parseInt(value)!=0);
			} else if (name.equals("ENC_ALT")) {
				clientStatus.pressureAltitude=Double.parseDouble(value)*Units.FT;
			}
		}
		
		clientStatus.expiryTime=System.currentTimeMillis()+timeoutMillis;
		
		updateClientStatus(address,clientStatus);
	}
	
	protected synchronized void expireClients() {
		long currentTime=System.currentTimeMillis();
		Set<Map.Entry<Object, ClientStatus>> clientEntries=clients.entrySet();
		
		Iterator<Map.Entry<Object, ClientStatus>> entryIterator=clientEntries.iterator();
		
		while (entryIterator.hasNext()) {
			Map.Entry<Object, ClientStatus> entry=entryIterator.next();
			if (entry.getValue().expiryTime<=currentTime) {
				Object target=entry.getKey();
				logger.fine("Client "+target+" expired");
				entryIterator.remove();
				fireTargetLost(target);
			}
		}
	}
	
	protected synchronized void sendTargetUpdate() {
		Set<PositionUpdate> targets=new HashSet<PositionUpdate>();
		
		for (Map.Entry<Object, ClientStatus> entry: clients.entrySet()) {
			Object trackID=entry.getKey();
			ClientStatus clientStatus=entry.getValue();
			SSRMode ssrMode;
			
			if (!clientStatus.ssrActive) {
				ssrMode=SSRMode.NONE;
			} else  if (clientStatus.encoderActive) {
				ssrMode=SSRMode.MODEC;
			} else {
				ssrMode=SSRMode.MODEA;
			}
			
			PositionUpdate targetInformation=new PositionUpdate(
					trackID,
					clientStatus.longitude,clientStatus.latitude,
					clientStatus.groundSpeed,clientStatus.trueCourse,
					ssrMode,clientStatus.ssrCode,clientStatus.pressureAltitude);
			
			targets.add(targetInformation);
		}
		logger.info("Radar update:"+targets);
		fireTargetDataUpdated(targets);
	}
	
	protected synchronized void updateClientStatus(Object id, ClientStatus clientStatus) {
		if (clients.containsKey(id)) {
			ClientStatus lastStatus=clients.get(id);
			GeodesicInformation geodesicInformation=geodesicUtils.inverse(lastStatus.longitude, lastStatus.latitude, clientStatus.longitude, clientStatus.latitude);
			final double dt=clientStatus.positionTime-lastStatus.positionTime;
			final double ds=geodesicInformation.length;
			
			clientStatus.groundSpeed=ds/dt;
			clientStatus.trueCourse=geodesicInformation.endAzimuth+180.0*Units.DEG;
			if (clientStatus.trueCourse > 360.0*Units.DEG) {
				clientStatus.trueCourse-=360.0*Units.DEG;
			}
		}
		clients.put(id, clientStatus);
	}
	
	@Override
	public int getSecondsBetweenUpdates() {
		return 1;
	}
	
	@Override
	public synchronized void registerPositionUpdateListener(IPositionUpdateListener consumer) {
		listeners.add(consumer);
	}
	
	@Override
	public synchronized void unregisterPositionUpdateListener(IPositionUpdateListener consumer) {
		listeners.remove(consumer);
	}
	
	protected void fireTargetDataUpdated(Set<PositionUpdate> targets) {
		for (IPositionUpdateListener consumer: listeners) {
			consumer.targetDataUpdated(targets);
		}
	}
	
	protected void fireTargetLost(Object target) {
		for (IPositionUpdateListener consumer: listeners) {
			consumer.targetLost(target);
		}
	}
}
