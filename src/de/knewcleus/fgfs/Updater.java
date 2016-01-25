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
package de.knewcleus.fgfs;

public class Updater extends Thread {
	protected IUpdateable updateable;
	protected long lastt;
	
	protected final long intervalMillis;
	
	public Updater(IUpdateable updateable) {
		this.updateable=updateable;
		this.intervalMillis=100;
	}
	
	public Updater(IUpdateable updateable, long intervalMillis) {
		this.updateable=updateable;
		this.intervalMillis=intervalMillis;
		setDaemon(true);
	}
	
	public void run() {
		lastt=System.nanoTime();
		while (!isInterrupted()) {
			long t=System.nanoTime();
			double dt=(t-lastt)*1.0E-9;
			updateable.update(dt);
			lastt=t;
			try {
				sleep(intervalMillis);
			} catch (InterruptedException e) {
				/* Sleep was interrupted => exit */
				break;
			}
		}
	}

}
