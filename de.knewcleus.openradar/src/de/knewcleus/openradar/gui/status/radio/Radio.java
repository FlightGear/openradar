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
    private volatile RadioFrequency frequency = null;
    private volatile boolean pttActive = false;
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

}
