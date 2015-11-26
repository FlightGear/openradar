/**
 * Copyright (C) 2008-2009 Ralf Gerlich
 * Copyright (C) 2012,2013,2015 Wolfram Wagner 
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
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.radardata.fgmp;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.multiplayer.IPlayerRegistry;
import de.knewcleus.fgfs.multiplayer.MultiplayerClient;
import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.radardata.IRadarDataProvider;
import de.knewcleus.openradar.radardata.IRadarDataRecipient;

public class FGMPClient<T extends TargetStatus> extends MultiplayerClient<T> implements IRadarDataProvider {
	protected final Position linearVelocity=new Position();
	protected final Position orientation=new Position();
	protected volatile String callsign=null;
	protected final String model;
	protected final Position position;
	protected long lastAntennaRotationTime;
	
	/**
	 * Time for a single antenna rotation in milliseconds.
	 * 
	 * At most one radar data packet per aircraft is sent per antenna rotation.
	 */
	protected volatile int antennaRotationTimeMsecs=1000;
	
	/**
	 * Timeout for removal of stale targets in milliseconds.
	 */
	protected final int staleTargetTimeoutMsecs= 10 * antennaRotationTimeMsecs;

	
	protected final Set<IRadarDataRecipient> recipients=new HashSet<IRadarDataRecipient>();

	public FGMPClient(IPlayerRegistry<T> playerRegistry, String callsign, String model, Position position, String mpServer, int mpServerPort, int mpLocalPort, int antennaRotationTimeMsecs,
	                  boolean packetForward1, String packetForwardHost1, int packetForwardPort1,boolean packetForward2, String packetForwardHost2, int packetForwardPort2) throws IOException {
		super(playerRegistry, mpServer, mpServerPort, mpLocalPort, packetForward1, packetForwardHost1, packetForwardPort1,packetForward2, packetForwardHost2, packetForwardPort2);
		//this.callsign=callsign;
		this.position=position;
		this.model=model;
		this.antennaRotationTimeMsecs = antennaRotationTimeMsecs;
		lastAntennaRotationTime=0;//System.currentTimeMillis();
		// must be last to be sure, everything is initialized!
		startSending();
	}
	
	public synchronized void setAntennaRotationTime(int timeInMs) {
	    this.antennaRotationTimeMsecs = timeInMs;
	}
	
//	@Override
//	protected int getUpdateMillis() {
//		return antennaRotationTimeMsecs;
//	}
	
	@Override
	protected int getPlayerTimeoutMillis() {
		return 1*60*1000; // staleTargetTimeoutMsecs;
	}
	
	@Override
	protected void update() {
		super.update();
		sendRadarPackets();
	}

	@Override
	public synchronized String getCallsign() {
		return callsign;
	}

	
    public synchronized void setCallsign(String callsign) {
        this.callsign = callsign;
    }


	@Override
	public Position getLinearVelocity() {
		return linearVelocity;
	}

	@Override
	public String getModel() {
		return model;
	}

	@Override
	public Position getOrientation() {
		return orientation;
	}

	@Override
	public Position getPosition() {
		return position;
	}
	
	protected synchronized void sendRadarPackets() {
		if (lastAntennaRotationTime+antennaRotationTimeMsecs>System.currentTimeMillis()) {
			return;
		}
		synchronized (getPlayerRegistry()) {
			for (TargetStatus targetStatus: getPlayerRegistry().getPlayers()) {
				final IRadarDataPacket packet=targetStatus.getRadarDataPacket();
				for (IRadarDataRecipient recipient: recipients) {
					recipient.acceptRadarData(this, packet);
				}
			}
		}
		lastAntennaRotationTime=System.currentTimeMillis();
	}
	
	@Override
	public synchronized void registerRecipient(IRadarDataRecipient recipient) {
		recipients.add(recipient);
	}
	
	@Override
	public synchronized void unregisterRecipient(IRadarDataRecipient recipient) {
		recipients.remove(recipient);
	}
}
