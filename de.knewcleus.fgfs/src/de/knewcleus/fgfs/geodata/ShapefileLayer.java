package de.knewcleus.fgfs.geodata;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ShapefileLayer implements Layer {
	protected final SHPFileReader shpFileReader;
	protected final DBFFileReader dbfFileReader;
	protected final FeatureDefinition featureDefinition;
	protected final int recordCount;
	protected int nextFeatureID=0;
	
	public ShapefileLayer(URL datasource, String layer) throws DataFormatException, IOException {
		final URL shapeFileURL=new URL(datasource, layer+".shp");
		final URL dbfFileURL=new URL(datasource, layer+".dbf");
		final URL indexFileURL=new URL(datasource, layer+".shx");
		
		final InputStream shapeFileStream=shapeFileURL.openStream();
		shpFileReader=new SHPFileReader(shapeFileStream);
		final InputStream dbfFileStream=dbfFileURL.openStream();
		DBFFileReader dbfReader;
		try {
			dbfReader=new DBFFileReader(dbfFileStream);
		} catch (IOException e) {
			dbfReader=null;
		}
		dbfFileReader=dbfReader;
		if (dbfFileReader!=null) {
			recordCount=dbfFileReader.getRecordCount();
		} else {
			int indexRecordCount=-1;
			try {
				final InputStream indexFileStream=indexFileURL.openStream();
				final DataInputStream indexFileDataStream=new DataInputStream(indexFileStream);
				indexFileDataStream.skipBytes(24);
				final int indexFileLength=LittleEndianHelper.readInt(indexFileDataStream);
				indexRecordCount=(indexFileLength-100)/8;
			} catch (IOException e) {
				// Ignore
			}
			recordCount=indexRecordCount;
		}
		FieldDescriptor[] fieldDescriptors;
		if (dbfFileReader!=null) {
			fieldDescriptors=dbfFileReader.getFieldDescriptors();
		} else {
			fieldDescriptors=new FieldDescriptor[0];
		}
		
		featureDefinition=new FeatureDefinition(fieldDescriptors);
	}
	
	public ShapefileLayer(File datasource, String layer) throws DataFormatException, IOException {
		this(datasource.toURI().toURL(), layer);
	}
	
	public int getRecordCount() {
		return recordCount;
	}
	
	public FeatureDefinition getFeatureDefinition() {
		return featureDefinition;
	}
	
	public Feature getNextFeature() throws IOException, DataFormatException {
		final Geometry geometry=shpFileReader.readRecord();
		if (geometry==null)
			return null;
		final int featureID=shpFileReader.getLastFeatureID();
		final Object[] fields;
		if (dbfFileReader!=null) {
			fields=dbfFileReader.readRow(featureID-1);
		} else {
			fields=new Object[0];
		}
		return new Feature(featureDefinition, featureID, geometry, fields);
	}
}
