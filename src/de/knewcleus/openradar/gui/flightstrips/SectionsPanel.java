package de.knewcleus.openradar.gui.flightstrips;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JPanel;

import de.knewcleus.openradar.gui.GuiMasterController;

public class SectionsPanel extends JPanel {

	private static final long serialVersionUID = -667775304844271231L;
	
	private final GuiMasterController master;
	private final GridBagLayout layout = new GridBagLayout();

	public SectionsPanel(GuiMasterController master) {
		this.master = master;
		// layout
		setOpaque(false);
		setLayout(layout);
		// contents
		recreateContents();
	}

	public void recreateContents() {
		// remove all section panels
		removeAll();
		// add all section panels
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		ArrayList<SectionData> sections = master.getSectionsListManager().getSections();
		for (SectionData section : sections) {
			add(section.getPanel(), gridBagConstraints);
			gridBagConstraints.gridy++;
		}
		revalidate();
	}
	/*
	public int getGridY(JComponent component) {
		return layout.getConstraints(component).gridy;
	}
	
	protected JComponent[] getGridYOrderedComponents() {
		JComponent[] result = new JComponent[getComponentCount()];
		for (Component component : getComponents()) {
			result[getGridY((JComponent) component)] = (JComponent) component;
		}
		return result;
	}
	*/
	public void moveSection(SectionPanel sectionpanel, int target_index) {
		master.getSectionsListManager().moveSectionToIndex(sectionpanel.getSection(), target_index);
		synchronized (getTreeLock()) {
			ArrayList<SectionData> sections = master.getSectionsListManager().getSections();
			for (int i = 0; i < sections.size(); i++) {
				SectionData section = sections.get(i);
				GridBagConstraints gridBagConstraints = layout.getConstraints(section.getPanel());
				if (gridBagConstraints.gridy != i) {
					gridBagConstraints.gridy = i;
					layout.setConstraints(section.getPanel(), gridBagConstraints);
				}
			}
			revalidate();
			/*
			GridBagConstraints sourceConstraints = layout.getConstraints(sectionpanel);
			int source_y = sourceConstraints.gridy;
			int target_y = source_y + count;
			if (target_y < 0) target_y = 0;
			if (target_y > getComponentCount()) target_y = getComponentCount() - 1;
			if (source_y != target_y) {
				JComponent[] orderedComponents = getGridYOrderedComponents();
				int step = source_y < target_y ? 1 : -1;
				for (int y = source_y; step * y < step * target_y; y += step) {
					JComponent component = orderedComponents[y + step];
					GridBagConstraints targetConstraints = layout.getConstraints(component);
					targetConstraints.gridy = y;
					layout.setConstraints(component, targetConstraints);
				}
				sourceConstraints.gridy = target_y;
				layout.setConstraints(sectionpanel, sourceConstraints);
			}
			*/
		}
	}
	
}
