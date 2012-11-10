package de.knewcleus.fgfs.geodata.shapefile;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
	protected final SHPFileReader shpFileReader;
	protected final DBFFileReader dbfFileReader;
	protected final FeatureDefinition featureDefinition;
	protected final int recordCount;
	protected int nextFeatureID=0;

	protected ZipFile zipArchive = null;
	
	public ZippedShapefileLayer(String airportDir, String layer) throws GeodataException {
		try {
		    final String archiveName = airportDir+ layer+".zip";
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
			throw new GeodataException(e);
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
