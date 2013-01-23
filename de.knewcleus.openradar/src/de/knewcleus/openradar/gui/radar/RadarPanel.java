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
package de.knewcleus.openradar.gui.radar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;

/**
 * The panel containing the radar components...
 * 
 * @author Wolfram Wagner
 */
public class RadarPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private GuiMasterController guiInteractionManager;
    
    private javax.swing.JPanel radarControlBar;
    private RadarMapPanel radarView;

    private javax.swing.JLabel lbFIX;
    private javax.swing.JLabel lbNDB;    
    private javax.swing.JLabel lbVOR;    
    private javax.swing.JLabel lbCircles;
    private javax.swing.JLabel lbApt;
    private javax.swing.JLabel lbPPN;
    private javax.swing.JLabel lbGsH;
    
    private JPanel zoomPanel;
    private javax.swing.JLabel lbZoomGround;
    private javax.swing.JLabel lbZoomTower;
    private javax.swing.JLabel lbZoomApp;
    private javax.swing.JLabel lbZoomSector;
    
    private JTextField tfSearchNavaids;
    
    /**
     * Creates new form RadarPanel
     */
    public RadarPanel(GuiMasterController guiInteractionManager) {
        this.guiInteractionManager=guiInteractionManager;
        guiInteractionManager.getRadarBackend().setPanel(this);
        initComponents();
    }

    public RadarMapPanel getRadarMapPanel() {
        return radarView;
    }
    
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        radarView = new RadarMapPanel(guiInteractionManager);
        
        lbFIX = new javax.swing.JLabel();
        lbNDB = new javax.swing.JLabel();
        lbVOR = new javax.swing.JLabel();
        lbCircles = new javax.swing.JLabel();
        lbApt = new javax.swing.JLabel();
        lbPPN = new javax.swing.JLabel();
        lbGsH = new javax.swing.JLabel();
        
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
        gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx=1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        radarControlBar.add(filterPanel, gridBagConstraints);
        
        
        lbFIX.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbFIX.setText("FIX");
        lbFIX.setName("FIX");
        lbFIX.setToolTipText("Toggle display of FIX");
        lbFIX.setForeground(java.awt.Color.white);
        lbFIX.addMouseListener(guiInteractionManager.getRadarManager().getObjectFilterListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        filterPanel.add(lbFIX, gridBagConstraints);
        
        lbNDB.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbNDB.setText("NDB");
        lbNDB.setName("NDB");
        lbNDB.setToolTipText("Toggle display of NDB");
        lbNDB.setForeground(java.awt.Color.white);
        lbNDB.addMouseListener(guiInteractionManager.getRadarManager().getObjectFilterListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        filterPanel.add(lbNDB, gridBagConstraints);

        lbVOR.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbVOR.setText("VOR");
        lbVOR.setName("VOR");
        lbVOR.setToolTipText("Toggle display of VORs");
        lbVOR.setForeground(java.awt.Color.white);
        lbVOR.addMouseListener(guiInteractionManager.getRadarManager().getObjectFilterListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        filterPanel.add(lbVOR, gridBagConstraints);
        
        lbCircles.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbCircles.setText("CIRC");
        lbCircles.setName("CIRCLES");
        lbCircles.setToolTipText("Toggle display of distance circles around airport");
        lbCircles.setForeground(java.awt.Color.white);
        lbCircles.addMouseListener(guiInteractionManager.getRadarManager().getObjectFilterListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        filterPanel.add(lbCircles, gridBagConstraints);
        
        lbApt.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbApt.setText("APT");
        lbApt.setName("APT");
        lbApt.setToolTipText("Toggle display of airport codes");
        lbApt.setForeground(java.awt.Color.white);
        lbApt.addMouseListener(guiInteractionManager.getRadarManager().getObjectFilterListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        filterPanel.add(lbApt, gridBagConstraints);
        
        lbPPN.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbPPN.setText("PPN");
        lbPPN.setName("PPN");
        lbPPN.setToolTipText("Toggle display of numbers for park positions");
        lbPPN.setForeground(java.awt.Color.white);
        lbPPN.addMouseListener(guiInteractionManager.getRadarManager().getObjectFilterListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        filterPanel.add(lbPPN, gridBagConstraints);
 
        lbGsH.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbGsH.setText("GsH");
        lbGsH.setName("GSH");
        lbGsH.setToolTipText("Toggle display of Glide slope heights");
        lbGsH.setForeground(java.awt.Color.white);
        lbGsH.addMouseListener(guiInteractionManager.getRadarManager().getObjectFilterListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        filterPanel.add(lbGsH, gridBagConstraints);
 
        JPanel filterSpace = new JPanel();
        filterSpace.setLayout(new java.awt.GridBagLayout());
        filterSpace.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx=1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        filterPanel.add(filterSpace, gridBagConstraints);

        
        
        zoomPanel = new JPanel();
        zoomPanel.setLayout(new java.awt.GridBagLayout());
        zoomPanel.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx=0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        radarControlBar.add(zoomPanel, gridBagConstraints);

        lbZoomGround.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbZoomGround.setText("GROUND");
        lbZoomGround.setName("GROUND");
        lbZoomGround.setToolTipText("left click to choose, middle click to define");
        lbZoomGround.setForeground(java.awt.Color.white);
        lbZoomGround.addMouseListener(guiInteractionManager.getRadarManager().getZoomMouseListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        zoomPanel.add(lbZoomGround, gridBagConstraints);

        lbZoomTower.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbZoomTower.setText("TOWER");
        lbZoomTower.setName("TOWER");
        lbZoomTower.setForeground(java.awt.Color.blue);
        lbZoomTower.setToolTipText("left click to choose, middle click to define");
        lbZoomTower.addMouseListener(guiInteractionManager.getRadarManager().getZoomMouseListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        zoomPanel.add(lbZoomTower, gridBagConstraints);

        lbZoomApp.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbZoomApp.setText("APP");
        lbZoomApp.setName("APP");
        lbZoomApp.setForeground(java.awt.Color.white);
        lbZoomApp.setToolTipText("left click to choose, middle click to define");
        lbZoomApp.addMouseListener(guiInteractionManager.getRadarManager().getZoomMouseListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        zoomPanel.add(lbZoomApp, gridBagConstraints);

        lbZoomSector.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbZoomSector.setText("SECTOR");
        lbZoomSector.setName("SECTOR");
        lbZoomSector.setToolTipText("left click to choose, middle/right click to define");
        lbZoomSector.setForeground(java.awt.Color.white);
        lbZoomSector.addMouseListener(guiInteractionManager.getRadarManager().getZoomMouseListener());
        zoomPanel.add(lbZoomSector, new java.awt.GridBagConstraints());

        JPanel space = new JPanel();
        space.setLayout(new java.awt.GridBagLayout());
        space.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill=GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx=2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        radarControlBar.add(space, gridBagConstraints);

        tfSearchNavaids = new JTextField();
        tfSearchNavaids.setFont(tfSearchNavaids.getFont().deriveFont(10));
        tfSearchNavaids.setToolTipText("Enter navaids/airport codes to find and highlight");
        //tfSearchNavaids.setForeground(java.awt.Color.white);
        tfSearchNavaids.addActionListener(guiInteractionManager.getRadarManager().getNavaidSearchActionListener());
        tfSearchNavaids.setPreferredSize(new Dimension(200,tfSearchNavaids.getFont().getSize()+10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
        radarControlBar.add(tfSearchNavaids, gridBagConstraints);
    }

    public void resetFilters() {
        lbZoomGround.setForeground(Color.white);
        lbZoomTower.setForeground(Color.white);
        lbZoomApp.setForeground(Color.white);
        lbZoomSector.setForeground(Color.white);
    }
    
    public void selectFilter(String zoomLevelKey) {
        // reset
        lbZoomGround.setForeground(Color.white);
        lbZoomTower.setForeground(Color.white);
        lbZoomApp.setForeground(Color.white);
        lbZoomSector.setForeground(Color.white);

        // set
        for(Component c : zoomPanel.getComponents()) {
            if(zoomLevelKey.equals(c.getName())) {
              ((JLabel)c).setForeground(Color.blue);
            }
        }
    }

    public void setObjecFilter(javax.swing.JLabel l, boolean state) {
        if(state) {
            l.setForeground(Color.white);
        } else {
            l.setForeground(Color.gray);
        }
    }
    
    public void validateToggles() {
        setObjecFilter(lbFIX,guiInteractionManager.getDataRegistry().getRadarObjectFilterState("FIX"));
        setObjecFilter(lbNDB,guiInteractionManager.getDataRegistry().getRadarObjectFilterState("NDB"));
        setObjecFilter(lbVOR,guiInteractionManager.getDataRegistry().getRadarObjectFilterState("VOR"));
        setObjecFilter(lbCircles,guiInteractionManager.getDataRegistry().getRadarObjectFilterState("CIRCLES"));
        setObjecFilter(lbApt,guiInteractionManager.getDataRegistry().getRadarObjectFilterState("APT"));
        setObjecFilter(lbPPN,guiInteractionManager.getDataRegistry().getRadarObjectFilterState("PPN"));
        setObjecFilter(lbGsH,guiInteractionManager.getDataRegistry().getRadarObjectFilterState("GSH"));
    }
}
