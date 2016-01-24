/**
 * Copyright (C) 2012,2013,2015 Wolfram Wagner
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
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne
 * die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public
 * License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 * <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.weather;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.chat.auto.AtcMenuChatMessage;
import de.knewcleus.openradar.gui.setup.AirportData;

/**
 * This class parses and delivers the METAR information.
 *
 * KSFO 140705Z 29010KT 10SM BKN002 14/13 A3020 RMK AO2 http://de.wikipedia.org/wiki/METAR Airport date time wind:DDDSS
 * A => Airpressure in inHG Q => Airpressure in hectorpascal
 *
 *
 * 2012/10/14 18:13KSFO 141813Z 32008KT 10SM SCT008 16/12 A3022 RMK AO2
 *
 * @author Wolfram Wagner
 */
public class MetarData {

    private final boolean exists;

    private String metarBaseData = null;
    private AirportData data = null;

    private String airportCode = null;
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private Date observationTime = null;

    // wind
    private boolean windDirectionVariates = false;
    private String windUnit = "KT";
    private String windDirection = null;
    private int windDirectionI = -1;
    private String windDirectionMin = null;
    private String windDirectionMax = null;
    private int windSpeed = -1;
    private int windSpeedGusts = -1;
    private double windFromNorth = -1;
    private double windFromWest = -1;

    // visibility && weather phenomena
    private float visibility;
    private float visibilitySM;
    private String visibilityUnit;
    private int temperature;
    private int dewPoint;

    private int ceiling;
    private String flightConditions;
    private Color flightConditionColor; 

    // clouds
    public enum CloudDensity {
        CAVOK, CLR, NSC, NCD, FEW, SCT, BKN, OVC
    }

    private CloudDensity cloudDensity;
    private int cloudBase;

    public enum CloudType {
        TCU, CB
    }

    private CloudType cloudType = null;

    // air presssure
    private float pressureInHG;
    private float pressureHPa;

    // color code (military)
    boolean cavok = false;

    private String weatherPhaenomena = "";
    private String weatherPhaenomenaHuman;

    // trend
    private enum Trend {
        NOSIG, BECMG, TEMPO
    }

    private Trend trend = null;

    private ArrayList<String> atisVariables;

    private long created = System.currentTimeMillis();

    private final static Logger log = LogManager.getLogger(MetarData.class);

