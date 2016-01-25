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
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne
 * die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public
 * License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 * <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.knewcleus.openradar.gui.radar.RadarMapPanel;
import de.knewcleus.openradar.gui.setup.AirportData;

/**
 * This is the application main window.
 * 
 * @author Wolfram Wagner
 */
public class MainFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 2623104404247180992L;

    private GuiMasterController guiInteractionManager;

    private JPanel jPnlContentPane = new JPanel();

    private de.knewcleus.openradar.gui.contacts.ContactsPanel contactsPanel;
    private javax.swing.JSplitPane hspMain;
    private ResizeListener resizeListener = new ResizeListener();
    private de.knewcleus.openradar.gui.chat.MpChatPanel mpChatPanel;
    private javax.swing.JPanel pnlRightTop;
    private de.knewcleus.openradar.gui.radar.RadarPanel radarPanel;
    private de.knewcleus.openradar.gui.status.StatusPanel radioRunwayPanel;
    private javax.swing.JSplitPane vspLeft;
    private javax.swing.JSplitPane vspRight;

    /**
     * Creates new form MainFrame
     */
    public MainFrame(GuiMasterController guiInteractionManager) {
        this.guiInteractionManager = guiInteractionManager;
        initComponents();
    }

    public RadarMapPanel getRadarScreen() {
        return radarPanel.getRadarMapPanel();
    }

    private void initComponents() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(guiInteractionManager.getAirportData().getAirportCode() + " " + guiInteractionManager.getAirportData().getAirportName() + " - OpenRadar");

        // maximize it
        Rectangle maxBounds = AirportData.MAX_WINDOW_SIZE;
        ;
        this.setLocation(0, 0);
        this.setSize(maxBounds.width, maxBounds.height);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.addWindowListener(new MainFrameListener());
        // add main view
        this.setContentPane(jPnlContentPane);

        jPnlContentPane.setMinimumSize(new java.awt.Dimension(800, 600));
        jPnlContentPane.setPreferredSize(new java.awt.Dimension(1280, 1050));
        jPnlContentPane.setLayout(new java.awt.GridBagLayout());
        jPnlContentPane.setBackground(Palette.DESKTOP);

        hspMain = new javax.swing.JSplitPane();
        hspMain.addComponentListener(resizeListener);

        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPnlContentPane.add(hspMain, gridBagConstraints);

        vspLeft = new javax.swing.JSplitPane();
        radarPanel = new de.knewcleus.openradar.gui.radar.RadarPanel(guiInteractionManager);
        mpChatPanel = new de.knewcleus.openradar.gui.chat.MpChatPanel(guiInteractionManager);
        vspRight = new javax.swing.JSplitPane();
        pnlRightTop = new javax.swing.JPanel();
        radioRunwayPanel = new de.knewcleus.openradar.gui.status.StatusPanel(guiInteractionManager);
        contactsPanel = new de.knewcleus.openradar.gui.contacts.ContactsPanel(guiInteractionManager);

        // Left MAIN split pane

        vspLeft.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        vspLeft.setResizeWeight(1.0);
        vspLeft.setForeground(Palette.DESKTOP);
        hspMain.setLeftComponent(vspLeft);

        // Left top: Radar panel

        radarPanel.setMinimumSize(new java.awt.Dimension(400, 400));
        vspLeft.setTopComponent(radarPanel);

        // Left botton: MP Chat

        mpChatPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        vspLeft.setBottomComponent(mpChatPanel);

        // Right MAIN split pane

        vspRight.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        vspRight.setResizeWeight(0.5);
        vspRight.setMinimumSize(new java.awt.Dimension(400, 0));
        vspRight.setPreferredSize(new java.awt.Dimension(400, 0));
        hspMain.setRightComponent(vspRight);

        // Right top: Radios, Details of wind and runways

        pnlRightTop.setLayout(new java.awt.GridBagLayout());
        pnlRightTop.setBackground(Palette.DESKTOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        pnlRightTop.add(radioRunwayPanel, gridBagConstraints);

        vspRight.setTopComponent(pnlRightTop);

        // Right bottom

        vspRight.setBottomComponent(contactsPanel);
    }

    /**
     * Adapts divider position, when window is being resized or moved to another screen.
     */
    public class ResizeListener extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            setDividerPosition();
        }
    }

    public void setDividerPosition() {
        Dimension windowSize = getSize();
        Dimension dim = pnlRightTop.getPreferredSize();
        hspMain.setDividerLocation((int) Math.round(windowSize.getWidth() - dim.getWidth()));
        vspLeft.setDividerLocation((int) Math.round(windowSize.getHeight() * 0.8));
        guiInteractionManager.getStatusManager().updateRunways();
        guiInteractionManager.getRadarBackend().forceRepaint();
    }

    /**
     * Responsible for closing the dialogs when user returns to the main window.
     */
    private class MainFrameListener extends WindowAdapter {
        @Override
        public void windowActivated(WindowEvent e) {
            if(e.getOppositeWindow()!=null) {
                guiInteractionManager.closeDialogs(true);
            }
        }
    }
}
