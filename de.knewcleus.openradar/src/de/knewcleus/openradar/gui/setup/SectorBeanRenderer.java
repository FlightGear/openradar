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
package de.knewcleus.openradar.gui.setup;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
/**
 * The renderer for the sector information in the list
 * 
 * @author Wolfram Wagner
 */
public class SectorBeanRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 468867566445845231L;
    private JPanel container = new JPanel();
    private JLabel labelCode = new JLabel();
    private JLabel labelDescription = new JLabel();

    public SectorBeanRenderer() {
        container.setLayout(new GridBagLayout());
        
        labelCode.setOpaque(true);
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx=0;
        gridBagConstraints.gridy=0;
        gridBagConstraints.weightx=0;
        gridBagConstraints.anchor=GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        container.add(labelCode, gridBagConstraints);
        
        labelDescription.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx=1;
        gridBagConstraints.gridy=0;
        gridBagConstraints.weightx=1;
        gridBagConstraints.anchor=GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 2);
        container.add(labelDescription, gridBagConstraints);
        
    }
    
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
        labelCode.setPreferredSize(new Dimension(50,list.getFont().getSize()));

        Color foreground = new Color(50,50,50);
        Color background = Color.WHITE;
        if(cellHasFocus) {
            foreground=Color.BLACK;
        }
        if(isSelected) {
            foreground=Color.WHITE;
            background = new Color(110,152,203);
        }
        
        SectorBean sb = (SectorBean)value;
        labelCode.setText(sb.getAirportCode());
        labelDescription.setText(sb.getAirportName() + (sb.isSectorDownloaded()?" (exists)":""));
        
        labelCode.setForeground(foreground);
        labelCode.setBackground(background);
        labelDescription.setForeground(foreground);
        labelDescription.setBackground(background);
        container.setBackground(background);
        
        return container;
    }
}
