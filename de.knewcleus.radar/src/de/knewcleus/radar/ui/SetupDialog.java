package de.knewcleus.radar.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;

import de.knewcleus.fgfs.multiplayer.MultiplayerClient;

public class SetupDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -2675313456609634813L;
	
	protected final JComboBox urlEntryBox=new JComboBox();
	protected final JButton fileBrowserButton=new JButton("Browse");
	
	protected final JTextField serverHostField=new JTextField();
	protected final JTextField serverPortField=new JTextField();
	protected final JTextField clientPortField=new JTextField();
	
	protected final JButton okButton=new JButton("OK");
	protected final JButton cancelButton=new JButton("Cancel");
	
	protected final JFileChooser fileChooser=new JFileChooser();
	
	protected URL selectedURL=null;

	public SetupDialog() {
		super((Frame)null, "Select Sector description file", true);
		
		JPanel entryPanel=new JPanel();
		JPanel multiplayerPanel=new JPanel();
		JPanel buttonsPanel=new JPanel();
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		add(entryPanel);
		add(multiplayerPanel);
		add(buttonsPanel);
		
		GroupLayout mpLayout=new GroupLayout(multiplayerPanel);
		entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.X_AXIS));
		multiplayerPanel.setLayout(mpLayout);
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		
		Dimension minSize=urlEntryBox.getMinimumSize();
		final Font urlFont=urlEntryBox.getFont();
		final FontMetrics urlFontMetrics=urlEntryBox.getFontMetrics(urlFont);
		final int minURLWidth=urlFontMetrics.charWidth('m')*40;
		urlEntryBox.setPreferredSize(new Dimension(minSize.width+minURLWidth, minSize.height));
		urlEntryBox.setEditable(true);
		
		entryPanel.add(urlEntryBox);
		entryPanel.add(fileBrowserButton);
		
		GroupLayout.SequentialGroup mpHorizGroup=mpLayout.createSequentialGroup();
		GroupLayout.SequentialGroup mpVertGroup=mpLayout.createSequentialGroup();
		mpLayout.setHorizontalGroup(mpHorizGroup);
		mpLayout.setVerticalGroup(mpVertGroup);
		
		JLabel serverHostLabel=new JLabel("Server Hostname:");
		JLabel serverPortLabel=new JLabel("Server Port:");
		JLabel clientPortLabel=new JLabel("Client Port:");
		
		mpHorizGroup.addGroup(mpLayout.createParallelGroup().
				addComponent(serverHostLabel).addComponent(serverPortLabel).addComponent(clientPortLabel));
		mpHorizGroup.addGroup(mpLayout.createParallelGroup().
				addComponent(serverHostField).addComponent(serverPortField).addComponent(clientPortField));
		
		mpVertGroup.addGroup(mpLayout.createParallelGroup(Alignment.BASELINE).
				addComponent(serverHostLabel).addComponent(serverHostField));
		mpVertGroup.addGroup(mpLayout.createParallelGroup(Alignment.BASELINE).
				addComponent(serverPortLabel).addComponent(serverPortField));
		mpVertGroup.addGroup(mpLayout.createParallelGroup(Alignment.BASELINE).
				addComponent(clientPortLabel).addComponent(clientPortField));
		
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		urlEntryBox.addActionListener(this);
		fileBrowserButton.addActionListener(this);
		
		populateRecentSectorsList();
		
		serverHostField.setText(MultiplayerClient.getStandardServerHost());
		serverPortField.setText(Integer.toString(MultiplayerClient.getStandardServerPort()));
		clientPortField.setText(Integer.toString(MultiplayerClient.getStandardClientPort()));
		
		pack();
	}
	
	protected void populateRecentSectorsList() {
		Preferences preferences=Preferences.userNodeForPackage(SetupDialog.class);
		Preferences recentSectors=preferences.node("recentSectors");
		final int recentSectorCount=preferences.getInt("recentSectorCount", 0);
		
		final Set<String> knownSectors=new HashSet<String>();
		
		for (int i=0;i<recentSectorCount;i++) {
			String recentSector=recentSectors.get(String.format("item%d",i), null);
			if (recentSector!=null) {
				if (knownSectors.contains(recentSector))
					continue;
				knownSectors.add(recentSector);
				urlEntryBox.addItem(recentSector);
			}
		}
	}
	
	protected void storeRecentSectorsList() {
		Preferences preferences=Preferences.userNodeForPackage(SetupDialog.class);
		Preferences recentSectors=preferences.node("recentSectors");
		final int recentSectorCount=urlEntryBox.getItemCount();
		preferences.putInt("recentSectorCount", recentSectorCount);
		
		try {
			recentSectors.clear();
		} catch (BackingStoreException e) {
			// ignore...
		}
		
		for (int i=0;i<recentSectorCount;i++) {
			Object recentSector=urlEntryBox.getItemAt(i);
			recentSectors.put(String.format("item%d",i), recentSector.toString());
		}
	}
	
	protected void storeMultiplayerSettings() {
		final Preferences mpPreferences=MultiplayerClient.getPreferences();
		mpPreferences.put(MultiplayerClient.serverHostKey, serverHostField.getText());
		mpPreferences.putInt(MultiplayerClient.serverPortKey, Integer.parseInt(serverPortField.getText()));
		mpPreferences.putInt(MultiplayerClient.clientPortKey, Integer.parseInt(clientPortField.getText()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==okButton) {
			final Object selectedItem=urlEntryBox.getSelectedItem();
			if (urlEntryBox.getSelectedIndex()!=-1) {
				urlEntryBox.removeItemAt(urlEntryBox.getSelectedIndex());
			}
			urlEntryBox.insertItemAt(selectedItem, 0);
			urlEntryBox.setSelectedIndex(0);
			storeRecentSectorsList();
			storeMultiplayerSettings();
			setVisible(false);
		} else if (e.getSource()==cancelButton) {
			selectedURL=null;
			setVisible(false);
		} else if (e.getSource()==urlEntryBox) {
			String enteredURL=urlEntryBox.getSelectedItem().toString();
			try {
				selectedURL=new URL(enteredURL);
				okButton.setEnabled(true);
			} catch (MalformedURLException e1) {
				selectedURL=null;
				okButton.setEnabled(false);
			}
		} else if (e.getSource()==fileBrowserButton) {
			if (fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
				final File selectedFile=fileChooser.getSelectedFile();
				try {
					selectedURL=selectedFile.toURI().toURL();
					urlEntryBox.setSelectedItem(selectedURL);
					okButton.setEnabled(true);
				} catch (MalformedURLException e1) {
					selectedURL=null;
					okButton.setEnabled(false);
				}
			}
		}
	}
	
	public URL getSelectedURL() {
		return selectedURL;
	}
}
