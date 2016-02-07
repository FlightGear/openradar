package de.knewcleus.openradar.gui.flightstrips;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.contacts.RadarContactController;
import de.knewcleus.openradar.gui.flightstrips.order.AbstractOrder;

/* FlightStripsPanel is a visual component
 * which is part of a section and organizes FlightStrips in FlightStripRows.
 * A FlightStripRow can be moved up or down.
 * A FlightStrip can be moved left and right within a FlightStripRow.
 */
public class FlightStripsPanel extends JPanel {

	private static final long serialVersionUID = 6896299435571655544L;
	
	private final SectionData section;
	private final GridBagLayout layout = new GridBagLayout();
	private final ArrayList<FlightStripRow> rows = new ArrayList<FlightStripRow>(); // manage order
	
	public FlightStripsPanel(SectionData section) {
		this.section = section;
		// design and layout
		setOpaque(false);
		setLayout(layout);
		// add rows for existing flight strips
		ArrayList<FlightStrip> flightstrips = section.getFlightStrips();
		for (FlightStrip flightstrip : flightstrips) {
			addFlightStrip(flightstrip);
		}
	}
	
	public synchronized void addFlightStrip(FlightStrip flightstrip) {
		//- System.out.println("addFlightStrip --- start");
		synchronized(getTreeLock()) { // should be used around getComponentCount
			// constraints
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = rows.size();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			// add row for flight strip
			FlightStripRow fsr = new FlightStripRow(flightstrip);
			rows.add(fsr);
			add(fsr, gridBagConstraints);
		}
		reorderFlightStrips();
		//- System.out.println("addFlightStrip --- end");
	}
	
	public synchronized void removeFlightStrip(FlightStrip flightstrip) {
		//- System.out.println("removeFlightStrip --- start");
		//- System.out.printf("for %s from section %s", flightstrip.getContact().getCallSign(), section.getTitle());
		synchronized(getTreeLock()) { // should be used around getComponentCount
			FlightStripRow fsr = (FlightStripRow)flightstrip.getParent();
			rows.remove(fsr);
			remove(fsr);
		}
		reorderFlightStrips();
		//- System.out.println("removeFlightStrip --- end");
	}
	
	public synchronized void moveFlightStrip(FlightStrip flightstrip) {
		//- System.out.println("moveFlightStrip --- start");
		synchronized(getTreeLock()) { // should be used around getComponentCount
			// constraints
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = getComponentCount();
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			// get row with flight strip
			FlightStripRow fsr = (FlightStripRow) flightstrip.getParent();
			if (!rows.contains(fsr)) rows.add(fsr);
			layout.setConstraints(fsr, gridBagConstraints);
			setComponentZOrder(fsr, 0); // this is not really working correctly when moving from a different component
		}
		updateColumn(flightstrip);
		reorderFlightStrips();
		//- System.out.println("moveFlightStrip --- end");
	}
	
	public void updateColumn(FlightStrip flightstrip) {
		((FlightStripRow) flightstrip.getParent()).setColumn();
	}
	
	public synchronized void reorderFlightStrips() {
		//- System.out.println("reorderFlightStrips --- start");
		section.getPanel().checkVisible();
		synchronized(getTreeLock()) { // should be used around getComponentCount
			// user defined sort order
			AbstractOrder<?> order = section.getOrder();
			if (order != null) order.sort(rows);
			// position rows (gridy) according to sort order
			for (int i = 0; i < rows.size(); i++) {
				FlightStripRow fsr = rows.get(i);
				GridBagConstraints gridBagConstraints = layout.getConstraints(fsr);
				if (gridBagConstraints.gridy != i) {
					gridBagConstraints.gridy = i;
					layout.setConstraints(fsr, gridBagConstraints);
				}
			}
			revalidate();
		}
		//- System.out.println("reorderFlightStrips --- end");
	}
	
