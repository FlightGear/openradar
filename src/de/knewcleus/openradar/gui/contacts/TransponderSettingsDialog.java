/**
 * Copyright (C) 2012,2013-2015 Wolfram Wagner
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
package de.knewcleus.openradar.gui.contacts;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.flightplan.SquawkCodeManagerOld;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.util.RegexTextField;
//import de.knewcleus.openradar.util.BetterTextField;

public class TransponderSettingsDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final GuiMasterController master;
//    private final RadarContactController controller;

    private JLabel lbTransitionAlt = new JLabel("Transition Altitude");
    private RegexTextField tfTransitionAlt;
    private JLabel lbTransitionLayerWidth = new JLabel("Min. Width of Transition Layer");
    private RegexTextField tfTransitionLayerWidth ;
    private JCheckBox cbManualTL = new JCheckBox("Manual TL");
    private JLabel lbManualTransitionLevel = new JLabel("Manual, fix Transition Level (TL)");
    private RegexTextField tfFixTransitionLevel;
    private JLabel lbVFRCode = new JLabel("VFR code");
    private JLabel lbIFRCode = new JLabel("IFR code");
    private JLabel lbVFR = new JLabel("VFR range");
    private JLabel lbIFR = new JLabel("IFR range");
    private JTextField tfVFRCode = new JTextField(4);
    private JTextField tfIFRCode = new JTextField(4);
    private JTextField tfSquawkFromVFR = new JTextField(4);
    private JTextField tfSquawkToVFR = new JTextField(4);
    private JTextField tfSquawkFromIFR = new JTextField(4);
    private JTextField tfSquawkToIFR = new JTextField(4);

    private SquawkCodeListener squawkCodeListener = new SquawkCodeListener();
    private SquawkCodeManagerOld squawkCodeManager;

    private Logger log = Logger.getLogger(TransponderSettingsDialog.class);
    
    public TransponderSettingsDialog(GuiMasterController master, RadarContactController controller) {
        this.master = master;
//        this.controller = controller;
        this.squawkCodeManager = master.getAirportData().getSquawkCodeManager();
        try {
            tfTransitionAlt = new RegexTextField(3, "^\\d{0,3}$");
            tfTransitionLayerWidth = new RegexTextField(2, "^\\d{0,2}$");
            tfFixTransitionLevel = new RegexTextField(3, "^\\d{0,3}$");
        } catch(Exception e) {
            log.error(e.getMessage());
        }
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
        if(isUniformTranslucencySupported) {
            this.setOpacity(0.92f);
        }

        setLayout(new GridBagLayout());

        setForeground(Palette.DESKTOP_TEXT);
        setBackground(Palette.DESKTOP);

        JPanel jPnlTransition = new JPanel();
        jPnlTransition.setOpaque(false);
        jPnlTransition.setLayout(new GridBagLayout());
        jPnlTransition.setBorder(new TitledBorder("Transition: ft <=> FL"));

        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(jPnlTransition, gridBagConstraints);

        // ---------------------------
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlTransition.add(lbTransitionAlt, gridBagConstraints);
        
        tfTransitionAlt.setToolTipText("Altitude to switch from ATIS airpressure to standard pressure while climbing.");
        tfTransitionAlt.addKeyListener(squawkCodeListener);
        tfTransitionAlt.setHorizontalAlignment(JTextField.RIGHT);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth=1;
        gridBagConstraints.weightx=1.0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlTransition.add(tfTransitionAlt, gridBagConstraints);

        JLabel lbFt1 = new JLabel("00 ft");
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth=1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlTransition.add(lbFt1, gridBagConstraints);
        

        // ---------------------------
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlTransition.add(lbTransitionLayerWidth, gridBagConstraints);
        
        tfTransitionLayerWidth.setToolTipText("The minimum separation between a plane flying TA and another flying TL (at least 500 or better 1000 ft)");
        tfTransitionLayerWidth.addKeyListener(squawkCodeListener);
        tfTransitionLayerWidth.setHorizontalAlignment(JTextField.RIGHT);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth=1;
        gridBagConstraints.weightx=1.0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlTransition.add(tfTransitionLayerWidth, gridBagConstraints);
        
        JLabel lbFt2 = new JLabel("00 ft");
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth=1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlTransition.add(lbFt2, gridBagConstraints);
        
        // ---------------------------
        
        cbManualTL.setToolTipText("Disables automatic update for every air pressure change. Use it only, if the airport really uses a fixed TL!");
        cbManualTL.addChangeListener(new ManualTLListener());
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlTransition.add(cbManualTL, gridBagConstraints);

        // ---------------------------
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlTransition.add(lbManualTransitionLevel, gridBagConstraints);
        
        JLabel lbFL = new JLabel("FL");
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth=1;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlTransition.add(lbFL, gridBagConstraints);
        
        tfFixTransitionLevel.setToolTipText("The minimum usable flightlevel above TA. Be aware, that YOU ARE RESPONSIBLE to define this correctly, if you disable automatic update!");
        tfFixTransitionLevel.addKeyListener(squawkCodeListener);
        tfFixTransitionLevel.setHorizontalAlignment(JTextField.RIGHT);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth=1;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlTransition.add(tfFixTransitionLevel, gridBagConstraints);

        // squawk
        
        JPanel jPnlSquawk = new JPanel();
        jPnlSquawk.setOpaque(false);
        jPnlSquawk.setLayout(new GridBagLayout());
        jPnlSquawk.setBorder(new TitledBorder("Squawk settings"));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        add(jPnlSquawk, gridBagConstraints);

        // squawk codes

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlSquawk.add(lbVFRCode, gridBagConstraints);

        tfVFRCode.setToolTipText("The default VFR code for uncontrolled flights");
        tfVFRCode.addKeyListener(squawkCodeListener);
        tfVFRCode.setHorizontalAlignment(JTextField.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlSquawk.add(tfVFRCode, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlSquawk.add(lbIFRCode, gridBagConstraints);

        tfIFRCode.setToolTipText("The default IFR code for uncontrolled flights");
        tfIFRCode.addKeyListener(squawkCodeListener);
        tfIFRCode.setHorizontalAlignment(JTextField.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlSquawk.add(tfIFRCode, gridBagConstraints);

        // squawk ranges
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlSquawk.add(lbVFR, gridBagConstraints);

        tfSquawkFromVFR.setToolTipText("lowest VFR squawk code to assign");
        tfSquawkFromVFR.addKeyListener(squawkCodeListener);
        tfSquawkFromVFR.setHorizontalAlignment(JTextField.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlSquawk.add(tfSquawkFromVFR, gridBagConstraints);

        tfSquawkToVFR.setToolTipText("greatest VFR squawk code to assign");
        tfSquawkToVFR.addKeyListener(squawkCodeListener);
        tfSquawkToVFR.setHorizontalAlignment(JTextField.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        jPnlSquawk.add(tfSquawkToVFR, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlSquawk.add(lbIFR, gridBagConstraints);

        tfSquawkFromIFR.setToolTipText("lowest IFR squawk code to assign");
        tfSquawkFromIFR.addKeyListener(squawkCodeListener);
        tfSquawkFromIFR.setHorizontalAlignment(JTextField.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlSquawk.add(tfSquawkFromIFR, gridBagConstraints);

        tfSquawkToIFR.setToolTipText("greatest IFR squawk code to assign");
        tfSquawkToIFR.addKeyListener(squawkCodeListener);
        tfSquawkToIFR.setHorizontalAlignment(JTextField.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlSquawk.add(tfSquawkToIFR, gridBagConstraints);
        
        pack();
    }

    public void show(MouseEvent e) {

        tfTransitionAlt.setText(""+master.getAirportData().getTransitionAlt()/100);
        tfTransitionLayerWidth.setValue(""+master.getAirportData().getTransitionLayerWidth()/100);
        tfFixTransitionLevel.setText(""+master.getAirportData().getTransitionLevelFix());
        if(master.getAirportData().isManualTransitionLevel()) {
            cbManualTL.setSelected(true);
            lbTransitionLayerWidth.setEnabled(false);
            tfTransitionLayerWidth.setEnabled(false);
            lbManualTransitionLevel.setEnabled(true);
            tfFixTransitionLevel.setEnabled(true);
        } else {
            cbManualTL.setSelected(false);
            lbTransitionLayerWidth.setEnabled(true);
            tfTransitionLayerWidth.setEnabled(true);
            lbManualTransitionLevel.setEnabled(false);
            tfFixTransitionLevel.setEnabled(false);
        }
        
        tfVFRCode.setText(""+squawkCodeManager.getVfrCode());
        tfIFRCode.setText(""+squawkCodeManager.getIfrCode());
        
        tfSquawkFromVFR.setText(""+squawkCodeManager.getSquawkFromVFR());
        tfSquawkToVFR.setText(""+squawkCodeManager.getSquawkToVFR());
        tfSquawkFromIFR.setText(""+squawkCodeManager.getSquawkFromIFR());
        tfSquawkToIFR.setText(""+squawkCodeManager.getSquawkToIFR());

        Dimension innerSize = getPreferredSize();
        setSize(new Dimension((int)innerSize.getWidth()+8, (int)innerSize.getHeight()+8));
        Rectangle maxBounds = AirportData.MAX_WINDOW_SIZE;

        Point2D p = e.getLocationOnScreen();// ((JComponent) e.getSource()).getLocationOnScreen();
        p = new Point2D.Double(p.getX() - this.getWidth() - 10, p.getY());

        int lowerDistanceToScreenBorder=50;
        if(p.getY()+getHeight()>maxBounds.getHeight()-lowerDistanceToScreenBorder) {
            p = new Point2D.Double(p.getX(), maxBounds.getHeight()-getHeight() - lowerDistanceToScreenBorder);
        }
        setLocation(new Point((int) p.getX(), (int) p.getY()));
        doLayout();
        setVisible(true);
        tfSquawkFromVFR.requestFocus();
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
        if(isVisible()) {
            try {
                master.getAirportData().setTransitionAlt(master,Integer.parseInt(tfTransitionAlt.getText())*100);
                if(cbManualTL.isSelected()) {
                    master.getAirportData().setManualTransitionLevel(true);
                    master.getAirportData().setTransitionLevelFix(Integer.parseInt(tfFixTransitionLevel.getText()));
                } else {
                    master.getAirportData().setManualTransitionLevel(false);
                    master.getAirportData().setTransitionLayerWidth(Integer.parseInt(tfTransitionLayerWidth.getText())*100);
                }
                
                master.getStatusManager().updateTransitionValues();
            } catch(Exception e) {
                return;
            }
            try {
                int iVFRCode = Integer.parseInt(tfVFRCode.getText());
                int iIFRCode = Integer.parseInt(tfIFRCode.getText());
                squawkCodeManager.setVfrCode(iVFRCode);
                squawkCodeManager.setIfrCode(iIFRCode);
                
                int squawkFromVFR = Integer.parseInt(tfSquawkFromVFR.getText());
                int squawkToVFR = Integer.parseInt(tfSquawkToVFR.getText());
                int squawkFromIFR = Integer.parseInt(tfSquawkFromIFR.getText());
                int squawkToIFR = Integer.parseInt(tfSquawkToIFR.getText());
                if( squawkFromVFR<squawkToVFR &&  squawkFromIFR<squawkToIFR) {
                    squawkCodeManager.setSquawkRange(squawkFromVFR,squawkToVFR,squawkFromIFR,squawkToIFR);
                    master.getAirportData().storeAirportData(master); // save it!
                }
                setVisible(false);
            } catch(Exception e) {}
        }
    }

    private class SquawkCodeListener extends KeyAdapter {
        
        @Override
        public void keyTyped(KeyEvent e) {

            if(e.getKeyChar()==KeyEvent.VK_ENTER) {
                if(tfTransitionAlt.equals(e.getSource())) {
                    tfVFRCode.requestFocus();
                }
                if(tfVFRCode.equals(e.getSource())) {
                    tfIFRCode.requestFocus();
                }
                if(tfIFRCode.equals(e.getSource())) {
                    tfSquawkFromVFR.requestFocus();
                }
                if(tfSquawkFromVFR.equals(e.getSource())) {
                    tfSquawkToVFR.requestFocus();
                }
                if(tfSquawkToVFR.equals(e.getSource())) {
                    tfSquawkFromIFR.requestFocus();
                }
                if(tfSquawkFromIFR.equals(e.getSource())) {
                    tfSquawkToIFR.requestFocus();
                }
                else if(tfSquawkToIFR.equals(e.getSource())) {
                    if(verifyRange(e.getKeyChar())) {
                        closeDialog();
                    } else {
                        e.consume();
                    }
                }
            } else if(e.getKeyChar()==KeyEvent.VK_ESCAPE) {
                closeDialog();
            } 

            if(tfVFRCode.equals(e.getSource()) || tfIFRCode.equals(e.getSource()) || tfSquawkFromVFR.equals(e.getSource()) || tfSquawkToVFR.equals(e.getSource()) || tfSquawkFromIFR.equals(e.getSource()) || tfSquawkToIFR.equals(e.getSource())) {
                if(((JTextField)e.getSource()).getText().length()>=4 || !(""+e.getKeyChar()).matches("[0-7]")) {
                    e.consume();
                } 
                verifyRange(e.getKeyChar());
            }
        }
    }

    private boolean verifyRange(char key) {
        if(!Character.isDigit(key)) return true;

        tfSquawkFromVFR.setForeground(Palette.FPD_SQUAWK_OK);
        tfSquawkToVFR.setForeground(Palette.FPD_SQUAWK_OK);
        tfSquawkFromIFR.setForeground(Palette.FPD_SQUAWK_OK);
        tfSquawkToIFR.setForeground(Palette.FPD_SQUAWK_OK);
        
        String sKey = ""+key;
        if(!Character.isLetterOrDigit(key)) {
            sKey="";
        } 
        
        if(tfSquawkFromVFR.hasFocus() || tfSquawkToVFR.hasFocus()) {
            int squawkFromVFR;
            int squawkToVFR;

            // check values
//            if(!verifySquawkCodeRange(tfSquawkFromVFR,tfSquawkFromVFR.getText()+(tfSquawkFromVFR.hasFocus()?sKey:"")) 
//                    || !verifySquawkCodeRange(tfSquawkToVFR,tfSquawkToVFR.getText()+(tfSquawkToVFR.hasFocus()?sKey:""))) {
//                     return false;
//                 }
            
            // compose value including the key
            try {
                if(tfSquawkFromVFR.hasFocus()) {
                    squawkFromVFR = Integer.parseInt(tfSquawkFromVFR.getText()+sKey);
                    squawkToVFR = Integer.parseInt(tfSquawkToVFR.getText());
                } else {
                    squawkFromVFR = Integer.parseInt(tfSquawkFromVFR.getText());
                    squawkToVFR = Integer.parseInt(tfSquawkToVFR.getText()+sKey);
                }
                // verify that begin is less than end
                if(squawkFromVFR<squawkToVFR) {
                    tfSquawkFromVFR.setForeground(Palette.FPD_SQUAWK_OK);
                    tfSquawkToVFR.setForeground(Palette.FPD_SQUAWK_OK);
                    return true;
                } else {
                    tfSquawkFromVFR.setForeground(Palette.FPD_SQUAWK_ERROR);
                    tfSquawkToVFR.setForeground(Palette.FPD_SQUAWK_ERROR);
                    return false;
                }
            } catch(Exception e) {
                return false;
            }
            
        } else {
            int squawkFromIFR;
            int squawkToIFR;
            
            // check values
//            if(!verifySquawkCodeRange(tfSquawkFromIFR,tfSquawkFromIFR.getText()+(tfSquawkFromIFR.hasFocus()?sKey:""))
//                    || !verifySquawkCodeRange(tfSquawkToIFR,tfSquawkToIFR.getText()+(tfSquawkToIFR.hasFocus()?sKey:""))) {
//                     return false;
//            }
            
            // compose value including the key
            if(tfSquawkFromIFR.hasFocus()) {
                squawkFromIFR = Integer.parseInt(tfSquawkFromIFR.getText()+sKey);
                squawkToIFR = Integer.parseInt(tfSquawkToIFR.getText());
            } else {
                squawkFromIFR = Integer.parseInt(tfSquawkFromIFR.getText());
                squawkToIFR = Integer.parseInt(tfSquawkToIFR.getText()+sKey);
            }
            
            // verify that begin is less than end
            if(squawkFromIFR<squawkToIFR) {
                tfSquawkFromIFR.setForeground(Palette.FPD_SQUAWK_OK);
                tfSquawkToIFR.setForeground(Palette.FPD_SQUAWK_OK);
                return true;
            } else {
                tfSquawkFromIFR.setForeground(Palette.FPD_SQUAWK_ERROR);
                tfSquawkToIFR.setForeground(Palette.FPD_SQUAWK_ERROR);
                return false;
            }
        }
        
    }

    public boolean verifySquawkCodeRange(JTextField squawkField, String newContent) {
        if(newContent.matches("[0-7]{1,4}")) {
            squawkField.setForeground(Palette.FPD_SQUAWK_OK);
            return true;
        } else {
            squawkField.setForeground(Palette.FPD_SQUAWK_ERROR);
            return false;
        }
    }
    
    class ManualTLListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent arg0) {
            if(cbManualTL.isSelected()) {
                lbTransitionLayerWidth.setEnabled(false);
                tfTransitionLayerWidth.setEnabled(false);
                lbManualTransitionLevel.setEnabled(true);
                tfFixTransitionLevel.setEnabled(true);
            } else {
                lbTransitionLayerWidth.setEnabled(true);
                tfTransitionLayerWidth.setEnabled(true);
                lbManualTransitionLevel.setEnabled(false);
                tfFixTransitionLevel.setEnabled(false);
            }
        }

    }

}
