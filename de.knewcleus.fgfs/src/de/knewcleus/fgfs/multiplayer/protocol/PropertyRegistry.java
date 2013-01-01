/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
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
