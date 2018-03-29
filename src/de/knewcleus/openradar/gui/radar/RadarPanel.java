/**
 * Copyright (C) 2012-2016,2018 Wolfram Wagner
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
package de.knewcleus.openradar.gui.radar;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.contacts.HandoverTargetDialog;
import de.knewcleus.openradar.rpvd.contact.ADatablockLayout;

/**
 * The panel containing the radar components...
 *
 * @author Wolfram Wagner
 */
public class RadarPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private GuiMasterController master;

    private javax.swing.JPanel radarControlBar;
    private RadarMapPanel radarView;

    private JCheckBoxMenuItem mItemFIX;
    private JCheckBoxMenuItem mItemFIX_NUM;
    private JCheckBoxMenuItem mItemNDB;
    private JCheckBoxMenuItem mItemVOR;
    private JCheckBoxMenuItem mItemCircles;
    private JCheckBoxMenuItem mItemApt;
    private JCheckBoxMenuItem mItemPPN;
    private JLabel mItemPPN2;
    private JCheckBoxMenuItem mItemGSH;
//    private JCheckBoxMenuItem mItemSTP;
    private JLabel mItemSTP2;
    private JCheckBoxMenuItem mItemSTARSID;
    private JLabel mItemSTARSID2;

    private HashMap<String,JMenuItem> dataLayoutsMenuItems = new HashMap<String,JMenuItem>();

    private JCheckBoxMenuItem mItemLANDMASS;
    private JCheckBoxMenuItem mItemURBAN;
    private JCheckBoxMenuItem mItemLAKE;
    private JCheckBoxMenuItem mItemSTREAM;
    private JCheckBoxMenuItem mItemTARMAC;

    private JCheckBoxMenuItem mItemSoundMute;
    private JCheckBoxMenuItem mItemSoundChat;
    private JCheckBoxMenuItem mItemSoundContact;
    private JCheckBoxMenuItem mItemSoundMetar;


    private JPanel zoomPanel;
    private javax.swing.JLabel lbZoomGround;
    private javax.swing.JLabel lbZoomTower;
    private javax.swing.JLabel lbZoomApp;
    private javax.swing.JLabel lbZoomSector;

    private JTextField tfSearchNavaids;
    
    private HandoverTargetDialog handoverDialog;

    private Logger log = Logger.getLogger(RadarPanel.class);
    
    /**
     * Creates new form RadarPanel
     */
    public RadarPanel(GuiMasterController master) {
        this.master=master;
        master.getRadarBackend().setPanel(this);
        handoverDialog = new HandoverTargetDialog(master);
        initComponents();
        setDropTarget( new RadarDropTarget() );
    }

    public RadarMapPanel getRadarMapPanel() {
        return radarView;
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        radarView = new RadarMapPanel(master);

        lbZoomGround = new javax.swing.JLabel();
        lbZoomTower = new javax.swing.JLabel();
        lbZoomApp = new javax.swing.JLabel();
        lbZoomSector = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(100, 100));
        setLayout(new java.awt.GridBagLayout());
        setOpaque(true);
        setBackground(Palette.DESKTOP);
        

        radarView.setBackground(Palette.DESKTOP);

        javax.swing.GroupLayout RadarDummyLayout = new javax.swing.GroupLayout(radarView);
        radarView.setLayout(RadarDummyLayout);
        RadarDummyLayout.setHorizontalGroup(
            RadarDummyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 392, Short.MAX_VALUE)
        );
        RadarDummyLayout.setVerticalGroup(
            RadarDummyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 279, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        add(radarView, gridBagConstraints);

        radarControlBar = new javax.swing.JPanel();
        radarControlBar.setLayout(new java.awt.GridBagLayout());
        radarControlBar.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor=GridBagConstraints.WEST;
        gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx=1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        add(radarControlBar, gridBagConstraints);

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new java.awt.GridBagLayout());
        filterPanel.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        radarControlBar.add(filterPanel, gridBagConstraints);

        JMenuBar menuBar = new JBackgroundMenuBar();
        menuBar.setOpaque(false);
        menuBar.setBackground(Palette.DESKTOP);
        menuBar.setForeground(Palette.WHITE);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor=GridBagConstraints.WEST;
        gridBagConstraints.weightx=1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        filterPanel.add(menuBar,gridBagConstraints);

        master.getRadarManager().getObjectFilterListener().setMenuParent(this);
        master.getRadarManager().getSoundMuteActionListener().setMenuParent(this);

        // menu map

        JMenu menuMap = new JMenu("map");
        menuMap.setFont(menuMap.getFont().deriveFont(Font.BOLD));
        menuMap.setBackground(Palette.DESKTOP);
        menuMap.setForeground(Palette.DESKTOP_TEXT);
        menuBar.add(menuMap);

