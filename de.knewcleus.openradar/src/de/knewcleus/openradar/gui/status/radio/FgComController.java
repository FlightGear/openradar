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

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.gui.setup.AirportData.FgComMode;

/**
 * This class controls the FgCom instances running separately.
 * 
 * This is what needs to be sent to FgCom to control it: orig:
 * COM1_FRQ=120.500,COM1_SRV
 * =1,COM2_FRQ=118.300,COM2_SRV=1,NAV1_FRQ=115.800,NAV1_SRV
 * =1,NAV2_FRQ=116.800,NAV2_SRV
 * =1,PTT=0,TRANSPONDER=0,IAS=09.8,GS=00.0,LON=-122.357193
 * ,LAT=37.613548,ALT=00004
 * ,HEAD=269.9,CALLSIGN=D-W794,MODEL=Aircraft/c172p/Models/c172p.xml new
 * COM1_FRQ
 * =122.75,COM1_SRV=1,COM2_FRQ=118.300,COM2_SRV=0,NAV1_FRQ=115.800,NAV1_SRV
 * =0,NAV2_FRQ
 * =116.800,NAV2_SRV=0,PTT=0,TRANSPONDER=0,IAS=00.0,GS=00.0,LON=-122.222050
 * ,LAT=37.719414,ALT=00000,HEAD=0.0,CALLSIGN=KOAK
 * UNICOM,MODEL=Model/atc/atc.xml
 * 
 * @author Wolfram Wagner
 * 
 */
public class FgComController implements Runnable, IRadioBackend {

    private Thread thread = new Thread(this, "OpenRadar - FGComController");
    private GuiMasterController master = null;
    private volatile double lon;
    private volatile double lat;
    private volatile String model = "Aircraft/ATC/Moddels/atc.xml";
    private Map<String, Process> fgComProcesses = Collections.synchronizedMap(new TreeMap<String, Process>());
    private List<LogWriterThread> logWriters = Collections.synchronizedList(new ArrayList<LogWriterThread>());
    
    private final Map<String, Radio> radios = Collections.synchronizedMap(new TreeMap<String, Radio>());

    private DatagramSocket datagramSocket;

    private volatile boolean isRunning = true;
    private int sleeptime = 500;

    private final static Logger log = LogManager.getLogger(FgComController.class);
    
    public FgComController() {
    }

    public FgComController(GuiMasterController master, String aircraftModel, double lon, double lat) {
        this.master = master;
        this.model = aircraftModel;
        this.lon = lon;
        this.lat = lat;

        thread.setDaemon(true);

        try {
            this.datagramSocket = new DatagramSocket();
            this.datagramSocket.setSoTimeout(500);
        } catch (SocketException e) {
            log.error("Error while configuring fgcom socket!",e);
        }

        Thread closeChildThread = new Thread("OpenRadar - Shutdown Hook") {
            public void run() {
                endFgComProcesses();
                isRunning = false;
            }
        };

        Runtime.getRuntime().addShutdownHook(closeChildThread);
    }

    private void endFgComProcesses() {

        for(LogWriterThread lw : logWriters) {
            lw.stop(); // marks them to exit
        }
        logWriters.clear();
        
        for (Process p : fgComProcesses.values()) {
            p.destroy();
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                // I don't care
            }
        }

