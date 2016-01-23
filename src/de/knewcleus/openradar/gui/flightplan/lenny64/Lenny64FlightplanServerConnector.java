/**
 * Copyright (C) 2014-2016 Wolfram Wagner
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
package de.knewcleus.openradar.gui.flightplan.lenny64;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData.FlightPlanStatus;
import de.knewcleus.openradar.gui.setup.AirportData;

/**
 * Interface to lenny64's webpage. Pilots can file their FP there and OR will download it.
 * 
 * ident 39c89680
 * 
 * @author Wolfram Wagner
 * 
 */
public class Lenny64FlightplanServerConnector {

    private static final Logger log = LogManager.getLogger(Lenny64FlightplanServerConnector.class.getName());

    public List<FlightPlanData> checkForFlightplan(AirportData data, GuiRadarContact contact) {

        String callsign = contact.getCallSign();
        // http://lenny64.free.fr/dev2014_01_13.php5?getFlightplans&callsign=
        // http://flightgear-atc.alwaysdata.net/dev2014_01_13.php5
        String baseUrl = data.getFpDownloadUrl();
        List<FlightPlanData> result = new ArrayList<FlightPlanData>();

        log.warn("Flightplan: " + data.getCallSign() + " Going to download existing flightplans for " + callsign + " from " + baseUrl);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            StringBuilder answer = new StringBuilder();
            String line = null;

            String parameters = "callsign=" + URLEncoder.encode(callsign, "UTF-8") //"&airport=*"// +
                                                                                                 // URLEncoder.encode(data.getAirportCode(),
                                                                                                 // "UTF-8")
                    + "&date=" + URLEncoder.encode(sdf.format(new Date()), "UTF-8");
            URL url = new URL(baseUrl + "?getFlightplans&" + parameters);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            con.setRequestProperty("User-Agent", "OpenRadar");
            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            // dos.write(parameters.getBytes("UTF-8"));
            dos.flush();
            dos.close();

            // parse response

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                line = reader.readLine();
                while (line != null) {
                    answer.append(line).append("\n");
                    line = reader.readLine();
                }
                reader.close();

                try {
                    Document document = new SAXBuilder().build(new StringReader(answer.toString().trim()));
                    // String version = document.getRootElement().getAttributeValue("version");

                    result = Lenny64XmlParser.parse(data, contact, document);

                } catch (IOException e) {
                    log.error("Error while parsing flightplan from lenny64!", e);
                    result = new ArrayList<FlightPlanData>();
                }
                log.warn("Flightplan download processing finished. "+result.size()+" FPs found!");
            } else {
                log.warn("Flightplan: " + data.getCallSign() + " Failed to retrieve flightplan from lenny64! (got response code " + responseCode + " from "
                        + url.toString() + ")...");
            }
        } catch (ConnectException e) {
            log.error("Problem to connect to lenny64 server: " + e.getMessage());
        } catch (Exception e) {
            log.error("Problem to parse updated lenny63 flightplans!", e);
        }

        return result;
    }

    public void openFlightPlan(GuiMasterController master, GuiRadarContact contact) {
        if(openOrCloseFlightPlan("openFlightplan", master, contact)) {
            // delete FP Data
            contact.getFlightPlan().setFpStatus(FlightPlanStatus.ACTIVE.toString());
            contact.getFlightPlan().setReadyForTransmission();
            master.getFlightPlanExchangeManager().triggerTransmission();
        }
    }

    public void closeFlightPlan(GuiMasterController master, GuiRadarContact contact) {
        if(openOrCloseFlightPlan("closeFlightplan", master, contact)) {
            // delete FP Data
            contact.getFlightPlan().reset();
            contact.getFlightPlan().setReadyForTransmission();
            master.getFlightPlanExchangeManager().triggerTransmission();
        }
    }
        
    public boolean openOrCloseFlightPlan(String method, GuiMasterController master, GuiRadarContact contact) {
        String callsign = contact.getCallSign();
        // http://lenny64.free.fr/dev2014_01_13.php5?closeFlightplans&callsign=
        // http://flightgear-atc.alwaysdata.net/dev2014_01_13.php5
        String baseUrl = master.getAirportData().getFpDownloadUrl();
        AirportData data = master.getAirportData();
        String code = contact.getFlightPlan().getFlightPlanId();
        
        log.warn("Flightplan: " + data.getCallSign() + " Going to close flightplan " + code + " for " + callsign + " from " + baseUrl);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            String parameters = "ident=openradar&flightplanId="+contact.getFlightPlan().getFlightPlanId();//+"&callsign=" + URLEncoder.encode(callsign, "UTF-8") + "&airport=*"// +
                                                                                                 // URLEncoder.encode(data.getAirportCode(),
                                                                                                 // "UTF-8")
                    //+ "&date=" + URLEncoder.encode(sdf.format(new Date()), "UTF-8");
            URL url = new URL(baseUrl + "?"+method+"&" + parameters);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            con.setRequestProperty("User-Agent", "OpenRadar");
            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            // dos.write(parameters.getBytes("UTF-8"));
            dos.flush();
            dos.close();

            // parse response

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                return true;
            } else {
                log.warn("Flightplan: " + data.getCallSign() + " Failed to close flightplan "+code+" from lenny64! (got response code " + responseCode + " from "
                        + url.toString() + ")...");
            }
        } catch (ConnectException e) {
            log.error("Problem to connect to lenny64 server: " + e.getMessage());
        } catch (Exception e) {
            log.error("Problem to parse updated lenny63 flightplans!", e);
        }
        return false;
    }
}
