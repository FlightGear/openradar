/**
 * Copyright (C) 2020 Benedikt Hallinger
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
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.gui.setup.AirportData.FgComMode;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class controls the FgCom-mumble plugin.
 * 
 * Message format is described in detail at https://github.com/hbeni/fgcom-mumble/blob/master/client/plugin.spec.md
 * 
 * @author Benedikt Hallinger
 * 
 */
public class FgComMumbleController extends FgComController implements Runnable, IRadioBackend  {

    protected final static Logger log = LogManager.getLogger(FgComMumbleController.class);
    
    private boolean comZeroDetected = false;
    
    public FgComMumbleController() {
    }

    public FgComMumbleController(GuiMasterController master, String aircraftModel, double lon, double lat, double alt) {
        super(master, aircraftModel, lon, lat, alt);
    }


    @Override
    public void run() {
        while (isRunning) {
            synchronized (this) {
                sendSettings();
            }
            try {
                Thread.sleep(sleeptime);
            } catch (InterruptedException e) {
            }
        }
    }
    
    @Override
    protected synchronized void sendSettings(Radio r) {
        this.sendSettings();
    }

    /*
    * Compose and send FGCom-mumble protocol messages
    */
    protected synchronized void sendSettings() {
        if (master.getCurrentATCCallSign()!=null && !master.getCurrentATCCallSign().isEmpty()) { // after initialization
  
            String message = "";
            
            message += "CALLSIGN=";
            message += master.getCurrentATCCallSign();
            message += String.format(",LAT=%.6f",lat);
            message += String.format(",LON=%.6f",lon); //.replaceAll(",", ".")
            message += String.format(",ALT=%1.0f",alt);
            
            // Compose individual radio state
            for (Radio r : radios.values()) {
                if (r.getCallSign() != null && r.getFgComPort() > 0) {
                   message += composeMessage(r);
                }
            }
            
            // Terminate the message
            message += System.lineSeparator();
            
            // Finally try to send
            try {
                // System.out.println("Sending fgcom message: "+message);
                byte[] msgBytes = message.getBytes(Charset.forName("ISO-8859-1"));
                DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length);

                // send all info to all configured radio ports, but only once
                Vector<Integer> seenPorts = new Vector();
                for (Radio r : radios.values()) {
                    int port = r.getFgComPort();

                    if (!seenPorts.contains(port)) {
                        packet.setSocketAddress(new InetSocketAddress(r.getFgComHost(), port));
                        datagramSocket.send(packet);
                        seenPorts.add(port);
                        log.debug("UDP-MSG sent: "+message);
                    }
                }
            } catch (IOException e) {
                log.error("Error while tuning FGCOM!",e);
            }
        }
    }

    /*
    * Compose UDP message string for the given radio
    */
    protected String composeMessage(Radio r) {
        StringBuilder sb = new StringBuilder();
        Touple<String, Integer> rName = this.splitComName(r.getKey());
        if (rName.left != null && rName.right != null) {
            String  rId = rName.left;
            Integer rNr = rName.right;
            if (rNr == 0 ) comZeroDetected = true;  // COM0 detected
            if (comZeroDetected) rNr++;  // If COM0 was detected: add 1, because COMs start with COM1!
            String rKey = rId + rNr.toString();
        
            sb.append(","+rKey+"_PTT=");
            sb.append(r.isPttActive() ? "1" : "0");
            sb.append(","+rKey+"_FRQ=");
            sb.append(r.getFrequency());
            sb.append(","+rKey+"_VOL=");
            sb.append(String.format("%.1f",r.getVolumeF()));//.replaceAll(",", ".")

            //System.out.println(sb.toString());
            return sb.toString();
        } else {
            throw new InvalidParameterException("Could not parse Touple from com="+r.getKey());
        }
    }

    /*
    * Split COMn name into Radio type and Number
    */
    protected Touple<String, Integer> splitComName(String com) {
        Pattern p = Pattern.compile("(\\w+)(\\d+)");
        Matcher m = p.matcher(com);
        if (m.matches()) {
            Touple<String, Integer> t = new Touple<>(m.group(1), Integer.valueOf(m.group(2)));
            return t;
        } else {
            throw new InvalidParameterException("Radio key '"+com+"' not in valid syntax COMn!");
        }
        
    }

    @Override
    public synchronized void addRadio(String pathToFgComExec, String fgComExec, String key, String fgComServer, String fgComHost, int localFgComPort,
                                      String callSign, RadioFrequency frequency) {
        String key_derived = "COM"+radios.size();
        Radio r = new Radio(key_derived, fgComHost, localFgComPort, callSign, frequency);
        //initFgCom(r, pathToFgComExec, fgComExec, fgComServer, localFgComPort);
        radios.put(key_derived, r);
    }
    
    
    /*
    * Simple Touple class
    */
    protected class Touple<Ta,Tb> {
        Ta left;
        Tb right;
        Touple(Ta a, Tb b) {
            left  = a;
            right = b;
        }
    }
}
