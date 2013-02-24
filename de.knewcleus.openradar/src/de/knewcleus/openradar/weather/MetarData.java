/**
 * Copyright (C) 2012,2013 Wolfram Wagner
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import de.knewcleus.openradar.gui.setup.AirportData;

/**
 * This class parses and delivers the METAR information.
 *
 * KSFO 140705Z 29010KT 10SM BKN002 14/13 A3020 RMK AO2
 * http://de.wikipedia.org/wiki/METAR
 * Airport date time wind:DDDSS
 * A => Airpressure in inHG
 * Q => Airpressure in hectorpascal
 *
 *
 * 2012/10/14 18:13KSFO 141813Z 32008KT 10SM SCT008 16/12 A3022 RMK AO2
 *
 * @author Wolfram Wagner
 */
public class MetarData {

    private String metarBaseData = null;
    private AirportData data = null;

    private String airportCode = null;
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private Date observationTime = null;

    // wind
    private boolean windDirectionVariates = false;
    private String windUnit  = "KT";
    private int windDirection = -1;
    private int windDirectionMin = -1;
    private int windDirectionMax = -1;
    private int windSpeed = -1;
    private int windSpeedGusts = -1;
    private double windFromNorth = -1;
    private double windFromWest = -1;

    // visibility && weather phenomena
    private float visibility;
    private String visibilityUnit;
    private int temperature;
    private int dewPoint;


    // clouds
    public enum CloudDensity { CAVOK,CLR,NSC,NCD,FEW,SCT,BKN,OVC}
    private CloudDensity cloudDensity;
    private int cloudBase;
    public enum CloudType { TCU, CB }
    private CloudType cloudType = null;

    // air presssure
    private float pressureInHG;
    private float pressureHPa;

    // color code (military)
    boolean cavok = false;

    // trend
    private enum Trend { NOSIG, BECMG, TEMPO}
    private Trend trend = null;

