/**
 * Copyright (C) 2012,2013 Wolfram Wagner
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
package de.knewcleus.openradar.gui.status.runways;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.impl.Glideslope;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.chat.auto.AtcMenuChatMessage;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.weather.MetarData;

/**
 * This panel contains the runways
 *
 * @author Wolfram Wagner
 */
public class RunwayPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private GuiMasterController master;
    private volatile boolean showOnlyActiveRunways = false;

    private List<JCheckBox> cbList = new ArrayList<JCheckBox>();

    public RunwayPanel(GuiMasterController guiInteractionManager) {
        this.master = guiInteractionManager;
        this.setLayout(new java.awt.GridBagLayout());
        this.addMouseListener(guiInteractionManager.getStatusManager().getRunwayMouseListener());
    }

    public synchronized void refreshRunways(MetarData metar) {
        this.setBackground(Palette.DESKTOP);
        this.setForeground(Palette.DESKTOP_TEXT);

        this.removeAll();
        cbList.clear();

        int i = 0;
        boolean noRWSelected = true;
        // check if at least one is active
        for (GuiRunway rw : master.getDataRegistry().getRunways().values()) {
            if (rw.isLandingActive() || rw.isStartingActive()) {
                noRWSelected = false;
                break;
            }
        }

        for (GuiRunway rw : master.getDataRegistry().getRunways().values()) {
            rw.setRunwayPanel(this);
            rw.setMetar(metar);

            if (noRWSelected || !showOnlyActiveRunways || rw.isLandingActive() || rw.isStartingActive()) {

                JLabel lbRwCode = new JLabel();
                lbRwCode.setName(rw.getCode());
                lbRwCode.setText(rw.getCode());
                lbRwCode.setToolTipText(rw.getRunwayData().isEnabledAtAll() ? "Right click to show settings"
                        : "Runway disabled by user. Right click to show settings");
                lbRwCode.addMouseListener(master.getStatusManager().getRunwayMouseListener());
                Font f = lbRwCode.getFont();
                f = f.deriveFont(Font.PLAIN, 20);
                lbRwCode.setFont(f);
                lbRwCode.setForeground(rw.getRunwayData().isEnabledAtAll() ? Palette.DESKTOP_TEXT : Color.LIGHT_GRAY);
                GridBagConstraints gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 2 * i;
                gridBagConstraints.gridheight = 2;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
                gridBagConstraints.insets = new java.awt.Insets(6, 4, 0, 0);
                this.add(lbRwCode, gridBagConstraints);

                f = lbRwCode.getFont();
                f = f.deriveFont(Font.PLAIN, 10);

                if (rw.getRunwayData().isEnabledAtAll()) {
                    if (rw.getRunwayData().isStartingEnabled()) {
                        JCheckBox cbStarting = new JCheckBox();
                        // cbStarting.setText("Start");
                        cbStarting.setFont(f);
                        cbStarting.setForeground(rw.getRunwayData().isStartingEnabled() ? Palette.DESKTOP_TEXT : Color.gray);
                        cbStarting.setRolloverEnabled(false);
                        cbStarting.setOpaque(false);
                        cbStarting.setName("STARTING");
                        cbStarting.setModel(rw.createStartCbModel());
                        cbStarting.setEnabled(rw.getRunwayData().isStartingEnabled());
                        cbStarting.addActionListener(rw);
                        gridBagConstraints = new GridBagConstraints();
                        gridBagConstraints.gridx = 1;
                        gridBagConstraints.gridy = 2 * i;
                        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
                        gridBagConstraints.insets = new java.awt.Insets(0, 4, -2, 0);
                        this.add(cbStarting, gridBagConstraints);
                        cbList.add(cbStarting);

                        JCheckBox cbSID = new JCheckBox();
                        cbSID.setText("Start,SID");
                        cbSID.setToolTipText("Toggles route display");
                        cbSID.setFont(f);
                        cbSID.setForeground(rw.getRunwayData().isStartingEnabled() ? Palette.DESKTOP_TEXT : Color.gray);
                        cbSID.setRolloverEnabled(false);
                        cbSID.setOpaque(false);
                        cbSID.setName("STARTROUTE");
                        cbSID.setSelected(rw.isStartRouteEnabled());
                        cbSID.setEnabled(rw.getRunwayData().isStartingEnabled());
                        cbSID.addActionListener(rw);
                        gridBagConstraints = new GridBagConstraints();
                        gridBagConstraints.gridx = 2;
                        gridBagConstraints.gridy = 2 * i;
                        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
                        gridBagConstraints.insets = new java.awt.Insets(0, -2, -2, 0);
                        this.add(cbSID, gridBagConstraints);

                        cbList.add(cbSID);
                    }

                    if (rw.getRunwayData().isLandingEnabled()) {
                        JCheckBox cbLanding = new JCheckBox();
                        // cbLanding.setText("Land");
                        cbLanding.setFont(f);
                        cbLanding.setForeground(rw.getRunwayData().isLandingEnabled() ? Palette.DESKTOP_TEXT : Color.gray);
                        cbLanding.setRolloverEnabled(false);
                        cbLanding.setOpaque(false);
                        cbLanding.setName("LANDING");
                        cbLanding.setModel(rw.createLandingCbModel());
                        cbLanding.setEnabled(rw.getRunwayData().isLandingEnabled());
                        cbLanding.addActionListener(rw);
                        gridBagConstraints = new GridBagConstraints();
                        gridBagConstraints.gridx = 1;
                        gridBagConstraints.gridy = 2 * i + 1;
                        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                        gridBagConstraints.insets = new java.awt.Insets(-2, 4, 0, 0);
                        this.add(cbLanding, gridBagConstraints);
                        cbList.add(cbLanding);

                        JCheckBox cbSTAR = new JCheckBox();
                        cbSTAR.setText("Land,STAR");
                        cbSTAR.setFont(f);
                        cbSTAR.setForeground(rw.getRunwayData().isLandingEnabled() ? Palette.DESKTOP_TEXT : Color.gray);
                        cbSTAR.setRolloverEnabled(false);
                        cbSTAR.setOpaque(false);
                        cbSTAR.setName("LANDINGROUTE");
                        cbSTAR.setSelected(rw.isLandingRouteEnabled());
                        cbSTAR.setEnabled(rw.getRunwayData().isLandingEnabled());
                        cbSTAR.addActionListener(rw);
                        gridBagConstraints = new GridBagConstraints();
                        gridBagConstraints.gridx = 2;
                        gridBagConstraints.gridy = 2 * i + 1;
                        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                        gridBagConstraints.insets = new java.awt.Insets(-2, -2, 0, 0);
                        this.add(cbSTAR, gridBagConstraints);
                        cbList.add(cbSTAR);
                    }
                    JLabel lbShearWind = new JLabel();
                    lbShearWind.setText(String.format("%.0fkn / %.0fkn", rw.getHeadWindSpeed(), rw.getCrossWindSpeed()));
                    lbShearWind.setToolTipText("Strength of head and crosswind");
                    lbShearWind.setFont(f);
                    lbShearWind.setForeground(Palette.DESKTOP_TEXT);
                    if (rw.getHeadWindSpeed() <= -1d) {
                        lbShearWind.setForeground(Palette.WARNING_REARWIND);
                        lbShearWind.setToolTipText("Wind from behind!");
                    }
                    if (metar.getWindSpeedGusts() > 0d) {
                        lbShearWind.setForeground(Palette.WARNING_GUSTS);
                        lbShearWind.setToolTipText("Wind Gusts: " + metar.getWindSpeedGusts() + "kn!");
                    }
                    gridBagConstraints = new GridBagConstraints();
                    gridBagConstraints.gridx = 3;
                    gridBagConstraints.gridy = 2 * i;
                    gridBagConstraints.gridheight = 1;
                    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
                    gridBagConstraints.insets = new java.awt.Insets(6, 4, 0, 0);
                    this.add(lbShearWind, gridBagConstraints);

                    CrossWindDisplay swd = new CrossWindDisplay(rw);
                    swd.setToolTipText("Strength and direction of shear component of wind");
                    gridBagConstraints = new GridBagConstraints();
                    gridBagConstraints.gridx = 3;
                    gridBagConstraints.gridy = 2 * i + 1;
                    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
                    gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
                    this.add(swd, gridBagConstraints);

                    JLabel lbHeading = new JLabel();
                    lbHeading.setName("lb" + rw.getCode() + "Heading");
                    lbHeading.setText(String.format("%1s°", rw.getMagneticHeading()));
                    lbHeading.setToolTipText("Magnetic heading of RW");
                    lbHeading.setFont(f);
                    lbHeading.setForeground(Palette.DESKTOP_TEXT);
                    gridBagConstraints = new GridBagConstraints();
                    gridBagConstraints.gridx = 4;
                    gridBagConstraints.gridy = 2 * i;
                    // gridBagConstraints.gridwidth = 2;
                    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
                    gridBagConstraints.insets = new java.awt.Insets(6, 4, 0, 0);
                    this.add(lbHeading, gridBagConstraints);

                    JLabel lbLength = new JLabel();
                    lbLength.setName("lb" + rw.getCode() + "Length");
                    lbLength.setText(String.format("%1$,3.0f\" x %2$,.0f\"", rw.getLengthFt(), rw.getWidthFt()));
                    lbLength.setToolTipText("Length x Width");
                    lbLength.setFont(f);
                    lbLength.setForeground(Palette.DESKTOP_TEXT);
                    gridBagConstraints = new GridBagConstraints();
                    gridBagConstraints.gridx = 4;
                    gridBagConstraints.gridy = 2 * i + 1;
                    // gridBagConstraints.gridwidth = 2;
                    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
                    gridBagConstraints.insets = new java.awt.Insets(-2, 4, 0, 0);
                    this.add(lbLength, gridBagConstraints);

                    // JLabel lbRelativeWindDirection = new JLabel();
                    // lbRelativeWindDirection.setText("rWD:" + df.format(rw.getWindDeviation()) + "°");
                    // lbRelativeWindDirection.setToolTipText("Wind direction relative to runway heading.");
                    // lbRelativeWindDirection.setFont(f);
                    // lbRelativeWindDirection.setForeground(Palette.DESKTOP_TEXT);
                    // gridBagConstraints = new GridBagConstraints();
                    // gridBagConstraints.gridx = 3;
                    // gridBagConstraints.gridy = 2 * i + 1;
                    // gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                    // gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
                    // pnlMain.add(lbRelativeWindDirection, gridBagConstraints);

                    if (rw.getRunwayData().isLandingEnabled() && rw.getGlideslope() != null) {
                        Glideslope gs = rw.getGlideslope();

                        JLabel lbILS = new JLabel();
                        lbILS.setText(String.format("ILS: %1$s %2$3.2f", gs.getIdentification(), gs.getFrequency().getValue() / Units.MHz));
                        lbILS.setToolTipText("ID, Frequency of ILS and elevation runway end");
                        lbILS.setFont(f);
                        lbILS.setForeground(Palette.DESKTOP_TEXT);
                        gridBagConstraints = new GridBagConstraints();
                        gridBagConstraints.gridx = 5;
                        gridBagConstraints.gridy = 2 * i;
                        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
                        gridBagConstraints.insets = new java.awt.Insets(6, 4, 0, 0);
                        this.add(lbILS, gridBagConstraints);

                        JLabel lbILSData = new JLabel();
                        lbILSData.setText(String.format("GS:%1$1.2f° E:%2$,.0f ft", gs.getGlideslopeAngle(), gs.getElevation() / Units.FT));
                        lbILSData.setToolTipText(String.format("Range: %1$,.0f ft", gs.getRange()));
                        lbILSData.setFont(f);
                        lbILSData.setForeground(Palette.DESKTOP_TEXT);
                        gridBagConstraints = new GridBagConstraints();
                        gridBagConstraints.gridx = 5;
                        gridBagConstraints.gridy = 2 * i + 1;
                        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
                        this.add(lbILSData, gridBagConstraints);
                    }
                } // rw is enabled at all

                JPanel space = new JPanel();
                space.setOpaque(false);
                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 6;
                gridBagConstraints.gridy = 2 * i;
                gridBagConstraints.weightx = 1;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
                this.add(space, gridBagConstraints);

            }
            i++;
        }
        doLayout();
        invalidate(); // marks this region to be layouted
        getParent().validate(); // ask parent to layout everything
        // alternatively, revalidate, would invalidate everthing from here to top level and run validate() there
        getParent().repaint();
        revalidate();
        ((JSplitPane) getParent().getParent().getParent()).setDividerLocation((int) getParent().getParent().getPreferredSize().getHeight());
    }

    public synchronized void updateRunways() {
        for (JCheckBox cb : cbList) {
            cb.repaint();
        }
        master.getRadarBackend().forceRepaint();
    }

    public void toggleActiveRunwayVisibility() {
        showOnlyActiveRunways = !showOnlyActiveRunways;
        refreshRunways(master.getAirportMetar());
        revalidate();
        ((JSplitPane) getParent().getParent().getParent()).setDividerLocation((int) getParent().getParent().getPreferredSize().getHeight());
    }

    public void setActiveRunwayVisibility(boolean b) {
        showOnlyActiveRunways = !b;
        refreshRunways(master.getAirportMetar());
        revalidate();
        ((JSplitPane) getParent().getParent().getParent()).setDividerLocation((int) getParent().getParent().getPreferredSize().getHeight());
    }

    public String getRunwayNumber(Point clickPoint) {
        Component c = getComponentAt(clickPoint);
        if (c instanceof JLabel) {
            JLabel target = (JLabel) c;
            if (target.getName().startsWith("Runway")) {
                return target.getText();
            }
        }
        return null;
    }

    public String getActiveLandingRunways() {
        StringBuilder sb = new StringBuilder();
        boolean initial = true;
        for (GuiRunway rw : master.getDataRegistry().getRunways().values()) {
            if (rw.isLandingActive()) {
                if (initial) {
                    initial=false;
                } else {
                    sb.append(",");
                }

                sb.append(rw.getCode());
//                if (rw.getIlsFrequency() != null && !rw.getIlsFrequency().trim().isEmpty()) {
//                    sb.append(" ILS ");
//                    // sb.append(rw.getGlideslope().getIdentification());
//                    // sb.append(" ");
//                    sb.append(rw.getIlsFrequency().trim());
//                    // sb.append(", GS:");
//                    // sb.append(rw.getGlideslope().getGlideslopeAngle());
//                    // sb.append("%");
//                }
//                sb.append(" (");
//                sb.append(rw.getMagneticHeading());
//                sb.append(")");
            }
        }
        return sb.toString();
    }

    public String getActiveRunways() {
        StringBuilder sb = new StringBuilder();
        boolean initial = true;
        sb.append("land: ");
        for (GuiRunway rw : master.getDataRegistry().getRunways().values()) {
            if (rw.isLandingActive()) {
                if (initial) {
                    initial=false;
                } else {
                    sb.append("+");
                }
                sb.append(rw.getCode());
//                if (rw.getIlsFrequency() != null && !rw.getIlsFrequency().trim().isEmpty()) {
//                    sb.append(" ILS ");
//                    // sb.append(rw.getGlideslope().getIdentification());
//                    // sb.append(" ");
//                    sb.append(rw.getIlsFrequency().trim());
//                    // sb.append(", GS:");
//                    // sb.append(rw.getGlideslope().getGlideslopeAngle());
//                    // sb.append("%");
//                }
//                sb.append(" (");
//                sb.append(rw.getMagneticHeading());
//                sb.append(")");
            }
        }
        sb.append(" start: ");
        initial = true;
        for (GuiRunway rw : master.getDataRegistry().getRunways().values()) {
            if (rw.isStartingActive()) {
                if (initial) {
                    initial=false;
                } else {
                    sb.append("+");
                }
                sb.append(rw.getCode());
            }
        }

        return sb.toString();
    }

    public void sendRunwayMessage(String rwId) {
        GuiRadarContact c = master.getRadarContactManager().getSelectedContact();
        if(c==null) return;
//        double altAboveAirport = c.getAltitude() - master.getDataRegistry().getElevationFt();
        double speed = c.getAirSpeedD();

        boolean isInAir =  speed > 45; // altAboveAirport > 300 &
        boolean isOnGround = speed <= 45;

        boolean rwLandingEnabled = master.getDataRegistry().getRunways().get(rwId).isLandingActive();
        boolean rwStartingEnabled = master.getDataRegistry().getRunways().get(rwId).isStartingActive();

        AtcMenuChatMessage msg = new AtcMenuChatMessage("Assign runway");

        if (isInAir && rwLandingEnabled) {
            msg.addTranslation("en", "%s: Expect landing on runway " + getRunwayInformation(rwId, true));
            msg.setVariables("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target");
            master.getMpChatManager().setAutoAtcMessage(msg);
            c.getFlightPlan().setAssignedRunway(rwId);
        } else if (isOnGround && rwStartingEnabled) {
            msg.addTranslation("en", "%s: Expect departure runway " + getRunwayInformation(rwId, false));
            msg.setVariables("/sim/gui/dialogs/ATC-ML/ATC-MP/CMD-target");
            master.getMpChatManager().setAutoAtcMessage(msg);
            c.getFlightPlan().setAssignedRunway(rwId);
        }
    }

    private String getRunwayInformation(String rwId, boolean isLanding) {
        StringBuilder sb = new StringBuilder();
        GuiRunway rw = master.getDataRegistry().getRunways().get(rwId);
        sb.append(rw.getCode());
        if(isLanding) {
            sb.append(" (");
            sb.append(rw.getMagneticHeading());
            sb.append(") ");
            if (rw.getIlsFrequency() != null && !rw.getIlsFrequency().trim().isEmpty()) {
                sb.append(" ILS ");
                // sb.append(rw.getGlideslope().getIdentification());
                // sb.append(" ");
                sb.append(rw.getIlsFrequency().trim());
                sb.append(", GS:");
                sb.append(rw.getGlideslope().getGlideslopeAngle());
                sb.append("deg.");
            }
        }
        return sb.toString();
    }
}
