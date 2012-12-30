package de.knewcleus.openradar.gui.contacts;

import java.awt.Color;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact.Alignment;

/**
 * The Panel showing the Radar Contacts in three columns
 * 
 * @author Wolfram Wagner
 */
public class ContactsPanel extends javax.swing.JPanel implements DropTargetListener {

    private static final long serialVersionUID = 1251028249377116215L;
    private GuiMasterController guiInteractionManager;
    private javax.swing.JLabel lbShowAll;
    private javax.swing.JLabel lbShowEmergencies;
    private javax.swing.JLabel lbShowGround;
    private javax.swing.JLabel lbShowLanding;
    private javax.swing.JLabel lbShowStarting;
    private javax.swing.JLabel lbShowTransits;
    private javax.swing.JLabel lbShowUnknown;
    private javax.swing.JList<GuiRadarContact> liRadarContacts;
    private javax.swing.JScrollPane spRadarContacs;

    public ContactsPanel(GuiMasterController guiInteractionManager) {
        this.guiInteractionManager = guiInteractionManager;
        initComponents();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbShowAll = new javax.swing.JLabel();
        lbShowGround = new javax.swing.JLabel();
        lbShowLanding = new javax.swing.JLabel();
        lbShowStarting = new javax.swing.JLabel();
        lbShowTransits = new javax.swing.JLabel();
        lbShowUnknown = new javax.swing.JLabel();
        lbShowEmergencies = new javax.swing.JLabel();
        spRadarContacs = new javax.swing.JScrollPane();
        liRadarContacts = new javax.swing.JList<GuiRadarContact>();

        setLayout(new java.awt.GridBagLayout());
        setBackground(Palette.DESKTOP);

        lbShowAll.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbShowAll.setForeground(java.awt.Color.blue);
        lbShowAll.setText("AUTO");
        lbShowAll.setName("MODE");
        lbShowAll.setToolTipText("Toggle auto ordering of contacts");
        lbShowAll.addMouseListener(guiInteractionManager.getRadarContactManager().getContactFilterMouseListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 6, 2, 0);
        add(lbShowAll, gridBagConstraints);

        // lbShowAll.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        // lbShowAll.setForeground(java.awt.Color.blue);
        // lbShowAll.setText("ALL");
        // lbShowAll.setName("ALL");
        // lbShowAll.setToolTipText("Show all");
        // lbShowAll.addMouseListener(guiInteractionManager.getRadarContactManager().getContactFilterMouseListener());
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 0;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        // gridBagConstraints.insets = new java.awt.Insets(4, 6, 0, 0);
        // add(lbShowAll, gridBagConstraints);
        //
        // lbShowGround.setText("GND");
        // lbShowGround.setName("GROUND");
        // lbShowGround.setToolTipText("Show contacts on ground");
        // lbShowGround.setForeground(java.awt.Color.white);
        // lbShowGround.addMouseListener(guiInteractionManager.getRadarContactManager().getContactFilterMouseListener());
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 1;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        // gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        // add(lbShowGround, gridBagConstraints);
        //
        // lbShowLanding.setText("LND");
        // lbShowLanding.setName("LANDING");
        // lbShowLanding.setToolTipText("Show landing contacts");
        // lbShowLanding.setForeground(java.awt.Color.white);
        // lbShowLanding.addMouseListener(guiInteractionManager.getRadarContactManager().getContactFilterMouseListener());
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 2;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        // gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        // add(lbShowLanding, gridBagConstraints);
        //
        // lbShowStarting.setText("STA");
        // lbShowStarting.setName("STARTING");
        // lbShowStarting.setToolTipText("Show starting contacts");
        // lbShowStarting.setForeground(java.awt.Color.white);
        // lbShowStarting.addMouseListener(guiInteractionManager.getRadarContactManager().getContactFilterMouseListener());
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 3;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        // gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        // add(lbShowStarting, gridBagConstraints);
        //
        // lbShowTransits.setText("TRV");
        // lbShowTransits.setName("TRAVEL");
        // lbShowTransits.setToolTipText("Show contacts travelling");
        // lbShowTransits.setForeground(java.awt.Color.white);
        // lbShowTransits.addMouseListener(guiInteractionManager.getRadarContactManager().getContactFilterMouseListener());
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 4;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        // gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        // add(lbShowTransits, gridBagConstraints);
        //
        // lbShowUnknown.setText("N/C");
        // lbShowUnknown.setName("UNKNOWN");
        // lbShowUnknown.setToolTipText("No contact/Unknown");
        // lbShowUnknown.setForeground(java.awt.Color.white);
        // lbShowUnknown.addMouseListener(guiInteractionManager.getRadarContactManager().getContactFilterMouseListener());
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 6;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        // gridBagConstraints.weightx = 1.0;
        // gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        // add(lbShowUnknown, gridBagConstraints);
        //
        // lbShowEmergencies.setText("EMG");
        // lbShowEmergencies.setName("EMERGENCY");
        // lbShowEmergencies.setToolTipText("Show contacts in transit");
        // lbShowEmergencies.setForeground(java.awt.Color.white);
        // lbShowEmergencies.addMouseListener(guiInteractionManager.getRadarContactManager().getContactFilterMouseListener());
        // gridBagConstraints = new java.awt.GridBagConstraints();
        // gridBagConstraints.gridx = 5;
        // gridBagConstraints.gridy = 0;
        // gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        // gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
        // add(lbShowEmergencies, gridBagConstraints);

        liRadarContacts.setBackground(Palette.DESKTOP);
        liRadarContacts.setToolTipText("<html><body><b>left click:</b> select/move,<br/> <b>left double click:</b> center map on contact, <br/><b>middle click:</b> edit details, <br/><b>right click:</b> show atcmsgs<br/><b>CTRL+left click</b>: neglect</body></html>");
        liRadarContacts.setModel(guiInteractionManager.getRadarContactManager());
        liRadarContacts.setCellRenderer(new RadarContactListCellRenderer(guiInteractionManager));
        liRadarContacts.setForeground(java.awt.Color.white);
        guiInteractionManager.getRadarContactManager().setJList(liRadarContacts);
        liRadarContacts.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        liRadarContacts.setDragEnabled(true);
        liRadarContacts.setDropMode(javax.swing.DropMode.ON);
        liRadarContacts.setDoubleBuffered(false);
        spRadarContacs.getViewport().setView(liRadarContacts);

        liRadarContacts.addMouseListener(guiInteractionManager.getRadarContactManager().getContactMouseListener());
        liRadarContacts.addMouseMotionListener(guiInteractionManager.getRadarContactManager().getContactMouseListener());
        new DropTarget(liRadarContacts, this); // link this with radar contact
                                               // list

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 4, 4);
        add(spRadarContacs, gridBagConstraints);
    }

    // Drop target

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        // TODO Auto-generated method stub

    }

    @Override
    public void drop(DropTargetDropEvent e) {

        if (((DropTarget) e.getSource()).getComponent() == liRadarContacts) {
            int selectedIndex = liRadarContacts.getSelectedIndex();
            ;
            int insertAtIndex = liRadarContacts.locationToIndex(e.getLocation());
            // alignment
            Alignment align = Alignment.RIGHT;
            int totalWidth = liRadarContacts.getSize().width;
            int releasePointX = e.getLocation().x;
            if (releasePointX < totalWidth / 3) {
                align = Alignment.LEFT;
            } else if (releasePointX < 2 * totalWidth / 3) {
                align = Alignment.CENTER;
            } else if (releasePointX > 2 * totalWidth / 3) {
                align = Alignment.RIGHT;
            }

            guiInteractionManager.getRadarContactManager().dragAndDrop(selectedIndex, insertAtIndex, align);
            e.getDropTargetContext().dropComplete(true);

        } else {
            e.rejectDrop();
        }
    }

    public void resetFilters() {
        lbShowAll.setForeground(Color.white);
        lbShowEmergencies.setForeground(Color.white);
        lbShowGround.setForeground(Color.white);
        lbShowLanding.setForeground(Color.white);
        lbShowStarting.setForeground(Color.white);
        lbShowTransits.setForeground(Color.white);
        lbShowUnknown.setForeground(Color.white);
    }

    public void selectFilter(javax.swing.JLabel l) {
        l.setForeground(Color.blue);
    }

}
