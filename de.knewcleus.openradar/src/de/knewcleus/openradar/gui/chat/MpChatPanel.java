package de.knewcleus.openradar.gui.chat;

import java.awt.Color;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;

/**
 * This is the panel housing the chat features
 * 
 * @author Wolfram Wagner
 */
public class MpChatPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private GuiMasterController guiInteractionManager;

    private javax.swing.JComboBox<String> cbMPChatInput;
    private javax.swing.JLabel lbMpShowAll;
    private javax.swing.JLabel lbMpShowFreq;
    private javax.swing.JLabel lbMpShowSect;
    private javax.swing.JLabel lbMpShowVisible;
    private javax.swing.JList<GuiChatMessage> liMPChatHistory;
    private javax.swing.JScrollPane spMPChatHistory;

    /**
     * Creates new form MpChatPanel
     */
    public MpChatPanel(GuiMasterController guiInteractionManager) {
        this.guiInteractionManager=guiInteractionManager;
        initComponents();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbMpShowAll = new javax.swing.JLabel();
        lbMpShowFreq = new javax.swing.JLabel();
        lbMpShowSect = new javax.swing.JLabel();
        lbMpShowVisible = new javax.swing.JLabel();
        cbMPChatInput = new javax.swing.JComboBox<String>();
        spMPChatHistory = new javax.swing.JScrollPane();
        liMPChatHistory = new javax.swing.JList<GuiChatMessage>();

        setLayout(new java.awt.GridBagLayout());
        setBackground(Palette.DESKTOP);
        
        lbMpShowAll.setFont(Palette.DESKTOP_FONT); // NOI18N
        lbMpShowAll.setForeground(java.awt.Color.blue);
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
        
        lbMpShowFreq.setFont(Palette.DESKTOP_FONT); // NOI18N
        lbMpShowFreq.setText("FRQ");
        lbMpShowFreq.setName("FRQ");
        lbMpShowFreq.setForeground(Color.white);
        lbMpShowFreq.setToolTipText("Show only messages of contacts in radar range");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(lbMpShowFreq, gridBagConstraints);

        lbMpShowFreq.addMouseListener(guiInteractionManager.getMpChatManager().getFilterMouseListener());
        
//        lbMpShowSect.setFont(Palette.DESKTOP_FONT); // NOI18N
//        lbMpShowSect.setText("RNG");
//        lbMpShowSect.setName("RNG");
//        lbMpShowSect.setToolTipText("Show only messages of contacts in radar range");
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 2;
//        gridBagConstraints.gridy = 0;
//        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
//        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
//        add(lbMpShowSect, gridBagConstraints);
//
//        lbMpShowSect.addMouseListener(guiInteractionManager.getMpChatManager().getFilterMouseListener());
        
        lbMpShowVisible.setFont(Palette.DESKTOP_FONT); // NOI18N
        lbMpShowVisible.setText("VIS");
        lbMpShowVisible.setName("VIS");
        lbMpShowVisible.setToolTipText("Show only messages of visible contacts");
        lbMpShowVisible.setForeground(Color.white);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        add(lbMpShowVisible, gridBagConstraints);

        lbMpShowVisible.addMouseListener(guiInteractionManager.getMpChatManager().getFilterMouseListener());
        
        cbMPChatInput.setEditable(true);
        cbMPChatInput.setModel(new javax.swing.DefaultComboBoxModel<String>(new String[] { "" }));
//        cbMPChatInput.getEditor().getEditorComponent().setForeground(Palette.DESKTOP_TEXT);
//          cbMPChatInput.getEditor().getEditorComponent().setBackground(Palette.DESKTOP);
//        cbMPChatInput.setForeground(Palette.DESKTOP_TEXT);
//        cbMPChatInput.setBackground(Palette.DESKTOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.BASELINE;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 4);
        add(cbMPChatInput, gridBagConstraints);

        cbMPChatInput.addActionListener(guiInteractionManager.getMpChatManager());
        guiInteractionManager.getMpChatManager().setChatMessageBox(cbMPChatInput);
        
        liMPChatHistory.setModel(guiInteractionManager.getMpChatManager());
        liMPChatHistory.setCellRenderer(new MpChatListCellRenderer());
        liMPChatHistory.setBackground(Palette.DESKTOP);
        liMPChatHistory.setForeground(Palette.DESKTOP_TEXT);
        spMPChatHistory.setViewportView(liMPChatHistory);
        guiInteractionManager.getMpChatManager().setChatHistory(liMPChatHistory);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 4, 4);
        add(spMPChatHistory, gridBagConstraints);
    }
    
    public void resetFilters() {
        lbMpShowAll.setForeground(Color.white);
        lbMpShowFreq.setForeground(Color.white);
        lbMpShowSect.setForeground(Color.white);
        lbMpShowVisible.setForeground(Color.white);
    }
    
    public void selectFilter(javax.swing.JLabel l) {
        l.setForeground(Color.blue);
    }
}
