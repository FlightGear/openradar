package de.knewcleus.radar.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class GeneralToolbox extends JInternalFrame implements InternalFrameListener, ActionListener {
	private static final long serialVersionUID = 997249369785932627L;

	protected final RadarDesktop desktop;
	
	protected final GridBagLayout toolBoxLayout=new GridBagLayout();
	protected final SquaredHorizontalLayout parkingRankLayout=new SquaredHorizontalLayout();
	protected final JPanel parkingRank=new JPanel(parkingRankLayout);
	protected final JToggleButton toggleRPVD=new JToggleButton("PVD");
	protected final JToggleButton toggleVAW=new JToggleButton("VAW");
	protected final JToggleButton toggleCRD=new JToggleButton("CRD");
	protected final JToggleButton togglePREF=new JToggleButton("PREF");
	protected final JToggleButton toggleFLEX=new JToggleButton("FLEX");
	protected final JButton clock=new JButton();
	
	protected final PreferencesFrame preferencesFrame;

	protected final Timer clockUpdateTimer;
	
	protected final TimeZone timeZone=TimeZone.getTimeZone("GMT");
	protected final Calendar calendar=Calendar.getInstance(timeZone);
	
	public GeneralToolbox(final RadarDesktop desktop) {
		super("GENERAL TOOLBOX",false,false,false,false);
		
		this.desktop=desktop;
		
		setLayout(toolBoxLayout);
		parkingRank.setLayout(parkingRankLayout);

		parkingRank.add(toggleRPVD);
		//parkingRank.add(toggleVAW);
		//parkingRank.add(toggleCRD);
		parkingRank.add(togglePREF);
		//parkingRank.add(toggleFLEX);
		
		toggleRPVD.addActionListener(this);
		togglePREF.addActionListener(this);
		
		clock.addActionListener(this);
		clock.setBackground(Palette.WINDOW_FAWN);
		
		preferencesFrame=new PreferencesFrame(desktop);
		preferencesFrame.setVisible(true);
		desktop.add(preferencesFrame, JLayeredPane.PALETTE_LAYER);
		preferencesFrame.setVisible(false);
		preferencesFrame.addInternalFrameListener(this);
		
		GridBagConstraints gridBagConstraints=new GridBagConstraints();
		gridBagConstraints.fill=GridBagConstraints.VERTICAL;
		add(parkingRank,gridBagConstraints);
		add(clock,gridBagConstraints);
		
		pack();
		
		clockUpdateTimer=new Timer(1000,this);
		clockUpdateTimer.setInitialDelay(0); // start at once
		clockUpdateTimer.start();
	}
	
	public PreferencesFrame getPreferencesFrame() {
		return preferencesFrame;
	}
	
	public void setRPVDPresent(boolean b) {
		toggleRPVD.setSelected(b);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==toggleRPVD) {
			desktop.acquireRadarPlanViewDisplay();
		} else if (e.getSource()==togglePREF) {
			preferencesFrame.setVisible(togglePREF.isSelected());
		} else if (e.getSource()==clock) {
			parkingRank.setVisible(!parkingRank.isVisible());
			pack();
		} else if (e.getSource()==clockUpdateTimer) {
			calendar.setTimeInMillis(System.currentTimeMillis());
			int hour,minute,second;
			hour=calendar.get(Calendar.HOUR_OF_DAY);
			minute=calendar.get(Calendar.MINUTE);
			second=calendar.get(Calendar.SECOND);
			String timeString=String.format("%02d:%02d:%02d",hour,minute,second);
			clock.setText(timeString);
			invalidate();
			pack();
		}
	}
	
	private void setFrameState(Object source, boolean state) {
		if (source==preferencesFrame) {
			togglePREF.setSelected(state);
		}
	}
	
	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
		setFrameState(e.getSource(),((JInternalFrame)e.getSource()).isVisible());
	}
	
	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
		setFrameState(e.getSource(),((JInternalFrame)e.getSource()).isVisible());
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
