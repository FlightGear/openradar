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
package de.knewcleus.openradar.gui.status.radio;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
/**
 * This class renders the frequency information in the combobox.
 * 
 * @author Wolfram Wagner
 */
public class RadioFrequencyListCellRenderer extends JPanel implements ListCellRenderer<RadioFrequency> {

    private static final long serialVersionUID = -2324428933630227401L;
    private JLabel lbAtcCode = null;
    private JLabel lbFreq = null;

//    private Font defaultFont = new java.awt.Font("Cantarell", Font.PLAIN, 12); // NOI18N
//    private Font activeFont = new java.awt.Font("Cantarell", Font.BOLD, 12); // NOI18N

    private Color defaultColor = Color.BLACK;
//    private Color selectionColor = Color.BLUE;
//    private Color errorColor = Color.RED;

    public RadioFrequencyListCellRenderer() {
        this.setLayout(new GridBagLayout());
        
        lbAtcCode = new JLabel("A");
        lbAtcCode.setForeground(defaultColor);
        //lbAtcCode.setFont(new java.awt.Font("Cantarell", 1, 10)); // NOI18N
        //lbAtcCode.setPreferredSize(new Dimension(80, defaultFont.getSize() + 4));
        //lbAtcCode.setOpaque(false);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lbAtcCode, gridBagConstraints);

        lbFreq = new JLabel("A");
        //lbFreq.setFont(new java.awt.Font("Cantarell", 1, 10)); // NOI18N
        //lbFreq.setPreferredSize(new Dimension(60, defaultFont.getSize() + 4));
        //lbAtcCode.setOpaque(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lbFreq, gridBagConstraints);

        //this.setOpaque(false);
        setBackground(Color.white);
        
        this.doLayout();
    }

    public Component getListCellRendererComponent(JList<? extends RadioFrequency> list, RadioFrequency value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {

        if(value==null) {
            lbAtcCode.setText("nothing");
            lbFreq.setText("found");
        } else {
            lbAtcCode.setText(value.getCode());
            lbFreq.setText(value.getFrequency());
        }

//        Color background;
//        Color foreground;
//
//        Font font = defaultFont;
//        foreground = defaultColor;
//        background = Color.WHITE;

//        if (isSelected) {
//            font = activeFont;
//            foreground = selectionColor;
//        } 
        
//        this.lbAtcCode.setFont(defaultFont);
//        this.lbAtcCode.setForeground(foreground);
//        this.lbAtcCode.setBackground(background);
//        this.lbFreq.setFont(defaultFont);
//        this.lbFreq.setForeground(foreground);
//        this.lbFreq.setBackground(background);

        // }

//        setBackground(background);
//        setForeground(foreground);

 //       doLayout();
        
        return this;
    }
    
}
