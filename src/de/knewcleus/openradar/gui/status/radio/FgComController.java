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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
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
    private volatile double alt;
    private Map<String, Process> fgComProcesses = Collections.synchronizedMap(new TreeMap<String, Process>());
    private List<LogWriterThread> logWriters = Collections.synchronizedList(new ArrayList<LogWriterThread>());
    
    private final Map<String, Radio> radios = Collections.synchronizedMap(new TreeMap<String, Radio>());

    private DatagramSocket datagramSocket;

    private volatile boolean isRunning = true;
    private int sleeptime = 500;

    private final static Logger log = LogManager.getLogger(FgComController.class);
    
    public FgComController() {
    }

    public FgComController(GuiMasterController master, String aircraftModel, double lon, double lat, double alt) {
        this.master = master;
        this.lon = lon;
        this.lat = lat;
        this.alt = alt;

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

        // the code above stops direct child processes started by OR
        // if the direct child process starts a script that starts fgcom, fgcom is still running.
        // here we try to stop it.
        if(fgComProcesses.size()>0) {
            // only if we have started FGCOM
            if (System.getProperty("os.name").startsWith("Windows")) {
                try {
                    Runtime.getRuntime().exec("taskkill /F /IM fgcom.exe");
                } catch (IOException e) {}
                try {
                    Runtime.getRuntime().exec("taskkill /F /IM FGComGui.exe");
                } catch (IOException e) {}
            } else if (System.getProperty("os.name").startsWith("Linux")) {
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
        sb.append("PTT=");
        sb.append(r.isPttActive() ? "1" : "0");
        sb.append(String.format(",LAT=%.6f",lat));
        sb.append(String.format(",LON=%.6f",lon)); //.replaceAll(",", ".")
        sb.append(String.format(",ALT=%1.0f",alt));
        sb.append(",COM1_FRQ=");
        sb.append(r.getFrequency());
        sb.append(",COM2_FRQ=118.300");
        sb.append(String.format(",OUTPUT_VOL=%.1f",r.getVolumeF()));//.replaceAll(",", ".")
        sb.append(",SILENCE_THD=-60.0");
        sb.append(",CALLSIGN=");
        sb.append(master.getCurrentATCCallSign());

        //System.out.println(sb.toString());
        return sb.toString();
    }

    public synchronized void stop() {
        isRunning = false;
        thread.interrupt();
    }

    public void setLocation(float lon, float lat, float alt) {
        this.lon = lon;
        this.lat = lat;
        this.alt = alt;
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

        if (master.getAirportData().getFgComMode()==FgComMode.External) {
            // do not start it internally
            return;
        }
        
        String os = System.getProperty("os.name");
        String arch = System.getProperty("os.arch");
        if (master.getAirportData().getFgComMode()==FgComMode.Auto) {
            // AUTO mode
            
            // set path and exec
            pathToFgComExec = System.getProperty("user.dir")+File.separator+"fgcom";
            if(os.toLowerCase().contains("win")) {
                if(arch.toLowerCase().contains("32")) {
                    fgComExec = "fgcom_win32.exe";
                } else if(arch.toLowerCase().contains("64")) {
                    fgComExec = "fgcom_win64.exe";
                }
            } else if(os.toLowerCase().contains("lin")) {
                if(arch.toLowerCase().contains("32")) {
                    fgComExec = "fgcom_lin32";
                } else if(arch.toLowerCase().contains("64")) {
                    fgComExec = "fgcom_lin64";
                }
            } else if(os.toLowerCase().contains("mac")) {
                fgComExec = "fgcom_mac";
            }

        } else {
            
            // INTERNAL MODE
            
            if (pathToFgComExec.isEmpty()) {
                pathToFgComExec = ".";
            }
        }
        File dir = new File(pathToFgComExec);
        if (!dir.exists()) {
            throw new IllegalArgumentException("FGCom path seems to be incorrect!");
        }
        
        // determine pathes

//        String posPath = getFgComPositionsPath(master.getAirportData(), pathToFgComExec);
//        String specialsPath = getFgComSpecialsPath(master.getAirportData(), pathToFgComExec);
        
        // build the command
        
        List<String> command = new ArrayList<String>();
        if (System.getProperty("os.name").startsWith("Windows")) {
            command.add("cmd");
            command.add("/c");
        } else if (System.getProperty("os.name").startsWith("Linux")) {
            command.add("/bin/bash");
            command.add("-c");
        } else if (System.getProperty("os.name").startsWith("Mac")) {
            command.add("/bin/bash");
            command.add("-c");
        }
        // add arguments
        StringBuilder arguments = new StringBuilder("\"");
        arguments.append(pathToFgComExec).append(File.separator).append(fgComExec).append("\"");
        if (!fgComServer.isEmpty()) {
            arguments.append(" --port=" + localPort);
            arguments.append(" --voipserver=" + fgComServer);
            arguments.append(" --callsign=" + master.getCurrentATCCallSign());
//            arguments.append(" --positions=" + posPath);
//            arguments.append(" --special=" + specialsPath);
        }
        command.add(arguments.toString());

        // START local fgcom instance

        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(dir);
            builder.command(command);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            fgComProcesses.put(r.getKey(), process);

            logWriters.add(new LogWriterThread(master.getLogWindow(), r, process, dir.getAbsolutePath(), buildString(command)));

        } catch (IOException e) {
            log.error("Error while starting fgcom!",e);
        }
    }

    private String buildString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for(String s : list) {
            if(sb.length()>0) {
                sb.append(" ");
            }
            sb.append(s);
        }
        return sb.toString();
    }
    
    public static String getFgComPositionsPath(AirportData data, String pathToFgComExec) {
        String os = System.getProperty("os.name");
        String fgcomBasePath = getFgComBasePath(pathToFgComExec);
        
        String posPath;
        if (data.getFgComMode()==FgComMode.Auto) {
            
            // auto mode
            
            posPath = fgcomBasePath+"/res/positions.txt";
            
        } else {
            
            // internal mode
            
            if(os.toLowerCase().contains("mac")) {
                posPath = fgcomBasePath+"/Resources/positions.txt";
            } else {
                // lin / win
                posPath = fgcomBasePath+"/share/flightgear/positions.txt";
            }
        }        
        return posPath;
    }

    public static String getFgComSpecialsPath(AirportData data, String pathToFgComExec) {
        String os = System.getProperty("os.name");
        String fgcomBasePath = getFgComBasePath(pathToFgComExec);
        
        String specialsPath;
        if (data.getFgComMode()==FgComMode.Auto) {
            
            // auto mode
            
            specialsPath= fgcomBasePath+ "/res/special_frequencies.txt";
            
        } else {
            
            // internal mode
            
            if(os.toLowerCase().contains("mac")) {
                specialsPath= fgcomBasePath+ "/Resources/special_frequencies.txt";
            } else {
                // lin / win
                specialsPath= fgcomBasePath+ "/share/flightgear/special_frequencies.txt";
            }
        }        
        return specialsPath;
    }

    private static String getFgComBasePath(String pathToFgComExec) {
        String path = pathToFgComExec.trim();
        if(path.contains(File.separator)) {
            File parentDir = pathToFgComExec.isEmpty() ? new File(System.getProperty("user.dir")) : new File(pathToFgComExec).getParentFile();
            path = parentDir!=null ? parentDir.getAbsolutePath() : "";
        }
        return path;
    }
}
