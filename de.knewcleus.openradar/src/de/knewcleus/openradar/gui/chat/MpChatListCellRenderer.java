package de.knewcleus.openradar.gui.chat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
/**
 * This class renders the chat messages into the list.
 * 
 * @author Wolfram Wagner
 */
public class MpChatListCellRenderer extends JComponent implements ListCellRenderer<GuiChatMessage> {
    
    private static final long serialVersionUID = 7792122217254832611L;

    private final GuiMasterController master;

    private JLabel lbTimeStamp = null;
    private JLabel lbCallSign = null;
    private JLabel lbMessage = null;

    public MpChatListCellRenderer(GuiMasterController master) {
        this.master=master;
        
        this.setLayout(new GridBagLayout());
        this.setOpaque(false);
        
        lbTimeStamp = new JLabel();
        lbTimeStamp.setForeground(java.awt.Color.white);
        lbTimeStamp.setOpaque(false);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        add(lbTimeStamp, gridBagConstraints);

        lbCallSign = new JLabel();
        lbCallSign.setForeground(java.awt.Color.white);
        lbCallSign.setOpaque(false);
        lbCallSign.setPreferredSize(new Dimension(80,lbCallSign.getFont().getSize()+4));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 5);
        add(lbCallSign, gridBagConstraints);

        lbMessage = new JLabel();
        lbMessage.setForeground(java.awt.Color.white);
        lbMessage.setOpaque(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 5);
        add(lbMessage, gridBagConstraints);
        
        doLayout();
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends GuiChatMessage> list, GuiChatMessage value,
            int index, boolean isSelected, boolean cellHasFocus) {

            lbTimeStamp.setText(value.getTimestamp());
            lbCallSign.setText(value.getCallSign()+":");
            lbMessage.setText(value.getMessage());
            Color foreground;

            if (value.isNeglectOrInactive()) {
                // bad guys
                foreground = Color.GRAY;
                
             } else if (value.isContactSelected()) {
                 // messages of selected contacts
                 foreground = Palette.CHAT_SELECTED;
                 
             } else if (value.isOwnMessage()) {
                 if(value.messageContainsSelectedContact(master)) {
                     // own to selected
                     foreground = Palette.CHAT_OWN_TO_SELECTED;
                     
                 } else {
                     // own to unselected
                     foreground = Palette.CHAT_OWN;

                 }
             } else if(value.isAirportMentioned()) {
                 // airport mentioned in contact message
                 foreground = Palette.CHAT_AIRPORT_MENTIONED;
                 
             } else {
                 // default
                foreground = Palette.CHAT_TEXT;
            }
            
            this.lbTimeStamp.setForeground(foreground);
            this.lbCallSign.setForeground(foreground);
            this.lbMessage.setForeground(foreground);

            doLayout();
            
            return this;
    }
}
