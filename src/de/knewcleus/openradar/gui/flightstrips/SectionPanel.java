package de.knewcleus.openradar.gui.flightstrips;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import de.knewcleus.openradar.gui.Palette;

/* SectionPanel is a visual component
 * with 3 parts: HeaderPanel, FlightStripsPanel, FooterPanel
 */
public class SectionPanel extends JPanel {

	private static final long serialVersionUID = 5927753538631135433L;

	private final SectionData section;
	private FlightStripsPanel flightstripspanel;

	public SectionPanel(SectionData section) {
		this.section = section;
		// design and layout
		setBackground(Palette.SECTION_BACKGROUND);
		setLayout(new GridBagLayout());
		// drag-move
		SectionMouseAdapter sma = new SectionMouseAdapter(this);
		addMouseMotionListener(sma);
		addMouseListener(sma);
		// contents
		recreateContents();
	}
	
	public void recreateContents() {
		// remove existing components
		removeAll();
		// add header panel
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		if (section.getShowHeader()) {
			add(new HeaderPanel(), gridBagConstraints);
			gridBagConstraints.gridy++;
		}
		// flight strips panel for this section
		flightstripspanel = new FlightStripsPanel(section);
		add(flightstripspanel, gridBagConstraints);
		gridBagConstraints.gridy++;
		// add footer panel
		if (section.getShowHeader()) {
			add(new FooterPanel(), gridBagConstraints);
			gridBagConstraints.gridy++;
		}
		checkVisible();
		revalidate();
	}
	
	public void checkVisible() {
		setVisible((section.isAutoVisible() ? (flightstripspanel.getRowCount() > 0) : true) || section.getSectionsManager().getIsDragging());
	}
	
	public SectionData getSection() {
		return section;
	}

	public void reorderFlightStrips() {
		flightstripspanel.reorderFlightStrips();
	}
	
	public void addFlightStrip(FlightStrip flightstrip) {
		flightstripspanel.addFlightStrip(flightstrip);
	}

	public void removeFlightStrip(FlightStrip flightstrip) {
		flightstripspanel.removeFlightStrip(flightstrip);
	}
	
	public void moveFlightStrip(FlightStrip flightstrip) {
		flightstripspanel.moveFlightStrip(flightstrip);
	}

	public void updateColumn(FlightStrip flightstrip) {
		flightstripspanel.updateColumn(flightstrip);
	}
	
	public ArrayList<FlightStrip> getFlightStrips() {
		return flightstripspanel.getFlightStrips();
	}

	// =====================================================================
	
	protected class HeaderPanel extends JPanel {

		private static final long serialVersionUID = -8544222267598893255L;

		public HeaderPanel() {
			// design and layout
			setBackground(Palette.SECTION_HEADER);
			setBorder(BorderFactory.createLineBorder(Palette.BLACK));
			setLayout(new GridBagLayout());
			recreatePanel();
		}
		
		public void recreatePanel() {
			removeAll();
			// columns informations
			ArrayList<ColumnData> columns = section.getColumns();
			// add label for section title
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints.weightx = 1.0;
			JLabel title = new JLabel();
			title.setText(section.getTitle());
	        title.setFont(title.getFont().deriveFont(Font.BOLD));
	        title.setHorizontalAlignment(JLabel.CENTER);
	        title.setForeground(Palette.SECTION_TITLE);
	        add(title, gridBagConstraints);
	        gridBagConstraints.gridy++;
	        gridBagConstraints.insets = new Insets(0, 3, 0, 3);
	        // create labels for column titles
	        if (section.getShowColumnTitles()) {
	        	JPanel titles = new JPanel();
		        add(titles, gridBagConstraints);
		        gridBagConstraints.gridy++;
		        titles.setOpaque(false);
		        titles.setLayout(new GridLayout(1, columns.size()));
	        	int ha = columns.size() > 1 ? JLabel.LEFT : JLabel.CENTER;
				for (ColumnData column : columns) {
					title = new JLabel();
					title.setText(column.getTitle());
			        title.setHorizontalAlignment(ha);
			        title.setForeground(Palette.SECTION_TITLE);
			        titles.add(title);
			        ha = JLabel.CENTER;
				}
				if (columns.size() > 1)	title.setHorizontalAlignment(JLabel.RIGHT);
	        }
		}

	}
	
	// =======================================================================
	
	protected class FooterPanel extends JPanel {

		private static final long serialVersionUID = -6982358451947496708L;

		public FooterPanel() {
			// design and layout
			setOpaque(false);
			setPreferredSize(new Dimension(0, 0)); // test 10));
		}
	}

}
