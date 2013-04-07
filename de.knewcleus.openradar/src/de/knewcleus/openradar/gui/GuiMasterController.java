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
package de.knewcleus.openradar.gui;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.JTextPane;

import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodToCartTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.multiplayer.IPlayerRegistry;
import de.knewcleus.openradar.gui.chat.MpChatManager;
import de.knewcleus.openradar.gui.contacts.RadarContactController;
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
    private AirportData dataRegistry;;
    private GuiRadarBackend radarBackend;
    private RadarManager radarManager;
    private RadarContactController radarContactManager;
    private MpChatManager mpChatManager;
    private StatusManager statusManager;
    private MetarReader metarReader;
    private RadioController radioManager;
    private MainFrame mainFrame = null;
    private JTextPane detailsArea = null;

    private final TrackManager trackManager = new TrackManager();

    private String airportCode = null;
    private volatile FGMPClient<TargetStatus> radarProvider;

    public GuiMasterController(AirportData data) {
        this.dataRegistry = data;
        // init managers
        radarBackend = new GuiRadarBackend(this);
        radarManager = new RadarManager(this, radarBackend);
        radarContactManager = new RadarContactController(this, radarBackend);
        metarReader = new MetarReader(dataRegistry);
        radioManager = new RadioController(this);
        statusManager = new StatusManager(this);
        mpChatManager = new MpChatManager(this);
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
        // initialize the front end and load environment data
        mainFrame = new MainFrame(this);

        if(!setupDialog.getIcons().isEmpty()) {
            mainFrame.setIconImages(setupDialog.getIcons());
        }

        mainFrame.getRadarScreen().setup(airportCode, this, setupDialog);
        initMpRadar();
        mainFrame.getRadarScreen().initRadarData();
        metarReader.start(); // loads metar and refreshes the runway panel
        radioManager.init();
        statusManager.setSelectedCallSign("-nobody-");
        dataRegistry.loadAirportData(this);
        radarBackend.validateToggles();
        radarBackend.addRadarViewListener(mpChatManager); // forwards Zoom and center changes to MPChat

        initShortCuts();
        // ready, so display it
        mainFrame.setDividerPosition();
        mainFrame.setVisible(true);
    }

    private void initMpRadar() throws Exception {
        /* Install the radar data provider(s) */
        final IPlayerRegistry<TargetStatus> playerRegistry = new FGMPRegistry();
        // register GUI contact list, to be updated, whenever a contact appears
        // or disappears
        playerRegistry.registerListener(getRadarContactManager());

        final Position clientPosition = new Position(dataRegistry.getLon(), dataRegistry.getLat(), dataRegistry.getElevationM());
        final GeodToCartTransformation geodToCartTransformation = new GeodToCartTransformation(Ellipsoid.WGS84);
        //
        radarProvider = new FGMPClient<TargetStatus>(playerRegistry,
                                                     dataRegistry.getInitialATCCallSign(),
                                                     "OpenRadar",
                                                     geodToCartTransformation.forward(clientPosition),
                                                     dataRegistry.getMpServer(),
                                                     dataRegistry.getMpServerPort(),
                                                     dataRegistry.getMpLocalPort());

        // register MP Chat (send & receive)
        radarProvider.addChatListener(getMpChatManager());
        getMpChatManager().setMpBackend(radarProvider);

        // initialize reception
        Thread atcNetworkThread = new Thread(radarProvider, "OpenRadar - AtcNetworkThread");
        atcNetworkThread.setDaemon(true);
        atcNetworkThread.start();
        Thread lossChecker = new Thread("OpenRadar - LossChecker") {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    trackManager.checkForLossOrRetirement();
                    statusManager.updateTime(); // update time display
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

                if (e.getID() == KeyEvent.KEY_PRESSED) {

                    if (e.getKeyCode() == 76 && e.isAltDown()) { // ALT + l
                        logWindow.setVisible(true);
                        e.consume();
                        return true;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        logWindow.setVisible(false);
                        statusManager.hideRunwayDialog();
                        radarContactManager.hideDialogs();
                        mpChatManager.cancelAutoAtcMessage();
                        mpChatManager.requestFocusForInput();
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
                    if (e.getKeyCode() == KeyEvent.VK_F12 && e.isShiftDown()) {
                        radarBackend.reloadStandardRoutes();
                        e.consume();
                        return true;
                    }
                }

                return false;
            }
        });
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
        if(save) {
            dataRegistry.storeAirportData(this);
        }
    }

    public String getCurrentATCCallSign() {
        if (radarProvider == null)
            return "";
        return radarProvider.getCallsign();
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

    public AirportData getDataRegistry() {
        return dataRegistry;
    }

    public void setDataRegistry(AirportData dataRegistry) {
        this.dataRegistry = dataRegistry;
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

    public MetarData getMetar() {
        return metarReader.getMetar();
    }

    public RadioController getRadioManager() {
        return radioManager;
    }

}
