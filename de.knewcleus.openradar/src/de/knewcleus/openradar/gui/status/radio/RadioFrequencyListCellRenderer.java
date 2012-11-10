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
