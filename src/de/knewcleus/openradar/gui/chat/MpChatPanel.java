/**
 * Copyright (C) 2012,2018 Wolfram Wagner
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
package de.knewcleus.openradar.gui.chat;

import java.awt.Color;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.chat.MpChatManager.Filter;

/**
 * This is the panel housing the chat features
 *
 * @author Wolfram Wagner
 */
public class MpChatPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private GuiMasterController guiInteractionManager;

    // private javax.swing.JTextArea taMPChatInput;
    private javax.swing.JTextField tfMPChatInput;
    private Color chatFieldDefaultForegroundColor;
    private final static Object chatMessageLock = new Object();

    private javax.swing.JLabel lbMpShowAll;
    private javax.swing.JLabel lbMpShowFreq;
    private javax.swing.JLabel lbMpShowRange;
    private javax.swing.JLabel lbMpShowACT;
    private javax.swing.JLabel lbMpShowVisible;
    private javax.swing.JList<GuiChatMessage> liMPChatHistory;
    private javax.swing.JScrollPane spMPChatHistory;

    /**
     * Creates new form MpChatPanel
     */
    public MpChatPanel(GuiMasterController guiInteractionManager) {
        this.guiInteractionManager = guiInteractionManager;
        initComponents();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbMpShowAll = new javax.swing.JLabel();
        lbMpShowFreq = new javax.swing.JLabel();
        lbMpShowRange = new javax.swing.JLabel();
        lbMpShowVisible = new javax.swing.JLabel();
        lbMpShowACT = new javax.swing.JLabel();
        tfMPChatInput = new javax.swing.JTextField();
        spMPChatHistory = new javax.swing.JScrollPane();
        liMPChatHistory = new javax.swing.JList<GuiChatMessage>();

        setLayout(new java.awt.GridBagLayout());
        setBackground(Palette.DESKTOP);

        lbMpShowAll.setFont(Palette.DESKTOP_FONT); // NOI18N
        lbMpShowAll.setForeground(Palette.CHAT_TEXT);
        lbMpShowAll.setText("ALL");
        lbMpShowAll.setName("ALL");
        lbMpShowAll.setToolTipText("Show all messages");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        add(lbMpShowAll, gridBagConstraints);
        lbMpShowAll.addMouseListener(guiInteractionManager.getMpChatManager().getFilterMouseListener());
        // frequencies are not transmitted
        // lbMpShowFreq.setFont(Palette.DESKTOP_FONT); // NOI18N
        // lbMpShowFreq.setText("FRQ");
        // lbMpShowFreq.setName("FRQ");
        // lbMpShowFreq.setForeground(Palette.CHAT_FILTER_FREQUENCY);
        // lbMpShowFreq.setToolTipText("Show only messages of contacts in radar range");
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 1;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
        // gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        // add(lbMpShowFreq, gridBagConstraints);
        //
        // lbMpShowFreq.addMouseListener(guiInteractionManager.getMpChatManager().getFilterMouseListener());

         lbMpShowRange.setFont(Palette.DESKTOP_FONT); // NOI18N
         lbMpShowRange.setForeground(Palette.CHAT_TEXT);
         lbMpShowRange.setText("RNG");
         lbMpShowRange.setName("RNG");
         lbMpShowRange.setToolTipText("Show only messages of contacts in radar range");
         gridBagConstraints = new java.awt.GridBagConstraints();
         gridBagConstraints.gridx = 2;
         gridBagConstraints.gridy = 0;
         gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
         gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
         add(lbMpShowRange, gridBagConstraints);
        
         lbMpShowRange.addMouseListener(guiInteractionManager.getMpChatManager().getFilterMouseListener());

//        lbMpShowVisible.setFont(Palette.DESKTOP_FONT); // NOI18N
//        lbMpShowVisible.setText("VIS");
//        lbMpShowVisible.setName("VIS");
//        lbMpShowVisible.setToolTipText("Show only messages of visible contacts");
//        lbMpShowVisible.setForeground(Palette.CHAT_FILTER_VISIBLE);
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 3;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
//        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
//        add(lbMpShowVisible, gridBagConstraints);
//
//        lbMpShowVisible.addMouseListener(guiInteractionManager.getMpChatManager().getFilterMouseListener());

        lbMpShowACT.setFont(Palette.DESKTOP_FONT); // NOI18N
        lbMpShowACT.setText("SEL");
        lbMpShowACT.setName("SEL");
        lbMpShowACT.setToolTipText("Show only messages of the SELECTED contact");
        lbMpShowACT.setForeground(Palette.CHAT_TEXT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(lbMpShowACT, gridBagConstraints);

        lbMpShowACT.addMouseListener(guiInteractionManager.getMpChatManager().getFilterMouseListener());

        tfMPChatInput.setEditable(true);
        tfMPChatInput.setColumns(1);
        chatFieldDefaultForegroundColor = tfMPChatInput.getForeground();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 8, 2, 4);
        add(tfMPChatInput, gridBagConstraints);

        // cbMPChatInput.addActionListener(guiInteractionManager.getMpChatManager());
        tfMPChatInput.addKeyListener(guiInteractionManager.getMpChatManager());
        guiInteractionManager.getMpChatManager().setChatPanel(this);

        liMPChatHistory.setModel(guiInteractionManager.getMpChatManager());
        liMPChatHistory.setCellRenderer(new MpChatListCellRenderer(guiInteractionManager));
        liMPChatHistory.setBackground(Palette.CHAT_BACKGROUND);
        liMPChatHistory.setForeground(Palette.CHAT_TEXT);
        liMPChatHistory.setDragEnabled(true);
        spMPChatHistory.setViewportView(liMPChatHistory);
        guiInteractionManager.getMpChatManager().setChatHistory(liMPChatHistory);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 4, 4);
        add(spMPChatHistory, gridBagConstraints);
    }

    public String getChatMessage() {
        synchronized(chatMessageLock) {
            return tfMPChatInput.getText();
        }
    }

    public void setChatMessage(String text) {
        synchronized(chatMessageLock) {
            tfMPChatInput.setText(text);
        }
    }

    public Object getChatMessageLock() {
        return chatMessageLock;
    }

    public void setChatMsgColor(Color color) {
        synchronized(chatMessageLock) {
            if(!tfMPChatInput.getForeground().equals(color)) {
                tfMPChatInput.setForeground(color);
                tfMPChatInput.invalidate();
            }
        }
    }

    public void resetChatMsgColor() {
        synchronized(chatMessageLock) {
            if(!tfMPChatInput.getForeground().equals(chatFieldDefaultForegroundColor)) {
                tfMPChatInput.setForeground(chatFieldDefaultForegroundColor);
                tfMPChatInput.invalidate();
            }
        }
    }
    
    public void requestFocusForInput() {
        tfMPChatInput.requestFocus();
    }


    public void selectFilter(Filter filter) {
        // reset Filters
        liMPChatHistory.setBackground(Palette.CHAT_BACKGROUND);

        lbMpShowAll.setForeground(Palette.CHAT_TEXT);
        lbMpShowFreq.setForeground(Palette.CHAT_TEXT);
        lbMpShowRange.setForeground(Palette.CHAT_TEXT);
        lbMpShowVisible.setForeground(Palette.CHAT_TEXT);
        lbMpShowACT.setForeground(Palette.CHAT_TEXT);

        // and set it
        switch(filter) {
        case FILTER_NONE:
           lbMpShowAll.setForeground(Palette.DESKTOP_FILTER_SELECTED);
           break;
        case FILTER_FREQUENCY:
            lbMpShowFreq.setForeground(Palette.DESKTOP_FILTER_SELECTED);
            break;
         case FILTER_RANGE:
             lbMpShowRange.setForeground(Palette.DESKTOP_FILTER_SELECTED);
             break;
         case FILTER_VISIBLE:
             lbMpShowVisible.setForeground(Palette.DESKTOP_FILTER_SELECTED);
             break;
         case FILTER_SELECTED_USER:
             lbMpShowACT.setForeground(Palette.DESKTOP_FILTER_SELECTED);
             break;
        }
    }
    
    public void forceRepaint() {
    	liMPChatHistory.repaint();
    }
}
