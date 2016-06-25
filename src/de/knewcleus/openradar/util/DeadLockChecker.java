/**
 * Copyright (C) 2016 Wolfram Wagner
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
package de.knewcleus.openradar.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import org.apache.log4j.Logger;

/**
 * This class checks periodically for dead locks and saves them into the log file for analysis
 *
 * @author Wolfram Wagner
 */
public class DeadLockChecker implements Runnable{

	private static Logger log = Logger.getLogger(DeadLockChecker.class);

	public void start() {
		new Thread(this,"OpenRadar - Deadlock Checker").start();
	}

	@Override
	public void run() {
		boolean active = true;
	    ThreadMXBean tmx = ManagementFactory.getThreadMXBean();

		while(active) {
//			log.info("Checking for deadlocks");
		    long[] ids = tmx.findDeadlockedThreads();
	        if (ids == null ) {
	        	// no deadlocks
//	        	log.info("NO deadlocks found");
	        	try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {}
	        } else {
	        	// deadlocks found
	        	ThreadInfo[] infos = tmx.getThreadInfo(ids,true,true);
	            log.fatal("DEADLOCK FOUND!");
	            for (ThreadInfo info : infos)
	            {
	                log.fatal(info.toString());
	            }
	            active=false;
	        }
	    }
	}
}