	public synchronized void moveRowToIndex(FlightStripRow row, int target_index) {
		//- System.out.println("moveRowToIndex --- start");
		int source_index = getRowIndex(row);
		if (target_index < 0) target_index = 0;
		if (target_index >= rows.size()) target_index = rows.size() - 1;
		if (source_index != target_index) {
			rows.remove(row);
			rows.add(target_index, row);
			reorderFlightStrips();
		}
		//- System.out.println("moveRowToIndex --- end");
	}
	
	public int getRowCount() {
		return rows.size();
	}
	
	public int getRowIndex(FlightStripRow row) {
		return rows.indexOf(row);
	}
	
	public ArrayList<FlightStrip> getFlightStrips() {
		ArrayList<FlightStrip> flightstrips = new ArrayList<FlightStrip>();
		for (FlightStripRow row : rows) {
			flightstrips.add(row.flightstrip);
		}
		return flightstrips;
	}

	// =======================================================================
	
	public class FlightStripRow extends JPanel implements MouseListener {
		
		private static final long serialVersionUID = -7531934536290497480L;

		
		private final FlightStrip flightstrip;
		private final GridBagLayout layout = new GridBagLayout();
		private final GridBagConstraints flightstripConstraints = new GridBagConstraints();
		
		public FlightStripRow(FlightStrip flightstrip) {
			this.flightstrip = flightstrip;
			// design and layout
			setLayout(layout);
			setOpaque(false);
			// constraints
			flightstripConstraints.gridx = 0;
			flightstripConstraints.gridy = 0;
			flightstripConstraints.fill = GridBagConstraints.HORIZONTAL;
			flightstripConstraints.weightx = 1.0;
			createInsets();
			add(flightstrip, flightstripConstraints);
			paintImmediately(getVisibleRect());
			// select, click left or right
			addMouseListener(this);
			// drag-move
			FlightStripRowMouseAdapter rma = new FlightStripRowMouseAdapter(this);
			addMouseMotionListener(rma);
			addMouseListener(rma);
		}
		
		public void setColumn() {
			createInsets();
			layout.setConstraints(flightstrip, flightstripConstraints);
			//-doLayout();
			//-paintImmediately(getVisibleRect());
			revalidate();
		}
		
		public FlightStrip getFlightStrip() {
			return flightstrip;
		}
		
		protected void createInsets() {
			// column position
			int columns = section.getColumnCount();
			int column = flightstrip.getColumn();
			if (column < 0) column = 0;
			if (column >= columns) column = columns - 1;
			flightstripConstraints.insets.left = Palette.STRIP_COLUMN_SPACE * column;
			flightstripConstraints.insets.right = Palette.STRIP_COLUMN_SPACE * (columns - 1 - column);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// always select contact / flightstrip
			GuiRadarContact contact = flightstrip.getContact();
			RadarContactController controller = contact.getManager();
			controller.select(contact, true, false);
			// calculate click position
			int x = e.getX();
			int fs_x = flightstrip.getX();
			int clickposition = (x < fs_x) ? -1 : ((x < fs_x + flightstrip.getWidth()) ? 0 : 1);
			if (clickposition == 0) {
				// click onto strip
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					// double click -> move radar map to contact (ShiftDown = don't zoom)
					controller.contactDoubleClicked(contact, !e.isShiftDown());
				}
				else if((e.getButton() == MouseEvent.BUTTON2 && e.getClickCount() == 1)
                        || (e.isAltDown() && e.getButton() == MouseEvent.BUTTON3) ) {
                    // show contact settings dialog
					controller.selectNShowFlightplanDialog(contact, e);
                } 
				else if (e.getButton() == MouseEvent.BUTTON3) {
					// show ATC message dialog
                	controller.selectNShowAtcMsgDialog(contact, e);
                }			
			}
			else {
				// click besides the flightstrip moves the flightstrips by clickCount columns to the left or right
				flightstrip.moveLeftRight(clickposition * e.getClickCount());
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

	}

}
