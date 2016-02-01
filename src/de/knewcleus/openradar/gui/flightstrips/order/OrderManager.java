package de.knewcleus.openradar.gui.flightstrips.order;

import java.util.ArrayList;

import org.jdom2.Element;

public class OrderManager {

	public static ArrayList<Class<? extends AbstractOrder<? extends Comparable<?>>>> getAvailableOrders() {
		ArrayList<Class<? extends AbstractOrder<? extends Comparable<?>>>> result = new ArrayList<Class<? extends AbstractOrder<? extends Comparable<?>>>>();
		result.add(AltitudeOrder.class);
		result.add(CallsignOrder.class);
		result.add(ColumnOrder.class);
		result.add(DistanceOrder.class);
		return result;
	}
	
	public static AbstractOrder<? extends Comparable<?>> createClass(Element element) throws Exception {
		AbstractOrder<? extends Comparable<?>> result = null;
		String classname = element.getName();
		Class<?> parameterTypes[] = new Class[] { Element.class };
		for (Class<? extends AbstractOrder<? extends Comparable<?>>> orderclass : getAvailableOrders()) {
			if (classname.equalsIgnoreCase(orderclass.getSimpleName())) {
				result = orderclass.getConstructor(parameterTypes).newInstance(element);
				break;
			}
		}
		return result;
	}
	
}
