package de.knewcleus.fgfs.navdata.xplane;

public class RawFrequency {

    private String code;
    private String frequency;
    
    public RawFrequency(String code, String frequency) {
        this.code = code;
        this.frequency = frequency.substring(0, 3)+"."+frequency.substring(3);
    }

    public String getCode() {
        return code;
    }

    public String getFrequency() {
        return frequency;
    }
}
