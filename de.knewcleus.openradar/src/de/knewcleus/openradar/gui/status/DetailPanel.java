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
