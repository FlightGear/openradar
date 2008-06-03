package de.knewcleus.openradar.ui.rpvd;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class RadarPlanViewSettings {
	protected final PropertyChangeSupport propertyChangeSupport=new PropertyChangeSupport(this);
	
	public final static String RANGE_PROPERTY="range";
	public final static String IS_SHOWING_SECTOR_PROPERTY="showingSector";
	public final static String IS_SHOWING_WAYPOINTS_PROPERTY="showingWaypoints";
	public final static String IS_SHOWING_AIRWAYS_PROPERTY="showingAirways";
	public final static String IS_SHOWING_MILITARY_PROPERTY="showingMilitary";
	public final static String IS_SHOWING_COASTLINE_PROPERTY="showingCoastline";
	public final static String IS_SHOWING_RINGS_PROPERTY="showingRings";
	public final static String IS_SHOWING_SCALELINE_PROPERTY="showingScaleline";
	public final static String SPEED_VECTOR_MINUTES_PROPERTY="speedVectorMinutes";
	public final static String TRACK_HISTORY_LENGTH_PROPERTY="trackHistoryLength";
	public final static String IS_AUTOMATIC_LABELING_ENABLED_PROPERTY="automaticLabelingEnabled";
	public final static String STANDARD_LABEL_POSITION_PROPERTY="standardLabelPosition";
	public final static String STANDARD_LABEL_DISTANCE_PROPERTY="standardLabelDistance";
	
	protected int range=10;
	protected boolean showingSector=true;
	protected boolean showingWaypoints=true;
	protected boolean showingAirways=true;
	protected boolean showingMilitary=true;
	protected boolean showingCoastline=true;
	protected boolean showingRings=false;
	protected boolean showingScaleLine=false;
	protected int speedVectorMinutes=1;
	protected int trackHistoryLength=3;
	protected boolean automaticLabelingEnabled=true;
	protected StandardLabelPosition standardLabelPosition=StandardLabelPosition.TOPRIGHT;
	protected StandardLabelDistance standardLabelDistance=StandardLabelDistance.MEDIUM;

	public int getRange() {
		return range;
	}

	public boolean isShowingSector() {
		return showingSector;
	}

	public boolean isShowingWaypoints() {
		return showingWaypoints;
	}

	public boolean isShowingAirways() {
		return showingAirways;
	}

	public boolean isShowingMilitary() {
		return showingMilitary;
	}

	public boolean isShowingCoastline() {
		return showingCoastline;
	}

	public boolean isShowingRings() {
		return showingRings;
	}

	public boolean isShowingScaleLine() {
		return showingScaleLine;
	}

	public int getSpeedVectorMinutes() {
		return speedVectorMinutes;
	}

	public int getTrackHistoryLength() {
		return trackHistoryLength;
	}
	
	public boolean isAutomaticLabelingEnabled() {
		return automaticLabelingEnabled;
	}
	
	public StandardLabelPosition getStandardLabelPosition() {
		return standardLabelPosition;
	}
	
	public StandardLabelDistance getStandardLabelDistance() {
		return standardLabelDistance;
	}

	public void setRange(int newValue) {
		int oldValue=this.range;
		this.range = newValue;
		propertyChangeSupport.firePropertyChange(RANGE_PROPERTY, oldValue, newValue);
	}

	public void setShowingSector(boolean newValue) {
		boolean oldValue=this.showingSector;
		this.showingSector = newValue;
		propertyChangeSupport.firePropertyChange(IS_SHOWING_SECTOR_PROPERTY, oldValue, newValue);
	}

	public void setShowingWaypoints(boolean showingWaypoints) {
		boolean oldValue=this.showingWaypoints;
		this.showingWaypoints = showingWaypoints;
		propertyChangeSupport.firePropertyChange(IS_SHOWING_WAYPOINTS_PROPERTY, oldValue, showingWaypoints);
	}

	public void setShowingAirways(boolean showingAirways) {
		boolean oldValue=this.showingAirways;
		this.showingAirways = showingAirways;
		propertyChangeSupport.firePropertyChange(IS_SHOWING_AIRWAYS_PROPERTY, oldValue, showingAirways);
	}

	public void setShowingMilitary(boolean showingMilitary) {
		boolean oldValue=this.showingMilitary;
		this.showingMilitary = showingMilitary;
		propertyChangeSupport.firePropertyChange(IS_SHOWING_MILITARY_PROPERTY, oldValue, showingMilitary);
	}

	public void setShowingCoastline(boolean showingCoastline) {
		boolean oldValue=this.showingCoastline;
		this.showingCoastline = showingCoastline;
		propertyChangeSupport.firePropertyChange(IS_SHOWING_COASTLINE_PROPERTY, oldValue, showingCoastline);
	}

	public void setShowingRings(boolean showingRings) {
		boolean oldValue=this.showingRings;
		this.showingRings = showingRings;
		propertyChangeSupport.firePropertyChange(IS_SHOWING_RINGS_PROPERTY, oldValue, showingRings);
	}

	public void setShowingScaleLine(boolean showingScaleLine) {
		boolean oldValue=this.showingScaleLine;
		this.showingScaleLine = showingScaleLine;
		propertyChangeSupport.firePropertyChange(IS_SHOWING_SCALELINE_PROPERTY, oldValue, showingScaleLine);
	}

	public void setSpeedVectorMinutes(int speedVectorMinutes) {
		int oldValue=this.speedVectorMinutes;
		this.speedVectorMinutes = speedVectorMinutes;
		propertyChangeSupport.firePropertyChange(SPEED_VECTOR_MINUTES_PROPERTY, oldValue, speedVectorMinutes);
	}

	public void setTrackHistoryLength(int trackHistoryLength) {
		int oldValue=this.trackHistoryLength;
		this.trackHistoryLength = trackHistoryLength;
		propertyChangeSupport.firePropertyChange(TRACK_HISTORY_LENGTH_PROPERTY, oldValue, trackHistoryLength);
	}
	
	public void setAutomaticLabelingEnabled(boolean automaticLabelsEnabled) {
		boolean oldValue=this.automaticLabelingEnabled;
		this.automaticLabelingEnabled = automaticLabelsEnabled;
		propertyChangeSupport.firePropertyChange(IS_AUTOMATIC_LABELING_ENABLED_PROPERTY, oldValue, automaticLabelsEnabled);
	}
	
	public void setStandardLabelPosition(StandardLabelPosition standardLabelPosition) {
		StandardLabelPosition oldValue=this.standardLabelPosition;
		this.standardLabelPosition = standardLabelPosition;
		propertyChangeSupport.firePropertyChange(STANDARD_LABEL_POSITION_PROPERTY, oldValue, standardLabelPosition);
	}
	
	public void setStandardLabelDistance(StandardLabelDistance standardLabelDistance) {
		StandardLabelDistance oldValue=this.standardLabelDistance;
		this.standardLabelDistance = standardLabelDistance;
		propertyChangeSupport.firePropertyChange(STANDARD_LABEL_DISTANCE_PROPERTY, oldValue, standardLabelDistance);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public PropertyChangeListener[] getPropertyChangeListeners() {
		return propertyChangeSupport.getPropertyChangeListeners();
	}

	public PropertyChangeListener[] getPropertyChangeListeners(
			String propertyName) {
		return propertyChangeSupport.getPropertyChangeListeners(propertyName);
	}

	public boolean hasListeners(String propertyName) {
		return propertyChangeSupport.hasListeners(propertyName);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}
}
