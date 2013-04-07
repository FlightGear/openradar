package de.knewcleus.openradar.gui.contacts;

public class FlightState {
    public final String atcMode;
    public final String state;
    public final String stateShort;
    public final String stateLong;

    public FlightState(String atcMode, String state, String stateShort, String stateLong) {
        this.atcMode = atcMode;
        this.state = state;
        this.stateShort = stateShort;
        this.stateLong = stateLong;
    }
}
