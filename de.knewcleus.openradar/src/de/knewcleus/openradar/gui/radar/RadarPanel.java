package de.knewcleus.openradar.gui.radar;

import java.awt.Color;

import javax.swing.JPanel;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;

/**
 * The panel containing the radar components...
 * 
 * @author Wolfram Wagner
 */
public class RadarPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private GuiMasterController guiInteractionManager;
    
    private javax.swing.JPanel radarControlBar;
    private RadarMapPanel radarView;
    private javax.swing.JLabel lbZoomClose;
    private javax.swing.JLabel lbZoomGround;
    private javax.swing.JLabel lbZoomSector;
    
    /**
     * Creates new form RadarPanel
     */
    public RadarPanel(GuiMasterController guiInteractionManager) {
        this.guiInteractionManager=guiInteractionManager;
        initComponents();
    }

    public RadarMapPanel getRadarMapPanel() {
        return radarView;
    }
    
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        radarView = new RadarMapPanel(guiInteractionManager);
        radarControlBar = new javax.swing.JPanel();
        lbZoomGround = new javax.swing.JLabel();
        lbZoomClose = new javax.swing.JLabel();
        lbZoomSector = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(100, 100));
        setLayout(new java.awt.GridBagLayout());
        setOpaque(true);
        setBackground(Palette.DESKTOP);
        
        radarView.setBackground(Palette.DESKTOP);

        javax.swing.GroupLayout RadarDummyLayout = new javax.swing.GroupLayout(radarView);
        radarView.setLayout(RadarDummyLayout);
        RadarDummyLayout.setHorizontalGroup(
            RadarDummyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 392, Short.MAX_VALUE)
        );
        RadarDummyLayout.setVerticalGroup(
            RadarDummyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 279, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        add(radarView, gridBagConstraints);

        radarControlBar.setLayout(new java.awt.GridBagLayout());
        radarControlBar.setOpaque(false);
        
        lbZoomGround.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbZoomGround.setText("GROUND");
        lbZoomGround.setName("GROUND");
        lbZoomGround.setToolTipText("Show airport details");
        lbZoomGround.setForeground(java.awt.Color.white);
        lbZoomGround.addMouseListener(guiInteractionManager.getRadarManager().getZoomMouseListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        radarControlBar.add(lbZoomGround, gridBagConstraints);

        lbZoomClose.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbZoomClose.setText("CLOSE");
        lbZoomClose.setName("CLOSE");
        lbZoomClose.setForeground(java.awt.Color.blue);
        lbZoomClose.setToolTipText("Area around airport");
        lbZoomClose.addMouseListener(guiInteractionManager.getRadarManager().getZoomMouseListener());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        radarControlBar.add(lbZoomClose, gridBagConstraints);

        lbZoomSector.setFont(new java.awt.Font("Cantarell", 1, 12)); // NOI18N
        lbZoomSector.setText("SECTOR");
        lbZoomSector.setName("SECTOR");
        lbZoomSector.setToolTipText("Radar range");
        lbZoomSector.setForeground(java.awt.Color.white);
        lbZoomSector.addMouseListener(guiInteractionManager.getRadarManager().getZoomMouseListener());
        radarControlBar.add(lbZoomSector, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        add(radarControlBar, gridBagConstraints);
    }

    public void resetFilters() {
        lbZoomGround.setForeground(Color.white);
        lbZoomClose.setForeground(Color.white);
        lbZoomSector.setForeground(Color.white);
    }
    
    public void selectFilter(javax.swing.JLabel l) {
        l.setForeground(Color.blue);
    }

}
