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
package de.knewcleus.openradar.gui.flightplan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.contacts.RadarContactController;
import de.knewcleus.openradar.gui.setup.AirportData;

public class FlightPlanExchangeManager implements Runnable{

    private Thread thread = new Thread(this, "OpenRadar - FlightPlanExchange");
    private final String baseUrl;
    private final AirportData data;
    private final RadarContactController radarContactController;
    private volatile boolean isRunning = true;
    private int sleeptime = 5 * 1000;

    public FlightPlanExchangeManager(GuiMasterController master) {
        this.data = master.getDataRegistry();
        this.radarContactController = master.getRadarContactManager();
        this.baseUrl = data.getFpServerUrl();
        thread.setDaemon(true);
    }

    /**
     * Starts the data exchange with the flightplanserver
     */
    public synchronized void start() {
        if(data.isFpExchangeEnabled()) {
            thread.start();
        }
    }

    /**
     * Stops the thread after the possibly running action.
     */
    public synchronized void stop() {
        isRunning = false;
    }
    /**
     * This method can be used to trigger a transmission immediatelly after a data update.
     * Don't call it too often to save resources on the server!
     * Regular update frequency is 5 Seconds.
     */
    public synchronized void triggerTransmission() {
        thread.interrupt();
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                postChangesToServer();
                loadUpdatesFromServer();
                Thread.sleep(sleeptime);
            } catch (InterruptedException e) {
            }

        }
    }

    private void loadUpdatesFromServer() {
        // collect the active contacts in range
        StringBuilder callSignList = new StringBuilder();
        for(GuiRadarContact contact : radarContactController.getContactListCopy()) {
            // we must send all because server releases contacts that are out of range
            if(callSignList.length()>0) {
                callSignList.append(",");
            }
            callSignList.append(contact.getCallSign().trim());
        }
System.out.println("Flightplan: Going to request updates for: "+callSignList);
        // send the list to the server to request updated flightplans

        if(callSignList.length()>0) {
            try {
                StringBuilder result = new StringBuilder();
                String line = null;
                URL url = new URL(baseUrl + "/getFlightplans");
                String parameters = "user="+URLEncoder.encode(data.getFpServerUser(),"UTF-8")+
                        "&password="+URLEncoder.encode(data.getFpServerPassword(),"UTF-8")+
                        "&atc="+URLEncoder.encode(data.getCallSign(),"UTF-8")+
                        "&callsigns="+URLEncoder.encode(callSignList.toString(),"UTF-8");

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestProperty("Accept-Charset", "UTF-8");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                con.getOutputStream().write(parameters.getBytes("UTF-8"));

                // parse response

                int responseCode = con.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    line = reader.readLine();
                    while ( line != null) {
                        result.append(line).append("\n");
                        line = reader.readLine();
                    }
                    reader.close();

                    try {
                        Document document = new SAXBuilder().build(new StringReader(result.toString().trim()));
//                        String version = document.getRootElement().getAttributeValue("version");

                        List<Element> eFlightPlans = document.getRootElement().getChildren("flightplan");
                        for(Element eFp : eFlightPlans) {
                            String callsign =  FpXml_1_0.getCallSign(eFp);
                            GuiRadarContact c = radarContactController.getContactFor(callsign);
                            if(c!=null) {
                                FpXml_1_0.parseXml(data,c,eFp);
                            }
                            System.out.println("Got FP update for: "+callsign);
                        }
                    } catch (IOException e) {
                        Logger.getLogger(FlightPlanExchangeManager.class.toString()).log(Level.SEVERE,"Error while parsing flightplan!",e);
                    }


                } else {
                    System.out.println("Flightplan: Failed to retrieve flightplan updates! (got response code " + responseCode + " from " + url.toString()+")...");
                }
            } catch(Exception e) {
                Logger.getLogger(FlightPlanExchangeManager.class.toString()).log(Level.SEVERE,"Problem to parse updated flightplans!", e);
            }
        } // callsignlist.length>0
    }

    private void postChangesToServer() {
        // collect the updates to send
        for(GuiRadarContact contact : radarContactController.getContactListCopy()) {
            FlightPlanData fp = contact.getFlightPlan();
            if(fp.isChanged() && fp.isOwnedByMe(data)) {
                sendChanges(fp.copy());
            }
        }
    }
    private void sendChanges(FlightPlanData fp) {
        // build XML
        String xml = buildXml(fp);

        // send it
        try {
            String parameters = "user="+URLEncoder.encode(data.getFpServerUser(),"UTF-8")+
                                "&password="+URLEncoder.encode(data.getFpServerPassword(),"UTF-8")+
                                "&atc="+URLEncoder.encode(data.getCallSign(),"UTF-8")+
                                "&flightPlans="+URLEncoder.encode(xml,"UTF-8");

            System.out.println("Flightplan: Going to send updates for: "+fp.getCallsign());

            URL url = new URL(baseUrl + "/updateFlightplans");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestProperty("Accept-Charset", "UTF-8");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            con.getOutputStream().write(parameters.getBytes("UTF-8"));

            // handle response

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                // Everything is fine
                System.out.println("Successfully updated flightplans! (got response code " + responseCode + " from " + url.toString()+")...");
            } else if (responseCode == 406) {
                System.err.println("Failed to update flightplan updates! XML not understood at server (got response code " + responseCode + " "+con.getResponseMessage()+ " from " + url.toString()+")...");
            } else if (responseCode == 409) {
                System.err.println("Failed to update flightplan updates! We are not owner (got response code " + responseCode + " "+con.getResponseMessage()+ " from " + url.toString()+")...");
            } else {
                System.out.println("Failed to update flightplan updates! (got response code " + responseCode + " "+con.getResponseMessage() + " from " + url.toString()+")...");
            }
        } catch(IOException e) {
            Logger.getLogger(FlightPlanExchangeManager.class.toString()).log(Level.SEVERE,"Problem to update flightplans!", e);
        }

    }

    private String buildXml(FlightPlanData fpd) {
        Document doc = new Document();
        Element root = new Element("flightplanList");
        doc.addContent(root);

        try {
            Element elementFp = FpXml_1_0.createXml(fpd);
            if(elementFp!=null) {
                root.addContent(elementFp);
            }
        } catch (Exception ex) {
            Logger.getLogger(FlightPlanExchangeManager.class.getName()).log(Level.SEVERE, "Problem to create flightplan...", ex);
        }
        StringWriter sw = new StringWriter();
        try {
    //        XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            sw = new StringWriter();
            outputter.output(doc, sw);
        } catch(Exception e) {
            Logger.getLogger(FlightPlanExchangeManager.class.getName()).log(Level.SEVERE, "Problem to create XML output...", e);
        }

        return sw.toString();
    }

}
