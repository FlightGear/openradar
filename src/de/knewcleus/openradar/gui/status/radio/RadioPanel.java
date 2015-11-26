/**
 * Copyright (C) 2012,2013,2015 Wolfram Wagner
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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.setup.AirportData.FgComMode;

/**
 * This panel contains the radios in the status panel
 *
 * @author Wolfram Wagner
 */
public class RadioPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private GuiMasterController master;
    private RadioController radioManager;
    private JTextField tfAltRadioText;

    private Map<String,JButton> mapPTTButtons = new HashMap<String,JButton>() ;
    private Map<String,JLabel> mapRadioLabels = new HashMap<String,JLabel>() ;

    public RadioPanel(GuiMasterController master, RadioController radioManager) {
        this.master=master;
        this.radioManager=radioManager;
        radioManager.setRadioPanel(this);
        initRadios();

        this.setOpaque(false);
    }

    public void initRadios() {
        this.removeAll();
        this.setLayout(new GridBagLayout());

        int i=0;

        this.setLayout(new GridBagLayout());

        boolean isFgComRestartable = master.getAirportData().getFgComMode() == FgComMode.Internal || master.getAirportData().getFgComMode() == FgComMode.Auto ;

        for(RadioModel model : radioManager.getModels().values()) {

            JLabel lbRadioKey = new JLabel();
            lbRadioKey.setForeground(Color.lightGray);
            lbRadioKey.setName("lb"+model.getRadioKey());
            lbRadioKey.setText(model.getRadioKey());
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(6, 4, 0, 0);
            this.add(lbRadioKey,gridBagConstraints);
            mapRadioLabels.put(model.getRadioKey(), lbRadioKey);

            JComboBox<RadioFrequency> cbFrequencies = new JComboBox<RadioFrequency>();
            cbFrequencies.setName(model.getRadioKey());
            cbFrequencies.setToolTipText("right button lets you define a frequency");
            cbFrequencies.setModel(model);
            if(model.getSelectedItem()==null && model.getSize()>i) model.setSelectedItem(model.getElementAt(i));
            cbFrequencies.setEditable(false);
            cbFrequencies.setRenderer(new RadioFrequencyListCellRenderer());
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = i;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
            gridBagConstraints.ipadx = 20;
            gridBagConstraints.ipady = 4;
            this.add(cbFrequencies,gridBagConstraints);

            cbFrequencies.addActionListener(radioManager.getActionListener());
            cbFrequencies.addMouseListener(radioManager.getRadioModeMouseListener());

            JLabel lbVolume = new JLabel();
            lbVolume.setToolTipText("Volume");
            lbVolume.setForeground(Color.lightGray);
            radioManager.registerLabel(model.getRadioKey(),lbVolume);
            lbVolume.setFont(lbVolume.getFont().deriveFont(8));
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = i;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(6, 4, 0, 0);
            this.add(lbVolume,gridBagConstraints);

            
            JButton btPTT = new JButton();
            btPTT.setText("PTT");
            btPTT.setName("ptt-"+model.getRadioKey());
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = i;
            gridBagConstraints.weightx = isFgComRestartable ? 0 : 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
            this.add(btPTT,gridBagConstraints);

            mapPTTButtons.put(model.getRadioKey(),btPTT);

            btPTT.addMouseListener(radioManager.getPttButtonListener());

            i++;
        }

        if(master.getAirportData().getFgComMode() != FgComMode.Off) {

            JPanel pnlRight = new JPanel();
            pnlRight.setOpaque(false);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weightx = 1;
            gridBagConstraints.gridheight = i;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints.insets = new java.awt.Insets(4, 4, 2, 10);
            this.add(pnlRight,gridBagConstraints);

            pnlRight.setLayout(new GridBagLayout());

            JLabel lbFreq = new JLabel();
            lbFreq.setForeground(Color.lightGray);
            lbFreq.setText("");//"910.00 Test FgCom"
            lbFreq.setFont(lbFreq.getFont().deriveFont(8));
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(6, 4, 0, 0);
            pnlRight.add(lbFreq,gridBagConstraints);

            if(isFgComRestartable) {
                JLabel lbReset = new JLabel();
                lbReset.setName("lbRestart");
                lbReset.setText("Restart");
                lbReset.setFont(lbFreq.getFont().deriveFont(6));
                lbReset.setForeground(Palette.DESKTOP_FILTER_SELECTED);
                lbReset.setToolTipText("<html><body><b>Restart FgCom</b> if sound is distorted...<br/> Use it of users complain!</body></html>");
                lbReset.addMouseListener(radioManager.getPttButtonListener());
                gridBagConstraints = new GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 1;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
                gridBagConstraints.insets = new java.awt.Insets(4, 2, 2, 0);
                pnlRight.add(lbReset,gridBagConstraints);
            }
        }
        
        if(master.getAirportData().isAltRadioTextEnabled()) {
            tfAltRadioText = new JTextField(30);
            tfAltRadioText.setName("tfAltRadioTextEnabled");
            tfAltRadioText.setText(master.getAirportData().getAltRadioText());
            tfAltRadioText.setFont(tfAltRadioText.getFont().deriveFont(8));
            tfAltRadioText.setToolTipText("A text that should be transmitted to the contacts in ATIS");
            tfAltRadioText.addFocusListener(new AltRadioTextFocusListener());
            tfAltRadioText.addKeyListener(new AltRadioTextKeyListener());
            Dimension preferredSize = tfAltRadioText.getPreferredSize();
            preferredSize.setSize(300, 24);//preferredSize.getHeight()-4);
            tfAltRadioText.setPreferredSize(preferredSize);
            tfAltRadioText.setMaximumSize(preferredSize);
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i;
            gridBagConstraints.gridwidth=4;
            gridBagConstraints.weightx=1.0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints.insets = new java.awt.Insets(6, 2, 2, 4);
            this.add(tfAltRadioText,gridBagConstraints);
        }
        doLayout();
        if(getParent()!=null) {
            getParent().invalidate();
            ((JSplitPane)getParent().getParent().getParent()).invalidate();
        }
    }

    public void displayEnabledPTT(String radioKey, boolean enablePTT) {
        if (enablePTT) {
            mapPTTButtons.get(radioKey).setForeground(Color.red);
        } else {
            mapPTTButtons.get(radioKey).setForeground(Color.black);
        }
        mapPTTButtons.get(radioKey).repaint();
    }

    public void setRadioConnectedToServer(String radioKey, boolean success) {
        if(success) {
            mapRadioLabels.get(radioKey).setForeground(Color.lightGray);
            mapRadioLabels.get(radioKey).setToolTipText("FGCom connected");
        } else {
            mapRadioLabels.get(radioKey).setForeground(Color.red);
            mapRadioLabels.get(radioKey).setToolTipText("ERROR: FGCom does not accept frequency (Toggle Log Window ALT+L)");
        }
        mapRadioLabels.get(radioKey).repaint();
    }

    public class AltRadioTextFocusListener extends FocusAdapter {
        @Override
        public void focusGained(FocusEvent e) {
        }

        @Override
        public void focusLost(FocusEvent e) {
            master.getAirportData().setAltRadioText(tfAltRadioText.getText());
            master.getAirportData().storeAirportData(master);
            if(tfAltRadioText.getText().length()>40) {
                tfAltRadioText.setText(tfAltRadioText.getText().substring(0,40));
            }
        }
    }
    
    public class AltRadioTextKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            if(e.getKeyChar() == '\n') {
                master.getAirportData().setAltRadioText(tfAltRadioText.getText());
                master.getAirportData().storeAirportData(master);
                master.getMpChatManager().requestFocusForInput();
                e.consume();
            }
            if(tfAltRadioText.getText().length()>40) {
                tfAltRadioText.setText(tfAltRadioText.getText().substring(0,40));
            }
        }
    }
}
