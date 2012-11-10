package de.knewcleus.openradar.gui.contacts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

/**
 * This class renders the flight strip like radar contacts 
 * 
 * @author Wolfram Wagner
 */

public class RadarContactListCellRenderer extends JComponent implements ListCellRenderer<GuiRadarContact> {
    private static final long serialVersionUID = 4683696532302543565L;

    private JPanel spaceRight = null;
    private JPanel strip = null;
    private JLabel lbOperation = null;
    private JLabel lbCallSign = null;
    private JLabel lbHeading = null;
    private JLabel lbAircraft = null;
    private JLabel lbFlightLevel= null;
    private JLabel lbTrueSpeed = null;
    private JLabel lbGroundSpeed = null;
    private JTextArea taAtcComment = null;

    private GridBagConstraints stripConstraints;
    private GridBagConstraints spaceRightConstraints;
    private GridBagConstraints lbOperationConstraints;
    private GridBagConstraints lbCallSignConstraints;
    private GridBagConstraints lbHeadingConstraints;
    private GridBagConstraints lbAircraftConstraints;
    private GridBagConstraints lbFlightLevelConstraints;
    private GridBagConstraints lbTrueSpeedConstraints;
    private GridBagConstraints lbGroundSpeedConstraints;
    private GridBagConstraints taAtcCommentConstraints;
    
    private Font defaultFont = new java.awt.Font("Cantarell", Font.PLAIN, 12); // NOI18N
    private Font boldFont = new java.awt.Font("Cantarell", Font.BOLD, 12); // NOI18N

    private Color defaultColor = Color.BLACK;
    private Color selectionColor = Color.BLUE;
    private Color emergencyColor = Color.RED;

