package de.knewcleus.openradar.sector;

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
import de.knewcleus.fgfs.geodata.DataFormatException;
import de.knewcleus.fgfs.geodata.Geometry;
import de.knewcleus.fgfs.geodata.PolyReader;
import de.knewcleus.fgfs.geodata.Polygon;
import de.knewcleus.fgfs.geodata.SHPFileReader;
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
	protected final int defaultXRange;
	protected List<Polygon> landmassPolygons=new ArrayList<Polygon>();
	protected List<Polygon> waterPolygons=new ArrayList<Polygon>();
	protected List<Polygon> restrictedPolygons=new ArrayList<Polygon>();
	protected List<Polygon> sectorPolygons=new ArrayList<Polygon>();
	protected List<Polygon> pavementPolygons=new ArrayList<Polygon>();
	protected final NamedFixDB fixDatabase=new NamedFixDB();
	protected final AirwayDB airwayDatabase=new AirwayDB();
	
	public Sector(Position initialCenter, double latRange, double lonRange, int defaultXRange) {
		this.initialCenter=initialCenter;
		this.latRange=latRange;
		this.lonRange=lonRange;
		this.defaultXRange=defaultXRange;
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
		int defaultXRange=30;
		
		NodeList initialCenterNodes=document.getElementsByTagName("initialcenter");
		Element initialCenterElem=(Element)initialCenterNodes.item(0);
		
		initialLat=Double.parseDouble(initialCenterElem.getAttribute("lat"))*Units.DEG;
		initialLon=Double.parseDouble(initialCenterElem.getAttribute("lon"))*Units.DEG;
		initialElev=Double.parseDouble(initialCenterElem.getAttribute("elev"))*Units.FT;
		
		if (initialCenterElem.hasAttribute("xrange_nm")) {
			defaultXRange=Integer.parseInt(initialCenterElem.getAttribute("xrange_nm"));
		}
		
		NodeList mapRangeNodes=document.getElementsByTagName("maprange");
		Element mapRangeElem=(Element)mapRangeNodes.item(0);
		
		lonRange=Double.parseDouble(mapRangeElem.getAttribute("x"))*Units.DEG;
		latRange=Double.parseDouble(mapRangeElem.getAttribute("y"))*Units.DEG;
		
		Sector sector=new Sector(new Position(initialLon,initialLat,initialElev),latRange,lonRange, defaultXRange);
		
		double north,west,south,east;
		
		north=initialLat+latRange/2.0;
		west=initialLon-latRange/2.0;
		south=initialLat-latRange/2.0;
		east=initialLon+latRange/2.0;
		
		NodeList geodataNodes=document.getElementsByTagName("geodata");
		
		for (int i=0;i<geodataNodes.getLength();i++) {
			Element geodataElem=(Element)geodataNodes.item(i);
			final String theme=geodataElem.getAttribute("theme");
			final String type=geodataElem.getAttribute("type");
			final String ref=geodataElem.getAttribute("ref");
			URL location=new URL(url,ref);
			
			long startTime=System.currentTimeMillis();
			if (theme.equals("fixes")) {
				sector.readFixes(location,type, north, west, south, east);
			} else if (theme.equals("apts")) {
				sector.readAirports(location, type, north, west, south, east);
			} else if (theme.equals("nav")) {
				sector.readNavaids(location, type, north, west, south, east);
			} else if (theme.equals("awy")) {
				sector.readAirways(location, type, north, west, south, east);
			} else if (theme.equals("landmass")) {
				sector.readPolygons(location, type, north, west, south, east, sector.getLandmassPolygons());
			} else if (theme.equals("water")) {
				sector.readPolygons(location, type, north, west, south, east, sector.getWaterPolygons());
			} else if (theme.equals("restricted")) {
				sector.readPolygons(location, type, north, west, south, east, sector.getRestrictedPolygons());
			} else if (theme.equals("sector")) {
				sector.readPolygons(location, type, north, west, south, east, sector.getSectorPolygons());
			} else if (theme.equals("pavement")) {
				sector.readPolygons(location, type, north, west, south, east, sector.getPavementPolygons());
			} else {
				logger.info("Unknown theme "+theme);
			}
			long endTime=System.currentTimeMillis();
			
			logger.info("Loading "+type+" at "+ref+" took "+(endTime-startTime)+" ms");
		}
		
		return sector;
	}
	
	protected void readFixes(URL location, String type, double north, double west, double south, double east) throws IOException, DBParserException {
		if (type.equals("xplane")) {
			FixParser fixParser=new FixParser(getFixDB(),north,west,south,east);
			InputStream geoStream=location.openStream();
			fixParser.readCompressed(geoStream);
		} else if (type.equals("point")) {
			PointReader pointReader=new PointFixReader();
			InputStream geoStream=location.openStream();
			pointReader.readPoints(getFixDB(), geoStream);
		} else {
			logger.info("Unknown type "+type+" for theme 'fixes'");
		}
	}
	
	protected void readAirports(URL location, String type, double north, double west, double south, double east) throws IOException, DBParserException {
		if (type.equals("xplane")) {
			AerodromeParser aerodromeParser=new AerodromeParser(getFixDB(),north,west,south,east);
			InputStream geoStream=location.openStream();
			aerodromeParser.readCompressed(geoStream);
		} else {
			logger.info("Unknown type "+type+" for theme 'apts'");
		}
	}
	
	protected void readNavaids(URL location, String type, double north, double west, double south, double east) throws IOException, DBParserException {
		if (type.equals("xplane")) {
			NavParser navParser=new NavParser(getFixDB(),north,west,south,east);
			InputStream geoStream=location.openStream();
			navParser.readCompressed(geoStream);
		} else {
			logger.info("Unknown type "+type+" for theme 'nav'");
		}
	}
	
	protected void readAirways(URL location, String type, double north, double west, double south, double east) throws IOException, DBParserException {
		if (type.equals("xplane")) {
			AirwayParser airwayParser=new AirwayParser(getAirwayDB(),north,west,south,east);
			InputStream geoStream=location.openStream();
			airwayParser.readCompressed(geoStream);
		} else {
			logger.info("Unknown type "+type+" for theme 'awy'");
		}
	}
	
	protected void readPolygons(URL location, String type, double north, double west, double south, double east, List<Polygon> polygons) throws DBParserException, IOException {
		if (type.equals("poly")) {
			PolyReader polyReader=new PolyReader();
			InputStream geoStream=location.openStream();
			polyReader.readPolygons(geoStream, polygons);
		} else if (type.equals("shape")) {
			try {
				SHPFileReader shapefileReader=new SHPFileReader(location);
				Geometry geometry;
				while (true) {
					try {
						geometry=shapefileReader.readRecord();
						if (geometry instanceof Polygon) {
							polygons.add((Polygon)geometry);
						}
					} catch (IOException e) {
						break;
					}
				}
			} catch (DataFormatException e) {
				throw new DBParserException(e);
			}
		} else {
			logger.info("Unknown type "+type+" for polygon theme");
		}
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
	
	public int getDefaultXRange() {
		return defaultXRange;
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
	
	public List<Polygon> getPavementPolygons() {
		return pavementPolygons;
	}
	
	public AirwayDB getAirwayDB() {
		return airwayDatabase;
	}
	
	public NamedFixDB getFixDB() {
		return fixDatabase;
	}
}
