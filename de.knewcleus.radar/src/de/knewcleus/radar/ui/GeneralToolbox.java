package de.knewcleus.radar.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import de.knewcleus.radar.ui.rpvd.RadarPlanViewDisplay;

public class GeneralToolbox extends JInternalFrame {
	private static final long serialVersionUID = 997249369785932627L;

	protected final RadarDesktop desktop;
	
	protected final FlowLayout toolBoxLayout=new FlowLayout();
	protected final SquaredHorizontalLayout parkingRankLayout=new SquaredHorizontalLayout();
	protected final JPanel parkingRank=new JPanel(parkingRankLayout);
	protected final JButton toggleRPVD=new JButton("PVD");
	protected final JButton toggleVAW=new JButton("VAW");
	protected final JButton toggleCRD=new JButton("CRD");
	protected final JButton togglePREF=new JButton("PREF");
	protected final JButton toggleFLEX=new JButton("FLEX");
	protected final JLabel clockLabel=new JLabel("15:52:59");
	
	protected final ActionListener clockUpdateListener;
	protected final Timer clockUpdateTimer;
	
	protected final TimeZone timeZone=TimeZone.getTimeZone("GMT");
	protected final Calendar calendar=Calendar.getInstance(timeZone);
	
	public GeneralToolbox(final RadarDesktop desktop) {
		super("GENERAL TOOLBOX",false,true,false,false);
		
		this.desktop=desktop;
		
		setLayout(toolBoxLayout);
		parkingRank.setLayout(parkingRankLayout);
		
		parkingRank.add(toggleRPVD);
		parkingRank.add(toggleVAW);
		parkingRank.add(toggleCRD);
		parkingRank.add(togglePREF);
		parkingRank.add(toggleFLEX);
		
		toggleVAW.setEnabled(false);
		toggleCRD.setEnabled(false);
		togglePREF.setEnabled(false);
		toggleFLEX.setEnabled(false);
		
		toggleRPVD.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RadarPlanViewDisplay radarPlanViewDisplay=desktop.getWorkstation().getRadarPlanViewDisplay();
				radarPlanViewDisplay.setVisible(false);
				radarPlanViewDisplay.getParent().remove(radarPlanViewDisplay);
				desktop.add(radarPlanViewDisplay);
				radarPlanViewDisplay.setVisible(true);
			}
		});
		
		add(parkingRank);
		add(clockLabel);
		
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
				clockLabel.setText(timeString);
				invalidate();
			}
		};
		
		clockUpdateTimer=new Timer(1000,clockUpdateListener);
		clockUpdateTimer.setInitialDelay(0); // start at once
		clockUpdateTimer.start();
	}
}
