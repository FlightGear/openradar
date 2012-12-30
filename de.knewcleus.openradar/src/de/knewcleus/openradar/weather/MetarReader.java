package de.knewcleus.openradar.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.openradar.gui.setup.AirportData;

/**
 * This class downloads the METAR information...
 * 
 * http://weather.noaa.gov/pub/data/observations/metar/stations/KSFO.TXT
 * 
 * @author wolfram
 * 
 */
public class MetarReader implements Runnable {

    private Thread thread = new Thread(this, "OpenRadar - Metar Reader");
    private String baseUrl = null;
    private volatile String airportCode = null;
    private AirportData data = null;
    private volatile boolean isRunning = true;
    private volatile MetarData metar;
    private volatile MetarData lastMetar = null;
    private int sleeptime = 15 * 60 * 1000;

    private List<IMetarListener> listener = new ArrayList<IMetarListener>();

    public void addMetarListener(IMetarListener l) {
        listener.add(l);
    }

    public void removeMetarListener(IMetarListener l) {
        listener.remove(l);
    }

    public MetarReader(AirportData data) {
        this.baseUrl = data.getMetarUrl();
        this.airportCode = data.getMetarSource();
        this.data = data;
        thread.setDaemon(true);
    }

    /**
     * Starts the metar loader after the first metar was loaded. This should prevent problems with 
     * arriving metar in initial screen setup.
     * So this method returns after Metar is loaded. 
     */
    public void start() {
        try {
            loadMetar();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        thread.start();
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Thread.sleep(sleeptime);
                loadMetar();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
            }
        }
    }

    private void loadMetar() throws IOException, MalformedURLException {
        StringBuilder result = new StringBuilder();
        String line = null;
        URL url = new URL(baseUrl + airportCode.toUpperCase() + ".TXT");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            line = reader.readLine();
            while ( line != null) {
                result.append(line).append("\n");
                line = reader.readLine();
            }
            reader.close();
            lastMetar = metar;
            metar = new MetarData(data, result.toString());

            for (IMetarListener l : listener) {
                l.registerNewMetar(metar);
            }
            System.out.println("Metar received: " + metar.getMetarBaseData());
        } else {
            System.out.println("WARNING: No Metar for "+airportCode+"(got response code " + responseCode + " from " + url.toString()+")...");
            System.out.println("Consider setting metarSource property in data/"+airportCode+"/sector.properties !");
        }
    }

    public void stop() {
        isRunning = false;
    }

    public void setAirportCode(String airportCode) {
        this.airportCode = airportCode;
    }

    public MetarData getMetar() {
        return metar;
    }

    public MetarData getLastMetar() {
        return lastMetar;
    }
}
