package de.knewcleus.openradar.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class PreferencesFrame extends JInternalFrame implements ActionListener {
	private static final long serialVersionUID = 1840851913787098021L;
	
	protected final RadarDesktop desktop;
	protected final JButton loadButton=new JButton("LOAD");
	protected final JButton saveButton=new JButton("SAVE");
	
	protected final ButtonGroup preferencesGroup=new ButtonGroup();
	protected final JRadioButton[] preferencesButtons=new JRadioButton[10];
	
	public PreferencesFrame(RadarDesktop desktop) {
		super("PREFERENCES",false,true,false,false);
		
		this.desktop=desktop;
		
		final JPanel actionButtonPanel=new JPanel();
		final JPanel preferenceButtonPanel=new JPanel();
		
		setLayout(new GridBagLayout());
		
		GridBagConstraints gridBagConstraints=new GridBagConstraints();
		gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridwidth=GridBagConstraints.REMAINDER;
		
		add(actionButtonPanel,gridBagConstraints);
		add(preferenceButtonPanel,gridBagConstraints);
		
		actionButtonPanel.setLayout(new GridLayout(1,2));
		actionButtonPanel.add(loadButton);
		actionButtonPanel.add(saveButton);
		
		final int buttonCount=preferencesButtons.length;
		preferenceButtonPanel.setLayout(new GridLayout((buttonCount+1)/2,2));
		for (int i=0;i<buttonCount;i++) {
			String label=Integer.toString(i+1);
			JRadioButton button=new JRadioButton(label);
			preferencesGroup.add(button);
			preferenceButtonPanel.add(button);
			preferencesButtons[i]=button;
			button.setActionCommand(label);
		}
		
		pack();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==loadButton || e.getSource()==saveButton) {
			String prefSetName=preferencesGroup.getSelection().getActionCommand();
			Preferences prefs=Preferences.userNodeForPackage(RadarWorkstation.class);
			Preferences prefSet=prefs.node(prefSetName);
			if (e.getSource()==loadButton) {
				desktop.getWorkstation().loadPreferences(prefSet);
			} else if (e.getSource()==saveButton) {
				desktop.getWorkstation().savePreferences(prefSet);
			}
		}
	}
}
