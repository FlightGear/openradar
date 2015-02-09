package de.knewcleus.openradar.fgfscontroller;

public class CameraPreset {

    private final String name;
    /** the latitude of the viewpoint of the camera view */
    private double lat;
    /** the longitude of the viewpoint of the camera view */
    private double lon;
    /** the the altitude of the viewpoint of the camera view */
    private double alt;
    /** the heading of the camera view */
    private double viewHeading;
    /** the pitch of the camera view */
    private double viewPitch;
    /** the zoom (field of view in degrees) of the camera view */
    private double viewZoom;
 
    public CameraPreset(String name) {
        this.name=name;
    }

    public synchronized double getLat() {
        return lat;
    }

    public synchronized void setLat(double lat) {
        this.lat = lat;
    }

    public synchronized double getLon() {
        return lon;
    }

    public synchronized void setLon(double lon) {
        this.lon = lon;
    }

    public synchronized double getAlt() {
        return alt;
    }

    public synchronized void setAlt(double alt) {
        this.alt = alt;
    }

    public synchronized double getViewHeading() {
        return viewHeading;
    }

    public synchronized void setViewHeading(double viewHeading) {
        this.viewHeading = viewHeading;
    }

    public synchronized double getViewPitch() {
        return viewPitch;
    }

    public synchronized void setViewPitch(double viewPitch) {
        this.viewPitch = viewPitch;
    }

    public synchronized double getViewZoom() {
        return viewZoom;
    }

    public synchronized void setViewZoom(double viewZoom) {
        this.viewZoom = viewZoom;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setData(double lon, double lat, double alt, double heading, double pitch, double fov) {
        this.lon = lon;
        this.lat = lat;
        this.alt = alt;
        this.viewHeading = heading;
        this.viewPitch = pitch;
        this.viewZoom = fov;
        
    }
}
