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
package de.knewcleus.openradar.gui.setup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.knewcleus.fgfs.Units;

public class AircraftCodeConverter {

    private List<AircraftDefinition> aircraftList = new ArrayList<AircraftDefinition>();

    private final static Logger log = LogManager.getLogger(AircraftCodeConverter.class);
    
    public AircraftCodeConverter() {
        BufferedReader ir = null;

        try {
            ir = new BufferedReader(new FileReader("data/aircraftCodes.txt"));
            String line = removeComment(ir.readLine().trim());
            while(line!=null) {
                if(!line.isEmpty()) {
                    try {
                        StringTokenizer st = new StringTokenizer(line,",");
                        st.countTokens();
                        String regex = st.nextToken();
                        String icao = st.nextToken();
                        String cruiseSpeed = st.hasMoreTokens() ? st.nextToken() : "";
                        String turbulanceClass = st.hasMoreTokens() ? st.nextToken(): "";
                        aircraftList.add(new AircraftDefinition(regex, icao, getMphToKnots(cruiseSpeed), turbulanceClass));
                    } catch(NoSuchElementException e) {
                        log.error("File data/aircraftCodes.txt: Missing fields (should be 4, comma separated): "+line);
                    }
                }
                line = ir.readLine();
                if(line!=null) {
                    line = removeComment(line.trim());
                }
            }
        } catch (IOException e) {
            log.error("File data/aircraftCodes.txt could not be read", e);
        } finally {
            if(ir!=null) {
                try {
                    ir.close();
                } catch (IOException e) {}
            }
        }
    }

    private String getMphToKnots(String cruiseSpeed) {
        if(!cruiseSpeed.isEmpty()) {
            try {
                double mph = Double.parseDouble(cruiseSpeed);
                return String.format("%01.0f",Units.getMphToKnots(mph)); 
            } catch (Exception e) {
                log.error("Problem to convert cruisespeed to number: "+cruiseSpeed,e);
            }
        }
        return "";
    }

    private String removeComment(String line) {
        if(line.contains("#")) {
            line = line.substring(0,line.indexOf("#"));
        }
        return line.trim();
    }

    public String convert(String modelName) {
        for(AircraftDefinition ad : aircraftList) {
            if(modelName.matches(ad.regex)) {
                return ad.icao;
            }
        }
        return checkLength(modelName, 10) ;
    }

    public String getCruiseSpeed(String modelName) {
        for(AircraftDefinition ad : aircraftList) {
            if(modelName.matches(ad.regex)) {
                return ad.cruiseSpeed;
            }
        }
        return "" ;
    }
    
    public String getTurbulanceClass(String modelName) {
        for(AircraftDefinition ad : aircraftList) {
            if(modelName.matches(ad.regex)) {
                return ad.turbulanceClass;
            }
        }
        return "" ;
    }

    public static String checkLength(String source, int length) {
        return source.length()>length ? source.substring(0,length):source;
    }

    private class AircraftDefinition {
        public AircraftDefinition(String regex, String icao, String cruiseSpeed, String turbulanceClass) {
            this.regex=regex;
            this.icao=icao;
            this.cruiseSpeed=cruiseSpeed;
            this.turbulanceClass=turbulanceClass;
        }

        public final String regex;
        public final String icao;
        public final String cruiseSpeed;
        public final String turbulanceClass;
    }
}
