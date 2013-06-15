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
package de.knewcleus.openradar.gui.status.runways;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultButtonModel;
import javax.swing.JCheckBox;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.impl.Glideslope;
import de.knewcleus.fgfs.navdata.impl.RunwayEnd;
import de.knewcleus.fgfs.navdata.model.IRunway;
import de.knewcleus.fgfs.navdata.model.IRunwayEnd;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.gui.setup.RunwayData;
import de.knewcleus.openradar.weather.MetarData;

/**
 * This class provides the runway data for the frontend.
 *
 * @author Wolfram Wagner
 *
 */
public class GuiRunway implements ActionListener {

    public enum Usabilty {CLOSED, HEAVY_ONLY, WARNING, OPEN}

    private AirportData data;
    private RunwayData rwData;
    private volatile MetarData metar = null;
    private IRunwayEnd runwayEnd = null;
    private RunwayPanel runwayPanel = null;

    private boolean startRouteEnabled = false;
    private boolean landingRouteEnabled = false;

    public GuiRunway(AirportData data, RunwayEnd runwayEnd) {
        this.data = data;
        this.runwayEnd = runwayEnd;
        this.rwData = new RunwayData(runwayEnd.getRunwayID());
    }

    public synchronized void setRunwayPanel(RunwayPanel runwayPanel) {
        this.runwayPanel = runwayPanel;
    }

    public synchronized void setMetar(MetarData metar) {
        this.metar=metar;
    }

    public String getCode() {
        return runwayEnd.getRunwayID();
    }

   public String getTrueHeading() {
       return String.format("%01.2",runwayEnd.getTrueHeading());
   }

   public String getMagneticHeading() {
       return String.format("%03.0f",runwayEnd.getTrueHeading()-data.getMagneticDeclination());
   }

   public String getIlsFrequency() {
        if(hasIls()) {
            return String.format("%3.2f",runwayEnd.getGlideslope().getFrequency().getValue()/Units.MHz);
        } else {
            return "";
        }
    }

    public boolean hasIls() {
        return runwayEnd.getGlideslope()!=null;
    }

    public String getWindDirection() {
        return metar.getWindDirection();
    }

    /**
     * Returns the number in degrees how much the wind differs from optimal
     * direction (directly from front)
     *
     * So 0 is optimal, 90/-90 a shear wind and 180/-180 the wind from behind
     */
    public double getWindDeviation() {
        double runwayHeading = runwayEnd.getTrueHeading() - data.getMagneticDeclination();
        double windDir = metar.getWindDirectionI();

        double normalizedWindDir = windDir-runwayHeading; // outer angle
        normalizedWindDir = normalizedWindDir<-180 ? normalizedWindDir+360 : normalizedWindDir;
        normalizedWindDir = normalizedWindDir>180 ? normalizedWindDir-360 : normalizedWindDir;

        return normalizedWindDir;
    }

    /**
     * Returns the effective wind strength in shear direction (90 degrees).
     *
     * @return the strength of the shear component of the wind in knots.
     */
    public double getCrossWindSpeed() {
        double angle = getWindDeviation()/360*2*Math.PI;//
        return Math.abs(Math.sin(angle)*getWindSpeed());
    }

    /**
     * Returns the effective wind strength blowing from magnetic runway heading.
     *
     * @return the strength of the shear component of the wind in knots.
     */
    public double getHeadWindSpeed() {
        double angle = getWindDeviation()/360*2*Math.PI;//
        return Math.cos(angle)*getWindSpeed();
    }

    /**
     * Returns the effective wind strength in cross direction (90 degrees).
     *
     * @return the strength of the shear component of the wind in knots.
     */
    public double getCrossWindGusts() {
        if(metar.getWindSpeedGusts()==-1) return -1;
        double angle = getWindDeviation()/360*2*Math.PI;//
        return Math.abs(Math.sin(angle)*metar.getWindSpeedGusts());
    }

    /**
     * Returns the effective wind strength in cross direction (90 degrees).
     *
     * @return the strength of the shear component of the wind in knots.
     */
    public double getHeadWindGusts() {
        if(metar.getWindSpeedGusts()==-1) return -1;
        double angle = getWindDeviation()/360*2*Math.PI;//
        return Math.cos(angle)*metar.getWindSpeedGusts();
    }

    public int getWindSpeed() {
        return metar.getWindSpeed();
    }

    public float getHeight() {
        return runwayEnd.getRunway().getAerodrome().getElevation();
    }

    public float getWidthM() {
        return runwayEnd.getRunway().getWidth();
    }

    public float getWidthFt() {
        return runwayEnd.getRunway().getWidth()/Units.FT;
    }

    public float getLengthM() {
        return runwayEnd.getRunway().getLength();
    }

    public float getLengthFt() {
        float length = runwayEnd.getRunway().getLength()/Units.FT;
        length = Math.round(length/100)*100;
        return length;
    }

