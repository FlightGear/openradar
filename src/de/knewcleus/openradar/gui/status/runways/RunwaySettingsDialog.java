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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.gui.status.runways;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.gui.setup.RunwayData;

public class RunwaySettingsDialog extends JDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private GuiMasterController master;
    private AirportData data;

    private volatile RunwayData rwd = null;

    private JLabel lbRWNumber;
    private JButton btCopySettings;

    private JTextField tfCLStart;
    private JTextField tfCLEnd;
    private JTextField tfCLMinorDMStart;
    private JTextField tfCLMinorDMEnd;
    private JTextField tfCLMinorDMInterval;
    private JTextField tfCLMinorDMTickLength;
    private JTextField tfCLMajorDMStart;
    private JTextField tfCLMajorDMEnd;
    private JTextField tfCLMajorDMInterval;
    private JTextField tfCLMajorDMTickLength;

    private JCheckBox chbRwBiDirectional;
    private JCheckBox chbRwActiveForStarting;
    private JCheckBox chbRwActiveForLanding;

    private JCheckBox chbSymmetric;
    private JCheckBox chbDisplayRight;
    private JCheckBox chbDisplayLeft;
    private JTextField tfVectStartRight;
    private JTextField tfVectAngleRight;
    private JTextField tfVectLengthRight;
    private JTextField tfBaselegLengthRight;
    private JTextField tfVectStartLeft;
    private JTextField tfVectAngleLeft;
    private JTextField tfVectLengthLeft;
    private JTextField tfBaselegLengthLeft;

    private ChooseRunwayMouseListener chooseRunwayMouseListener = new ChooseRunwayMouseListener();

    private static Logger log = LogManager.getLogger(RunwaySettingsDialog.class);
    
    public RunwaySettingsDialog(GuiMasterController master) {
        this.master = master;
        this.data = master.getAirportData();
        initComponents();
    }

    public void setLocation(MouseEvent e) {

        Dimension innerSize = getContentPane().getPreferredSize();
        setSize(new Dimension((int) innerSize.getWidth() + 8, (int) innerSize.getHeight() + 8));
        Rectangle maxBounds = AirportData.MAX_WINDOW_SIZE;

        Point2D p = ((JComponent) e.getSource()).getLocationOnScreen();
        p = new Point2D.Double(p.getX() - this.getWidth() - 10, p.getY() - 100);

        int lowerDistanceToScreenBorder = 50;
        if (p.getY() + getHeight() > maxBounds.getHeight() - lowerDistanceToScreenBorder) {
            p = new Point2D.Double(p.getX(), maxBounds.getHeight() - getHeight() - lowerDistanceToScreenBorder);
        }
        setLocation(new Point((int) p.getX(), (int) p.getY()));
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

        JPanel jPnlContentPane = new JPanel();
        jPnlContentPane.setLayout(new GridBagLayout());
        setContentPane(jPnlContentPane);

        FieldFocusLostListener fieldFocusLostListener = new FieldFocusLostListener();

        // runway selection line
        JLabel lbBefore = new JLabel("<<");
        Font boldFont = lbBefore.getFont().deriveFont(Font.BOLD);
        lbBefore.setFont(boldFont);
        lbBefore.setName("<<");
        lbBefore.addMouseListener(chooseRunwayMouseListener);
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 24, 0, 2);
        jPnlContentPane.add(lbBefore, gridBagConstraints);

        lbRWNumber = new JLabel("");
        lbRWNumber.setFont(boldFont);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlContentPane.add(lbRWNumber, gridBagConstraints);

        JLabel lbRwNext = new JLabel(">>");
        lbRwNext.setFont(boldFont);
        lbRwNext.setName(">>");
        lbRwNext.addMouseListener(chooseRunwayMouseListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlContentPane.add(lbRwNext, gridBagConstraints);

        btCopySettings = new JButton("Copy to all");
        btCopySettings.setName("COPY");
        btCopySettings.setToolTipText("Copy settings to all other runways");
        btCopySettings.addMouseListener(chooseRunwayMouseListener);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 8, 2);
        jPnlContentPane.add(btCopySettings, gridBagConstraints);

