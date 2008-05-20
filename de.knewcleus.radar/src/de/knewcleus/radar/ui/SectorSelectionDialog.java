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
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class SectorSelectionDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -2675313456609634813L;
	
	protected final JComboBox urlEntryBox=new JComboBox();
	protected final JButton fileBrowserButton=new JButton("Browse");
	
	protected final JButton okButton=new JButton("OK");
	protected final JButton cancelButton=new JButton("Cancel");
	
	protected final JFileChooser fileChooser=new JFileChooser();
	
	protected URL selectedURL=null;

	public SectorSelectionDialog() {
		super((Frame)null, "Select Sector description file", true);
		
		JPanel entryPanel=new JPanel();
		JPanel buttonsPanel=new JPanel();
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		add(entryPanel);
		add(buttonsPanel);
		
		entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.X_AXIS));
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		
		Dimension minSize=urlEntryBox.getMinimumSize();
		final Font urlFont=urlEntryBox.getFont();
		final FontMetrics urlFontMetrics=urlEntryBox.getFontMetrics(urlFont);
		final int minURLWidth=urlFontMetrics.charWidth('m')*40;
		urlEntryBox.setPreferredSize(new Dimension(minSize.width+minURLWidth, minSize.height));
		urlEntryBox.setEditable(true);
		
		entryPanel.add(urlEntryBox);
		entryPanel.add(fileBrowserButton);
		
		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		urlEntryBox.addActionListener(this);
		fileBrowserButton.addActionListener(this);
		
		populateRecentSectorsList();
		
		pack();
	}
	
	protected void populateRecentSectorsList() {
		Preferences preferences=Preferences.userNodeForPackage(SectorSelectionDialog.class);
		Preferences recentSectors=preferences.node("recentSectors");
		final int recentSectorCount=preferences.getInt("recentSectorCount", 0);
		
		for (int i=0;i<recentSectorCount;i++) {
			String recentSector=recentSectors.get(String.format("item%d",i), null);
			if (recentSector!=null) {
				urlEntryBox.addItem(recentSector);
			}
		}
	}
	
	protected void storeRecentSectorsList() {
		Preferences preferences=Preferences.userNodeForPackage(SectorSelectionDialog.class);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==okButton) {
			urlEntryBox.addItem(urlEntryBox.getSelectedItem());
			storeRecentSectorsList();
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
