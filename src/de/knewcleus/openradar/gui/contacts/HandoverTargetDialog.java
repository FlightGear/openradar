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
package de.knewcleus.openradar.gui.contacts;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import org.apache.log4j.Logger;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.flightplan.FpAtc;
import de.knewcleus.openradar.gui.setup.AirportData;

public class HandoverTargetDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final GuiMasterController master;
    private final RadarContactController manager;
    private final JPanel pnlHandoverAtcs = new JPanel();
    private JScrollPane spList;
    private List<String> handoverTargets;

    private JLabel lbHandoverTo;

    private Logger log = Logger.getLogger(HandoverTargetDialog.class);
    
    public HandoverTargetDialog(GuiMasterController master) {
        this.master = master;
        this.manager = master.getRadarContactManager();
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

        JPanel jPnlContentPane = new JPanel();
        jPnlContentPane.setOpaque(false);
        jPnlContentPane.setLayout(new GridBagLayout());
        setContentPane(jPnlContentPane);

        //lbCallSign
        lbHandoverTo = new JLabel("handover");
        lbHandoverTo.setName("handover");
        lbHandoverTo.setFont(lbHandoverTo.getFont().deriveFont(Font.ITALIC));
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 4, 8);
        jPnlContentPane.add(lbHandoverTo, gridBagConstraints);

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

        spList.setViewportView(pnlHandoverAtcs);

        pnlHandoverAtcs.setLayout(new GridBagLayout());
        pnlHandoverAtcs.setOpaque(false);
        pnlHandoverAtcs.setDropTarget(new HandoverDropTarget());

        doLayout();
        pack();
    }

    private void reloadAtcs() {
        pnlHandoverAtcs.removeAll();
        handoverTargets = manager.getOtherATCsList(true);
        for(int i=0;i<handoverTargets.size();i++) {
            GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);

            String msg = handoverTargets.get(i);
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            if(msg.isEmpty()) {
                pnlHandoverAtcs.add(new JSeparator(), gridBagConstraints);
            } else {
                JLabel lb = new JLabel("<html>"+msg+"</html>");
                lb.setName(msg);
                lb.addMouseListener(new TextMouseListener());
                pnlHandoverAtcs.add(lb, gridBagConstraints);
            }
        }
    }
    
    public void setLocation(MouseEvent e) {

        reloadAtcs();
        if(pnlHandoverAtcs.getComponentCount()>0) {
        
            Rectangle maxBounds = AirportData.MAX_WINDOW_SIZE;
    
            // size scrollpane
            Dimension preferredSize = pnlHandoverAtcs.getPreferredSize();
            spList.setPreferredSize(preferredSize.getHeight()>maxBounds.getHeight() ?
                    new Dimension(pnlHandoverAtcs.getPreferredSize().width+30, maxBounds.height-40) : pnlHandoverAtcs.getPreferredSize());
    
            // side dialog
            Dimension innerSize = getContentPane().getPreferredSize();
            setSize(new Dimension((int)innerSize.getWidth()+8, (int)innerSize.getHeight()+8));
    
            Point2D p;
            if(e!=null) {
                p = /*e.getSource() instanceof JList ?
                        ((JComponent) e.getSource()).getLocationOnScreen():*/
                        e.getLocationOnScreen();
                p = new Point2D.Double(p.getX() - 90 , p.getY() - this.getWidth()/2 );
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
    }

    private class TextMouseListener extends MouseAdapter {
        @Override
        public void mouseExited(MouseEvent e) {
            closeDialog();
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

    public void closeDialog() {
        setVisible(false);
    }

    private class HandoverDropTarget extends DropTarget {
        
        private static final long serialVersionUID = -2749068600723364921L;

        @Override
        public void drop(DropTargetDropEvent e) {
            GuiRadarContact contact=null;
            try {
                String callsign = (String) e.getTransferable().getTransferData(new DataFlavor(java.lang.String.class,"text/plain"));
                contact = manager.getContactFor(callsign);
            } catch (Exception e1) {
                log.error("Exception while reading drag and drop data", e1);
            } 

            if(contact!=null && contact.getFlightPlan().isOwnedByMe()) {
                // determine handover
                String handover = ((JLabel)pnlHandoverAtcs.getComponentAt(e.getLocation())).getName();
                
                // set handover
                if(handover.equals("-revoke-")) {
                    manager.setContactHandover(contact,"");
                } else {
                    FpAtc handoverAtc = manager.getAtcFor(handover);;
                    if(handoverAtc!=null) {
                        master.getFlightPlanExchangeManager().sendHandoverMessage(contact, handoverAtc);
                        manager.setContactHandover(contact,handover);
                    }
                }
                e.getDropTargetContext().dropComplete(true);

            } else {
                e.rejectDrop();
            }
            
            closeDialog();
        }

    }
    
}
