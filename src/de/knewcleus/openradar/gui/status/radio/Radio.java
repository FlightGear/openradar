/**
 * Copyright (C) 2012 Wolfram Wagner
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

/**
 * A bean keeping the radia data
 *
 * @author Wolfram Wagner
 *
 */
public class Radio {
    private String key = null;
    private String fgComHost = null;
    private int fgComPort = 0;
    private volatile String callSign;
    private String restoredFrequency = null;
    private volatile RadioFrequency frequency = null;
    private volatile boolean pttActive = false;
    private volatile int volume = 9;
    private volatile boolean connectedToServer = true;

    public Radio(String key, String fgComHost, int fgComPort) {
        this.key = key;
        this.fgComHost = fgComHost;
        this.fgComPort = fgComPort;
    }

    public Radio(String key, String fgComHost, int fgComPort, String callSign, RadioFrequency frequency) {
        this.key = key;
        this.fgComHost = fgComHost;
        this.fgComPort = fgComPort;
        this.callSign = callSign;
        this.frequency = frequency;
    }

    public String getKey() {
        return key;
    }

    public String getFgComHost() {
        return fgComHost;
    }

    public int getFgComPort() {
        return fgComPort;
    }

    public String getCallSign() {
        return callSign;
    }

    public String getFrequency() {
        return (frequency!=null) ? frequency.getFrequency() : null;
    }

    public synchronized void tuneTo(String callSign, RadioFrequency frequency) {
        this.callSign = callSign;
        this.frequency = frequency;
    }

    public synchronized boolean isPttActive() {
        return pttActive;
    }

    public synchronized void setPttActive(boolean pttActive) {
        this.pttActive = pttActive;
    }

    public synchronized boolean isConnectedToServer() {
        // System.out.println(this.key+" "+connectedToServer);
        return connectedToServer;
    }

    public synchronized void setConnectedToServer(boolean connectedToServer) {
        this.connectedToServer = connectedToServer;
    }

    public void setRestoredFrequency(String restoredFrequency) {
        this.restoredFrequency = restoredFrequency;
    }
    public String getRestoredFrequency() {
        return restoredFrequency;
    }
    public synchronized int getVolume() {
        return volume;
    }
    public synchronized double getVolumeF() {
        return ((double)volume+1)/10.0;
    }
    public synchronized void increaseVolume() {
        if(volume<9) {
            volume++;
        }
    }
    public synchronized void decreaseVolume() {
        if(volume>0) {
            volume--;
        }
    }
}
