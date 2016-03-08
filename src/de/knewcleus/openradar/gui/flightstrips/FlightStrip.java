package de.knewcleus.openradar.gui.flightstrips;

import java.awt.AWTEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.contacts.FgComSupportSymbol;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.ListDialog.ListDialogListener;

/* FlightStrip is a visual component 
 * which displays the current state of the flight
 * and the assigned parameters
 * It is organized in sections in the FlightStripBay
 */
public class FlightStrip extends JPanel implements FocusListener {

	private static final long serialVersionUID = 1334878569053630391L;

	private final String regexpSquawk   = "[0-7]{0,4}";
	private final String regexpSpeed    = "[0-9]{0,3}";
	private final String regexpHeading  = "360|3[0-5][0-9]|[0-2]{0,1}[0-9]{0,2}";
	private final String regexpAltitude = "[Ff]|[Ff][Ll]|[Ff][Ll][0-9]{0,3}|[0-9]{0,5}";

	
	private final GuiRadarContact contact;
	private boolean pending = false; // apply no rules because it's being dragged with the mouse
	
	private SectionData section = null;
	private int column = 0;

	private boolean atclayout;
	
	private FgComSupportSymbol cRadio;
	private Label              cCallsign;
	private Label              cAircraft;
	private Label              cFrequency;
	private EditLabel          cSquawk;
	private AtcComponent       cATC;
	private Label              cDistance;
	private EditLabel          cAltitude;
	private EditLabel          cHeading;
	private EditLabel          cSpeed;
	private Label              cVerticalSpeed;
	private Label              cFlightrules;
	private Label              cDepartureAirport;
	private Label              cRunway;
	private Label              cSidStar;
	private Label              cCruiseAlt;
	private EnrouteLabel	   cEnRoute;
	private Label              cDestinationAirport;
	private Label              cDestinationHeading;
	private EditArea           cComments;
	private EditArea           cNotes;
	
	private JPanel row3;    
	private JPanel row4;    
	private JPanel row5;    
	
	private JCheckBox showRows; 
	
	private boolean rowsVisible = false;
	private boolean row3Visible = true;
	
	public FlightStrip(GuiRadarContact contact) {
		this.contact = contact;
		contact.setFlightstrip(this);
		// design and layout
		setOpaque(true);
		setBorder(BorderFactory.createLineBorder(Palette.BLACK));
		setLayout(new GridBagLayout());
		// contents components
		atclayout = contact.isAtc();
		if (atclayout) createAtcLayout(this);
		else createFlightLayout(this);
		// contents data
		updateContents();
	}
	
	protected void createAtcLayout(JComponent parent) {
		// --- create used components ---
		cRadio     = new FgComSupportSymbol();
		cCallsign  = new Label("----");
		cAircraft  = new Label("---");
		cFrequency = new Label("---.---");
		cFrequency.setHorizontalAlignment(JLabel.RIGHT);
		// --- constraints ---
		GridBagConstraints rowConstraints = new GridBagConstraints();
		rowConstraints.gridx = 0;
		rowConstraints.gridy = 0;
		rowConstraints.fill = GridBagConstraints.HORIZONTAL;
		rowConstraints.weightx = 0.0;
		rowConstraints.insets = new Insets(2, 3, 2, 3);
		// --- first row ---
		parent.add(cCallsign, rowConstraints);
		rowConstraints.gridx++;
		parent.add(cRadio, rowConstraints);
		rowConstraints.gridx++;
		rowConstraints.weightx = 1.0;
		parent.add(cAircraft, rowConstraints);
		rowConstraints.weightx = 0.0;
		rowConstraints.gridx++;
		parent.add(cFrequency, rowConstraints);
		rowConstraints.gridx++;
	}
	
