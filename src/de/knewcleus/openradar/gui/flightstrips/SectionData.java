package de.knewcleus.openradar.gui.flightstrips;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.config.SectionsManager;
import de.knewcleus.openradar.gui.flightstrips.order.AbstractOrder;
import de.knewcleus.openradar.gui.flightstrips.order.OrderManager;

/* SectionData is a non-visual class
 * which provides information about a section of the FlightStripBay
 * like section title and columns data
 */
public class SectionData implements IDomElement {

	private final SectionsManager sectionsManager;
	
	private String title = "";
	private boolean autoVisible = false;
	private boolean showHeader = true;
	private boolean showColumnTitles = false;
	private AbstractOrder<?> order = null;
	
	private ArrayList<ColumnData> columns = new ArrayList<ColumnData>();

	private final SectionPanel panel;
	
	// --- constructors ---

	public SectionData(SectionsManager sectionsManager, String title) {
		this.sectionsManager = sectionsManager;
		this.title = title;
		panel = new SectionPanel(this);
		showHeader = title.length() > 0;
	}

	public SectionData(SectionsManager sectionsManager, String title, String... columnTitles) {
		this.sectionsManager = sectionsManager;
		this.title = title;
		panel = new SectionPanel(this);
		for (String columnTitle : columnTitles) {
			addColumn(columnTitle);
			showColumnTitles |= columnTitle.length() > 0;
		}
		showHeader = showColumnTitles || (title.length() > 0);  
	}
	
	public SectionData(SectionsManager sectionsManager, Element element) throws Exception {
		this.sectionsManager = sectionsManager;
		title = element.getAttributeValue("title");
		autoVisible = Boolean.valueOf(element.getAttributeValue("autovisible"));
		showHeader = Boolean.valueOf(element.getAttributeValue("showheader"));
		showColumnTitles = Boolean.valueOf(element.getAttributeValue("showcolumntitles"));
		panel = new SectionPanel(this);
		String columntag = ColumnData.getClassDomElementName();
		for (Element e : element.getChildren()) {
			if (e.getName().equals(columntag)) {
				// column
				columns.add(new ColumnData(e));
			}
			else {
				// order: last order is valid
				AbstractOrder<?> order = OrderManager.createByClassName(e); 
				if (order != null) setOrder(order); 
			}
		}
		panel.recreateContents();
	}

	// --- title ---
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		if (!this.title.equals(title)) {
			this.title = title;
			panel.recreateContents();
		}
	}
	
	// --- autoVisible ---
	
	public boolean isAutoVisible() {
		return autoVisible;
	}

	public void setAutoVisible(boolean autoVisible) {
		this.autoVisible = autoVisible;
		panel.checkVisible();
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

	public void removeLastColumn() {
		int cc = getColumnCount();
		if (cc > 1) {
			columns.remove(cc - 1);
			panel.recreateContents();
		}
	}

	public ColumnData getColumn(int index) {
		return columns.get(index);
	}

	public ArrayList<ColumnData> getColumns() {
		return columns;
	}
	
	public void setColumnTitle(int index, String title) {
		columns.get(index).setTitle(title);
		panel.recreateContents();
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
			oldsection.removeFlightStrip(flightstrip);
			addFlightStrip(flightstrip);
		}
	}
	
	public void updateColumn(FlightStrip flightstrip) {
		panel.updateColumn(flightstrip);
	}
	
	public ArrayList<FlightStrip> getFlightStrips() {
		return (panel == null) ? new ArrayList<FlightStrip>() : panel.getFlightStrips();
	}

	// --- links provider ---
	
	public SectionPanel getPanel() {
		return panel;
	}
	
	public SectionsManager getSectionsManager() {
		return sectionsManager;
	}
	
	// --- sort order ---
	
	public AbstractOrder<?> getOrder() {
		return order;
	}

	public synchronized void setOrder(AbstractOrder<?> order) {
		this.order = order; 
		panel.reorderFlightStrips();
	}

	public void reorderFlightStrips() {
		panel.reorderFlightStrips();
	}

	// --- IDomElement ---
	
	public static String getClassDomElementName() {
		return "section";
	}

	@Override
	public String getDomElementName() {
		return getClassDomElementName();
	}

	@Override
	public Element createDomElement() {
		// section
		Element element = new Element(getDomElementName());
		element.setAttribute("title", title);
		element.setAttribute("autovisible", String.valueOf(autoVisible));
		element.setAttribute("showheader", String.valueOf(showHeader));
		element.setAttribute("showcolumntitles", String.valueOf(showColumnTitles));
		// order
		AbstractOrder<?> order = getOrder();
		if (order != null) element.addContent(getOrder().createDomElement());
		// columns
		for (ColumnData column : getColumns()) element.addContent(column.createDomElement());
		return element;
	}
	
}
