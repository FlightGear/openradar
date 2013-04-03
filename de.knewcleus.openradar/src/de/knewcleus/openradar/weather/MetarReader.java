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

            metar = new MetarData(data, result.toString());
            if(lastMetar==null || !lastMetar.equals(metar)) {

                lastMetar = metar;
                for (IMetarListener l : listener) {
                    l.registerNewMetar(metar);
                }
                System.out.println("Metar received: " + metar.getMetarBaseData());
            }
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
