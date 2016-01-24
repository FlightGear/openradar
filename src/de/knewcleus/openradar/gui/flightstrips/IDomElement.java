package de.knewcleus.openradar.gui.flightstrips;

import org.jdom2.Element;

public interface IDomElement {
	public String getDomElementName();
	public Element createDomElement();
}
