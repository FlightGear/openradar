package de.knewcleus.openradar.sector;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SectorParser {
	protected final SectorFactory sectorFactory;
	
	public SectorParser(SectorFactory sectorFactory) {
		this.sectorFactory = sectorFactory;
	}

	public Sector parseSector(Element sectorElement) {
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
				}
			}
		}
		return sector;
	}
	
	public Bounds parseBounds(Element boundsElement) {
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
				}
			}
		}
		return bounds;
	}
	
	protected double parseLatitude(String latitudeString) {
		// [NS]\d{1,2}((\.\d+)?D|D(\d{1,2}((\.\d+)?M|M(\d{1,2}(\.\d+)?S)?)?)?)?
		// TODO
		return 0.0;
	}
	
	protected double parseLongitude(String longitudeString) {
		// [EW]\d{1,3}((\.\d+)?D|D(\d{1,2}((\.\d+)?M|M(\d{1,2}(\.\d+)?S)?)?)?)?
		// TODO
		return 0.0;
	}
	
	protected Topology parseTopology(Element topologyElement) {
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
				}
			}
		}

		return topology;
	}
	
	protected List<MapLayer> parseMapLayer(Element mapLayerElement) {
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
				}
			}
		}
		return mapLayerList;
	}
	
	protected FileLayer parseFileLayer(Element fileLayerElement) {
		final FileLayer fileLayer = sectorFactory.createFileLayer();
		
		final NodeList childNodeList = fileLayerElement.getChildNodes();
		
		for (int idx = 0; idx < childNodeList.getLength(); ++idx) {
			final Node childNode = childNodeList.item(idx);
			if (childNode.getNodeType() == Node.ATTRIBUTE_NODE) {
				final String nodeName=childNode.getNodeName();
				if (nodeName.equals("source")) {
					try {
						fileLayer.setSource(new URI(childNode.getNodeValue()));
					} catch (URISyntaxException e) {
						throw new RuntimeException(e);
					}
				} else if (nodeName.equals("layer")) {
					fileLayer.setLayer(childNode.getNodeValue());
				} else if (nodeName.equals("kind")) {
					fileLayer.setType(parseFileLayerKind(childNode.getNodeValue()));
				}
			}
		}

		return fileLayer;
	}
	
	protected FileLayerKind parseFileLayerKind(String kindString) {
		if (kindString.equals("shapefile")) {
			return FileLayerKind.SHAPEFILE;
		}
		return null;
	}
	
	protected PolygonLayer parsePolygonLayer(Element polygonLayerElement) {
		final PolygonLayer polygonLayer = sectorFactory.createPolygonLayer();
		final NodeList childNodeList = polygonLayerElement.getChildNodes();
		
		for (int idx = 0; idx < childNodeList.getLength(); ++idx) {
			final Node childNode = childNodeList.item(idx);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				final String nodeName=childNode.getNodeName();
				if (nodeName.equals("polygon")) {
					polygonLayer.getPolygon().add(parsePolygon((Element)childNode));
				}
			}
		}

		return polygonLayer;
	}
	
	protected Polygon parsePolygon(Element polygonElement) {
		final Polygon polygon = sectorFactory.createPolygon();
		
		final NodeList childNodeList = polygonElement.getChildNodes();
		
		for (int idx = 0; idx < childNodeList.getLength(); ++idx) {
			final Node childNode = childNodeList.item(idx);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				final String nodeName=childNode.getNodeName();
				if (nodeName.equals("point")) {
					polygon.getPoint().add(parseGeographicPosition((Element)childNode));
				}
			}
		}
		
		return polygon;
	}
	
	protected GeographicPosition parseGeographicPosition(Element geographicPositionElement) {
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
				}
			}
		}
		
		return geographicPosition;
	}
}
