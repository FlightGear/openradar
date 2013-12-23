/**
 * Copyright (C) 2012,2013 Wolfram Wagner
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
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.gui.setup.AirportData.FgComMode;

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
    private RadioModeMouseListener radioModeMouseListener = new RadioModeMouseListener();
    private final Map<String, Long> mapLastOns = Collections.synchronizedMap(new HashMap<String, Long>());
    private Thread thread = new Thread(this, "OpenRadar - Radio manager (release PTT)");
    private boolean isRunning = true;
    private final static long SEND_OFF_DELAY = 500;
    private final static long THREAD_SLEEP = 100;

    public RadioController(GuiMasterController guiInteractionManager) {
        this.master = guiInteractionManager;
    }

    public void init() {
        AirportData data = master.getAirportData();
        if(data.getFgComMode()!=FgComMode.Off) {
            fgComController = new FgComController(master, data.getModel(), data.getLon(), data.getLat());

            int i = 0;
            for (Radio r : data.getRadios().values()) {
                String device = r.getKey();
                RadioModel model = new RadioModel(master, device, data.getRadioFrequencies(), r.getRestoredFrequency() ,i++);
                models.put(device, model);
                fgComController.addRadio(data.getFgComPath(), data.getFgComExec(), device, data.getFgComServer(), r.getFgComHost(), r.getFgComPort(), model.getSelectedItem().getCode(), model.getSelectedItem());
            }
            modelList = new ArrayList<RadioModel>(models.values());
            fgComController.start();

            radioPanel.initRadios();
            initShortCuts();
            thread.setDaemon(true);
            thread.start();
        }
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
                if (!((JTextField) cb.getEditor().getEditorComponent()).getText().isEmpty()) {
                    if (cb.isEditable()) {
                        cb.setEditable(false);
                    }
                }
                RadioFrequency rf = (RadioFrequency) cb.getSelectedItem();
                if (fgComController != null) {
                    String radioKey = cb.getName();
                    fgComController.tuneRadio(radioKey, rf.getCode(), rf);
                    if(radioKey.equals("COM0")) {
                        master.getMpChatManager().getMpBackend().setFrequency(rf.getFrequency());
                    }
                }
                // todo tune other radios with same frequency away

                master.getAirportData().storeAirportData(master);
            }

        }
    }

    public MouseListener getPttButtonListener() {
        return pttButtonListener;
    }

    private class PttButtonListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getSource() instanceof JLabel) {
                if(((JLabel)e.getSource()).getName().equals("lbRestart")) {
                    fgComController.restartFgCom();
                }
            }
        }

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
            if(e.getSource() instanceof JButton) {
                JButton btPTT = ((JButton) e.getSource());
                if(!btPTT.getName().equals("lbRestart"))  {
                    String radioKey = btPTT.getName().substring(4); // prefix but_
                    if (fgComController != null) {
                        fgComController.setPttActive(radioKey, enablePtt);
                    }
                    radioPanel.displayEnabledPTT(radioKey, enablePtt);
                }
            }
        }
    }

    public MouseListener getRadioModeMouseListener() {
        return radioModeMouseListener;
    }

    private class RadioModeMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                if (e.getSource() instanceof JComboBox<?>) {
                    @SuppressWarnings("unchecked")
                    JComboBox<RadioFrequency> cb = ((JComboBox<RadioFrequency>) e.getSource());
                    cb.requestFocus();
                    cb.setEditable(true);
                    ((JTextField) cb.getEditor().getEditorComponent()).setText("");
                }
            }
        }
    }

    private void initShortCuts() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            /*
             * The following method handles PTT shortcuts for radios. Here we
             * face a problem: STRG and SHIFT have no auto repeat feature. Real
             * characters have it. That means. For STRG and SHIFT we can use KEY
             * RELEASE to turn off sending, for real keys we need a time based
             * switch off.
             */

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {

                // boolean keyPressed = e.getID() == KeyEvent.KEY_PRESSED;
                // boolean keyReleased = e.getID() == KeyEvent.KEY_RELEASED;
                String radioKey = null;
                boolean consume = false;

                boolean hasAutoRepeat = true;

                if (fgComController != null) {

                    if (e.getKeyCode() == 17 && e.getKeyLocation() == 2 && models.size() > 0) { // RIGHT CTRL
                        hasAutoRepeat = false; // STRG
                        radioKey = modelList.get(0).getRadioKey();
                    }
                    if (e.getKeyCode() == 155 && e.getKeyLocation() == 4 && models.size() > 0) { // NUM0
                        radioKey = modelList.get(0).getRadioKey();
                    }
//                    if (e.getKeyCode() == 16 && e.getKeyLocation() == 2 && models.size() > 1) { // right
//                        hasAutoRepeat = false; // SHIFT
//                        radioKey = modelList.get(1).getRadioKey();
//                    }
                    if (e.getKeyCode() == 35 && e.getKeyLocation() == 4 && models.size() > 1) { // NUM1
                        radioKey = modelList.get(1).getRadioKey();
                    }
                    if (e.getKeyCode() == 225 && e.getKeyLocation() == 4 && models.size() > 2) { // NUM2
                        radioKey = modelList.get(2).getRadioKey();
                    }
                    if (e.getKeyCode() == 34 && e.getKeyLocation() == 4 && models.size() > 3) { // NUM3
                        radioKey = modelList.get(3).getRadioKey();
                    }
                }
                if (radioKey != null) {

                    if (e.getID() == KeyEvent.KEY_PRESSED) {
                        if (!hasAutoRepeat || (hasAutoRepeat && !fgComController.isPttActive(radioKey))) {
                            // System.out.println("Toggle PTT ON for " + radioKey);

                            fgComController.setPttActive(radioKey, true);
                            radioPanel.displayEnabledPTT(radioKey, true);
                        }
                    }
                    if (!hasAutoRepeat && e.getID() == KeyEvent.KEY_RELEASED) {
                        // System.out.println("Toggle PTT OFF for " + radioKey);

                        fgComController.setPttActive(radioKey, false);
                        radioPanel.displayEnabledPTT(radioKey, false);
                    }

                    e.consume();
                    consume = true;

                    synchronized (mapLastOns) {
                        if (hasAutoRepeat) {
                            mapLastOns.put(radioKey, e.getWhen());
                        }
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
                        //System.out.println("Toggle PTT OFF for " + radioKey);
                        fgComController.setPttActive(radioKey, false);
                        radioPanel.displayEnabledPTT(radioKey, false);
                        mapLastOns.remove(radioKey);
                    }
                }
                for (String radioKey : models.keySet()) {
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

    // save and load to file

    public void addSelectedFrequenciesTo(Properties p) {
        for (RadioModel m : models.values()) {
            p.setProperty("radio." + m.getRadioKey(), m.getSelectedItem().getFrequency());
        }
    }

    public void restoreSelectedFrequenciesFrom(Properties p) {
        for (RadioModel m : models.values()) {
            String savedFrequency = p.getProperty("radio." + m.getRadioKey());
            if (savedFrequency != null) {
                if (m.containsFrequency(savedFrequency)) {
                    m.setSelectedItem(m.get(savedFrequency));
                } else {
                    m.setUserFrequency(savedFrequency);
                }
            }
        }
    }

    public String getActiveFrequenciesForDisplay() {
        StringBuffer result = new StringBuffer();
        for(RadioModel model : modelList) {
            if(result.length()>0) {
                result.append(", ");
            }
            String frequency = model.getSelectedItem().getFrequency();
//            String code = model.getSelectedItem().getCode();
            result.append(frequency);
         // until mpchat supports more characters
//            result.append("(");
//            result.append(replaceFunctions(code));
//            result.append(")");
            break; // until mpchat supports more characters
        }

        return result.toString();
    }

//    private Object replaceFunctions(String code) {
//        if(code.contains("GND")) return "GND";
//        if(code.contains("TWR")) return "TWR";
//        if(code.contains("APP")) return "APP";
//        return code;
//    }
}
