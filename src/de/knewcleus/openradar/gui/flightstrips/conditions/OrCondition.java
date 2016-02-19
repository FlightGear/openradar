package de.knewcleus.openradar.gui.flightstrips.conditions;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

/* This class bundles a set of conditions with the OR operator together to one condition
 * 
 */
public class OrCondition extends AbstractOperatorCondition {

	public OrCondition() {
		super();
	}

	public OrCondition(ArrayList<AbstractCondition> conditions) {
		super(conditions);
	}

	public OrCondition(AbstractCondition... conditions) {
		super(conditions);
	}

	public OrCondition(Element element) throws Exception {
		super(element);
	}
	
	@Override
	public Boolean isAppropriate(FlightStrip flightstrip, AirportData airportData) {
		for (AbstractCondition condition : conditions) {
			Boolean value = condition.isAppropriate(flightstrip, airportData);
			if ((value == null) || value) return value; 
		}
		return false;
	}
	
	@Override
	public String getSimpleText() {
		return "OR";
	}
	
}
