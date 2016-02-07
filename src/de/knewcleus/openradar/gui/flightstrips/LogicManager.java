package de.knewcleus.openradar.gui.flightstrips;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.contacts.RadarContactController;
import de.knewcleus.openradar.gui.flightstrips.actions.ControlAction;
import de.knewcleus.openradar.gui.flightstrips.actions.MoveToAction;
import de.knewcleus.openradar.gui.flightstrips.actions.UncontrolAction;
import de.knewcleus.openradar.gui.flightstrips.order.AltitudeOrder;
import de.knewcleus.openradar.gui.flightstrips.order.CallsignOrder;
import de.knewcleus.openradar.gui.flightstrips.order.ColumnOrder;
import de.knewcleus.openradar.gui.flightstrips.order.DistanceOrder;
import de.knewcleus.openradar.gui.flightstrips.rules.ATCRule;
import de.knewcleus.openradar.gui.flightstrips.rules.AircraftRule;
import de.knewcleus.openradar.gui.flightstrips.rules.AltitudeMaxRule;
import de.knewcleus.openradar.gui.flightstrips.rules.AndRule;
import de.knewcleus.openradar.gui.flightstrips.rules.AtcNoneRule;
import de.knewcleus.openradar.gui.flightstrips.rules.AtcOtherRule;
import de.knewcleus.openradar.gui.flightstrips.rules.AtcSelfRule;
import de.knewcleus.openradar.gui.flightstrips.rules.ColumnRule;
import de.knewcleus.openradar.gui.flightstrips.rules.DistanceMaxRule;
import de.knewcleus.openradar.gui.flightstrips.rules.DistanceMinRule;
import de.knewcleus.openradar.gui.flightstrips.rules.EmergencyRule;
import de.knewcleus.openradar.gui.flightstrips.rules.GroundSpeedMaxRule;
import de.knewcleus.openradar.gui.flightstrips.rules.GroundSpeedMinRule;
import de.knewcleus.openradar.gui.flightstrips.rules.NewRule;
import de.knewcleus.openradar.gui.flightstrips.rules.OrRule;
import de.knewcleus.openradar.gui.flightstrips.rules.RuleAndAction;
import de.knewcleus.openradar.gui.flightstrips.rules.RuleManager;

public class LogicManager implements Runnable {

	public enum FilenameId { DEFAULT, ROLE, AIRPORT, CALLSIGN };
	
	private final String FilePath = "layout";
	
	private final GuiMasterController master;
	private final ArrayList<SectionData> sections = new ArrayList<SectionData>();
    private static Logger log = LogManager.getLogger(LogicManager.class.getName());
	
	// --- constructors ---
	
	public LogicManager(GuiMasterController master) {
		this.master = master;
	}
	
	// --- sections ---
	
	public int getSectionCount() {
		return sections.size();
	}
	
	public SectionData addSection(String title) {
		SectionData result = new SectionData(this, title); 
		sections.add(result);
		return result;
	}

	public SectionData addSection(String title, String... columnTitles) {
		SectionData result = new SectionData(this, title, columnTitles); 
		sections.add(result);
		return result;
	}
	
	public ArrayList<SectionData> getSections() {
		return sections;
	}
	
	public SectionData getPreviousSection (SectionData section) {
		int i = sections.indexOf(section) - 1;
		try {
			return sections.get(i); 
		}
		catch (IndexOutOfBoundsException e) {
			return null; 
		}
	}
	
	public SectionData getNextSection (SectionData section) {
		int i = sections.indexOf(section) + 1;
		try {
			return sections.get(i); 
		}
		catch (IndexOutOfBoundsException e) {
			return null; 
		}
	}
	
	public SectionData getSectionByTitle (String title) {
		for (SectionData section : sections) {
			if (title.equalsIgnoreCase(section.getTitle())) return section;
		}
		return null;
	}

	public void moveSectionToIndex (SectionData section, int target_index) {
		int source_index = sections.indexOf(section);
		if (target_index < 0) target_index = 0;
		if (target_index >= sections.size()) target_index = sections.size() - 1;
		if (source_index != target_index) {
			sections.remove(section);
			sections.add(target_index, section);
		}
	}
	
	public void updateFlightstrips() {
		RadarContactController radarContactController = master.getRadarContactManager();
		// get list of contacts
		radarContactController.clearSectionsListManagerFlag();
		List<GuiRadarContact> contacts = radarContactController.getContactListCopy();
		// collect all flightstrips
		ArrayList<FlightStrip> flightstrips = new ArrayList<FlightStrip>();
		for (SectionData section : sections) {
			flightstrips.addAll(section.getFlightStrips());
		}
		// update existing and remove expired flightstrips
        RuleManager rules = master.getRulesManager();
		for (FlightStrip flightstrip : flightstrips) {
			GuiRadarContact contact = flightstrip.getContact();
			if (contacts.contains(contact)) {
				// contact exists
				flightstrip.updateContents();
				rules.ApplyAppropriateRule(flightstrip);
				// remove contact from list, so that new contacts remain in list
				contacts.remove(contact);
			}
			else {
				// contact is expired
				flightstrip.remove();
			}
		}
		// create new flightstrips
		for (GuiRadarContact contact : contacts) {
			FlightStrip flightstrip = new FlightStrip(contact);
			flightstrip.updateContents();
			rules.ApplyAppropriateRule(flightstrip);
		}
		// order flightstrips within the section
		for (SectionData section : sections) {
			section.reorderFlightStrips();
		}
	}
	
