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
