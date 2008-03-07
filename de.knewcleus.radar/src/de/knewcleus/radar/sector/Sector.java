package de.knewcleus.radar.sector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.AirwayDB;
import de.knewcleus.fgfs.navaids.DBParserException;
import de.knewcleus.fgfs.navaids.INavaidDatabase;
import de.knewcleus.fgfs.navaids.NamedFixDB;
import de.knewcleus.fgfs.navaids.xplane.AerodromeParser;
import de.knewcleus.fgfs.navaids.xplane.AirwayParser;
import de.knewcleus.fgfs.navaids.xplane.FixParser;
import de.knewcleus.fgfs.navaids.xplane.NavParser;

public class Sector implements INavaidDatabase {
	protected static Logger logger=Logger.getLogger(Sector.class.getName());
	protected final Position initialCenter;
	protected final double latRange;
	protected final double lonRange;
	protected List<Polygon> landmassPolygons=new ArrayList<Polygon>();
	protected List<Polygon> waterPolygons=new ArrayList<Polygon>();
	protected List<Polygon> restrictedPolygons=new ArrayList<Polygon>();
	protected List<Polygon> sectorPolygons=new ArrayList<Polygon>();
	protected final NamedFixDB fixDatabase=new NamedFixDB();
	protected final AirwayDB airwayDatabase=new AirwayDB();
	
	public Sector(Position initialCenter, double latRange, double lonRange) {
		this.initialCenter=initialCenter;
		this.latRange=latRange;
		this.lonRange=lonRange;
	}
	
	public static Sector loadFromURL(URL url) throws IOException, ParserConfigurationException, SAXException, DBParserException {
		InputStream inputStream=url.openStream();
		
		DocumentBuilderFactory documentBuilderFactory=DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setCoalescing(true);
		documentBuilderFactory.setExpandEntityReferences(true);
		documentBuilderFactory.setIgnoringComments(true);
		DocumentBuilder documentBuilder=documentBuilderFactory.newDocumentBuilder();
		
		Document document=documentBuilder.parse(inputStream);
		
		double initialLat=0.0;
		double initialLon=0.0;
		double initialElev=0.0;
		double latRange=3.0;
		double lonRange=3.0;
		
		NodeList initialCenterNodes=document.getElementsByTagName("initialcenter");
		Element initialCenterElem=(Element)initialCenterNodes.item(0);
		
		initialLat=Double.parseDouble(initialCenterElem.getAttribute("lat"))*Units.DEG;
		initialLon=Double.parseDouble(initialCenterElem.getAttribute("lon"))*Units.DEG;
		initialElev=Double.parseDouble(initialCenterElem.getAttribute("elev"))*Units.FT;
		
		NodeList mapRangeNodes=document.getElementsByTagName("maprange");
		Element mapRangeElem=(Element)mapRangeNodes.item(0);
		
		lonRange=Double.parseDouble(mapRangeElem.getAttribute("x"))*Units.DEG;
		latRange=Double.parseDouble(mapRangeElem.getAttribute("y"))*Units.DEG;
		
		Sector sector=new Sector(new Position(initialLon,initialLat,initialElev),latRange,lonRange);
		
		double north,west,south,east;
		
		north=initialLat+latRange/2.0;
		west=initialLon-latRange/2.0;
		south=initialLat-latRange/2.0;
		east=initialLon+latRange/2.0;
		
		NodeList geodataNodes=document.getElementsByTagName("geodata");
		
		for (int i=0;i<geodataNodes.getLength();i++) {
			Element geodataElem=(Element)geodataNodes.item(i);
			String type=geodataElem.getAttribute("type");
			String ref=geodataElem.getAttribute("ref");
			URL location=new URL(url,ref);
			
			long startTime=System.currentTimeMillis();
			if (type.equals("xplane_fixes")) {
				FixParser fixParser=new FixParser(sector.getFixDB(),north,west,south,east);
				InputStream geoStream=location.openStream();
				fixParser.readCompressed(geoStream);
			} else if (type.equals("xplane_apts")) {
				AerodromeParser aerodromeParser=new AerodromeParser(sector.getFixDB(),north,west,south,east);
				InputStream geoStream=location.openStream();
				aerodromeParser.readCompressed(geoStream);
			} else if (type.equals("xplane_nav")) {
				NavParser navParser=new NavParser(sector.getFixDB(),north,west,south,east);
				InputStream geoStream=location.openStream();
				navParser.readCompressed(geoStream);
			} else if (type.equals("xplane_awy")) {
				AirwayParser airwayParser=new AirwayParser(sector.getAirwayDB(),north,west,south,east);
				InputStream geoStream=location.openStream();
				airwayParser.readCompressed(geoStream);
			} else if (type.equals("poly_landmass")) {
				PolyReader polyReader=new PolyReader();
				InputStream geoStream=location.openStream();
				polyReader.readPolygons(geoStream, sector.getLandmassPolygons());
			} else if (type.equals("poly_water")) {
				PolyReader polyReader=new PolyReader();
				InputStream geoStream=location.openStream();
				polyReader.readPolygons(geoStream, sector.getWaterPolygons());
			} else if (type.equals("poly_restricted")) {
				PolyReader polyReader=new PolyReader();
				InputStream geoStream=location.openStream();
				polyReader.readPolygons(geoStream, sector.getRestrictedPolygons());
			} else if (type.equals("poly_sector")) {
				PolyReader polyReader=new PolyReader();
				InputStream geoStream=location.openStream();
				polyReader.readPolygons(geoStream, sector.getSectorPolygons());
			} else if (type.equals("point_fixes")) {
				PointReader pointReader=new PointFixReader();
				InputStream geoStream=location.openStream();
				pointReader.readPoints(sector.getFixDB(), geoStream);
			} else {
				logger.info("Unknown geotype "+type);
			}
			long endTime=System.currentTimeMillis();
			
			logger.info("Loading "+type+" at "+ref+" took "+(endTime-startTime)+" ms");
		}
		
		return sector;
	}
	
	public Position getInitialCenter() {
		return initialCenter;
	}
	
	public double getLatRange() {
		return latRange;
	}
	
	public double getLonRange() {
		return lonRange;
	}
	
	public double getNorthBorder() {
		return initialCenter.getY()+latRange/2.0;
	}
	
	public double getSouthBorder() {
		return initialCenter.getY()-latRange/2.0;
	}
	
	public double getEastBorder() {
		return initialCenter.getX()+lonRange/2.0;
	}
	
	public double getWestBorder() {
		return initialCenter.getX()-lonRange/2.0;
	}
	
	public List<Polygon> getLandmassPolygons() {
		return landmassPolygons;
	}
	
	public List<Polygon> getWaterPolygons() {
		return waterPolygons;
	}
	
	public List<Polygon> getRestrictedPolygons() {
		return restrictedPolygons;
	}
	
	public List<Polygon> getSectorPolygons() {
		return sectorPolygons;
	}
	
	public AirwayDB getAirwayDB() {
		return airwayDatabase;
	}
	
	public NamedFixDB getFixDB() {
		return fixDatabase;
	}
}
