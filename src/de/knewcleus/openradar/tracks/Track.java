/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2014,2015 Wolfram Wagner
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
package de.knewcleus.openradar.tracks;

import java.util.ArrayList;
import java.util.List;

import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.radardata.fgmp.TargetStatus;

public class Track implements ITrack {
    private final TargetStatus identifier;
	protected final static int historySize = 1000;//2000;
	protected final ArrayList<IRadarDataPacket> history = new ArrayList<IRadarDataPacket>(historySize);
	protected int headIndex = historySize-1;
	protected int size = 0;
	protected long lastUpdateTimestamp = 0;
	protected boolean lost = false;
	
	protected int age = 0;
    private volatile int tailOffset=0;

    public Track(TargetStatus identifier) {
        this.identifier = identifier;
    }
    
    public TargetStatus getIdentifier() {
        return identifier;
    }
    
	@Override
	public synchronized IRadarDataPacket getCurrentState() {
		assert(size>0);
		return history.get(0);
	}

	@Override
	public synchronized IRadarDataPacket getState(int index) {
		if (index>=history.size()) {
			throw new IndexOutOfBoundsException();
		}
		return history.get(index);
	}

	@Override
	public synchronized int size() {
		return history.size();
	}
	
	@Override
	public synchronized boolean isLost() {
		return lost;
	}
	
	public synchronized void setLost(boolean lost) {
		if (this.lost==lost) {
			return;
		}
		this.lost = lost;
	}
	
	public synchronized void addState(IRadarDataPacket state) {
		assert(state!=null);
		history.add(0, state);
		if(history.size()>historySize) {
		    history.remove(historySize-1);
		}
		++age;
        tailOffset++;
	}
	
	public synchronized void setLastUpdateTimestamp(long lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}
	
	public synchronized long getLastUpdateTimestamp() {
		return lastUpdateTimestamp;
	}
	
    public synchronized int getTailOffset() {
        return tailOffset;
    }
    @Override
    public synchronized void resetTailOffset() {
        tailOffset=0;
        
    }
    
    public synchronized List<IRadarDataPacket> getCopyOfHistory() {
        return new ArrayList<IRadarDataPacket>(history);
    }
}
