/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2018 Wolfram Wagner
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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.fgfs.multiplayer.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class PropertyRegistry {

	public enum DataType {
		NONE,ALIAS,BOOL,INT,SHORTINT,LONG,FLOAT,SHORT_FLOAT_NORM,SHORT_FLOAT_1,SHORT_FLOAT_2,SHORT_FLOAT_3,SHORT_FLOAT_4,DOUBLE,STRING,UNSPECIFIED,TT_BOOLARRAY;
	}
	
	protected static Logger log = LogManager.getLogger("de.knewcleus.fgfs.multiplayer");
	protected static PropertyRegistry instance;
	
	protected final Map<String, PropertyDescriptor> descriptorsByName=new HashMap<String, PropertyDescriptor>();
	protected final Map<Integer, PropertyDescriptor> descriptorsByID=new HashMap<Integer, PropertyDescriptor>();
	
	protected PropertyRegistry() {
		InputStream inputStream=PropertyRegistry.class.getResourceAsStream("propertytypes.csv");

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
			String line;
			while(null != (line = br.readLine())) {
				StringTokenizer t = new StringTokenizer(line, ",");
				int id = Integer.parseInt(t.nextToken());
				String prop = t.nextToken();
				DataType type = DataType.valueOf(t.nextToken());
				String sTransType = t.nextToken();
				DataType transType = ("same".equals(sTransType)) ? type : DataType.valueOf(sTransType);
				String proto = t.nextToken();
				
				PropertyDescriptor descriptor=new PropertyDescriptor(id,prop,type,transType,proto);
				descriptorsByName.put(prop,descriptor);
				descriptorsByID.put(id,descriptor);
			}
		} catch (IOException e) {
			log.warn("Failed to load multiplayer propertytypes");
			return;
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
