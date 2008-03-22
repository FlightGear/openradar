package de.knewcleus.radar.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.Timer;

public class GeneralToolbox extends JInternalFrame {
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
	
	protected final ActionListener clockUpdateListener;
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
		//parkingRank.add(togglePREF);
		//parkingRank.add(toggleFLEX);
		
		toggleRPVD.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				desktop.acquireRadarPlanViewDisplay();
			}
		});
		
		clock.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parkingRank.setVisible(!parkingRank.isVisible());
				pack();
			}
		});
		clock.setBackground(Palette.WINDOW_FAWN);
		
		GridBagConstraints gridBagConstraints=new GridBagConstraints();
		gridBagConstraints.fill=GridBagConstraints.VERTICAL;
		add(parkingRank,gridBagConstraints);
		add(clock,gridBagConstraints);
		
		pack();
		
		clockUpdateListener=new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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
		};
		
		clockUpdateTimer=new Timer(1000,clockUpdateListener);
		clockUpdateTimer.setInitialDelay(0); // start at once
		clockUpdateTimer.start();
	}
	
	public void setRPVDPresent(boolean b) {
		toggleRPVD.setSelected(b);
	}
}
