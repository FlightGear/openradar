package de.knewcleus.openradar.gui.status.radio;
/**
 * A bean keeping the radio frequency information.
 * 
 * @author Wolfram Wagner
 */
public class RadioFrequency {

    private String code = null;
    private volatile String frequency = null;
    
    public RadioFrequency(String code, String frequency) {
        this.code=code;
        this.frequency=frequency;
    }
    
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public synchronized String getFrequency() {
        return frequency;
    }
    public synchronized void setFrequency(String frequency) {
        this.frequency = frequency;
    }

}
