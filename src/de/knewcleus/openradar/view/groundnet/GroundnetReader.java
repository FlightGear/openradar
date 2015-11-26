/**
 * Copyright (C) 2012 Wolfram Wagner
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
package de.knewcleus.openradar.view.groundnet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 * This file reads the airport groundnet xml file
 *
 * @author Wolfram Wagner
 *
 */
public class GroundnetReader {

    private String airportCode;
    private List<ParkPos> parkPosList = new ArrayList<ParkPos>();
    private List<TaxiSign> signs = new ArrayList<TaxiSign>();
    private Map<String, TaxiPoint> mapTaxiPoints = new TreeMap<String, TaxiPoint>();
    private List<TaxiWaySegment> taxiwaySegmentList = new ArrayList<TaxiWaySegment>();
    private ZipFile zipArchive = null;

    private static Logger log = LogManager.getLogger(GroundnetReader.class);

    public GroundnetReader(String airportCode) {
        this.airportCode = airportCode;
        readGroundnetXml();
        // readAptNavData(); // not in use because of bad quality
    }

    private void readGroundnetXml() {

        SAXBuilder builder = new SAXBuilder();
        InputStream xmlInputStream = null;

        try {
            xmlInputStream = getZipArchiveFileInputStream("data/airports.zip", airportCode);

            Document document = (Document) builder.build(xmlInputStream);
            Element rootNode = document.getRootElement();

            // read parking list
            List<Element> list = rootNode.getChild("parkingList").getChildren("Parking");
            for (Element p : list) {
                String index = p.getAttributeValue("index");
                String type = p.getAttributeValue("type");
                String name = p.getAttributeValue("name");
                String number = p.getAttributeValue("number");
                String lat = p.getAttributeValue("lat");
                String lon = p.getAttributeValue("lon");
                String heading = p.getAttributeValue("heading");
                String radius = p.getAttributeValue("radius");
                String airlineCodes = p.getAttributeValue("airlineCodes");
                ParkPos pos = new ParkPos(index, type, name, number, lat, lon, heading, radius, airlineCodes);
                parkPosList.add(pos);
                mapTaxiPoints.put(index, pos);
            }

            // read nodes
            list = rootNode.getChild("TaxiNodes").getChildren("node");
            for (Element p : list) {
                String index = p.getAttributeValue("index");
                String lat = p.getAttributeValue("lat");
                String lon = p.getAttributeValue("lon");
                boolean isOnRunway = !"0".equals(p.getAttributeValue("isOnRunway"));
                String holdPointType = p.getAttributeValue("holdPointType");
                TaxiPoint point = new TaxiPoint(index, lat, lon, isOnRunway, holdPointType);
                mapTaxiPoints.put(index, point);
            }
            // read segments
            list = rootNode.getChild("TaxiWaySegments").getChildren("arc");
            for (Element a : list) {
                String beginPoint = a.getAttributeValue("begin");
                String endPoint = a.getAttributeValue("end");
                boolean isPushBackRoute = !"0".equals(a.getAttributeValue("isPushBackRoute"));
                String name = a.getAttributeValue("name");
                TaxiWaySegment seg = new TaxiWaySegment(name, mapTaxiPoints.get(beginPoint), mapTaxiPoints.get(endPoint), isPushBackRoute);
                taxiwaySegmentList.add(seg);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
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

    /**
     * This method is not used, because it produces too many lines, which do not completely align with the shapes
     */
//    private void readAptNavData() {
//        BufferedReader br = null;
//        ArrayList<TaxiPoint> points = new ArrayList<TaxiPoint>();
//        boolean found = false;
//        StringBuilder taxiSegName = null;
//        int currentPaintStyle = 2;
//
//        try {
//            br = new BufferedReader(new InputStreamReader(getZipAptFileInputStream()));
//            String nextLine = br.readLine().trim();
//            while (nextLine != null) {
//                if (nextLine.trim().length() == 0) {
//                    // skip empty lines
//                    nextLine = br.readLine().trim();
//                    continue;
//                }
//                Record r = new Record(nextLine);
//
//                if (r.code.equals("1") && !found) { // airport
//
//                    r.getNextField();
//                    r.getNextField();
//                    r.getNextField();
//                    String ac = r.getNextField();
//                    if (ac.equals(airportCode)) {
//                        // airport found
//                        found = true;
//                        nextLine = br.readLine().trim();
//                        while (nextLine != null) {
//                            r = new Record(nextLine);
//                            if ((r.iCode < 111 || r.iCode > 116) && !points.isEmpty()) {
//                                generateSegments(taxiSegName.toString(), points);// generate
//                                                                                 // the
//                                                                                 // previous
//                                                                                 // segments
//                                points.clear();
//                                currentPaintStyle = 2;
//                            }
//
//                            if (r.iCode == 1) {
//                                break; // ready
//                            }
//
//                            if (found && r.code.equals("110")) { // taxiway
//
//                                r.getNextField();
//                                r.getNextField();
//                                r.getNextField();
//                                taxiSegName = new StringBuilder();
//                                while (r.hasNext()) {
//                                    taxiSegName.append(r.getNextField());
//                                    if (r.hasNext())
//                                        taxiSegName.append(" ");
//                                }
//                            }
//                            if (r.iCode > 110 && r.iCode < 117) {
//                                // point has been found
//                                String lat = r.getNextField();
//                                String lon = r.getNextField();
//                                String ctrlLat = null;
//                                String ctrlLon = null;
//                                if (r.iCode == 112 || r.iCode == 114 || r.iCode == 116) {
//                                    ctrlLat = r.getNextField();
//                                    ctrlLon = r.getNextField();
//                                }
//                                ArrayList<Integer> listLineTypeCodes = new ArrayList<Integer>();
//                                while (r.hasNext()) {
//                                    int i = Integer.parseInt(r.getNextField());
//                                    if (i < 100)
//                                        currentPaintStyle = i;
//                                    listLineTypeCodes.add(i);
//                                }
//                                TaxiPoint tp = new TaxiPoint(r.code, lat, lon, ctrlLat, ctrlLon, currentPaintStyle);
//                                points.add(tp);
//                            }
//
//                            if (r.iCode == 20) {
//                                // sign found
//                                String lat = r.getNextField();
//                                String lon = r.getNextField();
//                                String heading = r.getNextField();
//                                r.getNextField(); // not used
//                                String size = r.getNextField();
//                                String text = r.getNextField();
//                                TaxiSign sign = new TaxiSign(lat, lon, heading, size,text);
//                                signs.add(sign);
//                            }
//                            nextLine = br.readLine().trim();
//                        }
//                    }
//                }
//
//                nextLine = br.readLine();
//            }
//        } catch (IOException e) {
//            throw new IllegalArgumentException("Problems to read sectors/AptNav1000.zip");
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//    }

//    private void generateSegments(String taxiSegName, ArrayList<TaxiPoint> points) {
//        // connect points to segments
//        if (points.size() > 0) {
//            TaxiPoint lastPoint = points.get(0);
//            for (int i = 1; i < points.size(); i++) {
//                TaxiPoint p = points.get(i);
//                TaxiWaySegment seg = new TaxiWaySegment(taxiSegName, lastPoint, p, false);
//                this.taxiwaySegmentList.add(seg);
//                lastPoint = p;
//            }
//            if (lastPoint.getIndex() == 113 || lastPoint.getIndex() == 114) {
//                // close loop nodes
//                TaxiWaySegment seg = new TaxiWaySegment(taxiSegName.toString(), lastPoint, points.get(0), false);
//                this.taxiwaySegmentList.add(seg);
//            }
//        }
//    }

    protected InputStream getZipAptFileInputStream() throws IOException {
        String archive = "data" + File.separator + "AptNav1000.zip";
        String filename = "apt.dat";

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
                log.error("Error while closing zip file!",e);
            }
        }
    }

    public List<TaxiWaySegment> getTaxiWaySegments() {
        return taxiwaySegmentList;
    }

    public List<TaxiSign> getTaxiSigns() {
        return signs;
    }

//    private class Record {
//        String code;
//        int iCode;
//        private StringTokenizer st;
//
//        public Record(String line) {
//            if (line.trim().isEmpty()) {
//                code = "-1";
//                iCode = -1;
//            } else {
//                st = new StringTokenizer(line, " \t");
//                code = st.nextToken();
//                try {
//                    iCode = Integer.parseInt(code);
//                } catch (Exception e) {
//                    iCode = -1;
//                }
//            }
//        }

//        public String getNextField() {
//            return st.hasMoreTokens() ? st.nextToken() : "";
//        }
//
//        public boolean hasNext() {
//            return st.hasMoreElements();
//        }
//    }
}
