/**
 * Copyright (C) 2013,2015,2018 Wolfram Wagner
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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.rpvd.contact;

import java.awt.Font;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.rpvd.contact.ContactShape.Symbol;
/**
 * This data block layout is even more strict.
 * In no transmitter mode it displays no text at all.
 *
 * @author Wolfram Wagner
 */
public class PureSimulationLayout extends ADatablockLayout {

    private final DatablockLayoutManager manager;

    public PureSimulationLayout(DatablockLayoutManager manager) {
        this.manager = manager;
        font = new Font("Courier", Font.PLAIN, 11);
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public String getMenuText() {
        return "Pure Simulation (Transponder enforced)";
    }

    // not overwritten 	

//  @Override
//  public synchronized Color getBackgroundColor(GuiRadarContact contact, boolean highlighted) {
//  }
//  
//  @Override
//  public Color getDataBlockColor(GuiRadarContact c) {
//  	
//  }
//
//  @Override
//  public Color getDataBlockColor(GuiRadarContact c) {
//  	
//  }
    

    @Override
    public String getDataBlockText(GuiMasterController master, GuiRadarContact c) {
        int currentAltSpeedIndex = 1;

        if(c.isAtc()) {
            setAltSpeedIndex(-1);
            return String.format("%s\n%s",c.getCallSign(),c.getAircraftCode());
        }


        boolean transmitterAvailable = c.getTranspSquawkCode() !=null;
        if(transmitterAvailable) {
            // there is a transmitter
            if(c.getTranspSquawkCode()==null || c.getTranspSquawkCode()==-9999) {
                return "";
            }
            
            boolean assignedSquawkTunedIn = c.getTranspSquawkCode()!=null && c.getAssignedSquawk()!=null && c.getTranspSquawkCode().equals(c.getAssignedSquawk());

            StringBuilder sb = new StringBuilder();

            if(7500==c.getTranspSquawkCode()) {
                sb.append("HJ").append("\n");
                currentAltSpeedIndex++;
            } else if(7600==c.getTranspSquawkCode()) {
                sb.append("RF").append("\n");
                currentAltSpeedIndex++;
            } else if(7700==c.getTranspSquawkCode()) {
                sb.append("EM").append("\n");
                currentAltSpeedIndex++;
            }

            if(!assignedSquawkTunedIn) {
                // squawk codes do not match
                Integer squawk = c.getTranspSquawkCode();
                sb.append(String.format("%s %2s",squawk == -9999 ? "----" : squawk,c.getMagnCourse())).append("\n");
                sb.append(c.getAltitudeString(master)).append(getAccuracySeparator(c));;
                sb.append(String.format("G%03.0f",c.getGroundSpeedD()));
            } else {
                // squawk codes match
                sb.append(String.format("%s %2s",c.getCallSign(),c.getMagnCourse())).append("\n");
                sb.append(c.getAltitudeString(master)).append(getAccuracySeparator(c));;
                sb.append(String.format("G%03.0f",c.getGroundSpeedD())).append("\n");
                sb.append(c.getAircraftCode());
            }
            setAltSpeedIndex(currentAltSpeedIndex);
            return sb.toString();
        } else {
            setAltSpeedIndex(-1);
            return "";
        }
    }

    private String getAccuracySeparator(GuiRadarContact c) {
        if(-9999!=c.getTranspAltitude()) {
            if(displayVSpeedArrow(c)) {
                return "  ";
            } else {
                return " ";
            }
        } else {
            if(displayVSpeedArrow(c)) {
                return "*  ";
            } else {
                return "* ";
            }
        }
    }

    @Override
    public void modify(ContactShape shape, GuiRadarContact c) {

        int vfrCode = manager.getData().getSquawkCodeManager().getVfrCode();
//        int ifrCode = manager.getData().getSquawkCodeManager().getIfrCode();
        
        if(c.getTranspSquawkCode()!=null && ( vfrCode==c.getTranspSquawkCode())) {
            // Squawking VFR
            shape.modify(Symbol.EmptySquare, c, 6);
        } else  if(c.getTranspSquawkCode()==null) {
            // no squawk or standby
            shape.modify(Symbol.FilledDiamond, c, 8);
        } else if(c.getTranspSquawkCode()!=null && c.getAtcLetter()==null) {
            // untracked
            shape.modify(Symbol.Asterix, c, 6);
        } else if(c.getTranspSquawkCode()!=null && c.getAtcLetter()!=null) {
            // controlled
            shape.modify(Symbol.Letter, c, 8);
        } else {
            shape.modify(Symbol.FilledDot, c, 6);
        }
    }
    
    @Override
    public boolean displayVSpeedArrow(GuiRadarContact c) {
        return Math.abs(c.getVerticalSpeedD())>100;
    }
}
