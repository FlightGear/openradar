/**
 * Copyright (C) 2015 Wolfram Wagner
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
package de.knewcleus.openradar.fgfscontroller;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.knewcleus.openradar.gui.GuiMasterController;

public class CameraPresetControlPanel extends JPanel {

    private static final long serialVersionUID = 1L;
//    private final GuiMasterController master;
    private final FGFSController fgfsController;
    
    private JLabel lbP1;
    private JLabel lbP2;
    private JLabel lbP3;
    private JLabel lbP4;
    private JLabel lbP5;
    
    private JLabel lbFollow;
    private JLabel lbSetPos;
    
//    private Logger log = Logger.getLogger(CameraPresetControlPanel.class);
    
    public CameraPresetControlPanel(GuiMasterController master, FGFSController fgfsController) {
//        this.master=master;
        this.fgfsController = fgfsController;
        this.fgfsController.setCameraPresetControlPanel(this);
        
        //this.setDropTarget(new CamDropTarget());
        
        this.setLayout(new GridBagLayout());
        this.setToolTipText("<html><body>CamControl: Click on 'follow' to follow the selected contact</body></html>");
        
        FgFsMouseListener fsMouseListener = new FgFsMouseListener();
        
        lbP1 = new JLabel("P1");
        lbP1.setName("P1");
        lbP1.addMouseListener(fsMouseListener);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(4, 6, 4, 0);
        add(lbP1, gbc);

        lbP2 = new JLabel("P2");
        lbP2.setName("P2");
        lbP2.addMouseListener(fsMouseListener);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(0, 6, 0, 0);
        add(lbP2, gbc);

        lbP3 = new JLabel("P3");
        lbP3.setName("P3");
        lbP3.addMouseListener(fsMouseListener);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(0, 6, 0, 0);
        add(lbP3, gbc);

        lbP4 = new JLabel("P4");
        lbP4.setName("P4");
        lbP4.addMouseListener(fsMouseListener);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(0, 6, 0, 0);
        add(lbP4, gbc);

        lbP5 = new JLabel("P5");
        lbP5.setName("P5");
        lbP5.addMouseListener(fsMouseListener);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(0, 6, 0, 0);
        add(lbP5, gbc);

        lbFollow = new JLabel("Follow");
        lbFollow.setToolTipText("Toggles the follow mode. OR will track the the selected contact as long it is with range");
        lbFollow.setName("FOLLOW");
        lbFollow.addMouseListener(fsMouseListener);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(0, 10, 0, 0);
        add(lbFollow, gbc);

        
        lbSetPos = new JLabel("MoveCam");
        lbSetPos.setToolTipText("Move viewpoint of current preset: Zoom to GND level, click this button, click on the map");
        lbSetPos.setName("SETPOS");
        lbSetPos.addMouseListener(fsMouseListener);
        
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.EAST;
        gbc.weightx=1.0;
        gbc.insets = new java.awt.Insets(0, 6, 0, 6);
        add(lbSetPos, gbc);
        
        displaySelectedPreset(lbP1);
    }
    
    private class FgFsMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getSource() instanceof JLabel) {
                String sourceName = ((JLabel) e.getSource()).getName();
                
                if(sourceName!=null && sourceName.matches("P\\d*")) {
                    if(e.getButton()==MouseEvent.BUTTON1) {
                        
                        fgfsController.activatePreset(sourceName);
                        displaySelectedPreset((JLabel) e.getSource());
                        
                    } else if(e.getButton()==MouseEvent.BUTTON3) {
                        
                        fgfsController.savePreset(sourceName);
                        displaySelectedPreset((JLabel) e.getSource());
                    }
                } else if("FOLLOW".equals(sourceName)) {
                    if(fgfsController.followSelectedContact()) {
                        lbFollow.setText("F: "+fgfsController.getFollowContact());
                        lbFollow.setToolTipText("Following the selected contact");
                        lbFollow.setFont(lbFollow.getFont().deriveFont(Font.BOLD));
                        lbFollow.setForeground(Color.blue);
                        lbFollow.repaint();
                        //fgfsController.setFollow(true);
                    } else {
                        // stop following
                        lbFollow.setText("Follow");
                       // lbFollow.setToolTipText("Click to follow the selected contact, drag a contact to this line to follow this contact");
                        lbFollow.setFont(lbFollow.getFont().deriveFont(Font.PLAIN));
                        lbFollow.setForeground(Color.black);
                        lbFollow.repaint();
                        //fgfsController.setFollow(false);
                        //fgfsController.followContact(null);
                    }
                } else if("SETPOS".equals(sourceName)) {
                    if(!fgfsController.isSetposActive()) {
                        fgfsController.setSetposActive(true);
                        lbSetPos.setFont(lbSetPos.getFont().deriveFont(Font.BOLD));
                        lbSetPos.setForeground(Color.blue);
                    } else {
                        fgfsController.setSetposActive(false);
                        disableSetPosInView();
                    }
                }
            }
        }

    }

    protected void displaySelectedPreset(JLabel lbToActivate) {
        lbP1.setFont(lbP1.getFont().deriveFont(Font.PLAIN));
        lbP2.setFont(lbP2.getFont().deriveFont(Font.PLAIN));
        lbP3.setFont(lbP3.getFont().deriveFont(Font.PLAIN));
        lbP4.setFont(lbP4.getFont().deriveFont(Font.PLAIN));
        lbP5.setFont(lbP5.getFont().deriveFont(Font.PLAIN));
        
        lbP1.setForeground(Color.black);
        lbP2.setForeground(Color.black);
        lbP3.setForeground(Color.black);
        lbP4.setForeground(Color.black);
        lbP5.setForeground(Color.black);
        
        if(lbToActivate!=null) {
            lbToActivate.setFont(lbToActivate.getFont().deriveFont(Font.BOLD));
            lbToActivate.setForeground(Color.blue);
        }
    }

    public void disableSetPosInView() {
        lbSetPos.setFont(lbSetPos.getFont().deriveFont(Font.PLAIN));
        lbSetPos.setForeground(Color.black);
    }
    
//   private class CamDropTarget extends DropTarget {
//        
//        private static final long serialVersionUID = -1L;
//
//        @Override
//        public synchronized void drop(DropTargetDropEvent dtde) {
//            String callsign=null;
//            GuiRadarContact contact=null;
//            try {
//                callsign = (String) dtde.getTransferable().getTransferData(new DataFlavor(java.lang.String.class,"text/plain"));
//                contact = master.getRadarContactManager().getContactFor(callsign);
//            } catch (Exception e1) {
//                log.error("Handled exception while reading drag and drop data"+ e1.getMessage());
//            } 
//            if(callsign!=null && contact!=null) { // is it a callsign? or any other text
//                // set as follow contact
//                lbFollow.setText("F: "+callsign);
//                lbFollow.setToolTipText("Manually defined by drag and drop");
//                lbFollow.setFont(lbFollow.getFont().deriveFont(Font.BOLD));
//                lbFollow.setForeground(Color.blue);
//                fgfsController.followContact(callsign);
//            }
//        }
//    }
}
