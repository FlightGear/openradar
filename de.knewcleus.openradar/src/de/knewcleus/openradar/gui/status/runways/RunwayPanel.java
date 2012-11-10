package de.knewcleus.openradar.gui.status.runways;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.impl.Glideslope;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.weather.MetarData;
/**
 * This panel contains the runways
 * 
 * @author Wolfram Wagner
 */
public class RunwayPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private GuiMasterController guiInteractionManager;
    private static DecimalFormat df = new DecimalFormat("0");

    private List<JCheckBox> cbList = new ArrayList<JCheckBox>();
    
    public RunwayPanel(GuiMasterController guiInteractionManager) {
        this.guiInteractionManager = guiInteractionManager;
        this.setLayout(new java.awt.GridBagLayout());
    }

    public synchronized void refreshRunways(MetarData metar) {
        setBackground(Palette.DESKTOP);
        setForeground(Palette.DESKTOP_TEXT);

        this.removeAll();
        cbList.clear();
        
        int i = 0;

        for (GuiRunway rw : guiInteractionManager.getDataRegistry().getRunways().values()) {
            rw.setRunwayPanel(this);
            rw.setMetar(metar);

            if (Math.abs(rw.getWindDeviation()) <= 90 || (rw.getHeadWindSpeed() > -2 && metar.getWindSpeedGusts() > -2)) {

                JLabel lbRwCode = new JLabel();
                lbRwCode.setName("lb" + rw.getCode());
                lbRwCode.setText(rw.getCode());
                lbRwCode.setToolTipText("True Heading: " + rw.getTrueHeading() + "°");
                Font f = lbRwCode.getFont();
                f = f.deriveFont(Font.PLAIN, 20);
                lbRwCode.setFont(f);
                lbRwCode.setForeground(Color.lightGray);
                GridBagConstraints gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 2 * i;
                gridBagConstraints.gridheight = 2;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
                gridBagConstraints.insets = new java.awt.Insets(6, 4, 0, 0);
                this.add(lbRwCode, gridBagConstraints);

                f = lbRwCode.getFont();
                f = f.deriveFont(Font.PLAIN, 10);

                JCheckBox cbStarting = new JCheckBox();
                cbStarting.setText("Start");
                cbStarting.setFont(f);
                cbStarting.setEnabled(true);
                cbStarting.setName("STARTING");
                cbStarting.setModel(rw.createStartCbModel());
                cbStarting.addActionListener(rw);
                cbStarting.setForeground(Color.lightGray);
                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 1;
                gridBagConstraints.gridy = 2 * i;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(0, 4, -2, 0);
                this.add(cbStarting, gridBagConstraints);

                cbList.add(cbStarting);
                
                JCheckBox cbLanding = new JCheckBox();
                cbLanding.setText("Land");
                cbLanding.setFont(f);
                cbLanding.setEnabled(true);
                cbLanding.setName("LANDING");
                cbLanding.setModel(rw.createLandingCbModel());
                cbLanding.addActionListener(rw);
                cbLanding.setForeground(Color.lightGray);
                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 1;
                gridBagConstraints.gridy = 2 * i + 1;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(-2, 4, 0, 0);
                this.add(cbLanding, gridBagConstraints);
                
                cbList.add(cbLanding);

                
                JLabel lbLength = new JLabel();
                lbLength.setName("lb" + rw.getCode() + "Length");
                lbLength.setText(String.format("%1$3.0f x %2$.0f",rw.getLength(), rw.getWidth()));
                lbLength.setToolTipText("Length x Width");
                lbLength.setFont(f);
                lbLength.setForeground(Color.lightGray);
                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 2;
                gridBagConstraints.gridy = 2 * i;
                //gridBagConstraints.gridwidth = 2;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
                gridBagConstraints.insets = new java.awt.Insets(6, 4, 0, 0);
                this.add(lbLength, gridBagConstraints);

                CrossWindDisplay swd = new CrossWindDisplay(rw);
                swd.setToolTipText("Strength and direction of shear component of wind");
                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 2;
                gridBagConstraints.gridy = 2 * i + 1;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
                this.add(swd, gridBagConstraints);

                JLabel lbShearWind = new JLabel();
                lbShearWind.setText(String.format("%.0fkn / %.0fkn",rw.getHeadWindSpeed(),rw.getCrossWindSpeed()));
                lbShearWind.setToolTipText("Strength of head and crosswind");
                lbShearWind.setFont(f);
                lbShearWind.setForeground(Color.lightGray);
                if(rw.getHeadWindSpeed()<=-1d ) {
                    lbShearWind.setForeground(Palette.WARNING_REARWIND);
                    lbShearWind.setToolTipText("Wind from behind!");
                }
                if(metar.getWindSpeedGusts()>0d) {
                    lbShearWind.setForeground(Palette.WARNING_GUSTS);
                    lbShearWind.setToolTipText("Wind Gusts: "+metar.getWindSpeedGusts()+"kn!");
                }
                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 3;
                gridBagConstraints.gridy = 2 * i;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
                this.add(lbShearWind, gridBagConstraints);

                JLabel lbRelativeWindDirection = new JLabel();
                lbRelativeWindDirection.setText("rWD:" + df.format(rw.getWindDeviation()) + "°");
                lbRelativeWindDirection.setToolTipText("Wind direction relative to runway heading.");
                lbRelativeWindDirection.setFont(f);
                lbRelativeWindDirection.setForeground(Color.lightGray);
                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 3;
                gridBagConstraints.gridy = 2 * i + 1;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
                this.add(lbRelativeWindDirection, gridBagConstraints);

                if(rw.getGlideslope()!=null) {
                    Glideslope gs = rw.getGlideslope();
                    
                    JLabel lbILS = new JLabel();
                    lbILS.setText(String.format("ILS: %1$s %2$3.2f MHz",gs.getIdentification(),gs.getFrequency().getValue()/Units.MHz));
                    lbILS.setToolTipText("ID, Frequency of ILS and elevation runway end");
                    lbILS.setFont(f);
                    lbILS.setForeground(Color.lightGray);
                    gridBagConstraints = new GridBagConstraints();
                    gridBagConstraints.gridx = 4;
                    gridBagConstraints.gridy = 2 * i;
                    gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
                    gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
                    this.add(lbILS, gridBagConstraints);
    
                    JLabel lbILSData = new JLabel();
                    lbILSData.setText(String.format("GS:%1$1.2f° E:%2$,.0f ft",gs.getGlideslopeAngle(),gs.getElevation()/Units.FT));
                    lbILSData.setToolTipText(String.format("Range: %1$,.0f ft",gs.getRange()));
                    lbILSData.setFont(f);
                    lbILSData.setForeground(Color.lightGray);
                    gridBagConstraints = new GridBagConstraints();
                    gridBagConstraints.gridx = 4;
                    gridBagConstraints.gridy = 2 * i + 1;
                    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                    gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
                    this.add(lbILSData, gridBagConstraints);
                }
                JPanel space = new JPanel();
                space.setOpaque(false);
                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 5;
                gridBagConstraints.gridy = 2 * i;
                gridBagConstraints.weightx = 1;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
                gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
                this.add(space, gridBagConstraints);

                gridBagConstraints.weightx = 1;
                
            }
            i++;
        }
        invalidate();
        doLayout();
    }

    public synchronized void updateRunways() {
        for(JCheckBox cb : cbList) {
            cb.repaint();
        }
        
        guiInteractionManager.getRadarBackend().repaint();
    }
}
