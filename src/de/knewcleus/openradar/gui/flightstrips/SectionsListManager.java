package de.knewcleus.openradar.gui.flightstrips;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.contacts.RadarContactController;
import de.knewcleus.openradar.gui.flightstrips.actions.AbstractAction;
import de.knewcleus.openradar.gui.flightstrips.actions.ControlAction;
import de.knewcleus.openradar.gui.flightstrips.actions.MoveToAction;
import de.knewcleus.openradar.gui.flightstrips.actions.UncontrolAction;
import de.knewcleus.openradar.gui.flightstrips.order.AbstractOrder;
import de.knewcleus.openradar.gui.flightstrips.order.AltitudeOrder;
import de.knewcleus.openradar.gui.flightstrips.order.CallsignOrder;
import de.knewcleus.openradar.gui.flightstrips.order.ColumnOrder;
import de.knewcleus.openradar.gui.flightstrips.order.DistanceOrder;
import de.knewcleus.openradar.gui.flightstrips.rules.ATCRule;
import de.knewcleus.openradar.gui.flightstrips.rules.AbstractRule;
import de.knewcleus.openradar.gui.flightstrips.rules.AircraftRule;
import de.knewcleus.openradar.gui.flightstrips.rules.AltitudeMaxRule;
import de.knewcleus.openradar.gui.flightstrips.rules.AndRule;
import de.knewcleus.openradar.gui.flightstrips.rules.AtcNoneRule;
import de.knewcleus.openradar.gui.flightstrips.rules.AtcOtherRule;
import de.knewcleus.openradar.gui.flightstrips.rules.AtcSelfRule;
import de.knewcleus.openradar.gui.flightstrips.rules.ColumnRule;
import de.knewcleus.openradar.gui.flightstrips.rules.DistanceMaxRule;
import de.knewcleus.openradar.gui.flightstrips.rules.DistanceMinRule;
import de.knewcleus.openradar.gui.flightstrips.rules.GroundSpeedMaxRule;
import de.knewcleus.openradar.gui.flightstrips.rules.GroundSpeedMinRule;
import de.knewcleus.openradar.gui.flightstrips.rules.NewRule;
import de.knewcleus.openradar.gui.flightstrips.rules.OrRule;
import de.knewcleus.openradar.gui.flightstrips.rules.RulesManager;

public class SectionsListManager implements Runnable {
	
	private final GuiMasterController master;
	private final ArrayList<SectionData> sections = new ArrayList<SectionData>();
    private static Logger log = LogManager.getLogger(SectionsListManager.class.getName());
	
	// --- constructors ---
	
	public SectionsListManager(GuiMasterController master) {
		this.master = master;
	}
	
	// --- sections ---
	
	public void createTraditional() {
		SectionData section_default = new SectionData("Traditional", "Controlled", "Interesting", "Uncontrolled");
		section_default.setShowHeader(false);
		sections.add(section_default);
		section_default.setOrder(new ColumnOrder());
		section_default.getColumn(0).addAction(true, new ControlAction());
		section_default.getColumn(2).addAction(true, new UncontrolAction());
		// rules for new contacts
		RulesManager rules = master.getRulesManager();
		rules.add(rules.new RuleAndAction("new", new NewRule(true), new MoveToAction(section_default, 2)));
		rules.add(rules.new RuleAndAction("Controlled", new AndRule(new AtcSelfRule(true), new ColumnRule(2, true)), new MoveToAction(null, 0)));
		rules.add(rules.new RuleAndAction("Uncontrolled", new AndRule(new AtcSelfRule(false), new ColumnRule(0, true)), new MoveToAction(null, 2)));
		// recreate FlightStripbay
		master.getFlightStripBay().recreateContents();

		SaveLayout(); // test
	}
	
	public void setTraditionalOrder(boolean ordered) {
		if (sections.get(0).getTitle().equals("Traditional")) sections.get(0).setOrder(ordered ? new ColumnOrder() : null);
	}
	
