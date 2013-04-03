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
package de.knewcleus.openradar.gui.status;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.setup.AirportData.FgComMode;
import de.knewcleus.openradar.gui.status.radio.RadioPanel;
import de.knewcleus.openradar.gui.status.runways.RunwayPanel;
import de.knewcleus.openradar.weather.IMetarListener;
import de.knewcleus.openradar.weather.MetarData;

/**
 * The panel containing the status information
 *
 * @author Wolfram Wagner
 */
public class StatusPanel extends javax.swing.JPanel implements IMetarListener {

    private static final long serialVersionUID = 1L;
    private GuiMasterController master;
    private RadioPanel radioPanel;
    private RunwayPanel runwayPanel;

    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel lbCurrentCallSign;
    private JTextField tfCurrentCallSign;
    private javax.swing.JLabel lbTime;
    private javax.swing.JLabel lbAirport;
    private javax.swing.JLabel lbPressure;
    private javax.swing.JLabel lbVisibility;
    private javax.swing.JLabel lbPtS;
    private javax.swing.JLabel lbSelection;
    private javax.swing.JLabel lbWind;
    private javax.swing.JLabel lbWeatherPhaenomena;
    private javax.swing.JSeparator sep1;
    private javax.swing.JSeparator sep2;
//    private javax.swing.JSeparator sep3;
    private JPanel weatherPanel;
    private LineBorder border = new LineBorder(Palette.LIGHTBLUE, 1);

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    private MetarData currentMetar = null;

    private MouseListener metarMouseListener = new MetarMouseListener();

    /**
     * Creates new form WeatherRadioRunwayPanel
     */
    public StatusPanel(GuiMasterController guiInteractionManager) {
        this.master=guiInteractionManager;
        this.master.getStatusManager().setStatusPanel(this);
        this.master.getMetarReader().addMetarListener(this);

        radioPanel = new RadioPanel(guiInteractionManager,guiInteractionManager.getRadioManager());

        runwayPanel = new RunwayPanel(guiInteractionManager);

        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        initComponents();
    }

    private void initComponents() {
        setBackground(Palette.DESKTOP);
        setForeground(Palette.DESKTOP_TEXT);

        java.awt.GridBagConstraints gridBagConstraints;
// todo nice up!
        headerPanel = new javax.swing.JPanel();
        lbCurrentCallSign = new javax.swing.JLabel();
        tfCurrentCallSign = new JTextField();
        lbTime = new javax.swing.JLabel();
        lbAirport = new javax.swing.JLabel();
        lbSelection = new javax.swing.JLabel();
        lbPressure = new javax.swing.JLabel();
        lbVisibility = new javax.swing.JLabel();
        lbWind = new javax.swing.JLabel();
        lbWeatherPhaenomena = new javax.swing.JLabel();
        lbPtS = new javax.swing.JLabel();
        sep1 = new javax.swing.JSeparator();
        sep2 = new javax.swing.JSeparator();
      //  sep3 = new javax.swing.JSeparator();

        setLayout(new java.awt.GridBagLayout());

        headerPanel.setLayout(new java.awt.GridBagLayout());
        headerPanel.setOpaque(false);

//        lbCurrentCallSign.setForeground(Palette.DESKTOP_TEXT);
//        lbCurrentCallSign.setText("Call Sign:");
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.gridwidth=1;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
//        gridBagConstraints.insets = new java.awt.Insets(4, 10, 0, 4);
//        headerPanel.add(lbCurrentCallSign, gridBagConstraints);
//

        lbTime.setForeground(Palette.DESKTOP_TEXT);
        lbTime.setText("");
        lbTime.setToolTipText("time in UTC");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth=2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        headerPanel.add(lbTime, gridBagConstraints);

        lbAirport.setForeground(Palette.DESKTOP_TEXT);
        lbAirport.setText(master.getDataRegistry().getAirportCode() + " " + master.getDataRegistry().getAirportName().toUpperCase());
        lbAirport.setToolTipText("Your current airport");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth=3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 0, 4);
        headerPanel.add(lbAirport, gridBagConstraints);

        tfCurrentCallSign.setOpaque(true);
        tfCurrentCallSign.setText(master.getDataRegistry().getInitialATCCallSign());
        tfCurrentCallSign.setToolTipText("Current ATC call sign");
        tfCurrentCallSign.setMinimumSize(new Dimension(80,(int)tfCurrentCallSign.getPreferredSize().getHeight()));
        tfCurrentCallSign.setPreferredSize(new Dimension(80,(int)tfCurrentCallSign.getPreferredSize().getHeight()));
        tfCurrentCallSign.addActionListener(master.getStatusManager().getCallSignActionListener());
        tfCurrentCallSign.addKeyListener(master.getStatusManager().getCallSignKeyListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight=2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 0, 10);
        headerPanel.add(tfCurrentCallSign, gridBagConstraints);

