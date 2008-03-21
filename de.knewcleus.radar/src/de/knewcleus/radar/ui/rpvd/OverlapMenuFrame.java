package de.knewcleus.radar.ui.rpvd;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import de.knewcleus.radar.ui.RadarWorkstation;

public class OverlapMenuFrame extends JInternalFrame implements PropertyChangeListener, ActionListener {
	private static final long serialVersionUID = 2239922786869601242L;

	protected final RadarToolbox radarToolbox;
	protected final RadarPlanViewSettings radarPlanViewSettings;

	protected final JToggleButton autoLabellingToggle=new JToggleButton("AUTO");
	
	protected final JPanel bottomPanel=new JPanel();
	
	protected final ButtonGroup lengthGroup=new ButtonGroup();
	protected final JRadioButton lengthSmall=new JRadioButton("S"); 
	protected final JRadioButton lengthMedium=new JRadioButton("M"); 
	protected final JRadioButton lengthLong=new JRadioButton("L");
	
	protected final ButtonGroup positionGroup=new ButtonGroup();
	protected final JRadioButton positionButtons[]=new JRadioButton[8];
	protected final StandardLabelPosition labelPositions[]=new StandardLabelPosition[] {
		StandardLabelPosition.TOPLEFT,	
		StandardLabelPosition.TOP,	
		StandardLabelPosition.TOPRIGHT,	
		StandardLabelPosition.RIGHT,
		StandardLabelPosition.BOTTOMRIGHT,	
		StandardLabelPosition.BOTTOM,
		StandardLabelPosition.BOTTOMLEFT,	
		StandardLabelPosition.LEFT
	};
	
	public OverlapMenuFrame(RadarToolbox radarToolbox) {
		super("LABEL POSITION",false,true,false,false);
		
		this.radarToolbox=radarToolbox;
		
		RadarPlanViewDisplay radarPlanViewDisplay=radarToolbox.getRadarPlanViewDisplay();
		RadarWorkstation radarWorkstation=radarPlanViewDisplay.getWorkstation();
		radarPlanViewSettings=radarWorkstation.getRadarPlanViewSettings();
		radarPlanViewSettings.addPropertyChangeListener(this);
		
		final JPanel topPanel=new JPanel();
		final JPanel lengthPanel=new JPanel();
		final JPanel positionPanel=new JPanel();
		
		setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
		bottomPanel.setLayout(new BoxLayout(bottomPanel,BoxLayout.Y_AXIS));
		lengthPanel.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
		positionPanel.setLayout(new GridBagLayout());
		
		bottomPanel.add(lengthPanel);
		bottomPanel.add(positionPanel);
		topPanel.add(autoLabellingToggle);
		
		add(topPanel);
		add(bottomPanel);
		
		lengthPanel.add(lengthSmall);
		lengthPanel.add(lengthMedium);
		lengthPanel.add(lengthLong);
		lengthGroup.add(lengthSmall);
		lengthGroup.add(lengthMedium);
		lengthGroup.add(lengthLong);
		
		autoLabellingToggle.addActionListener(this);
		lengthSmall.addActionListener(this);
		lengthMedium.addActionListener(this);
		lengthLong.addActionListener(this);
		
		GridBagConstraints gridBagConstraints=new GridBagConstraints();
		for (int i=0;i<8;i++) {
			positionButtons[i]=new JRadioButton("");
			positionButtons[i].setMinimumSize(new Dimension(10,10));
			gridBagConstraints.gridx=labelPositions[i].getDx()+1;
			gridBagConstraints.gridy=labelPositions[i].getDy()+1;
			positionPanel.add(positionButtons[i],gridBagConstraints);
			positionGroup.add(positionButtons[i]);
			positionButtons[i].addActionListener(this);
		}
		
		updateAutolabellingStatus();
		updateLabelPosition();
		updateLabelDistance();
		
		pack();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==autoLabellingToggle) {
			final boolean status=autoLabellingToggle.isSelected();
			radarPlanViewSettings.setAutomaticLabelingEnabled(status);
		} else if (e.getSource()==lengthSmall) {
			radarPlanViewSettings.setStandardLabelDistance(StandardLabelDistance.SMALL);
		} else if (e.getSource()==lengthMedium) {
			radarPlanViewSettings.setStandardLabelDistance(StandardLabelDistance.MEDIUM);
		} else if (e.getSource()==lengthLong) {
			radarPlanViewSettings.setStandardLabelDistance(StandardLabelDistance.LONG);
		} else {
			for (int i=0;i<positionButtons.length;i++) {
				if (e.getSource()==positionButtons[i]) {
					radarPlanViewSettings.setStandardLabelPosition(labelPositions[i]);
				}
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String propertyName=evt.getPropertyName();
		if (propertyName.equals(RadarPlanViewSettings.IS_AUTOMATIC_LABELING_ENABLED_PROPERTY)) {
			updateAutolabellingStatus();
		} else if (propertyName.equals(RadarPlanViewSettings.STANDARD_LABEL_POSITION_PROPERTY)) {
			updateLabelPosition();
		} else if (propertyName.equals(RadarPlanViewSettings.STANDARD_LABEL_DISTANCE_PROPERTY)) {
			updateLabelDistance();
		}
	}
	
	private void updateAutolabellingStatus() {
		final boolean status=radarPlanViewSettings.isAutomaticLabelingEnabled();
		autoLabellingToggle.setSelected(status);
		bottomPanel.setVisible(!status);
		invalidate();
		pack();
	}
	
	private void updateLabelPosition() {
		final StandardLabelPosition position=radarPlanViewSettings.getStandardLabelPosition();
		
		for (int i=0;i<labelPositions.length;i++) {
			if (labelPositions[i]==position) {
				positionButtons[i].setSelected(true);
			}
		}
	}
	
	private void updateLabelDistance() {
		final StandardLabelDistance distance=radarPlanViewSettings.getStandardLabelDistance();
		
		switch (distance) {
		case SMALL:
			lengthSmall.setSelected(true);
			break;
		case MEDIUM:
			lengthMedium.setSelected(true);
			break;
		case LONG:
			lengthLong.setSelected(true);
			break;
		}
	}
}
