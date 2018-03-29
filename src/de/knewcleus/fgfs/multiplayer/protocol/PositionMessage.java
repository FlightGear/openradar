/**
 * Copyright (C) 2008-2009 Ralf Gerlich
 * Copyright (C) 2012,2018 Wolfram Wagner
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

import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.multiplayer.MultiplayerException;
import de.knewcleus.fgfs.multiplayer.protocol.PropertyRegistry.DataType;

public class PositionMessage implements IMultiplayerMessage {
	protected static Logger log = LogManager.getLogger("de.knewcleus.fgfs.multiplayer");
	public static final int MAX_MODEL_NAME_LEN=96;
	public static final int MAX_PROPERTY_LEN=52;

	/**
	 * null old/not defined
	 * 0    detected old version (by initial padding
	 * 1    client runs in compatibility mode (sees all chats to old only)
	 * 2    client runs 2017.2 (new with smaller transport data types 
	 */
	protected Integer protocolVersion=null;
	
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
		return MAX_MODEL_NAME_LEN+5*8+15*4+4+getPropertiesLength();
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

	public Integer getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(Integer protocolVersion) {
		this.protocolVersion = protocolVersion;
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
			MPUtils.writeCString(outputStream, model, MAX_MODEL_NAME_LEN); // 96 byte

			outputStream.writeDouble(time);  // 8 byte
			outputStream.writeDouble(lag);  // 8 byte
			MPUtils.writeDoublePosition(outputStream,position); //96 byte
			MPUtils.writeFloatPosition(outputStream,orientation); // 1st 4 byte
			MPUtils.writeFloatPosition(outputStream,linearVelocity); // 2nd 4 byte
			MPUtils.writeFloatPosition(outputStream,angularVelocity); // 3rd 4 byte
			MPUtils.writeFloatPosition(outputStream,linearAcceleration); // 4th 4 byte
			MPUtils.writeFloatPosition(outputStream,angularAcceleration); // 5th 4 byte
			// add 4 bytes padding are missing pad them
			outputStream.write(new byte[] {0,0,0,0}); // 6th 4 byte
			// => 200 byte
			encodeProperties(outputStream);
		} catch (IOException e) {
			throw new MultiplayerException(e);
		}
	}

	public void decode(XDRInputStream inputStream) throws MultiplayerException {
		try {
			model=MPUtils.readCString(inputStream, MAX_MODEL_NAME_LEN); // zero terminated char array 96 byte long?
			//System.out.println(model);
			time=inputStream.readDouble(); // 8 B
			lag=inputStream.readDouble(); // 8 B
            //System.out.println(time+" "+lag);
			position=MPUtils.readDoublePosition(inputStream); // 24 B
			orientation=MPUtils.readFloatPosition(inputStream); // 3x4 B
			linearVelocity=MPUtils.readFloatPosition(inputStream); // 3x4 B
			angularVelocity=MPUtils.readFloatPosition(inputStream); // 3x4 B
			linearAcceleration=MPUtils.readFloatPosition(inputStream); // 3x4 B
			angularAcceleration=MPUtils.readFloatPosition(inputStream); // 3x4 B
			inputStream.skipBytes(4);
			
			decodeProperties(inputStream);
		} catch (IOException e) {
			throw new MultiplayerException(e);
		}
	}

	protected void decodeProperties(XDRInputStream inputStream) throws IOException {
		boolean firstLoop= true;
		try {
			while (true) {

				int id;
				// initial protocol recognition: in new format the ids are shorts, before they have been stored as int
				if(firstLoop) {
					inputStream.mark(4);
					id=inputStream.readShort();
					if(id==10) {
						setProtocolVersion(2); // recognized new client
					} else {
						setProtocolVersion(0); // recognized old client
					}
					firstLoop=false;
					inputStream.reset();
				}	
				
				if(isProtocolBefore2017_2()) {
					id=inputStream.readInt();
				} else {
					id=inputStream.readShort();
				}
				
				PropertyDescriptor descriptor=PropertyRegistry.getInstance().getByID(id);
				//System.out.println(id+" "+(descriptor!=null ? descriptor.getPropertyName()+" ("+descriptor.getType()+")": "unknown"));
				if (descriptor==null) {
					//log.warn("Unknown property id "+id+", skipping rest of properties: "+toString());
				    // unknown ID this is mostly an error in protocol. I think
					//id=inputStream.readInt();System.out.println(id);
					// inputStream.skip(0);
					//System.out.println(id);
					continue;
				    //descriptor = new PropertyDescriptor(id, "unknown", PropertyType.INT);
				}
				log.trace("Reading property "+descriptor.getPropertyID()+", name="+descriptor.getPropertyName());

				Object value=null;

				DataType type = descriptor.getTransType();
				if(isProtocolBefore2017_2()) {
					/** old versions used the full raw format to encode the properties */
					type = descriptor.getType();
				}
				
				switch (type) {
				case INT:
					value=new Integer(inputStream.readInt());
					break;
				case SHORTINT:
					if(descriptor.getType()==DataType.BOOL) {
						// in new world the transport type of bool is short
						value=new Boolean(inputStream.readShort()!=0);
					} else {
						value=new Integer(inputStream.readShort());
					}
					break;
				case BOOL:
					value=new Boolean(inputStream.readInt()!=0);
					break;
				case LONG: // not in use anymore, I think
					value=new Long(inputStream.readInt());
					break;
				case FLOAT:
					value=new Float(inputStream.readFloat());
					break;
				case SHORT_FLOAT_NORM: // -1 .. 1 encoded into a short int 
					value=new Float(inputStream.readShort()/32767f);
					break;
				case SHORT_FLOAT_1: // range -3276.7 .. 3276.7 float encoded into a short int (16 bit) 
					value=new Float(inputStream.readShort()/10f);
					break;
				case SHORT_FLOAT_2: // range -327.67 .. 327.67 float encoded into a short int (16 bit) 
					value=new Float(inputStream.readShort()/100f);
					break;
				case SHORT_FLOAT_3: // range -32.767 .. 32.767 float encoded into a short int (16 bit)
					value=new Float(inputStream.readShort()/1000f);
					break;
				case SHORT_FLOAT_4: // range -3.2767 .. 3.2767 float encoded into a short int (16 bit)
					value=new Float(inputStream.readShort()/10000f);
					break;
				case DOUBLE: // not in use anymore, I think
					value=new Double(inputStream.readFloat());
					break;
				case STRING:
				//case UNSPECIFIED:
					if(isProtocolBefore2017_2()) {
						// protocol < 2017.2
						int length=inputStream.readInt();
						byte bytes[]=new byte[length];
						for (int i=0;i<length;i++) {
							bytes[i]=(byte)inputStream.readInt();
						}
						// padding only for old and compatible version
						inputStream.skip(4*((-length)&3));
						value=new String(bytes,"US-ASCII");
					} else {
						// new protocol: 2 bytes len + char[len]
						int length=inputStream.readShort();
						byte bytes[]=new byte[length];
						inputStream.read(bytes);
						value=new String(bytes,"US-ASCII");
					}
					
					break;
                default:
                    break;
				}

				if(10==id) {
					// client tells us his protocol version => store it
					setProtocolVersion((Integer)value);
				}
				
				if (value!=null) {
					properties.put(descriptor.getPropertyName(), value);

				}
			}
		} catch (EOFException e) {
		}
	}

	protected void encodeProperties(XDROutputStream outputStream) throws IOException {
		for (Map.Entry<String, Object> entry: properties.entrySet()) {
			PropertyDescriptor descriptor=PropertyRegistry.getInstance().getByName(entry.getKey());
			if (descriptor==null) {
				log.warn("Skipping encoding of property "+entry.getKey());
				continue;
			}
			
			if(isProtocolBefore2017_2()) {
				outputStream.writeInt(descriptor.getPropertyID());
			} else {
				outputStream.writeShort(descriptor.getPropertyID());
			}

			DataType type = descriptor.getTransType();
			if(isProtocolBefore2017_2()) {
				/** old versions used the full raw format to encode the properties */
				type = descriptor.getType();
			}
			
			Number num;
			switch (type) {
			case SHORTINT:
				if(descriptor.getType()==DataType.BOOL) {
					outputStream.writeShort(((Boolean)entry.getValue())==true ? 0 : 1);
				} else {
					outputStream.writeShort(((Number)entry.getValue()).intValue());
				}
				break;
			case INT:
			case BOOL:
			case LONG:
				if (entry.getValue() instanceof Boolean) {
					Boolean bool=(Boolean)entry.getValue();
					num=new Integer(bool.booleanValue()?1:0);
					if(isProtocolBefore2017_2()) {
						outputStream.writeInt(num.intValue());
					} else {
						outputStream.writeShort(num.intValue());
					}
				} else {
					num=(Number)entry.getValue();
					outputStream.writeInt(num.intValue());
				}
				break;
			case SHORT_FLOAT_NORM: { // -1 .. 1 encoded into a short int
				num=(Number)entry.getValue();
				assert(num.floatValue()>=-1 && num.floatValue()<=1);
				int value = (short) Math.round(num.floatValue()*32767f);
				outputStream.writeShort(value);
				break;
			}
			case SHORT_FLOAT_1: { // range -3276.7 .. 3276.7 float encoded into a short int (16 bit) 
				num=(Number)entry.getValue();
				assert(num.floatValue()>=-3276.7 && num.floatValue()<=3276.7);
				int value = (short) Math.round(num.floatValue()*10f);
				outputStream.writeShort(value);
				break;
			}
			case SHORT_FLOAT_2: { // range -327.67 .. 327.67 float encoded into a short int (16 bit) 
				num=(Number)entry.getValue();
				assert(num.floatValue()>=-327.67 && num.floatValue()<=327.67);
				int value = (short) Math.round(num.floatValue()*100f);
				outputStream.writeShort(value);
				break;
			}
			case SHORT_FLOAT_3: { // range -32.767 .. 32.767 float encoded into a short int (16 bit)
				num=(Number)entry.getValue();
				assert(num.floatValue()>=-32.767 && num.floatValue()<=32.767);
				int value = (short) Math.round(num.floatValue()*1000f);
				outputStream.writeShort(value);
				break;
			}
			case SHORT_FLOAT_4: {// range -3.2767 .. 3.2767 float encoded into a short int (16 bit)
				num=(Number)entry.getValue();
				assert(num.floatValue()>=-3.2767 && num.floatValue()<=3.2767);
				int value = (short) Math.round(num.floatValue()*10000f);
				outputStream.writeShort(value);
				break;
			}
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
				if(isProtocolBefore2017_2()) {
					byte bytes[]=str.getBytes("US-ASCII");
					outputStream.writeInt(bytes.length);
					for (int i=0;i<bytes.length;i++) {
						outputStream.writeInt(bytes[i]);
					}
					// padding only for old and compatible version
					int pad=(-bytes.length)&3;
					while (pad>0) {
						outputStream.writeInt(0);
						pad--;
					}
				} else {
					// new protocol
					byte bytes[]=str.getBytes("US-ASCII");
					outputStream.writeInt(bytes.length);
					for (int i=0;i<bytes.length;i++) {
						outputStream.write(bytes[i]);
					}
				}
				break;
            default:
                break;
			}
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
			
			DataType type = descriptor.getTransType();
			if(isProtocolBefore2017_2()) {
				/** old versions used the full raw format to encode the properties */
				type = descriptor.getType();
			}
			switch (type) {
				case SHORTINT:
				case SHORT_FLOAT_NORM:
				case SHORT_FLOAT_1:
				case SHORT_FLOAT_2:
				case SHORT_FLOAT_3:
				case SHORT_FLOAT_4:
					length+=2;
					break;
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
					int strlen;
					if(isProtocolBefore2017_2()) {
						// padding only for old and compatible version
						int pad=(-bytes.length)&3;
						strlen=4+4*(bytes.length+pad);
					} else {
						strlen=4+4*(bytes.length);
					}
					length+=strlen;
					break;
	            default:
	                break;
			}
		}

		return length;
	}

	private boolean isProtocolBefore2017_2() {
		return protocolVersion==null || protocolVersion<2;
	}

	@Override
	public String toString() {
		return "model="+model+" position="+position+" orientation="+orientation+" linearVelocity="+linearVelocity;
	}
}
