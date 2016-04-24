package de.knewcleus.openradar.gui.flightstrips.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.RadarContactController;
import de.knewcleus.openradar.gui.flightstrips.ColumnData;
import de.knewcleus.openradar.gui.flightstrips.SectionData;
import de.knewcleus.openradar.gui.flightstrips.actions.ControlAction;
import de.knewcleus.openradar.gui.flightstrips.actions.MoveToAction;
import de.knewcleus.openradar.gui.flightstrips.actions.UncontrolAction;
import de.knewcleus.openradar.gui.flightstrips.conditions.AGLCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.AtcCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.AircraftCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.AndCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.ControlNoneCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.ControlOtherCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.ControlSelfCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.ColumnCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.DistanceCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.EmergencyCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.GroundSpeedCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.NewCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.OrCondition;
import de.knewcleus.openradar.gui.flightstrips.conditions.SectionCondition;
import de.knewcleus.openradar.gui.flightstrips.order.AltitudeOrder;
import de.knewcleus.openradar.gui.flightstrips.order.CallsignOrder;
import de.knewcleus.openradar.gui.flightstrips.order.ColumnOrder;
import de.knewcleus.openradar.gui.flightstrips.order.DistanceOrder;

public class LogicManager implements Runnable {

	public enum FilenameId { DEFAULT, ROLE, AIRPORT, CALLSIGN };
	
	private final String FilePath = "layout";
	
	private final GuiMasterController master;
    private final SectionsManager sectionsManager; 
    private final RulesManager rulesManager; 

    private static Logger log = LogManager.getLogger(LogicManager.class.getName());
    
	// --- constructors ---
	
	public LogicManager(GuiMasterController master) {
		this.master = master;
        sectionsManager = new SectionsManager(this);
        rulesManager = new RulesManager(master); 
	}

	// --- Runnable ---
	
	@Override
	public void run() {
		// runs in AWT-Thread, invoked by RadarContactController.publishData: 
		//      SwingUtilities.invokeLater(master.getSectionsListManager());
		RadarContactController radarContactController = master.getRadarContactManager();
		// get list of contacts
		radarContactController.clearSectionsListManagerFlag();
		sectionsManager.updateFlightstrips(radarContactController.getContactListCopy(), rulesManager);
	}
	
	// --- links provider ---
	
	public GuiMasterController getGuiMasterController() {
		return master; // TODO
	}
	
	public SectionsManager getSectionsManager() {
		return sectionsManager; // TODO
	}
	
	public RulesManager getRulesManager() { 
		return rulesManager; // TODO
	}
	
