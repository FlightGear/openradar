/**
 * Copyright (C) 2012,2015 Wolfram Wagner
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
package de.knewcleus.openradar.gui.setup;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

/**
 * This class bundles the code to find an airport location and download
 * airports.
 *
 * @author Wolfram Wagner
 */
public abstract class SectorCreator {

    // "http://mapserver.flightgear.org/dlaction?layer=<layername>&xmin=<degree>&xmax=<degree>&ymin=<degree>&ymax=<degree>";
    private static String address = "http://mapserver.flightgear.org/dlsingle?";
    private static String[] layers = { "v0_landmass", "cs_urban", "cs_lake", "osm_river", "apt_airfield", "apt_runway", "apt_tarmac" };
    private static double mapWidth = 10; // degrees
    private static double mapHeight = 10; // degrees
    private static final Logger log = Logger.getLogger(SectorCreator.class);
    
    public static Point2D findLocationOf(String searchTerm) {
        ZipFile zif = null;;
        BufferedReader ir = null;
        Point2D position = null;
        
        try {
            final File inputFile = new File("data/AptNav.zip");
            zif = new ZipFile(inputFile);
            Enumeration<? extends ZipEntry> entries = zif.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipentry = entries.nextElement();
                if (zipentry.getName().equals("apt.dat")) {
                    ir = new BufferedReader(new InputStreamReader(zif.getInputStream(zipentry)));
                    break;
                }
            } 
            if(ir==null) return null;
            
            String line = ir.readLine();
            while(line!=null) {
                if(line.startsWith("1 ")) {
                    StringTokenizer st = new StringTokenizer(line," \t");
                    st.nextElement(); // code "1"
                    st.nextElement(); //
                    st.nextElement(); //
                    st.nextElement(); //
                    String airportCode = st.nextToken();
                    StringBuilder name = new StringBuilder();
                    while(st.hasMoreElements()) {
                        if(name.length()>0) name.append(" ");
                        name.append(st.nextToken());
                    }
                    if(airportCode.equalsIgnoreCase(searchTerm)) {
                        
                        // seach runway
                        line = ir.readLine();
                        while(line!=null) {
                            if(line.startsWith("100 ")) {
                                position = SetupController.getRunwayPosition(line);
                                return position;
                            }
                            else if(line.startsWith("1 ")) {
                                // next airport
                                break;
                            }
                            // next line
                            line = ir.readLine();
                        }
                    } else {
                        // airport name did not match
                        line = ir.readLine();
                    }
                }
                if(!line.startsWith("1 ")) {
                    line = ir.readLine();
                }
            }
        } catch (IOException e) {
            log.error("Error while reading xplane aptdat file!",e);
        } finally {
            if(zif!=null) {
                try {
                    zif.close();
                } catch (IOException e) { }
            }
            if(ir!=null) {
                try {
                    ir.close();
                } catch (IOException e) {}
            }
        }
        return position;
    }

    public static void downloadData(AirportData data, SetupDialog setupDialog) throws IOException {

        // 2x2 degrees
        Point2D upperLeftCorner = new Point2D.Double(data.getLon() - mapWidth / 2, data.getLat() - mapHeight / 2);
        Point2D lowerRightCorner = new Point2D.Double(data.getLon() + mapWidth / 2, data.getLat() + mapHeight / 2);

        File dir = new File("data" + File.separator + data.getAirportCode());
        if (!dir.exists())
            dir.mkdirs();

        // Create sector.properties
        Properties p = new Properties();
        p.put("airportName", data.getAirportName());
        p.put("metarSource", data.getAirportCode());
        p.put("lat", Double.toString(data.getLat()));
        p.put("lon", Double.toString(data.getLon()));
       
        SetupController.saveSectorProperties(data.getAirportCode(), p);

        // download shapefiles
        for (int i = 0; i < layers.length; i++) {
            setupDialog.setStatus((i) * 100 / layers.length, "Downloading " + layers[i] + "...");
            downloadZip(data, layers[i], upperLeftCorner, lowerRightCorner);
            setupDialog.setStatus((i + 1) * 100 / layers.length, "..." + layers[i] + " downloaded");
        }
        setupDialog.setStatus(0, "Ready.");
    }

    private static void downloadZip(AirportData data, String layer, Point2D upperLeftCorner, Point2D lowerRightCorner) throws IOException {
        HttpURLConnection con = null;
        InputStream in = null;
        FileOutputStream out = null;
        try {

            File targetFile = new File("data" + File.separator + data.getAirportCode() + File.separator + layer + ".zip");
            if (targetFile.exists()) {
                targetFile.delete();
            }
            String sUrl = address
                    + String.format("layer=%1$s&xmin=%2$1.2f&xmax=%3$1.2f&ymin=%4$1.2f&ymax=%5$1.2f", layer, upperLeftCorner.getX(), lowerRightCorner.getX(),
                            upperLeftCorner.getY(), lowerRightCorner.getY());
            URL url = new URL(sUrl.replaceAll(",", "."));
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            byte[] buffer = new byte[4096];
            if (responseCode == 200) {
                in = con.getInputStream();
                int byteCount = -1;
                out = new FileOutputStream(targetFile);
                while ((byteCount = in.read(buffer)) > 0) {
                    out.write(buffer, 0, byteCount);
                }
            } else {
                throw new IOException("Server responded with error " + responseCode + " " + con.getResponseMessage());
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (con != null) {
                con.disconnect();
            }
        }
    }

    public static void deleteDir(SectorBean selectedSector) {
        try {
            Path dirToDelete = FileSystems.getDefault().getPath("data" + File.separator + selectedSector.getAirportCode());
            if(Files.exists(dirToDelete,LinkOption.NOFOLLOW_LINKS)) {
                File[] content = dirToDelete.toFile().listFiles();
                if(content!=null) {
                    for(File f : content) {
                        f.delete();
                    }
                }
                Files.deleteIfExists(dirToDelete);
            }
        } catch(IOException e) {
            log.error("Error while deleting airport dir!", e);
            JOptionPane.showMessageDialog(null,
                    "Connect cleanup sector "+selectedSector.getAirportCode()+"! ("+e.getMessage()+")",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
