/**
 * Copyright (C) 2012-2015 Wolfram Wagner
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
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData.FlightType;
import de.knewcleus.openradar.gui.flightplan.FpAtc;
import de.knewcleus.openradar.gui.flightplan.SquawkCode;
import de.knewcleus.openradar.gui.flightplan.lenny64.Lenny64Controller;
import de.knewcleus.openradar.gui.flightplan.lenny64.Lenny64FpExistsChecker;
import de.knewcleus.openradar.gui.setup.AirportData;

public class FlightPlanDialog extends JDialog implements FocusListener {

    private static final long serialVersionUID = 1L;
    private final GuiMasterController master;
    private final RadarContactController controller;
    private volatile GuiRadarContact contact = null;

    private TitledBorder tbContacts;

    private JCheckBox chbFgComSupport;
    private JComboBox<String> cbLanguages;

    private JPanel jPnlOnwerShip;

    private JButton btReset;
    private JTextField tfFlightCode;
    private JComboBox<String> cbFlightPlanTypes;
    private JTextField tfAircraft;
    private JTextField tfSquawk;
    private JButton btNextSquawk;
    private JButton btResetSquawk;

    private JComboBox<String> cbHandoverATCs = new JComboBox<String>();

    private JComboBox<String> cbAssignedRunway;
    private JComboBox<String> cbAssignedRoute;
    private JTextField tfAssignedAltitude;

    private JTextField tfDepAirport;
    private JTextField tfDepTime;
    private JTextField tfDestAirport;
    private JTextField tfArrivalTime;
    private JTextField tfFpAltitude;
    private JTextField tfFpTAS;
    private JButton btCloseFp;
    private JButton btRetrieveFp;
    
    private JTextField tfRoute;
    private JTextField tfAlternAirports;

    private javax.swing.JScrollPane spDetails;
    private javax.swing.JTextPane tpDetails;

    private javax.swing.JScrollPane spPrivateDetails;
    private javax.swing.JTextPane tpPrivateDetails;

    private FpMouseListener fpMouseListener = new FpMouseListener();

    private final Lenny64Controller lenny64Controller;
    
    private final String FPCLOSE_BUTTON_DEFAULT = "Close FP";
    private final String FPCLOSE_BUTTON_ASK = "Really Close?";
    
    public FlightPlanDialog(GuiMasterController master, RadarContactController controller) {
        this.master = master;
        this.controller = controller;
        initComponents();
        lenny64Controller = new Lenny64Controller(master, this, master.getAirportData());
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        //setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
//        this.addWindowListener(new DialogCloseListener());

        // Determine what the default GraphicsDevice can support.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        boolean isUniformTranslucencySupported = gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT);
        if (isUniformTranslucencySupported) {
            this.setOpacity(0.92f);
        }
        
        List<Image> icons = new ArrayList<Image>();
        File iconDir = new File("res/icons");
        if(iconDir.exists()) {
            File[] files = iconDir.listFiles();
            for(File f : files) {
                if(f.getName().matches("OpenRadar.*\\.ico") || f.getName().matches("OpenRadar.*\\.png")
                  || f.getName().matches("OpenRadar.*\\.gif") || f.getName().matches("OpenRadar.*\\.jpg")) {
                    icons.add(new ImageIcon(f.getAbsolutePath()).getImage());
                }
            }
            if(!icons.isEmpty()) {
                setIconImages(icons);
            }
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
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
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


        btReset = new JButton("Reset Flightplan");
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
       // master.setDetailsArea(tpDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPnlComment.add(spDetails, gridBagConstraints);

        // private comment
        
        JPanel jPnlPrivateComment = new JPanel();
        jPnlPrivateComment.setOpaque(false);
        jPnlPrivateComment.setLayout(new GridBagLayout());
        jPnlPrivateComment.setBorder(new TitledBorder("Private notes"));
        jPnlPrivateComment.setPreferredSize(new Dimension(100, 70));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(jPnlPrivateComment, gridBagConstraints);

        spPrivateDetails = new javax.swing.JScrollPane();
        spPrivateDetails.setMinimumSize(new Dimension(100, 60));
        spPrivateDetails.setPreferredSize(new Dimension(120, 60));
        tpPrivateDetails = new javax.swing.JTextPane();

        tpPrivateDetails.setToolTipText("Private Notes: RETURN save, STRG+RETURN newline");
        tpPrivateDetails.addKeyListener(new DetailsKeyListener());
        tpPrivateDetails.setOpaque(true);
        tpPrivateDetails.setMinimumSize(new Dimension(100, 60));
        tpPrivateDetails.setPreferredSize(new Dimension(100, 60));
        spPrivateDetails.setViewportView(tpPrivateDetails);
        master.setDetailsArea(tpPrivateDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPnlPrivateComment.add(spPrivateDetails, gridBagConstraints);
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
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
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
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
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

        JLabel lbModel = new JLabel("Squawk:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPnlLine2.add(lbModel, gridBagConstraints);

        tfSquawk = new JTextField(5);
        tfSquawk.setToolTipText("Assigned squawk code");
        tfSquawk.setEditable(true);
        tfSquawk.setEnabled(true);
        tfSquawk.addKeyListener(new SquawkFieldKeyListener());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPnlLine2.add(tfSquawk, gridBagConstraints);

        btNextSquawk = new JButton("next");
        btNextSquawk.setToolTipText("Assign next free Squawk, depending on flight mode below");
        btNextSquawk.setName("NEXT_SQUAWK");
        btNextSquawk.addMouseListener(fpMouseListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPnlLine2.add(btNextSquawk, gridBagConstraints);

        btResetSquawk = new JButton("del");
        btResetSquawk.setToolTipText("Reset Squawk");
        btResetSquawk.setName("RESET_SQUAWK");
        btResetSquawk.addMouseListener(fpMouseListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine2.add(btResetSquawk, gridBagConstraints);
        
        
        // Contact Line 3

        JPanel jPnlLine3 = new JPanel();
        jPnlLine3.setOpaque(false);
        jPnlLine3.setLayout(new GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
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
        cbAssignedRunway.setPreferredSize(new Dimension(80, (int) dim.getHeight()));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine3.add(cbAssignedRunway, gridBagConstraints);

        JLabel lbAssRoute = new JLabel(" SID/STAR:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine3.add(lbAssRoute, gridBagConstraints);

        cbAssignedRoute = new JComboBox<String>();
        cbAssignedRoute.setEditable(true);
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
//        gridBagConstraints.weightx = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine1.add(cbFlightPlanTypes, gridBagConstraints);

        btRetrieveFp = new JButton("Retrieve FP");
        btRetrieveFp.setToolTipText("Download FPs from http://flightgear-atc.alwaysdata.net/");
        btRetrieveFp.setName("RETRIEVE_FP");
        btRetrieveFp.addMouseListener(fpMouseListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine1.add(btRetrieveFp, gridBagConstraints);

        btCloseFp = new JButton(FPCLOSE_BUTTON_DEFAULT);
        btCloseFp.setToolTipText("Closes the current FP and resets it");
        btCloseFp.setName("CLOSE_FP");
        btCloseFp.addMouseListener(fpMouseListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine1.add(btCloseFp, gridBagConstraints);
        
        // line 2

        JPanel jPnlLine2 = new JPanel();
        jPnlLine2.setOpaque(false);
        jPnlLine2.setLayout(new GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        jPnlFlight.add(jPnlLine2, gridBagConstraints);

        JLabel lbFrom = new JLabel("Departure:");
        lbFrom.setName("START_HERE");
        lbFrom.setToolTipText("Click to initialize flightplan as originating from "+master.getAirportData().getAirportCode());
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

        JLabel lbTo = new JLabel("Destination:");
        lbTo.setName("LAND_HERE");
        lbTo.addMouseListener(fpMouseListener);
        lbTo.setToolTipText("Click to file flightplan to go to "+master.getAirportData().getAirportCode());
        lbTo.setForeground(Color.blue);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPnlLine2.add(lbTo, gridBagConstraints);

        tfDestAirport = new JTextField(4);
        tfDestAirport.setToolTipText("Destination airport code");
        tfDestAirport.addFocusListener(this);
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
        tfArrivalTime.setEditable(true);
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
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        jPnlFlight.add(jPnlLine3, gridBagConstraints);

        JLabel lbVia = new JLabel("Waypoints:");
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
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 0);
        jPnlFlight.add(jPnlLine4, gridBagConstraints);

        JLabel lbCruisingAlt = new JLabel("Crsg.Alt.:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine4.add(lbCruisingAlt, gridBagConstraints);

        tfFpAltitude = new JTextField(6);
        tfFpAltitude.setToolTipText("Either the alt in ft or as flight level (FLxxx)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine4.add(tfFpAltitude, gridBagConstraints);

        JLabel lbCruisingSpeed = new JLabel("Crsg.TAS.:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine4.add(lbCruisingSpeed, gridBagConstraints);

        tfFpTAS = new JTextField(4);
        tfFpTAS.setToolTipText("Planned TAS in KN with respect to wind");
        tfFpTAS.addFocusListener(this);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlLine4.add(tfFpTAS, gridBagConstraints);

        JLabel lbAltAirports = new JLabel("Alt. Airports:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPnlLine4.add(lbAltAirports, gridBagConstraints);

        tfAlternAirports = new JTextField(9);
        tfAlternAirports.setToolTipText("Comma separated list of alternative airports.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
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

        setData(contact);

        Dimension innerSize = getPreferredSize();
        setSize(new Dimension((int) innerSize.getWidth() + 8, (int) innerSize.getHeight() + 8));
        Rectangle maxBounds = AirportData.MAX_WINDOW_SIZE;

        Point2D p;
        if(e!=null) {
            p = e.getLocationOnScreen();// ((JComponent) e.getSource()).getLocationOnScreen();
            p = new Point2D.Double(p.getX() - this.getWidth()/2, p.getY());
        } else {
            double x = maxBounds.getCenterX()-innerSize.getWidth()/2;
            double y = maxBounds.getCenterY()-innerSize.getHeight()/2;
            
            p = new Point2D.Double(x, y);
        }
        

        int lowerDistanceToScreenBorder = 50;
        if (p.getY() + getHeight() > maxBounds.getHeight() - lowerDistanceToScreenBorder) {
            p = new Point2D.Double(p.getX(), maxBounds.getHeight() - getHeight() - lowerDistanceToScreenBorder);
        }
        setLocation(new Point((int) p.getX(), (int) p.getY()));
        setVisible(true);
        tpDetails.requestFocus();
    }

    public void setData(GuiRadarContact contact) {
        
        btRetrieveFp.setForeground(Color.black);
        if(master.getAirportData().isLenny64Enabled()) {
            (new Thread(new Lenny64FpExistsChecker(master, contact, this,this.lenny64Controller.getLenny64Connector()),"OpenRadar - Lenny64 Flightplan exists checker")).start();
        }

        tbContacts.setTitle("<html><body>Contact <b>"+contact.getCallSign()+"</b></body></html>");
        chbFgComSupport.setSelected(contact.hasFgComSupport());
        cbLanguages.setSelectedItem(contact.getAtcLanguage());


        FlightPlanData fpd = contact.getFlightPlan();

        String aircraftCode = fpd.getAircraft();
        tfAircraft.setText(aircraftCode==null|| aircraftCode.isEmpty()?contact.getAircraftCode():aircraftCode);
        tfAircraft.setToolTipText("Model: "+contact.getModel());
        tfSquawk.setText(contact.getAssignedSquawk()!=null?""+contact.getAssignedSquawk():"");

        tfFlightCode.setText(fpd.getFlightCode());
        cbFlightPlanTypes.setSelectedItem(fpd.getType());

        tfDepAirport.setText(fpd.getDepartureAirport());
        tfDepTime.setText(fpd.getDeparture());
        tfDestAirport.setText(fpd.getDestinationAirport());
        tfArrivalTime.setText(controller.getEstimatedArrivalTime(contact));
        tfRoute.setText(fpd.getRoute());
        tfAlternAirports.setText(fpd.getAlternativeDestinationAirports());
        tfFpAltitude.setText(fpd.getCruisingAltitude());
        tfFpTAS.setText(fpd.getTrueAirspeed());
        
        cbAssignedRunway.setModel(master.getAirportData().getRunwayModel(true));
        cbAssignedRunway.getEditor().setItem(fpd.getAssignedRunway());
        cbAssignedRoute.setModel(master.getAirportData().getNavaidDB().getRoutesCbModel(master,true));
        cbAssignedRoute.getEditor().setItem(fpd.getAssignedRoute());

        tfAssignedAltitude.setText(fpd.getAssignedAltitude());
        //cbFlightPlanStatus.getEditor().setItem(fpd.getFpStatus());

        cbHandoverATCs.setModel(master.getRadarContactManager().getOtherATCsCbModel());

        tpDetails.setText(fpd.getRemarks());
        tpPrivateDetails.setText(contact.getAtcComment());

        initOwnership(fpd);
        
        setLennyButtonText("Retrieve FP");
        btCloseFp.setText(FPCLOSE_BUTTON_DEFAULT);

        setFpReadable(fpd.isUncontrolled() || fpd.isOwnedByMe());
        btCloseFp.setEnabled(fpd.isOwnedByMe() && fpd.getFlightPlanId()!=null && !fpd.getFlightPlanId().isEmpty());
    }
    /** fills the panel in fron of the reset flightplan button */
    private void initOwnership(FlightPlanData fpd) {
        jPnlOnwerShip.removeAll();

        if(fpd.isOwnedByMe()) {
            // my contact
            
            JLabel lbControlledBy = new JLabel("Controlled by me.");
            lbControlledBy.setFont(lbControlledBy.getFont().deriveFont(Font.ITALIC).deriveFont(Font.BOLD));
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

            cbHandoverATCs.setEditable(false);
            Dimension dim = cbAssignedRunway.getPreferredSize();
            cbHandoverATCs.setPreferredSize(new Dimension(100, (int) dim.getHeight()));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
            jPnlOnwerShip.add(cbHandoverATCs, gridBagConstraints);

            JButton btReleaseControl = new JButton("UnControl");
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

        } else if(fpd.isOfferedToMe()) {
            // offered to me
            JLabel lbControlledBy = new JLabel("Offered to my by: " + fpd.getOwner());
            lbControlledBy.setFont(lbControlledBy.getFont().deriveFont(Font.ITALIC).deriveFont(Font.BOLD));
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
            lbControlledBy.setFont(lbControlledBy.getFont().deriveFont(Font.ITALIC).deriveFont(Font.BOLD));
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
            lbControlledBy.setFont(lbControlledBy.getFont().deriveFont(Font.ITALIC).deriveFont(Font.BOLD));
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

    private void setFpReadable(boolean b) {
        
        btReset.setEnabled(b);
        tfFlightCode.setEditable(b);
        btResetSquawk.setEnabled(b);
        cbFlightPlanTypes.setEnabled(b);
        tfDepAirport.setEditable(b);
        tfDepTime.setEditable(b);
        tfDestAirport.setEditable(b);
        tfArrivalTime.setEditable(b);
        tfRoute.setEditable(b);
        tfAlternAirports.setEditable(b);
        tfFpAltitude.setEditable(b);
        tfFpTAS.setEditable(b);
        cbAssignedRunway.setEnabled(b);
        cbAssignedRoute.setEnabled(b);
        tfAssignedAltitude.setEditable(b);
        // cbFlightPlanStatus.setSelectedItem(fpd.getFpStatus());
        tpDetails.setEditable(b);
        
        btRetrieveFp.setEnabled(b);
    }

//    private class DialogCloseListener extends WindowAdapter {
//        
//        @Override
//        public void windowOpened(WindowEvent e) {
//            
//        }
//        
//        @Override
//        public void windowClosed(WindowEvent e) {
////            closeDialog();
//        }
//
//        @Override
//        public void windowDeactivated(WindowEvent e) {
////            if(!lenny64Controller.isDialogOpen()) {
////                closeDialog();
////            }
//        }
//
//        @Override
//        public void windowLostFocus(WindowEvent e) {
////            if(!lenny64Controller.isDialogOpen()) {
////                closeDialog();
////            }
//        }
//
//    }

    public void closeDialog(boolean save) {
        if (isVisible()) {
            if(lenny64Controller.isDialogOpen()) {
                getLenny64Controller().closeFpSelectionDialog();
                toFront();
            } else {
                if(save) {
                    saveData();
                }
                
                setVisible(false);
            }
        }
    }

    public void saveData() {

        FlightPlanData fpd = contact.getFlightPlan();

        contact.setFgComSupport(chbFgComSupport.isSelected());
        contact.setAtcLanguage(controller.getAutoAtcLanguages(cbLanguages.getSelectedIndex()));


        fpd.setAircraft(tfAircraft.getText());

        fpd.setFlightCode(tfFlightCode.getText());
        fpd.setType((String)cbFlightPlanTypes.getSelectedItem());

        fpd.setDepartureAirport(tfDepAirport.getText());
        fpd.setDeparture(tfDepTime.getText());
        fpd.setDestinationAirport(tfDestAirport.getText());
        fpd.setRoute(tfRoute.getText());
        fpd.setAlternativeDestinationAirports(tfAlternAirports.getText());
        fpd.setCruisingAltitude(tfFpAltitude.getText());
        fpd.setTrueAirspeed(tfFpTAS.getText());
        // estimatedFlightTime+
        // estimatedFuelTime
        
        fpd.setAssignedRunway((String)cbAssignedRunway.getEditor().getItem());//.getSelectedItem());
        fpd.setAssignedRoute((String)cbAssignedRoute.getEditor().getItem());//getSelectedItem());
        fpd.setAssignedAltitude(tfAssignedAltitude.getText());

        if(contact.getFlightPlan().isOwnedByMe()) {
            String handover = (String)cbHandoverATCs.getSelectedItem();
            fpd.setHandover(handover);
            if(handover!=null && !handover.isEmpty()) {
                FpAtc handoverAtc = master.getRadarContactManager().getAtcFor(handover);
                if(handoverAtc!=null) {
                    master.getFlightPlanExchangeManager().sendHandoverMessage(contact, handoverAtc);
                }
            }
        }
        
        String sqCode = tfSquawk.getText();
        if(SquawkCode.checkValue(sqCode)) {
            contact.setAssignedSquawk(Integer.parseInt(sqCode));
        }
        
        // cbFlightPlanStatus.setSelectedItem(fpd.getFpStatus());
        fpd.setRemarks(tpDetails.getText().trim());
        contact.setAtcComment(tpPrivateDetails.getText().trim());

        contact.getFlightPlan().setReadyForTransmission();
        master.getFlightPlanExchangeManager().triggerTransmission();
    }

    private class DetailsKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            JTextPane ta = (JTextPane) e.getSource();
            String currentText = ta.getText();
//            if(ta.equals(tpPrivateDetails)) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER && !e.isControlDown()) {
                    int carretPos = ta.getCaretPosition() - 1;
                    currentText = new StringBuilder(currentText).deleteCharAt(carretPos).toString();
                    // master.getRadarContactManager().setAtcComment(currentText);
                    saveData();
                    ta.setText(currentText); // remove newline
                    ta.setCaretPosition(carretPos);
                    e.consume();
    
                    closeDialog(true);
                }
                if (e.getKeyChar() == KeyEvent.VK_ENTER && e.isControlDown()) {
                    int carretPos = ta.getCaretPosition();
                    currentText = new StringBuilder(currentText).insert(carretPos, "\n").toString();
                    //master.getRadarContactManager().setAtcComment(currentText); // save and continue
                    saveData();
                    ta.setText(currentText);
                    ta.setCaretPosition(carretPos + 1);
                    ta.requestFocus();
                }
//            }
//            if(ta.equals(tpPrivateDetails)) {
//                if (e.getKeyChar() == KeyEvent.VK_ENTER && !e.isControlDown()) {
//                    int carretPos = ta.getCaretPosition() - 1;
//                    currentText = new StringBuilder(currentText).deleteCharAt(carretPos).toString();
//                    master.getRadarContactManager().setAtcComment(currentText);
//                    ta.setText(currentText); // remove newline
//                    ta.setCaretPosition(carretPos);
//                    e.consume();
//    
//                    closeDialog();
//                }
//                if (e.getKeyChar() == KeyEvent.VK_ENTER && e.isControlDown()) {
//                    int carretPos = ta.getCaretPosition();
//                    currentText = new StringBuilder(currentText).insert(carretPos, "\n").toString();
//                    master.getRadarContactManager().setAtcComment(currentText); // save and continue
//                    ta.setText(currentText);
//                    ta.setCaretPosition(carretPos + 1);
//                    ta.requestFocus();
//                }
//            }
        }
    }

    private class FpMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            JComponent source = (JComponent)e.getSource();

            if("RESET".equals(source.getName())) {
                contact.getFlightPlan().reset();
                setData(contact);
            } else if("START_HERE".equals(source.getName())) {
                contact.getFlightPlan().startFromHere(master.getAirportData());
                setData(contact);
            } else if("LAND_HERE".equals(source.getName())) {
                contact.getFlightPlan().landHere(master.getAirportData());
                setData(contact);
            } else if("TAKE_OVER".equals(source.getName())) {
                master.getRadarContactManager().takeUnderControl(contact);
                setData(contact);
            } else if("CONTROL".equals(source.getName())) {
                master.getRadarContactManager().takeUnderControl(contact);
                setData(contact);
            } else if("RELEASE_CONTROL".equals(source.getName())) {
                master.getRadarContactManager().releaseFromControl(contact);
                setData(contact);
            } else if("RETRIEVE_FP".equals(source.getName()) && e.getClickCount()==1) {
                lenny64Controller.downloadFlightPlansFor(e, contact.getCallSign());
            } else if("CLOSE_FP".equals(source.getName()) && e.getClickCount()==1) {
                if(btCloseFp.getText().equals(FPCLOSE_BUTTON_DEFAULT)) {
                    // change text to are you sure?
                    btCloseFp.setText(FPCLOSE_BUTTON_ASK); 
                } else {
                    btCloseFp.setText(FPCLOSE_BUTTON_DEFAULT);
                    // really close it
                    lenny64Controller.closeFlightPlan(contact);
                    setData(contact);
                    saveData();
                }
            } else if("NEXT_SQUAWK".equals(source.getName()) && e.getClickCount()==1) {
                saveData();
                master.getRadarContactManager().assignSquawkCode(FlightType.valueOf(contact.getFlightPlan().getType()));
                setData(contact);
            } else if("RESET_SQUAWK".equals(source.getName()) && e.getClickCount()==1) {
                contact.getFlightPlan().setSquawk(null);
                setData(contact);
            }
        }
    }

    // focus listener
    
    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
        if(e.getSource().equals(tfDestAirport) || e.getSource().equals(tfFpTAS)) {
            if(!tfDestAirport.getText().isEmpty() &&
               !tfDestAirport.getText().equals(master.getAirportData().getAirportCode())) {
                // destination airport is not own airport
                cbAssignedRunway.setSelectedItem("");
                cbAssignedRoute.setSelectedItem("");
            }
            //saveData();
            tfArrivalTime.setText(controller.getEstimatedArrivalTime(contact));
        }
        
    }

    public synchronized boolean shows(GuiRadarContact c) {
        return c.equals(contact);
    }

    public synchronized Lenny64Controller getLenny64Controller() {
        return lenny64Controller;
    }

    public void setLennyButtonText(String text) {
        btRetrieveFp.setText(text);
    }
    
    private class SquawkFieldKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            
            if(((JTextField)e.getSource()).getText().length()>=4 || !(""+e.getKeyChar()).matches("[0-7]")) {
                e.consume();
                return;
            } 
            
            String newText = tfSquawk.getText();
            tfSquawk.setForeground(newText.matches("[0-7]{0,4}")?Color.black:Color.red);
        
            String key=""+e.getKeyChar();
            newText = newText+key;
            if(newText.length()==4 && !"".equals(key)) {
                if(!SquawkCode.checkValue(newText)) {
                    tfSquawk.setForeground(Color.red);
                    e.consume();
                } else {
                    tfSquawk.setForeground(Color.black);
                }
            }                
        }
    }

    public void setFlightplansAvailable(boolean fpsExist) {
        if(fpsExist) {
            btRetrieveFp.setForeground(Color.blue);
        } else {
            btRetrieveFp.setForeground(Color.GRAY);
        }
        btRetrieveFp.repaint();
    }
}
