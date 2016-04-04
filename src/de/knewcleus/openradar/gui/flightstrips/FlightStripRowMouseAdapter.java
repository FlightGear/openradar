package de.knewcleus.openradar.gui.flightstrips;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.contacts.RadarContactController;
import de.knewcleus.openradar.gui.flightstrips.FlightStripsPanel.FlightStripRow;

public class FlightStripRowMouseAdapter extends MouseAdapter {

	private final FlightStripRow row;
	private FlightStripsPanel parent = null;
	private GridBagLayout layout = null;
	private Point panelPoint = null;
	private Point targetPoint;
	
	public FlightStripRowMouseAdapter(FlightStripRow row) {
		this.row = row;
		panelPoint = null;
	}
	
	protected FlightStripsPanel getParent() {
		if (parent == null)	parent = (FlightStripsPanel) row.getParent();
		return parent;
	}
	
	protected GridBagLayout getLayout() {
		if (layout == null)	layout = (GridBagLayout) getParent().getLayout();
		return layout;
	}
	
	protected void updateTargetPoint(MouseEvent e) {
		targetPoint = SwingUtilities.convertPoint(row, e.getX(), e.getY(), getParent());
	}
	
	protected void updateParent(MouseEvent e) {
		updateTargetPoint(e);
		synchronized(getParent().getTreeLock()) { // should be used around getComponents
			// search for row under mouse position
	        int targetIndex = -1;
			for (Component component : getParent().getComponents()) {
				if (!component.equals(row)) {
					Rectangle bounds = component.getBounds();
					bounds.grow(5, 0);
					//if (component.contains(SwingUtilities.convertPoint(getParent(), targetPoint, component))) {
					if (bounds.contains(targetPoint)) {
						targetIndex = getParent().getRowIndex((FlightStripRow) component);
						break;
					}
				}
			}
	        if (targetIndex >= 0) {
	        	getParent().moveRowToIndex(row, targetIndex);
	        }
		}
		getParent().doLayout();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (panelPoint == null) {
			panelPoint = e.getPoint();
			getParent().setComponentZOrder(row, 0);
			getParent().doLayout();
			FlightStrip flightstrip = ((FlightStrip) row.getComponent(0));
			flightstrip.setPending(true);
			// always select contact / flightstrip
			GuiRadarContact contact = flightstrip.getContact();
			RadarContactController controller = contact.getManager();
			controller.select(contact, true, false, true);
		}
		updateParent(e);
		row.setBounds(0 /*-targetPoint.x - panelPoint.x*/, targetPoint.y - panelPoint.y, row.getWidth(), row.getHeight());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		updateParent(e);
		((FlightStrip) row.getComponent(0)).setPending(false);
		panelPoint = null;
	}
	
}
