/**
 * Copyright (C) 2012-2015 Wolfram Wagner
 *
 * This file is part of OpenRadar.
 *
 * OpenRadar is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OpenRadar. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von OpenRadar.
 *
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie von der Free
 * Software Foundation, Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 *
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne
 * die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public
 * License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 * <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.gui.setup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.knewcleus.fgfs.navdata.xplane.RawFrequency;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.setup.AirportData.FgComMode;
import de.knewcleus.openradar.gui.status.radio.FgComController;
import de.knewcleus.openradar.rpvd.contact.DatablockLayoutManager;

/**
 * The GUI controller for the setup dialog.
 *
 * @author Wolfram Wagner
 */
public class SetupController {

    private String propertiesFilename, autoStartAirport;
    private SectorBean preselectedAirport = null;
    private SetupDialog setupDialog;
    private SetupActionListener setupActionListener = new SetupActionListener();
    private DefaultListModel<SectorBean> searchResultsModel = new DefaultListModel<SectorBean>();
    private final AirportData data;
    private SectorListSelectionListener listSelectionListener = new SectorListSelectionListener();
    private SectorListMouseListener sectorListMouseListener = new SectorListMouseListener();
    private Map<String, SectorBean> mapExistingSectors = new TreeMap<String, SectorBean>();

    private ZipFile zif = null;;
    private static Reader fgcomPositionsFile = null; // new fgcom file

    private static Logger log = LogManager.getLogger(SetupController.class);

    public SetupController(String propertiesFile, String autoStartAirport) {
        this.propertiesFilename = propertiesFile;
        this.autoStartAirport = autoStartAirport;
        data = new AirportData();
        parseSectorDir(); // fills existing airport list
        showDialog();
    }

    public DatablockLayoutManager getDatablockLayoutManager() {
        return data.getDatablockLayoutManager();
    }

    private void parseSectorDir() {
        searchResultsModel.clear();
        mapExistingSectors.clear();
        mapExistingSectors = new TreeMap<String, SectorBean>();
        File dataDir = new File("data");
        if (!dataDir.exists())
            dataDir.mkdir();
        File[] content = dataDir.listFiles();
        for (File f : content) {
            if (f.isDirectory() && f.getName().length() == 4) {
                String airportCode = f.getName();
                String airportName = "";
                Point2D position = null;
                double magneticDeclination = 0d;
                File propertyFile = new File("data" + File.separator + airportCode + File.separator + "sector.properties");
                if (propertyFile.exists()) {
                    Properties p = loadSectorProperties(airportCode);
                    airportName = p.getProperty("airportName", "");
                    if (p.getProperty("lat") != null && p.getProperty("lon") != null) {
                        double lon = Double.parseDouble(p.getProperty("lon", ""));
                        double lat = Double.parseDouble(p.getProperty("lat", ""));
                        position = new Point2D.Double(lon, lat);
                    }
                    // not needed anymore, replaced by fgfs model
                    // if(p.getProperty("magneticDeclination")==null) {
                    // log.severe("Error: Property 'magneticDeclination' not found in "+propertyFile.getAbsolutePath()+"! Please delete the airport and download it again!");
                    // System.exit(99);
                    // }
                    // magneticDeclination = Double.parseDouble(p.getProperty("magneticDeclination", "0"));
                }
                SectorBean sb = new SectorBean(airportCode, airportName, position, magneticDeclination, true);
                mapExistingSectors.put(airportCode, sb);
                if (sb.getAirportCode().equalsIgnoreCase(autoStartAirport)) {
                    preselectedAirport = sb;
                }
            }
        }
        for (SectorBean sb : mapExistingSectors.values()) {
            searchResultsModel.addElement(sb);
        }
    }

    private void showDialog() {
        setupDialog = new SetupDialog(this);

        setupDialog.setVisible(true);
        setupDialog.preselectAirport(autoStartAirport, preselectedAirport);
    }

    public ActionListener getActionListener() {
        return setupActionListener;
    }

    public ListModel<SectorBean> getSearchResultsModel() {
        return searchResultsModel;
    }

