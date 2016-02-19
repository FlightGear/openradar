package de.knewcleus.openradar.gui.flightstrips.conditions;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.config.LogicManager;
import de.knewcleus.openradar.gui.setup.AirportData;

/* This class bundles a set of conditions with the AND operator together to one condition
 * 
 */
public class AndCondition extends AbstractOperatorCondition {

	public AndCondition() {
		super();
	}

	public AndCondition(LogicManager logic) {
		this();
	}
	
	public AndCondition(ArrayList<AbstractCondition> conditions) {
		super(conditions);
	}

	public AndCondition(AbstractCondition... conditions) {
		super(conditions);
	}

	public AndCondition(Element element) throws Exception {
		super(element);
	}
	
	@Override
	public Boolean isAppropriate(FlightStrip flightstrip, AirportData airportData) {
		for (AbstractCondition condition : conditions) {
			Boolean value = condition.isAppropriate(flightstrip, airportData);
			if ((value == null) || !value) return value; 
		}
		return true;
	}
	
	@Override
	public String getSimpleText() {
		return "AND";
	}
	
}
