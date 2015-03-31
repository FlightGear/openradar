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
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.gui.contacts;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.ToolTipManager;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact.Alignment;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.setup.DataBlockLayoutListener;
import de.knewcleus.openradar.rpvd.contact.ADatablockLayout;

/**
 * The Panel showing the Radar Contacts in three columns
 *
 * @author Wolfram Wagner
 */
public class ContactsPanel extends javax.swing.JPanel implements DropTargetListener, DataBlockLayoutListener {

    private static final long serialVersionUID = 1251028249377116215L;
    private GuiMasterController master;
    private javax.swing.JLabel lbShowAll;
    private javax.swing.JLabel lbShowEmergencies;
    private javax.swing.JLabel lbShowGround;
    private javax.swing.JLabel lbShowLanding;
    private javax.swing.JLabel lbShowStarting;
    private javax.swing.JLabel lbShowTransits;
    private javax.swing.JLabel lbShowUnknown;
    private JLabel lbAssignSquawkVFR;
    private JLabel lbAssignSquawkIFR;
    private JLabel lbRevokeSquawk;
    private javax.swing.JList<GuiRadarContact> liRadarContacts;
    private javax.swing.JScrollPane spRadarContacs;

    public ContactsPanel(GuiMasterController guiInteractionManager) {
        this.master = guiInteractionManager;
        initComponents();

        datablockLayoutChanged(guiInteractionManager.getAirportData().getDatablockLayoutManager().getActiveLayout());
        guiInteractionManager.getAirportData().getDatablockLayoutManager().addDataBlockLayoutListener(this);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbShowAll = new javax.swing.JLabel();
        lbShowGround = new javax.swing.JLabel();
        lbShowLanding = new javax.swing.JLabel();
        lbShowStarting = new javax.swing.JLabel();
        lbShowTransits = new javax.swing.JLabel();
        lbShowUnknown = new javax.swing.JLabel();
        lbShowEmergencies = new javax.swing.JLabel();
        spRadarContacs = new javax.swing.JScrollPane();
        liRadarContacts = new javax.swing.JList<GuiRadarContact>();

        setLayout(new java.awt.GridBagLayout());
        setBackground(Palette.DESKTOP);

        lbShowAll.setFont(lbShowAll.getFont().deriveFont(Font.BOLD));
        lbShowAll.setForeground(Palette.DESKTOP_FILTER_SELECTED);
        lbShowAll.setText("AUTO");
        lbShowAll.setName("MODE");
        lbShowAll.setToolTipText("Toggle auto ordering of contacts");
        lbShowAll.addMouseListener(master.getRadarContactManager().getContactFilterMouseListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 6, 2, 0);
        add(lbShowAll, gridBagConstraints);

        // lbShowAll.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        // lbShowAll.setForeground(java.awt.Color.blue);
        // lbShowAll.setText("ALL");
        // lbShowAll.setName("ALL");
        // lbShowAll.setToolTipText("Show all");
        // lbShowAll.addMouseListener(guiInteractionManager.getRadarContactManager().getContactFilterMouseListener());
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 0;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        // gridBagConstraints.insets = new java.awt.Insets(4, 6, 0, 0);
        // add(lbShowAll, gridBagConstraints);
        //
        // lbShowGround.setText("GND");
        // lbShowGround.setName("GROUND");
        // lbShowGround.setToolTipText("Show contacts on ground");
        // lbShowGround.setForeground(java.awt.Palette.DESKTOP_TEXT);
        // lbShowGround.addMouseListener(guiInteractionManager.getRadarContactManager().getContactFilterMouseListener());
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 1;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        // gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        // add(lbShowGround, gridBagConstraints);
        //
        // lbShowLanding.setText("LND");
        // lbShowLanding.setName("LANDING");
        // lbShowLanding.setToolTipText("Show landing contacts");
        // lbShowLanding.setForeground(java.awt.Palette.DESKTOP_TEXT);
        // lbShowLanding.addMouseListener(guiInteractionManager.getRadarContactManager().getContactFilterMouseListener());
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 2;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        // gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        // add(lbShowLanding, gridBagConstraints);
        //
        // lbShowStarting.setText("STA");
        // lbShowStarting.setName("STARTING");
        // lbShowStarting.setToolTipText("Show starting contacts");
        // lbShowStarting.setForeground(java.awt.Palette.DESKTOP_TEXT);
        // lbShowStarting.addMouseListener(guiInteractionManager.getRadarContactManager().getContactFilterMouseListener());
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 3;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        // gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        // add(lbShowStarting, gridBagConstraints);
        //
        // lbShowTransits.setText("TRV");
        // lbShowTransits.setName("TRAVEL");
        // lbShowTransits.setToolTipText("Show contacts travelling");
        // lbShowTransits.setForeground(java.awt.Palette.DESKTOP_TEXT);
        // lbShowTransits.addMouseListener(guiInteractionManager.getRadarContactManager().getContactFilterMouseListener());
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 4;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        // gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        // add(lbShowTransits, gridBagConstraints);
        //
        // lbShowUnknown.setText("N/C");
        // lbShowUnknown.setName("UNKNOWN");
        // lbShowUnknown.setToolTipText("No contact/Unknown");
        // lbShowUnknown.setForeground(java.awt.Palette.DESKTOP_TEXT);
        // lbShowUnknown.addMouseListener(guiInteractionManager.getRadarContactManager().getContactFilterMouseListener());
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 6;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        // gridBagConstraints.weightx = 1.0;
        // gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        // add(lbShowUnknown, gridBagConstraints);
        //
        // lbShowEmergencies.setText("EMG");
        // lbShowEmergencies.setName("EMERGENCY");
        // lbShowEmergencies.setToolTipText("Show contacts in transit");
        // lbShowEmergencies.setForeground(java.awt.Palette.DESKTOP_TEXT);
        // lbShowEmergencies.addMouseListener(guiInteractionManager.getRadarContactManager().getContactFilterMouseListener());
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 5;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        // gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        // add(lbShowEmergencies, gridBagConstraints);


        lbAssignSquawkVFR = new JLabel("AssVFR");
        lbAssignSquawkVFR.setToolTipText("Assigns a new VFR squawk code from range");
        lbAssignSquawkVFR.setName("ASSIGN_SQUAWK_VFR");
        lbAssignSquawkVFR.setFont(lbAssignSquawkVFR.getFont().deriveFont(Font.BOLD));
        lbAssignSquawkVFR.setForeground(Palette.DESKTOP_FILTER_SELECTED);
        lbAssignSquawkVFR.addMouseListener(new HelpMouseListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx=1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 6, 2, 0);
        add(lbAssignSquawkVFR, gridBagConstraints);

        lbAssignSquawkIFR= new JLabel("AssIFR");
        lbAssignSquawkIFR.setToolTipText("Assigns a new IFR squawk code from range");
        lbAssignSquawkIFR.setName("ASSIGN_SQUAWK_IFR");
        lbAssignSquawkIFR.setFont(lbAssignSquawkVFR.getFont().deriveFont(Font.BOLD));
        lbAssignSquawkIFR.setForeground(Palette.DESKTOP_FILTER_SELECTED);
        lbAssignSquawkIFR.addMouseListener(new HelpMouseListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 0);
        add(lbAssignSquawkIFR, gridBagConstraints);

        lbRevokeSquawk = new JLabel("RevSqw");
        lbRevokeSquawk.setToolTipText("Revoke the assigned Squawk code");
        lbRevokeSquawk.setName("REVOKE_SQUAWK");
        lbRevokeSquawk.setFont(lbAssignSquawkVFR.getFont().deriveFont(Font.BOLD));
        lbRevokeSquawk.setForeground(Palette.DESKTOP_FILTER_SELECTED);
        lbRevokeSquawk.addMouseListener(new HelpMouseListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 6, 2, 0);
        add(lbRevokeSquawk, gridBagConstraints);

        JLabel lbDeselect = new JLabel("Deselect");
        lbDeselect.setToolTipText("Shortcut: press ESC twice"); 
        lbDeselect.setName("DESELECT");
        lbDeselect.setFont(lbDeselect.getFont().deriveFont(Font.BOLD));
        lbDeselect.setForeground(Palette.DESKTOP_FILTER_SELECTED);
        lbDeselect.addMouseListener(new HelpMouseListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 6, 2, 0);
        add(lbDeselect, gridBagConstraints);

        JLabel lbNeglect = new JLabel("Neglect");
        lbNeglect.setName("NEGLECT");
        lbNeglect.setFont(lbNeglect.getFont().deriveFont(Font.BOLD));
        lbNeglect.setForeground(Palette.DESKTOP_FILTER_SELECTED);
        lbNeglect.addMouseListener(new HelpMouseListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx=0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 6, 2, 0);
        add(lbNeglect, gridBagConstraints);

        JLabel lbHelp = new JLabel("?");
        lbHelp.setName("HELP");
        lbHelp.setFont(lbHelp.getFont().deriveFont(Font.BOLD));
        lbHelp.setForeground(Palette.DESKTOP_FILTER_SELECTED);
        lbHelp.addMouseListener(new HelpMouseListener());
        lbHelp.setToolTipText("<html><body><b>left click:</b> select/move,<br/> <b>left double click:</b> center map on contact, <br/><b>middle click:</b> edit details, <br/><b>right click:</b> show atcmsgs<br/><b>CTRL+left click</b>: neglect</body></html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 2, 2);
        add(lbHelp, gridBagConstraints);

        
        liRadarContacts.setBackground(Palette.DESKTOP);
//         liRadarContacts.setToolTipText("<html><body><b>left click:</b> select/move,<br/> <b>left double click:</b> center map on contact, <br/><b>middle click:</b> edit details, <br/><b>right click:</b> show atcmsgs<br/><b>CTRL+left click</b>: neglect</body></html>");
        liRadarContacts.setModel(master.getRadarContactManager());
        liRadarContacts.setCellRenderer(new FlightStripCellRenderer(master));
        liRadarContacts.setForeground(Palette.DESKTOP_TEXT);
        master.getRadarContactManager().setJList(liRadarContacts);
        liRadarContacts.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        liRadarContacts.setDragEnabled(true);
        liRadarContacts.setDropMode(javax.swing.DropMode.ON);
        liRadarContacts.setDoubleBuffered(false);
//        spRadarContacs.getViewport().setView(liRadarContacts);

        liRadarContacts.addMouseListener(master.getRadarContactManager().getContactMouseListener());
        liRadarContacts.addMouseMotionListener(master.getRadarContactManager().getContactMouseListener());
        new DropTarget(liRadarContacts, this); // link this with radar contact
                                               // list
        
        // to avoid the clicking below the list selects the last contact
        // I shrink the list to match the required size.
        JPanel pnlHelper = new ContactViewPanel(spRadarContacs, this.liRadarContacts);
        pnlHelper.setBackground(Palette.DESKTOP);
        pnlHelper.setLayout(new BorderLayout());
        pnlHelper.add(liRadarContacts, BorderLayout.NORTH);
        spRadarContacs.getViewport().setBackground(Palette.DESKTOP);
        spRadarContacs.getViewport().setView(pnlHelper);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
        add(spRadarContacs, gridBagConstraints);
    }

    // Drop target

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drop(DropTargetDropEvent e) {

        if (((DropTarget) e.getSource()).getComponent() == liRadarContacts) {
            int selectedIndex = liRadarContacts.getSelectedIndex();
            ;
            int insertAtIndex = liRadarContacts.locationToIndex(e.getLocation());
            // alignment
            Alignment align = Alignment.RIGHT;
            int totalWidth = liRadarContacts.getSize().width;
            int releasePointX = e.getLocation().x;
            if (releasePointX < totalWidth / 3) {
                align = Alignment.LEFT;
            } else if (releasePointX < 2 * totalWidth / 3) {
                align = Alignment.CENTER;
            } else if (releasePointX > 2 * totalWidth / 3) {
                align = Alignment.RIGHT;
            }

            master.getRadarContactManager().dragAndDrop(selectedIndex, insertAtIndex, align);
            e.getDropTargetContext().dropComplete(true);

        } else {
            e.rejectDrop();
        }
    }

    public void resetFilters() {
        lbShowAll.setForeground(Palette.DESKTOP_TEXT);
        lbShowEmergencies.setForeground(Palette.DESKTOP_TEXT);
        lbShowGround.setForeground(Palette.DESKTOP_TEXT);
        lbShowLanding.setForeground(Palette.DESKTOP_TEXT);
        lbShowStarting.setForeground(Palette.DESKTOP_TEXT);
        lbShowTransits.setForeground(Palette.DESKTOP_TEXT);
        lbShowUnknown.setForeground(Palette.DESKTOP_TEXT);
    }

    public void selectFilter(javax.swing.JLabel l) {
        l.setForeground(Palette.DESKTOP_FILTER_SELECTED);
    }

    private class HelpMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            MouseEvent dummyEvent = new MouseEvent(
                    (JComponent)e.getSource(),
                    MouseEvent.MOUSE_MOVED,
                    System.currentTimeMillis(),
                    0,
                    0,
                    0,
                    0,
                    false);

            if(((JLabel)e.getSource()).getName().equals("ASSIGN_SQUAWK_VFR")) {
                if(e.getButton()==MouseEvent.BUTTON1) {
                    // assign
                    master.getRadarContactManager().assignSquawkCode(FlightPlanData.FlightType.VFR);
                } else if(e.getButton()==MouseEvent.BUTTON3) {
                    // open dialog
                    master.getRadarContactManager().getTransponderSettingsDialog().show(e);
                }
            } else if(((JLabel)e.getSource()).getName().equals("ASSIGN_SQUAWK_IFR")) {
                if(e.getButton()==MouseEvent.BUTTON1) {
                    // assign
                    master.getRadarContactManager().assignSquawkCode(FlightPlanData.FlightType.IFR);;
                } else if(e.getButton()==MouseEvent.BUTTON3) {
                    // open dialog
                    master.getRadarContactManager().getTransponderSettingsDialog().show(e);
                }
            } else if(((JLabel)e.getSource()).getName().equals("REVOKE_SQUAWK")) {
                if(e.getButton()==MouseEvent.BUTTON1) {
                    // assign
                    master.getRadarContactManager().revokeSquawkCode();
                } else if(e.getButton()==MouseEvent.BUTTON3) {
                    // open dialog
                    master.getRadarContactManager().getTransponderSettingsDialog().show(e);
                }
            } else if(((JLabel)e.getSource()).getName().equals("HELP")) {
               int delay = ToolTipManager.sharedInstance().getInitialDelay();
               ToolTipManager.sharedInstance().setInitialDelay(0);
               ToolTipManager.sharedInstance().mouseMoved(dummyEvent);
               ToolTipManager.sharedInstance().setInitialDelay(delay);
            } else if(((JLabel)e.getSource()).getName().equals("DESELECT")) {
                master.getRadarContactManager().deselectContact();
            } else if(((JLabel)e.getSource()).getName().equals("NEGLECT")) {
                master.getRadarContactManager().neglectSelectedContact();
            }
        }
    }

    @Override
    public void datablockLayoutChanged(ADatablockLayout newLayout) {
        lbAssignSquawkVFR.setVisible(newLayout.supportsSquawk());
        lbAssignSquawkIFR.setVisible(newLayout.supportsSquawk());
        lbRevokeSquawk.setVisible(newLayout.supportsSquawk());
    }
    
    /** This panel helps to shrink the JList to its minimum size */
    private static final class ContactViewPanel extends JPanel implements Scrollable {

        private static final long serialVersionUID = 1L;

        private final JList<GuiRadarContact> contactList;
        private final JScrollPane scrollpane;
        
        public ContactViewPanel(JScrollPane scrollpane, JList<GuiRadarContact> contactList) {
            this.contactList = contactList;
            this.scrollpane = scrollpane;
        }
        
        @Override
        public Dimension getPreferredSize() {
            Dimension liDim = contactList.getPreferredSize();
            if(contactList.getModel().getSize()>0) {
                // with content
                return new Dimension((int)scrollpane.getViewport().getWidth(), (int)liDim.getHeight());
            } else {
                // empty
                return new Dimension((int)liDim.getWidth(), (int)liDim.getHeight());
            }
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return contactList.getScrollableBlockIncrement(visibleRect, orientation, direction);
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return contactList.getScrollableTracksViewportHeight();
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return contactList.getScrollableTracksViewportWidth();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return contactList.getScrollableUnitIncrement(visibleRect, orientation, direction);
        }
        
    }
}
