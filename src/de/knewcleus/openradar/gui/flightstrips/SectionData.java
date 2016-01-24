package de.knewcleus.openradar.gui.flightstrips;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.flightstrips.order.AbstractOrder;

/* SectionData is a non-visual class
 * which provides information about a section of the FlightStripBay
 * like section title and columns data
 */
public class SectionData implements DomAttributes {

	private String title = "";
	private boolean showHeader = true;
	private boolean showColumnTitles = false;
	
	private final SectionPanel panel;

	private ArrayList<ColumnData> columns = new ArrayList<ColumnData>();

	
	// --- constructors ---

	public SectionData(String title) {
		this.title = title;
		panel = new SectionPanel(this);
		showHeader = title.length() > 0;
	}

	public SectionData(String title, String... columnTitles) {
		this.title = title;
		panel = new SectionPanel(this);
		for (String columnTitle : columnTitles) {
			addColumn(columnTitle);
			showColumnTitles |= columnTitle.length() > 0;
		}
		showHeader = showColumnTitles || (title.length() > 0);  
	}
	
	// --- title ---
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		if (!this.title.equals(title)) {
			this.title = title;
			// TODO: broadcast message: contacts section column title changed
		}
	}
	
	// --- showHeader ---
	
	public boolean getShowHeader() {
		return showHeader;
	}
	
	public void setShowHeader(boolean value) {
		if (showHeader != value) {
			showHeader = value;
			panel.recreateContents();
		}
	}
	
	// --- showColumnTitles ---
	
	public boolean getShowColumnTitles() {
		return showColumnTitles;
	}
	
	public void setShowColumnTitles(boolean value) {
		if (showColumnTitles != value) {
			showColumnTitles = value;
			panel.recreateContents();
		}
	}
	
	// --- columns ---
	
	public int getColumnCount() {
		return columns.size();
	}
	
	public ColumnData addColumn(String title) {
		ColumnData result = new ColumnData(title);
		columns.add(result);
		panel.recreateContents();
		return result;
	}

	public ColumnData getColumn(int Index) {
		return columns.get(Index);
	}

	public ArrayList<ColumnData> getColumns() {
		return columns;
	}

	// --- contacts ---

	public void addFlightStrip(FlightStrip flightstrip) {
		panel.addFlightStrip(flightstrip);
	}

	public void removeFlightStrip(FlightStrip flightstrip) {
		panel.removeFlightStrip(flightstrip);
	}

	public void moveFlightStrip(FlightStrip flightstrip, SectionData oldsection) {
		if (oldsection == null) {
			addFlightStrip(flightstrip);
		}
		else if (this.equals(oldsection)) {
			panel.updateColumn(flightstrip);
		}
		else {
			GuiRadarContact contact = flightstrip.getContact();
			boolean s = contact.isSelected();
			oldsection.removeFlightStrip(flightstrip);
			addFlightStrip(flightstrip);
			if (s) contact.getManager().select(contact, true, false);
		}
	}
	
	public void updateColumn(FlightStrip flightstrip) {
		panel.updateColumn(flightstrip);
	}
	
	public ArrayList<FlightStrip> getFlightStrips() {
		return (panel == null) ? new ArrayList<FlightStrip>() : panel.getFlightStrips();
	}

	// --- Panel ---
	
	public SectionPanel getPanel() {
		return panel;
	}
	
	// --- sort order ---
	
	public AbstractOrder<?> getOrder() {
		return panel.getOrder();
	}

	public synchronized void setOrder(AbstractOrder<?> order) {
		panel.setOrder(order);
	}

	public void reorderFlightStrips() {
		panel.reorderFlightStrips();
	}

	// --- DomAttributes ---
	
	@Override
	public void putAttributes(Element element) {
		element.setAttribute("title", title);
		element.setAttribute("showHeader", String.valueOf(showHeader));
		element.setAttribute("showColumnTitles", String.valueOf(showColumnTitles));
	}
	
}
