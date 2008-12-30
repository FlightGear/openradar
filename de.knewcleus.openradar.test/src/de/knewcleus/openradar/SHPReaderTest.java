package de.knewcleus.openradar;

import java.io.File;
import java.io.IOException;

import de.knewcleus.fgfs.geodata.DataFormatException;
import de.knewcleus.fgfs.geodata.FeatureDefinition;
import de.knewcleus.fgfs.geodata.FieldDescriptor;
import de.knewcleus.fgfs.geodata.shapefile.ShapefileLayer;

public class SHPReaderTest {
	public static void main(String[] args) throws IOException, DataFormatException {
		final String datasource=args[0];
		final String layer=args[1];
		final ShapefileLayer shapefileLayer=new ShapefileLayer(new File(datasource), layer);
		
		final FeatureDefinition featureDefinition=shapefileLayer.getFeatureDefinition();
		
		for (int i=0;i<featureDefinition.getColumnCount();i++) {
			final FieldDescriptor fieldDescriptor=featureDefinition.getFieldDescriptors()[i];
			System.out.println("Field "+i+":"+fieldDescriptor.getName()+"; "+fieldDescriptor.getType());
		}
		
		int recordCount=0;
		long startMillis=System.currentTimeMillis();
		while (shapefileLayer.getNextFeature()!=null) {
			recordCount++;
		}
		long endMillis=System.currentTimeMillis();
		
		System.out.println("Time for "+recordCount+" records:"+(endMillis-startMillis)+"ms");
	}

}
