package de.knewcleus.openradar.gui.setup;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * This class bundles the code to find an airport location and download
 * airports.
 * 
 * @author Wolfram Wagner
 */
public abstract class SectorCreator {

    // "http://mapserver.flightgear.org/dlaction?layer=<layername>&xmin=<degree>&xmax=<degree>&ymin=<degree>&ymax=<degree>";
    private static String address = "http://mapserver.flightgear.org/dlaction?";
    private static String[] layers = { "v0_landmass", "v0_lake", "v0_stream", "apt_airfield", "apt_runway", "apt_tarmac" };
    private static double mapWidth = 10; // degrees
    private static double mapHeight = 10; // degrees

    public static void findLocationOf(AirportData dataRegistry) {
        File fgAirportIndexFile = new File("data/AirportIndex.txt");
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
        Point2D upperLeftCorner = new Point2D.Double(data.getLon() - mapWidth / 2, data.getLat() - mapHeight / 2);
        Point2D lowerRightCorner = new Point2D.Double(data.getLon() + mapWidth / 2, data.getLat() + mapHeight / 2);

        File dir = new File("data" + File.separator + data.getAirportCode());
        if (!dir.exists())
            dir.mkdirs();

        for (int i = 0; i < layers.length; i++) {
            setupDialog.setStatus((i - 1) * 100 / layers.length, "Downloading " + layers[i] + "...");
            downloadZip(data, layers[i], upperLeftCorner, lowerRightCorner);
            setupDialog.setStatus((i + 1) * 100 / layers.length, "..." + layers[i] + " downloaded");
        }
        setupDialog.setStatus(100, "Ready.");
        // Create sector.properties
        File sectorFile = new File("data" + File.separator + data.getAirportCode() + File.separator + "sector.properties");
        Properties p = new Properties();
        p.put("airportName", data.getAirportName());
        p.put("metarSource", data.getAirportCode());
        p.put("lat", Double.toString(data.getLat()));
        p.put("lon", Double.toString(data.getLon()));
        p.put("magneticDeclination", Double.toString(loadMagneticDeclination(data)));

        FileWriter userWriter = null;
        try {
            if (sectorFile.exists())
                sectorFile.delete();
            userWriter = new FileWriter(sectorFile);

            p.store(userWriter, "Open Radar Sector property file");
        } catch (IOException e) {

        } finally {
            if (userWriter != null) {
                try {
                    userWriter.close();
                } catch (IOException e) {
                }
            }
        }
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

    /**
     * 
     * URL:
     * http://www.ngdc.noaa.gov/geomag-web/calculators/calculateDeclination?
     * startYear
     * =2012&startMonth=11&startDay=15&resultFormat=csv&lon1Hemisphere=W
     * &lon1=122.220624012373
     * &lat1Hemisphere=N&lat1=37.7214006137091&browserRequest=true
     * 
     * ################################################## # Declination Values
     * ################################################## # 5 Fields: # (1) Date
     * in decimal years # (2) Latitude in decimal degrees # (3) Longitude in
     * decimal degrees # (4) Declination in decimal degrees # (5) Change in
     * declination in decimal minutes / year # # Magnetic Model: IGRF #
     * Elevation: 0.00000 km ##################################################
     * 
     * 2012.87158,37.72140,-122.22060,13.98877,-6.17181
     * 
     * @param data
     * @return
     */
    public static double loadMagneticDeclination(AirportData data) throws IOException {
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
        SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
        Date today = new Date();
        boolean isEast = data.getLon() >= 0;
        boolean isNorth = data.getLat() >= 0;

        double declination = 0d; // fallback
//www.ngdc.noaa.gov/geomagmodels/Declination.jsp
        StringBuffer sbUrl = new StringBuffer("http://www.ngdc.noaa.gov/geomag-web/calculators/calculateDeclination?");
        sbUrl.append("startYear=").append(sdfYear.format(today));
        sbUrl.append("&startMonth=").append(sdfMonth.format(today));
        sbUrl.append("&startDay=").append(sdfDay.format(today));
        sbUrl.append("&resultFormat=csv");
        sbUrl.append("&lon1Hemisphere=").append((isEast ? "E" : "W"));
        sbUrl.append("&lon1=").append(String.format("%.12f", Math.abs(data.getLon())));
        sbUrl.append("&lat1Hemisphere=").append((isNorth ? "N" : "S"));
        sbUrl.append("&lat1=").append(String.format("%.12f", Math.abs(data.getLat())));
        sbUrl.append("&browserRequest=false");

        HttpURLConnection con = null;
        BufferedReader br = null;
        try {
            con = (HttpURLConnection) new URL(sbUrl.toString()).openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("ISO-8859-1")));
                String line = br.readLine();
                while (line != null) {
                    if (!line.trim().startsWith("#") && line.trim().length() > 0) {
                        StringTokenizer st = new StringTokenizer(line, ",");
                        st.nextToken(); // date in a weird format
                        st.nextToken(); // latitude
                        st.nextToken(); // longitude
                        String sDeclination = st.nextToken(); // declination
                        declination = Double.parseDouble(sDeclination);
                        break;
                    }
                    line = br.readLine();
                }
            } else {
                throw new IOException("Server responded with error " + responseCode + " " + con.getResponseMessage());
            }
        } finally {
            if (br != null) {
                br.close();
            }
            if (con != null) {
                con.disconnect();
            }
        }
        return declination;
    }
}
