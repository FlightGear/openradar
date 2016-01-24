/**
 * Copyright (C) 2012-2016 Wolfram Wagner
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
package de.knewcleus.openradar.gui.setup;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.setup.AirportData.FgComMode;
import de.knewcleus.openradar.rpvd.contact.ADatablockLayout;
import de.knewcleus.openradar.view.map.GeometryToShapeProjector;

/**
 * The setup dialog...
 *
 * @author Wolfram Wagner
 */
public class SetupDialog extends JFrame {

    private static final long serialVersionUID = -3703867188892313521L;
    private SetupController setupManager;

    private JTextField tfSearchBox;
    private JList<SectorBean> liSearchResults;
    private JComboBox<String> cbFgComMode;
    private JLabel lbCallSign;
    private JTextField tfCallSign;

    private JTextField tfFgComPath;
    private JTextField tfFgComExec;
    private JTextField tfFgComServer;
    private JTextField tfFgComHost;
    private JTextField tfFgComPorts;
    private JTextField tfMpServer;
    private JTextField tfMpPort;
    private JTextField tfMpLocalPort;
    private JTextField tfChatPrefix;
    private JTextField tfMetarUrl;
    private JLabel lbFgComMode;
    private JLabel lbfgComPath;
    private JLabel lbfgComExec;
    private JLabel lbFgComServer;
    private JLabel lbFgComHost;
    private JLabel lbFgComPorts;
    private JPanel jPnlMultiplayer;
    private JLabel lbMpServer;
    private JLabel lbMpPort;
    private JLabel lbMpLocalPort;
    private JCheckBox cbEnableFpExchange;
    private JLabel lbFpServer;
    private JTextField tfFpServer;
    private JLabel lbFpServerUser;
    private JTextField tfFpServerUser;
    private JLabel lbFpServerPassword;
    private JPasswordField tfFpServerPassword;
    private JLabel lbChatPrefix;
    private JLabel lbMetarUrl;
    private JLabel lbMessage;
    private JButton btStart;
    private JButton btCheckSettings;
    private JButton btCheckSettings2;
    private JButton btCheckSettings3;
    private JPanel jPnlSettings;
    private JPanel jPnlFlightPlans;
    private JCheckBox cbEnableFpDownload;
    private JLabel lbLennysServer;
    private JTextField tfLennysFpServer;
    private JButton btCreateSector;
    private JButton btDeleteSector;
    private JComboBox<String> cbStatusMessages;
    private JProgressBar jProgressBar;
    private StatusMessageComboboxModel cbStatusModel;

    private JComboBox<ADatablockLayout> cbDataboxLayout;
    private JCheckBox cbNiceShapes;

    private JCheckBox cbEnableAltRadioText;
    private JCheckBox cbEnableChatAliases;
    private JCheckBox cbLandmass;
    private JCheckBox cbUrban;
    private JCheckBox cbLake;
    private JCheckBox cbStream;
//    private JCheckBox cbTarmac; // if needed again, uncomment all lines containing cbTarmac!
    private JCheckBox cbGroundnet;

    private JCheckBox cbFgfsCamera1Enabled;
    private JTextField tfFgfsCamera1Host;
    private JTextField tfFgfsCamera1Port;
    private JCheckBox cbFgfsLocalMPPacketForward1;
    private JTextField tfFgfsLocalMPPacketPort1;

    private JCheckBox cbFgfsCamera2Enabled;
    private JCheckBox cbFgfsCamera2SlavedTo1;
    private JTextField tfFgfsCamera2Host;
    private JTextField tfFgfsCamera2Port;
    private JCheckBox cbFgfsLocalMPPacketForward2;
    private JTextField tfFgfsLocalMPPacketPort2;

    private FgComMode fgComMode = FgComMode.Internal;
    private String[] modeModel = new String[] { "Auto: Use the internal fgcom", "Internal: OR starts and controls a fgcom client",
            "External: Control external fgcom client instance", "OFF: No FgCom support" };

    private List<Image> icons = new ArrayList<Image>();

    private final static Logger log = LogManager.getLogger(SetupDialog.class);

    public SetupDialog(SetupController setupManager) {
        this.setupManager = setupManager;

        File iconDir = new File("res/icons");
        if (iconDir.exists()) {
            File[] files = iconDir.listFiles();
            for (File f : files) {
                if (f.getName().matches("OpenRadar.*\\.ico") || f.getName().matches("OpenRadar.*\\.png") || f.getName().matches("OpenRadar.*\\.gif")
                        || f.getName().matches("OpenRadar.*\\.jpg")) {
                    icons.add(new ImageIcon(f.getAbsolutePath()).getImage());
                }
            }
            if (!icons.isEmpty()) {
                setIconImages(icons);
            }
        }

        initComponents();
        invalidate();
        this.loadProperties();
    }

    public synchronized List<Image> getIcons() {
        return icons;
    }

    private void initComponents() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("OpenRadar - Welcome!");
        Rectangle maxBounds = AirportData.MAX_WINDOW_SIZE;

        this.setLocation((int) maxBounds.getWidth() / 2 - 300, 100);// (int) maxBounds.getHeight() / 2 - 200);
        // this.setSize(400,600);

        JPanel jPnlContentPane = new JPanel();
        jPnlContentPane.setLayout(new GridBagLayout());
        setContentPane(jPnlContentPane);

        JTabbedPane jtpMain = new JTabbedPane();
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlContentPane.add(jtpMain, gridBagConstraints);

