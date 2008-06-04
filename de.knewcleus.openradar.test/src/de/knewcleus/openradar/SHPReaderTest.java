package de.knewcleus.openradar;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import de.knewcleus.fgfs.geodata.DataFormatException;
import de.knewcleus.fgfs.geodata.Feature;
import de.knewcleus.fgfs.geodata.FeatureDefinition;
import de.knewcleus.fgfs.geodata.FieldDescriptor;
import de.knewcleus.fgfs.geodata.ShapefileLayer;

public class SHPReaderTest {
	public static void main(String[] args) throws IOException, DataFormatException {
		final String datasource=args[0];
		final String layer=args[1];
		final ShapefileLayer shapefileLayer=new ShapefileLayer(new File(datasource), layer);
		Feature feature;
		
		final FeatureDefinition featureDefinition=shapefileLayer.getFeatureDefinition();
		
		for (int i=0;i<featureDefinition.getColumnCount();i++) {
			final FieldDescriptor fieldDescriptor=featureDefinition.getFieldDescriptors()[i];
			System.out.println("Field "+i+":"+fieldDescriptor.getName()+"; "+fieldDescriptor.getType());
		}
		
		while ((feature=shapefileLayer.getNextFeature())!=null) {
			System.out.println(feature.getGeometry()+", "+Arrays.toString(feature.getFields()));
		}
	}

}
