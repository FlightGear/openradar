package de.knewcleus.openradar.gui.flightstrips.order;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.jdom2.Element;

public class OrderManager {

	public static ArrayList<Class<? extends AbstractOrder<? extends Comparable<?>>>> getAvailableOrderClasses() {
		ArrayList<Class<? extends AbstractOrder<? extends Comparable<?>>>> result = new ArrayList<Class<? extends AbstractOrder<? extends Comparable<?>>>>();
		result.add(AltitudeOrder.class);
		result.add(CallsignOrder.class);
		result.add(ColumnOrder.class);
		result.add(DistanceOrder.class);
		return result;
	}
	
	public static ArrayList<AbstractOrder<? extends Comparable<?>>> getAvailableOrders() {
		ArrayList<AbstractOrder<? extends Comparable<?>>> result = new ArrayList<AbstractOrder<? extends Comparable<?>>>();
		for (Class<? extends AbstractOrder<? extends Comparable<?>>> orderclass : getAvailableOrderClasses()) {
			try {
				result.add(orderclass.getConstructor().newInstance());
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
			}
		}
		return result;
	}
	
	public static AbstractOrder<? extends Comparable<?>> createByClassName(Element element) throws Exception {
		AbstractOrder<? extends Comparable<?>> result = null;
		String classname = element.getName();
		//System.out.println("order name = " + classname);
		Class<?> parameterTypes[] = new Class[] { Element.class };
		for (Class<? extends AbstractOrder<? extends Comparable<?>>> orderclass : getAvailableOrderClasses()) {
			if (classname.equalsIgnoreCase(orderclass.getSimpleName())) {
				result = orderclass.getConstructor(parameterTypes).newInstance(element);
				//System.out.println("order name = " + classname + " found: result " + (result == null ? "<null>" : result.getClass().getSimpleName()));
				break;
			}
		}
		return result;
	}
	
}
