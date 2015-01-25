/**
 * Copyright (C) 2012-2014 Wolfram Wagner
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
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
//    private javax.swing.JLabel lbCurrentCallSign;
    private JTextField tfCurrentCallSign;
    private javax.swing.JLabel lbTime;
    private javax.swing.JLabel lbAirport;
    private javax.swing.JLabel lbPressure;
    private javax.swing.JLabel lbVisibility;
    private javax.swing.JLabel lbPtS;
    private javax.swing.JLabel lbSelection;
    private javax.swing.JLabel lbFlightConditions;
    private javax.swing.JLabel lbWind;
    private javax.swing.JLabel lbWeatherPhaenomena;
    private javax.swing.JSeparator sep1;
    private javax.swing.JSeparator sep2;
//    private javax.swing.JSeparator sep3;
    private JPanel weatherPanel;
    private JPanel addWeatherStationPanel;
    private LineBorder border = new LineBorder(Palette.LIGHTBLUE, 1);

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    private MetarData currentMetar = null;

    private final MetarSettingsDialog metarSettingsDialog;

    private MouseListener metarMouseListener = new MetarMouseListener();
    private StatusPanelMouseListener statusPanelMouseListener = new StatusPanelMouseListener();

    /**
     * Creates new form WeatherRadioRunwayPanel
     */
    public StatusPanel(GuiMasterController guiInteractionManager) {
        this.master=guiInteractionManager;
        this.master.getStatusManager().setStatusPanel(this);
        this.master.getMetarReader().addMetarListener(this);

        radioPanel = new RadioPanel(guiInteractionManager,guiInteractionManager.getRadioManager());

        runwayPanel = new RunwayPanel(guiInteractionManager);

        metarSettingsDialog = new MetarSettingsDialog(master);

        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        initComponents();
    }

    private void initComponents() {
        setBackground(Palette.DESKTOP);
        setForeground(Palette.DESKTOP_TEXT);

        java.awt.GridBagConstraints gridBagConstraints;
// todo nice up!
        headerPanel = new javax.swing.JPanel();
//        lbCurrentCallSign = new javax.swing.JLabel();
        tfCurrentCallSign = new JTextField();
        lbTime = new javax.swing.JLabel();
        lbAirport = new javax.swing.JLabel();
        lbSelection = new javax.swing.JLabel();
        lbPressure = new javax.swing.JLabel();
        lbVisibility = new javax.swing.JLabel();
        lbFlightConditions = new javax.swing.JLabel();
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
        lbTime.addMouseListener(statusPanelMouseListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth=2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        headerPanel.add(lbTime, gridBagConstraints);

        lbAirport.setForeground(Palette.DESKTOP_TEXT);
        lbAirport.setText(master.getAirportData().getAirportCode() + " " + master.getAirportData().getAirportName().toUpperCase());
        updateTransitionValues(); // sets the tool tip text
        lbAirport.addMouseListener(statusPanelMouseListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth=3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 0, 4);
        headerPanel.add(lbAirport, gridBagConstraints);

        tfCurrentCallSign.setOpaque(true);
        tfCurrentCallSign.setText(master.getAirportData().getInitialATCCallSign());
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
        weatherPanel.setName(master.getAirportData().getAirportCode());
        weatherPanel.addMouseListener(metarMouseListener);
        weatherPanel.addComponentListener(new StatusResizeListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        headerPanel.add(weatherPanel, gridBagConstraints);

        lbFlightConditions.setForeground(Color.white);
        lbFlightConditions.setText("VFR");
        lbFlightConditions.setOpaque(true);
        lbFlightConditions.setFont(lbFlightConditions.getFont().deriveFont(Font.BOLD));
        lbFlightConditions.setForeground(Color.white);
        lbFlightConditions.addMouseListener(metarMouseListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 0, 0);
        weatherPanel.add(lbFlightConditions, gridBagConstraints);

        lbWind.setForeground(Palette.LIGHTBLUE);
        lbWind.setText("W:");
        lbWind.setToolTipText("Wind: knods@Direction");
        lbWind.setName(master.getAirportData().getAirportCode());
        lbWind.addMouseListener(metarMouseListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 0, 0);
        weatherPanel.add(lbWind, gridBagConstraints);

        lbPressure.setForeground(Palette.DESKTOP_TEXT);
        lbPressure.setText("");
        lbPressure.setName(master.getAirportData().getAirportCode());
        lbPressure.addMouseListener(metarMouseListener);
        lbPressure.setToolTipText("Pressure");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 0, 0);
        weatherPanel.add(lbPressure, gridBagConstraints);

        lbVisibility.setForeground(Palette.DESKTOP_TEXT);
        lbVisibility.setText("");
        lbVisibility.setName(master.getAirportData().getAirportCode());
        lbVisibility.addMouseListener(metarMouseListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 0, 4);
        weatherPanel.add(lbVisibility, gridBagConstraints);

        lbWeatherPhaenomena.setForeground(Palette.DESKTOP_TEXT);
        lbWeatherPhaenomena.setText("");
        lbWeatherPhaenomena.setName(master.getAirportData().getAirportCode());
        lbWeatherPhaenomena.addMouseListener(metarMouseListener);
        //lbWeatherPhaenomena.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 4);
        weatherPanel.add(lbWeatherPhaenomena, gridBagConstraints);

        addWeatherStationPanel = new JPanel();
        addWeatherStationPanel.setLayout(new GridBagLayout());
        addWeatherStationPanel.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        weatherPanel.add(addWeatherStationPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        add(headerPanel, gridBagConstraints);

        if(!master.getAirportData().getRadios().isEmpty() && master.getAirportData().getFgComMode()!=FgComMode.Off) {
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
        lbTime.setText(sdf.format(new Date())+" UTC  "+String.format("%d/TL%03d", master.getAirportData().getTransitionAlt(),master.getAirportData().getTransitionFL(master)));
        if(currentMetar!=null && weatherPanel.getBorder()!=border && currentMetar.isNew()) {
            weatherPanel.setBorder(border);
            doLayout();
            weatherPanel.invalidate();
            weatherPanel.repaint();
        }
        if(currentMetar!=null && weatherPanel.getBorder()==border  && !currentMetar.isNew()) {
            weatherPanel.setBorder(null);
            doLayout();
            weatherPanel.invalidate();
            weatherPanel.repaint();
        }
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
        if(metar.getAirportCode().equals(master.getAirportData().getMetarSource())) {
            addWeatherStationPanel.removeAll();
            
            currentMetar = metar;

            lbFlightConditions.setText(" "+metar.getFlightConditions()+" ");
            lbFlightConditions.setBackground(metar.getFlightConditionColor());
            StringBuilder sb = new StringBuilder();
            //sb.append("Wind: ");
            sb.append(metar.getWindDisplayString());
            lbWind.setText(sb.toString());
            lbPressure.setText(String.format("QNH: %2.2f/%4.0f", metar.getPressureInHG(),metar.getPressureHPa()));
            lbWind.setToolTipText(metar.getMetarBaseData());
            lbPressure.setToolTipText(metar.getMetarBaseData());
            lbVisibility.setText(metar.isCavok()?"CAVOK":"V: "+metar.getVisibility()+""+metar.getVisibilityUnit());
            lbVisibility.setToolTipText(metar.getMetarBaseData());
            String phaenomena = metar.getWeatherPhaenomena().trim();
            if(phaenomena.isEmpty()) {
                lbWeatherPhaenomena.setVisible(false);
            } else {
                lbWeatherPhaenomena.setVisible(true);
                lbWeatherPhaenomena.setText("("+ phaenomena+")");
                lbWeatherPhaenomena.setToolTipText(metar.getWeatherPhaenomenaForHumans());
            }
            runwayPanel.refreshRunways(metar);
            doLayout();
            revalidate();
            updateTime();
        } else {
            // fill add weather station panel
            String addMetarSources = master.getAirportData().getAddMetarSources();
            addWeatherStationPanel.removeAll();
            if(addMetarSources!=null && !addMetarSources.trim().isEmpty()) {
                StringTokenizer st = new StringTokenizer(addMetarSources,",");
                int i=0;
                JPanel pnlLine=null;
                while(st.hasMoreElements()) {
                    String code = st.nextToken().trim();
                    MetarData m = master.getMetarReader().getMetar(code);
                    if(m.getWindDirectionI()>=0) {
                        if(pnlLine==null || 0==i - (i/3)*3) {
                            pnlLine = new JPanel();
                            pnlLine.setOpaque(false);
                            pnlLine.setLayout(new GridBagLayout());
                            GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
                            gridBagConstraints.gridx = 0;
                            gridBagConstraints.gridy = i;
                            gridBagConstraints.gridwidth = 0;
                            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                            gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
                            addWeatherStationPanel.add(pnlLine, gridBagConstraints);
                        }
                        JLabel lbAddMetar = new JLabel();
                        lbAddMetar.setFont(lbAddMetar.getFont().deriveFont(12.0f));
                        lbAddMetar.setForeground(Palette.DESKTOP_TEXT);
                        lbAddMetar.setName(code);
                        lbAddMetar.setText(code+": "+m.getWindDisplayString());
                        lbAddMetar.setToolTipText(m.getMetarBaseData());
                        lbAddMetar.addMouseListener(metarMouseListener);
                        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
                        gridBagConstraints.gridx = i - (i/3)*3;
                        gridBagConstraints.gridy = 0;
                        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 4);
                        pnlLine.add(lbAddMetar, gridBagConstraints);
                        i++;
                    }
                }
            }
            doLayout();
            revalidate();
            updateTime();
        }
    }

    public void setSelectionToPointer(Long degreesToPointer, Long degreesToSelection, Double distanceMiles, Integer timeMinutes, Integer timeSeconds) {
        String dTP = degreesToPointer==null ? "n/a" : String.format("%03d",degreesToPointer);
        String dTS = degreesToSelection==null ? "n/a" : String.format("%03d",degreesToSelection);
        String dist = distanceMiles==null ? "n/a" : String.format("%.1f", distanceMiles);
        String min = timeMinutes==null ? "n/a" : String.format("%1d:%02d",timeMinutes,timeSeconds);

        lbPtS.setText(dTP+"° ("+dTS+"°)   "+dist+" NM   ETA "+min);
    }

    public String getActiveRunways() {
        return runwayPanel.getActiveRunways();
    }
    public String getActiveLandingRunways() {
        return runwayPanel.getActiveLandingRunways();
    }
    public void updateRunways() {
        runwayPanel.refreshRunways(master.getAirportMetar());
    }

    private class StatusPanelMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            //if(e.getClickCount()==1 && e.getButton() == MouseEvent.BUTTON2) {
                if(e.getSource()==lbAirport || e.getSource()==lbTime) {
                    master.getRadarContactManager().getTransponderSettingsDialog().show(e);
                } 
            //}
        }
    }
    
    private class MetarMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount()==2 && e.getButton() == MouseEvent.BUTTON1) {
                // send ATIS
                String metarCode = ((JComponent)e.getSource()).getName();
                MetarData metar = master.getMetarReader().getMetar(metarCode);
                if(metarCode.equals(master.getAirportData().getAirportCode())) {
                    if(e.getSource()==lbWeatherPhaenomena) {
                        master.getMpChatManager().sendMessages(metar.createATIS(master,false)); // long
                    } else {
                        master.getMpChatManager().sendMessages(metar.createATIS(master,true)); // short
                    }
                } else {
                    ArrayList<String> msg = new ArrayList<String>();
                    msg.add(metar.getMetarBaseData());
                    master.getMpChatManager().sendMessages(msg); // METAR
                }
            } else if(e.getButton() == MouseEvent.BUTTON3) {
                metarSettingsDialog.show(e);
            }
        }
    }

    public boolean isMetarDialogVisible() {
        return metarSettingsDialog.isVisible();
    }

    public void updateTransitionValues() {
        lbAirport.setToolTipText((String.format("<html><body>Magnetic declination: %1.1f°<br>Transition: %d/FL%03d</body></html>", master.getAirportData().getMagneticDeclination(),master.getAirportData().getTransitionAlt(),master.getAirportData().getTransitionFL(master))));
    }
    
    private class StatusResizeListener extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            super.componentResized(e);
            
            ((JSplitPane) getParent().getParent()).setDividerLocation((int) getParent().getPreferredSize().getHeight());
        }
    }
}