	@Override
	public void run() {
		// runs in AWT-Thread, invoked by RadarContactController.publishData: 
		//      SwingUtilities.invokeLater(master.getSectionsListManager());
		updateFlightstrips();
	}
	
	// XML
	
	public String createFilename(FilenameId id) {
		String callsign = master.getAirportData().getCallSign();
		switch (id) {
		case CALLSIGN:
			return callsign;
		case AIRPORT:
			return callsign.substring(0, 4);
		case ROLE:
			return callsign.substring(4);
		default:
			return "default";
		}
	}

	protected String createFilename(String filename) {
		return FilePath + File.separator + filename + ".xml";
	}
	
	public void SaveLayout(FilenameId id) {
		SaveLayout(createFilename(id));
	}
	
	public void SaveLayout(String filename) {
        try {
        	filename = createFilename (filename);
        	File f = new File (FilePath);
        	f.mkdirs();
    		// --- create document ---
            Document doc = new Document();
            Element root = new Element("flightstripbay");
            doc.addContent(root);
            root.setAttribute("version", "1");
            // sections
            for (SectionData section : sections) {
    			root.addContent(section.createDomElement());
    		}
    		// rules
            for (RuleAndAction ra : master.getRulesManager().getRuleAndActions()) {
            	root.addContent(ra.createDomElement());
            }
            // --- save document ---
            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(doc, new FileWriter(filename));        
        } catch (Exception exception) {
        	log.error( "Problem to create " + filename + " document for flightstripbay: ", exception);
        }
	}
	
	public void LoadLayout() {
		if (!LoadLayout(createFilename(FilenameId.CALLSIGN)))
			if (!LoadLayout(createFilename(FilenameId.AIRPORT)))
				if (!LoadLayout(createFilename(FilenameId.ROLE)))
					if (!LoadLayout(createFilename(FilenameId.DEFAULT)))
						createExample();
	}
	
	public void LoadLayout(FilenameId id) {
		LoadLayout(createFilename(id));
	}
	
