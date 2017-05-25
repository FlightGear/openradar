/**
 * Copyright (C) 2012-2017 Wolfram Wagner
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

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import de.knewcleus.openradar.gui.radar.RadarMapPanel;
import de.knewcleus.openradar.gui.setup.AirportData;

/**
 * This is the application main window.
 * 
 * @author Wolfram Wagner
 */
public class MainFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 2623104404247180992L;

    private GuiMasterController master;
    private AirportData data;
    
    private JPanel jPnlContentPane = new JPanel();

    private de.knewcleus.openradar.gui.contacts.ContactsPanel contactsPanel;
    private javax.swing.JSplitPane hspMain;
    private de.knewcleus.openradar.gui.chat.MpChatPanel mpChatPanel;
    private javax.swing.JPanel pnlRightTop;
    private de.knewcleus.openradar.gui.radar.RadarPanel radarPanel;
    private de.knewcleus.openradar.gui.status.StatusPanel radioRunwayPanel;
    private javax.swing.JSplitPane vspLeft;
    private javax.swing.JSplitPane vspRight;

    private SplitPaneDividerListener dividerListener = new SplitPaneDividerListener(); 
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame(GuiMasterController master) {
        this.master = master;
        this.data = master.getAirportData();
        initComponents();
    }

    public RadarMapPanel getRadarScreen() {
        return radarPanel.getRadarMapPanel();
    }

    private void initComponents() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(master.getAirportData().getAirportCode() + " " + master.getAirportData().getAirportName() + " - OpenRadar");

        this.addWindowListener(new MainFrameListener());
        this.addComponentListener(new MovementListener());
        // add main view
        this.setContentPane(jPnlContentPane);

        jPnlContentPane.setMinimumSize(new java.awt.Dimension(800, 600));
        jPnlContentPane.setPreferredSize(new java.awt.Dimension(1280, 1050));
        jPnlContentPane.setLayout(new java.awt.GridBagLayout());
        jPnlContentPane.setBackground(Palette.DESKTOP);

        hspMain = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT, false);
        hspMain.setResizeWeight(1);

        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPnlContentPane.add(hspMain, gridBagConstraints);

        vspLeft = new javax.swing.JSplitPane();
        radarPanel = new de.knewcleus.openradar.gui.radar.RadarPanel(master);
        mpChatPanel = new de.knewcleus.openradar.gui.chat.MpChatPanel(master);
        vspRight = new javax.swing.JSplitPane();
        pnlRightTop = new javax.swing.JPanel();
        radioRunwayPanel = new de.knewcleus.openradar.gui.status.StatusPanel(master);
        contactsPanel = new de.knewcleus.openradar.gui.contacts.ContactsPanel(master);

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
        vspRight.setResizeWeight(0);
        vspRight.setMinimumSize(new java.awt.Dimension(450, 0));
        vspRight.setPreferredSize(new java.awt.Dimension(450, 0));
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
        
        hspMain.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,dividerListener);
        vspLeft.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,dividerListener);
        vspRight.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,dividerListener);
    }

    public void storeData(Properties p) {
    	p.setProperty("fgfs.mainFrame.bounds.x",""+getBounds().x);
    	p.setProperty("fgfs.mainFrame.bounds.y",""+getBounds().y);
    	p.setProperty("fgfs.mainFrame.bounds.width",""+getBounds().width);
    	p.setProperty("fgfs.mainFrame.bounds.height",""+getBounds().height);
        p.setProperty("fgfs.mainFrame.main", ""+hspMain.getDividerLocation());
        p.setProperty("fgfs.mainFrame.left", ""+vspLeft.getDividerLocation());
    }
    
    public void restoreWindowAndDividerPosition() {
    	if(data.getLastBounds()!=null) {
            setLocation(data.getLastBounds().x, data.getLastBounds().y);
            this.setSize(data.getLastBounds().width, data.getLastBounds().height);
    	} else {
	        // maximize it on center monitor
    		Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        	GraphicsDevice gds[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	        Rectangle maxBounds = AirportData.MAX_WINDOW_SIZE;
        	for(int i=0;i<gds.length;i++){
        		GraphicsDevice gd = gds[i];
        	    if(gd.getDefaultConfiguration().getBounds().contains(center)) {
    	    	    maxBounds = gd.getDefaultConfiguration().getBounds();
    	    	    break;
        	    }
        	}
	        
	        this.setLocation(maxBounds.x, maxBounds.y);
	        this.setSize(maxBounds.width, maxBounds.height);
	        this.setExtendedState(JFrame.MAXIMIZED_BOTH);    		
        }
    	if(data.getMainFrameDividerMainPos()>-1) hspMain.setDividerLocation(data.getMainFrameDividerMainPos());
    	if(data.getMainFrameDividerLeftPos()>-1) vspLeft.setDividerLocation(data.getMainFrameDividerLeftPos());
    }
    
    /**
     * Responsible for closing the dialogs when user returns to the main window.
     */
    private class MainFrameListener extends WindowAdapter {
        @Override
        public void windowActivated(WindowEvent e) {
            if(e.getOppositeWindow()!=null) {
                master.closeDialogs(true);
            }
        }
    }
    
    private class SplitPaneDividerListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent pce) {
        	// save new divider position
        	master.getAirportData().storeAirportData(master);
        };
    }

    private class MovementListener extends ComponentAdapter {
    	@Override
    	public void componentMoved(ComponentEvent e) {
    		storePosition();
    	}
    	@Override
    	public void componentResized(ComponentEvent e) {
    		// TODO Auto-generated method stub
    		storePosition();
    	}
    	
    	private void storePosition() {
        	// save new divider position
        	master.getAirportData().storeAirportData(master);
    	}
    }
}
