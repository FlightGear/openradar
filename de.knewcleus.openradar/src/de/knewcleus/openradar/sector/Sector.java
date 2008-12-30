package de.knewcleus.openradar.sector;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
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
import de.knewcleus.fgfs.geodata.Feature;
import de.knewcleus.fgfs.geodata.PolyReader;
import de.knewcleus.fgfs.geodata.geometry.Point;
import de.knewcleus.fgfs.geodata.geometry.Polygon;
import de.knewcleus.fgfs.geodata.shapefile.ShapefileLayer;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.Aerodrome;
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
	protected final Shape geographicBounds;
	protected final int defaultXRange;
	protected List<Polygon> landmassPolygons=new ArrayList<Polygon>();
	protected List<Polygon> waterPolygons=new ArrayList<Polygon>();
	protected List<Polygon> restrictedPolygons=new ArrayList<Polygon>();
	protected List<Polygon> sectorPolygons=new ArrayList<Polygon>();
	protected List<Polygon> pavementPolygons=new ArrayList<Polygon>();
	protected final NamedFixDB fixDatabase=new NamedFixDB();
	protected final AirwayDB airwayDatabase=new AirwayDB();
	
	public Sector(Position initialCenter, Shape geographicBounds, int defaultXRange) {
		this.initialCenter=initialCenter;
		this.geographicBounds=geographicBounds;
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
		int defaultXRange=30;

		NodeList mapRangeNodes=document.getElementsByTagName("maprange");
		Element mapRangeElem=(Element)mapRangeNodes.item(0);

		final Shape mapRange=parseMapRange(mapRangeElem);
		
		NodeList initialCenterNodes=document.getElementsByTagName("initialcenter");
		Element initialCenterElem=(Element)initialCenterNodes.item(0);
		
		initialLat=Double.parseDouble(initialCenterElem.getAttribute("lat"))*Units.DEG;
		initialLon=Double.parseDouble(initialCenterElem.getAttribute("lon"))*Units.DEG;
		initialElev=Double.parseDouble(initialCenterElem.getAttribute("elev"))*Units.FT;
		
		if (initialCenterElem.hasAttribute("xrange_nm")) {
			defaultXRange=Integer.parseInt(initialCenterElem.getAttribute("xrange_nm"));
		}
		
		final Position initialPosition=new Position(initialLon,initialLat,initialElev);
		Sector sector=new Sector(initialPosition, mapRange, defaultXRange);
		
		NodeList geodataNodes=document.getElementsByTagName("geodata");
		
		for (int i=0;i<geodataNodes.getLength();i++) {
			Element geodataElem=(Element)geodataNodes.item(i);
			final String theme=geodataElem.getAttribute("theme");
			final String type=geodataElem.getAttribute("type");
			final String ref=geodataElem.getAttribute("ref");
			
			long startTime=System.currentTimeMillis();
			if (theme.equals("fixes")) {
				sector.readFixes(url, geodataElem);
			} else if (theme.equals("apts")) {
				sector.readAirports(url, geodataElem);
			} else if (theme.equals("nav")) {
				sector.readNavaids(url, geodataElem);
			} else if (theme.equals("awy")) {
				sector.readAirways(url, geodataElem);
			} else if (theme.equals("landmass")) {
				sector.readPolygons(url, geodataElem, sector.getLandmassPolygons());
			} else if (theme.equals("water")) {
				sector.readPolygons(url, geodataElem, sector.getWaterPolygons());
			} else if (theme.equals("restricted")) {
				sector.readPolygons(url, geodataElem, sector.getRestrictedPolygons());
			} else if (theme.equals("sector")) {
				sector.readPolygons(url, geodataElem, sector.getSectorPolygons());
			} else if (theme.equals("pavement")) {
				sector.readPolygons(url, geodataElem, sector.getPavementPolygons());
			} else {
				logger.info("Unknown theme "+theme);
			}
			long endTime=System.currentTimeMillis();
			
			logger.info("Loading "+type+" at "+ref+" took "+(endTime-startTime)+" ms");
		}
		
		return sector;
	}
	
	protected static Shape parseMapRange(Element mapRangeElem) {
		final String northText=mapRangeElem.getAttribute("north");
		final String southText=mapRangeElem.getAttribute("south");
		final String eastText=mapRangeElem.getAttribute("east");
		final String westText=mapRangeElem.getAttribute("west");
		
		final double north=Double.parseDouble(northText);
		final double south=Double.parseDouble(southText);
		final double east=Double.parseDouble(eastText);
		final double west=Double.parseDouble(westText);
		
		return new Rectangle2D.Double(west, south, east-west, north-south);
	}
	
	protected void readFixes(URL context, Element geodataElem) throws IOException, DBParserException {
		final String type=geodataElem.getAttribute("type");
		if (type.equals("xplane")) {
			final String ref=geodataElem.getAttribute("ref");
			final URL location=new URL(context, ref);
			FixParser fixParser=new FixParser(getFixDB(), geographicBounds);
			InputStream geoStream=location.openStream();
			fixParser.readCompressed(geoStream);
		} else if (type.equals("point")) {
			final String ref=geodataElem.getAttribute("ref");
			final URL location=new URL(context, ref);
			PointReader pointReader=new PointFixReader();
			InputStream geoStream=location.openStream();
			pointReader.readPoints(getFixDB(), geoStream);
		} else {
			logger.info("Unknown type "+type+" for theme 'fixes'");
		}
	}
	
	protected void readAirports(URL context, Element geodataElem) throws IOException, DBParserException {
		final String type=geodataElem.getAttribute("type");
		if (type.equals("xplane")) {
			final String ref=geodataElem.getAttribute("ref");
			final URL location=new URL(context, ref);
			AerodromeParser aerodromeParser=new AerodromeParser(getFixDB(), geographicBounds);
			InputStream geoStream=location.openStream();
			aerodromeParser.readCompressed(geoStream);
		} else if (type.equals("shape")) {
			try {
				final String datasourceRef=geodataElem.getAttribute("datasource");
				final String layer=geodataElem.getAttribute("layer");
				final URL datasourceLocation=new URL(context, datasourceRef);
				final ShapefileLayer shapefileLayer=new ShapefileLayer(datasourceLocation, layer);
				final int icaoIndex=shapefileLayer.getFeatureDefinition().getColumnIndex("ICAO");
				if (icaoIndex==-1) {
					throw new DBParserException("Shapefile has no ICAO column");
				}
				while (true) {
					try {
						final Feature feature=shapefileLayer.getNextFeature();
						if (feature==null) {
							break;
						}
						if (feature.getGeometry() instanceof Point) {
							final String icaoID=(String)feature.getDatabaseRow().getField(icaoIndex);
							final Point arp=(Point)feature.getGeometry();
							// FIXME: Aerodrome name is missing
							getFixDB().addFix(new Aerodrome(icaoID, icaoID, new Position(arp.getX(), arp.getY(), arp.getZ())));
						}
					} catch (IOException e) {
						break;
					}
				}
			} catch (DataFormatException e) {
				throw new DBParserException(e);
			}
		} else {
			logger.info("Unknown type "+type+" for theme 'apts'");
		}
	}
	
	protected void readNavaids(URL context, Element geodataElem) throws IOException, DBParserException {
		final String type=geodataElem.getAttribute("type");
		if (type.equals("xplane")) {
			final String ref=geodataElem.getAttribute("ref");
			final URL location=new URL(context, ref);
			NavParser navParser=new NavParser(getFixDB(), geographicBounds);
			InputStream geoStream=location.openStream();
			navParser.readCompressed(geoStream);
		} else {
			logger.info("Unknown type "+type+" for theme 'nav'");
		}
	}
	
	protected void readAirways(URL context, Element geodataElem) throws IOException, DBParserException {
		final String type=geodataElem.getAttribute("type");
		if (type.equals("xplane")) {
			final String ref=geodataElem.getAttribute("ref");
			final URL location=new URL(context, ref);
			AirwayParser airwayParser=new AirwayParser(getAirwayDB(), geographicBounds);
			InputStream geoStream=location.openStream();
			airwayParser.readCompressed(geoStream);
		} else {
			logger.info("Unknown type "+type+" for theme 'awy'");
		}
	}
	
	protected void readPolygons(URL context, Element geodataElem, List<Polygon> polygons) throws DBParserException, IOException {
		final String type=geodataElem.getAttribute("type");
		if (type.equals("poly")) {
			final String ref=geodataElem.getAttribute("ref");
			final URL location=new URL(context, ref);
			PolyReader polyReader=new PolyReader();
			InputStream geoStream=location.openStream();
			polyReader.readPolygons(geoStream, polygons);
		} else if (type.equals("shape")) {
			try {
				final String datasourceRef=geodataElem.getAttribute("datasource");
				final String layer=geodataElem.getAttribute("layer");
				final URL datasourceLocation=new URL(context, datasourceRef);
				final ShapefileLayer shapefileLayer=new ShapefileLayer(datasourceLocation, layer);
				while (true) {
					try {
						final Feature feature=shapefileLayer.getNextFeature();
						if (feature==null) {
							break;
						}
						if (feature.getGeometry() instanceof Polygon) {
							polygons.add((Polygon)feature.getGeometry());
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
	
	public int getDefaultXRange() {
		return defaultXRange;
	}
	
	public Shape getGeographicBounds() {
		return geographicBounds;
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
