package de.knewcleus.openradar.ui.rpvd.toolbox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.knewcleus.openradar.ui.rpvd.IMapLayer;
import de.knewcleus.openradar.ui.rpvd.RadarPlanViewDisplay;
import de.knewcleus.openradar.ui.rpvd.RadarPlanViewPanel;

public class MapMenuFrame extends JInternalFrame {
	private static final long serialVersionUID = 6741748501762906857L;
	
	protected final RadarToolbox radarToolbox;
	
	protected final JPanel checkboxPanel=new JPanel();
	protected final JScrollPane scrollPane=new JScrollPane(checkboxPanel);
	
	public MapMenuFrame(RadarToolbox radarToolbox) {
		super("MAP",false,true,false,false);
		this.radarToolbox=radarToolbox;
		
		final RadarPlanViewDisplay radarPlanViewDisplay=radarToolbox.getRadarPlanViewDisplay();
		
		final RadarPlanViewPanel radarMapPanel=radarPlanViewDisplay.getRadarMapPanel();
		
		for (final IMapLayer layer: radarMapPanel.getMapLayers()) {
			final JCheckBox layerCheckbox=new JCheckBox(layer.getName());
			layerCheckbox.setSelected(layer.isVisible());
			layerCheckbox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					layer.setVisible(layerCheckbox.isSelected());
					radarMapPanel.repaint();
				}
			});
			checkboxPanel.add(layerCheckbox);
		}
		
		checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
		
		setContentPane(scrollPane);
		
		pack();
	}
}
