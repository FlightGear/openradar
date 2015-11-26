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
package de.knewcleus.openradar.gui.contacts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;

/**
 * This class renders the flight strip like radar contacts
 *
 * @author Wolfram Wagner
 */

public class FlightStripCellRendererTrad extends JComponent implements ListCellRenderer<GuiRadarContact> {
    private static final long serialVersionUID = 4683696532302543565L;

//    private GuiMasterController master;

    private JPanel spaceLEFT = null;
    private JPanel strip = null;
    private FgComSupportSymbol fgComSupportSymbol = new FgComSupportSymbol();
    private JLabel lbCallSign = null;
    private JLabel lbRadarDistance = null;
    private JLabel lbSquawkCode = null;
    private JLabel lbAircraft = null;
    private JLabel lbFlightLevel= null;
    private JLabel lbGroundSpeed = null;
    private JLabel lbControllingATC = null;
//    private JLabel lbVerticalSpeed = null;
    private JTextArea taAtcComment = null;
    private JPanel lowerArea = null;

    private GridBagConstraints fgComSupportConstraints;
    private GridBagConstraints stripConstraints;
    private GridBagConstraints spaceRightConstraints;
    private GridBagConstraints lbCallSignConstraints;
    private GridBagConstraints lbRadarDistanceConstraints;
    private GridBagConstraints lbHeadingConstraints;
    private GridBagConstraints lbAircraftConstraints;
    private GridBagConstraints lbFlightLevelConstraints;
    private GridBagConstraints lbControllingATCConstraints;
//    private GridBagConstraints lbGroundSpeedConstraints;
//    private GridBagConstraints lbVerticalSpeedConstraints;
    private GridBagConstraints taAtcCommentConstraints;

//    private Font defaultFont = new java.awt.Font("Cantarell", Font.PLAIN, 12); // NOI18N
//    private Font smallFont = new java.awt.Font("Cantarell", Font.PLAIN, 9); // NOI18N
//    private Font boldFont = new java.awt.Font("Cantarell", Font.BOLD, 12); // NOI18N

    private Font defaultFont;
    private Font smallFont;
    private Font boldFont;

    private Color defaultColor = Color.BLACK;
    private Color incativeColor = Color.GRAY;
    private Color selectionColor = Color.BLUE;
    private Color emergencyColor = new Color(255,150,100);
    private Color emergencyColorSelected = new Color(255,200,150);
    private Color newContactColor = new Color(0,200,0);

    public static int STRIP_WITDH = 250;