	public boolean LoadLayout(String filename) {
		boolean result = false; 
    	filename = createFilename (filename);
		InputStream xmlInputStream = null;
		try {
			SAXBuilder builder = new SAXBuilder();
			xmlInputStream = new FileInputStream(filename);
			Document document = (Document) builder.build(xmlInputStream);
			Element root = document.getRootElement();
			if (!root.getName().equals("flightstripbay")) throw(new Exception("root element <flightstripbay> not recognized!"));
			RuleManager rules = master.getRulesManager();
			rules.setActive(false);
			rules.clear();
			sections.clear();
			LoadLayout1(root, Integer.parseInt(root.getAttributeValue("version")), rules);
			rules.setActive(true);
			result = true;
		} catch (Exception e) {
			log.error("Problem to parse file " + filename + ", Error:" + e.getMessage());
		} finally {
			if (xmlInputStream != null) {
				try {
					xmlInputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return result;
	}
	
	protected void LoadLayout1(Element root, int version, RuleManager rules) throws Exception {
		if (version != 1) throw(new Exception(String.format("version %d root element <flightstripbay> can not be parsed!", version)));
		// load sections
		for (Element eSection : root.getChildren(SectionData.getClassDomElementName())) {
			sections.add(new SectionData(this, eSection));
		}
		master.getFlightStripBay().recreateContents();
		// load rules
		for (Element eRuleAction : root.getChildren(RuleAndAction.getClassDomElementName())) {
			rules.add(new RuleAndAction(eRuleAction, this));
		}
	}

	// --- default examples ---
	
	public void createTraditional() {
		SectionData section_default = new SectionData(this, "Traditional", "Controlled", "Interesting", "Uncontrolled");
		section_default.setShowHeader(false);
		sections.add(section_default);
		section_default.setOrder(new ColumnOrder());
		section_default.getColumn(0).addAction(true, new ControlAction());
		section_default.getColumn(2).addAction(true, new UncontrolAction());
		// rules for new contacts
		RuleManager rules = master.getRulesManager();
		rules.add(new RuleAndAction("new", new NewRule(true), new MoveToAction(section_default, 2)));
		rules.add(new RuleAndAction("Controlled", new AndRule(new AtcSelfRule(true), new ColumnRule(2, true)), new MoveToAction(null, 0)));
		rules.add(new RuleAndAction("Uncontrolled", new AndRule(new AtcSelfRule(false), new ColumnRule(0, true)), new MoveToAction(null, 2)));
		// recreate FlightStripbay
		master.getFlightStripBay().recreateContents();

		SaveLayout(createFilename(FilenameId.DEFAULT)); // test
		rules.setActive(true);
	}
	
	public void setTraditionalOrder(boolean ordered) {
		if (sections.get(0).getTitle().equals("Traditional")) sections.get(0).setOrder(ordered ? new ColumnOrder() : null);
	}
	
	public void createExample() {
		SectionData section_emergency = new SectionData(this, "Emergency", "");
		sections.add(section_emergency);
		
		SectionData section_controlled = new SectionData(this, "Controlled", "APP", "Transit | Pattern", "DEP");
		sections.add(section_controlled);
		section_controlled.setOrder(new DistanceOrder());
		
		SectionData section_interesting = new SectionData(this, "Interesting", "APP", "Transit", "DEP");
		sections.add(section_interesting);
		section_controlled.setOrder(new DistanceOrder());

		SectionData section_uncontrolled = new SectionData(this, "Uncontrolled", "APP", "Other", "DEP");
		sections.add(section_uncontrolled);
		section_uncontrolled.setOrder(new AltitudeOrder(false));
		
		SectionData section_ground = new SectionData(this, "Ground", "TAXI IN", "PARKING", "TAXI OUT");
		sections.add(section_ground);

		SectionData section_car = new SectionData(this, "car", "park", "drive");
		sections.add(section_car);
		section_car.setOrder(new CallsignOrder());
		
		SectionData section_atc = new SectionData(this, "ATC | carrier", "");
		sections.add(section_atc);
		section_atc.setOrder(new CallsignOrder());
		
		// rules for new contacts
		RuleManager rules = master.getRulesManager();
		// ATC / carrier / car contacts
		rules.add(new RuleAndAction("ATC", new ATCRule(true), new MoveToAction(section_atc, 0)));
		rules.add(new RuleAndAction("carrier", new OrRule (new AircraftRule("MP-NIMITZ", true), new AircraftRule("MP-VINSON", true)), new MoveToAction(section_atc, 0)));
		rules.add(new RuleAndAction("car",  new AndRule(new AircraftRule("FOLLOWME", true), new GroundSpeedMaxRule(1)), new MoveToAction(section_car, 0)));
		rules.add(new RuleAndAction("car", new AircraftRule("FOLLOWME", true), new MoveToAction(section_car, 0)));
		// ground
		double ground = master.getAirportData().getElevationFt() + 50;
		//System.out.printf("ground = %f\n", ground);
		rules.add(new RuleAndAction("Ground Taxi OUT", new AndRule(new DistanceMaxRule(2), new AltitudeMaxRule(ground), new GroundSpeedMinRule(1), new ColumnRule(1, true)), new MoveToAction(section_ground, 2)));
		rules.add(new RuleAndAction("Ground Taxi", new AndRule(new DistanceMaxRule(2), new AltitudeMaxRule(ground), new GroundSpeedMinRule(1)), new MoveToAction(section_ground, -1)));
		rules.add(new RuleAndAction("Ground Parking", new AndRule(new DistanceMaxRule(2), new AltitudeMaxRule(ground), new GroundSpeedMaxRule(1)), new MoveToAction(section_ground, 1)));
		// emergency
		rules.add(new RuleAndAction("Emergency", new EmergencyRule(true), new MoveToAction(section_emergency, 0)));
		// controlled by me
		rules.add(new RuleAndAction("Controlled", new AtcSelfRule(true), new MoveToAction(section_controlled, -1)));
		// controlled by any other ATC
		rules.add(new RuleAndAction("Interesting DEP", new AndRule(new NewRule(true), new AtcOtherRule(""), new DistanceMaxRule(2)), new MoveToAction(section_interesting, 2)));
		rules.add(new RuleAndAction("Interesting APP", new AndRule(new NewRule(true), new AtcOtherRule(""), new DistanceMinRule(90)), new MoveToAction(section_interesting, 0)));
		rules.add(new RuleAndAction("Interesting OTHERS", new AndRule(new NewRule(true), new AtcOtherRule("")), new MoveToAction(section_interesting, 1)));
		rules.add(new RuleAndAction("Interesting", new AtcOtherRule(""), new MoveToAction(section_interesting, -1)));
		// uncontrolled
		rules.add(new RuleAndAction("new APP", new AndRule(new NewRule(true), new DistanceMinRule(90)), new MoveToAction(section_uncontrolled, 0)));
		rules.add(new RuleAndAction("new OTHERS", new NewRule(true), new MoveToAction(section_uncontrolled, 1)));
		rules.add(new RuleAndAction("Uncontrolled", new AtcNoneRule(true), new MoveToAction(section_uncontrolled, -1)));
		// recreate FlightStripbay
		master.getFlightStripBay().recreateContents();

		SaveLayout(createFilename(FilenameId.DEFAULT)); // test
		rules.setActive(true);
	}

}
