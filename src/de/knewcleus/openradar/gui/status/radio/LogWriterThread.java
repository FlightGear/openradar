/**
 * Copyright (C) 2012,2013 Wolfram Wagner 
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
package de.knewcleus.openradar.gui.status.radio;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.knewcleus.openradar.gui.LogWindow;

public class LogWriterThread implements Runnable {

    private final Radio radio;
    private final LogWindow logWindow;
    private final Process process;
    private final String tabName;
    private final String dir;
    private final String command;
    
    private BufferedInputStream bis = null;
    private volatile boolean isRunning = true;
    private Thread thread;

    private final static Logger log = LogManager.getLogger(LogWriterThread.class);
    
    public LogWriterThread(LogWindow logWindow, Radio radio, Process process, String dir, String command) {
        this.logWindow = logWindow;
        this.process = process;
        this.radio = radio;
        this.tabName=radio.getKey();
        this.dir=dir;
        this.command=command;
        thread = new Thread(this, "OpenRadar - FGCom Log Writer");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        
        byte[] buffer = new byte[1024];
        bis = new BufferedInputStream(process.getInputStream());

        try {
            logWindow.addText(tabName,"FGCOM LOG \n"
                                     +"dir:\n"+dir+"\n"
                                     +"cmd:\n"+command+"\n\n");
            
            while(isRunning) {
                int len = isRunning ? bis.read(buffer) : 0;
                synchronized(this) {
                    if(len>0 && isRunning) {
                        String content = new String(buffer,0,len);
                        // System.out.println(content);
                        if(content.contains("rejected") || content.contains("Hanging up") || content.contains("timed out")) {
                            radio.setConnectedToServer(false);
                        } else if(content.contains("accepted")){
                            radio.setConnectedToServer(true);
                        }
                        logWindow.addText(tabName,content);
                    }
                }
                if(isRunning) {
                    try {
                        if(len>0) {
                            Thread.sleep(200);
                        } else {
                            Thread.sleep(1000);
                        }
                    } catch(InterruptedException e) {
                        // this is ok, I interupt it
                    }
                }
            }
        } catch (IOException e) {
            // stream closed
        } catch(ThreadDeath e) {
        } catch(Exception e) {
            log.error("Error in FGCOM logging!",e);
        } 
    }

    public synchronized void stop() {
        radio.setConnectedToServer(false);
        isRunning=false;
        thread.interrupt();
    }
}