        if(fgComProcesses.size()>0) {
            // only if we have started FGCOM
            if (System.getProperty("os.name").startsWith("Windows")) {
                try {
                    Runtime.getRuntime().exec("taskkill /F /IM fgcom.exe");
                } catch (IOException e) {}
                try {
                    Runtime.getRuntime().exec("taskkill /F /IM FGComGui.exe");
                } catch (IOException e) {}
            }
            if (System.getProperty("os.name").startsWith("Linux")) {
                try {
                    Runtime.getRuntime().exec("killall fgcom.exe");
                } catch (IOException e) {}
                try {
                    Runtime.getRuntime().exec("killall FGComGui.exe");
                } catch (IOException e) {}
                try {
                    Runtime.getRuntime().exec("killall fgcom");
                } catch (IOException e) {}
                try {
                    Runtime.getRuntime().exec("killall fgcomgui");
                } catch (IOException e) {}
            }
        }
        fgComProcesses.clear();
        
    }   
    
    public synchronized void start() {
        thread.start();
    }

    @Override
    public void run() {
        while (isRunning) {
            synchronized (this) {
                for (Radio r : radios.values()) {
                    if (r.getCallSign() != null) {
                        sendSettings(r);
                    }
                }
            }
            try {
                Thread.sleep(sleeptime);
            } catch (InterruptedException e) {
            }
        }
    }

    private void sendSettings(Radio r) {
        if(master.getCurrentATCCallSign()!=null && !master.getCurrentATCCallSign().isEmpty()) { // after initialization
            try {
                String message = composeMessage(r);
                // System.out.println("Sending fgcom message: "+message);
                byte[] msgBytes = message.getBytes(Charset.forName("ISO-8859-1"));
                DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length);
                packet.setSocketAddress(new InetSocketAddress(r.getFgComHost(), r.getFgComPort()));
                datagramSocket.send(packet);
            } catch (IOException e) {
                log.error("Error while tuning FGCOM!",e);
            }
        }
    }

    private String composeMessage(Radio r) {
        // COM1_FRQ=120.500,COM1_SRV=1,COM2_FRQ=118.300,COM2_SRV=1,NAV1_FRQ=115.800,NAV1_SRV=1,NAV2_FRQ=116.800,NAV2_SRV=1,PTT=0,TRANSPONDER=0,IAS=09.8,GS=00.0,LON=-122.357193,LAT=37.613548,ALT=00004,HEAD=269.9,CALLSIGN=D-W794,MODEL=Aircraft/c172p/Models/c172p.xml
        StringBuilder sb = new StringBuilder();
        sb.append("COM1_FRQ=");
        sb.append(r.getFrequency());
        sb.append("0,COM1_SRV=1,COM2_FRQ=118.300,COM2_SRV=0,NAV1_FRQ=115.800,NAV1_SRV=0,NAV2_FRQ=116.800,NAV2_SRV=0");
        sb.append(",PTT=");
        sb.append(r.isPttActive() ? "1" : "0");
        sb.append(",TRANSPONDER=0,IAS=00.0,GS=00.0");
        sb.append(",LON=");
        sb.append(String.format("%.6f", this.lon).replaceAll(",", "."));
        sb.append(",LAT=");
        sb.append(String.format("%.6f", this.lat).replaceAll(",", "."));
        sb.append(",ALT=00000,HEAD=0.0");
        sb.append(",CALLSIGN=");
        sb.append(master.getCurrentATCCallSign());
        sb.append(",MODEL=");
        sb.append(this.model);

        // System.out.println(sb.toString());
        return sb.toString();
    }

    public synchronized void stop() {
        isRunning = false;
        thread.interrupt();
    }

    public void setLocation(float lon, float lat) {
        this.lon = lon;
        this.lat = lat;
    }

    public synchronized void addRadio(String pathToFgComExec, String fgComExec, String key, String fgComServer, String fgComHost, int localFgComPort,
                                      String callSign, RadioFrequency frequency) {
        Radio r = new Radio(key, fgComHost, localFgComPort, callSign, frequency);
        initFgCom(r, pathToFgComExec, fgComExec, fgComServer, localFgComPort);
        radios.put(key, r);
    }

    public synchronized void restartFgCom() {
        endFgComProcesses();
        master.getLogWindow().removeLogs();
        
        AirportData data = master.getAirportData();
        for(Radio r : radios.values()) {
            initFgCom(r, data.getFgComPath(), data.getFgComExec(), data.getFgComServer(), r.getFgComPort());
        }
    }
    
    public Radio getRadio(String key) {
        return radios.get(key);
    }

    public synchronized int getRadioCount() {
        return radios.size();
    }

    public synchronized void tuneRadio(String key, String callSign, RadioFrequency frequency) {
        Radio r = radios.get(key);
        r.tuneTo(callSign, frequency);

    }

    @Override
    public synchronized void setPttActive(String radioKey, boolean active) {
        Radio r = radios.get(radioKey);
        r.setPttActive(active);
        if (r.getCallSign() != null)
            sendSettings(r);
    }

    public synchronized boolean isPttActive(String radioKey) {
        Radio r = radios.get(radioKey);
        return r.isPttActive();
    }

    /**
     * Start FgCom in background
     */
    public void initFgCom(Radio r, String pathToFgComExec, String fgComExec, String fgComServer, int localPort) {

        if (master.getAirportData().getFgComMode()==FgComMode.External)
            // do not start it internally
            return;

        if (pathToFgComExec.isEmpty()) {
            pathToFgComExec = ".";
        }

        // START local fgcom instances

        File dir = new File(pathToFgComExec);
        if (!dir.exists())
            throw new IllegalArgumentException("FGCom path seems to be incorrect!");

        List<String> command = new ArrayList<String>();
        if (System.getProperty("os.name").startsWith("Windows")) {
            command.add("cmd");
            command.add("/c");
            StringBuilder arguments = new StringBuilder(pathToFgComExec + File.separator + fgComExec);
            if (!fgComServer.isEmpty()) {
                arguments.append(" --port=" + localPort);
                arguments.append(" --voipserver=" + fgComServer);
                arguments.append(" --callsign=" + master.getCurrentATCCallSign());
            }
            command.add(arguments.toString());
        }
        if (System.getProperty("os.name").startsWith("Linux")) {
            command.add("/bin/bash");
            command.add("-c");
            StringBuilder arguments = new StringBuilder(pathToFgComExec + File.separator + fgComExec);
            if (!fgComServer.isEmpty()) {
                arguments.append(" --port=" + localPort);
                arguments.append(" --voipserver=" + fgComServer);
                arguments.append(" --callsign=" + master.getCurrentATCCallSign());
            }
            command.add(arguments.toString());
        }
        if (System.getProperty("os.name").startsWith("Mac")) {
            // ???
        }

        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(dir);
            builder.command(command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            fgComProcesses.put(r.getKey(), process);

            logWriters.add(new LogWriterThread(master.getLogWindow(), r, process));

        } catch (IOException e) {
            log.error("Error while starting fgcom!",e);
        }
    }
}
