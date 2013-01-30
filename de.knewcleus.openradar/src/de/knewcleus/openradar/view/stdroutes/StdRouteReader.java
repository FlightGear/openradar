/**
 * Copyright (C) 2013 Wolfram Wagner 
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.stdroutes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
    private String airportCode;
    private final IMapViewerAdapter mapViewAdapter; 
    private List<StdRoute> stdRoutes = new ArrayList<StdRoute>();
    private ZipFile zipArchive = null;

    public StdRouteReader(AirportData data,IMapViewerAdapter mapViewAdapter) {
        this.data = data;
        this.airportCode = data.getAirportCode();
        this.mapViewAdapter = mapViewAdapter; 
        readRouteXml();
    }

    private void readRouteXml() {

        SAXBuilder builder = new SAXBuilder();
        InputStream xmlInputStream = null;

        try {
            xmlInputStream = getZipArchiveFileInputStream("data/airports.zip", airportCode);

            Document document = (Document) builder.build(xmlInputStream);
            Element rootNode = document.getRootElement();

            List<Element> list = rootNode.getChildren("route");
            for (Element eRoute : list) {
                String name = eRoute.getAttributeValue("name");
                List<Element> sublist = rootNode.getChildren("route");
                String activeLandingRunways=null;
                String activeStartRunways=null;
                String navaids=null;
                AStdRouteElement previous = null;
                StdRoute route = new StdRoute(data, name);
                for (Element element : sublist) {
                    if(element.getName().equalsIgnoreCase("activeLandingRunways")) {
                        route.setActiveLandingRunways(element.getText());
                    } else if(element.getName().equalsIgnoreCase("activeStartRunways")) {
                        route.setActiveLandingRunways(element.getText());
                    } else if(element.getName().equalsIgnoreCase("navaids")) {
                        route.setActiveStartingRunways(element.getText());
                    } else if(element.getName().equalsIgnoreCase("line")) {
                        String begin = element.getAttributeValue("begin");
                        String end = element.getAttributeValue("end");
                        String angle = element.getAttributeValue("angle");
                        String length = element.getAttributeValue("length");
                        String beginOffset = element.getAttributeValue("beginOffset");
                        String endOffset = element.getAttributeValue("endOffset");
                        String stroke = element.getAttributeValue("stroke");
                        String lineWidth = element.getAttributeValue("lineWidth");
                        String arrows = element.getAttributeValue("arrows");
                        StdRouteLine line = new StdRouteLine(route,mapViewAdapter,previous,begin,end,angle,length,beginOffset,endOffset,stroke,lineWidth,arrows);
                        previous = line;
                        route.addSegment(line);
                     } else if(element.getName().equalsIgnoreCase("bow")) {
                         String center = element.getAttributeValue("center");
                         String radius = element.getAttributeValue("radius");
                         String beginAngle = element.getAttributeValue("beginAngle");
                         String endAngle = element.getAttributeValue("endAngle");
                         String stroke = element.getAttributeValue("stroke");
                         String lineWidth = element.getAttributeValue("lineWidth");
                         StdRouteRadiusBow bow = new StdRouteRadiusBow(route,mapViewAdapter,previous,center,radius,beginAngle,endAngle,stroke,lineWidth);
                         previous = bow;
                         route.addSegment(bow);
                     } else if(element.getName().equalsIgnoreCase("curve")) {
                         String begin = element.getAttributeValue("begin");
                         String end = element.getAttributeValue("end");
                         String controlPoint = element.getAttributeValue("controlPoint");
                         String stroke = element.getAttributeValue("stroke");
                         String lineWidth = element.getAttributeValue("lineWidth");
                         StdRouteBow bow = new StdRouteBow(route,mapViewAdapter,previous,begin,end,controlPoint,stroke,lineWidth);
                         previous = bow;
                         route.addSegment(bow);
                     } else if(element.getName().equalsIgnoreCase("loop")) {
                         String navpoint = element.getAttributeValue("navpoint");
                         String inboundHeading = element.getAttributeValue("inboundHeading");
                         String width = element.getAttributeValue("width");
                         String right = element.getAttributeValue("right");
                         String arrows = element.getAttributeValue("arrows");
                         String minHeight = element.getAttributeValue("minHeight");
                         String maxHeight = element.getAttributeValue("maxHeight");
                         String misapHeight = element.getAttributeValue("misapHeight");
                         String stroke = element.getAttributeValue("stroke");
                         String lineWidth = element.getAttributeValue("lineWidth");
                         StdRouteEllipse ellipse = new StdRouteEllipse(route,mapViewAdapter,previous,navpoint,inboundHeading,width,right,arrows,minHeight,maxHeight,misapHeight,stroke,lineWidth);
                         previous = ellipse;
                         route.addSegment(ellipse);
                         // todo do something with it!
                     } 
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (xmlInputStream != null) {
                try {
                    xmlInputStream.close();
                    zipArchive.close();
                } catch (IOException e) {
                }
            }
        }
    }

    protected InputStream getZipArchiveFileInputStream(String archive, String airportCode) throws IOException {
        // data is stored in file /K/O/A/KOAK.groundnet.xml
        String dir1 = airportCode.substring(0, 1);
        String dir2 = airportCode.substring(1, 2);
        String dir3 = airportCode.substring(2, 3);
        String file = airportCode + ".groundnet.xml";

        // zip files have forward slashs
        String filename = "Airports" + "/" + dir1 + "/" + dir2 + "/" + dir3 + "/" + file;

        final File inputFile = new File(archive);
        zipArchive = new ZipFile(inputFile);
        Enumeration<? extends ZipEntry> entries = zipArchive.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            if (zipEntry.getName().equals(filename)) {
                return zipArchive.getInputStream(zipEntry);
            }
        }
        throw new IllegalStateException(filename + " not found in " + archive);
    }

    public void closeZipArchive() {
        if (zipArchive != null) {
            try {
                zipArchive.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<StdRoute> getStdRoutes() {
        return stdRoutes;
    }
    
}