    public FlightStripCellRendererTrad(GuiMasterController master) {
        //this.master=master;
        this.setLayout(new GridBagLayout());

        spaceLEFT = new JPanel();
        spaceLEFT.setOpaque(false);
        spaceRightConstraints = new GridBagConstraints();
        spaceRightConstraints.gridx = 0;
        spaceRightConstraints.gridy = 0;
        spaceRightConstraints.gridheight = 1;
        spaceRightConstraints.fill=GridBagConstraints.HORIZONTAL;
        spaceRightConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(spaceLEFT, spaceRightConstraints);

        strip = new JPanel();
        strip.setLayout(new GridBagLayout());
        stripConstraints = new GridBagConstraints();
        stripConstraints.gridx = 1;
        stripConstraints.gridy = 0;
        stripConstraints.gridheight = 1;
        stripConstraints.weightx=1;
        stripConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        stripConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(strip, stripConstraints);

        // first line

        lbCallSign = new JLabel();
        defaultFont = lbCallSign.getFont().deriveFont(10f);
        smallFont = lbCallSign.getFont().deriveFont(9f);
        boldFont = lbCallSign.getFont().deriveFont(10f).deriveFont(Font.BOLD);

        fgComSupportConstraints = new GridBagConstraints();
        fgComSupportConstraints.gridx = 0;
        fgComSupportConstraints.gridy = 0;
        fgComSupportConstraints.gridwidth = 1;
        fgComSupportConstraints.weightx=0;
        fgComSupportConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        fgComSupportConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        strip.add(fgComSupportSymbol,fgComSupportConstraints);

        lbCallSign.setFont(boldFont);
        lbCallSign.setForeground(java.awt.Color.blue);
        lbCallSign.setToolTipText("Current call sign");
        lbCallSign.setMinimumSize(new Dimension(60,defaultFont.getSize()+2));
        lbCallSign.setPreferredSize(new Dimension(60,defaultFont.getSize()+2));
        lbCallSign.setOpaque(true);
        lbCallSignConstraints = new GridBagConstraints();
        lbCallSignConstraints.gridx = 1;
        lbCallSignConstraints.gridy = 0;
        lbCallSignConstraints.gridwidth = 1;
        lbCallSignConstraints.weightx=1;
        lbCallSignConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        lbCallSignConstraints.insets = new java.awt.Insets(4, 2, 0, 5);
        strip.add(lbCallSign, lbCallSignConstraints);

        lbAircraft = new JLabel();
        lbAircraft.setFont(defaultFont);
        lbAircraft.setForeground(java.awt.Color.blue);
        lbAircraft.setOpaque(true);
        lbAircraft.setMinimumSize(new Dimension(80,defaultFont.getSize()+2));
        lbAircraft.setPreferredSize(new Dimension(80,defaultFont.getSize()+2));
        lbAircraftConstraints = new GridBagConstraints();
        lbAircraftConstraints.gridx = 2;
        lbAircraftConstraints.gridy = 0;
        lbAircraftConstraints.weightx=1;
        lbAircraftConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        lbAircraftConstraints.insets = new java.awt.Insets(4, 4, 0, 5);
        strip.add(lbAircraft, lbAircraftConstraints);

        lbSquawkCode = new JLabel(" ");
        lbSquawkCode.setFont(boldFont);
        lbSquawkCode.setForeground(java.awt.Color.blue);
        lbSquawkCode.setOpaque(true);
        lbHeadingConstraints = new GridBagConstraints();
        lbHeadingConstraints.gridx = 3;
        lbHeadingConstraints.gridy = 0;
//        lbHeadingConstraints.weightx = 0;
//        lbHeadingConstraints.fill = GridBagConstraints.HORIZONTAL;
        lbHeadingConstraints.anchor = java.awt.GridBagConstraints.EAST;
        lbHeadingConstraints.insets = new java.awt.Insets(4, 5, 0, 5);
        strip.add(lbSquawkCode, lbHeadingConstraints);


        // second line

        lowerArea = new JPanel();
        lowerArea.setLayout(new GridBagLayout());
        lowerArea.setBackground(Color.white);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth=4;
        constraints.gridheight=1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill=GridBagConstraints.BOTH;
        constraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        constraints.insets = new java.awt.Insets(0, 0, 0, 0);
        strip.add(lowerArea, constraints);

        lbRadarDistance = new JLabel();
        lbRadarDistance.setFont(boldFont);
        lbRadarDistance.setForeground(java.awt.Color.blue);
        lbRadarDistance.setMinimumSize(new Dimension(50,defaultFont.getSize()+2));
        lbRadarDistance.setPreferredSize(new Dimension(50,defaultFont.getSize()+2));
        lbRadarDistance.setOpaque(true);
        lbRadarDistanceConstraints = new GridBagConstraints();
        lbRadarDistanceConstraints.gridx = 0;
        lbRadarDistanceConstraints.gridy = 0;
        lbRadarDistanceConstraints.anchor = java.awt.GridBagConstraints.EAST;
        lbRadarDistanceConstraints.insets = new java.awt.Insets(0, 4, 4, 0);
        lowerArea.add(lbRadarDistance, lbRadarDistanceConstraints);

        lbFlightLevel = new JLabel();
        lbFlightLevel.setFont(boldFont);
        lbFlightLevel.setForeground(java.awt.Color.blue);
//        lbFlightLevel.setPreferredSize(new Dimension(45,defaultFont.getSize()+2));
        lbFlightLevel.setOpaque(true);
        lbFlightLevelConstraints = new GridBagConstraints();
        lbFlightLevelConstraints.gridx = 1;
        lbFlightLevelConstraints.gridy = 0;
        lbFlightLevelConstraints.anchor = java.awt.GridBagConstraints.WEST;
        lbFlightLevelConstraints.insets = new java.awt.Insets(0, 8, 4, 0);
        lowerArea.add(lbFlightLevel, lbFlightLevelConstraints);


        lbGroundSpeed = new JLabel();
        lbGroundSpeed.setFont(boldFont);
        lbGroundSpeed.setForeground(java.awt.Color.blue);
        lbControllingATCConstraints = new GridBagConstraints();
        lbControllingATCConstraints.gridx = 2;
        lbControllingATCConstraints.gridy = 0;
        lbControllingATCConstraints.anchor = java.awt.GridBagConstraints.WEST;
        lbControllingATCConstraints.insets = new java.awt.Insets(0, 20, 4, 0);
        lowerArea.add(lbGroundSpeed, lbControllingATCConstraints);

        lbControllingATC = new JLabel();
        lbControllingATC.setFont(boldFont);
        lbControllingATC.setForeground(java.awt.Color.blue);
        lbControllingATC.setOpaque(true);
        lbControllingATCConstraints = new GridBagConstraints();
        lbControllingATCConstraints.gridx = 3;
        lbControllingATCConstraints.gridy = 0;
        lbControllingATCConstraints.weightx = 1;
        lbControllingATCConstraints.anchor = java.awt.GridBagConstraints.EAST;
        lbControllingATCConstraints.insets = new java.awt.Insets(0, 8, 4, 5);
        lowerArea.add(lbControllingATC, lbControllingATCConstraints);


        // third line

        taAtcComment = new JTextArea();
        taAtcComment.setFont(smallFont);
        taAtcComment.setForeground(java.awt.Color.blue);
        taAtcComment.setOpaque(true);
        taAtcComment.setEditable(false);
        taAtcCommentConstraints = new GridBagConstraints();
        taAtcCommentConstraints.gridx = 0;
        taAtcCommentConstraints.gridy = 1;
        taAtcCommentConstraints.gridwidth=5;
        taAtcCommentConstraints.gridheight=1;
        taAtcCommentConstraints.weightx = 1;
        taAtcCommentConstraints.weighty = 1;
        taAtcCommentConstraints.fill=GridBagConstraints.BOTH;
        taAtcCommentConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        taAtcCommentConstraints.insets = new java.awt.Insets(0, 4, 6, 4);
        lowerArea.add(taAtcComment, taAtcCommentConstraints);
//
//        lbVerticalSpeed = new JLabel();
//        lbVerticalSpeed.setFont(defaultFont);
//        lbVerticalSpeed.setForeground(java.awt.Color.blue);
//        lbVerticalSpeed.setOpaque(true);
//        lbVerticalSpeedConstraints = new GridBagConstraints();
//        lbVerticalSpeedConstraints.gridx = 2;
//        lbVerticalSpeedConstraints.gridy = 1;
//        lbVerticalSpeedConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        lbVerticalSpeedConstraints.insets = new java.awt.Insets(0, 0, 6, 5);
//        lowerArea.add(lbVerticalSpeed, lbVerticalSpeedConstraints);

        doLayout();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends GuiRadarContact> list, GuiRadarContact value, int index, boolean isSelected, boolean cellHasFocus) {
        int totalWidth = list.getParent().getWidth();
        int moveIncrement = (totalWidth-STRIP_WITDH)/2-4;

        // alinment
        switch(value.getAlignment()) {
        case LEFT:
            spaceLEFT.setPreferredSize(new Dimension(0,0));
            break;
        case CENTER:
            spaceLEFT.setPreferredSize(new Dimension(moveIncrement*1,0));
            break;
        case RIGHT:
            spaceLEFT.setPreferredSize(new Dimension(moveIncrement*2,0));
            break;
        }

        if(!value.isAtc()) {
            fgComSupportSymbol.setActive(value.hasFgComSupport());
            lbCallSign.setText(value.getCallSign());
            lbFlightLevel.setText(value.getFlightLevel());
            lbRadarDistance.setText(value.getRadarContactDistance()+" NM");
            // lbRadarBearing.setText("@"+value.getRadarContactDirection()+"°");
            lbSquawkCode.setText(getSquawkDisplay(value)/*+ " "+value.getFrequency()*/);
            if(value.isNeglect()) {
                lbAircraft.setText("neglected");
            } else if(!value.isActive()) {
                long seconds = (System.currentTimeMillis()-value.getLastUpdate())/1000;
                lbAircraft.setText("inact.: "+seconds+" sec");
            } else {
                lbAircraft.setText(value.getModel());
            }
            taAtcComment.setText(composeComment(value));
            lbGroundSpeed.setText(value.getGroundSpeed());
            String handover = value.getFlightPlan().getHandover();
            if(handover!=null && !handover.isEmpty()) {
                lbControllingATC.setText(value.getFlightPlan().getOwner()+"=>"+handover);
            } else {
                lbControllingATC.setText(value.getFlightPlan().getOwner());
            }
            //lbVerticalSpeed.setText("V:"+value.getVerticalSpeed());
        } else {
            // ATC
            fgComSupportSymbol.setActive(value.hasFgComSupport());
            lbCallSign.setText(value.getCallSign());
            lbFlightLevel.setText("");
            lbRadarDistance.setText("");
            // lbRadarBearing.setText("@"+value.getRadarContactDirection()+"°");
            lbSquawkCode.setText(value.getFrequency());
            if(value.isNeglect()) {
                lbAircraft.setText("neglected");
            } else if(!value.isActive()) {
                long seconds = (System.currentTimeMillis()-value.getLastUpdate())/1000;
                lbAircraft.setText("inact.: "+seconds+" sec");
            } else {
                lbAircraft.setText(value.getAircraftCode());
            }

            taAtcComment.setText(value.getFlightPlan().getRemarks());
            lbGroundSpeed.setText("");
            lbControllingATC.setText("");
            //lbVerticalSpeed.setText("");
        }


        Color background;
        Color foreground;

        // check if this cell represents the current DnD drop location
        JList.DropLocation dropLocation = list.getDropLocation();
        if (dropLocation != null && !dropLocation.isInsert() && dropLocation.getIndex() == index) {

            background = Color.WHITE;
            foreground = Color.WHITE;


        } else {
            // normal display

            foreground = defaultColor;
            background = Color.white;
            newContactColor = new Color(0,110,0);

            FlightPlanData fpd = value.getFlightPlan();
            if (value.isOnEmergency()) {
                if (value.isSelected()) {
                    background = emergencyColorSelected;
                } else {
                    background = emergencyColor;
                }

            } else if (value.isSelected()) {
                foreground = selectionColor;

            } else if (fpd!=null && fpd.isOfferedToMe()) {
                foreground = Color.black;
                background = new Color(255,255,203);

            } else if (value.isNew()) {
                foreground = newContactColor;
                foreground = Color.black;
                background = new Color(215,255,215);

            } else if(!value.isActive() || value.isNeglect()) {
                foreground=incativeColor;
            }
        }

        // this.lbCallSign.setFont(activeFont);
        this.fgComSupportSymbol.setForeground(foreground);
        this.lbCallSign.setForeground(foreground);
        this.lbCallSign.setBackground(background);
        // this.lbFlightLevel.setFont(activeFont);
        this.lbFlightLevel.setForeground(foreground);
        this.lbFlightLevel.setBackground(background);
        // this.lbRadarDistance.setFont(font);
        this.lbRadarDistance.setForeground(foreground);
        this.lbRadarDistance.setBackground(background);

        // this.lbHeading.setFont(activeFont);
        this.lbSquawkCode.setForeground(foreground);
        this.lbSquawkCode.setBackground(background);
        // this.lbAircraft.setFont(activeFont);
        this.lbAircraft.setForeground(foreground);
        this.lbAircraft.setBackground(background);
        this.taAtcComment.setForeground(foreground);
        this.taAtcComment.setBackground(background);

        this.lbGroundSpeed.setForeground(foreground);
        this.lbGroundSpeed.setBackground(background);
        // this.lbGroundSpeed.setFont(activeFont);
        this.lbControllingATC.setForeground(foreground);
        this.lbControllingATC.setBackground(background);

//        this.lbVerticalSpeed.setForeground(foreground);
//        this.lbVerticalSpeed.setBackground(background);

        this.lowerArea.setBackground(background);
        
        boolean isAtcCommentEmpty = taAtcComment.getText().trim().isEmpty();

        int taNormalHeight = (int)lbCallSign.getPreferredSize().getHeight();
        taNormalHeight = value.isAtc() ? taNormalHeight +8 : taNormalHeight + (int)lbGroundSpeed.getPreferredSize().getHeight() + 8;
        int taCommentHeight = isAtcCommentEmpty ? taNormalHeight : taNormalHeight + (int)taAtcComment.getPreferredSize().getHeight() + 8 ;
        strip.setPreferredSize(new Dimension(250,Math.max(taNormalHeight, taCommentHeight)));
        strip.setBackground(background);
        strip.setOpaque(true);

        return this;
    }

