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

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import de.knewcleus.openradar.flightplan.SquawkCodeManager;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;

public class TransponderSettingsDialog extends JFrame {

    private static final long serialVersionUID = 1L;
    private final GuiMasterController master;
//    private final RadarContactController controller;

    private JLabel lbCallSign = new JLabel("Squawk range");
    private JTextField tfSquawkFrom = new JTextField();
    private JTextField tfSquawkTo = new JTextField();

    private SquawkCodeListener squawkCodeListener = new SquawkCodeListener();
    private SquawkCodeManager squawkCodeManager;

    public TransponderSettingsDialog(GuiMasterController master, RadarContactController controller) {
        this.master = master;
//        this.controller = controller;
        this.squawkCodeManager = master.getDataRegistry().getSquawkCodeManager();
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
            this.setOpacity(0.8f);
        }

        setLayout(new GridBagLayout());

        setForeground(Palette.DESKTOP_TEXT);
        setBackground(Palette.DESKTOP);

        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 4, 4);
        add(lbCallSign, gridBagConstraints);

        tfSquawkFrom.setToolTipText("lowest squawk code to assign");
        tfSquawkFrom.addKeyListener(squawkCodeListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 4, 8);
        add(tfSquawkFrom, gridBagConstraints);

        tfSquawkTo.setToolTipText("greatest squawk code to assign");
        tfSquawkTo.addKeyListener(squawkCodeListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 4, 4, 8);
        add(tfSquawkTo, gridBagConstraints);

    }

    public void show(MouseEvent e) {

        tfSquawkFrom.setText(""+squawkCodeManager.getSquawkFrom());
        tfSquawkTo.setText(""+squawkCodeManager.getSquawkTo());

        Dimension innerSize = getPreferredSize();
        setSize(new Dimension((int)innerSize.getWidth()+8, (int)innerSize.getHeight()+8));
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maxBounds = env.getMaximumWindowBounds();

        Point2D p = e.getLocationOnScreen();// ((JComponent) e.getSource()).getLocationOnScreen();
        p = new Point2D.Double(p.getX() - this.getWidth() - 10, p.getY());

        int lowerDistanceToScreenBorder=50;
        if(p.getY()+getHeight()>maxBounds.getHeight()-lowerDistanceToScreenBorder) {
            p = new Point2D.Double(p.getX(), maxBounds.getHeight()-getHeight() - lowerDistanceToScreenBorder);
        }
        setLocation(new Point((int) p.getX(), (int) p.getY()));
        doLayout();
        setVisible(true);
        tfSquawkFrom.requestFocus();
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
            int squawkFrom = Integer.parseInt(tfSquawkFrom.getText());
            int squawkTo = Integer.parseInt(tfSquawkTo.getText());
            squawkCodeManager.setSquawkRange(squawkFrom,squawkTo);
            master.getDataRegistry().storeAirportData(master); // save it!
            setVisible(false);
        }
    }

    private class SquawkCodeListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            if(e.getKeyChar()==KeyEvent.VK_ENTER) {
                if(tfSquawkFrom.equals(e.getSource())) {
                    tfSquawkTo.requestFocus();
                }
                else if(tfSquawkTo.equals(e.getSource())) {
                    closeDialog();
                }
            } else if(e.getKeyChar()==KeyEvent.VK_ESCAPE) {
                    closeDialog();
            } else  if(!Character.isDigit(e.getKeyChar())) {
                // don't accept characters
                e.consume();
            }
        }
    }

}