	public void createExample() {
		/* test
		SectionData section = new SectionData("Emergency", "");
		sections.add(section);
		*/
		SectionData section_controlled = new SectionData("Controlled", "APP", "Transit | Pattern", "DEP");
		sections.add(section_controlled);
		section_controlled.setOrder(new DistanceOrder());
		
		SectionData section_interesting = new SectionData("Interesting", "APP", "Transit", "DEP");
		sections.add(section_interesting);
		section_controlled.setOrder(new DistanceOrder());

		SectionData section_uncontrolled = new SectionData("Uncontrolled", "APP", "Other", "DEP");
		sections.add(section_uncontrolled);
		section_uncontrolled.setOrder(new AltitudeOrder(false));
		
		SectionData section_ground = new SectionData("Ground", "TAXI IN", "PARKING", "TAXI OUT");
		sections.add(section_ground);
		section_ground.setOrder(new ColumnOrder(false));

		SectionData section_atc = new SectionData("ATC | carrier | car", "");
		sections.add(section_atc);
		section_atc.setOrder(new CallsignOrder());
		
		// rules for new contacts
		RulesManager rules = master.getRulesManager();
		// ATC / carrier / car contacts
		rules.add(rules.new RuleAndAction("ATC", new ATCRule(true), new MoveToAction(section_atc, 0)));
		rules.add(rules.new RuleAndAction("carrier", new OrRule (new AircraftRule("MP-NIMITZ", true), new AircraftRule("MP-VINSON", true)), new MoveToAction(section_atc, 0)));
		rules.add(rules.new RuleAndAction("car", new AircraftRule("FOLLOWME", true), new MoveToAction(section_atc, 0)));
		// ground
		double ground = master.getAirportData().getElevationFt() + 50;
		System.out.printf("ground = %f\n", ground);
		rules.add(rules.new RuleAndAction("Ground Taxi OUT", new AndRule(new DistanceMaxRule(2), new AltitudeMaxRule(ground), new GroundSpeedMinRule(1), new ColumnRule(1, true)), new MoveToAction(section_ground, 2)));
		rules.add(rules.new RuleAndAction("Ground Taxi", new AndRule(new DistanceMaxRule(2), new AltitudeMaxRule(ground), new GroundSpeedMinRule(1)), new MoveToAction(section_ground, -1)));
		rules.add(rules.new RuleAndAction("Ground Parking", new AndRule(new DistanceMaxRule(2), new AltitudeMaxRule(ground), new GroundSpeedMaxRule(1)), new MoveToAction(section_ground, 1)));
		// controlled by me
		rules.add(rules.new RuleAndAction("Controlled", new AtcSelfRule(true), new MoveToAction(section_controlled, -1)));
		// controlled by any other ATC
		rules.add(rules.new RuleAndAction("Interesting DEP", new AndRule(new NewRule(true), new AtcOtherRule(""), new DistanceMaxRule(2)), new MoveToAction(section_interesting, 2)));
		rules.add(rules.new RuleAndAction("Interesting APP", new AndRule(new NewRule(true), new AtcOtherRule(""), new DistanceMinRule(90)), new MoveToAction(section_interesting, 0)));
		rules.add(rules.new RuleAndAction("Interesting OTHERS", new AndRule(new NewRule(true), new AtcOtherRule("")), new MoveToAction(section_interesting, 1)));
		rules.add(rules.new RuleAndAction("Interesting", new AtcOtherRule(""), new MoveToAction(section_interesting, -1)));
		// uncontrolled
		rules.add(rules.new RuleAndAction("new APP", new AndRule(new NewRule(true), new DistanceMinRule(90)), new MoveToAction(section_uncontrolled, 0)));
		rules.add(rules.new RuleAndAction("new OTHERS", new NewRule(true), new MoveToAction(section_uncontrolled, 1)));
		rules.add(rules.new RuleAndAction("Uncontrolled", new AtcNoneRule(true), new MoveToAction(section_uncontrolled, -1)));
		// recreate FlightStripbay
		master.getFlightStripBay().recreateContents();

		SaveLayout(); // test
	}

	public int getSectionCount() {
		return sections.size();
	}
	
