package de.knewcleus.openradar.gui.setup;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * This class bundles the code to find an airport location and download airports.
 * 
 * @author Wolfram Wagner
 */
public abstract class SectorCreator {
    
    // "http://mapserver.flightgear.org/dlaction?layer=<layername>&xmin=<degree>&xmax=<degree>&ymin=<degree>&ymax=<degree>";
    private static String address = "http://mapserver.flightgear.org/dlaction?";
    private static String[] layers = {"v0_landmass","v0_lake","v0_stream","apt_airfield","apt_runway","apt_tarmac"};
    private static double mapWidth = 2; // degrees
    private static double mapHeight = 2; // degrees
    
    public static void findLocationOf(AirportData dataRegistry) {
        File fgAirportIndexFile = new File("sectors/index.txt");
        if (!fgAirportIndexFile.exists()) {
            throw new IllegalArgumentException("Path to flightgear seems to be wrong. Cannot find " + fgAirportIndexFile.getAbsolutePath());
        }

        String airportCode = dataRegistry.getAirportCode();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(fgAirportIndexFile));
            String nextLine = br.readLine();
            while (nextLine != null) {
                if (nextLine.startsWith(airportCode)) {
                    StringTokenizer st = new StringTokenizer(nextLine, "|");
                    st.nextElement(); // skip airport code
                    dataRegistry.setAirportPosition(new Point2D.Double(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken())));
                    break;
                }
                nextLine = br.readLine();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Problems to read " + fgAirportIndexFile.getAbsolutePath());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
    }


    public static void downloadData(AirportData data, SetupDialog setupDialog) throws IOException {
        
        findLocationOf(data); // find lon and lat of airport
        
        // 2x2 degrees
        Point2D upperLeftCorner = new Point2D.Double(data.getLon()-mapWidth/2, data.getLat()-mapHeight/2);
        Point2D lowerRightCorner = new Point2D.Double(data.getLon()+mapWidth/2, data.getLat()+mapHeight/2);
        
        File dir = new File("sectors"+File.separator+data.getAirportCode());
        if(!dir.exists()) dir.mkdirs();
        
        for(int i=0; i<layers.length ; i++) {
            setupDialog.setStatus((i-1)*100/layers.length, "Downloading "+layers[i]+"...");
            downloadZip(data, layers[i], upperLeftCorner, lowerRightCorner);
            setupDialog.setStatus((i)*100/layers.length, "..." + layers[i]+" downloaded");
        }
        setupDialog.setStatus(100, "Ready.");
        // Create sector.properties
        File userFile = new File("sectors" + File.separator + data.getAirportCode()+File.separator+"sector.properties");
        Properties p = new Properties();
        p.put("airportName", data.getAirportName());
        p.put("lat", Double.toString(data.getLat()));
        p.put("lon", Double.toString(data.getLon()));

        FileWriter userWriter = null;
        try {
            if (userFile.exists())
                userFile.delete();
            userWriter = new FileWriter(userFile);

            p.store(userWriter, "Open Radar Sector property file");
        } catch (IOException e) {

        } finally {
            if (userWriter != null) {
                try {
                    userWriter.close();
                } catch (IOException e) {}
            }
        }
    }
    
    private static void downloadZip(AirportData data, String layer, Point2D upperLeftCorner, Point2D lowerRightCorner) throws IOException {
        HttpURLConnection con=null;
        InputStream in = null;
        FileOutputStream out = null;
        try {
        
            File targetFile = new File("sectors"+File.separator+data.getAirportCode()+File.separator+layer+".zip");
            if(targetFile.exists()) {
                targetFile.delete();
            }
            String sUrl = address + String.format("layer=%1$s&xmin=%2$1.2f&xmax=%3$1.2f&ymin=%4$1.2f&ymax=%5$1.2f", layer, upperLeftCorner.getX(), lowerRightCorner.getX(),upperLeftCorner.getY(), lowerRightCorner.getY());
            URL url = new URL(sUrl.replaceAll(",","."));
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
                throw new IOException("Server responded with error "+responseCode+" "+con.getResponseMessage());
            }
        }  finally {
            if(in!=null) {
                in.close();
            }
            if(out!=null) {
                out.close();
            }
            if(con!=null) {
                con.disconnect();
            }
        }
    }
}
