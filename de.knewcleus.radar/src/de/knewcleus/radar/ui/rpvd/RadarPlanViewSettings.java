package de.knewcleus.radar.ui.rpvd;

import java.awt.Font;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.ICoordinateTransformation;

public class RadarPlanViewSettings {
	protected double range=15.0f*(float)Units.NM;
	protected Font font=new Font(Font.SANS_SERIF,Font.PLAIN,12);
	protected boolean showingSector=true;
	protected boolean showingWaypoints=true;
	protected boolean showingAirways=true;
	protected boolean showingMilitary=true;
	protected boolean showingCoastline=true;
	protected boolean showingRings=false;
	protected boolean showingScaleLine=false;
	protected ICoordinateTransformation mapTransformation;
	
	public double getRange() {
		return range;
	}
	
	public void setRange(double range) {
		this.range = range;
	}
	
	public Font getFont() {
		return font;
	}
	
	public void setFont(Font font) {
		this.font = font;
	}
	
	public boolean isShowingSector() {
		return showingSector;
	}

	public void setShowingSector(boolean showingSector) {
		this.showingSector = showingSector;
	}

	public boolean isShowingWaypoints() {
		return showingWaypoints;
	}

	public void setShowingWaypoints(boolean showingWaypoints) {
		this.showingWaypoints = showingWaypoints;
	}

	public boolean isShowingAirways() {
		return showingAirways;
	}

	public void setShowingAirways(boolean showingAirways) {
		this.showingAirways = showingAirways;
	}

	public boolean isShowingMilitary() {
		return showingMilitary;
	}

	public void setShowingMilitary(boolean showingMilitary) {
		this.showingMilitary = showingMilitary;
	}

	public boolean isShowingCoastline() {
		return showingCoastline;
	}

	public void setShowingCoastline(boolean showingCoastline) {
		this.showingCoastline = showingCoastline;
	}

	public boolean isShowingRings() {
		return showingRings;
	}

	public void setShowingRings(boolean showingRings) {
		this.showingRings = showingRings;
	}

	public boolean isShowingScaleLine() {
		return showingScaleLine;
	}

	public void setShowingScaleLine(boolean showingScaleLine) {
		this.showingScaleLine = showingScaleLine;
	}

	public ICoordinateTransformation getMapTransformation() {
		return mapTransformation;
	}
	
	public void setMapTransformation(ICoordinateTransformation mapTransformation) {
		this.mapTransformation = mapTransformation;
	}
}
