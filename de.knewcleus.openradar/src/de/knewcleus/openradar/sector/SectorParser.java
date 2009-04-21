package de.knewcleus.openradar.sector;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SectorParser {
	protected final SectorFactory sectorFactory;
	protected final URL baseURL;
	
	public SectorParser(SectorFactory sectorFactory, URL baseURL) {
		this.sectorFactory = sectorFactory;
		this.baseURL = baseURL;
	}

	public Sector parseSector(Element sectorElement) throws SectorParserException {
		final Sector sector = sectorFactory.createSector();
		final NodeList childNodeList = sectorElement.getChildNodes();
		
		for (int idx = 0; idx < childNodeList.getLength(); ++idx) {
			final Node childNode = childNodeList.item(idx);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				final String nodeName=childNode.getNodeName();
				if (nodeName.equals("name")) {
					sector.setName(childNode.getTextContent());
				} else if (nodeName.equals("description")) {
					sector.setDescription(childNode.getTextContent());
				} else if (nodeName.equals("bounds")) {
					sector.setBounds(parseBounds((Element)childNode));
				} else if (nodeName.equals("topology")) {
					sector.setTopology(parseTopology((Element)childNode));
				} else {
					throw new SectorParserException("Unknown element "+nodeName);
				}
			}
		}
		return sector;
	}
	
	public Bounds parseBounds(Element boundsElement) throws SectorParserException {
		final Bounds bounds = sectorFactory.createBounds();
		final NodeList childNodeList = boundsElement.getChildNodes();
		
		for (int idx = 0; idx < childNodeList.getLength(); ++idx) {
			final Node childNode = childNodeList.item(idx);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				final String nodeName=childNode.getNodeName();
				if (nodeName.equals("north")) {
					bounds.setNorth(parseLatitude(childNode.getTextContent()));
				} else if (nodeName.equals("south")) {
					bounds.setSouth(parseLatitude(childNode.getTextContent()));
				} else if (nodeName.equals("west")) {
					bounds.setWest(parseLongitude(childNode.getTextContent()));
				} else if (nodeName.equals("east")) {
					bounds.setEast(parseLongitude(childNode.getTextContent()));
				} else {
					throw new SectorParserException("Unknown element "+nodeName);
				}
			}
		}
		return bounds;
	}
	
	public double parseLatitude(String latitudeString) throws SectorParserException {
		// [NS]\d{1,2}((\.\d+)?D|D(\d{1,2}((\.\d+)?M|M(\d{1,2}(\.\d+)?S)?)?)?)?
		final char hemisphere=Character.toUpperCase(latitudeString.charAt(0));
		if (hemisphere!='N' && hemisphere!='S') {
			throw new SectorParserException("Invalid hemisphere:"+latitudeString);
		}
		final int degreesEnd = latitudeString.indexOf('D');
		final int minutesEnd = latitudeString.indexOf('M');
		final int secondsEnd = latitudeString.indexOf('S', 1);
		if (degreesEnd==-1) {
			throw new SectorParserException("Missing 'D':"+latitudeString);
		}
		if (minutesEnd==-1 && secondsEnd!=-1) {
			throw new SectorParserException("Illegally specified seconds, but not minutes:"+latitudeString);
		}
		if (minutesEnd!=-1 && minutesEnd<degreesEnd) {
			throw new SectorParserException("Illegally specified minutes before degrees:"+latitudeString);
		}
		if (minutesEnd!=-1 && secondsEnd!=-1 && secondsEnd<minutesEnd) {
			throw new SectorParserException("Illegally specified seconds before minutes:"+latitudeString);
		}
		final String degreesString = latitudeString.substring(1,degreesEnd);
		if (degreesString.indexOf('.')!=-1 && minutesEnd!=-1) {
			throw new SectorParserException("Cannot specify minutes and fractional degrees at the same time:"+latitudeString);
		}
		final double degrees = Double.parseDouble(degreesString);
		final double minutes;
		final double seconds;
		if (minutesEnd!=-1) {
			final String minutesString = latitudeString.substring(degreesEnd+1,minutesEnd);
			if (minutesString.indexOf('.')!=-1 && secondsEnd!=-1) {
				throw new SectorParserException("Cannot specify seconds and fractional minudes at the same time:"+latitudeString);
			}
			minutes = Double.parseDouble(minutesString);
		} else {
			minutes = 0.0;
		}
		if (secondsEnd!=-1) {
			if (secondsEnd!=latitudeString.length()-1) {
				throw new SectorParserException("Garbage after 'S':"+latitudeString);
			}
			final String secondsString = latitudeString.substring(minutesEnd+1,secondsEnd);
			seconds = Double.parseDouble(secondsString);
		} else {
			seconds = 0.0;
		}
		final double value = degrees + minutes/60.0 + seconds/3600.0;
		return (hemisphere=='S'?-value:value);
	}
	
	public double parseLongitude(String longitudeString) throws SectorParserException {
		// [EW]\d{1,3}((\.\d+)?D|D(\d{1,2}((\.\d+)?M|M(\d{1,2}(\.\d+)?S)?)?)?)?
		final char hemisphere=Character.toUpperCase(longitudeString.charAt(0));
		if (hemisphere!='E' && hemisphere!='W') {
			throw new SectorParserException("Invalid hemisphere:"+longitudeString);
		}
		final int degreesEnd = longitudeString.indexOf('D');
		final int minutesEnd = longitudeString.indexOf('M');
		final int secondsEnd = longitudeString.indexOf('S');
		if (degreesEnd==-1) {
			throw new SectorParserException("Missing 'D':"+longitudeString);
		}
		if (minutesEnd==-1 && secondsEnd!=-1) {
			throw new SectorParserException("Illegally specified seconds, but not minutes:"+longitudeString);
		}
		if (minutesEnd!=-1 && minutesEnd<degreesEnd) {
			throw new SectorParserException("Illegally specified minutes before degrees:"+longitudeString);
		}
		if (minutesEnd!=-1 && secondsEnd!=-1 && secondsEnd<minutesEnd) {
			throw new SectorParserException("Illegally specified seconds before minutes:"+longitudeString);
		}
		final String degreesString = longitudeString.substring(1,degreesEnd);
		if (degreesString.indexOf('.')!=-1 && minutesEnd!=-1) {
			throw new SectorParserException("Cannot specify minutes and fractional degrees at the same time:"+longitudeString);
		}
		final double degrees = Double.parseDouble(degreesString);
		final double minutes;
		final double seconds;
		if (minutesEnd!=-1) {
			final String minutesString = longitudeString.substring(degreesEnd+1,minutesEnd);
			if (minutesString.indexOf('.')!=-1 && secondsEnd!=-1) {
				throw new SectorParserException("Cannot specify seconds and fractional minudes at the same time:"+longitudeString);
			}
			minutes = Double.parseDouble(minutesString);
		} else {
			minutes = 0.0;
		}
		if (secondsEnd!=-1) {
			if (secondsEnd!=longitudeString.length()-1) {
				throw new SectorParserException("Garbage after 'S':"+longitudeString);
			}
			final String secondsString = longitudeString.substring(minutesEnd+1,secondsEnd);
			seconds = Double.parseDouble(secondsString);
		} else {
			seconds = 0.0;
		}
		final double value = degrees + minutes/60.0 + seconds/3600.0;
		return (hemisphere=='W'?-value:value);
	}
	
	public Topology parseTopology(Element topologyElement) throws SectorParserException {
		final Topology topology = sectorFactory.createTopology();
		final NodeList childNodeList = topologyElement.getChildNodes();
		
		for (int idx = 0; idx < childNodeList.getLength(); ++idx) {
			final Node childNode = childNodeList.item(idx);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				final String nodeName=childNode.getNodeName();
				if (nodeName.equals("landmass")) {
					topology.setLandmass(parseMapLayer((Element)childNode));
				} else if (nodeName.equals("watermass")) {
					topology.setWatermass(parseMapLayer((Element)childNode));
				} else if (nodeName.equals("runways")) {
					topology.setRunways(parseMapLayer((Element)childNode));
				} else if (nodeName.equals("tarmac")) {
					topology.setTarmac(parseMapLayer((Element)childNode));
				} else {
					throw new SectorParserException("Unknown element "+nodeName);
				}
			}
		}

		return topology;
	}
	
	public List<MapLayer> parseMapLayer(Element mapLayerElement) throws SectorParserException {
		List<MapLayer> mapLayerList = null;
		final NodeList childNodeList = mapLayerElement.getChildNodes();
		
		for (int idx = 0; idx < childNodeList.getLength(); ++idx) {
			final Node childNode = childNodeList.item(idx);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				final String nodeName=childNode.getNodeName();
				if (nodeName.equals("filelayer")) {
					if (mapLayerList==null) {
						mapLayerList=new ArrayList<MapLayer>();
					}
					mapLayerList.add(parseFileLayer((Element)childNode));
				} else if (nodeName.equals("polygonlayer")) {
					if (mapLayerList==null) {
						mapLayerList=new ArrayList<MapLayer>();
					}
					mapLayerList.add(parsePolygonLayer((Element)childNode));
				} else {
					throw new SectorParserException("Unknown element "+nodeName);
				}
			}
		}
		return mapLayerList;
	}
	
	public FileLayer parseFileLayer(Element fileLayerElement) throws SectorParserException {
		final FileLayer fileLayer = sectorFactory.createFileLayer();
		fileLayer.setType(FileLayerKind.SHAPEFILE);
		
		final NamedNodeMap childNodeList = fileLayerElement.getAttributes();
		
		for (int idx = 0; idx < childNodeList.getLength(); ++idx) {
			final Node childNode = childNodeList.item(idx);
			if (childNode.getNodeType() == Node.ATTRIBUTE_NODE) {
				final String nodeName=childNode.getNodeName();
				if (nodeName.equals("source")) {
					try {
						fileLayer.setSource(new URL(baseURL, childNode.getNodeValue()));
					} catch (MalformedURLException e) {
						throw new SectorParserException("Invalid URI in source element",e);
					}
				} else if (nodeName.equals("layer")) {
					fileLayer.setLayer(childNode.getNodeValue());
				} else if (nodeName.equals("kind")) {
					fileLayer.setType(parseFileLayerKind(childNode.getNodeValue()));
				} else {
					throw new SectorParserException("Unknown element "+nodeName);
				}
			}
		}

		return fileLayer;
	}
	
	public FileLayerKind parseFileLayerKind(String kindString) throws SectorParserException {
		if (kindString.equals("shapefile")) {
			return FileLayerKind.SHAPEFILE;
		}
		return null;
	}
	
	public PolygonLayer parsePolygonLayer(Element polygonLayerElement) throws SectorParserException {
		final PolygonLayer polygonLayer = sectorFactory.createPolygonLayer();
		final NodeList childNodeList = polygonLayerElement.getChildNodes();
		
		for (int idx = 0; idx < childNodeList.getLength(); ++idx) {
			final Node childNode = childNodeList.item(idx);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				final String nodeName=childNode.getNodeName();
				if (nodeName.equals("polygon")) {
					polygonLayer.getPolygon().add(parsePolygon((Element)childNode));
				} else {
					throw new SectorParserException("Unknown element "+nodeName);
				}
			}
		}

		return polygonLayer;
	}
	
	public Polygon parsePolygon(Element polygonElement) throws SectorParserException {
		final Polygon polygon = sectorFactory.createPolygon();
		
		final NodeList childNodeList = polygonElement.getChildNodes();
		
		for (int idx = 0; idx < childNodeList.getLength(); ++idx) {
			final Node childNode = childNodeList.item(idx);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				final String nodeName=childNode.getNodeName();
				if (nodeName.equals("point")) {
					polygon.getPoint().add(parseGeographicPosition((Element)childNode));
				} else {
					throw new SectorParserException("Unknown element "+nodeName);
				}
			}
		}
		
		return polygon;
	}
	
	public GeographicPosition parseGeographicPosition(Element geographicPositionElement) throws SectorParserException {
		final GeographicPosition geographicPosition = sectorFactory.createGeographicPosition();
		
		final NodeList childNodeList = geographicPositionElement.getChildNodes();
		
		for (int idx = 0; idx < childNodeList.getLength(); ++idx) {
			final Node childNode = childNodeList.item(idx);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				final String nodeName=childNode.getNodeName();
				if (nodeName.equals("latitude")) {
					geographicPosition.setLatitude(parseLatitude(childNode.getTextContent()));
				} else if (nodeName.equals("longitude")) {
					geographicPosition.setLongitude(parseLongitude(childNode.getTextContent()));
				} else {
					throw new SectorParserException("Unknown element "+nodeName);
				}
			}
		}
		
		return geographicPosition;
	}
}
