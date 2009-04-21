package de.knewcleus.openradar.sector.test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.knewcleus.openradar.sector.MapLayer;
import de.knewcleus.openradar.sector.Sector;
import de.knewcleus.openradar.sector.SectorFactory;
import de.knewcleus.openradar.sector.SectorParser;
import de.knewcleus.openradar.sector.SectorParserException;

public class SectorTest {

	/**
	 * @param args
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws SectorParserException 
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, SectorParserException {
		final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		
		final Document document = docBuilder.parse(args[0]);
		final URL baseURL=new URL("file://"+args[0]);
		final SectorParser sectorParser = new SectorParser(SectorFactory.instance, baseURL);
		final Sector sector = sectorParser.parseSector(document.getDocumentElement());
		System.out.println("Name:"+sector.getName());
		System.out.println("Description:"+sector.getDescription());
		System.out.println("Bounds:"+sector.getBounds());
		System.out.println("Landmass");
		printMapLayers(sector.getTopology().getLandmass());
		System.out.println("Watermass");
		printMapLayers(sector.getTopology().getWatermass());
		System.out.println("Runways");
		printMapLayers(sector.getTopology().getRunways());
		System.out.println("Tarmac");
		printMapLayers(sector.getTopology().getTarmac());
	}
	
	protected static void printMapLayers(List<MapLayer> mapLayers) {
		for (MapLayer layer: mapLayers) {
			System.out.println(layer);
		}
	}

}
