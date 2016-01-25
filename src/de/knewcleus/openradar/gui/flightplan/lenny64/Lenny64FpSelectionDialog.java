/**
 * Copyright (C) 2014 Wolfram Wagner
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
package de.knewcleus.openradar.gui.flightplan.lenny64;

import java.awt.Dimension;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.knewcleus.openradar.gui.contacts.FlightPlanDialog;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.setup.AirportData;

public class Lenny64FpSelectionDialog extends JDialog  implements FocusListener {

    private static final long serialVersionUID = 1L;
    private final Lenny64Controller lenny64Controller;
//    private volatile String callsign;
    private volatile List<FlightPlanData> existingFPs;

    private DialogMouseListener mouseListener = new DialogMouseListener();
    
    private final JPanel pnlMessages = new JPanel();
    private JScrollPane spList;

    private JLabel lbCallSign = new JLabel();
    
    public Lenny64FpSelectionDialog(Lenny64Controller lenny64Controller, FlightPlanDialog flightPlanDialog) {
        super(flightPlanDialog);
        this.lenny64Controller = lenny64Controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("OpenRadar - Select FP!");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        this.addFocusListener(this);
        
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


        // Determine what the default GraphicsDevice can support.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        boolean isUniformTranslucencySupported = gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT);
        if(isUniformTranslucencySupported) {
            this.setOpacity(0.92f);
        }


        JPanel jPnlContentPane = new JPanel();
        jPnlContentPane.setOpaque(false);
        jPnlContentPane.setLayout(new GridBagLayout());
        setContentPane(jPnlContentPane);


        //lbCallSign
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 4, 8);
        jPnlContentPane.add(lbCallSign, gridBagConstraints);

        spList = new JScrollPane();
        spList.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 2);
        jPnlContentPane.add(spList, gridBagConstraints);

        spList.setViewportView(pnlMessages);

        pnlMessages.setLayout(new GridBagLayout());

    }

    
    public synchronized void show(String callsign, List<FlightPlanData> existingFPs) {
//        this.callsign = callsign;
        this.existingFPs = existingFPs;
        
        lbCallSign.setText("Please select a FP for "+callsign);

        pnlMessages.removeAll();
        
        for(int i=0;i<existingFPs.size();i++) {
            GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);

            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            JLabel lb = new JLabel(existingFPs.get(i).getTextForSelection());
            lb.setName(""+i);
            lb.addMouseListener(mouseListener);
            pnlMessages.add(lb, gridBagConstraints);
        }
        pnlMessages.setOpaque(false);
        pack();
        doLayout();
    }
    
    public void setLocation(MouseEvent e) {

        Rectangle maxBounds = AirportData.MAX_WINDOW_SIZE;

        // size scrollpane
        Dimension preferredSize = pnlMessages.getPreferredSize();
        spList.setPreferredSize(preferredSize.getHeight()>maxBounds.getHeight() ?
                new Dimension(pnlMessages.getPreferredSize().width+30, maxBounds.height-40) : pnlMessages.getPreferredSize());

        // side dialog
        Dimension innerSize = getContentPane().getPreferredSize();
        setSize(new Dimension((int)innerSize.getWidth()+8, (int)innerSize.getHeight()+8));

        Point2D p;
        if(e!=null) {
            p = /*e.getSource() instanceof JList ?
                    ((JComponent) e.getSource()).getLocationOnScreen():*/
                    e.getLocationOnScreen();
            p = new Point2D.Double(p.getX() - 100 , p.getY()); //- this.getWidth() - 10
        } else {
            double x = maxBounds.getCenterX()-innerSize.getWidth()/2;
            double y = maxBounds.getCenterY()-innerSize.getHeight()/2;
            
            p = new Point2D.Double(x, y);
        }
            
        int lowerDistanceToScreenBorder=50;
        if(p.getY()+getHeight()>maxBounds.getHeight()-lowerDistanceToScreenBorder) {
            p = new Point2D.Double(p.getX(), maxBounds.getHeight()-getHeight() - lowerDistanceToScreenBorder);
        }
        setLocation(new Point((int) p.getX(), (int) p.getY()));
        setVisible(true);
        invalidate();
    }

   
   // focus listener
    
    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
        setVisible(false);
    }

    private class DialogMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            // list entry selected
            if(e.getSource() instanceof JLabel && e.getClickCount()==1) {
                int clickedIndex = Integer.parseInt(((JLabel)e.getSource()).getName());
                lenny64Controller.mergeFlightplans(existingFPs.get(clickedIndex));
                setVisible(false);
            }
        }        
    }
}
