package de.knewcleus.radar.ui.rpvd.toolbox;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;

import de.knewcleus.radar.ui.RadarWorkstation;
import de.knewcleus.radar.ui.rpvd.RadarPlanViewDisplay;
import de.knewcleus.radar.ui.rpvd.RadarPlanViewSettings;

public class MapMenuFrame extends JInternalFrame implements PropertyChangeListener,ActionListener {
	private static final long serialVersionUID = 6741748501762906857L;
	
	protected final RadarToolbox radarToolbox;
	protected final RadarPlanViewSettings radarPlanViewSettings;
	
	protected final JCheckBox sectorCheckbox=new JCheckBox("Sector");
	protected final JCheckBox waypointsCheckbox=new JCheckBox("Waypoints");
	protected final JCheckBox airwaysCheckbox=new JCheckBox("Airways");
	protected final JCheckBox militaryCheckbox=new JCheckBox("Military");
	protected final JCheckBox coastlineCheckbox=new JCheckBox("Coastline");
	protected final JCheckBox ringsCheckbox=new JCheckBox("R-Rings");
	protected final JCheckBox scaleLineCheckbox=new JCheckBox("Scale Line");
	
	public MapMenuFrame(RadarToolbox radarToolbox) {
		super("MAP",false,true,false,false);
		this.radarToolbox=radarToolbox;
		
		RadarPlanViewDisplay radarPlanViewDisplay=radarToolbox.getRadarPlanViewDisplay();
		RadarWorkstation radarWorkstation=radarPlanViewDisplay.getWorkstation();
		radarPlanViewSettings=radarWorkstation.getRadarPlanViewSettings();
		radarPlanViewSettings.addPropertyChangeListener(this);
		
		setLayout(new GridLayout(7,1));
		
		sectorCheckbox.addActionListener(this);
		waypointsCheckbox.addActionListener(this);
		airwaysCheckbox.addActionListener(this);
		militaryCheckbox.addActionListener(this);
		coastlineCheckbox.addActionListener(this);
		ringsCheckbox.addActionListener(this);
		scaleLineCheckbox.addActionListener(this);
		
		add(sectorCheckbox);
		add(waypointsCheckbox);
		add(airwaysCheckbox);
		add(militaryCheckbox);
		add(coastlineCheckbox);
		add(ringsCheckbox);
		add(scaleLineCheckbox);
		
		sectorCheckbox.setSelected(radarPlanViewSettings.isShowingSector());
		waypointsCheckbox.setSelected(radarPlanViewSettings.isShowingWaypoints());
		airwaysCheckbox.setSelected(radarPlanViewSettings.isShowingAirways());
		militaryCheckbox.setSelected(radarPlanViewSettings.isShowingMilitary());
		coastlineCheckbox.setSelected(radarPlanViewSettings.isShowingCoastline());
		ringsCheckbox.setSelected(radarPlanViewSettings.isShowingRings());
		scaleLineCheckbox.setSelected(radarPlanViewSettings.isShowingScaleLine());
		
		pack();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==sectorCheckbox) {
			radarPlanViewSettings.setShowingSector(sectorCheckbox.isSelected());
		} else if (e.getSource()==waypointsCheckbox) {
			radarPlanViewSettings.setShowingWaypoints(waypointsCheckbox.isSelected());
		} else if (e.getSource()==airwaysCheckbox) {
			radarPlanViewSettings.setShowingAirways(airwaysCheckbox.isSelected());
		} else if (e.getSource()==militaryCheckbox) {
			radarPlanViewSettings.setShowingMilitary(militaryCheckbox.isSelected());
		} else if (e.getSource()==coastlineCheckbox) {
			radarPlanViewSettings.setShowingCoastline(coastlineCheckbox.isSelected());
		} else if (e.getSource()==ringsCheckbox) {
			radarPlanViewSettings.setShowingRings(ringsCheckbox.isSelected());
		} else if (e.getSource()==scaleLineCheckbox) {
			radarPlanViewSettings.setShowingScaleLine(scaleLineCheckbox.isSelected());
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName=evt.getPropertyName();
		if (propertyName.equals(RadarPlanViewSettings.IS_SHOWING_SECTOR_PROPERTY)) {
			sectorCheckbox.setSelected(radarPlanViewSettings.isShowingSector());
		} else if (propertyName.equals(RadarPlanViewSettings.IS_SHOWING_WAYPOINTS_PROPERTY)) {
			waypointsCheckbox.setSelected(radarPlanViewSettings.isShowingWaypoints());
		} else if (propertyName.equals(RadarPlanViewSettings.IS_SHOWING_AIRWAYS_PROPERTY)) {
			airwaysCheckbox.setSelected(radarPlanViewSettings.isShowingAirways());
		} else if (propertyName.equals(RadarPlanViewSettings.IS_SHOWING_MILITARY_PROPERTY)) {
			militaryCheckbox.setSelected(radarPlanViewSettings.isShowingMilitary());
		} else if (propertyName.equals(RadarPlanViewSettings.IS_SHOWING_COASTLINE_PROPERTY)) {
			coastlineCheckbox.setSelected(radarPlanViewSettings.isShowingCoastline());
		} else if (propertyName.equals(RadarPlanViewSettings.IS_SHOWING_RINGS_PROPERTY)) {
			ringsCheckbox.setSelected(radarPlanViewSettings.isShowingRings());
		} else if (propertyName.equals(RadarPlanViewSettings.IS_SHOWING_SCALELINE_PROPERTY)) {
			scaleLineCheckbox.setSelected(radarPlanViewSettings.isShowingScaleLine());
		}
	}
}