//        mItemSTP = new JCheckBoxMenuItem();
//        mItemSTP.setText("STP: Mousetip measuring");
//        mItemSTP.setName("STP");
//        mItemSTP.setToolTipText("Toggle display of distance data near mousetip");
//        mItemSTP.addActionListener(master.getRadarManager().getObjectFilterListener());
//        menuMap.add(mItemSTP);

        mItemSTARSID = new JCheckBoxMenuItem();
        mItemSTARSID.setText("STAR/SID");
        mItemSTARSID.setName("STARSID");
        mItemSTARSID.setToolTipText("Toggle display of parking/gate names");
        mItemSTARSID.addActionListener(master.getRadarManager().getObjectFilterListener());
        menuMap.add(mItemSTARSID);

        mItemPPN = new JCheckBoxMenuItem();
        mItemPPN.setText("PPN: Parking/Gate Names");
        mItemPPN.setName("PPN");
        mItemPPN.setToolTipText("Toggle display of parking/gate names");
        mItemPPN.addActionListener(master.getRadarManager().getObjectFilterListener());
        menuMap.add(mItemPPN);

        menuMap.add(new JSeparator());

        mItemFIX = new JCheckBoxMenuItem();
        mItemFIX.setText("FIX");
        mItemFIX.setName("FIX");
        mItemFIX.setToolTipText("Toggle display of FIX");
        mItemFIX.addActionListener(master.getRadarManager().getObjectFilterListener());
        menuMap.add(mItemFIX);

        mItemFIX_NUM = new JCheckBoxMenuItem();
        mItemFIX_NUM.setText("RW-FIX (name includes numbers)");
        mItemFIX_NUM.setName("FIX_NUM");
        mItemFIX_NUM.setToolTipText("Toggle display of runway FIX");
        mItemFIX_NUM.addActionListener(master.getRadarManager().getObjectFilterListener());
        menuMap.add(mItemFIX_NUM);

        mItemNDB = new JCheckBoxMenuItem();
        mItemNDB.setText("NDB");
        mItemNDB.setName("NDB");
        mItemNDB.setToolTipText("Toggle display of NDB");
        mItemNDB.addActionListener(master.getRadarManager().getObjectFilterListener());
        menuMap.add(mItemNDB);

        mItemVOR = new JCheckBoxMenuItem();
        mItemVOR.setText("VOR");
        mItemVOR.setName("VOR");
        mItemVOR.setToolTipText("Toggle display of VOR");
        mItemVOR.addActionListener(master.getRadarManager().getObjectFilterListener());
        menuMap.add(mItemVOR);

        mItemApt = new JCheckBoxMenuItem();
        mItemApt.setText("Airports");
        mItemApt.setName("APT");
        mItemApt.setToolTipText("Toggle display of airport codes");
        mItemApt.addActionListener(master.getRadarManager().getObjectFilterListener());
        menuMap.add(mItemApt);

        menuMap.add(new JSeparator());

        mItemCircles = new JCheckBoxMenuItem();
        mItemCircles.setText("Circles");
        mItemCircles.setName("CIRCLES");
        mItemCircles.setToolTipText("Toggle display of distance circles");
        mItemCircles.addActionListener(master.getRadarManager().getObjectFilterListener());
        menuMap.add(mItemCircles);


        mItemGSH = new JCheckBoxMenuItem();
        mItemGSH.setText("GS Heights");
        mItemGSH.setName("GSH");
        mItemGSH.setToolTipText("Toggle display of heights of glideslope");
        mItemGSH.addActionListener(master.getRadarManager().getObjectFilterListener());
        menuMap.add(mItemGSH);

        menuMap.add(new JSeparator());

        // submenu data block layout

        JMenu menuDataMode = new JMenu("data mode");
        menuDataMode.setFont(menuMap.getFont().deriveFont(Font.BOLD));
        menuMap.add(menuDataMode);
        ButtonGroup bg = new ButtonGroup();

        for(ADatablockLayout layout : master.getAirportData().getDatablockLayoutManager().getLayoutModes()) {
            JCheckBoxMenuItem mItemDbLayout = new JCheckBoxMenuItem();
            bg.add(mItemDbLayout);
            mItemDbLayout.setText(layout.getMenuText());
            mItemDbLayout.setName(layout.getName());
            dataLayoutsMenuItems.put(layout.getName(),mItemDbLayout);
            mItemDbLayout.addActionListener(master.getAirportData().getDatablockLayoutManager().getActionListener(master));
            menuDataMode.add(mItemDbLayout);
        }

        // submenu update speed

        JMenu menuMapUpdates = new JMenu("update frequency");
        menuMapUpdates.setFont(menuMap.getFont().deriveFont(Font.BOLD));
        menuMap.add(menuMapUpdates);
        ButtonGroup bgUpdateSpeed = new ButtonGroup();

        JCheckBoxMenuItem mItemFast = new JCheckBoxMenuItem();
        bgUpdateSpeed.add(mItemFast);
        mItemFast.setText("1 s");
        mItemFast.setName("UPDATE_1");
        mItemFast.setSelected(master.getAirportData().getAntennaRotationTime()==1000);
        mItemFast.addActionListener(new MenuListenerMapUpdate());
        menuMapUpdates.add(mItemFast);

        JCheckBoxMenuItem mItemMiddle = new JCheckBoxMenuItem();
        bgUpdateSpeed.add(mItemMiddle);
        mItemMiddle.setText("3 s");
        mItemMiddle.setName("UPDATE_3");
        mItemMiddle.setSelected(master.getAirportData().getAntennaRotationTime()==3000);
        mItemMiddle.addActionListener(new MenuListenerMapUpdate());
        menuMapUpdates.add(mItemMiddle);

        JCheckBoxMenuItem mItemSlow = new JCheckBoxMenuItem();
        bgUpdateSpeed.add(mItemSlow);
        mItemSlow.setText("5 s");
        mItemSlow.setName("UPDATE_5");
        mItemSlow.setSelected(master.getAirportData().getAntennaRotationTime()==5000);
        mItemSlow.addActionListener(new MenuListenerMapUpdate());
        menuMapUpdates.add(mItemSlow);
        
        // submenu aircraft tail
        
        JMenu menuTailLength = new JMenu("contact tail");
        menuTailLength.setFont(menuMap.getFont().deriveFont(Font.BOLD));
        menuMap.add(menuTailLength);
        ButtonGroup bgTailLength = new ButtonGroup();

        JCheckBoxMenuItem mItemTLOff = new JCheckBoxMenuItem();
        bgTailLength.add(mItemTLOff);
        mItemTLOff.setText("no tail");
        mItemTLOff.setName("TAIL_OFF");
        mItemTLOff.setSelected(master.getAirportData().getContactTailLength()==0);
        mItemTLOff.addActionListener(new MenuListenerTailLength());
        menuTailLength.add(mItemTLOff);

        JCheckBoxMenuItem mItemTLShort = new JCheckBoxMenuItem();
        bgTailLength.add(mItemTLShort);
        mItemTLShort.setText("5");
        mItemTLShort.setName("TAIL_5");
        mItemTLShort.setSelected(master.getAirportData().getContactTailLength()==5);
        mItemTLShort.addActionListener(new MenuListenerTailLength());
        menuTailLength.add(mItemTLShort);

        JCheckBoxMenuItem mItemTLLong = new JCheckBoxMenuItem();
        bgTailLength.add(mItemTLLong);
        mItemTLLong.setText("10");
        mItemTLLong.setName("TAIL_10");
        mItemTLLong.setSelected(master.getAirportData().getContactTailLength()==10);
        mItemTLLong.addActionListener(new MenuListenerTailLength());
        menuTailLength.add(mItemTLLong);

        // submenu layers

        JMenu menuLayers = new JMenu("layers");
        menuLayers.setFont(menuMap.getFont().deriveFont(Font.BOLD));
        menuMap.add(menuLayers);

        mItemLANDMASS = new JCheckBoxMenuItem();
        mItemLANDMASS.setText("Landmass");
        mItemLANDMASS.setName("LANDMASS");
        mItemLANDMASS.setToolTipText("Toggle display of landmass layer");
        mItemLANDMASS.addActionListener(master.getRadarManager().getObjectFilterListener());
        if(master.getAirportData().isLayerVisible("landmass")) {
            menuLayers.add(mItemLANDMASS);
        }

        mItemURBAN = new JCheckBoxMenuItem();
        mItemURBAN.setText("Urban");
        mItemURBAN.setName("URBAN");
        mItemURBAN.setToolTipText("Toggle display of urban layer");
        mItemURBAN.addActionListener(master.getRadarManager().getObjectFilterListener());
        if(master.getAirportData().isLayerVisible("urban")) {
            menuLayers.add(mItemURBAN);
        }

        mItemLAKE = new JCheckBoxMenuItem();
        mItemLAKE.setText("Lake");
        mItemLAKE.setName("LAKE");
        mItemLAKE.setToolTipText("Toggle display of lake layer");
        mItemLAKE.addActionListener(master.getRadarManager().getObjectFilterListener());
        if(master.getAirportData().isLayerVisible("lake")) {
            menuLayers.add(mItemLAKE);
        }

        mItemSTREAM = new JCheckBoxMenuItem();
        mItemSTREAM.setText("Stream");
        mItemSTREAM.setName("STREAM");
        mItemSTREAM.setToolTipText("Toggle display of stream layer");
        mItemSTREAM.addActionListener(master.getRadarManager().getObjectFilterListener());
        if(master.getAirportData().isLayerVisible("stream")) {
            menuLayers.add(mItemSTREAM);
        }

        mItemTARMAC = new JCheckBoxMenuItem();
        mItemTARMAC.setText("Tarmac");
        mItemTARMAC.setName("TARMAC");
        mItemTARMAC.setToolTipText("Toggle display of tarmac layer");
        mItemTARMAC.addActionListener(master.getRadarManager().getObjectFilterListener());
        if(master.getAirportData().isLayerVisible("tarmac")) {
            menuLayers.add(mItemTARMAC);
        }

        // ---------------

        // menu sounds

        JMenu menuSound = new JMenu("sounds");
        menuSound.setBackground(Palette.DESKTOP);
        menuSound.setForeground(Palette.DESKTOP_TEXT);
        menuSound.setFont(menuSound.getFont().deriveFont(Font.BOLD));
        menuBar.add(menuSound);

        mItemSoundMute = new JCheckBoxMenuItem();
        mItemSoundMute.setText("- mute -");
        mItemSoundMute.setName("MUTE");
        mItemSoundMute.setToolTipText("Mute all sounds");
        mItemSoundMute.addActionListener(master.getRadarManager().getSoundMuteActionListener());
        menuSound.add(mItemSoundMute);

        menuSound.add(new JSeparator());

        mItemSoundChat = new JCheckBoxMenuItem();
        mItemSoundChat.setText("mute Chat message sound");
        mItemSoundChat.setName("CHAT");
        mItemSoundChat.setToolTipText("Mute chat messages for me");
        mItemSoundChat.addActionListener(master.getRadarManager().getSoundMuteActionListener());
        menuSound.add(mItemSoundChat);

        mItemSoundContact = new JCheckBoxMenuItem();
        mItemSoundContact.setText("mute New Contact sound");
        mItemSoundContact.setName("CONTACT");
        mItemSoundContact.setToolTipText("New contact arrives");
        mItemSoundContact.addActionListener(master.getRadarManager().getSoundMuteActionListener());
        menuSound.add(mItemSoundContact);

        mItemSoundMetar = new JCheckBoxMenuItem();
        mItemSoundMetar.setText("mute Metar change sound");
        mItemSoundMetar.setName("METAR");
        mItemSoundMetar.setToolTipText("Metar has changed");
        mItemSoundMetar.addActionListener(master.getRadarManager().getSoundMuteActionListener());
        menuSound.add(mItemSoundMetar);
        // top level items

        mItemSTP2 = new JLabel("STP");
        //mItemSTP2.setText("STP");
        mItemSTP2.setName("STP");
        mItemSTP2.setForeground(Palette.DESKTOP_TEXT);
        mItemSTP2.setToolTipText("Toggle display of distance data near mousetip");
        mItemSTP2.addMouseListener(master.getRadarManager().getObjectFilterListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        filterPanel.add(mItemSTP2, gridBagConstraints);

        mItemSTARSID2 = new JLabel();
        mItemSTARSID2.setText("STAR/SID");
        mItemSTARSID2.setName("STARSID");
        mItemSTARSID2.setForeground(Palette.DESKTOP_TEXT);
        mItemSTARSID2.setToolTipText("Toggle display of standard routes");
        mItemSTARSID2.addMouseListener(master.getRadarManager().getObjectFilterListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor=GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        filterPanel.add(mItemSTARSID2, gridBagConstraints);

        mItemPPN2 = new JLabel();
        mItemPPN2.setText("PPN");
        mItemPPN2.setName("PPN");
        mItemPPN2.setForeground(Palette.DESKTOP_TEXT);
        mItemPPN2.setToolTipText("Toggle display of parking/gate names");
        mItemPPN2.addMouseListener(master.getRadarManager().getObjectFilterListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        filterPanel.add(mItemPPN2, gridBagConstraints);

        JPanel filterSpace = new JPanel();
        filterSpace.setLayout(new java.awt.GridBagLayout());
        filterSpace.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx=10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        filterPanel.add(filterSpace, gridBagConstraints);

        JPanel space1 = new JPanel();
        space1.setLayout(new java.awt.GridBagLayout());
        space1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx=2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        radarControlBar.add(space1, gridBagConstraints);

        zoomPanel = new JPanel();
        zoomPanel.setLayout(new java.awt.GridBagLayout());
        zoomPanel.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx=5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        radarControlBar.add(zoomPanel, gridBagConstraints);

        lbZoomGround.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbZoomGround.setText("GROUND");
        lbZoomGround.setName("GROUND");
        lbZoomGround.setToolTipText("F1, left click to choose, middle click to define");
        lbZoomGround.setForeground(Palette.WHITE);
        lbZoomGround.addMouseListener(master.getRadarManager().getZoomMouseListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        zoomPanel.add(lbZoomGround, gridBagConstraints);

        lbZoomTower.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbZoomTower.setText("TOWER");
        lbZoomTower.setName("TOWER");
        lbZoomTower.setForeground(Palette.BLUE);
        lbZoomTower.setToolTipText("F2, left click to choose, middle click to define");
        lbZoomTower.addMouseListener(master.getRadarManager().getZoomMouseListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        zoomPanel.add(lbZoomTower, gridBagConstraints);

        lbZoomApp.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbZoomApp.setText("APP");
        lbZoomApp.setName("APP");
        lbZoomApp.setForeground(Palette.WHITE);
        lbZoomApp.setToolTipText("F3, left click to choose, middle click to define");
        lbZoomApp.addMouseListener(master.getRadarManager().getZoomMouseListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        zoomPanel.add(lbZoomApp, gridBagConstraints);

        lbZoomSector.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbZoomSector.setText("SECTOR");
        lbZoomSector.setName("SECTOR");
        lbZoomSector.setToolTipText("F4, left click to choose, middle/right click to define");
        lbZoomSector.setForeground(Palette.WHITE);
        lbZoomSector.addMouseListener(master.getRadarManager().getZoomMouseListener());
        zoomPanel.add(lbZoomSector, new java.awt.GridBagConstraints());

        JPanel space = new JPanel();
        space.setLayout(new java.awt.GridBagLayout());
        space.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx=1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        radarControlBar.add(space, gridBagConstraints);

        tfSearchNavaids = new JTextField();
        tfSearchNavaids.setFont(tfSearchNavaids.getFont().deriveFont(10));
        tfSearchNavaids.setToolTipText("Enter navaids/airport codes to find and highlight");
        //tfSearchNavaids.setForeground(Palette.WHITE);
        tfSearchNavaids.addActionListener(master.getRadarManager().getNavaidSearchActionListener());
        tfSearchNavaids.setMinimumSize(new Dimension(100,tfSearchNavaids.getFont().getSize()+10));
        tfSearchNavaids.setPreferredSize(new Dimension(400,tfSearchNavaids.getFont().getSize()+10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx=0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        radarControlBar.add(tfSearchNavaids, gridBagConstraints);

        // radar radius
        JComboBox<String> cbRange = new JComboBox<>();
        cbRange.setToolTipText("Flightstrip filter range. Default 100NM radius");
        cbRange.setMaximumSize(new Dimension(100,cbRange.getFont().getSize()+10));
        cbRange.setPreferredSize(new Dimension(70,cbRange.getFont().getSize()+10));
        cbRange.setForeground(tfSearchNavaids.getForeground());
        cbRange.setFont(menuMap.getFont().deriveFont(Font.BOLD));
        cbRange.addItem("100");
        cbRange.addItem("50");
        cbRange.addItem("80");
        cbRange.addItem("150");
        cbRange.addItem("200");
        cbRange.addItem("300");
        cbRange.addItem("400");
        cbRange.addItem("500");
        cbRange.addItem("750");
        cbRange.addItem("1000");
        cbRange.setEditable(true);
        cbRange.setSelectedItem(""+master.getAirportData().getFlightStripRadarRange());
        cbRange.addActionListener(master.getRadarManager().getRadarRangeActionListener());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx=0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        radarControlBar.add(cbRange);
        
        doLayout();
    }

    public void resetFilters() {
        lbZoomGround.setForeground(Palette.WHITE);
        lbZoomTower.setForeground(Palette.WHITE);
        lbZoomApp.setForeground(Palette.WHITE);
        lbZoomSector.setForeground(Palette.WHITE);
    }

    public void selectFilter(String zoomLevelKey) {
        if(!master.getAirportData().getNavaidDB().hasRoutes()) {  // show only if there is something to hide
            mItemSTARSID.setVisible(false);
        }
        // reset
        lbZoomGround.setForeground(Palette.WHITE);
        lbZoomTower.setForeground(Palette.WHITE);
        lbZoomApp.setForeground(Palette.WHITE);
        lbZoomSector.setForeground(Palette.WHITE);

        // set
        for(Component c : zoomPanel.getComponents()) {
            if(zoomLevelKey.equals(c.getName())) {
              ((JComponent)c).setForeground(Palette.DESKTOP_FILTER_SELECTED);
            }
        }
    }

    public void setObjectFilter(JComponent l, boolean state) {
        if(l instanceof JMenuItem) { 
        	((JMenuItem)l).setSelected(state);
        }
        
        if(l.getName().equals("PPN")) {
            mItemPPN.setSelected(state);
            if(state) {
                mItemPPN2.setForeground(Palette.WHITE);
            } else {
                mItemPPN2.setForeground(Palette.GRAY);
            }
        } else if(l.getName().equals("STP")) {
            if(state) {
            	mItemSTP2.setForeground(Palette.WHITE);
            } else {
            	mItemSTP2.setForeground(Palette.GRAY);
            }
        } else if(l.getName().equals("STARSID")) {
            mItemSTARSID.setSelected(state);
            if(state) {
                mItemSTARSID2.setForeground(Palette.WHITE);
            } else {
                mItemSTARSID2.setForeground(Palette.GRAY);
            }
        }

    }

    public void validateToggles() {
        setObjectFilter(mItemFIX,master.getAirportData().getRadarObjectFilterState("FIX"));
        setObjectFilter(mItemFIX_NUM,master.getAirportData().getToggleState("FIX_NUM", false));
        setObjectFilter(mItemNDB,master.getAirportData().getRadarObjectFilterState("NDB"));
        setObjectFilter(mItemVOR,master.getAirportData().getRadarObjectFilterState("VOR"));
        setObjectFilter(mItemCircles,master.getAirportData().getRadarObjectFilterState("CIRCLES"));
        setObjectFilter(mItemApt,master.getAirportData().getToggleState("APT",false));
        setObjectFilter(mItemPPN,master.getAirportData().getRadarObjectFilterState("PPN"));
        setObjectFilter(mItemPPN2,master.getAirportData().getRadarObjectFilterState("PPN"));
        setObjectFilter(mItemGSH,master.getAirportData().getRadarObjectFilterState("GSH"));
//        setObjectFilter(mItemSTP,master.getAirportData().getRadarObjectFilterState("STP"));
        setObjectFilter(mItemSTP2,master.getAirportData().getRadarObjectFilterState("STP"));
        setObjectFilter(mItemSTARSID,master.getAirportData().getRadarObjectFilterState("STARSID"));
        setObjectFilter(mItemSTARSID2,master.getAirportData().getRadarObjectFilterState("STARSID"));

        setObjectFilter(mItemSoundMute,master.getAirportData().getToggleState("MUTE",false));
        setObjectFilter(mItemSoundChat,master.getAirportData().getToggleState("CHAT",false));
        setObjectFilter(mItemSoundContact,master.getAirportData().getToggleState("CONTACT",false));
        setObjectFilter(mItemSoundMetar,master.getAirportData().getToggleState("METAR",false));

        setObjectFilter(mItemLANDMASS,master.getAirportData().getRadarObjectFilterState("LANDMASS"));
        setObjectFilter(mItemURBAN,master.getAirportData().getRadarObjectFilterState("URBAN"));
        setObjectFilter(mItemLAKE,master.getAirportData().getRadarObjectFilterState("LAKE"));
        setObjectFilter(mItemSTREAM,master.getAirportData().getToggleState("STREAM", false));
        setObjectFilter(mItemTARMAC,master.getAirportData().getRadarObjectFilterState("TARMAC"));

        String key = master.getAirportData().getDatablockLayoutManager().getActiveLayout().getName();
        dataLayoutsMenuItems.get(key).setSelected(true);
    }

    private class RadarDropTarget extends DropTarget {
        
        private static final long serialVersionUID = -2749068600723364921L;

        @Override
        public void dragEnter(DropTargetDragEvent e) { 
            
            GuiRadarContact contact=null;
            try {
                String callsign = (String) e.getTransferable().getTransferData(new DataFlavor(java.lang.String.class,"text/plain"));
                contact = master.getRadarContactManager().getContactFor(callsign);
            } catch (Exception e1) {
                log.error("Handled exception while reading drag and drop data"+ e1.getMessage());
            } 

            if(contact!=null && contact.getFlightPlan().isOwnedByMe()) {
                // show handover target dialog
                if(!handoverDialog.isVisible()) {
                    handoverDialog = handoverDialog==null ? new HandoverTargetDialog(master) : handoverDialog;
                    MouseEvent dummyEvent = new MouseEvent(radarView.getParent(), MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), MouseEvent.BUTTON1, (int)(getX()+ e.getLocation().getX()), (int)(getY()+e.getLocation().getY()), 1, false);
                    handoverDialog.setLocation(dummyEvent);
                }
            }
        }
    }
    
    private class MenuListenerMapUpdate implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem)e.getSource();
            if(item.getName().equals("UPDATE_1")) {
                master.getRadarProvider().setAntennaRotationTime(1*1000);
                master.getAirportData().setAntennaRotationTime(1000);
            } else if(item.getName().equals("UPDATE_3")) {
                master.getRadarProvider().setAntennaRotationTime(3*1000);
                master.getAirportData().setAntennaRotationTime(3000);
            } else if(item.getName().equals("UPDATE_5")) {
                master.getRadarProvider().setAntennaRotationTime(5*1000);
                master.getAirportData().setAntennaRotationTime(5000);
            }
            master.getAirportData().storeAirportData(master); // save it!
        }
    }

    private class MenuListenerTailLength implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem)e.getSource();
            if(item.getName().equals("TAIL_OFF")) {
                radarView.setMaxTailLength(0);
                master.getAirportData().setContactTailLength(0);
            } else if(item.getName().equals("TAIL_5")) {
                radarView.setMaxTailLength(5);
                master.getAirportData().setContactTailLength(5);
            } else if(item.getName().equals("TAIL_10")) {
                    radarView.setMaxTailLength(10);
                    master.getAirportData().setContactTailLength(10);
            }
            master.getAirportData().storeAirportData(master); // save it!
        }
    }

}