    private String composeComment(GuiRadarContact contact) {
        
        FlightPlanData fpd = contact.getFlightPlan();
        String fpRemarks = fpd.getRemarks()==null?"":fpd.getRemarks();
        String atcComments = contact.getAtcComment()==null? "" : contact.getAtcComment();
        
        StringBuffer remarks = new StringBuffer();
        if(!fpd.getDepartureAirport().isEmpty() || !fpd.getDestinationAirport().isEmpty()) {
            remarks.append(fpd.getDepartureAirport().isEmpty()?"____":fpd.getDepartureAirport());
            remarks.append(" ");
            remarks.append(fpd.getDestinationAirport().isEmpty()?"____":fpd.getDestinationAirport());
            remarks.append(" ");
            remarks.append(fpd.getDirectiontoDestinationAirport().isEmpty()?"___":fpd.getDirectiontoDestinationAirport());
            remarks.append(" ");
            remarks.append(fpd.getCruisingAltitude().isEmpty()?"FL___":fpd.getCruisingAltitude());
            remarks.append(" ");
            remarks.append(fpd.getAssignedRoute().isEmpty()?"":fpd.getAssignedRoute()+" ");
            remarks.append(fpd.getAssignedRunway().isEmpty()?"":"RW"+fpd.getAssignedRunway());
        }        
        if(remarks.length()>0 && (!fpRemarks.isEmpty() || !atcComments.isEmpty()) ) {
            remarks.append("\n");
        }
        if(fpd.getRemarks()!=null && !fpd.getRemarks().isEmpty()) {
            remarks.append(fpd.getRemarks());
        }
        if(remarks.length()>0 && !fpd.getRemarks().isEmpty() && !atcComments.isEmpty() ) {
            remarks.append("\n");
        }
        if(!atcComments.isEmpty()) {
            remarks.append(atcComments);
        }

        return remarks.toString();
    }

    private String getSquawkDisplay(GuiRadarContact c) {
        //if(c.getTranspSquawkCode()!=null) System.out.println(c.getCallSign()+": Sq:"+c.getTranspSquawkCode()+" A:"+c.getTranspAltitude());
        if(c.getAssignedSquawk()==null && c.getTranspSquawkCode()==null) return "";
        if(c.getAssignedSquawk()!=null && c.getTranspSquawkCode()==null && (null==c.getTranspSquawkCode() || -9999==c.getTranspSquawkCode() || c.getTranspSquawkCode()==null)) return ""+c.getAssignedSquawk()+"(standby)";
        if(c.getAssignedSquawk()==null && (null==c.getTranspSquawkCode() || -9999==c.getTranspSquawkCode() || c.getTranspSquawkCode()==null)) return "(standby)";
        if(c.getAssignedSquawk()==null && -9999!=c.getTranspSquawkCode() ) return "("+c.getTranspSquawkCode()+")";
        if(c.getAssignedSquawk().equals(c.getTranspSquawkCode())) return ""+c.getTranspSquawkCode();
        return c.getAssignedSquawk()+"("+c.getTranspSquawkCode()+")";
    }
}