	protected void createFlightLayout(JComponent parent) {
		JPanel row;
		Label label;
		// --- create used components ---
		cRadio              = new FgComSupportSymbol();
		// can't click if tooltips set // cRadio.setToolTipText("visible if radio communication is available");
		cCallsign           = new Label("----");
		// can't click if tooltips set // cCallsign.setToolTipText("<html>left click to select; middle click for flightplan; right click for menu<br>double click to show; shift + double click to center<html>");
		cAircraft           = new Label("---");
		cSquawk             = new EditLabel(new SquawkEdit(), " ", "", "");
		cATC                = new AtcComponent(" -> ", "", "");
		cDistance           = new Label("--");
		cAltitude           = new EditLabel(new AltitudeEdit(), " ", "", "");
		cHeading            = new EditLabel(new HeadingEdit(), " ", "", "°");
		cSpeed              = new EditLabel(new SpeedEdit(), " ", "G", "");
		cVerticalSpeed		= new Label(null);
		cFlightrules        = new Label(null);
		cDepartureAirport   = new Label("----");
		cRunway             = new Label("RW--");
		cSidStar            = new Label(null);
		cCruiseAlt			= new Label(null);
		cEnRoute 			= new EnrouteLabel();
		cDestinationAirport = new Label("----");
		cDestinationHeading = new Label("n/a");
		cComments           = new EditArea();
		cNotes              = new EditArea();
		showRows			= new JCheckBox();
		showRows.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
				// select contact/flightstrip if edit has got the focus
				if (!contact.isSelected()) {
					contact.getManager().select(contact, true, false);
					((JCheckBox)e.getSource()).requestFocus();
				}
		    	rowsVisible = ((JCheckBox) e.getSource()).isSelected();
		    }
		});
		
		// --- constraints ---
		GridBagConstraints parentConstraints = new GridBagConstraints();
		parentConstraints.gridx = 0;
		parentConstraints.gridy = 0;
		parentConstraints.fill = GridBagConstraints.HORIZONTAL;
		parentConstraints.weightx = 1.0;
		GridBagConstraints rowConstraints = new GridBagConstraints();
		rowConstraints.gridx = 0;
		rowConstraints.gridy = 0;
		rowConstraints.fill = GridBagConstraints.HORIZONTAL;
		rowConstraints.weightx = 0.0;
		rowConstraints.insets = new Insets(0, 3, 0, 3);
		// --- row 1 ---
		row = new JPanel();
		row.setOpaque(false);
		row.setLayout(new GridBagLayout());
		parent.add(row, parentConstraints);
		parentConstraints.gridy++;
		row.add(cCallsign, rowConstraints);
		rowConstraints.gridx++;
		row.add(cRadio, rowConstraints);
		rowConstraints.gridx++;
		rowConstraints.weightx = 1.0;
		row.add(cAircraft, rowConstraints);
		rowConstraints.weightx = 0.0;
		rowConstraints.gridx++;
		rowConstraints.fill = GridBagConstraints.NONE;
		row.add(cSquawk, rowConstraints);
		rowConstraints.fill = GridBagConstraints.HORIZONTAL;
		rowConstraints.gridx++;
		row.add(cATC, rowConstraints);
		rowConstraints.gridx++;
		row.add(showRows, rowConstraints);
		rowConstraints.gridx = 0;
		// --- row 2 ---
		row = new JPanel();
		row.setOpaque(false);
		row.setLayout(new GridBagLayout());
		parent.add(row, parentConstraints);
		parentConstraints.gridy++;
		row.add(cDistance, rowConstraints);
		rowConstraints.gridx++;
		row.add(cAltitude, rowConstraints);
		rowConstraints.gridx++;
		row.add(cVerticalSpeed, rowConstraints);
		rowConstraints.gridx++;
		row.add(cHeading, rowConstraints);
		rowConstraints.gridx++;
		row.add(cSpeed, rowConstraints);
		rowConstraints.gridx++;
		rowConstraints.weightx = 1.0;
		row.add(new Label(null), rowConstraints);
		rowConstraints.weightx = 0.0;
		rowConstraints.gridx++;
		row.add(cFlightrules, rowConstraints);
		rowConstraints.gridx = 0;
		// --- row 3 ---
		row3 = new JPanel();
		row3.setOpaque(false);
		row3.setLayout(new GridBagLayout());
		parent.add(row3, parentConstraints);
		parentConstraints.gridy++;
		row3.add(cDepartureAirport, rowConstraints);
		rowConstraints.gridx++;
		row3.add(cRunway, rowConstraints);
		rowConstraints.gridx++;
		rowConstraints.weightx = 1.0;
		row3.add(cSidStar, rowConstraints);
		rowConstraints.weightx = 0.0;
		rowConstraints.gridx++;
		row3.add(cCruiseAlt, rowConstraints);
		rowConstraints.gridx++;
		rowConstraints.weightx = 1.0;
		row3.add(cEnRoute, rowConstraints);
		rowConstraints.weightx = 0.0;
		rowConstraints.gridx++;
		row3.add(cDestinationAirport, rowConstraints);
		rowConstraints.gridx++;
		row3.add(cDestinationHeading, rowConstraints);
		rowConstraints.gridx = 0;
		// --- row 4 ---
		row4 = new JPanel();
		row4.setOpaque(false);
		row4.setLayout(new GridBagLayout());
		parent.add(row4, parentConstraints);
		parentConstraints.gridy++;
		label = new Label(null);
		label.setText("public:");
		row4.add(label, rowConstraints);
		rowConstraints.gridx++;
		rowConstraints.weightx = 1.0;
		row4.add(cComments, rowConstraints);
		rowConstraints.weightx = 0.0;
		rowConstraints.gridx = 0;
		// --- row 5 ---
		row5 = new JPanel();
		row5.setOpaque(false);
		row5.setLayout(new GridBagLayout());
		parent.add(row5, parentConstraints);
		parentConstraints.gridy++;
		label = new Label(null);
		label.setText("private:");
		row5.add(label, rowConstraints);
		rowConstraints.gridx++;
		rowConstraints.weightx = 1.0;
		row5.add(cNotes, rowConstraints);
		rowConstraints.weightx = 0.0;
		rowConstraints.gridx = 0;
	}
	
	public void updateContents() {
		setBackground(contact.getFlightPlan().isOfferedToMe() ? Palette.STRIP_BACKGROUND_OFFERED  :     
	                 (contact.isNew()                         ? Palette.STRIP_BACKGROUND_NEW      :     
			         (contact.isSelected()                    ? Palette.STRIP_BACKGROUND_SELECTED : 
			         (contact.isAtc()                         ? Palette.STRIP_BACKGROUND_ATC      : 
			        	                                        Palette.STRIP_BACKGROUND_FLIGHT))));
		// check ATC
		if (atclayout != contact.isAtc()) {
			atclayout = contact.isAtc();
			removeAll();
			if (atclayout) createAtcLayout(this);
			else createFlightLayout(this);
		}
		// common components
		cRadio.setActive(contact.hasFgComSupport());
		cCallsign.setText(contact.getCallSign());
		cCallsign.setFont(contact.isActive() ? Palette.STRIP_FONT_BOLD : Palette.STRIP_FONT);
		// different components
		if (contact.isAtc()) updateContentsATC();
		else updateContentsWithContactData();
	}
	
	protected void updateContentsATC() {
		cAircraft.setText(contact.isActive() ? contact.getModel() : "inactive: " + ((System.currentTimeMillis() - contact.getLastUpdate()) / 1000) + " sec");
		cAircraft.setFont(contact.isActive() ? Palette.STRIP_FONT : Palette.STRIP_FONT_BOLD);
		cFrequency.setText(contact.getFrequency());
	}
	
	protected void updateContentsWithContactData() {
		if (rowsVisible && !contact.isSelected()) {
			showRows.setSelected(false);
			rowsVisible = false;
		}
		// row 1
		cAircraft.setText(contact.isNeglect() ? "neglected" : (contact.isActive() ? contact.getModel() : "inactive: " + ((System.currentTimeMillis() - contact.getLastUpdate()) / 1000) + " sec"));
		cAircraft.setFont(contact.isActive() ? Palette.STRIP_FONT : Palette.STRIP_FONT_BOLD);
		String s = contact.getTranspSquawkDisplay();
		cSquawk.setLabelText(s.isEmpty() ? "----" : s);
		cSquawk.setEditText(contact.getAssignedSquawkDisplay());
		// row 2
		cDistance.setText(contact.getRadarContactDistance() + " NM");
		cAltitude.setLabelText(formatAltitude(AltitudeFeet(contact.getFlightLevel())));
		cVerticalSpeed.setText(String.format("%+01.0f", contact.getVerticalSpeedD()/100));
		cHeading.setLabelText(String.format("%03.0f", contact.getMagnCourseD()));
		cSpeed.setLabelText(String.format("%03.0f", contact.getGroundSpeedD()));
		cDestinationHeading.setText(contact.getFlightPlan().getDirectiontoDestinationAirport(contact.getCenterGeoCoordinates()));
		updateContentsWithFlightPlanData(contact.getFlightPlan());
	}
	
	protected void updateContentsWithFlightPlanData(FlightPlanData fpd) {
		String t;
		// enable/disable edit fields
		boolean edit_enabled = !fpd.isOwnedBySomeoneElse();
		cSquawk.setEnabled(edit_enabled);
		cAltitude.setEnabled(edit_enabled);
		cSpeed.setEnabled(edit_enabled);
		cHeading.setEnabled(edit_enabled);
		cComments.setEnabled(edit_enabled);
		// row 1
		cATC.setData(fpd);
		// row 2
		cAltitude.setEditText(formatAltitude(AltitudeFeet(fpd.getAssignedAltitude())));
		cFlightrules.setText(fpd.getType());
		// row 3
		row3Visible = false;
		t = fpd.getDepartureAirport();
		row3Visible |= !t.isEmpty();
		cDepartureAirport.setText(t);
		t = fpd.getAssignedRunway();
		row3Visible |= !t.isEmpty();
		if (!t.isEmpty()) t = "RW" + t;
		cRunway.setText(t);
		t = fpd.getAssignedRoute();
		row3Visible |= !t.isEmpty();
		cSidStar.setText(t);
		t = fpd.getCruisingAltitude();
		row3Visible |= !t.isEmpty();
		cCruiseAlt.setText(t);
		t = fpd.getRoute();
		row3Visible |= !t.isEmpty();
		cEnRoute.setLongText(t);
		t = fpd.getDestinationAirport();
		row3Visible |= !t.isEmpty();
		cDestinationAirport.setText(t);
		row3.setVisible(row3Visible);
		// row 4
		t = fpd.getRemarks();
		cComments.setText(t);
		row4.setVisible(rowsVisible || !t.isEmpty());
		// row 5
		t = contact.getAtcComment();
		cNotes.setText(t);
		row5.setVisible(rowsVisible || (t != null && !t.isEmpty()));
	}
	
	protected void updateFlightPlanDataFromEdit(AWTEvent event) {
		JTextComponent edit = (JTextComponent)event.getSource(); 
		String value = edit.getText();
		if (edit.equals(cSquawk.edit)) {
			contact.setAssignedSquawk(value.isEmpty() ? null : Integer.parseInt(value));
			if (!value.isEmpty()) ((SquawkEdit)cSquawk.edit).setAutoAtcMessage(value);
		}
		else if (edit.equals(cAltitude.edit)) {
			int feet = AltitudeFeet(value);
			if ((feet >= 0) && (feet < 500)) feet *= 100; // all below 500 is interpreted as flightlevel
			value = formatAltitude(feet);
			contact.getFlightPlan().setAssignedAltitude(value);
			if ((feet >= 0) && !value.isEmpty()) ((AltitudeEdit)cAltitude.edit).setAutoAtcMessage(value, feet);
		}
		else if (edit.equals(cHeading.edit)) {
			if (!value.isEmpty()) ((HeadingEdit)cHeading.edit).setAutoAtcMessage(value);
		}
		else if (edit.equals(cSpeed.edit)) {
			if (!value.isEmpty()) ((SpeedEdit)cSpeed.edit).setAutoAtcMessage(value);
		}
		else if (edit.equals(cComments)) {
			contact.getFlightPlan().setRemarks(value);
		}
		else if (edit.equals(cNotes)) {
			contact.setAtcComment(value);
		}
        contact.getFlightPlan().setReadyForTransmission();
        contact.getManager().transmitFlightplan();
	}
	
	protected int AltitudeFeet(String altitude) {
		if (altitude == null)   return -1;
		if (altitude.isEmpty()) return -1;
		altitude = altitude.trim().toUpperCase();
		int feet; 
		try {
			if (altitude.startsWith("FL")) {
				feet = Integer.parseInt(altitude.substring(2)) * 100;
			}
			else {
				feet = Integer.parseInt(altitude);
			} 
		} catch (NumberFormatException exception) {
			feet = -1;
		}
		return feet;
	}
	
	protected String formatAltitude(int feet) {
		String altitude = "";
		if (feet >= 0) {
			int fl = feet / 100;
			int tfl;
			try {
				tfl = contact.getAirportData().getTransitionFL(null);
			} catch (NullPointerException exeption) {
				tfl = (contact.getAirportData().getTransitionAlt() + contact.getAirportData().getTransitionLayerWidth()) / 100;
			}
			altitude = (fl >= tfl) ? String.format("FL%03d", fl) : "" + String.format("%04d", feet); 
		}
		return altitude;
	}
	
	public GuiRadarContact getContact() {
		return contact;
	}
	
	public void moveToPosition(SectionData section, int column) {
		SectionData oldsection = this.section;
		// execute exit actions
		if (this.section != null) this.section.getColumn(this.column).executeExitActions(this);
		// move visual
		this.section = section;
		this.column = column;
		if (section == null) {
			if (oldsection != null) oldsection.removeFlightStrip(this);
		}
		else section.moveFlightStrip(this, oldsection);
		// execute enter actions
		// use this because column could be changed in section.moveFlightStrip (see call history of setColumn)
		if (this.section != null) this.section.getColumn(this.column).executeEnterActions(this);
	}
	
	public SectionData getSection() {
		return section;
	}
	
	public int getColumn() {
		return column;
	}
	
	public void setColumn(int column) {
		this.column = column;
	}
	
	public void remove() {
		moveToPosition(null, 0);
	}
	
	public void moveLeftRight(int columns) {
		moveToPosition(section, column + columns);
	}
	
	// --- FocusListener ---
	
	@Override
	public void focusGained(FocusEvent e) {
		// select contact/flightstrip if edit has got the focus
		if (!contact.isSelected()) {
			contact.getManager().select(contact, true, false);
			((JComponent)e.getSource()).requestFocus();
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		((UserChange)e.getSource()).resetUserChange();
	}

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	// =====================================================

	protected interface UserChange {
		public abstract void resetUserChange();
	}
	
	// =====================================================
	
	protected class Label extends JLabel {

		private static final long serialVersionUID = -2089161153316588661L;
		
		private final String emptyText;
		
		public Label(String emptyText) {
			this.emptyText = emptyText;
			setFont(Palette.STRIP_FONT);
			setForeground(Palette.STRIP_DEFAULT);
		}
		
		// --- JLabel ---
		
		@Override
		public void setText(String text) {
			if (text.isEmpty()) {
				if (emptyText != null) text = emptyText;
			}
			super.setText(text);
		}

	}
	
	// =====================================================
	
	protected class EnrouteLabel extends Label implements MouseListener, ListDialogListener {
		
		private static final long serialVersionUID = 571939353923493300L;

		protected String longtext = "";
		protected String last = "";
		protected int selected = -1;
		ArrayList<String> parts = new ArrayList<String>();
		protected ArrayList<String> segments= new ArrayList<String>();
		protected ListDialog dialog = null;
		
		public EnrouteLabel() {
			super("");
			addMouseListener(this);
		}
		
		public void setLongText(String text) {
			if (!this.longtext.equals(text)) {
				//System.out.println("setLongText" + System.currentTimeMillis());
				this.longtext = text;
				parts.clear();
				parts.addAll(Arrays.asList(text.split("\\W+")));
				if ((this.last.length() <= 0) || (!parts.contains(this.last))) {
					selected = 0;
					this.last = parts.get(selected);
				}
				else selected = parts.indexOf(this.last);
				segments.clear();
				String last = null;
				String way = null;
				String next = null;
				String shorttext;
				for (String part : parts) {
					last = way;
					way = next;
					next = part;
					if (last != null) {
						shorttext = last + " " + way + " " + next;
						segments.add(shorttext);
						if (this.last == last) {
							setText(shorttext);
						}
					}
				}
				if (way != null) {
					shorttext = way + " " + next;
					segments.add(shorttext);
					if (this.last == way) setText(shorttext);
				}
				if (next != null) {
					shorttext = next;
					segments.add(shorttext);
					if (this.last == next) setText(shorttext);
				}
			}
		}

		// --- MouseListener ---
		
		@Override
		public void mouseClicked(MouseEvent e) {
			contact.getManager().select(contact, true, false);
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (dialog == null)	dialog = new ListDialog();
				dialog.setListItems(segments, selected, this);
				dialog.setLocation(getLocationOnScreen());
				dialog.setVisible(true);
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

		// --- ListDialogListener ---
		
		@Override
		public void valueSelected(int index) {
			selected = index;
			last = parts.get(selected);
			setText(segments.get(selected));
		}

	}
	
	// =====================================================
	
	protected class Edit extends JTextField implements DocumentListener, ActionListener, UserChange {

		private static final long serialVersionUID = -2089161153316588661L;
		
		private boolean userChange = false;
		
		public Edit() {
			super();
			setFont(Palette.STRIP_FONT);
			setForeground(Palette.STRIP_EDIT_TEXT);
			setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
			getDocument().addDocumentListener(this);
			addActionListener(this);
			addFocusListener(FlightStrip.this);
		}

		@Override
		public void setText(String text) {
			if (!userChange) {
				if (text == null) text = "";
				if (!text.equals(getText())) {
					super.setText(text);
				}
			}
		}

		// --- DocumentListener ---
		
		@Override
		public void changedUpdate(DocumentEvent e) {
			userChange = true;
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			userChange = true;
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			userChange = true;
		}

		// --- ActionListener ---
		
		@Override
		public void actionPerformed(ActionEvent e) {
			userChange = false;
			updateFlightPlanDataFromEdit(e);
		}

		// --- UserChange ---
		
		@Override
		public void resetUserChange() {
			userChange = false;
		}
		
	}
	
	// =====================================================
	
	protected class RegExpEdit extends Edit {

		private static final long serialVersionUID = -2687496532378115367L;

		private final String regexp;
		
		public RegExpEdit(int width, String regexp) {
			this.regexp = regexp;
			setPreferredSize(new Dimension(width, getMinimumSize().height));
			((AbstractDocument) getDocument()).setDocumentFilter(new RegExpEditDocumentFilter());
		}

		// =====================================================

		protected class RegExpEditDocumentFilter extends DocumentFilter {
			
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
				string = string.toUpperCase();
				String text = getText();
				text = text.substring(0, offset) + string + text.substring(offset);
				if (text.matches(regexp)) {
					fb.insertString(offset, string, attr);
				}
			}

			public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
				string = string.toUpperCase();
				String text = getText();
				text = text.substring(0, offset) + string + text.substring(offset + length);
				if (text.matches(regexp)) {
					fb.replace(offset, length, string, attrs);
				}
			}

			public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
				String text = getText();
				text = text.substring(0, offset) + text.substring(offset + length);
				if (text.matches(regexp)) {
					fb.remove(offset, length);
				}
			}
			
		}
		
	}
	
	// =====================================================
	
	protected class EditArea extends JTextArea implements DocumentListener, KeyListener, UserChange {

		private static final long serialVersionUID = -2089161153316588661L;
		
		private boolean userChange = false;
		
		public EditArea() {
			super();
			setFont(Palette.STRIP_FONT);
			setForeground(Palette.STRIP_EDIT_TEXT);
			setBorder(BorderFactory.createLineBorder(Palette.BLACK));
			setLineWrap(true);
			setWrapStyleWord(true);
			getDocument().addDocumentListener(this);
			addKeyListener(this);
			addFocusListener(FlightStrip.this);
		}

		@Override
		public void setText(String text) {
			if (!userChange) {
				if (text == null) text = "";
				if (!text.equals(getText())) {
					super.setText(text);
				}
			}
		}

		// --- DocumentListener ---
		
		@Override
		public void changedUpdate(DocumentEvent e) {
			userChange = true;
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			userChange = true;
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			userChange = true;
		}

		// --- KeyListener ---
		
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				userChange = false;
				updateFlightPlanDataFromEdit(e);
				e.consume();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		// --- UserChange ---
		
		@Override
		public void resetUserChange() {
			userChange = false;
		}

	}
	
	// =====================================================
	
	protected class SquawkEdit extends RegExpEdit {

		private static final long serialVersionUID = -2687496532378115367L;

		public SquawkEdit() {
			super(34, regexpSquawk);
			setHorizontalAlignment(JTextField.RIGHT);
		}

		public void setAutoAtcMessage(String newValue) {
            contact.getManager().getMaster().getMpChatManager().setAtcMessage(contact, "squawk", newValue);
		}
		
	}
	
	// =====================================================
	
	protected class AltitudeEdit extends RegExpEdit {

		private static final long serialVersionUID = -2687496532378115367L;

		public AltitudeEdit() {
			super(40, regexpAltitude);
			setHorizontalAlignment(JTextField.RIGHT);
		}
		
		public void setAutoAtcMessage(String newValue, int newAltitude) {
            contact.getManager().getMaster().getMpChatManager().setAtcMessage(contact, newAltitude < contact.getAltitude() ? "descend" : "climb", newValue);
		}
		
	}
	
	// =====================================================
	
	protected class HeadingEdit extends RegExpEdit {

		private static final long serialVersionUID = -2687496532378115367L;

		public HeadingEdit() {
			super(28, regexpHeading);
			setHorizontalAlignment(JTextField.RIGHT);
		}

		public void setAutoAtcMessage(String newValue) {
			double relativeHeading = Integer.parseInt(newValue) - contact.getHeadingD();
			if (relativeHeading < -180) relativeHeading += 360;
			if (relativeHeading > 180) relativeHeading -= 360;
            contact.getManager().getMaster().getMpChatManager().setAtcMessage(contact, relativeHeading < 0 ? "left" : "right", newValue);
		}
		
	}
	
	// =====================================================
	
	protected class SpeedEdit extends RegExpEdit {

		private static final long serialVersionUID = -2687496532378115367L;

		public SpeedEdit() {
			super(28, regexpSpeed);
			setHorizontalAlignment(JTextField.RIGHT);
		}

		public void setAutoAtcMessage(String newValue) {
            contact.getManager().getMaster().getMpChatManager().setAtcMessage(contact, Integer.parseInt(newValue) < contact.getGroundSpeedD() ? "reduce" : "increase", newValue);
		}
		
	}
	
	// =====================================================
	
	protected class EditLabel extends JComponent {
		
		private static final long serialVersionUID = 9077507081990182119L;
		
		private final JLabel editPrefix = new FlightStrip.Label(null);
		private final Edit   edit;
		private final JLabel editSuffix = new FlightStrip.Label(null);
		private final JLabel separator = new FlightStrip.Label(null);
		private final JLabel labelPrefix = new FlightStrip.Label(null);
		private final JLabel label = new FlightStrip.Label(null);
		private final JLabel labelSuffix = new FlightStrip.Label(null);
		
		public EditLabel(Edit edit, String separator, String prefix, String suffix) {
			// layout
			setLayout(new GridBagLayout());
			// --- constraints ---
			GridBagConstraints gridConstraints = new GridBagConstraints();
			gridConstraints.gridx = 0;
			gridConstraints.gridy = 0;
			gridConstraints.weightx = 0.0;
			// --- add components ---
			// editPrefix
			if (prefix.length() > 0) {
				editPrefix.setText(prefix);
				add(editPrefix, gridConstraints);
				gridConstraints.gridx++;
			}
			// edit
			this.edit = edit;
			add(edit, gridConstraints);
			gridConstraints.gridx++;
			// editSuffix
			if (suffix.length() > 0) {
				editSuffix.setText(suffix);
				add(editSuffix, gridConstraints);
				gridConstraints.gridx++;
			}
			// separator
			if (separator.length() > 0) {
				this.separator.setText(separator);
				add(this.separator, gridConstraints);
				gridConstraints.gridx++;
			}
			// labelPrefix
			if (prefix.length() > 0) {
				labelPrefix.setText(prefix);
				add(labelPrefix, gridConstraints);
				gridConstraints.gridx++;
			}
			add(label, gridConstraints);
			gridConstraints.gridx++;
			// labelSuffix
			if (suffix.length() > 0) {
				labelSuffix.setText(suffix);
				add(labelSuffix, gridConstraints);
				gridConstraints.gridx++;
			}
		}
		
		public void setLabelText(String label) {
			this.label.setText(label);
			showLabel();
		}
		
		public void setEditText(String edit) {
			this.edit.setText(edit);
			showLabel();
		}
		
		public String getEditText() {
			return edit.getText();
		}
		
		@Override
		public void setEnabled(boolean enabled) {
			editPrefix.setEnabled(enabled);
			edit.setEnabled(enabled);
			editSuffix.setEnabled(enabled);
			showLabel();
		}
		
		protected void showLabel() {
			if (edit.isEnabled()) {
				boolean showLabel = !label.getText().equals(getEditText());
				//System.out.printf("edit='%s' label='%s' showLabel=%s", args)
				separator.setVisible(showLabel && edit.isVisible());
				labelPrefix.setVisible(showLabel);
				label.setVisible(showLabel);
				labelSuffix.setVisible(showLabel);
			}
		}
		
	}
	
	// =====================================================
	
	protected class AtcComponent extends Label implements MouseListener {

		private static final long serialVersionUID = 2878942603784012607L;
		
		protected final String separator;
		protected final String prefix;
		protected final String suffix;
		
		protected final Border label = BorderFactory.createEmptyBorder();  
		protected final Border button = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0), 
																	       BorderFactory.createLineBorder(Palette.STRIP_DEFAULT, 1, true));  

		public AtcComponent(String separator, String prefix, String suffix) {
			super("-------");
			this.separator = separator;
			this.prefix	   = prefix;
			this.suffix    = suffix;
			addMouseListener(this);
		}
		
		public void setData(FlightPlanData fpd) {
			if (fpd.isUncontrolled()) {
				setText("CONTROL");
				setHorizontalAlignment(JLabel.CENTER);
				setBorder(button);
			}
			else if ((fpd.getHandover() == null) || fpd.getHandover().isEmpty()) {
				setText(prefix + fpd.getOwner() + suffix);
				setHorizontalAlignment(JLabel.RIGHT);
				setBorder(label);
			}
			else {
				setText(prefix + fpd.getOwner() + suffix + separator + prefix + fpd.getHandover() + suffix);
				setHorizontalAlignment(JLabel.RIGHT);
				setBorder(label);
			}
		}

		// --- MouseListener ---
		
		@Override
		public void mouseClicked(MouseEvent e) {
			contact.getManager().select(contact, true, false);
			FlightPlanData fpd = contact.getFlightPlan();
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (fpd.isUncontrolled() || fpd.isOfferedToMe()) {
					contact.getManager().takeUnderControl(contact);
				}
				else if (fpd.isOwnedByMe()) {
					contact.getManager().releaseFromControl(contact);
				}
			}
			if (e.getButton() == MouseEvent.BUTTON3) {
				if (fpd.isOwnedByMe()) {
					contact.getManager().showHandoverDialog(e);
				}
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
