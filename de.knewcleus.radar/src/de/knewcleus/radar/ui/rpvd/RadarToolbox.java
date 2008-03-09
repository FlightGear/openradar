package de.knewcleus.radar.ui.rpvd;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public class RadarToolbox extends JInternalFrame {
	private static final long serialVersionUID = -3296094812456539553L;

	protected final RadarPlanViewDisplay radarPlanViewDisplay;
	protected final JPanel buttonPanel=new JPanel();
	protected final JPanel zoomPanel=new JPanel();
	protected final JPanel heightFilterPanel=new JPanel();
	protected final JPanel speedAndTrackPanel;
	
	protected final JToggleButton mapMenuButton=new JToggleButton("MAP MENU");
	protected final JToggleButton labelMenuButton=new JToggleButton("LABEL MENU");
	protected final JToggleButton overlapMenuButton=new JToggleButton("OVERLAP");
	protected final JToggleButton modeMenuButton=new JToggleButton("MODES");
	
	public RadarToolbox(RadarPlanViewDisplay radarPlanViewDisplay) {
		super("RADAR TOOLBOX",false,false,false,true);
		this.radarPlanViewDisplay=radarPlanViewDisplay;
		
		speedAndTrackPanel=new SpeedAndTrackPanel(this);
		

		setLayout(new GridBagLayout());
		
		buttonPanel.setLayout(new GridLayout());
		buttonPanel.add(mapMenuButton);
		buttonPanel.add(labelMenuButton);
		buttonPanel.add(overlapMenuButton);
		buttonPanel.add(modeMenuButton);
		
		GridBagConstraints gridBagConstraints=new GridBagConstraints();
		
		gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridwidth=2;
		add(buttonPanel,gridBagConstraints);
		
		gridBagConstraints.fill=GridBagConstraints.BOTH;
		gridBagConstraints.gridwidth=1;
		gridBagConstraints.gridheight=2;
		add(speedAndTrackPanel,gridBagConstraints);

		gridBagConstraints.gridx=0;
		gridBagConstraints.gridheight=1;
		add(zoomPanel,gridBagConstraints);
		
		gridBagConstraints.gridx=GridBagConstraints.RELATIVE;
		add(heightFilterPanel,gridBagConstraints);
		
		pack();
	}
	
	public RadarPlanViewDisplay getRadarPlanViewDisplay() {
		return radarPlanViewDisplay;
	}
}