//        JPanel space0 = new JPanel();
//        space0.setOpaque(false);
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 4;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.gridheight = 1;
//        gridBagConstraints.weightx = 1;
//        gridBagConstraints.anchor = GridBagConstraints.WEST;
//        jPnlContentPane.add(space0, gridBagConstraints);

        // Main switches

        JPanel jPnlMainSwitches = new JPanel();
        jPnlMainSwitches.setLayout(new GridBagLayout());
        jPnlMainSwitches.setBorder(new TitledBorder("Main switches"));
        jPnlMainSwitches.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlContentPane.add(jPnlMainSwitches, gridBagConstraints);

        chbRwBiDirectional = new JCheckBox("Bi-directional");
        chbRwBiDirectional.setToolTipText("Same runway end for landings and starts");
        chbRwBiDirectional.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMainSwitches.add(chbRwBiDirectional, gridBagConstraints);

        chbRwActiveForStarting = new JCheckBox("Starting");
        chbRwActiveForStarting.setToolTipText("Can runway be used for starts?");
        chbRwActiveForStarting.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMainSwitches.add(chbRwActiveForStarting, gridBagConstraints);

        chbRwActiveForLanding = new JCheckBox("Landing");
        chbRwActiveForLanding.setToolTipText("Can runway be used for landings?");
        chbRwActiveForLanding.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlMainSwitches.add(chbRwActiveForLanding, gridBagConstraints);

        // Extended center line

        JPanel jPnlExtCenterLine = new JPanel();
        jPnlExtCenterLine.setLayout(new GridBagLayout());
        jPnlExtCenterLine.setBorder(new TitledBorder("Extended center line (Unit: NM)"));
        jPnlExtCenterLine.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlContentPane.add(jPnlExtCenterLine, gridBagConstraints);

        JLabel lbECLStart = new JLabel("Start");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlExtCenterLine.add(lbECLStart, gridBagConstraints);

        tfCLStart = new JTextField();
        tfCLStart.setHorizontalAlignment(JTextField.RIGHT);
        tfCLStart.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfCLStart.addFocusListener(fieldFocusLostListener);
        tfCLStart.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlExtCenterLine.add(tfCLStart, gridBagConstraints);

        JLabel lbECLEnd = new JLabel("End");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlExtCenterLine.add(lbECLEnd, gridBagConstraints);

        tfCLEnd = new JTextField();
        tfCLEnd.setHorizontalAlignment(JTextField.RIGHT);
        tfCLEnd.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfCLEnd.addFocusListener(fieldFocusLostListener);
        tfCLEnd.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlExtCenterLine.add(tfCLEnd, gridBagConstraints);

        JPanel space1 = new JPanel();
        space1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.weightx = 100;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        jPnlExtCenterLine.add(space1, gridBagConstraints);

        // Distance markers

        JPanel jPnlDistanceMarkers = new JPanel();
        jPnlDistanceMarkers.setLayout(new GridBagLayout());
        jPnlDistanceMarkers.setBorder(new TitledBorder("Distance markers (Unit: NM)"));
        jPnlDistanceMarkers.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlContentPane.add(jPnlDistanceMarkers, gridBagConstraints);

        JLabel lbECLMinor = new JLabel("Minor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlDistanceMarkers.add(lbECLMinor, gridBagConstraints);

        JLabel lbECLMajor = new JLabel("Major");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlDistanceMarkers.add(lbECLMajor, gridBagConstraints);

        JLabel lbDmStart = new JLabel("Start distance");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlDistanceMarkers.add(lbDmStart, gridBagConstraints);

        tfCLMinorDMStart = new JTextField();
        tfCLMinorDMStart.setHorizontalAlignment(JTextField.RIGHT);
        tfCLMinorDMStart.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfCLMinorDMStart.addFocusListener(fieldFocusLostListener);
        tfCLMinorDMStart.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlDistanceMarkers.add(tfCLMinorDMStart, gridBagConstraints);

        tfCLMajorDMStart = new JTextField();
        tfCLMajorDMStart.setHorizontalAlignment(JTextField.RIGHT);
        tfCLMajorDMStart.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfCLMajorDMStart.addFocusListener(fieldFocusLostListener);
        tfCLMajorDMStart.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlDistanceMarkers.add(tfCLMajorDMStart, gridBagConstraints);

        JLabel lbDmEnd = new JLabel("End distance");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlDistanceMarkers.add(lbDmEnd, gridBagConstraints);

        tfCLMinorDMEnd = new JTextField();
        tfCLMinorDMEnd.setHorizontalAlignment(JTextField.RIGHT);
        tfCLMinorDMEnd.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfCLMinorDMEnd.addFocusListener(fieldFocusLostListener);
        tfCLMinorDMEnd.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlDistanceMarkers.add(tfCLMinorDMEnd, gridBagConstraints);

        tfCLMajorDMEnd = new JTextField();
        tfCLMajorDMEnd.setHorizontalAlignment(JTextField.RIGHT);
        tfCLMajorDMEnd.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfCLMajorDMEnd.addFocusListener(fieldFocusLostListener);
        tfCLMajorDMEnd.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlDistanceMarkers.add(tfCLMajorDMEnd, gridBagConstraints);

        JLabel lbECLInterval = new JLabel("Intervall");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlDistanceMarkers.add(lbECLInterval, gridBagConstraints);

        tfCLMinorDMInterval = new JTextField();
        tfCLMinorDMInterval.setHorizontalAlignment(JTextField.RIGHT);
        tfCLMinorDMInterval.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfCLMinorDMInterval.addFocusListener(fieldFocusLostListener);
        tfCLMinorDMInterval.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlDistanceMarkers.add(tfCLMinorDMInterval, gridBagConstraints);

        tfCLMajorDMInterval = new JTextField();
        tfCLMajorDMInterval.setHorizontalAlignment(JTextField.RIGHT);
        tfCLMajorDMInterval.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfCLMajorDMInterval.addFocusListener(fieldFocusLostListener);
        tfCLMajorDMInterval.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlDistanceMarkers.add(tfCLMajorDMInterval, gridBagConstraints);

        JLabel lbECLMLength = new JLabel("Tick Length:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlDistanceMarkers.add(lbECLMLength, gridBagConstraints);

        tfCLMinorDMTickLength = new JTextField();
        tfCLMinorDMTickLength.setHorizontalAlignment(JTextField.RIGHT);
        tfCLMinorDMTickLength.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfCLMinorDMTickLength.addFocusListener(fieldFocusLostListener);
        tfCLMinorDMTickLength.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlDistanceMarkers.add(tfCLMinorDMTickLength, gridBagConstraints);

        tfCLMajorDMTickLength = new JTextField();
        tfCLMajorDMTickLength.setHorizontalAlignment(JTextField.RIGHT);
        tfCLMajorDMTickLength.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfCLMajorDMTickLength.addFocusListener(fieldFocusLostListener);
        tfCLMajorDMTickLength.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlDistanceMarkers.add(tfCLMajorDMTickLength, gridBagConstraints);

        JPanel space2 = new JPanel();
        space2.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        jPnlDistanceMarkers.add(space2, gridBagConstraints);

        // Vectoring & Baseleg

        JPanel jPnlVectoring = new JPanel();
        jPnlVectoring.setLayout(new GridBagLayout());
        jPnlVectoring.setBorder(new TitledBorder("Vectoring and base leg (Unit: NM)"));
        jPnlVectoring.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlContentPane.add(jPnlVectoring, gridBagConstraints);

        chbSymmetric = new JCheckBox("symmetric");
        chbSymmetric.setToolTipText("Check if both sides should be symetric");
        chbSymmetric.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(chbSymmetric, gridBagConstraints);

        JLabel lbLeft = new JLabel("left");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(lbLeft, gridBagConstraints);

        JLabel lbRight = new JLabel("right");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(lbRight, gridBagConstraints);

        JLabel lbDisplay = new JLabel("Display");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(lbDisplay, gridBagConstraints);

        chbDisplayLeft = new JCheckBox();
        chbDisplayLeft.addFocusListener(fieldFocusLostListener);
        chbDisplayLeft.setToolTipText("Display left side of Vectoring&Baseleg");
        chbDisplayLeft.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(chbDisplayLeft, gridBagConstraints);

        chbDisplayRight = new JCheckBox();
        chbDisplayRight.addFocusListener(fieldFocusLostListener);
        chbDisplayRight.setToolTipText("Display right side of Vectoring&Baseleg");
        chbDisplayRight.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(chbDisplayRight, gridBagConstraints);

        JLabel lbVStart = new JLabel("Vectoring Start");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(lbVStart, gridBagConstraints);

        tfVectStartLeft = new JTextField();
        tfVectStartLeft.setHorizontalAlignment(JTextField.RIGHT);
        tfVectStartLeft.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfVectStartLeft.addFocusListener(fieldFocusLostListener);
        tfVectStartLeft.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(tfVectStartLeft, gridBagConstraints);

        tfVectStartRight = new JTextField();
        tfVectStartRight.setHorizontalAlignment(JTextField.RIGHT);
        tfVectStartRight.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfVectStartRight.addFocusListener(fieldFocusLostListener);
        tfVectStartRight.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(tfVectStartRight, gridBagConstraints);

        JLabel lbVAngle = new JLabel("Vectoring Angle");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(lbVAngle, gridBagConstraints);

        tfVectAngleLeft = new JTextField();
        tfVectAngleLeft.setHorizontalAlignment(JTextField.RIGHT);
        tfVectAngleLeft.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfVectAngleLeft.addFocusListener(fieldFocusLostListener);
        tfVectAngleLeft.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(tfVectAngleLeft, gridBagConstraints);

        tfVectAngleRight = new JTextField();
        tfVectAngleRight.setHorizontalAlignment(JTextField.RIGHT);
        tfVectAngleRight.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfVectAngleRight.addFocusListener(fieldFocusLostListener);
        tfVectAngleRight.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(tfVectAngleRight, gridBagConstraints);

        JLabel lbVLength = new JLabel("Vectoring Length");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(lbVLength, gridBagConstraints);

        tfVectLengthLeft = new JTextField();
        tfVectLengthLeft.setHorizontalAlignment(JTextField.RIGHT);
        tfVectLengthLeft.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfVectLengthLeft.addFocusListener(fieldFocusLostListener);
        tfVectLengthLeft.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(tfVectLengthLeft, gridBagConstraints);

        tfVectLengthRight = new JTextField();
        tfVectLengthRight.setHorizontalAlignment(JTextField.RIGHT);
        tfVectLengthRight.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfVectLengthRight.addFocusListener(fieldFocusLostListener);
        tfVectLengthRight.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(tfVectLengthRight, gridBagConstraints);

        JLabel lbBLength = new JLabel("Baseline Length");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(lbBLength, gridBagConstraints);

        tfBaselegLengthLeft = new JTextField();
        tfBaselegLengthLeft.setHorizontalAlignment(JTextField.RIGHT);
        tfBaselegLengthLeft.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfBaselegLengthLeft.addFocusListener(fieldFocusLostListener);
        tfBaselegLengthLeft.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(tfBaselegLengthLeft, gridBagConstraints);

        tfBaselegLengthRight = new JTextField();
        tfBaselegLengthRight.setHorizontalAlignment(JTextField.RIGHT);
        tfBaselegLengthRight.addFocusListener(fieldFocusLostListener);
        tfBaselegLengthRight.setPreferredSize(new Dimension(60, boldFont.getSize() + 12));
        tfBaselegLengthRight.addActionListener(new RunwayFieldActionListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 2);
        jPnlVectoring.add(tfBaselegLengthRight, gridBagConstraints);

        JPanel space4 = new JPanel();
        space4.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.weightx = 100;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        jPnlVectoring.add(space4, gridBagConstraints);

        doLayout();
        pack();
    }

    public void showData(String rwCode) {
        setVisible(true);
        rwd = data.getRunwayData(rwCode);
        if (rwd == null) {
            throw new IllegalStateException("No runway data found for rw " + rwCode);
        }

        lbRWNumber.setText(rwCode);

        chbRwBiDirectional.setSelected(rwd.isBiDirectional());
        chbRwActiveForStarting.setSelected(rwd.isStartingEnabled());
        chbRwActiveForLanding.setSelected(rwd.isLandingEnabled());

        tfCLStart.setText(format(rwd.getExtCenterlineStart()));
        tfCLEnd.setText(format(rwd.getExtCenterlineLength()));
        tfCLMinorDMStart.setText(format(rwd.getMinorDMStart()));
        tfCLMinorDMEnd.setText(format(rwd.getMinorDMEnd()));
        tfCLMinorDMInterval.setText(format(rwd.getMinorDMInterval()));
        tfCLMinorDMTickLength.setText(format(rwd.getMinorDMTickLength()));
        tfCLMajorDMStart.setText(format(rwd.getMajorDMStart()));
        tfCLMajorDMEnd.setText(format(rwd.getMajorDMEnd()));
        tfCLMajorDMInterval.setText(format(rwd.getMajorDMInterval()));
        tfCLMajorDMTickLength.setText(format(rwd.getMajorDMTickLength()));

        chbSymmetric.setSelected(rwd.isSymetric());
        chbDisplayRight.setSelected(rwd.isRightBaseEnabled());
        chbDisplayLeft.setSelected(rwd.isLeftBaseEnabled());
        tfVectStartRight.setText(format(rwd.getRightVectoringCLStart()));
        tfVectAngleRight.setText(format(rwd.getRightVectoringAngle()));
        tfVectLengthRight.setText(format(rwd.getRightVectoringLength()));
        tfBaselegLengthRight.setText(format(rwd.getRightBaselegLength()));
        tfVectStartLeft.setText(format(rwd.getLeftVectoringCLStart()));
        tfVectAngleLeft.setText(format(rwd.getLeftVectoringAngle()));
        tfVectLengthLeft.setText(format(rwd.getLeftVectoringLength()));
        tfBaselegLengthLeft.setText(format(rwd.getLeftBaselegLength()));

        tfVectStartRight.setEnabled(!rwd.isSymetric());
        tfVectAngleRight.setEnabled(!rwd.isSymetric());
        tfVectLengthRight.setEnabled(!rwd.isSymetric());
        tfBaselegLengthRight.setEnabled(!rwd.isSymetric());

        doLayout();
        invalidate();
    }

    private String format(double d) {
        return String.format("%1.1f", d);
    }

    public void storeData(boolean permanent) {
        try {
            rwd.setBiDirectional(chbRwBiDirectional.isSelected());
            // store at opposite runway direction
            try {
                GuiRunway rw = data.getRunways().get(lbRWNumber.getText());
                String sOtherRwNumber = rw.getRunwayEnd().getOppositeEnd().getRunwayID();
                RunwayData otherEndRwd = data.getRunwayData( sOtherRwNumber );
                otherEndRwd.setBiDirectional(chbRwBiDirectional.isSelected());
            } catch(Exception e) {
                log.error("Problem to set other runway end of "+data.getAirportCode()+"."+lbRWNumber.getText()+" to be bi-idrectional",e);
            }
            rwd.setStartingEnabled(chbRwActiveForStarting.isSelected());
            rwd.setLandingEnabled(chbRwActiveForLanding.isSelected());

            rwd.setExtCenterlineStart(parse(tfCLStart.getText()));
            rwd.setExtCenterlineLength(parse(tfCLEnd.getText()));

            rwd.setMinorDMStart(parse(tfCLMinorDMStart.getText()));
            rwd.setMinorDMEnd(parse(tfCLMinorDMEnd.getText()));
            rwd.setMinorDMInterval(parse(tfCLMinorDMInterval.getText()));
            rwd.setMinorDMTickLength(parse(tfCLMinorDMTickLength.getText()));
            rwd.setMajorDMStart(parse(tfCLMajorDMStart.getText()));
            rwd.setMajorDMEnd(parse(tfCLMajorDMEnd.getText()));
            rwd.setMajorDMInterval(parse(tfCLMajorDMInterval.getText()));
            rwd.setMajorDMTickLength(parse(tfCLMajorDMTickLength.getText()));

            rwd.setSymetric(chbSymmetric.isSelected() && chbDisplayRight.isSelected() && chbDisplayLeft.isSelected());
            rwd.setRightBaseEnabled(chbDisplayRight.isSelected());
            rwd.setLeftBaseEnabled(chbDisplayLeft.isSelected());
            if (rwd.isSymetric()) {
                // take values from left side
                rwd.setRightVectoringCLStart(parse(tfVectStartLeft.getText()));
                rwd.setRightVectoringAngle(parse(tfVectAngleLeft.getText()));
                rwd.setRightVectoringLength(parse(tfVectLengthLeft.getText()));
                rwd.setRightBaselegLength(parse(tfBaselegLengthLeft.getText()));
            } else {
                rwd.setRightVectoringCLStart(parse(tfVectStartRight.getText()));
                rwd.setRightVectoringAngle(parse(tfVectAngleRight.getText()));
                rwd.setRightVectoringLength(parse(tfVectLengthRight.getText()));
                rwd.setRightBaselegLength(parse(tfBaselegLengthRight.getText()));
            }
            rwd.setLeftVectoringCLStart(parse(tfVectStartLeft.getText()));
            rwd.setLeftVectoringAngle(parse(tfVectAngleLeft.getText()));
            rwd.setLeftVectoringLength(parse(tfVectLengthLeft.getText()));
            rwd.setLeftBaselegLength(parse(tfBaselegLengthLeft.getText()));

            if (permanent)
                data.storeAirportData(master);
            rwd.setRepaintNeeded(true);
            master.getRadarBackend().forceRepaint();
        } catch (Exception e) {
            this.showData(lbRWNumber.getText());
        }
    }

    private double parse(String input) throws Exception {
        return Double.parseDouble(input);
    }

    private class ChooseRunwayMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getSource() instanceof JLabel) {
                JLabel label = (JLabel) e.getSource();
                if ("<<".equals(label.getName())) {
                    storeData(true);
                    String rwCode = getPreviousRw();
                    showData(rwCode);
                } else
                if (">>".equals(label.getName())) {
                    storeData(true);
                    String rwCode = getNextRw();
                    showData(rwCode);
                }
            } else {
                if ("COPY".equals(((JButton)e.getSource()).getName())) {
                    storeData(true);
                    copyToAll(lbRWNumber.getText());
                }
            }
        }

        private void copyToAll(String sourceCode) {
            RunwayData SourceRwd = data.getRunwayData(sourceCode);

            for(GuiRunway rw : data.getRunways().values()) {
                if(!rw.getCode().equals(sourceCode)) {
                    RunwayData rwd = rw.getRunwayData();
                    rwd.copyDataFrom(SourceRwd);
                    rwd.setRepaintNeeded(true);
                }
            }
            master.getAirportData().storeAirportData(master);
            master.getRadarBackend().forceRepaint();
        }

        private String getNextRw() {
            ArrayList<String> runways = new ArrayList<String>(data.getRunways().keySet());
            int currentRwIndex = runways.indexOf(lbRWNumber.getText());

            if (currentRwIndex < runways.size() - 1) {
                currentRwIndex++;
            } else {
                currentRwIndex = 0;
            }
            return runways.get(currentRwIndex);
        }

        private String getPreviousRw() {
            ArrayList<String> runways = new ArrayList<String>(data.getRunways().keySet());
            int currentRwIndex = runways.indexOf(lbRWNumber.getText());

            if (currentRwIndex > 0) {
                currentRwIndex--;
            } else {
                currentRwIndex = runways.size() - 1;
            }
            return runways.get(currentRwIndex);
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

    private void closeDialog() {
        storeData(true);
        setVisible(false);
    }

    /**
     * Refreshes the display to handle actions on 'symmetric' check box
     *
     * @author wolfram
     *
     */
    private class RunwayFieldActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            storeData(false);
            showData(lbRWNumber.getText());
            master.getStatusManager().updateRunways();
        }
    }

    private class FieldFocusLostListener extends FocusAdapter {
        @Override
        public void focusLost(FocusEvent e) {
            storeData(true); // can be set to false to make less hd saves
            showData(lbRWNumber.getText());
            master.getStatusManager().updateRunways();
        }
    }
}