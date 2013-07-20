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
package de.knewcleus.openradar.gui.contacts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;

public class ContactSettingsDialog extends JFrame {

    private static final long serialVersionUID = 1L;
    private final GuiMasterController master;
    private final RadarContactController controller;
    private volatile GuiRadarContact contact = null;

    private TitledBorder tbContacts;

    private JCheckBox chbFgComSupport;
    private JComboBox<String> cbLanguages;

    private JPanel jPnlOnwerShip;

    private JTextField tfFlightCode;
    private JComboBox<String> cbFlightPlanTypes;
    private JTextField tfAircraft;
    private JTextField tfModel;

    private JComboBox<String> cbHandoverATCs = new JComboBox<String>();
;

    private JComboBox<String> cbAssignedRunway;
    private JComboBox<String> cbAssignedRoute;
    private JTextField tfAssignedAltitude;

    private JTextField tfDepAirport;
    private JTextField tfDepTime;
    private JTextField tfDestAirport;
    private JTextField tfArrivalTime;
    private JTextField tfFpAltitude;

    private JTextField tfRoute;
    private JTextField tfAlternAirports;

    private javax.swing.JScrollPane spDetails;
    private javax.swing.JTextPane tpDetails;

    private FpMouseListener fpMouseListener = new FpMouseListener();

