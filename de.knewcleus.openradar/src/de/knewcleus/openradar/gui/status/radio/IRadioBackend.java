package de.knewcleus.openradar.gui.status.radio;

public interface IRadioBackend {

    public int getRadioCount();
    /**
     * Attempts to tune radio #index to the given frequency.
     * 
     * @param frequency
     * @return true if tuning was successful
     */
    public void tuneRadio(String radioKey, String callSign, RadioFrequency frequency);

    public void setPttActive(String radioKey, boolean active);
}