    public MetarData(AirportData data, String metar) {
        exists = true;

        // parse metar data
        this.metarBaseData = metar.substring(metar.indexOf("\n")).trim();
        this.data = data;
        StringTokenizer st = new StringTokenizer(metarBaseData, " ");

        try {
            observationTime = sdf.parse(metar.substring(0,metar.indexOf("\n")));
        } catch (ParseException e) {
            log.error("Error while parsing observation time!", e);
        }

        airportCode = st.nextToken();

        try {
            String t = st.nextToken(); // former last update code 141813Z

            t = st.nextToken();
            if (t.matches("AUTO")) {
                // automatic station
                t = st.nextToken();
            }

            if (t.matches("^[A-Z]{3}")) {
                // correction indicator
                t = st.nextToken();
            }

            if (t.matches("^[VRB0-9G]{5,8}KT.*$")) {
                parseWind(t, "KT");
                t = st.nextToken();

            } else if (t.matches("^[VRB0-9G]{5,8}KMH.*$")) {
                parseWind(t, "KMH");
                t = st.nextToken();

            } else if (t.matches("^[VRB0-9G]MPS.*$")) {
                parseWind(t, "MPS");
                t = st.nextToken();

            } else {
                t = st.nextToken(); // ignore it
            }

            if (t.matches("^[\\d]{3}V[\\d]{3}$")) {
                parseVariations(t);
                t = st.nextToken();
            }

            while (true) {
                if (t.matches("^[\\d]{4}$") || t.matches("^[\\d/]{1,4}SM$") || t.matches("^[\\d]{1,4}/[\\d]{1,4}SM$")) {
                    parseVisibility(t);
                }
                if (t.matches("^[\\d]{4}.*$")) {
                    parseDirectionalVisibility(t);
                }
                if (t.matches("^CAVOK$"))
                    parseCavok(t);
                else if (t.matches("^R.*/.*$"))
                    parseRunwayVisibility(t);
                else if (t.matches("^A[\\d]{4}$"))
                    parsePressureInHG(t);
                else if (t.matches("^Q[\\d]{4}$"))
                    parsePressureHPa(t);
                else if (t.matches("[M]{0,1}^[\\d]{2}/[M]{0,1}[\\d]{2}$"))
                    parseTemperatures(t); // todo M1/M4
                else if (t.matches("^RMK$"))
                    break; // means "national information follow
                else
                    parsePhenomena(t);
                if (st.hasMoreElements()) {
                    t = st.nextToken();
                } else {
                    break;
                }
            }
            ceiling = getValueOf("BKN");
            int ceilingOVC = getValueOf("OVC");
            ceiling = ceiling < ceilingOVC ? ceiling : ceilingOVC;


            double angle = (double) (getWindDirectionI() - 270) / 360d * 2 * Math.PI;
            windFromNorth = Math.sin(angle) * (double) getWindSpeed();
            windFromWest = Math.cos(angle) * (double) getWindSpeed();
            // System.out.println("Wind N: "+windFromNorth+" W: "+windFromWest);

            weatherPhaenomena = weatherPhaenomena.replaceAll("A[\\d]{4}", "");
            weatherPhaenomena = weatherPhaenomena.replaceAll("Q[\\d]{4}", "");
            weatherPhaenomena = weatherPhaenomena.replaceAll("[\\d]{2}/[\\d]{2}", "");
            weatherPhaenomena = weatherPhaenomena.replaceAll("CAVOK", "");
            if (weatherPhaenomena.contains("RMK")) {
                weatherPhaenomena = weatherPhaenomena.substring(0, weatherPhaenomena.indexOf("RMK")).trim();
            }
            weatherPhaenomena = weatherPhaenomena.replaceAll("  ", " ");
            weatherPhaenomena = weatherPhaenomena.replaceAll("\n", "");

            weatherPhaenomenaHuman = weatherPhaenomena.replaceAll("SKC", "sky clear ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("FEW", "few clouds ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("SCT", "scattered ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("BKN", "broken ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("OVC", "overcast ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("TCU", "towering cumulus ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("CB", "Cumulonimbus ");

            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("CLR", "clear ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("NSC", "Nil significant clouds ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("CAVOK", "clouds+vis OK");

            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("NOSIG", "(stable)");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("BECMG", "(will change)");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("TEMPO", "(is changing)");

            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("-", "light ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("\\+", "heavy ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("RA", "rain");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("DZ", "drizzle");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("SN", "snow");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("PE", "ice pellets");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("GR", "hail");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("GS", "snow pellets");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("IC", "ice crystals");

            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("MI", "shallow ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("BC", "patches ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("PR", "partial ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("DR", "drifting ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("BL", "blowing ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("SH", "showers ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("TS", "thunder storm ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("FR", "freezing ");

            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("BR", "mist,");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("FG", "fog,");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("SA", "sand,");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("VA", "vulcano ash,");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("FU", "smoke,");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("HZ", "haze,");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("SU", "dust,");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("SS", "sand storm,");

            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("REDZ", "drizzle");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("RERA", "rain,");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("RESN", "snow");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("REASN", "snow");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("REFZRA", "frozen rain,");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("RESH", "showers ");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("REGR", "hail");
            weatherPhaenomenaHuman = weatherPhaenomenaHuman.replaceAll("RETS", "thunderstorm");

            if (ceiling < 500 || visibilitySM < 1) {
                flightConditions = "LIFR";
                flightConditionColor = Palette.METAR_LIFR;
            } else if (ceiling < 1000 || visibilitySM < 3) {
                flightConditions = "IFR";
                flightConditionColor = Palette.METAR_IFR;
            } else if (ceiling < 3000 || visibilitySM < 5) {
                flightConditions = "MVFR";
                flightConditionColor = Palette.METAR_MVFR;
            } else {
                flightConditions = "VFR";
                flightConditionColor = Palette.METAR_VFR;
            }

        } catch (Exception e) {
            log.error("Failed to parse Metar: " + metar, e);
            weatherPhaenomena = "";
        }

    }

    public MetarData() {
        exists = false;
    }

    public boolean isNew() {
        return System.currentTimeMillis() - created < 60 * 1000;
    }

    private void parseWind(String t, String unit) {
        windUnit = unit;
        int wd = -1;
        if (t.startsWith("VRB")) {
            // variable
            windDirection = "VRB";
            windDirectionI = 0;
            windDirectionVariates = true;
        } else {
            wd = (Integer.parseInt(t.substring(0, 3)) - (int) data.getMagneticDeclination());
            wd = wd <= 0 ? wd + 360 : wd;
            windDirection = String.format("%03d", Math.round((float) wd / 10) * 10);
            windDirectionI = wd;
            windDirectionVariates = false;
        }
        windSpeed = Integer.parseInt(t.substring(3, 5));
        if (t.charAt(5) == 'G') {
            // gusts
            windSpeedGusts = Integer.parseInt(t.substring(6, t.indexOf(unit)));
        }
    }

    private void parseVariations(String t) {
        int wdMin = Integer.parseInt(t.substring(0, 3)) - (int) data.getMagneticDeclination();
        wdMin = wdMin <= 0 ? wdMin + 360 : wdMin;
        windDirectionMin = String.format("%03d", Math.round((float) wdMin / 10) * 10);
        int wdMax = Integer.parseInt(t.substring(4)) - (int) data.getMagneticDeclination();
        wdMax = wdMax <= 0 ? wdMax + 360 : wdMax;
        windDirectionMax = String.format("%03d", Math.round((float) wdMax / 10) * 10);
    }

    private void parseVisibility(String t) {
        if (t.matches("^[\\d]{4}$")) {
            visibility = Integer.parseInt(t);
            visibilitySM = visibility / Units.SM;
            visibilityUnit = "m";
        } else if (t.matches("^[\\d]{1,4}SM$")) {
            visibility = Integer.parseInt(t.substring(0, t.indexOf("S")));
            visibilitySM = visibility;
            visibilityUnit = "SM";
        } else if (t.matches("^[\\d]{1,4}/[\\d]{1,4}SM$")) {
            visibility = Integer.parseInt(t.substring(0, t.indexOf("/")));
            visibility = visibility / Integer.parseInt(t.substring(t.indexOf("/") + 1, t.indexOf("SM")));
            visibilitySM = visibility;
            visibilityUnit = "SM";
        } else {
            log.warn("Unparsed visibility: " + t);
        }

    }

    private void parseDirectionalVisibility(String t) {
        // TODO Auto-generated method stub

    }

    private void parseCavok(String t) {
        cavok = true;
        visibility = 10000;
        visibilityUnit = "m";
        visibilitySM = visibility / Units.SM;
    }

    private void parseRunwayVisibility(String t) {
        // TODO Auto-generated method stub

    }

    private void parsePressureInHG(String t) {
        pressureInHG = Float.parseFloat(t.substring(1)) / 100;
        pressureHPa = 33.86389f * pressureInHG;
    }

    private void parsePressureHPa(String t) {
        pressureHPa = Float.parseFloat(t.substring(1));
        pressureInHG = pressureHPa / 33.86389f;
    }

    private void parseTemperatures(String t) {
        int sep = t.indexOf("/");
        String sTemperature = t.substring(0, sep);
        if (sTemperature.startsWith("M")) {
            temperature = -1 * Integer.parseInt(sTemperature.substring(1));
        } else {
            temperature = Integer.parseInt(sTemperature);
        }

        String sDewPoint = t.substring(sep + 1);
        if (sDewPoint.startsWith("M")) {
            dewPoint = -1 * Integer.parseInt(sDewPoint.substring(1));
        } else {
            dewPoint = Integer.parseInt(sDewPoint);
        }
    }

    private void parsePhenomena(String t) {
        weatherPhaenomena = weatherPhaenomena.length()==0 ? t : weatherPhaenomena+" "+t;
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

    public String getWindDirection() {
        return windDirection;
    }

    public String getWindDirectionRounded() {
        int wd = windDirectionI;
        return String.format("%03d", Math.round((float) wd / 10) * 10);
    }

    public int getWindDirectionI() {
        return windDirectionI;
    }

    public String getWindDirectionMin() {
        return windDirectionMin;
    }

    public String getWindDirectionMax() {
        return windDirectionMax;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public int getWindSpeedGusts() {
        return windSpeedGusts;
    }

    public String getWindUnit() {
        return windUnit;
    }

    public String getVisibility() {
        if (isCavok() || 9999 == visibility) {
            visibilityUnit = "km";
            return ">10";
        }
        if ("NM".equalsIgnoreCase(getVisibilityUnit()) || "SM".equalsIgnoreCase(getVisibilityUnit())) {
            return String.format("%2.1f", visibility);
        } else {
            // meter
            return String.format("%2.0f", visibility);
        }
    }

    public String getVisibilityUnit() {
        if (isCavok()) {
            return "km";
        }
        return visibilityUnit == null ? "SM" : visibilityUnit;
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

    public synchronized String getWeatherPhaenomena() {
        return weatherPhaenomena;
    }

    public synchronized String getWeatherPhaenomenaForHumans() {
        return weatherPhaenomenaHuman;
    }

    public String getWindDisplayString() {
        StringBuffer sb = new StringBuffer();
        if (isWindDirectionVariates()) {
            sb.append("VRB");
        } else {
            sb.append(getWindDirectionRounded());
        }
        if (getWindDirectionMin() != null) {
            sb.append("(");
            sb.append(getWindDirectionMin());
            sb.append("-");
            sb.append(getWindDirectionMax());
            sb.append(")");
        }
        sb.append("@");
        sb.append(getWindSpeed());
        sb.append(getWindUnit());
        if (getWindSpeedGusts() > -1) {
            sb.append("(");
            sb.append(getWindSpeedGusts());
            sb.append(getWindUnit());
            sb.append(")");
        }
        return sb.toString();
    }

    public Object getPressureDisplayString() {
        return String.format("%2.2f/%4.0f", pressureInHG, pressureHPa);
    }

    public List<String> createATIS(GuiMasterController master, boolean shortMsg) {
        // boolean includeFgCom = data.getFgComMode()!=FgComMode.Off;

        if (atisVariables == null) {
            atisVariables = new ArrayList<String>();
            atisVariables.add("/sim/tower/airport-id");
            atisVariables.add("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-APalt");
            atisVariables.add("/openradar/metar/pressure-sea-level");
            atisVariables.add("/openradar/metar/wind");
            atisVariables.add("/openradar/transitionAlt");
            // atisVariables.add("/openradar/metar/visibility");
            atisVariables.add("/sim/atc/activeRW");
            atisVariables.add("/openradar/comm/frequencies"); // this includes also the alternative radio texts
        }

        ArrayList<String> list = new ArrayList<String>();
        // if(includeFgCom) {
        list.add(String.format(
                AtcMenuChatMessage.replaceVariables("ATIS for %s at %2.0fft: QNH %s wind %s transAlt %s act. rwy(s) %s; %s", master, null, atisVariables))
                .trim());
        // } else {
        // list.add(String.format(AtcMenuChatMessage.replaceVariables("ATIS for %s at %2.0fft: QNH %s wind %s transAlt %s act. rwy(s) %s; %s",
        // master, null, atisVariables)).trim());
        // }
        if (!shortMsg)
            list.add("(ATIS) vis:" + getVisibility() + "" + getVisibilityUnit() + " " + getWeatherPhaenomenaForHumans());
        return list;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MetarData))
            return false;
        return metarBaseData.equals(((MetarData) obj).metarBaseData);
    }

    public static MetarData createNotFoundMetar(String code) {
        MetarData metar = new MetarData();
        metar.airportCode = code;
        metar.metarBaseData = "Metar for " + code + " not found, define alternative Metar via the dialog please!";
        metar.weatherPhaenomena = "Metar for " + code + " not found. (RightClick!)";

        return metar;
    }

    public boolean exists() {
        return exists;
    }

    public String getFlightConditions() {
        return flightConditions;
    }

    public Color getFlightConditionColor() {
        return flightConditionColor;
    }
    
    private int getValueOf(String code) {
        int start = metarBaseData.indexOf(code);
        if (start == -1)
            return Integer.MAX_VALUE;

        int value = Integer.parseInt(metarBaseData.substring(start+3, start+6)) * 100;
        return value;
    }

}