    public Usabilty getUsability() {
        // land with wind
        if(Math.abs(getWindDeviation())>90 && getWindSpeed()>3) return Usabilty.CLOSED;
        // Strong sidewinds
        if(Math.abs(getWindDeviation())<=10 && getWindSpeed()>20) return Usabilty.CLOSED;
        if(Math.abs(getWindDeviation())<=90 && getWindSpeed()>10) return Usabilty.HEAVY_ONLY;
        if(Math.abs(getWindDeviation())>45 && getWindSpeed()>5) return Usabilty.HEAVY_ONLY;
        // minor deviation, mid winds
        if(Math.abs(getWindDeviation())<=45 && getWindSpeed()>5 && getWindSpeed()<=10) return Usabilty.WARNING;

        return Usabilty.OPEN;
    }

    public String getUseability() {
        switch(getUsability()) {
        case CLOSED:
            return "Closed";
        case HEAVY_ONLY:
            return "Heavy only";
        case OPEN:
            return "Open";
        case WARNING:
            return "Warning!";
        default:
            return "";
        }
    }

    public String getFormatedDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("Runway: ").append(getCode()).append(" (").append(getTrueHeading()).append("!)\n");
        if(hasIls()) {
            sb.append("ILS: ").append(getIlsFrequency()).append(" / ").append(getTrueHeading()).append("°\n");
        }
        sb.append("Wind: ").append(getWindSpeed()).append("@").append(getWindDirection()).append(" Dev:").append(getWindDeviation()).append("°\n");
        sb.append("Status: ").append(getUseability());
        return sb.toString();
    }

    public IRunwayEnd getRunwayEnd() {
        return runwayEnd;
    }

    public void setRunwayEnd(IRunwayEnd runwayEnd) {
        this.runwayEnd = runwayEnd;
    }

    public boolean isStartingActive() {
        return runwayEnd.getRunway().getStartSide()==runwayEnd.getOppositeEnd();
    }

    public boolean isLandingActive() {
        return runwayEnd.getRunway().getLandSide()==runwayEnd;
    }

    public synchronized boolean isStartRouteEnabled() {
        return startRouteEnabled;
    }

    public synchronized void setStartRouteEnabled(boolean startRouteEnabled) {
        this.startRouteEnabled = startRouteEnabled;
    }

    public synchronized boolean isLandingRouteEnabled() {
        return landingRouteEnabled;
    }

    public synchronized void setLandingRouteEnabled(boolean landingRouteEnabled) {
        this.landingRouteEnabled = landingRouteEnabled;
    }

    public void addILS(Glideslope gs) {
        runwayEnd.setGlideslope(gs);
    }

    public Glideslope getGlideslope() {
        return runwayEnd.getGlideslope();
    }

    // ActionListener

    @Override
    public void actionPerformed(ActionEvent e) {
        JCheckBox cb = ((JCheckBox)e.getSource());
        String name = cb.getName();

        if("STARTING".equals(name) && e.getID()==ActionEvent.ACTION_PERFORMED   ) {
            setStartingActive(!isStartingActive());

        } else if("STARTROUTE".equals(name) && e.getID()==ActionEvent.ACTION_PERFORMED   ) {
            setStartRouteEnabled(!isStartRouteEnabled());

        } else if("LANDING".equals(name) && e.getID()==ActionEvent.ACTION_PERFORMED ) {
            setLandingActive(!isLandingActive());

        } else if("LANDINGROUTE".equals(name) && e.getID()==ActionEvent.ACTION_PERFORMED ) {
            setLandingRouteEnabled(!isLandingRouteEnabled());
        }
        runwayPanel.updateRunways();
    }

    public void setStartingActive(boolean startingActive) {
        IRunway rw = (IRunway) runwayEnd.getRunway();
        if(startingActive) {
            rw.setStartSide(runwayEnd.getOppositeEnd());
            if(rw.getLandSide()==runwayEnd.getOppositeEnd()) {
                // this means a direction change
                if(!rwData.isBiDirectional()) { // some airports land and start on the same side
                    // disable the other side
                    rw.setLandSide(null);
                }
            }
        } else {
            rw.setStartSide(null);
        }
        runwayPanel.updateRunways();
    }

    public void setLandingActive(boolean landingActive) {
        IRunway rw = (IRunway) runwayEnd.getRunway();
        if(landingActive) {
            rw.setLandSide(runwayEnd);
            if(rw.getStartSide()==runwayEnd) {
                // this means a direction change
                if(!rwData.isBiDirectional()) { // some airports land and start on the same side
                 // disable the other side
                    rw.setStartSide(null);
                }
            }
        } else {
            rw.setLandSide(null);
        }
        runwayPanel.updateRunways();
    }

    public CbModel createStartCbModel() {
        return new CbModel(this, false);
    }

    public CbModel createLandingCbModel() {
        return new CbModel(this, true);
    }

    private class CbModel extends DefaultButtonModel {

        private static final long serialVersionUID = 1L;
        private GuiRunway rw = null;
        private boolean landingMode = false;

        public CbModel(GuiRunway rw, boolean landingMode) {
            this.rw = rw;
            this.landingMode=landingMode;
        }


        @Override
        public boolean isSelected() {
            boolean r = landingMode ? rw.isLandingActive() : rw.isStartingActive();
            return r;
        }
   }

    public RunwayData getRunwayData() {
        return rwData;
    }

}