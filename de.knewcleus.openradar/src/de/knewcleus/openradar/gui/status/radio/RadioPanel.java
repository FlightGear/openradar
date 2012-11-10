package de.knewcleus.openradar.gui.status.radio;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import de.knewcleus.openradar.gui.GuiMasterController;

/**
 * This panel contains the radios in the status panel
 * 
 * @author Wolfram Wagner
 */
public class RadioPanel extends JPanel {

    private static final long serialVersionUID = 1L;
//    private GuiMasterController master;
    private RadioController radioManager;
    
    private Map<String,JButton> mapPTTButtons = new HashMap<String,JButton>() ;
    private Map<String,JLabel> mapRadioLabels = new HashMap<String,JLabel>() ;
    
    public RadioPanel(GuiMasterController master, RadioController radioManager) {
//        this.master=master;
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
        
        for(RadioModel model : radioManager.getModels().values()) {
            //JPanel radioPanel = new JPanel();
            
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
            cbFrequencies.setModel(model);
            if(model.getSize()>i) model.setSelectedItem(model.getElementAt(i));
            cbFrequencies.setEditable(false);
            cbFrequencies.setRenderer(new RadioFrequencyListCellRenderer());
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = i;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 0);
            this.add(cbFrequencies,gridBagConstraints);

            cbFrequencies.addActionListener(radioManager.getActionListener());

            JButton btPTT = new JButton();
            btPTT.setText("PTT");
            btPTT.setName("ptt-"+model.getRadioKey());
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = i;
            gridBagConstraints.weightx = 1;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
            this.add(btPTT,gridBagConstraints);

            mapPTTButtons.put(model.getRadioKey(),btPTT);
            
            btPTT.addMouseListener(radioManager.getPttButtonListener());
            
            i++;
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

}