        cbStatusMessages = new JComboBox<String>();
        cbStatusModel = new StatusMessageComboboxModel();
        cbStatusMessages.setEditable(false);
        // cbStatusMessages.setEnabled(false);
        cbStatusMessages.setFont(cbStatusMessages.getFont().deriveFont(7));
        cbStatusMessages.setModel(cbStatusModel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlContentPane.add(cbStatusMessages, gridBagConstraints);

        jProgressBar = new JProgressBar();
        jProgressBar.setMinimum(0);
        jProgressBar.setMaximum(100);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        jPnlContentPane.add(jProgressBar, gridBagConstraints);

        // TAB SELECT/CREATE AIRPORT

        JPanel jPnlSelectAirport = new JPanel();
        jtpMain.add("Select Airport", jPnlSelectAirport);
        jPnlSelectAirport.setLayout(new GridBagLayout());

        JLabel lbWelcome = new JLabel();
        lbWelcome.setText("Welcome to OpenRadar!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 0, 2);
        jPnlSelectAirport.add(lbWelcome, gridBagConstraints);

        JLabel lbPleaseSelect = new JLabel();
        lbPleaseSelect.setText("Please select your airport!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlSelectAirport.add(lbPleaseSelect, gridBagConstraints);

        tfSearchBox = new JTextField();
        tfSearchBox.setName("SearchBox");
        tfSearchBox.setToolTipText("Please enter code or name of airport! (Wildcard *)");
        tfSearchBox.addActionListener(setupManager.getActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.insets = new java.awt.Insets(12, 8, 0, 4);
        jPnlSelectAirport.add(tfSearchBox, gridBagConstraints);

        JButton btSearch = new JButton();
        btSearch.setText("search");
        btSearch.setName("SearchButton");
        btSearch.addActionListener(setupManager.getActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 0, 0);
        jPnlSelectAirport.add(btSearch, gridBagConstraints);

        JButton btShowExisting = new JButton();
        btShowExisting.setText("show existing");
        btShowExisting.setToolTipText("Reset search results to existing sectors");
        btShowExisting.setName("ShowExistingButton");
        btShowExisting.addActionListener(setupManager.getActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 0, 8);
        jPnlSelectAirport.add(btShowExisting, gridBagConstraints);

        JScrollPane jsPane = new JScrollPane();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 0, 8);
        jPnlSelectAirport.add(jsPane, gridBagConstraints);

        liSearchResults = new JList<SectorBean>();
        liSearchResults.setModel(setupManager.getSearchResultsModel());
        liSearchResults.setName("SearchResultList");
        liSearchResults.setVisibleRowCount(10);
        liSearchResults.setCellRenderer(new SectorBeanRenderer());
        liSearchResults.addListSelectionListener(setupManager.getSectorListSelectionListener());
        liSearchResults.addMouseListener(setupManager.getSectorListMouseListener());
        jsPane.getViewport().add(liSearchResults);

        lbMessage = new JLabel();
        lbMessage.setForeground(Palette.RED);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlSelectAirport.add(lbMessage, gridBagConstraints);

        JPanel jPnlButtons = new JPanel();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPnlSelectAirport.add(jPnlButtons, gridBagConstraints);

        jPnlButtons.setLayout(new GridBagLayout());

        btCreateSector = new JButton();
        btCreateSector.setText("Download Scenery");
        btCreateSector.setName("DownloadButton");
        btCreateSector.setEnabled(false);
        btCreateSector.addActionListener(setupManager.getActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 2);
        jPnlButtons.add(btCreateSector, gridBagConstraints);

        btDeleteSector = new JButton();
        btDeleteSector.setText("Delete Airport");
        btDeleteSector.setName("DeleteAirport");
        btDeleteSector.setEnabled(false);
        btDeleteSector.addActionListener(setupManager.getActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 2);
        jPnlButtons.add(btDeleteSector, gridBagConstraints);

        lbCallSign = new JLabel();
        lbCallSign.setText("CallSign:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlButtons.add(lbCallSign, gridBagConstraints);

        tfCallSign = new JTextField(8);
        tfCallSign.setName("callsign");
        tfCallSign.setText("");
        tfCallSign.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 2);
        jPnlButtons.add(tfCallSign, gridBagConstraints);

        btStart = new JButton();
        btStart.setText("Start");
        btStart.setName("StartButton");
        btStart.setEnabled(false);
        btStart.addActionListener(setupManager.getActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 4);
        jPnlButtons.add(btStart, gridBagConstraints);

        // TAB SETTINGS

        jPnlSettings = new JPanel();
        jtpMain.add("Settings", jPnlSettings);
        jPnlSettings.setLayout(new GridBagLayout());

        // FGCom

        JPanel jPnlFgCom = new JPanel();
        jPnlFgCom.setLayout(new GridBagLayout());
        jPnlFgCom.setBorder(new TitledBorder("FGCom"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlSettings.add(jPnlFgCom, gridBagConstraints);

        // mode select box
        lbFgComMode = new JLabel();
        lbFgComMode.setText("FGComMode");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(lbFgComMode, gridBagConstraints);

        cbFgComMode = new JComboBox<String>(modeModel);
        cbFgComMode.setEditable(false);
        cbFgComMode.addActionListener(new FgComModeActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(cbFgComMode, gridBagConstraints);

        // executable

        lbfgComPath = new JLabel();
        lbfgComPath.setText("Path:");
        lbfgComPath.setToolTipText("Path of your FgCom installation");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(lbfgComPath, gridBagConstraints);

        tfFgComPath = new JTextField();
        tfFgComPath.setName("FgComExec");
        tfFgComPath.setText("");
        tfFgComPath.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(tfFgComPath, gridBagConstraints);

        lbfgComExec = new JLabel();
        lbfgComExec.setText("Executable:");
        lbfgComExec.setToolTipText("Executable of FGCom if it should be started loaclly in background");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(lbfgComExec, gridBagConstraints);

        tfFgComExec = new JTextField();
        tfFgComExec.setName("FgComExec");
        tfFgComExec.setText("");
        tfFgComExec.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(tfFgComExec, gridBagConstraints);

        // JButton btBrowseFgComServer = new JButton();
        // btBrowseFgComServer.setText("Search...");
        // btBrowseFgComServer.setName("SearchFgCom");
        // btBrowseFgComServer.addActionListener(setupManager.getActionListener());
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 2;
        // gridBagConstraints.gridy = 1;
        // gridBagConstraints.anchor = GridBagConstraints.WEST;
        // gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        // jPnlFgCom.add(btBrowseFgComServer, gridBagConstraints);

        // server

        lbFgComServer = new JLabel();
        lbFgComServer.setText("FgCom Server");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        jPnlFgCom.add(lbFgComServer, gridBagConstraints);

        tfFgComServer = new JTextField();
        tfFgComServer.setName("FgComServer");
        tfFgComServer.setToolTipText("Please enter the server address!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(tfFgComServer, gridBagConstraints);

        // host

        lbFgComHost = new JLabel();
        lbFgComHost.setText("Client host");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(lbFgComHost, gridBagConstraints);

        tfFgComHost = new JTextField();
        tfFgComHost.setName("FgComHost");
        tfFgComHost.setToolTipText("The machine that runs fgcom client, usually 'localhost'");
        tfFgComHost.setText("localhost");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(tfFgComHost, gridBagConstraints);

        // ports

        lbFgComPorts = new JLabel();
        lbFgComPorts.setText("FgCom Ports");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgCom.add(lbFgComPorts, gridBagConstraints);

        tfFgComPorts = new JTextField();
        tfFgComPorts.setName("FgComPorts");
        tfFgComPorts.setToolTipText("Comma separated list of FgCom Ports, two are enough, four maximum, e.g. '16661,16662'");
        tfFgComPorts.setText("16661");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 2);
        jPnlFgCom.add(tfFgComPorts, gridBagConstraints);

        // alternative radios

        JPanel jPnlAltRadio = new JPanel();
        jPnlAltRadio.setLayout(new GridBagLayout());
        jPnlAltRadio.setBorder(new TitledBorder("Alternative radios (like Mumble)"));
        jPnlAltRadio.setToolTipText("Enable a text field to send radio information to your contacts");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 0, 2);
        jPnlSettings.add(jPnlAltRadio, gridBagConstraints);

        cbEnableAltRadioText = new JCheckBox();
        cbEnableAltRadioText.setText("Enable alternative radio text (for mumble etc.)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlAltRadio.add(cbEnableAltRadioText, gridBagConstraints);

        // Multiplayer

        jPnlMultiplayer = new JPanel();
        jPnlMultiplayer.setLayout(new GridBagLayout());
        jPnlMultiplayer.setBorder(new TitledBorder("FG Multiplayer"));
        jPnlMultiplayer.setToolTipText("These settings should be correct already!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 0, 2);
        jPnlSettings.add(jPnlMultiplayer, gridBagConstraints);

        lbMpServer = new JLabel();
        lbMpServer.setText("Server");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMultiplayer.add(lbMpServer, gridBagConstraints);

        tfMpServer = new JTextField();
        tfMpServer.setName("MpServer");
        tfMpServer.setToolTipText("Address of FlightGear multiplayer server");
        tfMpServer.setText("mpserver01.flightgear.com");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMultiplayer.add(tfMpServer, gridBagConstraints);

        lbMpPort = new JLabel();
        lbMpPort.setText("Server Port");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMultiplayer.add(lbMpPort, gridBagConstraints);

        tfMpPort = new JTextField();
        tfMpPort.setName("MpServer");
        tfMpPort.setToolTipText("Port of FlightGear multiplayer server");
        tfMpPort.setText("5000");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMultiplayer.add(tfMpPort, gridBagConstraints);

        lbMpLocalPort = new JLabel();
        lbMpLocalPort.setText("Client port");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMultiplayer.add(lbMpLocalPort, gridBagConstraints);

        tfMpLocalPort = new JTextField();
        tfMpLocalPort.setName("MpPort");
        tfMpLocalPort.setToolTipText("Port of FlightGear multiplayer client on this machine");
        tfMpLocalPort.setText("5001");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMultiplayer.add(tfMpLocalPort, gridBagConstraints);

        // CHAT ALIASES

        JPanel jPnlChat = new JPanel();
        jPnlChat.setLayout(new GridBagLayout());
        jPnlChat.setBorder(new TitledBorder("Multiplayer Chat"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 0, 2);
        jPnlSettings.add(jPnlChat, gridBagConstraints);

        cbEnableChatAliases = new JCheckBox();
        cbEnableChatAliases.setText("Enable chat aliases");
        cbEnableChatAliases.addActionListener(new ChatEnabledActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlChat.add(cbEnableChatAliases, gridBagConstraints);

        lbChatPrefix = new JLabel();
        lbChatPrefix.setText("Prefix of Chat aliases");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlChat.add(lbChatPrefix, gridBagConstraints);

        tfChatPrefix = new JTextField(1);
        tfChatPrefix.setToolTipText("The prefix, for instance '.' to use '.tr 010'");
        tfChatPrefix.setText(".");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 2);
        jPnlChat.add(tfChatPrefix, gridBagConstraints);

        // METAR

        JPanel jPnlMetar = new JPanel();
        jPnlMetar.setLayout(new GridBagLayout());
        jPnlMetar.setBorder(new TitledBorder("METAR"));
        jPnlMetar.setToolTipText("These settings should be correct already!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 0, 2);
        jPnlSettings.add(jPnlMetar, gridBagConstraints);

        lbMetarUrl = new JLabel();
        lbMetarUrl.setText("METAR URL");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMetar.add(lbMetarUrl, gridBagConstraints);

        tfMetarUrl = new JTextField();
        tfMetarUrl.setName("MetarUrl");
        tfMetarUrl.setToolTipText("URL of weather data provider");
        tfMetarUrl.setText("http://weather.noaa.gov/pub/data/observations/metar/stations/");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 2);
        jPnlMetar.add(tfMetarUrl, gridBagConstraints);

        btCheckSettings = new JButton();
        btCheckSettings.setText("Check Settings");
        btCheckSettings.setName("CheckButton");
        btCheckSettings.addActionListener(setupManager.getActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(12, 2, 2, 2);
        jPnlSettings.add(btCheckSettings, gridBagConstraints);

        // TAB Flightplans

        jPnlFlightPlans = new JPanel();
        jtpMain.add("Flightplans", jPnlFlightPlans);
        jPnlFlightPlans.setLayout(new GridBagLayout());

        // Flightplan Server

        JPanel jPnlFlightPlanExchange = new JPanel();
        jPnlFlightPlanExchange.setLayout(new GridBagLayout());
        jPnlFlightPlanExchange.setBorder(new TitledBorder("Flightplan exchange / Handovers (PROTOTYPE!)"));
        jPnlFlightPlanExchange.setToolTipText("These settings should be correct already!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 0, 2);
        jPnlFlightPlans.add(jPnlFlightPlanExchange, gridBagConstraints);

        cbEnableFpExchange = new JCheckBox();
        cbEnableFpExchange.setText("enable flightplan exchange");
        cbEnableFpExchange.addActionListener(new FpServerActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFlightPlanExchange.add(cbEnableFpExchange, gridBagConstraints);

        lbFpServer = new JLabel();
        lbFpServer.setText("FlightPlanServer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFlightPlanExchange.add(lbFpServer, gridBagConstraints);

        tfFpServer = new JTextField();
        tfFpServer.setName("FPServer");
        tfFpServer.setToolTipText("Base URL of the flightplan exchange server.");
        tfFpServer.setText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFlightPlanExchange.add(tfFpServer, gridBagConstraints);

        lbFpServerUser = new JLabel();
        lbFpServerUser.setText("User:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFlightPlanExchange.add(lbFpServerUser, gridBagConstraints);

        tfFpServerUser = new JTextField();
        tfFpServerUser.setToolTipText("Username for Flightplan exchange server.");
        tfFpServerUser.setText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFlightPlanExchange.add(tfFpServerUser, gridBagConstraints);

        lbFpServerPassword = new JLabel();
        lbFpServerPassword.setText("Password");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFlightPlanExchange.add(lbFpServerPassword, gridBagConstraints);

        tfFpServerPassword = new JPasswordField();
        tfFpServerPassword.setToolTipText("Password for Flightplan exchange server.");
        tfFpServerPassword.setText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFlightPlanExchange.add(tfFpServerPassword, gridBagConstraints);

        // Flightplan download

        JPanel jPnlLenny = new JPanel();
        jPnlLenny.setLayout(new GridBagLayout());
        jPnlLenny.setBorder(new TitledBorder("Flightplan Download"));
        jPnlLenny.setToolTipText("These settings should be correct already!");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 0, 2);
        jPnlFlightPlans.add(jPnlLenny, gridBagConstraints);

        cbEnableFpDownload = new JCheckBox();
        cbEnableFpDownload.setText("Enable flightplan downloads");
        cbEnableFpDownload.addActionListener(new FpDownloadActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlLenny.add(cbEnableFpDownload, gridBagConstraints);
        
        lbLennysServer = new JLabel();
        lbLennysServer.setText("FlightPlan Webserver");
        lbLennysServer.setToolTipText("The URL of Lenny's server");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlLenny.add(lbLennysServer, gridBagConstraints);

        tfLennysFpServer = new JTextField();
        tfLennysFpServer.setName("FPServer");
        tfLennysFpServer.setToolTipText("Base URL of the flightplan exchange server.");
        tfLennysFpServer.setText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlLenny.add(tfLennysFpServer, gridBagConstraints);

        btCheckSettings2 = new JButton();
        btCheckSettings2.setText("Check Settings");
        btCheckSettings2.setName("CheckButton");
        btCheckSettings2.addActionListener(setupManager.getActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 2, 2, 2);
        jPnlFlightPlans.add(btCheckSettings2, gridBagConstraints);

        // TAB Layers

        JPanel jPnlTweaks = new JPanel();
        jtpMain.add("Tweaks", jPnlTweaks);
        jPnlTweaks.setLayout(new GridBagLayout());

        JPanel jPnlDataBlockLayout = new JPanel();
        jPnlDataBlockLayout.setLayout(new GridBagLayout());
        jPnlDataBlockLayout.setBorder(new TitledBorder("Radar Data Block Layout"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 0, 2);
        jPnlTweaks.add(jPnlDataBlockLayout, gridBagConstraints);

        cbDataboxLayout = new JComboBox<ADatablockLayout>(new Vector<ADatablockLayout>(setupManager.getDatablockLayoutManager().getLayoutModes()));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        jPnlDataBlockLayout.add(cbDataboxLayout, gridBagConstraints);

        cbNiceShapes = new JCheckBox();
        cbNiceShapes.setText("Enable detailed background shapes (Slower initial loading)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        jPnlTweaks.add(cbNiceShapes, gridBagConstraints);

        JPanel jPnlLayerInput = new JPanel();
        jPnlLayerInput.setLayout(new GridBagLayout());
        jPnlLayerInput.setBorder(new TitledBorder("Visible background layers"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 0, 2);
        jPnlTweaks.add(jPnlLayerInput, gridBagConstraints);

        cbLandmass = new JCheckBox();
        cbLandmass.setText("landmass (coast lines)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlLayerInput.add(cbLandmass, gridBagConstraints);

        cbUrban = new JCheckBox();
        cbUrban.setText("urban areas");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlLayerInput.add(cbUrban, gridBagConstraints);

        cbLake = new JCheckBox();
        cbLake.setText("lakes, water body");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlLayerInput.add(cbLake, gridBagConstraints);

        cbStream = new JCheckBox();
        cbStream.setText("streams (disable it to accelerate everything)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlLayerInput.add(cbStream, gridBagConstraints);

//        cbTarmac = new JCheckBox();
//        cbTarmac.setText("tarmac");
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        gridBagConstraints.gridy = 4;
//        gridBagConstraints.gridwidth = 1;
//        gridBagConstraints.anchor = GridBagConstraints.WEST;
//        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
//        jPnlLayerInput.add(cbTarmac, gridBagConstraints);

        cbGroundnet = new JCheckBox();
        cbGroundnet.setText("ground net / parkings");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlLayerInput.add(cbGroundnet, gridBagConstraints);

        JPanel jpnlSpace = new JPanel();
        jpnlSpace.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 0, 2);
        jPnlTweaks.add(jpnlSpace, gridBagConstraints);

        // TAB Window to airport

        JPanel jPnlFGFSRemote = new JPanel();
        jPnlFGFSRemote.setLayout(new GridBagLayout());
        jtpMain.add("FGFS ORCAM", jPnlFGFSRemote);

        // fgfs cam 1

        JPanel jPnlFgfsCam1 = new JPanel();
        jPnlFgfsCam1.setLayout(new GridBagLayout());
        jPnlFgfsCam1.setBorder(new TitledBorder("FGFS Camera 1"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 0, 2);
        jPnlFGFSRemote.add(jPnlFgfsCam1, gridBagConstraints);

        JTextArea taCam = new JTextArea(
                "OR can use the aircraft ORCAM to give you a view onto the airport. \nIt supports view presets, different view locations and more.\nSee http://wiki.flightgear.org/OpenRadar_FGFS_ORCAM");
        taCam.setEditable(false);
        taCam.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPnlFgfsCam1.add(taCam, gridBagConstraints);

        cbFgfsCamera1Enabled = new JCheckBox();
        cbFgfsCamera1Enabled.setText("Enable FGFS Cam Control");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        jPnlFgfsCam1.add(cbFgfsCamera1Enabled, gridBagConstraints);

        JLabel lbFgfsCamera1Host = new JLabel();
        lbFgfsCamera1Host.setText("FGFS host");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgfsCam1.add(lbFgfsCamera1Host, gridBagConstraints);

        tfFgfsCamera1Host = new JTextField();
        tfFgfsCamera1Host.setName("FGFS host");
        tfFgfsCamera1Host.setToolTipText("The machine that runs the telnet enabled FGFS ('localhost')");
        tfFgfsCamera1Host.setText("localhost");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgfsCam1.add(tfFgfsCamera1Host, gridBagConstraints);

        JLabel lbFgfsCamera1Port = new JLabel();
        lbFgfsCamera1Port.setText("FGFS telnet port (TCP):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgfsCam1.add(lbFgfsCamera1Port, gridBagConstraints);

        tfFgfsCamera1Port = new JTextField(5);
        tfFgfsCamera1Port.setName("FGFSPort");
        tfFgfsCamera1Port.setToolTipText("The port that listens for telnet commands ('5000')");
        tfFgfsCamera1Port.setText("5010");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgfsCam1.add(tfFgfsCamera1Port, gridBagConstraints);

        cbFgfsLocalMPPacketForward1 = new JCheckBox();
        cbFgfsLocalMPPacketForward1.setText("Enable MP Forwarding");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        jPnlFgfsCam1.add(cbFgfsLocalMPPacketForward1, gridBagConstraints);

        JLabel lbFgfsLocalMPPacketPort1 = new JLabel();
        lbFgfsLocalMPPacketPort1.setText("FGFS MP Port (UDP)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgfsCam1.add(lbFgfsLocalMPPacketPort1, gridBagConstraints);

        tfFgfsLocalMPPacketPort1 = new JTextField(5);
        tfFgfsLocalMPPacketPort1.setName("FgComHost");
        tfFgfsLocalMPPacketPort1.setToolTipText("The UDP Port at which FGFS receives UDP MP data usually '5000'");
        tfFgfsLocalMPPacketPort1.setText("5010");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgfsCam1.add(tfFgfsLocalMPPacketPort1, gridBagConstraints);

        // fgfs cam 2

        JPanel jPnlFgfsCam2 = new JPanel();
        jPnlFgfsCam2.setLayout(new GridBagLayout());
        jPnlFgfsCam2.setBorder(new TitledBorder("FGFS Camera 2"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(12, 4, 0, 2);
        jPnlFGFSRemote.add(jPnlFgfsCam2, gridBagConstraints);

        cbFgfsCamera2Enabled = new JCheckBox();
        cbFgfsCamera2Enabled.setText("Enable ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        jPnlFgfsCam2.add(cbFgfsCamera2Enabled, gridBagConstraints);

        cbFgfsCamera2SlavedTo1 = new JCheckBox();
        cbFgfsCamera2SlavedTo1.setText("Slave to Cam1");
        cbFgfsCamera2SlavedTo1.setToolTipText("One control line will control two cams");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        jPnlFgfsCam2.add(cbFgfsCamera2SlavedTo1, gridBagConstraints);

        JLabel lbFgfsCamera2Host = new JLabel();
        lbFgfsCamera2Host.setText("FGFS host");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgfsCam2.add(lbFgfsCamera2Host, gridBagConstraints);

        tfFgfsCamera2Host = new JTextField();
        tfFgfsCamera2Host.setName("FGFS host");
        tfFgfsCamera2Host.setToolTipText("The machine that runs the telnet enabled FGFS ('localhost')");
        tfFgfsCamera2Host.setText("localhost");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgfsCam2.add(tfFgfsCamera2Host, gridBagConstraints);

        JLabel lbFgfsCamera2Port = new JLabel();
        lbFgfsCamera2Port.setText("FGFS telnet port (TCP):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgfsCam2.add(lbFgfsCamera2Port, gridBagConstraints);

        tfFgfsCamera2Port = new JTextField(5);
        tfFgfsCamera2Port.setName("FGFSPort");
        tfFgfsCamera2Port.setToolTipText("The port that listens for telnet commands ('5000')");
        tfFgfsCamera2Port.setText("5020");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgfsCam2.add(tfFgfsCamera2Port, gridBagConstraints);

        cbFgfsLocalMPPacketForward2 = new JCheckBox();
        cbFgfsLocalMPPacketForward2.setText("Enable MP Forwarding");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        jPnlFgfsCam2.add(cbFgfsLocalMPPacketForward2, gridBagConstraints);

        JLabel cbFgfsLocalMPPacketForward2 = new JLabel();
        cbFgfsLocalMPPacketForward2.setText("FGFS MP Port (UDP)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgfsCam2.add(cbFgfsLocalMPPacketForward2, gridBagConstraints);

        tfFgfsLocalMPPacketPort2 = new JTextField(5);
        tfFgfsLocalMPPacketPort2.setName("FgComHost");
        tfFgfsLocalMPPacketPort2.setToolTipText("The UDP Port at which FGFS receives UDP MP data usually '5000'");
        tfFgfsLocalMPPacketPort2.setText("5020");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlFgfsCam2.add(tfFgfsLocalMPPacketPort2, gridBagConstraints);

        // JTextArea taFGFSURL = new JTextArea();
        // taFGFSURL.setText("fgfs --aircraft=OR-Cam --callsign=dummy --airport=LFSB --telnet=,,10,,5000, --multiplay=in,100,,5010 --multiplay=out,100,localhost,5010");
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 0;
        // gridBagConstraints.gridy = 2;
        // gridBagConstraints.gridwidth = 1;
        // gridBagConstraints.weightx = 1;
        // gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        // gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        // jPnlFGFSRemote.add(taFGFSURL, gridBagConstraints);
        //

        btCheckSettings3 = new JButton();
        btCheckSettings3.setText("Check Settings");
        btCheckSettings3.setName("CheckButton");
        btCheckSettings3.addActionListener(setupManager.getActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 2, 2, 2);
        jPnlFGFSRemote.add(btCheckSettings3, gridBagConstraints);

        
        doLayout();
        this.setSize((int) jPnlContentPane.getPreferredSize().getWidth(), (int) jPnlContentPane.getPreferredSize().getHeight() + 30);
        doLayout();
    }

    void preselectAirport(String code, SectorBean autostartAirport) {
        if (autostartAirport != null) {
        	tfSearchBox.setText(autostartAirport.getAirportCode());
            liSearchResults.setSelectedValue(autostartAirport, true);
            btStart.requestFocus();
        } else if (code != null) {
            tfSearchBox.setText(code.trim().toUpperCase());
        }
    }

    public String getSearchTerm() {
        return tfSearchBox.getText();
    }

    public SectorBean getSelectedSector() {
        return liSearchResults.getSelectedValue();
    }

    public boolean readInputs(AirportData data) {
        btCheckSettings.setText("Checking...");
        btCheckSettings.setEnabled(false);
        btCheckSettings.setForeground(Palette.GRAY);

        boolean dataOk = true;

        data.setFgComMode(fgComMode);

        List<Integer> list = null;

        if (fgComMode != FgComMode.Internal) {
            lbfgComPath.setForeground(Palette.BLACK);
        } else {
            // internal
            if (checkPath(tfFgComPath.getText().trim())) {
                lbfgComPath.setForeground(Palette.BLACK);
                data.setFgComPath(tfFgComPath.getText().trim());
            } else {
                lbfgComPath.setForeground(Palette.RED);
                dataOk = false;
            }
        }

        if (fgComMode != FgComMode.Internal) {
            lbfgComExec.setForeground(Palette.BLACK);
        } else {
            // internal
            if (!tfFgComExec.getText().trim().isEmpty() && checkPath(tfFgComPath.getText().trim() + File.separator + tfFgComExec.getText().trim())) {
                lbfgComExec.setForeground(Palette.BLACK);
                data.setFgComExec(tfFgComExec.getText().trim());
            } else {
                lbfgComExec.setForeground(Palette.RED);
                dataOk = false;
            }
        }

        if (fgComMode != FgComMode.Internal) {
            if (fgComMode == FgComMode.Auto) {
                lbFgComHost.setText("localhost");
            }
            lbFgComHost.setForeground(Palette.BLACK);
        } else {
            // auto + internal
            if (checkHost(tfFgComHost.getText().trim())) {
                lbFgComHost.setForeground(Palette.BLACK);
                data.setFgComHost(tfFgComHost.getText().trim());
            } else {
                lbFgComHost.setForeground(Palette.RED);
                dataOk = false;
            }
        }

        if (fgComMode == FgComMode.Off
                || ((list = checkPorts(tfFgComPorts.getText().trim())).size() > 0 && (list = checkPorts(tfFgComPorts.getText().trim())).size() < 5)) {
            lbFgComPorts.setForeground(Palette.BLACK);
            if (fgComMode != FgComMode.Off)
                data.setFgComPorts(list);
        } else {
            lbFgComPorts.setForeground(Palette.RED);
            dataOk = false;
        }
        if (fgComMode == FgComMode.Off || checkHost(tfFgComServer.getText())) {
            lbFgComServer.setForeground(Palette.BLACK);
            data.setFgComServer(tfFgComServer.getText().trim());
        } else {
            lbFgComServer.setForeground(Palette.RED);
            dataOk = false;
        }
        data.setAltRadioTextEnabled(cbEnableAltRadioText.isSelected());
        
        data.setFpExchangeEnabled(cbEnableFpExchange.isSelected());
        if (cbEnableFpExchange.isSelected()) {
            if (checkUrl(tfFpServer.getText().trim())) {
                lbFpServer.setForeground(Palette.BLACK);
                data.setFpServerUrl(tfFpServer.getText().trim());
                data.setFpServerUser(tfFpServerUser.getText().trim());
                data.setFpServerPassword(new String(tfFpServerPassword.getPassword()));
            } else {
                lbFpServer.setForeground(Palette.RED);
                dataOk = false;
            }
        } else {
            lbFpServer.setForeground(Palette.BLACK);
        }
        
        if (checkUrl(tfMetarUrl.getText().trim())) {
            lbMetarUrl.setForeground(Palette.BLACK);
            data.setMetarUrl(tfMetarUrl.getText().trim());
        } else {
            lbMetarUrl.setForeground(Palette.RED);
            dataOk = false;
        }
        if ((list = checkPorts(tfMpLocalPort.getText().trim())).size() > 0) {
            lbMpLocalPort.setForeground(Palette.BLACK);
            data.setMpLocalPort(list.get(0));
        } else {
            lbMpLocalPort.setForeground(Palette.RED);
            dataOk = false;
        }
        if (checkHost(tfMpServer.getText().trim())) {
            lbMpServer.setForeground(Palette.BLACK);
            data.setMpServer(tfMpServer.getText().trim());
        } else {
            lbMpServer.setForeground(Palette.RED);
            dataOk = false;
        }
        if ((list = checkPorts(tfMpPort.getText().trim())).size() == 1) {
            lbMpPort.setForeground(Palette.BLACK);
            data.setMpServerPort(list.get(0));
        } else {
            lbMpPort.setForeground(Palette.RED);
            dataOk = false;
        }
        if ((list = checkPorts(tfMpLocalPort.getText().trim())).size() == 1) {
            lbMpLocalPort.setForeground(Palette.BLACK);
            data.setMpLocalPort(list.get(0));
        } else {
            lbMpLocalPort.setForeground(Palette.RED);
            dataOk = false;
        }
        
        data.setFpDownloadEnabled(cbEnableFpDownload.isSelected());
        if(cbEnableFpDownload.isEnabled()) {
	        if (checkUrl(tfLennysFpServer.getText().trim())) {
	            lbLennysServer.setForeground(Palette.BLACK);
	            data.setFpDownloadUrl(tfLennysFpServer.getText().trim());
	        } else {
	            lbLennysServer.setForeground(Palette.RED);
	            dataOk = false;
	        }
        } else {
        	lbLennysServer.setForeground(Palette.BLACK);
        }

        data.setChatAliasesEnabled(cbEnableChatAliases.isSelected());
        String alias = tfChatPrefix.getText().trim();
        if (!alias.isEmpty()) {
            data.setChatAliasPrefix(alias);
        }

        data.setToggle(GeometryToShapeProjector.TOGGLE_STATE, cbNiceShapes.isSelected());
        Map<String, Boolean> visibleLayerMap = new HashMap<String, Boolean>();
        visibleLayerMap.put("landmass", cbLandmass.isSelected());
        visibleLayerMap.put("urban", cbUrban.isSelected());
        visibleLayerMap.put("lake", cbLake.isSelected());
        visibleLayerMap.put("stream", cbStream.isSelected());
//        visibleLayerMap.put("tarmac", cbTarmac.isSelected());
        visibleLayerMap.put("groundnet", cbGroundnet.isSelected());
        data.setVisibleLayerMap(visibleLayerMap);

        // fgfs remove control
        data.setFgfsCamera1Enabled(cbFgfsCamera1Enabled.isSelected());
        if (checkHost(tfFgfsCamera1Host.getText().trim())) {
            tfFgfsCamera1Host.setForeground(Palette.BLACK);
            data.setFgfsCamera1Host(tfFgfsCamera1Host.getText().trim());
        } else {
            tfFgfsCamera1Host.setForeground(Palette.RED);
            dataOk = false;
        }
        if ((list = checkPorts(tfFgfsCamera1Port.getText().trim())).size() == 1) {
            tfFgfsCamera1Port.setForeground(Palette.BLACK);
            data.setFgfsCamera1Port(list.get(0));
        } else {
            tfFgfsCamera1Port.setForeground(Palette.RED);
            dataOk = false;
        }
        data.setFgfsLocalMPPacketForward1(cbFgfsLocalMPPacketForward1.isSelected());
        if ((list = checkPorts(tfFgfsLocalMPPacketPort1.getText().trim())).size() == 1) {
            tfFgfsLocalMPPacketPort1.setForeground(Palette.BLACK);
            data.setFgfsLocalMPPacketPort1(list.get(0));
        } else {
            tfFgfsLocalMPPacketPort1.setForeground(Palette.RED);
            dataOk = false;
        }
        if ((tfFgfsCamera1Host.getText().trim().equalsIgnoreCase("localhost") || tfFgfsCamera1Host.getText().trim().equalsIgnoreCase("127.0.0.1"))
                && tfFgfsLocalMPPacketPort1.getText().trim().equalsIgnoreCase(tfMpLocalPort.getText().trim())) {
            // same port as OR local MP port => Loop
            tfFgfsLocalMPPacketPort1.setForeground(Palette.RED);
            dataOk = false;
        } else {
            tfFgfsLocalMPPacketPort1.setForeground(Palette.BLACK);
        }
        // cam 2
        data.setFgfsCamera2Enabled(cbFgfsCamera2Enabled.isSelected());
        data.setFgfsSlave2To1(cbFgfsCamera2SlavedTo1.isSelected());
        if (checkHost(tfFgfsCamera2Host.getText().trim())) {
            tfFgfsCamera2Host.setForeground(Palette.BLACK);
            data.setFgfsCamera2Host(tfFgfsCamera2Host.getText().trim());
        } else {
            tfFgfsCamera2Host.setForeground(Palette.RED);
            dataOk = false;
        }
        if ((list = checkPorts(tfFgfsCamera2Port.getText().trim())).size() == 1) {
            tfFgfsCamera2Port.setForeground(Palette.BLACK);
            data.setFgfsCamera2Port(list.get(0));
        } else {
            tfFgfsCamera2Port.setForeground(Palette.RED);
            dataOk = false;
        }
        data.setFgfsLocalMPPacketForward2(cbFgfsLocalMPPacketForward2.isSelected());
        if ((list = checkPorts(tfFgfsLocalMPPacketPort2.getText().trim())).size() == 1) {
            tfFgfsLocalMPPacketPort2.setForeground(Palette.BLACK);
            data.setFgfsLocalMPPacketPort2(list.get(0));
        } else {
            tfFgfsLocalMPPacketPort2.setForeground(Palette.RED);
            dataOk = false;
        }
        if ((tfFgfsCamera2Host.getText().trim().equalsIgnoreCase("localhost") || tfFgfsCamera2Host.getText().trim().equalsIgnoreCase("127.0.0.1"))
                && tfFgfsLocalMPPacketPort2.getText().trim().equalsIgnoreCase(tfMpLocalPort.getText().trim())) {
            // same port as OR local MP port => Loop
            tfFgfsLocalMPPacketPort2.setForeground(Palette.RED);
            dataOk = false;
        } else {
            tfFgfsLocalMPPacketPort2.setForeground(Palette.BLACK);
        }

        btCheckSettings.setText("Check Settings & Save");
        btCheckSettings.setEnabled(true);
        btCheckSettings.setForeground(Palette.BLACK);

        if (!dataOk) {
            lbMessage.setText("Please verify your settings!");
            return false;
        } else {
            lbMessage.setText(null);
            saveProperties();
            if(liSearchResults.getSelectedValue() != null) {
	            btStart.setEnabled(true);
            }
            return true;
        }
    }

    private boolean checkUrl(String sUrl) {
        if (sUrl.isEmpty())
            return false;
        try {
            URL url = new URL(sUrl);
            return checkHost(url.getHost());
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private List<Integer> checkPorts(String text) {
        try {
            List<Integer> result = new ArrayList<Integer>();
            StringTokenizer st = new StringTokenizer(text, ",");
            while (st.hasMoreElements()) {
                int port = Integer.parseInt(st.nextToken());
                if (port < 1025 || port > 65535)
                    return new ArrayList<Integer>();
                result.add(port);
            }
            return result;
        } catch (Exception e) {
            return new ArrayList<Integer>();
        }
    }

    private boolean checkPath(String text) {
        if (text.isEmpty())
            return true;
        return new File(text).exists();
    }

    private boolean checkHost(String hostname) {
        if (hostname.isEmpty())
            return true;
        try {
            InetAddress address = Inet4Address.getByName(hostname);
            return address != null;
        } catch (UnknownHostException e) {
            return false;
        } catch (Exception e) {
            log.error("Error while checking host!", e);
        }
        return false;
    }

    private void loadProperties() {
        String filename = setupManager.getPropertiesFile();
        File defaultsFile = new File("settings" + File.separator + "defaults.properties");
        File userFile = new File("settings" + File.separator + filename);
        FileReader defReader = null;
        FileReader userReader = null;
        if (defaultsFile.exists()) {
            Properties p = new Properties();
            try {
                defReader = new FileReader(defaultsFile);
                p.load(defReader);
                defReader.close();
                if (userFile.exists()) {
                    userReader = new FileReader(userFile);
                    p.load(userReader);

                }

            } catch (IOException e) {

            } finally {
                if (defReader != null) {
                    try {
                        defReader.close();
                    } catch (IOException e) {
                    }
                }
                if (userReader != null) {
                    try {
                        userReader.close();
                    } catch (IOException e) {
                    }
                }
                fgComMode = FgComMode.valueOf(p.getProperty("fgCom.mode", FgComMode.Internal.toString()));
                if (fgComMode == FgComMode.Auto)
                    cbFgComMode.setSelectedIndex(0);
                if (fgComMode == FgComMode.Internal)
                    cbFgComMode.setSelectedIndex(1);
                if (fgComMode == FgComMode.External)
                    cbFgComMode.setSelectedIndex(2);
                if (fgComMode == FgComMode.Off)
                    cbFgComMode.setSelectedIndex(3);

                tfFgComPath.setText(p.getProperty("fgCom.path", ""));
                tfFgComPath.setText(p.getProperty("fgCom.path", ""));
                tfFgComExec.setText(p.getProperty("fgCom.exec", ""));
                tfFgComServer.setText(p.getProperty("fgCom.server", ""));
                tfFgComHost.setText(p.getProperty("fgCom.host", "localhost"));
                tfFgComPorts.setText(p.getProperty("fgCom.clientPorts", "16661,16662"));

                cbEnableAltRadioText.setSelected("true".equals(p.getProperty("altRadioText.enable", "false")));

                tfMpServer.setText(p.getProperty("mp.server", "mpserver01.flightgear.org"));
                tfMpPort.setText(p.getProperty("mp.serverPort", "5000"));
                tfMpLocalPort.setText(p.getProperty("mp.clientPort", "5001"));

                cbEnableFpExchange.setSelected("true".equals(p.getProperty("fpExchange.enable")));
                tfFpServer.setText(p.getProperty("fpExchange.server", "http://h2281805.stratoserver.net/FgFpServer/"));
                tfFpServer.setEnabled(cbEnableFpExchange.isSelected());
                tfFpServerUser.setText(p.getProperty("fpExchange.user", ""));
                tfFpServerUser.setEnabled(cbEnableFpExchange.isSelected());
                tfFpServerPassword.setText(p.getProperty("fpExchange.password", ""));
                tfFpServerPassword.setEnabled(cbEnableFpExchange.isSelected());

                cbEnableChatAliases.setSelected(!"false".equals(p.getProperty("chat.alias.enabled")));
                tfChatPrefix.setText(p.getProperty("chat.alias.prefix", "."));
                tfChatPrefix.setEnabled(cbEnableChatAliases.isSelected());

                tfMetarUrl.setText(p.getProperty("metar.url", "http://weather.noaa.gov/pub/data/observations/metar/stations/"));

                cbDataboxLayout.setSelectedIndex(setupManager.getDatablockLayoutManager().getIndexOfActiveLayout());

                cbEnableFpDownload.setSelected("true".equals(p.getProperty("fpDownload.enable")));
                tfLennysFpServer.setText(p.getProperty("fpLenny.server", "http://flightgear-atc.alwaysdata.net/dev2014_01_13.php5"));
                tfLennysFpServer.setEnabled(cbEnableFpDownload.isSelected());
                
                {
                    String name = p.getProperty("radar.datablockLayout");
                    if (name != null) {
                        setupManager.getDatablockLayoutManager().setActiveLayout(null, name);
                        cbDataboxLayout.setSelectedItem(setupManager.getDatablockLayoutManager().getActiveLayout());
                    }
                }
                cbNiceShapes.setSelected(!"false".equals(p.getProperty(GeometryToShapeProjector.TOGGLE_STATE)));

                cbLandmass.setSelected(!"false".equals(p.getProperty("layer.landmass")));
                cbUrban.setSelected(!"false".equals(p.getProperty("layer.urban")));
                cbLake.setSelected(!"false".equals(p.getProperty("layer.lake")));
                cbStream.setSelected("true".equals(p.getProperty("layer.stream")));
//                cbTarmac.setSelected(!"false".equals(p.getProperty("layer.tarmac")));
                cbGroundnet.setSelected(!"false".equals(p.getProperty("layer.groundnet")));

                cbFgfsCamera1Enabled.setSelected("true".equals(p.getProperty("fgfs.cameraEnabled")));
                tfFgfsCamera1Host.setText(p.getProperty("fgfs.cameraHost", "localhost"));
                tfFgfsCamera1Port.setText(p.getProperty("fgfs.cameraPort", "5010"));
                cbFgfsLocalMPPacketForward1.setSelected("true".equals(p.getProperty("fgfs.localMPPacketForward")));
                tfFgfsLocalMPPacketPort1.setText(p.getProperty("fgfs.localMPPacketPort", "5010"));

                cbFgfsCamera2Enabled.setSelected("true".equals(p.getProperty("fgfs.camera2Enabled")));
                cbFgfsCamera2SlavedTo1.setSelected("true".equals(p.getProperty("fgfs.camera2SlavedTo1")));
                tfFgfsCamera2Host.setText(p.getProperty("fgfs.camera2Host", "localhost"));
                tfFgfsCamera2Port.setText(p.getProperty("fgfs.camera2Port", "5020"));
                cbFgfsLocalMPPacketForward2.setSelected("true".equals(p.getProperty("fgfs.localMPPacketForward2")));
                tfFgfsLocalMPPacketPort2.setText(p.getProperty("fgfs.localMPPacketPort2", "5020"));
            }

        }
    }

    private void saveProperties() {
        String filename = setupManager.getPropertiesFile();
        File userFile = new File("settings" + File.separator + filename);
        Properties p = new Properties();
        p.put("fgCom.mode", fgComMode.toString());
        p.put("fgCom.path", tfFgComPath.getText().trim());
        p.put("fgCom.exec", tfFgComExec.getText().trim());
        p.put("fgCom.server", tfFgComServer.getText().trim());
        p.put("fgCom.host", tfFgComHost.getText().trim());
        p.put("fgCom.clientPorts", tfFgComPorts.getText().trim());
        p.put("mp.server", tfMpServer.getText().trim());
        p.put("mp.serverPort", tfMpPort.getText().trim());
        p.put("mp.clientPort", tfMpLocalPort.getText().trim());

        p.put("fpExchange.enable", "" + cbEnableFpExchange.isSelected());
        p.put("fpExchange.server", tfFpServer.getText().trim());
        p.put("fpExchange.user", tfFpServerUser.getText().trim());
        p.put("fpExchange.password", new String(tfFpServerPassword.getPassword()));

        p.put("altRadioText.enable", "" + cbEnableAltRadioText.isSelected());

        p.put("chat.alias.enabled", "" + cbEnableChatAliases.isSelected());
        p.put("chat.alias.prefix", tfChatPrefix.getText().trim());

        p.put("metar.url", tfMetarUrl.getText().trim());

        p.put("fpDownload.enable", "" + cbEnableFpDownload.isSelected());
        p.put("fpLenny.server", tfLennysFpServer.getText().trim());

        p.put("radar.datablockLayout", ((ADatablockLayout) cbDataboxLayout.getSelectedItem()).getName());

        p.put(GeometryToShapeProjector.TOGGLE_STATE, "" + cbNiceShapes.isSelected());

        p.put("layer.landmass", "" + cbLandmass.isSelected());
        p.put("layer.urban", "" + cbUrban.isSelected());
        p.put("layer.lake", "" + cbLake.isSelected());
        p.put("layer.stream", "" + cbStream.isSelected());
//        p.put("layer.tarmac", "" + cbTarmac.isSelected());
        p.put("layer.groundnet", "" + cbGroundnet.isSelected());

        p.put("fgfs.cameraEnabled", "" + cbFgfsCamera1Enabled.isSelected());
        p.put("fgfs.cameraHost", tfFgfsCamera1Host.getText());
        p.put("fgfs.cameraPort", tfFgfsCamera1Port.getText());
        p.put("fgfs.localMPPacketForward", "" + cbFgfsLocalMPPacketForward1.isSelected());
        p.put("fgfs.localMPPacketPort", tfFgfsLocalMPPacketPort1.getText());
        p.put("fgfs.camera2Enabled", "" + cbFgfsCamera2Enabled.isSelected());
        p.put("fgfs.camera2SlavedTo1", "" + cbFgfsCamera2SlavedTo1.isSelected());
        p.put("fgfs.camera2Host", tfFgfsCamera2Host.getText());
        p.put("fgfs.camera2Port", tfFgfsCamera2Port.getText());
        p.put("fgfs.localMPPacketForward2", "" + cbFgfsLocalMPPacketForward2.isSelected());
        p.put("fgfs.localMPPacketPort2", tfFgfsLocalMPPacketPort2.getText());

        FileWriter userWriter = null;
        try {
            if (userFile.exists())
                userFile.delete();
            userWriter = new FileWriter(userFile);

            p.store(userWriter, "Open Radar user settings... This file will be overwritten!");
        } catch (IOException e) {

        } finally {
            if (userWriter != null) {
                try {
                    userWriter.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void sectorSelected(AirportData data) {
        SectorBean sb = liSearchResults.getSelectedValue();
        if (sb != null) {
            if (sb.isSectorDownloaded()) {
                data.setAirportCode(sb.getAirportCode());
                data.setAirportPosition(sb.getPosition());
                data.setAirportName(sb.getAirportName());
                data.setMagneticDeclination(sb.getMagneticDeclination());
                btStart.setEnabled(readInputs(data));
                btCreateSector.setEnabled(false);
                btDeleteSector.setEnabled(true);
                data.loadLastCallSign();
                if (data.getCallSign() != null) {
                    tfCallSign.setText(data.getCallSign());
                } else {
                    tfCallSign.setText(data.getAirportCode() + "_TW");
                }
            } else {
                data.setAirportCode(sb.getAirportCode());
                data.setAirportName(sb.getAirportName());
                btStart.setEnabled(false);
                btCreateSector.setEnabled(true);
                btDeleteSector.setEnabled(false);
            }
        } else {
            data.setAirportCode(null);
            btStart.setEnabled(false);
            btCreateSector.setEnabled(false);
            btDeleteSector.setEnabled(true);
        }
    }

    public void setStatus(int progress, String message) {
        jProgressBar.setValue(progress);
        jProgressBar.setString("" + progress + "%");
        Rectangle rect = jProgressBar.getBounds();
        rect.x = 0;
        rect.y = 0;
        jProgressBar.paintImmediately(rect);
        cbStatusModel.addNewStatusMessage(message);
        cbStatusMessages.setSelectedItem(message);
        rect = cbStatusMessages.getBounds();
        rect.x = 0;
        rect.y = 0;
        cbStatusMessages.paintImmediately(rect);

    }

    private class FgComModeActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == cbFgComMode) {
                if (cbFgComMode.getSelectedIndex() == 0) {
                    fgComMode = FgComMode.Auto;
                    cbFgComMode.setToolTipText("OpenRadar will start FGCom internally and control it!");
                    // internal
                    tfFgComPath.setEnabled(false);
                    tfFgComExec.setEnabled(false);
                    tfFgComPorts.setEnabled(true);
                    tfFgComHost.setEnabled(false);
                    tfFgComHost.setText("localhost");
                    tfFgComServer.setEnabled(true);
                } else if (cbFgComMode.getSelectedIndex() == 1) {
                    fgComMode = FgComMode.Internal;
                    cbFgComMode.setToolTipText("OpenRadar will start FGCom internally and control it!");
                    // internal
                    tfFgComPath.setEnabled(true);
                    tfFgComExec.setEnabled(true);
                    tfFgComPorts.setEnabled(true);
                    tfFgComHost.setEnabled(true);
                    tfFgComServer.setEnabled(true);
                } else if (cbFgComMode.getSelectedIndex() == 2) {
                    fgComMode = FgComMode.External;
                    cbFgComMode.setToolTipText("You will start FGCom or FgComGui yourself and OpenRadar will control it!");
                    // external
                    tfFgComPath.setEnabled(false);
                    tfFgComExec.setEnabled(false);
                    tfFgComPorts.setEnabled(true);
                    tfFgComHost.setEnabled(true);
                    tfFgComServer.setEnabled(false);
                } else if (cbFgComMode.getSelectedIndex() == 3) {
                    fgComMode = FgComMode.Off;
                    cbFgComMode.setToolTipText("FGCom will not be controlled by OpenRadar");
                    // off
                    tfFgComPath.setEnabled(false);
                    tfFgComExec.setEnabled(false);
                    tfFgComPorts.setEnabled(false);
                    tfFgComHost.setEnabled(false);
                    tfFgComServer.setEnabled(false);
                }
            }
        }
    }

    private class FpServerActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            tfFpServer.setEnabled(cbEnableFpExchange.isSelected());
            tfFpServerUser.setEnabled(cbEnableFpExchange.isSelected());
            tfFpServerPassword.setEnabled(cbEnableFpExchange.isSelected());
        }
    }

    private class FpDownloadActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            tfLennysFpServer.setEnabled(cbEnableFpDownload.isSelected());
        }
    }
    
    private class ChatEnabledActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            tfChatPrefix.setEnabled(cbEnableChatAliases.isSelected());
        }
    }

    public String getCallsign() {
        return tfCallSign.getText();
    }
}
