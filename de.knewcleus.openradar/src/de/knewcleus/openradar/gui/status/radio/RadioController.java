package de.knewcleus.openradar.gui.status.radio;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JComboBox;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.setup.AirportData;
/**
 * This class is the controller for the radio feature.
 * 
 * @author Wolfram Wagner
 */
public class RadioController implements Runnable {

    private GuiMasterController master;
    private Map<String, RadioModel> models = new TreeMap<String, RadioModel>();
    private RadioPanel radioPanel = null;
    private ArrayList<RadioModel> modelList = null;
    private FgComController fgComController;
    private FrequencyListItemListener frequencyListActionListener = new FrequencyListItemListener();
    private PttButtonListener pttButtonListener = new PttButtonListener();

    private final Map<String, Long> mapLastOns = Collections.synchronizedMap(new HashMap<String, Long>());
    private Thread thread = new Thread(this, "OpenRadar - Radio manager (release PTT)");
    private boolean isRunning = true;
    private final static long SEND_OFF_DELAY = 500;
    private final static long THREAD_SLEEP = 100;

    public RadioController(GuiMasterController guiInteractionManager) {
        this.master = guiInteractionManager;
    }
    
    public void init() {
        AirportData data = master.getDataRegistry();
        fgComController = new FgComController(master, data.getModel(), data.getLon(), data.getLat());

        int i = 0;
        for (Radio r : data.getRadios().values()) {
            String device = r.getKey();
            RadioModel model = new RadioModel(master, device, data.getRadioFrequencies(), i++);
            models.put(device, model);
            fgComController.addRadio(data.getFgComPath(), device, data.getFgComServer(), r.getFgComHost(), r.getFgComPort(), model.getSelectedItem().getCode(), model.getSelectedItem());
        }
        modelList = new ArrayList<RadioModel>(models.values());
        fgComController.start();

        radioPanel.initRadios();
        initShortCuts();
        thread.start();
    }

    void setRadioPanel(RadioPanel radioPanel) {
        this.radioPanel = radioPanel;
    }

    public Map<String, RadioModel> getModels() {
        return models;
    }

    public String getActiveFrequencyList() {
        StringBuilder sb = new StringBuilder();
        for (RadioModel m : models.values()) {
            sb.append(m.getSelectedItem().getFrequency());
            sb.append(";");
        }
        return sb.toString();
    }

    public ActionListener getActionListener() {
        return frequencyListActionListener;
    }

    private class FrequencyListItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JComboBox<?>) {
                @SuppressWarnings("unchecked")
                JComboBox<RadioFrequency> cb = ((JComboBox<RadioFrequency>) e.getSource());
                String radioKey = cb.getName();
                RadioFrequency rf = (RadioFrequency) cb.getSelectedItem();
                if (fgComController != null) {
                    fgComController.tuneRadio(radioKey, rf.getCode(), rf);
                }
                // todo tune other radios with same frequency away
            }

        }

    }

    public MouseListener getPttButtonListener() {
        return pttButtonListener;
    }

    private class PttButtonListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            managePtt(e, true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            managePtt(e, false);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            managePtt(e, false);
        }

        private void managePtt(MouseEvent e, boolean enablePtt) {
            // if (e.getSource() instanceof JButton) {
            JButton btPTT = ((JButton) e.getSource());
            String radioKey = btPTT.getName().substring(4); // prefix but_
            if (fgComController != null) {
                fgComController.setPttActive(radioKey, enablePtt);
            }
            radioPanel.displayEnabledPTT(radioKey, enablePtt);

            // }
        }
    }

    private void initShortCuts() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {

//                boolean keyPressed = e.getID() == KeyEvent.KEY_PRESSED;
//                boolean keyReleased = e.getID() == KeyEvent.KEY_RELEASED;

                String radioKey = null;
                if (fgComController != null && e.getID() == KeyEvent.KEY_PRESSED) {

                    if (e.getKeyCode() == 155 && e.getKeyLocation() == 4 && models.size() > 0) {
                        radioKey = modelList.get(0).getRadioKey();
                    }
                    if (e.getKeyCode() == 35 && e.getKeyLocation() == 4 && models.size() > 1) {
                        radioKey = modelList.get(1).getRadioKey();
                    }
                    if (e.getKeyCode() == 255 && e.getKeyLocation() == 4 && models.size() > 2) {
                        radioKey = modelList.get(2).getRadioKey();
                    }
                    if (e.getKeyCode() == 99 && e.getKeyLocation() == 4 && models.size() > 3) {
                        radioKey = modelList.get(3).getRadioKey();
                    }
                }
                boolean consume = false;
                if (radioKey != null) {

                    if (!fgComController.isPttActive(radioKey)) {
                        System.out.println("Toggle PTT ON for " + radioKey);

                        fgComController.setPttActive(radioKey, true);
                        radioPanel.displayEnabledPTT(radioKey, true);
                    }
                    e.consume();
                    consume = true;

                    synchronized (mapLastOns) {
                        mapLastOns.put(radioKey, e.getWhen());
                    }

                }

                return consume;
            }
        });
    }

    @Override
    public void run() {
        while (isRunning) {
            synchronized (mapLastOns) {
                for (String radioKey : new ArrayList<String>(mapLastOns.keySet())) {
                    // turn off PTT after PTT key release
                    long lastOn = mapLastOns.get(radioKey);
                    if (lastOn + SEND_OFF_DELAY < System.currentTimeMillis()) {
                        System.out.println("Toggle PTT OFF for " + radioKey);
                        fgComController.setPttActive(radioKey, false);
                        radioPanel.displayEnabledPTT(radioKey, false);
                        mapLastOns.remove(radioKey);
                    }
                }
                for(String radioKey : models.keySet()) {
                    // publish connection status to gui
                    Radio r = fgComController.getRadio(radioKey);
                    radioPanel.setRadioConnectedToServer(radioKey, r.isConnectedToServer());
                }
            }
            try {
                Thread.sleep(THREAD_SLEEP);
            } catch (InterruptedException e) {
            }
        }

    }
}