    public ContactSettingsDialog(GuiMasterController master, RadarContactController controller) {
        this.master = master;
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        this.addWindowListener(new DialogCloseListener());

        // Determine what the default GraphicsDevice can support.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        boolean isUniformTranslucencySupported = gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT);
        if (isUniformTranslucencySupported) {
            this.setOpacity(0.8f);
        }

        setLayout(new GridBagLayout());

        setForeground(Palette.DESKTOP_TEXT);
        setBackground(Palette.DESKTOP);

        // Contact settings details

        JPanel jPnlContact = new JPanel();
        jPnlContact.setOpaque(false);
        jPnlContact.setLayout(new GridBagLayout());
        tbContacts = new TitledBorder("Contact");
        jPnlContact.setBorder(tbContacts);

        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        add(jPnlContact, gridBagConstraints);

        initContactData(jPnlContact);

        // Control panel

        JPanel jPnlControl = new JPanel();
        jPnlControl.setOpaque(false);
        jPnlControl.setLayout(new GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        add(jPnlControl, gridBagConstraints);

        jPnlOnwerShip = new JPanel();
        jPnlOnwerShip.setOpaque(false);
        jPnlOnwerShip.setLayout(new GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPnlControl.add(jPnlOnwerShip, gridBagConstraints);


        JButton btReset = new JButton("Reset Flightplan");
        btReset.setName("RESET");
        btReset.addMouseListener(fpMouseListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlControl.add(btReset, gridBagConstraints);

        // Flight details

        JPanel jPnlFlight = new JPanel();
        jPnlFlight.setOpaque(false);
        jPnlFlight.setLayout(new GridBagLayout());
        jPnlFlight.setBorder(new TitledBorder("Flight plan"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(jPnlFlight, gridBagConstraints);

        initFlightDetails(jPnlFlight);

        // Comment

        JPanel jPnlComment = new JPanel();
        jPnlComment.setOpaque(false);
        jPnlComment.setLayout(new GridBagLayout());
        jPnlComment.setBorder(new TitledBorder("Comments"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(jPnlComment, gridBagConstraints);

        spDetails = new javax.swing.JScrollPane();
        spDetails.setMinimumSize(new Dimension(200, 60));
        spDetails.setPreferredSize(new Dimension(200, 60));
        tpDetails = new javax.swing.JTextPane();

        tpDetails.setToolTipText("ATC Notes: RETURN save, STRG+RETURN newline");
        tpDetails.addKeyListener(new DetailsKeyListener());
        tpDetails.setOpaque(true);
        tpDetails.setMinimumSize(new Dimension(200, 60));
        tpDetails.setPreferredSize(new Dimension(200, 60));
        spDetails.setViewportView(tpDetails);
        master.setDetailsArea(tpDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPnlComment.add(spDetails, gridBagConstraints);

    }

    private void initContactData(JPanel jPnlContact) {
        // Contact Line 1

        JPanel jPnlLine1 = new JPanel();
        jPnlLine1.setOpaque(false);
        jPnlLine1.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPnlContact.add(jPnlLine1, gridBagConstraints);

        JLabel lbLang = new JLabel("native Language:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPnlLine1.add(lbLang, gridBagConstraints);

        cbLanguages = new JComboBox<String>(controller.getAutoAtcLanguages());
        cbLanguages.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        jPnlLine1.add(cbLanguages, gridBagConstraints);

        chbFgComSupport = new JCheckBox("FgCom");
        chbFgComSupport.setToolTipText("This contact uses FgCom.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 4, 8);
        jPnlLine1.add(chbFgComSupport, gridBagConstraints);


        // Contact Line 2

        JPanel jPnlLine2 = new JPanel();
        jPnlLine2.setOpaque(false);
        jPnlLine2.setLayout(new GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPnlContact.add(jPnlLine2, gridBagConstraints);

        JLabel lbAircraft = new JLabel("Aircraft:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine2.add(lbAircraft, gridBagConstraints);

        tfAircraft = new JTextField(10);
        tfAircraft.setToolTipText("ICAO Aircraft code if known.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPnlLine2.add(tfAircraft, gridBagConstraints);

        JLabel lbModel = new JLabel("Model:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPnlLine2.add(lbModel, gridBagConstraints);

        tfModel = new JTextField(14);
        tfModel.setToolTipText("Simulation model used by this contact");
        tfModel.setEditable(false);
        tfModel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPnlLine2.add(tfModel, gridBagConstraints);

        // Contact Line 3

        JPanel jPnlLine3 = new JPanel();
        jPnlLine3.setOpaque(false);
        jPnlLine3.setLayout(new GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPnlContact.add(jPnlLine3, gridBagConstraints);

        JLabel lbAssRw = new JLabel("Assgnd RWY:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine3.add(lbAssRw, gridBagConstraints);

        cbAssignedRunway = new JComboBox<String>();
        cbAssignedRunway.setEditable(true);
        Dimension dim = cbAssignedRunway.getPreferredSize();
        cbAssignedRunway.setPreferredSize(new Dimension(60, (int) dim.getHeight()));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine3.add(cbAssignedRunway, gridBagConstraints);

        JLabel lbAssRoute = new JLabel(" Route:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine3.add(lbAssRoute, gridBagConstraints);

        cbAssignedRoute = new JComboBox<String>();
        dim = cbAssignedRoute.getPreferredSize();
        cbAssignedRoute.setPreferredSize(new Dimension(100, (int) dim.getHeight()));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine3.add(cbAssignedRoute, gridBagConstraints);


        JLabel lbAssAltitude = new JLabel(" Altitude:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine3.add(lbAssAltitude, gridBagConstraints);

        tfAssignedAltitude = new JTextField(7);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine3.add(tfAssignedAltitude, gridBagConstraints);

    }

    private void initFlightDetails(JPanel jPnlFlight) {

        // line 1

        JPanel jPnlLine1 = new JPanel();
        jPnlLine1.setOpaque(false);
        jPnlLine1.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPnlFlight.add(jPnlLine1, gridBagConstraints);

        JLabel lbCode = new JLabel("Code:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine1.add(lbCode, gridBagConstraints);

        tfFlightCode = new JTextField(10);
        tfFlightCode.setToolTipText("Optional code for this flight. Not the callsign.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPnlLine1.add(tfFlightCode, gridBagConstraints);

        JLabel lbType = new JLabel("Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPnlLine1.add(lbType, gridBagConstraints);

        cbFlightPlanTypes = new JComboBox<String>();
        cbFlightPlanTypes.setModel(FlightPlanData.getFpTypeModel());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine1.add(cbFlightPlanTypes, gridBagConstraints);

        // line 2

        JPanel jPnlLine2 = new JPanel();
        jPnlLine2.setOpaque(false);
        jPnlLine2.setLayout(new GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        jPnlFlight.add(jPnlLine2, gridBagConstraints);

        JLabel lbFrom = new JLabel("Departure:");
        lbFrom.setName("START_HERE");
        lbFrom.setToolTipText("Click to initialize flightplan as originating from "+master.getDataRegistry().getAirportCode());
        lbFrom.addMouseListener(fpMouseListener);
        lbFrom.setForeground(Color.blue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine2.add(lbFrom, gridBagConstraints);

        tfDepAirport = new JTextField(4);
        tfDepAirport.setToolTipText("Departure airport");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine2.add(tfDepAirport, gridBagConstraints);

        tfDepTime = new JTextField(5);
        tfDepTime.setToolTipText("Time of departure in UTC hh:mm");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine2.add(tfDepTime, gridBagConstraints);

        JLabel lbTo = new JLabel("Arrival:");
        lbTo.setName("LAND_HERE");
        lbTo.addMouseListener(fpMouseListener);
        lbTo.setToolTipText("Click to file flightplan to go to "+master.getDataRegistry().getAirportCode());
        lbTo.setForeground(Color.blue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPnlLine2.add(lbTo, gridBagConstraints);

        tfDestAirport = new JTextField(4);
        tfDestAirport.setToolTipText("Destination airport code");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine2.add(tfDestAirport, gridBagConstraints);

        JLabel lbETA = new JLabel("ETA:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPnlLine2.add(lbETA, gridBagConstraints);

        tfArrivalTime = new JTextField(5);
        tfArrivalTime.setToolTipText("estimated arrival time");
        tfArrivalTime.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine2.add(tfArrivalTime, gridBagConstraints);


        // line 3

        JPanel jPnlLine3 = new JPanel();
        jPnlLine3.setOpaque(false);
        jPnlLine3.setLayout(new GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPnlFlight.add(jPnlLine3, gridBagConstraints);

        JLabel lbVia = new JLabel("Route:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine3.add(lbVia, gridBagConstraints);

        tfRoute = new JTextField(20);
        tfRoute.setToolTipText("Comma separated list of navaids.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine3.add(tfRoute, gridBagConstraints);

        // line 4

        JPanel jPnlLine4 = new JPanel();
        jPnlLine4.setOpaque(false);
        jPnlLine4.setLayout(new GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPnlFlight.add(jPnlLine4, gridBagConstraints);

        JLabel lbCruisingAlt = new JLabel("Cruising Alt.:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine4.add(lbCruisingAlt, gridBagConstraints);

        tfFpAltitude = new JTextField(10);
        tfFpAltitude.setToolTipText("Either the alt in ft or as flight level (FLxxx)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine4.add(tfFpAltitude, gridBagConstraints);

        JLabel lbAltAirports = new JLabel("Alt. Airports:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPnlLine4.add(lbAltAirports, gridBagConstraints);

        tfAlternAirports = new JTextField();
        tfAlternAirports.setToolTipText("Comma separated list of alternative airports.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine4.add(tfAlternAirports, gridBagConstraints);


        // line 5
//
//        JPanel jPnlLine5 = new JPanel();
//        jPnlLine5.setOpaque(false);
//        jPnlLine5.setLayout(new GridBagLayout());
//
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 4;
//        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gridBagConstraints.weightx = 1.0;
//        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
//        jPnlFlight.add(jPnlLine5, gridBagConstraints);
//

        // line 6
        //
        // JPanel jPnlLine6 = new JPanel();
        // jPnlLine6.setOpaque(false);
        // jPnlLine6.setLayout(new GridBagLayout());
        //
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 0;
        // gridBagConstraints.gridy = 5;
        // gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        // gridBagConstraints.weightx = 1.0;
        // gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        // jPnlFlight.add(jPnlLine6,gridBagConstraints);

    }

    public void show(GuiRadarContact contact, MouseEvent e) {
        this.contact = contact;

        initDataDisplay(contact);

        Dimension innerSize = getPreferredSize();
        setSize(new Dimension((int) innerSize.getWidth() + 8, (int) innerSize.getHeight() + 8));
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maxBounds = env.getMaximumWindowBounds();

        Point2D p = e.getLocationOnScreen();// ((JComponent) e.getSource()).getLocationOnScreen();
        p = new Point2D.Double(p.getX() - this.getWidth()/2, p.getY());

        int lowerDistanceToScreenBorder = 50;
        if (p.getY() + getHeight() > maxBounds.getHeight() - lowerDistanceToScreenBorder) {
            p = new Point2D.Double(p.getX(), maxBounds.getHeight() - getHeight() - lowerDistanceToScreenBorder);
        }
        setLocation(new Point((int) p.getX(), (int) p.getY()));
        doLayout();
        setVisible(true);
        tpDetails.requestFocus();
    }

    private void initDataDisplay(GuiRadarContact contact) {

        tbContacts.setTitle("<html><body>Contact <b>"+contact.getCallSign()+"</b></body></html>");
        chbFgComSupport.setSelected(contact.hasFgComSupport());
        tpDetails.setText(contact.getAtcComment());
        cbLanguages.setSelectedItem(contact.getAtcLanguage());


        FlightPlanData fpd = contact.getFlightPlan();

        String aircraftCode = fpd.getAircraft();
        tfAircraft.setText(aircraftCode==null|| aircraftCode.isEmpty()?contact.getAircraftCode():aircraftCode);
        tfModel.setText(contact.getModel());

        tfFlightCode.setText(fpd.getFlightCode());
        cbFlightPlanTypes.setSelectedItem(fpd.getType());

        tfDepAirport.setText(fpd.getDepartureAirport());
        tfDepTime.setText(fpd.getDeparture());
        tfDestAirport.setText(fpd.getDestinationAirport());
        tfArrivalTime.setText(fpd.getArrivalTime());
        tfRoute.setText(fpd.getRoute());
        tfAlternAirports.setText(fpd.getAlternativeDestinationAirports());
        tfFpAltitude.setText(fpd.getCruisingAltitude());

        cbAssignedRunway.setModel(master.getDataRegistry().getRunwayModel(true));
        cbAssignedRunway.setSelectedItem(fpd.getAssignedRunway());
        cbAssignedRoute.setModel(master.getDataRegistry().getNavaidDB().getRoutesCbModel(true));
        cbAssignedRoute.setSelectedItem(fpd.getAssignedRoute());

        tfAssignedAltitude.setText(fpd.getAssignedAltitude());
        // cbFlightPlanStatus.setSelectedItem(fpd.getFpStatus());

        cbHandoverATCs.setModel(master.getRadarContactManager().getOtherATCsCbModel());

        initOwnership(fpd);
    }
    /** fills the panel in fron of the reset flightplan button */
    private void initOwnership(FlightPlanData fpd) {
        jPnlOnwerShip.removeAll();

        if(fpd.isOwnedByMe(master.getDataRegistry())) {
            // my contact
            JLabel lbControlledBy = new JLabel("Controlled by me.");
            lbControlledBy.setFont(lbControlledBy.getFont().deriveFont(Font.ITALIC));
            GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
            jPnlOnwerShip.add(lbControlledBy, gridBagConstraints);

            JLabel lbHandover = new JLabel("Handover to:");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
            jPnlOnwerShip.add(lbHandover, gridBagConstraints);

            cbHandoverATCs.setEditable(true);
            Dimension dim = cbAssignedRunway.getPreferredSize();
            cbHandoverATCs.setPreferredSize(new Dimension(80, (int) dim.getHeight()));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
            jPnlOnwerShip.add(cbHandoverATCs, gridBagConstraints);

            JButton btReleaseControl = new JButton("Release");
            btReleaseControl.setName("RELEASE_CONTROL");
            btReleaseControl.setToolTipText("Release the contact from control.");
            btReleaseControl.addMouseListener(fpMouseListener);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx=1.0;
            gridBagConstraints.anchor = GridBagConstraints.EAST;
            gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
            jPnlOnwerShip.add(btReleaseControl, gridBagConstraints);

            cbHandoverATCs.setSelectedItem(fpd.getHandover());

        } else if(fpd.isOfferedToMe(master.getDataRegistry())) {
            // offered to me
            JLabel lbControlledBy = new JLabel("Offered to my by: " + fpd.getOwner());
            lbControlledBy.setFont(lbControlledBy.getFont().deriveFont(Font.ITALIC));
            GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
            jPnlOnwerShip.add(lbControlledBy, gridBagConstraints);

            JButton btTakeOver = new JButton("Take over");
            btTakeOver.setName("TAKE_OVER");
            btTakeOver.addMouseListener(fpMouseListener);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx=1.0;
            gridBagConstraints.anchor = GridBagConstraints.EAST;
            gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
            jPnlOnwerShip.add(btTakeOver, gridBagConstraints);


        } else if(fpd.getOwner()!=null && !fpd.getOwner().isEmpty()) {
            // owned by somebody else
            JLabel lbControlledBy = new JLabel("Controlled by: ");
            lbControlledBy.setFont(lbControlledBy.getFont().deriveFont(Font.ITALIC));
            GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
            jPnlOnwerShip.add(lbControlledBy, gridBagConstraints);

            JLabel lbOwner = new JLabel(fpd.getOwner());
            lbOwner.setFont(lbOwner.getFont().deriveFont(Font.BOLD));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx=1.0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
            jPnlOnwerShip.add(lbOwner, gridBagConstraints);
        } else {
            // owned by nobody
            JLabel lbControlledBy = new JLabel("Uncontrolled...");
            lbControlledBy.setFont(lbControlledBy.getFont().deriveFont(Font.ITALIC));
            GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
            jPnlOnwerShip.add(lbControlledBy, gridBagConstraints);

            JButton btControl = new JButton("Control");
            btControl.setName("CONTROL");
            btControl.addMouseListener(fpMouseListener);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx=1.0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
            jPnlOnwerShip.add(btControl, gridBagConstraints);

        }
    }

    private class DialogCloseListener extends WindowAdapter {
        @Override
        public void windowClosed(WindowEvent e) {
            closeDialog();
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            closeDialog();
        }

        @Override
        public void windowLostFocus(WindowEvent e) {
            closeDialog();
        }

    }

    public void closeDialog() {
        if (isVisible()) {

            saveData(contact);

            setVisible(false);
        }
    }

    private void saveData(GuiRadarContact contact2) {
        contact.setFgComSupport(chbFgComSupport.isSelected());
        contact.setAtcComment(tpDetails.getText().trim());
        contact.setAtcLanguage(controller.getAutoAtcLanguages(cbLanguages.getSelectedIndex()));

        FlightPlanData fpd = contact.getFlightPlan();


        fpd.setAircraft(tfAircraft.getText());

        fpd.setFlightCode(tfFlightCode.getText());
        fpd.setType((String)cbFlightPlanTypes.getSelectedItem());

        fpd.setDepartureAirport(tfDepAirport.getText());
        fpd.setDeparture(tfDepTime.getText());
        fpd.setDestinationAirport(tfDestAirport.getText());
        fpd.setRoute(tfRoute.getText());
        fpd.setAlternativeDestinationAirports(tfAlternAirports.getText());
        fpd.setCruisingAltitude(tfFpAltitude.getText());

        fpd.setAssignedRunway((String)cbAssignedRunway.getSelectedItem());
        fpd.setAssignedRoute((String)cbAssignedRoute.getSelectedItem());
        fpd.setAssignedAltitude(tfAssignedAltitude.getText());

        if(contact2.getFlightPlan().isOwnedByMe(master.getDataRegistry())) {
            fpd.setHandover((String)cbHandoverATCs.getSelectedItem());
        }
        // cbFlightPlanStatus.setSelectedItem(fpd.getFpStatus());
    }

    private class DetailsKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            JTextPane ta = (JTextPane) e.getSource();
            String currentText = ta.getText();
            if (e.getKeyChar() == KeyEvent.VK_ENTER && !e.isControlDown()) {
                int carretPos = ta.getCaretPosition() - 1;
                currentText = new StringBuilder(currentText).deleteCharAt(carretPos).toString();
                master.getRadarContactManager().setAtcComment(currentText);
                ta.setText(currentText); // remove newline
                ta.setCaretPosition(carretPos);
                e.consume();

                closeDialog();
            }
            if (e.getKeyChar() == KeyEvent.VK_ENTER && e.isControlDown()) {
                int carretPos = ta.getCaretPosition();
                currentText = new StringBuilder(currentText).insert(carretPos, "\n").toString();
                master.getRadarContactManager().setAtcComment(currentText); // save and continue
                ta.setText(currentText);
                ta.setCaretPosition(carretPos + 1);
                ta.requestFocus();
            }
        }
    }

    private class FpMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            JComponent source = (JComponent)e.getSource();

            if("RESET".equals(source.getName())) {
                contact.getFlightPlan().reset(master.getDataRegistry());
                initDataDisplay(contact);
            } else if("START_HERE".equals(source.getName())) {
                contact.getFlightPlan().startFromHere(master.getDataRegistry());
                initDataDisplay(contact);
            } else if("LAND_HERE".equals(source.getName())) {
                contact.getFlightPlan().landHere(master.getDataRegistry());
                initDataDisplay(contact);
            } else if("TAKE_OVER".equals(source.getName())) {
                contact.getFlightPlan().takeControl(master.getDataRegistry());
                initDataDisplay(contact);
            } else if("CONTROL".equals(source.getName())) {
                contact.getFlightPlan().takeControl(master.getDataRegistry());
                initDataDisplay(contact);
            } else if("RELEASE_CONTROL".equals(source.getName())) {
                contact.getFlightPlan().releaseControl();
                initDataDisplay(contact);
            }
        }
    }

}