    private int ownWidth = 250;

    
    public RadarContactListCellRenderer() {
        this.setLayout(new GridBagLayout());

        spaceRight = new JPanel();
        spaceRight.setOpaque(false);
        spaceRightConstraints = new GridBagConstraints();
        spaceRightConstraints.gridx = 0;
        spaceRightConstraints.gridy = 0;
        spaceRightConstraints.gridheight = 1;
        spaceRightConstraints.fill=GridBagConstraints.HORIZONTAL;
        spaceRightConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(spaceRight, spaceRightConstraints);
        
        strip = new JPanel();
        strip.setLayout(new GridBagLayout());
        stripConstraints = new GridBagConstraints();
        stripConstraints.gridx = 1;
        stripConstraints.gridy = 0;
        stripConstraints.gridheight = 1;
        stripConstraints.weightx=1;
        stripConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        stripConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        //strip.setPreferredSize(new Dimension(ownWidth, 3* defaultFont.getSize() + 12));
        add(strip, stripConstraints);

        // first line
        
        lbCallSign = new JLabel();
        lbCallSign.setFont(boldFont);
        lbCallSign.setForeground(java.awt.Color.blue);
        lbCallSign.setToolTipText("Current call sign");
        // lbFreq.setMinimumSize(new Dimension)
        lbCallSign.setPreferredSize(new Dimension(160,defaultFont.getSize()+2));
        lbCallSign.setOpaque(true);
        lbCallSignConstraints = new GridBagConstraints();
        lbCallSignConstraints.gridx = 0;
        lbCallSignConstraints.gridy = 0;
        lbCallSignConstraints.gridwidth = 2;
        lbCallSignConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        lbCallSignConstraints.insets = new java.awt.Insets(4, 4, 0, 5);
        strip.add(lbCallSign, lbCallSignConstraints);

        lbOperation = new JLabel();
        lbOperation.setFont(boldFont);
        lbOperation.setForeground(java.awt.Color.blue);
        lbOperation.setToolTipText("Current call sign");
        // lbOperation.setMinimumSize(new Dimension)
        lbOperation.setPreferredSize(new Dimension(30,defaultFont.getSize()+2));
        lbOperation.setOpaque(true);
        lbOperationConstraints = new GridBagConstraints();
        lbOperationConstraints.gridx = 2;
        lbOperationConstraints.gridy = 0;
        lbOperationConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        lbOperationConstraints.insets = new java.awt.Insets(4,0, 0, 5);
        strip.add(lbOperation, lbOperationConstraints);

        lbFlightLevel = new JLabel();
        lbFlightLevel.setFont(boldFont);
        lbFlightLevel.setForeground(java.awt.Color.blue);
        lbFlightLevel.setToolTipText("Current call sign");
        // lbFreq.setMinimumSize(new Dimension)
        lbFlightLevel.setPreferredSize(new Dimension(45,defaultFont.getSize()+2));
        lbFlightLevel.setOpaque(true);
        lbFlightLevelConstraints = new GridBagConstraints();
        lbFlightLevelConstraints.gridx = 3;
        lbFlightLevelConstraints.gridy = 0;
        lbFlightLevelConstraints.weightx = 1;
        lbFlightLevelConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        lbFlightLevelConstraints.insets = new java.awt.Insets(4, 0, 0, 5);
        strip.add(lbFlightLevel, lbFlightLevelConstraints);
        
        
        // second line

        lbHeading = new JLabel();
        lbHeading.setFont(defaultFont);
        lbHeading.setForeground(java.awt.Color.blue);
        lbHeading.setToolTipText("Current call sign");
        // lbFreq.setMinimumSize(new Dimension)
        lbHeading.setOpaque(true);
        lbHeadingConstraints = new GridBagConstraints();
        lbHeadingConstraints.gridx = 0;
        lbHeadingConstraints.gridy = 1;
        lbHeadingConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        lbHeadingConstraints.insets = new java.awt.Insets(0, 4, 0, 5);
        strip.add(lbHeading, lbHeadingConstraints);

        lbAircraft = new JLabel();
        lbAircraft.setFont(defaultFont);
        lbAircraft.setForeground(java.awt.Color.blue);
        lbAircraft.setToolTipText("Current call sign");
        lbAircraft.setOpaque(true);
        lbAircraft.setPreferredSize(new Dimension(120,defaultFont.getSize()+2));
        lbAircraftConstraints = new GridBagConstraints();
        lbAircraftConstraints.gridx = 1;
        lbAircraftConstraints.gridy = 1;
        lbAircraftConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        lbAircraftConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        strip.add(lbAircraft, lbAircraftConstraints);

        taAtcComment = new JTextArea();
        taAtcComment.setFont(defaultFont);
        taAtcComment.setForeground(java.awt.Color.blue);
        taAtcComment.setOpaque(true);
        taAtcComment.setEditable(false);
        taAtcCommentConstraints = new GridBagConstraints();
        taAtcCommentConstraints.gridx = 2;
        taAtcCommentConstraints.gridy = 1;
        taAtcCommentConstraints.gridwidth=2;
        taAtcCommentConstraints.gridheight=2;
        taAtcCommentConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        taAtcCommentConstraints.insets = new java.awt.Insets(0, 0, 6, 4);
        strip.add(taAtcComment, taAtcCommentConstraints);

        // third line
        
        lbTrueSpeed = new JLabel();
        lbTrueSpeed.setFont(defaultFont);
        lbTrueSpeed.setForeground(java.awt.Color.blue);
        lbTrueSpeedConstraints = new GridBagConstraints();
        lbTrueSpeedConstraints.gridx = 0;
        lbTrueSpeedConstraints.gridy = 2;
        lbTrueSpeedConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        lbTrueSpeedConstraints.insets = new java.awt.Insets(0, 4, 6, 5);
        strip.add(lbTrueSpeed, lbTrueSpeedConstraints);
        
        lbGroundSpeed = new JLabel();
        lbGroundSpeed.setFont(defaultFont);
        lbGroundSpeed.setForeground(java.awt.Color.blue);
        lbGroundSpeed.setToolTipText("Current call sign");
        lbGroundSpeed.setOpaque(true);
        lbGroundSpeedConstraints = new GridBagConstraints();
        lbGroundSpeedConstraints.gridx = 1;
        lbGroundSpeedConstraints.gridy = 2;
        lbGroundSpeedConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        lbGroundSpeedConstraints.insets = new java.awt.Insets(0, 0, 6, 5);
        strip.add(lbGroundSpeed, lbGroundSpeedConstraints);
        
        doLayout();
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends GuiRadarContact> list, GuiRadarContact value, int index, boolean isSelected, boolean cellHasFocus) {
        int totalWidth = list.getParent().getWidth();
        int moveIncrement = (totalWidth-ownWidth)/2-4;
        
        lbCallSign.setText(value.getCallSign());
        lbFlightLevel.setText(value.getFlightLevel());
        lbOperation.setText(value.getOperationString());
        lbHeading.setText(value.getTrueCourse());
        lbAircraft.setText(value.getAircraft());
        taAtcComment.setText(value.getAtcComment());
        lbTrueSpeed.setText(value.getAirspeed());
        lbGroundSpeed.setText(value.getGroundSpeed());

        Color background;
        Color foreground;

        // check if this cell represents the current DnD drop location
        JList.DropLocation dropLocation = list.getDropLocation();
        if (dropLocation != null && !dropLocation.isInsert() && dropLocation.getIndex() == index) {

            background = Color.BLUE;
            foreground = Color.WHITE;

            
        } else {
            // normal display
            
            foreground = defaultColor;
            background = Color.white;

            if (value.isOnEmergency()) {
                // font = activeFont;
                foreground = emergencyColor;
            } else if (value.isSelected()) {
                // font = activeFont;
                foreground = selectionColor;
            }
        }
        // this.lbCallSign.setFont(activeFont);
        this.lbCallSign.setForeground(foreground);
        this.lbCallSign.setBackground(background);
        // this.lbFlightLevel.setFont(activeFont);
        this.lbFlightLevel.setForeground(foreground);
        this.lbFlightLevel.setBackground(background);
        // this.lbOperation.setFont(font);
        this.lbOperation.setForeground(foreground);
        this.lbOperation.setBackground(background);
        // this.lbHeading.setFont(activeFont);
        this.lbHeading.setForeground(foreground);
        this.lbHeading.setBackground(background);
        // this.lbAircraft.setFont(activeFont);
        this.lbAircraft.setForeground(foreground);
        this.lbAircraft.setBackground(background);
        this.taAtcComment.setForeground(foreground);
        this.taAtcComment.setBackground(background);

        this.lbTrueSpeed.setForeground(foreground);
        this.lbTrueSpeed.setBackground(background);
        // this.lbGroundSpeed.setFont(activeFont);
        this.lbGroundSpeed.setForeground(foreground);
        this.lbGroundSpeed.setBackground(background);

        // alinment
        switch(value.getAlignment()) {
        case LEFT:
            spaceRight.setPreferredSize(new Dimension(0,0));
            break;
        case CENTER:
            spaceRight.setPreferredSize(new Dimension(moveIncrement*1,0));
            break;
        case RIGHT:
            spaceRight.setPreferredSize(new Dimension(moveIncrement*2,0));
            break;
        }
        
        strip.setBackground(background);
        strip.setOpaque(true);
        
        return this;
    }
    
}
