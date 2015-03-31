/**
 * Copyright (C) 2012 Wolfram Wagner 
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
package de.knewcleus.openradar.gui.status;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;

/**
 * This Panel contains the view and input area for ATC notes to radar contacts.
 *@deprecated
 * @author Wolfram Wagner
 */
public class DetailPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 5247122066591225377L;
    private GuiMasterController guiInteractionManager;
    private javax.swing.JScrollPane spDetails;
    private javax.swing.JTextPane tpDetails;
    
    /**
     * Creates new form DetailPanel
     */
    public DetailPanel(GuiMasterController guiInteractionManager) {
        this.guiInteractionManager=guiInteractionManager;
//        guiInteractionManager.getStatusManager().setDetailPanel(this);
        initComponents();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        spDetails = new javax.swing.JScrollPane();
        tpDetails = new javax.swing.JTextPane();

        setLayout(new java.awt.GridBagLayout());
        setForeground(Palette.DESKTOP_TEXT);
        setBackground(Palette.DESKTOP);
        
        tpDetails.setToolTipText("ATC Notes: RETURN save, STRG+RETURN newline");
        tpDetails.setOpaque(true);
        spDetails.setViewportView(tpDetails);
        guiInteractionManager.setDetailsArea(tpDetails);
        tpDetails.addFocusListener(guiInteractionManager.getRadarContactManager().getDetailsFocusListener());
//        tpDetails.addKeyListener(guiInteractionManager.getStatusManager().getDetailsKeyListener());
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(spDetails, gridBagConstraints);
    }

    public void requestFocusForDetailInput() {
        this.tpDetails.requestFocus();
    }

    
}
