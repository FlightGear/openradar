/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.radardata.fgatc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.radardata.RadarDataProvider;

public class FGATCEndpoint extends RadarDataProvider implements Runnable {
	protected static Logger log = LogManager.getLogger("de.knewcleus.openradar.radardata.fgatc");
	protected final static int receiveBufferLength=1024;
	
	protected final DatagramSocket datagramSocket;
	
	protected final Map<TargetIdentifier, TargetStatus> targetMap = new HashMap<TargetIdentifier, TargetStatus>();
	
	/**
	 * Time for a single antenna rotation in milliseconds.
	 * 
	 * At most one radar data packet per aircraft is sent per antenna rotation.
	 */
	protected static final int antennaRotationTimeMsecs=1000;
	
	/**
	 * Timeout for removal of stale targets in milliseconds.
	 */
	protected static final int staleTargetTimeoutMsecs=4 * antennaRotationTimeMsecs;

	public FGATCEndpoint() throws IOException {
		datagramSocket=new DatagramSocket();
		datagramSocket.setSoTimeout(antennaRotationTimeMsecs/2);
	}
	
	public FGATCEndpoint(int port) throws IOException {
		datagramSocket=new DatagramSocket(port);
		datagramSocket.setSoTimeout(antennaRotationTimeMsecs/2);
	}
	
	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				processPacket();
			} catch (SocketTimeoutException e) {
				// Do nothing, this is expected
			} catch (IOException e) {
			    log.error("Error in FGFS networking!",e);
			}
			sendRadarPackets();
			removeStaleTargets();
		}
	}
	
	/**
	 * Receive the FG MP data and parse it
	 */
	protected void processPacket() throws IOException {
		byte[] buf=new byte[receiveBufferLength];
		final DatagramPacket datagramPacket=new DatagramPacket(buf,buf.length);
		datagramSocket.receive(datagramPacket);
		
		final TargetIdentifier targetIdentifier=new TargetIdentifier(datagramPacket.getAddress(),datagramPacket.getPort());

		String dataLine=new String(buf, Charset.forName("US-ASCII"));
		dataLine=dataLine.trim();

		log.trace("Received packet '"+dataLine+"' from "+targetIdentifier);

		final PositionPacket newPacket = parsePacket(dataLine);
		
		if (newPacket==null) {
			log.warn("Invalid packet '"+dataLine+"' from "+targetIdentifier+", ignoring");
			return;
		}
		
		final TargetStatus status;
		if (!targetMap.containsKey(targetIdentifier)) {
			status = new TargetStatus(targetIdentifier);
			targetMap.put(targetIdentifier, status);
		} else {
			status = targetMap.get(targetIdentifier);
		}
		
		status.update(newPacket);
	}
	/**
	 * Forward it to the recipients (the screen)
	 */
	protected void sendRadarPackets() {
		for (TargetStatus status: targetMap.values()) {
			if (status.getLastAntennaRotationTime() + antennaRotationTimeMsecs >= status.getLastPacketTime()) {
				/* Was not seen for at least one rotation */
				continue;
			}
			/* at least one antenna rotation since the last update, so now we provide the radar data packet */
			publishRadarDataPacket(new RadarDataPacket(status));
			status.setLastAntennaRotationTime(status.getLastPacketTime());
		}
	}
	
	protected void removeStaleTargets() {
		final Iterator<TargetStatus> targetIterator;
		final long earliestStalePacketTime = System.currentTimeMillis() - staleTargetTimeoutMsecs;
		targetIterator = targetMap.values().iterator();
		while (targetIterator.hasNext()) {
			final TargetStatus target = targetIterator.next();
			if (target.getLastPacketTime() < earliestStalePacketTime) { 
				targetIterator.remove();
			}
		}
	}
	
	protected PositionPacket parsePacket(String dataLine) {
		boolean hadPositionTime = false, hadLongitude = false, hadLatitude = false;
		boolean hadSSRActive = false, hadSSRCode = false;
		boolean hadEncoderActive = false, hadEncoderAlt = false;
		
		float positionTime=0.0f;
		double longitude=0.0, latitude=0.0;
		boolean ssrActive = false, encoderActive = false;
		String ssrCode = "0000";
		float encoderAltitude = 0.0f;
		boolean specialPurposeIndicator = false;
		
		for (String element: dataLine.split("\\s+")) {
			int eqIndex=element.indexOf('=');
			if (eqIndex==-1) {
				return null;
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
				positionTime=Float.parseFloat(value)*(float)Units.SEC;
				hadPositionTime = true;
			} else if (name.equals("LON")) {
				longitude=Double.parseDouble(value)*Units.DEG;
				hadLongitude = true;
			} else if (name.equals("LAT")) {
				latitude=Double.parseDouble(value)*Units.DEG;
				hadLatitude = true;
			} else if (name.equals("SSR_SRV")) {
				ssrActive=(Integer.parseInt(value)!=0);
				hadSSRActive = true;
			} else if (name.equals("SSR_CODE")) {
				ssrCode=value;
				hadSSRCode = true;
			} else if (name.equals("SSR_SPI")) {
				specialPurposeIndicator=(Integer.parseInt(value)!=0);
			} else if (name.equals("ENC_SRV")) {
				encoderActive=(Integer.parseInt(value)!=0);
				hadEncoderActive = true;
			} else if (name.equals("ENC_ALT")) {
				encoderAltitude=Float.parseFloat(value)*(float)Units.FT;
				hadEncoderAlt = true;
			}
		}
		
		if (!hadPositionTime || !hadLongitude || !hadLatitude || !hadSSRActive || !hadEncoderActive) {
			return null;
		}
		
		if (ssrActive && !hadSSRCode) {
			return null;
		}
		
		if (encoderActive && !hadEncoderAlt ) {
			return null;
		}
		
		if (!encoderActive) {
			encoderAltitude = 0.0f;
		}
		
		if (!ssrActive) {
			ssrCode = "0000";
		}
		return new PositionPacket(positionTime, longitude, latitude,
				ssrActive, ssrCode, encoderActive, encoderAltitude, specialPurposeIndicator);
	}
}
