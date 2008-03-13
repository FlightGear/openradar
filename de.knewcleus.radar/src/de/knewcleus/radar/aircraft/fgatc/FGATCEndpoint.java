package de.knewcleus.radar.aircraft.fgatc;

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
import de.knewcleus.radar.aircraft.IRadarDataConsumer;
import de.knewcleus.radar.aircraft.IRadarDataProvider;
import de.knewcleus.radar.aircraft.RadarTargetInformation;
import de.knewcleus.radar.aircraft.SSRMode;

public class FGATCEndpoint implements Runnable, IRadarDataProvider {
	protected final static Logger logger=Logger.getLogger("de.knewcleus.radar.aircraft.fgatc");
	protected final DatagramSocket datagramSocket;
	protected final static int receiveBufferLength=1024;
	protected final Set<IRadarDataConsumer> consumers=new HashSet<IRadarDataConsumer>();
	protected final Map<Object, ClientStatus> clients=new HashMap<Object, ClientStatus>();
	protected final int timeoutMillis=5000;
	protected long nextRadarUpdate;

	protected class ClientStatus {
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
				sendRadarUpdate();
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
			String name=element.substring(0, eqIndex).trim();
			String value=element.substring(eqIndex+1).trim();
			
			if (eqIndex==-1) {
				name=element;
				value="1";
			} else {
				name=element.substring(0, eqIndex).trim();
				value=element.substring(eqIndex+1).trim();
			}
			
			if (name.equals("LON")) {
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
				fireRadarTargetLost(target);
			}
		}
	}
	
	protected synchronized void sendRadarUpdate() {
		Set<RadarTargetInformation> targets=new HashSet<RadarTargetInformation>();
		
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
			
			RadarTargetInformation targetInformation=new RadarTargetInformation(
					trackID,
					clientStatus.longitude,clientStatus.latitude,
					clientStatus.groundSpeed,clientStatus.trueCourse,
					ssrMode,clientStatus.ssrCode,clientStatus.pressureAltitude);
			
			targets.add(targetInformation);
		}
		logger.info("Radar update:"+targets);
		fireRadarDataUpdated(targets);
	}
	
	protected synchronized void updateClientStatus(Object id, ClientStatus clientStatus) {
		// TODO: determine groundspeed and true course for existing targets
		clients.put(id, clientStatus);
	}
	
	@Override
	public int getSecondsBetweenUpdates() {
		return 1;
	}
	
	@Override
	public synchronized void registerRadarDataConsumer(IRadarDataConsumer consumer) {
		consumers.add(consumer);
	}
	
	@Override
	public synchronized void unregisterRadarDataConsumer(IRadarDataConsumer consumer) {
		consumers.remove(consumer);
	}
	
	protected void fireRadarDataUpdated(Set<RadarTargetInformation> targets) {
		for (IRadarDataConsumer consumer: consumers) {
			consumer.radarDataUpdated(targets);
		}
	}
	
	protected void fireRadarTargetLost(Object target) {
		for (IRadarDataConsumer consumer: consumers) {
			consumer.radarTargetLost(target);
		}
	}
}