	// --- XML ---
	
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
            for (SectionData section : sectionsManager.getSections()) {
    			root.addContent(section.createDomElement());
    		}
    		// rules
            for (Rule ra : rulesManager.getRules()) {
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
					if (!LoadLayout(createFilename(FilenameId.DEFAULT))) {
						//createExample();
						createTraditional();
						SaveLayout(createFilename(FilenameId.DEFAULT));
					}
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
			rulesManager.setActive(false);
			rulesManager.clear();
			sectionsManager.clear();
			LoadLayout1(root, Integer.parseInt(root.getAttributeValue("version")));
			rulesManager.setActive(true);
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
	
	protected void LoadLayout1(Element root, int version) throws Exception {
		if (version != 1) throw(new Exception(String.format("version %d root element <flightstripbay> can not be parsed!", version)));
		// load sections
		for (Element eSection : root.getChildren(SectionData.getClassDomElementName())) {
			sectionsManager.add(new SectionData(sectionsManager, eSection));
		}
		// load rules
		for (Element eRuleAction : root.getChildren(Rule.getClassDomElementName())) {
			rulesManager.add(new Rule(eRuleAction));
		}
	}
	
	// --- default examples ---
	
	public void createTraditional() {
		sectionsManager.clear();
		SectionData section_default = new SectionData(sectionsManager, "Traditional", "Controlled", "Interesting", "Uncontrolled");
		section_default.setShowHeader(false);
		sectionsManager.add(section_default);
		section_default.setOrder(new ColumnOrder());
		section_default.getColumn(0).addAction(true, new ControlAction());
		section_default.getColumn(0).setPaintLevel(ColumnData.PaintLevel.TOP);
		section_default.getColumn(2).addAction(true, new UncontrolAction());
		section_default.getColumn(2).setPaintLevel(ColumnData.PaintLevel.BOTTOM);
		// rules for new contacts
		rulesManager.setActive(false);
		rulesManager.clear();
		rulesManager.add(new Rule("new", new OrCondition(new NewCondition(true), new SectionCondition("Traditional", false)), new MoveToAction(section_default.getTitle(), 2)));
		rulesManager.add(new Rule("Controlled", new AndCondition(new ControlSelfCondition(true), new ColumnCondition(2, true)), new MoveToAction("", 0)));
		rulesManager.add(new Rule("Uncontrolled", new AndCondition(new ControlSelfCondition(false), new ColumnCondition(0, true)), new MoveToAction("", 2)));
		rulesManager.setActive(true);
	}
	
	public void setTraditionalOrder(boolean ordered) {
		SectionData section = sectionsManager.getElementAt(0);
		if (section.getTitle().equals("Traditional")) section.setOrder(ordered ? new ColumnOrder() : null);
	}
	
	public void createExample() {
		sectionsManager.clear();
		SectionData section_emergency = new SectionData(sectionsManager, "Emergency", "");
		sectionsManager.add(section_emergency);
		section_emergency.getColumn(0).setPaintLevel(ColumnData.PaintLevel.TOP);
		
		SectionData section_controlled = new SectionData(sectionsManager, "Controlled", "APP", "Transit | Pattern", "DEP");
		sectionsManager.add(section_controlled);
		section_controlled.setOrder(new DistanceOrder());
		section_controlled.getColumn(0).setPaintLevel(ColumnData.PaintLevel.HIGHER);
		section_controlled.getColumn(1).setPaintLevel(ColumnData.PaintLevel.LOWER);
		section_controlled.getColumn(2).setPaintLevel(ColumnData.PaintLevel.HIGHER);
		
		SectionData section_interesting = new SectionData(sectionsManager, "Interesting", "APP", "Transit", "DEP");
		sectionsManager.add(section_interesting);
		section_interesting.setOrder(new DistanceOrder());
		section_interesting.getColumn(0).setPaintLevel(ColumnData.PaintLevel.LOWER);
		section_interesting.getColumn(1).setPaintLevel(ColumnData.PaintLevel.LOWER);
		section_interesting.getColumn(2).setPaintLevel(ColumnData.PaintLevel.LOWER);

		SectionData section_uncontrolled = new SectionData(sectionsManager, "Uncontrolled", "APP", "Other", "DEP");
		sectionsManager.add(section_uncontrolled);
		section_uncontrolled.setOrder(new AltitudeOrder(false));
		section_uncontrolled.getColumn(0).setPaintLevel(ColumnData.PaintLevel.BOTTOM);
		section_uncontrolled.getColumn(1).setPaintLevel(ColumnData.PaintLevel.BOTTOM);
		section_uncontrolled.getColumn(2).setPaintLevel(ColumnData.PaintLevel.BOTTOM);
		
		SectionData section_ground = new SectionData(sectionsManager, "Ground", "TAXI IN", "stop", "PARKING", "stop", "TAXI OUT");
		sectionsManager.add(section_ground);
		section_ground.getColumn(1).setPaintLevel(ColumnData.PaintLevel.LOWER);
		section_ground.getColumn(2).setPaintLevel(ColumnData.PaintLevel.BOTTOM);
		section_ground.getColumn(3).setPaintLevel(ColumnData.PaintLevel.LOWER);

		SectionData section_dual = new SectionData(sectionsManager, "Dual", "copilot", "passenger");
		sectionsManager.add(section_dual);
		section_dual.setOrder(new CallsignOrder());
		section_dual.getColumn(0).setPaintLevel(ColumnData.PaintLevel.BOTTOM);
		section_dual.getColumn(1).setPaintLevel(ColumnData.PaintLevel.BOTTOM);
		
		SectionData section_car = new SectionData(sectionsManager, "car", "drive", "park");
		sectionsManager.add(section_car);
		section_car.setOrder(new ColumnOrder());
		section_car.getColumn(1).setPaintLevel(ColumnData.PaintLevel.BOTTOM);
		
		SectionData section_carrier = new SectionData(sectionsManager, "carrier", "");
		sectionsManager.add(section_carrier);
		section_carrier.setOrder(new CallsignOrder());
		section_carrier.getColumn(0).setPaintLevel(ColumnData.PaintLevel.BOTTOM);
		
		SectionData section_atc = new SectionData(sectionsManager, "ATC", "");
		sectionsManager.add(section_atc);
		section_atc.setOrder(new CallsignOrder());
		section_atc.getColumn(0).setPaintLevel(ColumnData.PaintLevel.BOTTOM);
		
		// rules for new contacts
		rulesManager.setActive(false);
		rulesManager.clear();
		// ATC / carrier / car contacts
		rulesManager.add(new Rule("ATC", new AtcCondition(true), new MoveToAction(section_atc.getTitle(), 0)));
		rulesManager.add(new Rule("carrier", new OrCondition (new AircraftCondition("MP-NIMITZ", true), new AircraftCondition("MP-VINSON", true)), new MoveToAction(section_carrier.getTitle(), 0)));
		rulesManager.add(new Rule("car park",  new AndCondition(new AircraftCondition("FOLLOWME", true), new GroundSpeedCondition(null, 1, true)), new MoveToAction(section_car.getTitle(), 1)));
		rulesManager.add(new Rule("car drive", new AircraftCondition("FOLLOWME", true), new MoveToAction(section_car.getTitle(), 0)));
		// dual
		rulesManager.add(new Rule("dual copilot", new OrCondition (new AircraftCondition(".+-copilot", true)), new MoveToAction(section_dual.getTitle(), 0)));
		rulesManager.add(new Rule("dual passenger", new OrCondition (new AircraftCondition(".+-PAX", true)), new MoveToAction(section_dual.getTitle(), 1)));
		// ground
		rulesManager.add(new Rule("PARKING", new AndCondition(new DistanceCondition(null, 2., true), new AGLCondition(null, 50, true), new GroundSpeedCondition(null, 1, true), new OrCondition (new NewCondition(true), new ColumnCondition(2, true))), new MoveToAction(section_ground.getTitle(), 2)));
		rulesManager.add(new Rule("Taxi OUT", new AndCondition(new DistanceCondition(null, 2., true), new AGLCondition(null, 50, true), new GroundSpeedCondition(1, null, true), new OrCondition (new NewCondition(true), new ColumnCondition(2, true), new ColumnCondition(3, true), new ColumnCondition(4, true))), new MoveToAction(section_ground.getTitle(), 4)));
		rulesManager.add(new Rule("Taxi OUT stop", new AndCondition(new DistanceCondition(null, 2., true), new AGLCondition(null, 50, true), new GroundSpeedCondition(null, 1, true), new OrCondition (new ColumnCondition(3, true), new ColumnCondition(4, true))), new MoveToAction(section_ground.getTitle(), 3)));
		rulesManager.add(new Rule("Taxi IN stop", new AndCondition(new DistanceCondition(null, 2., true), new AGLCondition(null, 50, true), new GroundSpeedCondition(null, 1, true), new OrCondition (new ColumnCondition(0, true), new ColumnCondition(1, true))), new MoveToAction(section_ground.getTitle(), 1)));
		rulesManager.add(new Rule("Taxi IN", new AndCondition(new DistanceCondition(null, 2., true), new AGLCondition(null, 50, true), new GroundSpeedCondition(1, null, true)), new MoveToAction(section_ground.getTitle(), 0)));
		// emergency
		rulesManager.add(new Rule("Emergency", new EmergencyCondition(true), new MoveToAction(section_emergency.getTitle(), 0)));
		// controlled by me
		rulesManager.add(new Rule("Controlled", new ControlSelfCondition(true), new MoveToAction(section_controlled.getTitle(), -1)));
		// controlled by any other ATC
		rulesManager.add(new Rule("Interesting DEP", new AndCondition(new NewCondition(true), new ControlOtherCondition(".+", true), new DistanceCondition(null, 2., true)), new MoveToAction(section_interesting.getTitle(), 2)));
		rulesManager.add(new Rule("Interesting APP", new AndCondition(new NewCondition(true), new ControlOtherCondition(".+", true), new DistanceCondition(90., null, true)), new MoveToAction(section_interesting.getTitle(), 0)));
		rulesManager.add(new Rule("Interesting OTHERS", new AndCondition(new NewCondition(true), new ControlOtherCondition(".+", true)), new MoveToAction(section_interesting.getTitle(), 1)));
		rulesManager.add(new Rule("Interesting", new ControlOtherCondition(".+", true), new MoveToAction(section_interesting.getTitle(), -1)));
		// uncontrolled
		rulesManager.add(new Rule("new APP", new AndCondition(new NewCondition(true), new DistanceCondition(90., null, true)), new MoveToAction(section_uncontrolled.getTitle(), 0)));
		rulesManager.add(new Rule("new OTHERS", new NewCondition(true), new MoveToAction(section_uncontrolled.getTitle(), 1)));
		rulesManager.add(new Rule("Uncontrolled", new ControlNoneCondition(true), new MoveToAction(section_uncontrolled.getTitle(), -1)));
		rulesManager.setActive(true);
	}

}
