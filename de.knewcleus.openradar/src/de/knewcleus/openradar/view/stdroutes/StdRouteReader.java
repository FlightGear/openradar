/**
 * Copyright (C) 2013 Wolfram Wagner
 *
 * This file is part of OpenRadar.
 *
 * OpenRadar is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OpenRadar. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von OpenRadar.
 *
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie von der Free
 * Software Foundation, Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 *
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne
 * die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public
 * License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 * <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.stdroutes;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

/**
 * This file reads the airport groundnet xml file
 *
 * @author Wolfram Wagner
 *
 */
public class StdRouteReader {

    private final AirportData data;
    private final IMapViewerAdapter mapViewAdapter;
    private List<StdRoute> stdRoutes = new ArrayList<StdRoute>();
    private static Logger log = LogManager.getLogger(StdRouteReader.class);

    public StdRouteReader(AirportData data, IMapViewerAdapter mapViewAdapter) {
        this.data = data;
        this.mapViewAdapter = mapViewAdapter;
        readRouteXml();
    }

    private void readRouteXml() {

        SAXBuilder builder = new SAXBuilder();
        InputStream xmlInputStream = null;

        File dir = new File("data/routes/" + data.getAirportCode());
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        List<File> files = new ArrayList<File>(Arrays.asList(dir.listFiles()));

        data.getNavaidDB().clearAddPoints();

        while (files.size()>0) {
            File file = files.remove(0);
            try {
                if (!file.getName().endsWith(".xml"))
                    continue;
                // todo read all files
                xmlInputStream = new FileInputStream(file);

                Document document = (Document) builder.build(xmlInputStream);
                Element rootNode = document.getRootElement();

//                String orFilename = "data/routes/" + data.getAirportCode() + "/" + file.getName().substring(0, file.getName().indexOf(".xml")) + ".or.xml";
                if ("ProceduresDB".equalsIgnoreCase(rootNode.getName())) {// && !(new File(orFilename).exists())) {
                    // if converted file does not exist, convert it now.
                    // deactivated for now convertProcedureDbFile(orFilename, rootNode);
                } else {
                    // read or file
                    List<Element> list = rootNode.getChildren("addPoint");
                    for (Element eAddPoint : list) {
                        String code = eAddPoint.getAttributeValue("code");
                        String sPoint = eAddPoint.getAttributeValue("point");
                        try {
                            Point2D point = StdRoute.getPoint(data, mapViewAdapter, sPoint ,null);
                            data.getNavaidDB().addPoint(code, point);
                        } catch (Exception e) {
                            log.error("Problem to parse file " + file.getAbsolutePath() + ", addPoint: " + code + ": " + sPoint+ ", Error:" + e.getMessage());
                        }
                    }
                    List<Element> includeList = rootNode.getChildren("include");
                    for (Element eInclude : includeList) {
                        String fileName = eInclude.getAttributeValue("file");
                        files.add(new File(file.getAbsolutePath().substring(0,file.getAbsolutePath().lastIndexOf(File.separator)+1)+fileName));
                    }
                    // routes
                    list = rootNode.getChildren("route");
                    for (Element eRoute : list) {
                        String name = eRoute.getAttributeValue("name");
                        String displayMode = eRoute.getAttributeValue("displayMode");
                        String zoomMin = eRoute.getAttributeValue("zoomMin");
                        String zoomMax = eRoute.getAttributeValue("zoomMax");
                        String stroke = eRoute.getAttributeValue("stroke");
                        String lineWidth = eRoute.getAttributeValue("lineWidth");
                        String color = eRoute.getAttributeValue("color");
                        List<Element> sublist = eRoute.getChildren();
                        AStdRouteElement previous = null;
                        StdRoute route = new StdRoute(data, mapViewAdapter, name, displayMode, zoomMin, zoomMax, stroke, lineWidth, color);
                        for (Element element : sublist) {
                            try {
                                if (element.getName().equalsIgnoreCase("activeLandingRunways")) {
                                    route.setActiveLandingRunways(element.getText());
                                } else if (element.getName().equalsIgnoreCase("activeStartRunways")) {
                                    route.setActiveStartingRunways(element.getText());
                                } else if (element.getName().equalsIgnoreCase("navaids")) {
                                    color = element.getAttributeValue("color");
                                    route.setNavaids(element.getText(), color);
                                } else if (element.getName().equalsIgnoreCase("line")) {
                                    String start = element.getAttributeValue("start");
                                    String end = element.getAttributeValue("end");
                                    String angle = element.getAttributeValue("angle");
                                    String length = element.getAttributeValue("length");
                                    String startOffset = element.getAttributeValue("startOffset");
                                    String endOffset = element.getAttributeValue("endOffset");
                                    stroke = element.getAttributeValue("stroke");
                                    lineWidth = element.getAttributeValue("lineWidth");
                                    String arrows = element.getAttributeValue("arrows");
                                    color = element.getAttributeValue("color");
                                    String text = element.getAttributeValue("text");
                                    StdRouteLine line = new StdRouteLine(route, mapViewAdapter, previous, start, end, angle, length, startOffset, endOffset,
                                            stroke, lineWidth, arrows, color, text);
                                    previous = line;
                                    route.addElement(line);
                                } else if (element.getName().equalsIgnoreCase("bow")) {
                                    String center = element.getAttributeValue("center");
                                    String radius = element.getAttributeValue("radius");
                                    String startAngle = element.getAttributeValue("startAngle");
                                    String extentAngle = element.getAttributeValue("extentAngle");
                                    stroke = element.getAttributeValue("stroke");
                                    lineWidth = element.getAttributeValue("lineWidth");
                                    String arrows = element.getAttributeValue("arrows");
                                    color = element.getAttributeValue("color");
                                    String text = element.getAttributeValue("text");
                                    StdRouteBow bow = new StdRouteBow(route, mapViewAdapter, previous, center, radius, startAngle, extentAngle, stroke,
                                            lineWidth, color, arrows, text);
                                    previous = bow;
                                    route.addElement(bow);
                                } else if (element.getName().equalsIgnoreCase("curve")) {
                                    String start = element.getAttributeValue("start");
                                    String end = element.getAttributeValue("end");
                                    String controlPoint = element.getAttributeValue("controlPoint");
                                    stroke = element.getAttributeValue("stroke");
                                    lineWidth = element.getAttributeValue("lineWidth");
                                    String arrows = element.getAttributeValue("arrows");
                                    color = element.getAttributeValue("color");
                                    StdRouteCurve bow = new StdRouteCurve(route, mapViewAdapter, previous, start, end, controlPoint, stroke, lineWidth, color,
                                            arrows);
                                    previous = bow;
                                    route.addElement(bow);
                                } else if (element.getName().equalsIgnoreCase("intercept")) {
                                    String start = element.getAttributeValue("start");
                                    String startOffset = element.getAttributeValue("startOffset");
                                    String startHeading = element.getAttributeValue("startHeading");
                                    String startTurn = element.getAttributeValue("startTurn");
                                    String radius = element.getAttributeValue("radius");
                                    String speed = element.getAttributeValue("speed");
                                    String end = element.getAttributeValue("end");
                                    String radial = element.getAttributeValue("radial");
                                    String endHeading = element.getAttributeValue("endHeading");
                                    String direction = element.getAttributeValue("direction");
                                    String endOffset = element.getAttributeValue("endOffset");
                                    String text = element.getAttributeValue("text");
                                    stroke = element.getAttributeValue("stroke");
                                    lineWidth = element.getAttributeValue("lineWidth");
                                    String arrows = element.getAttributeValue("arrows");
                                    color = element.getAttributeValue("color");
                                    StdRouteIntercept intercept = new StdRouteIntercept(route, mapViewAdapter, previous, start, startOffset, startHeading, startTurn, radius,speed,
                                                                        end, radial, endHeading, direction, endOffset, stroke, lineWidth, arrows, color, text);
                                    previous = intercept;
                                    route.addElement(intercept);
                                } else if (element.getName().equalsIgnoreCase("loop")) {
                                    String navpoint = element.getAttributeValue("navpoint");
                                    String inboundHeading = element.getAttributeValue("inboundHeading");
                                    String length = element.getAttributeValue("length");
                                    String width = element.getAttributeValue("width");
                                    String right = element.getAttributeValue("right");
                                    String arrows = element.getAttributeValue("arrows");
                                    String minHeight = element.getAttributeValue("minHeight");
                                    String maxHeight = element.getAttributeValue("maxHeight");
                                    String misapHeight = element.getAttributeValue("misapHeight");
                                    stroke = element.getAttributeValue("stroke");
                                    lineWidth = element.getAttributeValue("lineWidth");
                                    color = element.getAttributeValue("color");
                                    StdRouteLoop ellipse = new StdRouteLoop(route, mapViewAdapter, previous, navpoint, inboundHeading, length, width, right,
                                            arrows, minHeight, maxHeight, misapHeight, stroke, lineWidth, color);
                                    previous = ellipse;
                                    route.addElement(ellipse);
                                } else if (element.getName().equalsIgnoreCase("multiPointLine")) {
                                    String close = element.getAttributeValue("close");
                                    stroke = element.getAttributeValue("stroke");
                                    lineWidth = element.getAttributeValue("lineWidth");
                                    color = element.getAttributeValue("color");
                                    List<String> points = new ArrayList<String>();
                                    List<Element> pointList = element.getChildren("point");
                                    for (Element ePoint : pointList) {
                                        points.add(ePoint.getTextTrim());
                                    }
                                    StdRouteMultipointLine line = new StdRouteMultipointLine(route, mapViewAdapter, previous, points, close, stroke, lineWidth,
                                            color);
                                    previous = line;
                                    route.addElement(line);
                                } else if (element.getName().equalsIgnoreCase("text")) {
                                    String position = element.getAttributeValue("position");
                                    String angle = element.getAttributeValue("angle");
                                    String alignHeading = element.getAttributeValue("alignHeading");
                                    String font = element.getAttributeValue("font");
                                    String fontSize = element.getAttributeValue("fontSize");
                                    color = element.getAttributeValue("color");
                                    String sText = element.getAttributeValue("text");
                                    StdRouteText text = new StdRouteText(route, mapViewAdapter, previous, position, angle, alignHeading, font, fontSize, color, sText);
                                    previous = text;
                                    route.addElement(text);
                                } else if (element.getName().equalsIgnoreCase("screenText")) {
                                    String position = element.getAttributeValue("screenPos");
                                    String angle = element.getAttributeValue("angle");
                                    String font = element.getAttributeValue("font");
                                    String fontSize = element.getAttributeValue("fontSize");
                                    color = element.getAttributeValue("color");
                                    String sText = element.getAttributeValue("text");
                                    StdRouteScreenText text = new StdRouteScreenText(route, mapViewAdapter, previous, position, angle, font, fontSize, color,
                                            sText);
                                    previous = text;
                                    route.addElement(text);
                                } else if (element.getName().equalsIgnoreCase("minAlt")) {
                                    String position = element.getAttributeValue("position");
                                    String value = element.getAttributeValue("value");
                                    String font = element.getAttributeValue("font");
                                    String fontSize = element.getAttributeValue("fontSize");
                                    color = element.getAttributeValue("color");
                                    StdRouteMinAltitude minAlt = new StdRouteMinAltitude(route, mapViewAdapter, previous, position, value, font, fontSize,
                                            color);
                                    previous = minAlt;
                                    route.addElement(minAlt);
                                }

                            } catch (Exception e) {
                                log.error("Problem to parse file " + file.getAbsolutePath() + ", Route: " + route.getName() + ", Error:" + e.getMessage(),e);
                                break;
                            }
                        }
                        stdRoutes.add(route);
                    }
                }

            } catch (Exception e) {
                log.error("Problem to parse file " + file.getAbsolutePath() + ", Error:" + e.getMessage());

            } finally {
                if (xmlInputStream != null) {
                    try {
                        xmlInputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        data.getNavaidDB().setStdRoutes(stdRoutes);
    }

    /**
     * I have given up this part of the project for now, as there are big problems to map the navdata made to feed an autopilot
     * to an xml made to add details to the map.
     *
     * http://gitorious.org/fg/flightgear/blobs/blame/44e672c25651dd3ef359ba300ddad3a2b47879fd/src/Navaids/route.cxx
     * Normal : /Sid/Star/_Waypoint defined by coordinates
     *
     * Hold: a holding pattern
     *
     * DmeIntc Waypoint defined by distance from point (dme) DMEtoIntercept the distance to 'dme' Hdg_Crs_value the
     * course to the waypoint
     *
     * Intc Intercept a radial of next waypoint Hdg_Crs_value original course RadialtoIntercept radial to intercept
     *
     *
     * @param orFilename
     * @param rootNode
     */
//    private void convertProcedureDbFile(String orFilename, Element rootNode) {
//        try {
//            Document newDoc = new Document();
//            Element newRoot = new Element("routes");
//            newDoc.setRootElement(newRoot);
//
//            Namespace ns = Namespace.getNamespace("http://tempuri.org/XMLSchema.xsd");
//
//            String lastWayPoint=""; // the position of the last waypoint
//
//            List<Element> airportList = rootNode.getChildren("Airport",ns);
//            for(Element eAirport : airportList) {
//                String airportCode = eAirport.getAttributeValue("ICAOcode");
//
//                // sid
//                List<Element> sidList = eAirport.getChildren("Sid", ns);
//                for(Element eSid : sidList) {
//                    String sidName = eSid.getAttributeValue("Name");
//                    String sidRunways = eSid.getAttributeValue("Runways");
//
//                    Element newRoute = new Element("route");
//                    newRoot.addContent(newRoute);
//                    newRoute.setAttribute("name",airportCode+" SID "+sidName);
//                    newRoute.setAttribute("color","120,140,120");
//                    Element newRunways = new Element("activeStartRunways");
//                    newRoute.addContent(newRunways);
//                    newRunways.setText(sidRunways);
//                    Element newNavaids = new Element("navaids");
//                    newRoute.addContent(newNavaids);
//                    Element newLine = null;
//
//                    List<Element> waypointList = eSid.getChildren("Sid_Waypoint",ns);
//                    for(Element eWaypoint : waypointList) {
//                        String id = eWaypoint.getAttributeValue("Id");
//                        String name = eWaypoint.getChildText("Name",ns);
//                        String type = eWaypoint.getChildText("Type",ns);
//                        String lat = eWaypoint.getChildText("Latitude",ns);
//                        String lon = eWaypoint.getChildText("Longitude",ns);
//                        String speed = eWaypoint.getChildText("Speed",ns);
//                        String altitude = eWaypoint.getChildText("Altitude",ns);
//                        String altitudeRestriction = eWaypoint.getChildText("AltitudeRestriction",ns);
//
//                        if(newLine==null) {
//                            // Start of route
//                            if(Double.parseDouble(lat)!=0 && Double.parseDouble(lon)!=0) {
//                                // no position given, sids start at runways => runwayend to first waypoint
//                                newLine = new Element("line");
//                                lastWayPoint = airportCode+"-RW"+sidRunways;
//                                newLine.setAttribute("start",lastWayPoint);
//
//                            } else {
//                                newLine = new Element("line");
//                                lastWayPoint = lat+","+lon;
//                                newLine.setAttribute("start",lastWayPoint);
//
//                            }
//                        } else {
//                            if( type.equals("Normal") ) {
//                                // finish and continue
//                                newLine.setAttribute("end",lat+","+lon);
//                                newRoute.addContent(newLine);
//                                // create next
//                                newLine = new Element("line");
//                                lastWayPoint = lat+","+lon;
//                                newLine.setAttribute("start",lastWayPoint);
//
//                            } else if(type.equals("Intc") || type.equals("VorRadialIntc")) { //|| type.equals("DmeIntc")
//                                String hdg = eWaypoint.getChildText("Hdg_Crs_value",ns);
//                                String radial = eWaypoint.getChildText("RadialtoIntercept",ns);
//                                String direction = eWaypoint.getChildText("Sp_Turn",ns);
//                                String dmeDistance = eWaypoint.getChildText("DMEtoIntercept",ns);
//
//                                Element intercept = new Element("intercept");
//                                intercept.setAttribute("startTurning",lastWayPoint); // this draws a line in dir heading
//
//                                intercept.setAttribute("heading",hdg);
//                                if(dmeDistance==null) {
//                                    lastWayPoint = lat+","+lon;
//                                    intercept.setAttribute("end",lastWayPoint);
//                                } else {
//                                    lastWayPoint = dmeDistance+"NM"+direction+"@"+lat+","+lon;
//                                    // this is not really correct. The direction must be a tangent to the dme circle in distance
//                                    // todo
//                                    intercept.setAttribute("end",lastWayPoint);
//                                }
//                                intercept.setAttribute("radial",radial);
//                                if(direction!=null) {
//                                    intercept.setAttribute("direction",direction);
//                                }
//                                newRoute.addContent(intercept);
//                                // create next
//                                newLine = new Element("line");
//                                newLine.setAttribute("start",lastWayPoint);
//
//                            }
//                        }
//                    }
//                }
//
//                // stars
//                List<Element> starList = eAirport.getChildren("Star",ns);
//                for(Element eStar : starList) {
//                    String starName = eStar.getAttributeValue("Name");
//                    String starRunways = eStar.getAttributeValue("Runways");
//
//                    Element newRoute = new Element("route");
//                    newRoot.addContent(newRoute);
//                    newRoute.setAttribute("name",airportCode+" STAR "+starName);
//                    newRoute.setAttribute("color","120,120,140");
//                    Element newRunways = new Element("activeLandingRunways");
//                    newRunways.setText(starRunways);
//                    newRoute.addContent(newRunways);
//                    Element newNavaids = new Element("navaids");
//                    newRoute.addContent(newNavaids);
//                    Element newLine = null;
//
//                    List<Element> waypointList = eStar.getChildren("Star_Waypoint",ns);
//                    for(Element eWaypoint : waypointList) {
//                        String id = eWaypoint.getAttributeValue("Id");
//                        String name = eWaypoint.getChildText("Name",ns);
//                        String type = eWaypoint.getChildText("Type",ns);
//                        String lat = eWaypoint.getChildText("Latitude",ns);
//                        String lon = eWaypoint.getChildText("Longitude",ns);
//                        String speed = eWaypoint.getChildText("Speed",ns);
//                        String altitude = eWaypoint.getChildText("Altitude",ns);
//
//                            if(newLine==null) {
//                                // create it
//                                newLine = new Element("line");
//                                lastWayPoint = lat+","+lon;
//                                newLine.setAttribute("start",lastWayPoint);
//                            } else {
//                                if( type.equals("Normal") ) {
//                                    // finish and continue
//                                    newLine.setAttribute("end",lat+","+lon);
//                                    newRoute.addContent(newLine);
//                                    // create next
//                                    newLine = new Element("line");
//                                    lastWayPoint = lat+","+lon;
//                                    newLine.setAttribute("start",lastWayPoint);
//
//                                } else if(type.equals("Intc") || type.equals("VorRadialIntc")) { //|| type.equals("DmeIntc")
//                                    String hdg = eWaypoint.getChildText("Hdg_Crs_value",ns);
//                                    String radial = eWaypoint.getChildText("RadialtoIntercept",ns);
//                                    String direction = eWaypoint.getChildText("Sp_Turn",ns);
//                                    String dmeDistance = eWaypoint.getChildText("DMEtoIntercept",ns);
//
//                                    Element intercept = new Element("intercept");
//                                    intercept.setAttribute("startTurning",lastWayPoint); // this draws a line in dir heading
//
//                                    intercept.setAttribute("heading",hdg);
//                                    if(dmeDistance==null) {
//                                        lastWayPoint = lat+","+lon;
//                                        intercept.setAttribute("end",lastWayPoint);
//                                    } else {
//                                        lastWayPoint = dmeDistance+"NM"+direction+"@"+lat+","+lon;
//                                        // this is not really correct. The direction must be a tangent to the dme circle in distance
//                                        // todo
//                                        intercept.setAttribute("end",lastWayPoint);
//                                    }
//                                    intercept.setAttribute("radial",radial);
//                                    if(direction!=null) {
//                                        intercept.setAttribute("direction",direction);
//                                    }
//                                    newRoute.addContent(intercept);
//                                    // create next
//                                    newLine = new Element("line");
//                                    newLine.setAttribute("start",lastWayPoint);
//                                }
//
//                        } else if(type.equals("Hold")) {
//
//                            String radOrInbd = eWaypoint.getChildText("Hld_Rad_or_Inbd",ns);
//                            String radValue = eWaypoint.getChildText("Hld_Rad_value",ns);
//                            boolean right = "right".equalsIgnoreCase(eWaypoint.getChildText("Hld_Turn",ns));
//                            String timeOrDist = eWaypoint.getChildText("Hld_Time_or_Dist",ns);
//                            String tdValue = eWaypoint.getChildText("Hld_td_value",ns);
//
//                            // <loop navpoint="SEDOR" inboundHeading="192" length="5" arrows="both" minHeight="7000" maxHeight="MAX 12000"/>>
//                            if(newLine==null) {
//                                // create it
//                                Element newLoop = new Element("loop");
//                                newLoop.setAttribute("navpoint",lat+","+lon);
//                                newLoop.setAttribute("inboundHeading",radValue);
//                                newLoop.setAttribute("length","5");     // todo make dynamic
//                                newLoop.setAttribute("right",right?"true":"false");
//                                newLoop.setAttribute("arrows","both");
//                                newLoop.setAttribute("minHeight",altitude);
//                                newRoute.addContent(newLoop);
//                            } else {
//
//                                // finish and continue
//                                newLine.setAttribute("end",lat+","+lon);
//                                newRoute.addContent(newLine);
//                                // create next
//                                Element newLoop = new Element("loop");
//                                newLoop.setAttribute("navpoint",lat+","+lon);
//                                newLoop.setAttribute("inboundHeading",radValue);
//                                newLoop.setAttribute("length","5");     // todo make dynamic
//                                newLoop.setAttribute("arrows","both");
//                                newLoop.setAttribute("minHeight",altitude);
//                                newRoute.addContent(newLoop);
//
//                                // next line starts at next waypoint
//                                newLine=null;
//                            }
//                        }
//                    }
//                }
//            }
//            // write OpenRadar format file
//            XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
//            FileWriter writer=null;
//            try {
//                writer = new FileWriter(orFilename);
//                xmlOutput.output(newDoc, writer);
//                writer.flush();
//            } catch (IOException e) {
//                log.log(Level.SEVERE,"Problem to write file "+orFilename,e);
//            } finally {
//                if(writer!=null) {
//                    try {
//                        writer.close();
//                    } catch (IOException e) {}
//                }
//            }
//        } catch(Exception e) {
//            log.log(Level.SEVERE,"Problem to convert file to "+orFilename,e);
//        }
//    }

    public List<StdRoute> getStdRoutes() {
        return stdRoutes;
    }

}
