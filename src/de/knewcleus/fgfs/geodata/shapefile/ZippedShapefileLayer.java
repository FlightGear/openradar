/**
 * Copyright (C) 2008-2009 Ralf Gerlich
 * Copyright (C) 2012,2013,2015 Wolfram Wagner
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.knewcleus.fgfs.geodata.DataFormatException;
import de.knewcleus.fgfs.geodata.Feature;
import de.knewcleus.fgfs.geodata.FeatureDefinition;
import de.knewcleus.fgfs.geodata.FieldDescriptor;
import de.knewcleus.fgfs.geodata.GeodataException;
import de.knewcleus.fgfs.geodata.IDatabaseRow;
import de.knewcleus.fgfs.geodata.IGeodataLayer;
import de.knewcleus.fgfs.geodata.geometry.Geometry;
/**
 * This class reads the shapefiles directly out of the zip files, delivered by the landcover database.
 * Bases on ShapefileLayer.
 *
 * @author Wolfram Wagner
 *
 */
public class ZippedShapefileLayer implements IGeodataLayer {
	protected SHPFileReader shpFileReader;
	protected DBFFileReader dbfFileReader;
	protected FeatureDefinition featureDefinition;
	protected int recordCount;
	protected int nextFeatureID=0;

	protected ZipFile zipArchive = null;

	private final static Logger log = LogManager.getLogger(ZippedShapefileLayer.class);
	
	public ZippedShapefileLayer(String airportDir, String layer) throws GeodataException {
		try {
	        String archiveName = airportDir+ layer+".zip";
			final String nameSHP = layer+".shp";
			final String nameDBF = layer+".dbf";
			final String nameSHX = layer+".shx";

			final InputStream shapeFileStream=getZipArchiveFileInputStream(archiveName, nameSHP);
			shpFileReader=new SHPFileReader(shapeFileStream);
			final InputStream dbfFileStream=getZipArchiveFileInputStream(archiveName, nameDBF);
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
					final InputStream indexFileStream=getZipArchiveFileInputStream(archiveName, nameSHX);
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
		} catch (IOException e) {
			log.warn("Problems to read "+airportDir+ layer+".zip"+": "+e.getMessage());
			shpFileReader=null;
			dbfFileReader=null;
			featureDefinition=null;
			recordCount=0;
		} catch (DataFormatException e) {
			throw new GeodataException(e);
		}
	}

	public int getRecordCount() {
		return recordCount;
	}

	public FeatureDefinition getFeatureDefinition() {
		return featureDefinition;
	}

	public Feature getNextFeature() throws GeodataException {
	    if(shpFileReader==null) return null;

		final Geometry geometry;
		try {
			geometry=shpFileReader.readRecord();
		} catch (IOException e) {
			throw new GeodataException(e);
		} catch (DataFormatException e) {
			throw new GeodataException(e);
		}
		if (geometry==null)
			return null;
		final int featureID=shpFileReader.getLastFeatureID();
		final IDatabaseRow row;
		if (dbfFileReader!=null) {
			try {
				row=dbfFileReader.readRow(featureID-1);
			} catch (IOException e) {
				throw new GeodataException(e);
			}
		} else {
			row=null;
		}
		return new Feature(featureDefinition, featureID, geometry, row);
	}

    protected InputStream getZipArchiveFileInputStream(String archive, String filename) throws IOException {
        final File inputFile = new File(archive);
        zipArchive = new ZipFile(inputFile);
        Enumeration<? extends ZipEntry> entries = zipArchive.entries();
        while(entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            if(zipEntry.getName().equals(filename)) {
                return zipArchive.getInputStream(zipEntry);
            }
        }
        throw new IllegalStateException(filename+" not found in "+archive);
    }

    public void closeZipArchive() {
        if(zipArchive!=null) {
            try {
                zipArchive.close();
            } catch (IOException e) {
                log.error("Error while closing zip archive!",e);
            }
        }
    }

    @Override
    public boolean hasNext() {
        return shpFileReader!=null && shpFileReader.getLastFeatureID()<recordCount;
    }
}
