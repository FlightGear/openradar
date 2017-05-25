/**
 * Copyright (C) 2012-2016 Wolfram Wagner
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
package de.knewcleus.openradar.gui;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.JTextPane;

import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodToCartTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.multiplayer.IPlayerRegistry;
import de.knewcleus.openradar.fgfscontroller.FGFSController;
import de.knewcleus.openradar.gui.chat.MpChatManager;
import de.knewcleus.openradar.gui.contacts.RadarContactController;
import de.knewcleus.openradar.gui.flightplan.FlightPlanExchangeManager;
import de.knewcleus.openradar.gui.flightstrips.FlightStripBay;
import de.knewcleus.openradar.gui.flightstrips.config.LogicManager;
import de.knewcleus.openradar.gui.radar.GuiRadarBackend;
import de.knewcleus.openradar.gui.radar.RadarManager;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.gui.setup.SetupDialog;
import de.knewcleus.openradar.gui.status.StatusManager;
import de.knewcleus.openradar.gui.status.radio.RadioController;
import de.knewcleus.openradar.radardata.fgmp.FGMPClient;
import de.knewcleus.openradar.radardata.fgmp.FGMPRegistry;
import de.knewcleus.openradar.radardata.fgmp.TargetStatus;
import de.knewcleus.openradar.tracks.TrackManager;
import de.knewcleus.openradar.util.DeadLockChecker;
import de.knewcleus.openradar.weather.MetarData;
import de.knewcleus.openradar.weather.MetarReader;

/**
 * This class is the central point where the GUI managers are initialized and
 * coordinated.
 *
 * GUI components are usually splitted into a VIEW Part (responsible for
 * displaying the, may include a renderer), a Model (sometimes the invisible
 * default model of SWING) and a Controller that does more complex operations
 * and implements the business logic.
 *
 * @author Wolfram Wagner
 */

public class GuiMasterController {

    private LogWindow logWindow = new LogWindow();
    private final AirportData airportData;
    private GuiRadarBackend radarBackend;
    private RadarManager radarManager;
    private RadarContactController radarContactManager;
    private MpChatManager mpChatManager;
    private StatusManager statusManager;
    private final MetarReader metarReader;
    private RadioController radioManager;
    private MainFrame mainFrame = null;
    private FlightPlanExchangeManager fpExchangeManager;
    private FGFSController fgfsController1 = null;
    private FGFSController fgfsController2 = null;

    private FlightStripBay flightstripbay = null;
    private LogicManager logicManager = null;

    private JTextPane detailsArea = null;

    private final TrackManager trackManager;

    private volatile FGMPClient<TargetStatus> radarProvider;

    private volatile long lastEscPressed = 0;

    public GuiMasterController(AirportData data) {
        this.airportData = data;

        // init managers
        trackManager = new TrackManager();
        radarBackend = new GuiRadarBackend(this);
        radarManager = new RadarManager(this, radarBackend);
        radarContactManager = new RadarContactController(this, radarBackend);
        metarReader= new MetarReader(this);
        radioManager = new RadioController(this);
        statusManager = new StatusManager(this);
        mpChatManager = new MpChatManager(this);
        fpExchangeManager = new FlightPlanExchangeManager(this);
        fgfsController1 = new FGFSController(this, data.getFgfsCamera1Host(), data.getFgfsCamera1Port());
        if(data.isFgfsCamera2Enabled()) {
            fgfsController2 = new FGFSController(this, data.getFgfsCamera2Host(), data.getFgfsCamera2Port());
            // link them
            fgfsController1.setOtherInstance(fgfsController2);
            fgfsController2.setOtherInstance(fgfsController1);
            if(data.isFgfsSlave2To1()) {
                fgfsController1.setSlave(fgfsController2);
            }
        }
        new DeadLockChecker().start();
        logicManager = new LogicManager(this);
    }

    public MainFrame getMainFrame() {
    	return mainFrame;
    }
    
    public LogWindow getLogWindow() {
        return logWindow;
    }

    public TrackManager getTrackManager() {
        return trackManager;
    }

    public MpChatManager getMpChatManager() {
        return mpChatManager;
    }

