package de.knewcleus.openradar.gui.flightstrips;

/*
 * The SectionMouseAdapter is connected to a single SectionPanel
 * It provides functionality to drag sections  
 * and with a double click open the configurations dialog 
 */

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class SectionMouseAdapter extends MouseAdapter {

	private final SectionPanel sectionPanel;
	private SectionsPanel parent = null;
	private GridBagLayout layout = null;
	private Point panelPoint = null;
	private Point targetPoint;
	
	public SectionMouseAdapter(SectionPanel sectionPanel) {
		this.sectionPanel = sectionPanel;
		panelPoint = null;
	}
	
	protected SectionsPanel getParent() {
		if (parent == null)	parent = (SectionsPanel) sectionPanel.getParent();
		return parent;
	}
	
	protected GridBagLayout getLayout() {
		if (layout == null)	layout = (GridBagLayout) getParent().getLayout();
		return layout;
	}
	
	protected int getGridY(JComponent component) {
		if (component == null) return -1; 
		GridBagConstraints sourceConstraints = getLayout().getConstraints(component);
		return sourceConstraints.gridy;
	}
	
	protected void updateTargetPoint(MouseEvent e) {
		targetPoint = SwingUtilities.convertPoint(sectionPanel, e.getX(), e.getY(), getParent());
	}
	
	protected int getTargetGridY(MouseEvent e) {
		updateTargetPoint(e);
		for (Component component : parent.getComponents()) {
			if (!component.equals(sectionPanel)) {
				if (component.contains(SwingUtilities.convertPoint(getParent(), targetPoint, component))) return getGridY((JComponent) component);
			}
		}
		return getGridY(null); 
	}
	
	protected void updateParent(MouseEvent e) {
        int targetGridY = getTargetGridY(e);
        if (targetGridY >= 0) {
        	sectionPanel.getSection().getSectionsManager().moveSectionToIndex(sectionPanel.getSection(), targetGridY);
        }
		getParent().doLayout();
		getParent().setComponentZOrder(sectionPanel, 0);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (panelPoint == null) {
			panelPoint = e.getPoint();
			getParent().doLayout();
			getParent().setComponentZOrder(sectionPanel, 0);
			sectionPanel.getSection().getSectionsManager().setIsDragging(true);
		}
		updateParent(e);
		sectionPanel.setBounds(targetPoint.x - panelPoint.x, targetPoint.y - panelPoint.y, sectionPanel.getWidth(), sectionPanel.getHeight());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		sectionPanel.getSection().getSectionsManager().setIsDragging(false);
		updateParent(e);
		panelPoint = null;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2) && sectionPanel.contains(e.getPoint())) {
			// double click -> show configuration dialog
			sectionPanel.getSection().getSectionsManager().showSectionColumnDialog(sectionPanel.getSection(), sectionPanel.getLocationOnScreen());
		}
	}

}
