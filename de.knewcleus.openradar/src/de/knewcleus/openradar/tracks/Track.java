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
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.tracks;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import de.knewcleus.openradar.notify.Notifier;
import de.knewcleus.openradar.radardata.IRadarDataPacket;

public class Track extends Notifier implements ITrack {
	protected final static int historySize = 10;//2000;
	protected final IRadarDataPacket history[] = new IRadarDataPacket[historySize];
	protected int headIndex = historySize-1;
	protected int size = 0;
	protected long lastUpdateTimestamp = 0;
	protected boolean lost = false;
	
	protected int age = 0;

	@Override
	public IRadarDataPacket getCurrentState() {
		assert(size>0);
		return history[headIndex];
	}

	@Override
	public IRadarDataPacket getState(int index) {
		if (index>=size) {
			throw new IndexOutOfBoundsException();
		}
		return history[(headIndex + index) % historySize];
	}

	@Override
	public Iterator<IRadarDataPacket> iterator() {
		return new HistoryIterator();
	}

	@Override
	public int size() {
		return size;
	}
	
	@Override
	public boolean isLost() {
		return lost;
	}
	
	public void setLost(boolean lost) {
		if (this.lost==lost) {
			return;
		}
		this.lost = lost;
		notify(new TrackLossStatusNotification(this));
	}
	
	public void addState(IRadarDataPacket state) {
		assert(state!=null);
		if (headIndex==0) {
			headIndex = historySize - 1;
		} else {
			headIndex--;
		}
		if (size<historySize) {
			size++;
		}
		history[headIndex]=state;
		++age;
		notify(new TrackUpdateNotification(this));
	}
	
	public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}
	
	public long getLastUpdateTimestamp() {
		return lastUpdateTimestamp;
	}
	
	protected class HistoryIterator implements Iterator<IRadarDataPacket> {
		protected final int age = Track.this.age;
		protected int index = 0;
		
		@Override
		public boolean hasNext() {
			if (age != Track.this.age) {
				throw new ConcurrentModificationException();
			}
			return index < size;
		}
		
		@Override
		public IRadarDataPacket next() {
			if (age != Track.this.age) {
				throw new ConcurrentModificationException();
			}
			if (index >= size) {
				throw new NoSuchElementException();
			}
			return getState(index++);
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
