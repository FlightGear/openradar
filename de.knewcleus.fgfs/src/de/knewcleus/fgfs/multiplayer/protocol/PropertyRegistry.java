package de.knewcleus.fgfs.multiplayer.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertyRegistry {
	protected static Logger logger=Logger.getLogger("de.knewcleus.fgfs.multiplayer");
	protected static PropertyRegistry instance;
	
	protected final Map<String, PropertyDescriptor> descriptorsByName=new HashMap<String, PropertyDescriptor>();
	protected final Map<Integer, PropertyDescriptor> descriptorsByID=new HashMap<Integer, PropertyDescriptor>();
	
	protected PropertyRegistry() {
		InputStream inputStream=PropertyRegistry.class.getResourceAsStream("propertytypes.properties");
		Properties properties=new Properties();
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			logger.warning("Failed to load multiplayer propertytypes");
			return;
		}
		
		int count=Integer.parseInt(properties.getProperty("propertytypes","0"));
		
		for (int i=1;i<=count;i++) {
			String prefix="propertytype."+i;
			
			int id=Integer.parseInt(properties.getProperty(prefix+".id"));
			String name=properties.getProperty(prefix+".name");
			PropertyType type=PropertyType.valueOf(properties.getProperty(prefix+".type","UNSPECIFIED"));
			if (type==null)
				type=PropertyType.UNSPECIFIED;
			
			PropertyDescriptor descriptor=new PropertyDescriptor(id,name,type);
			descriptorsByName.put(name,descriptor);
			descriptorsByID.put(id,descriptor);
		}
	}
	
	public PropertyDescriptor getByID(int id) {
		return descriptorsByID.get(id);
	}
	
	public PropertyDescriptor getByName(String name) {
		return descriptorsByName.get(name);
	}
	
	public static PropertyRegistry getInstance() {
		if (instance==null)
			instance=new PropertyRegistry();
		return instance;
	}
	
}