    private class SetupActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JComponent jSource = (JComponent) e.getSource();
            if (jSource.getName().equals("SearchButton") || jSource.getName().equals("SearchBox")) {
                searchAirport(setupDialog.getSearchTerm());
            } else if (jSource.getName().equals("ShowExistingButton")) {
                parseSectorDir();
            } else if (jSource.getName().equals("DownloadButton")) {
                downloadSector(setupDialog.getSelectedSector());
            } else if (jSource.getName().equals("StartButton")) {
                startApplication();
            } else if (jSource.getName().equals("CheckButton")) {
                setupDialog.readInputs(data);
                checkSettings();
            }
        }
    }

    public SectorListSelectionListener getSectorListSelectionListener() {
        return listSelectionListener;
    }

    private class SectorListSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting() == false) {
                setupDialog.sectorSelected(data);
            }
        }
    }

    public SectorListMouseListener getSectorListMouseListener() {
        return sectorListMouseListener;
    }

    private class SectorListMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                @SuppressWarnings("unchecked")
                JList<SectorBean> jList = (JList<SectorBean>) e.getSource();
                SectorBean sb = jList.getSelectedValue();
                if (sb.isSectorDownloaded()) {
                    startApplication();
                } else {
                    downloadSector(setupDialog.getSelectedSector());
                }
            }
        }
    }

    private void searchAirport(String searchTerm) {
        Map<String,SectorBean> mapFindings = new TreeMap<String, SectorBean>();
        BufferedReader ir = null;

        try {
            ir = new BufferedReader(openXPlaneAptDat());
            String line = ir.readLine();
            while(line!=null) {
                if(line.startsWith("1 ")) {
                    StringTokenizer st = new StringTokenizer(line," \t");
                    st.nextElement(); // code "1"
                    st.nextElement(); //
                    st.nextElement(); //
                    st.nextElement(); //
                    String airportCode = st.nextToken();
                    StringBuilder name = new StringBuilder();
                    while(st.hasMoreElements()) {
                        if(name.length()>0) name.append(" ");
                        name.append(st.nextToken());
                    }
                    if(airportCode.toUpperCase().contains(searchTerm.toUpperCase()) || name.toString().toUpperCase().contains(searchTerm.toUpperCase())) {
                        
                        // seach runway
                        line = ir.readLine();
                        while(line!=null) {
                            if(line.startsWith("100 ")) {
                                Point2D position = getRunwayPosition(line);
                                if(!mapExistingSectors.containsKey(airportCode)) {
                                    // the base for a new download
                                    SectorBean sb = new SectorBean(airportCode, name.toString(), position, mapExistingSectors.containsKey(airportCode));
                                    mapFindings.put(airportCode, sb);
                                } else {
                                    // add the existing
                                    mapFindings.put(airportCode, mapExistingSectors.get(airportCode));
                                }
                            }
                            else if(line.startsWith("1 ")) {
                                // next airport
                                break;
                            }
                            // next line
                            line = ir.readLine();
                        }
                    } else {
                        // airport name did not match
                        line = ir.readLine();
                    }
                }
                if(!line.startsWith("1 ")) {
                    line = ir.readLine();
                }
            }
            searchResultsModel.clear();
            for (SectorBean sb : mapFindings.values()) {
                searchResultsModel.addElement(sb);
            }

        } catch (IOException e) {
            log.error("Error while reading xplane aptdat file!",e);
        } finally {
            if(zif!=null) {
                try {
                    zif.close();
                } catch (IOException e) { }
            }
            if(ir!=null) {
                try {
                    ir.close();
                } catch (IOException e) {}
            }
        }

    }

    /**
     * Parses the line and extracts the position
     * 
     * @param line
     * @return
     */
    private Point2D getRunwayPosition(String line) {
        Point2D result;
        
        StringTokenizer st = new StringTokenizer(line," \t");
        st.nextElement(); // 1 code "100"
        st.nextElement(); // 2
        st.nextElement(); // 3
        st.nextElement(); // 4
        st.nextElement(); // 5
        st.nextElement(); // 6
        st.nextElement(); // 7
        st.nextElement(); // 8
        st.nextElement(); // 9
        float lon1 = Float.parseFloat(st.nextToken());
        float lat1 = Float.parseFloat(st.nextToken());
        try {
            st.nextElement(); // 1
            st.nextElement(); // 2
            st.nextElement(); // 3
            st.nextElement(); // 4
            st.nextElement(); // 5
            st.nextElement(); // 6
            st.nextElement(); // 7
            float lon2 = Float.parseFloat(st.nextToken());
            float lat2 = Float.parseFloat(st.nextToken());
            result = new Point2D.Float((lat1+lat2)/2, (lon1+lon2)/2);
        } catch(Exception e) {
            // one way runway
            result = new Point2D.Float(lat1, lon1);
        }
        return result;
    }

    private void checkSettings() {
        setupDialog.readInputs(data);
    }

    private void downloadSector(SectorBean selectedSector) {
        try {
            data.setAirportName(selectedSector.getAirportName());
            data.setAirportPosition(selectedSector.getPosition());
            SectorCreator.downloadData(data, setupDialog);
            parseSectorDir(); // to find results
        } catch (IOException e) {
            log.error("Error while downloading sector!", e);
        }
    }

    private void startApplication() {
        if (setupDialog.readInputs(data)) {
            // start application
            try {
                data.setCallSign(setupDialog.getCallsign());
                GuiMasterController manager = new GuiMasterController(data);
                manager.start(setupDialog);
                setupDialog.dispose();
            } catch (Exception e) {
                log.error("Error while starting application!", e);
            }
        }
    }

    protected InputStreamReader openXPlaneAptDat() throws IOException {
        final File inputFile = new File("data/AptNav.zip");
        zif = new ZipFile(inputFile);
        Enumeration<? extends ZipEntry> entries = zif.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipentry = entries.nextElement();
            if (zipentry.getName().equals("apt.dat")) {
                return new InputStreamReader(zif.getInputStream(zipentry));
            }
        }
        throw new IllegalStateException("apt.dat not found in sectors/AtpNav.zip!");
    }

    public static Properties loadSectorProperties(String airportCode) {
        Properties p = null;
        File propertyFile = new File("data" + File.separator + airportCode + File.separator + "sector.properties");
        if (propertyFile.exists()) {
            p = new Properties();
            try {
                p.load(new FileReader(propertyFile));
            } catch (IOException e) {
            }
        }
        return p;
    }

    public static void saveSectorProperties(String airportCode, Properties p) {
        // Create sector.properties
        File sectorFile = new File("data" + File.separator + airportCode + File.separator + "sector.properties");

        FileWriter userWriter = null;
        try {
            if (sectorFile.exists())
                sectorFile.delete();
            userWriter = new FileWriter(sectorFile);

            p.store(userWriter, "Open Radar Sector property file");
        } catch (IOException e) {

        } finally {
            if (userWriter != null) {
                try {
                    userWriter.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public String getPropertiesFile() {
        if (propertiesFilename == null) {
            propertiesFilename = "user.properties";
        }

        return propertiesFilename;
    }

    // FGCOM 3

    /**
     * Loads the radio frequencies from fgcoms positions file.
     *
     * @param airportCode
     * @return
     */
    public static Set<RawFrequency> loadRadioFrequenciesFgCom3(AirportData data, String airportCode) {
        Set<RawFrequency> radioFrequencies = new TreeSet<RawFrequency>();
        BufferedReader ir = null;

        try {
            ir = new BufferedReader(openFgComPositionsTxt(data));
            // now search the airport
            String line = ir.readLine();
            while (line != null) {
                StringTokenizer st = new StringTokenizer(line, ",");
                String ac = st.nextToken().trim();

                if (ac != null && airportCode.equals(ac)) {
                    String freq = st.nextToken();
                    st.nextToken(); // lat
                    st.nextToken(); // lon
                    String code = st.nextToken();
                    if (!code.contains("ATIS") && frequencyIsNotKnownYet(radioFrequencies, freq)) {
                        radioFrequencies.add(new RawFrequency(code, freq));
                    }
                }
                line = ir.readLine();
            }
        } catch (IOException e) {
            log.error("Error while reading positions.txt zipfile!", e);
        } finally {
            if (fgcomPositionsFile != null) {
                try {
                    fgcomPositionsFile.close();
                } catch (IOException e) {
                }
            }
            if (ir != null) {
                try {
                    ir.close();
                } catch (IOException e) {
                }
            }
        }
        return radioFrequencies;

    }

    private static boolean frequencyIsNotKnownYet(Set<RawFrequency> radioFrequencies, String freq) {
        BigDecimal newFreq = new BigDecimal(freq);
        for (RawFrequency rf : radioFrequencies) {
            try {
                BigDecimal existingFreq = new BigDecimal(rf.getFrequency());
                BigDecimal existingPlus = existingFreq.add(new BigDecimal("0.005"));
                BigDecimal existingMinus = existingFreq.add(new BigDecimal("-0.005"));
                if (newFreq.compareTo(existingPlus) == 0 || newFreq.compareTo(existingMinus) == 0) {
                    return false;
                }
            } catch (Exception e) {
                return true;
            }

        }
        return true;
    }

    private static Reader openFgComPositionsTxt(AirportData data) throws IOException {
        String pathToFgComExec = data.getFgComPath();
        if (data.getFgComMode() == FgComMode.Auto) {
            // AUTO mode
            pathToFgComExec = System.getProperty("user.dir") + File.separator + "fgcom" + File.separator + "bin";
        }
        final File inputFile = new File(FgComController.getFgComPositionsPath(data, pathToFgComExec));
        if (!inputFile.exists()) {
            log.error("FgCcom frequency file not found at " + inputFile.getAbsolutePath());
            throw new FileNotFoundException("FgCcom frequency file not found at " + inputFile.getAbsolutePath());
        }
        fgcomPositionsFile = new FileReader(inputFile);
        return fgcomPositionsFile;
    }

}
