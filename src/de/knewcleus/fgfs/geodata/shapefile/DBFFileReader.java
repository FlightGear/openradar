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
package de.knewcleus.fgfs.geodata.shapefile;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

import de.knewcleus.fgfs.geodata.DataFormatException;
import de.knewcleus.fgfs.geodata.FieldDescriptor;
import de.knewcleus.fgfs.geodata.FieldType;
import de.knewcleus.fgfs.geodata.IDatabaseRow;

public class DBFFileReader {
	protected final DataInputStream dbfInputStream;

	protected final byte versionNumber;
	protected final Date lastChangeDate;
	protected final int recordCount;
	protected final short headerLength;
	protected final short recordLength;
	protected final byte languageDriver;
	protected final int columnCount;
	protected final DBFFieldDescriptor fieldDescriptors[];
	protected final Charset charset=Charset.forName("US-ASCII");
	protected int currentRow=0;

	public DBFFileReader(InputStream inputStream) throws IOException, DataFormatException {
		this.dbfInputStream=new DataInputStream(inputStream);

		/* Read the header */
		versionNumber=dbfInputStream.readByte();

		final byte lastChangeYear, lastChangeMonth, lastChangeDay;

		lastChangeYear=dbfInputStream.readByte();
		lastChangeMonth=dbfInputStream.readByte();
		lastChangeDay=dbfInputStream.readByte();

		final Calendar calendar=Calendar.getInstance();
		calendar.set(lastChangeYear+1900, lastChangeMonth-1, lastChangeDay);
		lastChangeDate=new Date(calendar.getTimeInMillis());

		recordCount=LittleEndianHelper.readInt(dbfInputStream);
		headerLength=LittleEndianHelper.readShort(dbfInputStream);
		recordLength=LittleEndianHelper.readShort(dbfInputStream);
		dbfInputStream.skipBytes(17);
		languageDriver=dbfInputStream.readByte();
		dbfInputStream.skipBytes(2);

		/* Read the record definition */
		final int recordDefinitionLength=headerLength-33;
		columnCount=recordDefinitionLength/32;
		fieldDescriptors=new DBFFieldDescriptor[columnCount];
		final byte nameBuffer[]=new byte[11];

		int fieldOffset=1; /* Include deletion flag */
		for (int col=0;col<columnCount;col++) {
			dbfInputStream.read(nameBuffer);
			int nameLength;
			for (nameLength=0;nameLength<nameBuffer.length;nameLength++) {
				if (nameBuffer[nameLength]==0)
					break;
			}
			final String fieldName=new String(nameBuffer, 0, nameLength, charset);
			final char fieldType=(char) dbfInputStream.readByte();
			dbfInputStream.skipBytes(4);
			final int fieldLength;
			final int decimalCount;
			if (fieldType=='N' || fieldType=='F' || fieldType=='I' || fieldType=='O') {
				fieldLength=dbfInputStream.readByte();
				decimalCount=dbfInputStream.readByte();
			} else {
				fieldLength=LittleEndianHelper.readShort(dbfInputStream);
				decimalCount=0;
			}
			dbfInputStream.skipBytes(14);
			fieldDescriptors[col]=new DBFFieldDescriptor(fieldName, fieldType, fieldOffset, fieldLength, decimalCount);
			fieldOffset+=fieldLength;
		}
		dbfInputStream.skipBytes(1);
		if (fieldOffset!=recordLength) {
			throw new DataFormatException("Invalid record length: header says "+recordLength+", definitions say "+fieldOffset);
		}
	}

	public Object parseField(byte[] recordData, DBFFieldDescriptor fieldDescriptor) {
		final int offset=fieldDescriptor.getFieldOffset();
		final int length=fieldDescriptor.getFieldLength();
		switch (fieldDescriptor.getFieldType()) {
		case 'C': {
			return new String(recordData, offset, length, charset);
		}
		case 'O':
		case 'N': {
			if (recordData[0]=='*') {
				return null;
			}
			final String text=new String(recordData, offset, length, charset);
			return new Double(Double.parseDouble(text));
		}
		case 'F': {
			if (recordData[0]=='*') {
				return null;
			}
			final String text=new String(recordData, offset, length, charset);
			return new Float(Float.parseFloat(text));
		}
		case 'I': {
			if (recordData[0]=='*') {
				return null;
			}
			final String text=new String(recordData, offset, length, charset);
			return new Integer(Integer.parseInt(text));
		}
		case 'L': {
			final int databyte=Character.toLowerCase(recordData[0]);
			if (databyte=='?') {
				return null;
			}
			return (databyte=='t' || databyte=='y');
		}
		default:
			return null;
		}
	}

	public IDatabaseRow readRow(int row) throws IOException {
		if (row<currentRow || row>=recordCount) {
			return null;
		}
		dbfInputStream.skipBytes(recordLength*(row-currentRow));
		currentRow=row;
		final byte[] recordData=new byte[recordLength];
		dbfInputStream.read(recordData);
		currentRow++;
		if (recordData[0]==0x2A) {
			return null;
		}
		return new DBFRow(recordData);
	}

	public FieldDescriptor[] getFieldDescriptors() {
		return fieldDescriptors;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public int getCurrentRow() {
		return currentRow;
	}

	public int getColumnCount() {
		return columnCount;
	}

	protected static FieldType convertDBFFieldType(char fieldType) {
		switch (fieldType) {
		case 'N': case 'O': case 'F':
			return FieldType.NUMBER;
		case 'I':
			return FieldType.INTEGER;
		case 'D':
			return FieldType.DATE;
		case 'L':
			return FieldType.LOGICAL;
		default:
			return FieldType.UNDEFINED;
		}
	}

	protected class DBFRow implements IDatabaseRow {
		protected final byte[] recordData;
		protected final Object[] fieldCache;

		public DBFRow(byte[] recordData) {
			this.recordData=recordData;
			fieldCache=new Object[columnCount];
		}

		@Override
		public Object getField(int index) {
			if (index<0 || index>=columnCount)
				return null;
			if (fieldCache[index]==null) {
				fieldCache[index]=parseField(recordData, fieldDescriptors[index]);
			}
			return fieldCache[index];
		}
	}

	protected static class DBFFieldDescriptor extends FieldDescriptor {
		protected final String fieldName;
		protected final char fieldType;
		protected final int fieldOffset;
		protected final int fieldLength;
		protected final int decimalCount;

		public DBFFieldDescriptor(String fieldName, char fieldType, int fieldOffset, int fieldLength, int decimalCount) {
			super(fieldName, convertDBFFieldType(fieldType));
			this.fieldName=fieldName;
			this.fieldOffset=fieldOffset;
			this.fieldType=fieldType;
			this.fieldLength=fieldLength;
			this.decimalCount=decimalCount;
		}

		public String getFieldName() {
			return fieldName;
		}

		public char getFieldType() {
			return fieldType;
		}

		public int getFieldOffset() {
			return fieldOffset;
		}

		public int getFieldLength() {
			return fieldLength;
		}

		public int getDecimalCount() {
			return decimalCount;
		}

		@Override
		public String toString() {
			return fieldName+":"+fieldType+"["+fieldLength+"], "+decimalCount;
		}
	}
}
