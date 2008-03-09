package de.knewcleus.radar.ui.rpvd;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.knewcleus.radar.ui.RadarWorkstation;

public class ZoomPanel extends JPanel implements ChangeListener, PropertyChangeListener {
	private static final long serialVersionUID = 3344886170820128917L;
	
	protected final static int minimumRange=1;
	protected final static int maximumRange=500;
	
	protected final RadarToolbox radarToolbox;
	protected final RadarPlanViewSettings radarPlanViewSettings;
	
	protected final JLabel zoomLabel=new JLabel("Zoom");
	protected final JSlider zoomSlider=new JSlider(JSlider.HORIZONTAL,minimumRange,maximumRange,minimumRange);
	protected final JLabel zoomValueLabel=new JLabel();
	
	public ZoomPanel(RadarToolbox radarToolbox) {
		this.radarToolbox=radarToolbox;
		
		final RadarPlanViewDisplay radarPlanViewDisplay=radarToolbox.getRadarPlanViewDisplay();
		final RadarWorkstation radarWorkstation=radarPlanViewDisplay.getWorkstation();
		radarPlanViewSettings=radarWorkstation.getRadarPlanViewSettings();
		radarPlanViewSettings.addPropertyChangeListener(this);
		
		zoomSlider.addChangeListener(this);
		zoomSlider.setValue(radarPlanViewSettings.getRange());
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gridBagConstraints=new GridBagConstraints();
		
		gridBagConstraints.anchor=GridBagConstraints.WEST;
		gridBagConstraints.gridwidth=GridBagConstraints.REMAINDER;
		add(zoomLabel,gridBagConstraints);
		
		gridBagConstraints.gridwidth=1;
		add(zoomSlider,gridBagConstraints);
		add(zoomValueLabel,gridBagConstraints);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(RadarPlanViewSettings.RANGE_PROPERTY)) {
			zoomSlider.setValue(radarPlanViewSettings.getRange());
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		assert(e.getSource()==zoomSlider);
		int value=zoomSlider.getValue();
		
		String valueString=String.format("%03d",value);
		zoomValueLabel.setText(valueString);
		radarPlanViewSettings.setRange(value);
	}
}
