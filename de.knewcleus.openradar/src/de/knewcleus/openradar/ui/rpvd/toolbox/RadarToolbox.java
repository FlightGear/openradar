package de.knewcleus.openradar.ui.rpvd.toolbox;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import de.knewcleus.openradar.ui.rpvd.RadarPlanViewDisplay;

public class RadarToolbox extends JInternalFrame implements ActionListener, InternalFrameListener {
	private static final long serialVersionUID = -3296094812456539553L;

	protected final RadarPlanViewDisplay radarPlanViewDisplay;
	protected final JPanel buttonPanel=new JPanel();
	protected final JPanel zoomPanel;
	protected final JPanel heightFilterPanel=new JPanel();
	protected final JPanel speedAndTrackPanel;
	
	protected final JToggleButton mapMenuButton=new JToggleButton("MAP MENU");
	protected final JToggleButton overlapMenuButton=new JToggleButton("OVERLAP");
	protected final JToggleButton modeMenuButton=new JToggleButton("MODES");
	
	protected final MapMenuFrame mapMenuFrame;
	protected final OverlapMenuFrame overlapMenuFrame;
	
	public RadarToolbox(RadarPlanViewDisplay radarPlanViewDisplay) {
		super("RADAR TOOLBOX",false,false,false,true);
		this.radarPlanViewDisplay=radarPlanViewDisplay;
		
		zoomPanel=new ZoomPanel(this);
		speedAndTrackPanel=new SpeedAndTrackPanel(this);
		
		mapMenuFrame=new MapMenuFrame(this);
		overlapMenuFrame=new OverlapMenuFrame(this);

		setLayout(new GridBagLayout());
		
		buttonPanel.setLayout(new GridLayout());
		buttonPanel.add(mapMenuButton);
		buttonPanel.add(overlapMenuButton);
		buttonPanel.add(modeMenuButton);
		
		mapMenuButton.addActionListener(this);
		overlapMenuButton.addActionListener(this);
		mapMenuFrame.addInternalFrameListener(this);
		overlapMenuFrame.addInternalFrameListener(this);
		
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
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JInternalFrame frame;
		
		JToggleButton toggleButton=(JToggleButton)e.getSource();
		if (e.getSource()==mapMenuButton) {
			frame=mapMenuFrame;
		} else if (e.getSource()==overlapMenuButton) {
			frame=overlapMenuFrame;
		} else {
			return;
		}
		
		if (toggleButton.isSelected()) {
			frame.setVisible(true);
			radarPlanViewDisplay.getSubDesktopPane().add(frame);
		} else {
			frame.setVisible(false);
			radarPlanViewDisplay.getSubDesktopPane().remove(frame);
		}
	}
	
	private void setFrameState(Object source, boolean state) {
		if (source==mapMenuFrame) {
			mapMenuButton.setSelected(state);
		} else if (source==overlapMenuFrame) {
			overlapMenuButton.setSelected(state);
		}
	}
	
	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
		setFrameState(e.getSource(),true);
	}
	
	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		setFrameState(e.getSource(),false);
	}
	
	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
		// NO-OP
	}
	
	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		// NO-OP
	}
	
	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
		// NO-OP
	}
	
	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
		// NO-OP
	}
	
	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
		// NO-OP
	}
}
