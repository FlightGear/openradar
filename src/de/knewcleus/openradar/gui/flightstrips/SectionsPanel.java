package de.knewcleus.openradar.gui.flightstrips;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.knewcleus.openradar.gui.flightstrips.config.SectionsManager;

public class SectionsPanel extends JPanel implements ListDataListener {

	private static final long serialVersionUID = -667775304844271231L;
	
	private final GridBagLayout layout = new GridBagLayout();

	public SectionsPanel() {
		// layout
		setOpaque(false);
		setLayout(layout);
	}

	public void recreateContents(SectionsManager sectionsManager) {
		synchronized (getTreeLock()) {
			// remove all section panels
			removeAll();
			// add all section panels
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			for (SectionData section : sectionsManager.getSections()) {
				add(section.getPanel(), gridBagConstraints);
				gridBagConstraints.gridy++;
			}
			revalidate();
		}
	}

	
	/*
	public void moveSection(SectionPanel sectionpanel, int target_index) {
		// logically move section
		master.getLogicManager().moveSectionToIndex(sectionpanel.getSection(), target_index);
		// visually move section
		synchronized (getTreeLock()) {
			ArrayList<SectionData> sections = master.getLogicManager().getSections();
			for (int i = 0; i < sections.size(); i++) {
				SectionData section = sections.get(i);
				GridBagConstraints gridBagConstraints = layout.getConstraints(section.getPanel());
				if (gridBagConstraints.gridy != i) {
					gridBagConstraints.gridy = i;
					layout.setConstraints(section.getPanel(), gridBagConstraints);
				}
			}
			revalidate();
		}
	}
	*/
	
	// --- ListDataListener ---
	
	@Override
	public void intervalAdded(ListDataEvent e) {
		recreateContents((SectionsManager) e.getSource());
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		recreateContents((SectionsManager) e.getSource());
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		recreateContents((SectionsManager) e.getSource());
	}
	
}