        lbSelection.setForeground(Palette.LIGHTBLUE);
        lbSelection.setText("-nobody-");
        lbSelection.setToolTipText("Callsign of selected contact");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 10, 5, 0);
        headerPanel.add(lbSelection, gridBagConstraints);

        lbPtS.setForeground(Palette.LIGHTBLUE);
        lbPtS.setText("(Please select a contact)");
        lbPtS.setToolTipText("magnetic, wind compensated direction Selection => Pointer, back, distance, time needed");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.ipadx = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 5, 4);
        headerPanel.add(lbPtS, gridBagConstraints);

        weatherPanel = new JPanel();
        weatherPanel.setLayout(new GridBagLayout());
        weatherPanel.setOpaque(false);
        weatherPanel.addMouseListener(metarMouseListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        headerPanel.add(weatherPanel, gridBagConstraints);

        lbWind.setForeground(Palette.LIGHTBLUE);
        lbWind.setText("W:");
        lbWind.setToolTipText("Wind: knods@Direction");
        lbWind.addMouseListener(metarMouseListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 0, 4);
        weatherPanel.add(lbWind, gridBagConstraints);

        lbPressure.setForeground(Palette.DESKTOP_TEXT);
        lbPressure.setText("");
        lbPressure.addMouseListener(metarMouseListener);
        lbPressure.setToolTipText("Pressure");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 0, 4);
        weatherPanel.add(lbPressure, gridBagConstraints);

        lbVisibility.setForeground(Palette.DESKTOP_TEXT);
        lbVisibility.setText("");
        lbVisibility.addMouseListener(metarMouseListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 0, 4);
        weatherPanel.add(lbVisibility, gridBagConstraints);

        lbWeatherPhaenomena.setForeground(Palette.DESKTOP_TEXT);
        lbWeatherPhaenomena.setText("");
        lbWeatherPhaenomena.addMouseListener(metarMouseListener);
        //lbWeatherPhaenomena.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 20, 2, 4);
        weatherPanel.add(lbWeatherPhaenomena, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        add(headerPanel, gridBagConstraints);

        if(!master.getDataRegistry().getRadios().isEmpty() && master.getDataRegistry().getFgComMode()!=FgComMode.Off) {
            // in the other case radioPanel is empty and two separator would be displayed...
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
            add(sep1, gridBagConstraints);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(radioPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        add(sep2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        add(runwayPanel, gridBagConstraints);

//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 5;
//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//        gridBagConstraints.weightx = 1.0;
//        gridBagConstraints.weighty = 1.0;
//        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
//        add(sep3, gridBagConstraints);

        updateTime();
        doLayout();

    }

    public void updateTime() {
        lbTime.setText(sdf.format(new Date())+" ZULU");
        if(currentMetar!=null && weatherPanel.getBorder()!=border && currentMetar.isNew()) {
            weatherPanel.setBorder(border);
        }
        if(currentMetar!=null && weatherPanel.getBorder()==border  && !currentMetar.isNew()) {
            weatherPanel.setBorder(null);
        }

        revalidate();
    }

    public void setAirport(String airport) {
        lbAirport.setText(airport);
    }

    public String getCurrentCallSign() {
        return tfCurrentCallSign.getText();
    }

    public void setCurrentCallSign(String callsign) {
        tfCurrentCallSign.setText(callsign);
        tfCurrentCallSign.invalidate();
    }

    public void setSelectedCallSign(String callsign) {
        lbSelection.setText(callsign);
        lbSelection.invalidate();
    }

    @Override
    public void registerNewMetar(MetarData metar) {
        currentMetar = metar;

        StringBuilder sb = new StringBuilder();
        sb.append("Wind: ");
        sb.append(metar.getWindDisplayString());
        lbWind.setText(sb.toString());
        lbPressure.setText(String.format("QNH: %2.2f / %4.1f", metar.getPressureInHG(),metar.getPressureHPa()));
        lbWind.setToolTipText(metar.getMetarBaseData());
        lbPressure.setToolTipText(metar.getMetarBaseData());
        lbVisibility.setText(metar.isCavok()?"CAVOK":"Vis: "+metar.getVisibility()+" "+metar.getVisibilityUnit());
        lbVisibility.setToolTipText(metar.getMetarBaseData());
        lbWeatherPhaenomena.setText("("+ metar.getWeatherPhaenomena()+")");
        lbWeatherPhaenomena.setToolTipText(metar.getweatherPhaenomenaForHumans());

        runwayPanel.refreshRunways(metar);
        doLayout();
        revalidate();
    }

    public void setSelectionToPointer(Long degreesToPointer, Long degreesToSelection, Double distanceMiles, Integer timeMinutes, Integer timeSeconds) {
        String dTP = degreesToPointer==null ? "n/a" : String.format("%03d",degreesToPointer);
        String dTS = degreesToSelection==null ? "n/a" : String.format("%03d",degreesToSelection);
        String dist = distanceMiles==null ? "n/a" : String.format("%.1f", distanceMiles);
        String min = timeMinutes==null ? "n/a" : String.format("%1d:%02d",timeMinutes,timeSeconds);

        lbPtS.setText(dTP+"° / "+dTS+"°     "+dist+" NM   ETA "+min);
    }

    public String getActiveRunways() {
        return runwayPanel.getActiveRunways();
    }
    public void updateRunways() {
        runwayPanel.refreshRunways(master.getMetar());
    }

    private class MetarMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount()==2) {
                // send ATIS
                if(e.getSource()==lbWeatherPhaenomena) {
                    master.getMpChatManager().sendMessages(master.getMetar().createATIS(master,false)); // long
                } else {
                    master.getMpChatManager().sendMessages(master.getMetar().createATIS(master,true)); // short
                }
            }
        }
    }
}