	public SectionData addSection(String title) {
		SectionData result = new SectionData(title); 
		sections.add(result);
		return result;
	}

	public SectionData addSection(String title, String... columnTitles) {
		SectionData result = new SectionData(title, columnTitles); 
		sections.add(result);
		return result;
	}
	
	public ArrayList<SectionData> getSections() {
		return sections;
	}

	public void updateFlightstrips() {
		RadarContactController radarContactController = master.getRadarContactManager();
		radarContactController.clearSectionsListManagerFlag();
		List<GuiRadarContact> contacts = radarContactController.getContactListCopy();
		ArrayList<FlightStrip> flightstrips = new ArrayList<FlightStrip>();
		// collect all flightstrips
		for (SectionData section : sections) {
			flightstrips.addAll(section.getFlightStrips());
		}
		// update existing and remove expired flightstrips
        RulesManager rm = master.getRulesManager();
		for (FlightStrip flightstrip : flightstrips) {
			GuiRadarContact contact = flightstrip.getContact();
			if (contacts.contains(contact)) {
				// contact exists
				flightstrip.updateContents();
				rm.ApplyAppropriateRule(flightstrip);
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
			rm.ApplyAppropriateRule(flightstrip);
		}
		// order flightstrips within the section
		for (SectionData section : sections) {
			section.reorderFlightStrips();
		}
	}
	
	@Override
	public void run() {
		// runs in AWT-Thread, invoked by RadarContactController.publishData: SwingUtilities.invokeLater(master.getSectionsListManager());
		updateFlightstrips();
	}
	
	// XML
	
	public void SaveLayout() {
        try {
    		// --- create document ---
            Document doc = new Document();
            Element root = new Element("flightstripbay");
            doc.addContent(root);
            root.setAttribute("version", "1.0");
            // sections
            Element eSections = new Element("sections");
            root.addContent(eSections);
            Element eSection; 
            Element eOrder; 
            Element eColumn; 
            Element eRuleAction; 
    		for (SectionData section : sections) {
    			// section
    			eSection = new Element("section");
    			eSections.addContent(eSection);
    			section.putAttributes(eSection);
    			// order
    			eOrder = new Element("order");
    			eSection.addContent(eOrder);
    			AbstractOrder<?> order = section.getOrder();
    			if (order != null) order.putAttributes(eOrder);
    			// columns
    			for (ColumnData column : section.getColumns()) {
    				// column
    				eColumn = new Element("column");
    				eSection.addContent(eColumn);
    				column.putAttributes(eColumn);
    				// actions
    				for (AbstractAction action : column.getEnterActions()) {
    					eRuleAction = new Element("action");
    					eColumn.addContent(eRuleAction);
    					action.putAttributes(eRuleAction);
    				}
    			}
    		}
    		// rules
            Element eRules = new Element("rules");
            root.addContent(eRules);
            for (RulesManager.RuleAndAction ra : master.getRulesManager().getRuleAndActions()) {
            	eRuleAction = new Element("ruleaction");
            	eRules.addContent(eRuleAction);
				ra.putAttributes(eRuleAction);
				addRule(eRuleAction, ra.getRule());
				addAction(eRuleAction, ra.getAction());
            }
            // --- save document ---
            XMLOutputter xmlOutput = new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(doc, new FileWriter("settings" + File.separator + "layout.xml"));        
        } catch (Exception exception) {
        	log.error( "Problem to create xml document for flightstripbay: ", exception);
            System.err.printf("Problem to create xml document for flightstripbay: ", exception.getMessage());
        }
	}
	
	public void addRule(Element element, AbstractRule rule) {
		Element eRule = new Element("rule"); 
		element.addContent(eRule);
		rule.putAttributes(eRule);
		for (AbstractRule r : rule.getRules()) {
			addRule(eRule, r);
		}
	}
	
	public void addAction(Element element, AbstractAction action) {
		Element eAction = new Element("action"); 
		element.addContent(eAction);
		action.putAttributes(eAction);
		for (AbstractAction a : action.getActions()) {
			addAction(eAction, a);
		}
	}
	
}
