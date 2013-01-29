/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2012 Wolfram Wagner
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

import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.multiplayer.MultiplayerException;

public class PositionMessage implements IMultiplayerMessage {
	protected static final Logger logger=Logger.getLogger("de.knewcleus.fgfs.multiplayer");
	public static final int MAX_MODEL_NAME_LEN=96;
	public static final int MAX_PROPERTY_LEN=52;

	protected String model;
	protected double time;
	protected double lag;
	protected Position position=new Position();
	protected Position orientation=new Position();
	protected Position linearVelocity=new Position();
	protected Position angularVelocity=new Position();
	protected Position linearAcceleration=new Position();
	protected Position angularAcceleration=new Position();
	protected final Map<String, Object> properties=Collections.synchronizedMap(new HashMap<String, Object>());

	public PositionMessage() {
	}

	public int getMessageID() {
		return MultiplayerPacket.POS_DATA_ID;
	}

	public int getMessageSize() {
		return MAX_MODEL_NAME_LEN+5*8+15*4+getPropertiesLength();
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public double getLag() {
		return lag;
	}

	public void setLag(double lag) {
		this.lag = lag;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Position getOrientation() {
		return orientation;
	}

	public void setOrientation(Position orientation) {
		this.orientation = orientation;
	}

	public Position getLinearVelocity() {
		return linearVelocity;
	}

	public void setLinearVelocity(Position linearVelocity) {
		this.linearVelocity = linearVelocity;
	}

	public Position getAngularVelocity() {
		return angularVelocity;
	}

	public void setAngularVelocity(Position angularVelocity) {
		this.angularVelocity = angularVelocity;
	}

	public Position getLinearAcceleration() {
		return linearAcceleration;
	}

	public void setLinearAcceleration(Position linearAcceleration) {
		this.linearAcceleration = linearAcceleration;
	}

	public Position getAngularAcceleration() {
		return angularAcceleration;
	}

	public void setAngularAcceleration(Position angularAcceleration) {
		this.angularAcceleration = angularAcceleration;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public <T> void putProperty(String name, T value) {
		properties.put(name, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T getProperty(String name) {
		return (T)properties.get(name);
	}

	public void encode(XDROutputStream outputStream) throws MultiplayerException {

		try {
			MPUtils.writeCString(outputStream, model, MAX_MODEL_NAME_LEN);

			outputStream.writeDouble(time);  // 8 byte
			outputStream.writeDouble(lag);  // 8 byte
			MPUtils.writeDoublePosition(outputStream,position); //96 byte
			MPUtils.writeFloatPosition(outputStream,orientation); // 1st 4 byte
			MPUtils.writeFloatPosition(outputStream,linearVelocity); // 2nd 4 byte
			MPUtils.writeFloatPosition(outputStream,angularVelocity); // 3rd 4 byte
			MPUtils.writeFloatPosition(outputStream,linearAcceleration); // 4th 4 byte
			MPUtils.writeFloatPosition(outputStream,angularAcceleration); // 5th 4 byte
//			// add 4 bytes padding are missing pad them
//			outputStream.write(new byte[] {0,0,0,0}); // 6th 4 byte
			// => 200 byte
			encodeProperties(outputStream);
		} catch (IOException e) {
			throw new MultiplayerException(e);
		}
	}

	public void decode(XDRInputStream inputStream) throws MultiplayerException {
		try {
			model=MPUtils.readCString(inputStream, MAX_MODEL_NAME_LEN);
			System.out.println(model);
			time=inputStream.readDouble();
			lag=inputStream.readDouble();
            //System.out.println(time+" "+lag);
			position=MPUtils.readDoublePosition(inputStream);
			orientation=MPUtils.readFloatPosition(inputStream);
			linearVelocity=MPUtils.readFloatPosition(inputStream);
			angularVelocity=MPUtils.readFloatPosition(inputStream);
			linearAcceleration=MPUtils.readFloatPosition(inputStream);
			angularAcceleration=MPUtils.readFloatPosition(inputStream);
//			inputStream.skipBytes(4); must be conditional, moved into decodeProperties();
			decodeProperties(inputStream);
		} catch (IOException e) {
			throw new MultiplayerException(e);
		}
	}

	protected int getPropertiesLength() {
		int length=0;
		for (Map.Entry<String, Object> entry: properties.entrySet()) {
			PropertyDescriptor descriptor=PropertyRegistry.getInstance().getByName(entry.getKey());
			if (descriptor==null) {
				continue;
			}
			
			length+=4;

			switch (descriptor.getType()) {
			case INT:
			case BOOL:
			case LONG:
			case FLOAT:
			case DOUBLE:
				length+=4;
				break;
			case STRING:
			case UNSPECIFIED:
				String str=entry.getValue().toString();
				byte bytes[];
				try {
					bytes = str.getBytes("US-ASCII");
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
				int pad=(-bytes.length)&3;
				int strlen=4+4*(bytes.length+pad);
				
				length+=strlen;
				break;
            default:
                break;
			}
		}

		return length;
	}

	protected void encodeProperties(XDROutputStream outputStream) throws IOException {
		for (Map.Entry<String, Object> entry: properties.entrySet()) {
			PropertyDescriptor descriptor=PropertyRegistry.getInstance().getByName(entry.getKey());
			if (descriptor==null) {
				logger.warning("Skipping encoding of property "+entry.getKey());
				continue;
			}
			outputStream.writeInt(descriptor.getPropertyID());

			Number num;
			switch (descriptor.getType()) {
			case INT:
			case BOOL:
			case LONG:
				if (entry.getValue() instanceof Boolean) {
					Boolean bool=(Boolean)entry.getValue();
					num=new Integer(bool.booleanValue()?1:0);
				} else {
					num=(Number)entry.getValue();
				}
				outputStream.writeInt(num.intValue());
				break;
			case FLOAT:
			case DOUBLE:
				if (entry.getValue() instanceof Boolean) {
					Boolean bool=(Boolean)entry.getValue();
					num=new Integer(bool.booleanValue()?1:0);
				} else {
					num=(Number)entry.getValue();
				}
				outputStream.writeFloat(num.floatValue());
				break;
			case STRING:
			case UNSPECIFIED:
				String str=entry.getValue().toString();
				byte bytes[]=str.getBytes("US-ASCII");
				outputStream.writeInt(bytes.length);
				for (int i=0;i<bytes.length;i++) {
					outputStream.writeInt(bytes[i]);
				}
				int pad=(-bytes.length)&3;
				while (pad>0) {
					outputStream.writeInt(0);
					pad--;
				}
				break;
            default:
                break;
			}
		}
	}

	protected void decodeProperties(XDRInputStream inputStream) throws IOException {
		try {
			while (true) {
				int id=inputStream.readInt();
				if(id==0) continue; // padding
				PropertyDescriptor descriptor=PropertyRegistry.getInstance().getByID(id);
				System.out.println(id+" "+(descriptor!=null ? descriptor.getPropertyName()+" ("+descriptor.getType()+")": "unknown"));
				if (descriptor==null) {
					//logger.warning("Unknown property id "+id+", skipping rest of properties: "+toString());
				    // unknown ID this is mostly an error in protocol. I think
					//id=inputStream.readInt();System.out.println(id);
					// inputStream.skip(0);
					continue;
				    //descriptor = new PropertyDescriptor(id, "unknown", PropertyType.INT);
				}
				logger.finer("Reading property "+descriptor.getPropertyID()+", name="+descriptor.getPropertyName());

				Object value=null;
				
				switch (descriptor.getType()) {
				case INT:
					value=new Integer(inputStream.readInt());
					break;
				case BOOL:
					value=new Boolean(inputStream.readInt()!=0);
					break;
				case LONG:
					value=new Long(inputStream.readInt());
					break;
				case FLOAT:
					value=new Float(inputStream.readFloat());
					break;
				case DOUBLE:
					value=new Double(inputStream.readFloat());
					break;
				case STRING:
				case UNSPECIFIED:
					int length=inputStream.readInt();
					byte bytes[]=new byte[length];
					for (int i=0;i<length;i++) {
						bytes[i]=(byte)inputStream.readInt();
					}
					inputStream.skip(4*((-length)&3));
					value=new String(bytes,"US-ASCII");
					break;
                default:
                    break;
				}
				
				if (value!=null) {
					properties.put(descriptor.getPropertyName(), value);
					
				}
			}
		} catch (EOFException e) {
		}
	}

	@Override
	public String toString() {
		return "model="+model+" position="+position+" orientation="+orientation+" linearVelocity="+linearVelocity;
	}
}