    /**
     * This method starts the application
     *
     */
    public void start(SetupDialog setupDialog) throws Exception {
        airportData.loadAirportData(this);

        // initialize the front end and load environment data
        mainFrame = new MainFrame(this);

        if(!setupDialog.getIcons().isEmpty()) {
            mainFrame.setIconImages(setupDialog.getIcons());
        }
        mainFrame.getRadarScreen().setup(setupDialog);
        initMpRadar();
        mainFrame.getRadarScreen().initRadarData();
        airportData.restoreRunwaySettings();
        metarReader.start(); // loads metar and refreshes the runway panel
        radioManager.init();
        statusManager.start();
        radarBackend.start(); // forwards Zoom and center changes to MPChat
        radarContactManager.start(); // GUI updater
        mpChatManager.start(); // GUI Updater
        initShortCuts();
        SoundManager.init(airportData);
        fpExchangeManager.start();
        if(airportData.isFgfsCamera1Enabled()) {
                fgfsController1.start();
        }

        airportData.storeAirportData(this); // to store initially set data

        // ready, so display it
        mainFrame.setVisible(true);

        mainFrame.getRadarScreen().showMap(); // move map and display it

        logicManager.LoadLayout();
        
        mainFrame.restoreWindowAndDividerPosition();
    }

    private void initMpRadar() throws Exception {
        /* Install the radar data provider(s) */
        final IPlayerRegistry<TargetStatus> playerRegistry = new FGMPRegistry();
        // register GUI contact list, to be updated, whenever a contact appears
        // or disappears
        playerRegistry.registerListener(getRadarContactManager());

        final Position clientPosition = new Position(airportData.getLon(), airportData.getLat(), airportData.getElevationM());
        final GeodToCartTransformation geodToCartTransformation = new GeodToCartTransformation(Ellipsoid.WGS84);
        //
        radarProvider = new FGMPClient<TargetStatus>(playerRegistry,
                                                     airportData.getCallSign(),
                                                     "OpenRadar",
                                                     geodToCartTransformation.forward(clientPosition),
                                                     airportData.getMpServer(),
                                                     airportData.getMpServerPort(),
                                                     airportData.getMpLocalPort(),
                                                     airportData.getAntennaRotationTime(),
                                                     airportData.isFgfsLocalMPPacketForward1(),
                                                     airportData.getFgfsCamera1Host(),
                                                     airportData.getFgfsLocalMPPacketPort1(),
                                                     airportData.isFgfsLocalMPPacketForward2(),
                                                     airportData.getFgfsCamera2Host(),
                                                     airportData.getFgfsLocalMPPacketPort2());


        radarProvider.setCallsign(airportData.getCallSign());
        // register MP Chat (send & receive)
        radarProvider.addChatListener(getMpChatManager());

        // initialize reception
        Thread atcNetworkThread = new Thread(radarProvider, "OpenRadar - AtcNetworkThread");
        atcNetworkThread.setDaemon(true);
        atcNetworkThread.start();
        Thread lossChecker = new Thread("OpenRadar - LossChecker") {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        // I don't care
                    }
//                    log.info("Checking for lost or retired contacts.");
                    trackManager.checkForLossOrRetirement();
                    statusManager.updateTime(); // update time display
//                    log.info("Checking done.");
                }
            }
        };
        lossChecker.setDaemon(true);
        lossChecker.start();

    }

    private void initShortCuts() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {

                if(e.getID()==KeyEvent.KEY_RELEASED) {
                    if (e.getKeyCode()==KeyEvent.VK_TAB) {
                        if(!radarContactManager.isFlightplanDialogVisible() &&
                           !getStatusManager().isRunwayDialogVisible() &&
                           !getStatusManager().isMetarDialogVisible() &&
                           !getRadarContactManager().getTransponderSettingsDialog().isVisible()) {

                            radarContactManager.displayChatMenu();
                            e.consume();
                            return true;
                        }
                        return false;
                    }
                    if (e.getKeyCode()==130) { // "^"
                        radarContactManager.displayContactDialogForSelectedUser();
                        e.consume();
                        return true;
                    }
                }

                if (e.getID() == KeyEvent.KEY_PRESSED) {

                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        mpChatManager.cancelAutoAtcMessage();
                        closeDialogs(false);
                        e.consume();
                        if(lastEscPressed==0) {
                            lastEscPressed = System.currentTimeMillis();
                        } else {
                            if(System.currentTimeMillis()-lastEscPressed < 2000) {
                                // double ESC press => deselect contact
                                radarContactManager.deselectContact();
                            }
                            lastEscPressed=0;
                        }
                        return true;
                    } else {
                        // other key, not ESC, pressed
                        lastEscPressed=0;
                    }
                    if (e.getKeyCode() == 76 && e.isAltDown()) { // ALT + l
                        logWindow.setVisible(true);
                        e.consume();
                        return true;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_F1) {
                        radarManager.setFilter("GROUND");
                        e.consume();
                        return true;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_F2) {
                        radarBackend.setZoomLevel("TOWER");
                        e.consume();
                        return true;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_F3) {
                        radarBackend.setZoomLevel("APP");
                        e.consume();
                        return true;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_F4) {
                        radarBackend.setZoomLevel("SECTOR");
                        e.consume();
                        return true;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_F5) {
                        radarContactManager.displayChatMenu();
                        e.consume();
                        return true;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_F6) {
                        radarContactManager.displayContactDialogForSelectedUser();
                        e.consume();
                        return true;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_F11) {
                        SoundManager.playChatSound();
                        SoundManager.playContactSound();
                        SoundManager.playWeather();
                        e.consume();
                        return true;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_F12 && !e.isShiftDown() && !e.isControlDown()) {
                        radarBackend.copyMouseLocationToClipboard();
                        e.consume();
                        return true;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_F12 && !e.isShiftDown() && e.isControlDown()) {
                        radarBackend.copyZoomLevelToClipboard();
                        e.consume();
                        return true;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_F12 && e.isShiftDown()) {
                        radarBackend.reloadStandardRoutes();
                        radarContactManager.reloadTexts();
                        e.consume();
                        return true;
                    }
                }

                return false;
            }
        });
    }

    protected void closeDialogs(boolean save) {
        mpChatManager.requestFocusForInput();

        logWindow.setVisible(false);
        statusManager.hideRunwayDialog();
        radarContactManager.hideDialogs(save);
    }

    public FGMPClient<TargetStatus> getRadarProvider() {
        return radarProvider;
    }

    public void setDetailsArea(JTextPane detailsArea) {
        this.detailsArea = detailsArea;
    }

    public RadarManager getRadarManager() {
        return radarManager;
    }

    public void setCurrentATCCallSign(String callsign, boolean save) {
        radarProvider.setCallsign(callsign);
        airportData.setCallSign(callsign);
        if(save) {
            airportData.storeAirportData(this);
        }
    }

    public String getCurrentATCCallSign() {
        return airportData.getCallSign();
    }

    public GuiRadarBackend getRadarBackend() {
        return radarBackend;
    }

    public void setRadarBackend(GuiRadarBackend radarBackend) {
        this.radarBackend = radarBackend;
    }

    public RadarContactController getRadarContactManager() {
        return radarContactManager;
    }

    public AirportData getAirportData() {
        return airportData;
    }

    public StatusManager getStatusManager() {
        return statusManager;
    }

    public String getDetails() {
        return detailsArea.getText();
    }

    public void setDetails(String details) {
        detailsArea.setText(details);
    }

    public MetarReader getMetarReader() {
        return metarReader;
    }

    public synchronized MetarData getAirportMetar() {
        return metarReader.getMetar(getAirportData().getMetarSource());
    }

    public RadioController getRadioManager() {
        return radioManager;
    }

    public FlightPlanExchangeManager getFlightPlanExchangeManager() {
        return fpExchangeManager;
    }

    public boolean isVisible() {
        return mainFrame.isVisible();
    }

    public FGFSController getFgfsController1() {
        return fgfsController1;
    }

    public FGFSController getFgfsController2() {
        return fgfsController2;
    }

    public LogicManager getLogicManager() {
    	return logicManager;
    }

    public FlightStripBay getFlightStripBay() {
    	return flightstripbay;
    }

    public void setFlightStripBay(FlightStripBay flightstripbay) {
    	this.flightstripbay = flightstripbay;
    }

}
