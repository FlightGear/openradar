package de.knewcleus.radar.aircraft;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

public class AircraftTypeInventory {
	protected static Logger logger=Logger.getLogger(AircraftTypeInventory.class.getName());
	protected static AircraftTypeInventory instance;
	protected Set<AircraftType> aircraftTypes=new HashSet<AircraftType>();
	protected Map<String,AircraftType> icaoIndex=new HashMap<String, AircraftType>();
	protected Map<String,Map<String,AircraftType>> vendorIndex=new HashMap<String, Map<String,AircraftType>>();
	
	private AircraftTypeInventory() {
		loadStandardTypes();
	}
	
	private void loadStandardTypes() {
		InputStream inputStream=this.getClass().getResourceAsStream("aircrafttypes.properties");
		if (inputStream==null)
			return;
		logger.config("Loading associatedTarget types");
		Properties properties=new Properties();
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		loadFromProperties(properties);
	}
	
	public void loadFromProperties(Properties properties) {
		String[] typeNames=properties.getProperty("typeNames","").split("\\s*,\\s*");
		for (String aircraftName: typeNames) {
			logger.config("Loading configuration for associatedTarget type '"+aircraftName+"'");
			String className=properties.getProperty("associatedTarget."+aircraftName,AircraftType.class.getName());
			try {
				Class<?> clazz=ClassLoader.getSystemClassLoader().loadClass(className);
				Constructor<?> constructor=clazz.getDeclaredConstructor(String.class, Properties.class);
				AircraftType aircraftType=(AircraftType)constructor.newInstance("associatedTarget."+aircraftName, properties);
				registerAircraftType(aircraftType);
			} catch (Exception e) {
				logger.severe("Unable to load associatedTarget type 'associatedTarget."+aircraftName+"':"+e.getMessage());
			}
		}
	}
	
	public void registerAircraftType(AircraftType aircraftType) {
		aircraftTypes.add(aircraftType);
		
		String icao=aircraftType.getDesignationICAO();
		logger.info("Registering associatedTarget type with ICAO designation "+icao);
		if (!icao.equals("ZZZZ") && !icao.equals("ULAC")) {
			icaoIndex.put(aircraftType.getDesignationICAO(), aircraftType);
		}
		
		String vendor, type;
		
		vendor=aircraftType.getVendor();
		type=aircraftType.getType();
		Map<String,AircraftType> vendorTypeIndex;
		if (vendorIndex.containsKey(vendor)) {
			vendorTypeIndex=vendorIndex.get(vendor);
		} else {
			vendorTypeIndex=new HashMap<String, AircraftType>();
			vendorIndex.put(vendor,vendorTypeIndex);
		}
		vendorTypeIndex.put(type,aircraftType);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends AircraftType> T getByICAODesignation(String designationICAO) {
		return (T)icaoIndex.get(designationICAO);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends AircraftType> T  getByVendorAndType(String vendor, String type) {
		if (!vendorIndex.containsKey(vendor))
			return null;
		return (T)vendorIndex.get(vendor).get(type);
	}
	
	public static AircraftTypeInventory getInstance() {
		if (instance==null)
			instance=new AircraftTypeInventory();
		return instance;
	}
}
