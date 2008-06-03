package de.knewcleus.openradar.ui.rpvd.toolbox;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import de.knewcleus.openradar.ui.RadarWorkstation;
import de.knewcleus.openradar.ui.rpvd.RadarPlanViewDisplay;
import de.knewcleus.openradar.ui.rpvd.RadarPlanViewSettings;

public class SpeedAndTrackPanel extends JPanel implements PropertyChangeListener {
	private static final long serialVersionUID = 2264072614359267276L;
	
	protected final RadarToolbox radarToolbox;
	protected final RadarPlanViewSettings settings;

	protected final Integer[] speedVectorLengths=new Integer[] { 0,1,2,3,4,5 };
	protected final JLabel speedVectorLabel=new JLabel("Speed Vector");
	protected final JPanel speedVectorPanel=new JPanel();
	protected final JRadioButton[] speedVectorButtons=new JRadioButton[6];
	
	protected final Integer[] trackHistoryLengths=new Integer[] { 0,3,5,7 };
	protected final JLabel trackHistoryLabel=new JLabel("Track History");
	protected final JPanel trackHistoryPanel=new JPanel();
	protected final JRadioButton[] trackHistoryButtons=new JRadioButton[4];
	
	public SpeedAndTrackPanel(final RadarToolbox radarToolbox) {
		this.radarToolbox=radarToolbox;
		
		setLayout(new GridLayout(4,1));
		
		add(speedVectorLabel);
		add(speedVectorPanel);
		add(trackHistoryLabel);
		add(trackHistoryPanel);
		
		final RadarPlanViewDisplay radarPlanViewDisplay=radarToolbox.getRadarPlanViewDisplay();
		final RadarWorkstation workstation=radarPlanViewDisplay.getWorkstation();
		settings=workstation.getRadarPlanViewSettings();
		
		ActionListener speedVectorChange=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int minutes=Integer.parseInt(e.getActionCommand());
				settings.setSpeedVectorMinutes(minutes);
			}
		};
		
		ButtonGroup speedVectorGroup=new ButtonGroup();
		speedVectorPanel.setLayout(new GridBagLayout());
		for (int i=0;i<speedVectorLengths.length;i++) {
			String label=Integer.toString(speedVectorLengths[i]);
			JRadioButton button=new JRadioButton(label);
			speedVectorButtons[i]=button;
			button.setActionCommand(label);
			button.addActionListener(speedVectorChange);
			speedVectorGroup.add(button);
			speedVectorPanel.add(button);
			
			if (settings.getSpeedVectorMinutes()==speedVectorLengths[i]) {
				button.setSelected(true);
			}
		}
		
		
		ActionListener trackHistoryChange=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int trackHistoryLength=Integer.parseInt(e.getActionCommand());
				settings.setTrackHistoryLength(trackHistoryLength);
			}
		};
		ButtonGroup trackHistoryGroup=new ButtonGroup();
		trackHistoryPanel.setLayout(new GridBagLayout());
		for (int i=0;i<trackHistoryLengths.length;i++) {
			String label=Integer.toString(trackHistoryLengths[i]);
			JRadioButton button=new JRadioButton(label);
			trackHistoryButtons[i]=button;
			button.setActionCommand(label);
			button.addActionListener(trackHistoryChange);
			trackHistoryGroup.add(button);
			trackHistoryPanel.add(button);
			
			if (settings.getTrackHistoryLength()==trackHistoryLengths[i]) {
				button.setSelected(true);
			}
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(RadarPlanViewSettings.SPEED_VECTOR_MINUTES_PROPERTY)) {
			for (int i=0;i<speedVectorLengths.length;i++) {
				if (settings.getSpeedVectorMinutes()==speedVectorLengths[i]) {
					speedVectorButtons[i].setSelected(true);
				}
			}
		} else if (evt.getPropertyName().equals(RadarPlanViewSettings.TRACK_HISTORY_LENGTH_PROPERTY)) {
			for (int i=0;i<speedVectorLengths.length;i++) {
				if (settings.getTrackHistoryLength()==trackHistoryLengths[i]) {
					trackHistoryButtons[i].setSelected(true);
				}
			}
		}
	}
}
