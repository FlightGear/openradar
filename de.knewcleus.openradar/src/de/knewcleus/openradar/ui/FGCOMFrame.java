package de.knewcleus.openradar.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class FGCOMFrame extends WorkstationGlobalFrame implements ActionListener {
	private static final long serialVersionUID = 287028884912477276L;
	
	protected final JTextField activeFrequency=new JTextField();
	protected final JButton swapButton=new JButton("<->");
	protected final JTextField standbyFrequency=new JTextField();
	
	public FGCOMFrame(RadarWorkstation workstation) {
		super(workstation, "COM", "FGCOM Console", false, false, false, false);
		
		final JLabel activeLabel=new JLabel("active:");
		final JLabel standbyLabel=new JLabel("standby:");
		setLayout(new FlowLayout());
		add(activeLabel);
		add(activeFrequency);
		add(swapButton);
		add(standbyLabel);
		add(standbyFrequency);

		activeFrequency.setColumns(7);
		standbyFrequency.setColumns(7);
		
		swapButton.addActionListener(this);
		
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==swapButton) {
			final String active=activeFrequency.getText();
			final String standby=standbyFrequency.getText();
			activeFrequency.setText(standby);
			standbyFrequency.setText(active);
		}
	}
}
