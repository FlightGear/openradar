package de.knewcleus.openradar.view;

public interface IRadarViewChangeListener {
    public enum Change {ZOOM,CENTER}
    
    public void radarViewChanged(ViewerAdapter v, Change c);
}