    public MetarData(AirportData data, String metar) {
        // parse metar data
        this.metarBaseData = metar.substring(metar.indexOf("\n")).trim();
        this.data=data;
        StringTokenizer st = new StringTokenizer(metar," \n");

        try {
            observationTime = sdf.parse(st.nextToken()+" "+st.nextToken());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        airportCode=st.nextToken();

        String t = st.nextToken(); // former last update code 141813Z

        t = st.nextToken();
        if(t.matches("AUTO")) {
            // automatic station
            t = st.nextToken();
        }

        if(t.matches("^[A-Z]{3}")) {
            // correction indicator
            t = st.nextToken();
        }

        if(t.matches("^.*KT.*$")) {
            parseWind(t,"KT");
            t = st.nextToken();
        }
        if(t.matches("^.*KMH.*$")) {
            parseWind(t,"KMH");
            t = st.nextToken();
        }
        if(t.matches("^.*MPS.*$")) {
            parseWind(t,"MPS");
            t = st.nextToken();
        }

        if(t.matches("^[\\d]{3}V[\\d]{3}$")) {
            parseVariations(t);
            t = st.nextToken();
        }

        if(t.matches("^[\\d]{4}$") || t.matches("^[\\d/]{1,4}SM$")) {
            parseVisibility(t);
            t = st.nextToken();
        }

        if(t.matches("^[\\d]{4}.*$")) {
            parseDirectionalVisibility(t);
        }
        while(st.hasMoreElements()) {
            t = st.nextToken();
            if(t.matches("^R.*/.*$")) parseRunwayVisibility(t);
            if(t.matches("^A[\\d]{4}$")) parsePressureInHG(t);
            else if(t.matches("^Q[\\d]{4}$")) parsePressureHPa(t);
            else if(t.matches("^[\\d]{2}/[\\d]{2}$")) parseTemperatures(t);
            else if(t.matches("^RMK$")) break; // means "national information follow
            else parsePhenomena(t);
        }

            double angle = (double)(getWindDirection()-270)/360d*2*Math.PI;
            windFromNorth = Math.sin(angle)*(double)getWindSpeed();
            windFromWest = Math.cos(angle)*(double)getWindSpeed();
            //System.out.println("Wind N: "+windFromNorth+" W: "+windFromWest);
    }

    private void parseWind(String t, String unit) {
        windUnit = unit;
        if(t.startsWith("VRB")) {
            // variable
            windDirection=-1;
        } else {
            windDirection=(Integer.parseInt(t.substring(0,3)) - (int)data.getMagneticDeclination());
            windDirection=windDirection<0?windDirection+360:windDirection;
        }
        windSpeed=Integer.parseInt(t.substring(3,5));
        if(t.charAt(5)=='G') {
            // gusts
            windSpeedGusts=Integer.parseInt(t.substring(6,t.indexOf(unit)));
        }
    }
    private void parseVariations(String t) {
        windDirectionMin=Integer.parseInt(t.substring(0,3)) - (int)data.getMagneticDeclination();
        windDirectionMin=windDirectionMin<0?windDirectionMin+360:windDirectionMin;
        windDirectionMax=Integer.parseInt(t.substring(4)) - (int)data.getMagneticDeclination();
        windDirectionMax=windDirectionMax<0?windDirectionMax+360:windDirectionMax;
    }

    private void parseVisibility(String t) {
        if(t.matches("^[\\d]{4}$")) {
            visibility=Integer.parseInt(t);
            visibilityUnit="m";
        } else if(t.matches("^[\\d]{1,4}SM$")) {
            visibility=Integer.parseInt(t.substring(0,t.indexOf("S")));
            visibilityUnit="SM";
        } else if(t.matches("^[\\d]{1,4}/[\\d]{1,4}SM$")) {
            visibility=Integer.parseInt(t.substring(0,t.indexOf("/")));
            visibility=visibility/Integer.parseInt(t.substring(t.indexOf("/")+1,t.indexOf("SM")));
            visibilityUnit="SM";
        } else {
            System.out.println("Unparsed visibility: "+t);
        }

    }
    private void parseDirectionalVisibility(String t) {
        // TODO Auto-generated method stub

    }
    private void parseRunwayVisibility(String t) {
        // TODO Auto-generated method stub

    }
    private void parsePressureInHG(String t) {
        pressureInHG = Float.parseFloat(t.substring(1))/100;
        pressureHPa = 33.86389f * pressureInHG;
    }
    private void parsePressureHPa(String t) {
        pressureHPa = Float.parseFloat(t.substring(1));
         pressureInHG = pressureHPa / 33.86389f ;
    }
    private void parseTemperatures(String t) {
        int sep = t.indexOf("/");
        temperature = Integer.parseInt(t.substring(0,sep));
        dewPoint = Integer.parseInt(t.substring(sep+1));
    }
    private void parsePhenomena(String t) {
        if("CAVOK".equals(t)) {
            cavok=true;
            visibility=10000;
            visibilityUnit="m";
        }
    }

    public String getMetarBaseData() {
        return metarBaseData;
    }

    public String getAirportCode() {
        return airportCode;
    }

    public Date getObservationTime() {
        return observationTime;
    }

    public boolean isWindDirectionVariates() {
        return windDirectionVariates;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public int getWindDirectionMin() {
        return windDirectionMin;
    }

    public int getWindDirectionMax() {
        return windDirectionMax;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public int getWindSpeedGusts() {
        return windSpeedGusts;
    }

    public String getVisibility() {
        if(isCavok()) {
            return ">10";
        }
        if("NM".equalsIgnoreCase(getVisibilityUnit())) {
            return String.format("%2.1f", visibility);
        } else {
            // meter
            return String.format("%2.0f", visibility);
        }
    }

    public String getVisibilityUnit() {
        if(isCavok()) {
            return "km";
        }
        return visibilityUnit==null?"SM":visibilityUnit;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getDewPoint() {
        return dewPoint;
    }

    public CloudDensity getCloudDensity() {
        return cloudDensity;
    }

    public int getCloudBase() {
        return cloudBase;
    }

    public CloudType getCloudType() {
        return cloudType;
    }

    public float getPressureInHG() {
        return pressureInHG;
    }

    public float getPressureHPa() {
        return pressureHPa;
    }

    public Trend getTrend() {
        return trend;
    }

    public double getWindNorthComponent() {
        return windFromNorth;
    }

    public double getWindWestComponent() {
        return windFromWest;
    }

    public boolean isCavok() {
        return cavok;
    }

    public Object getWindDisplayString() {
        StringBuffer sb = new StringBuffer();
        if(getWindDirection()==-1) {
            sb.append("VRB");
        } else {
            sb.append(getWindDirection());
        }
        if(getWindDirectionMin()>-1) {
            sb.append("(");
            sb.append(getWindDirectionMin());
            sb.append("-");
            sb.append(getWindDirectionMax());
            sb.append(")");
        }
        sb.append("@");
        sb.append(getWindSpeed());
        if(getWindSpeedGusts()>-1) {
            sb.append("(");
            sb.append(getWindSpeedGusts());
            sb.append(")");
        }
        return sb.toString();
    }
}
