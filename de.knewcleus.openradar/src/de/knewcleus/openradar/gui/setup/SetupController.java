package de.knewcleus.openradar.gui.setup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.knewcleus.openradar.gui.GuiMasterController;

/**
 * The GUI controller for the setup dialog.
 * 
 * @author Wolfram Wagner
 */
public class SetupController {

    private SetupDialog setupDialog;
    private SetupActionListener setupActionListener = new SetupActionListener();
    private DefaultListModel<SectorBean> searchResultsModel = new DefaultListModel<SectorBean>();
    private AirportData data = new AirportData();// "LFSB","/home/wolfram/bin/fgcomgui-win32-bundle-01192010/wine fgcom",
    private SectorListSelectionListener listSelectionListener = new SectorListSelectionListener();
    private SectorListMouseListener sectorListMouseListener = new SectorListMouseListener();
    private Map<String, SectorBean> mapExistingSectors = new TreeMap<String, SectorBean>();

    private ZipFile zif = null;;
    
    public SetupController() {
        parseSectorDir(); // fills existing airport list
        showDialog();
    }

    private void parseSectorDir() {
        searchResultsModel.clear();
        mapExistingSectors.clear();
        mapExistingSectors = new TreeMap<String, SectorBean>();
        File sectorDir = new File("sectors");
        if(!sectorDir.exists()) sectorDir.mkdir();
        File[] content = sectorDir.listFiles();
        for (File f : content) {
            if (f.isDirectory() && f.getName().length() == 4) {
                String airportCode = f.getName();
                String airportName = "";
                Point2D position = null;
                File propertyFile = new File("sectors" + File.separator + airportCode + File.separator + "sector.properties");
                if (propertyFile.exists()) {
                    Properties p = new Properties();
                    try {
                        p.load(new FileReader(propertyFile));
                    } catch (IOException e) {
                    }
                    airportName = p.getProperty("airportName", "");
                    if(p.getProperty("lat")!=null && p.getProperty("lon")!=null) {
                        double lon = Double.parseDouble(p.getProperty("lon", ""));
                        double lat = Double.parseDouble(p.getProperty("lat", ""));
                        position = new Point2D.Double(lon, lat);
                    }
                }
                SectorBean sb = new SectorBean(airportCode, airportName, position, true);
                mapExistingSectors.put(airportCode, sb);
            }
        }
        for (SectorBean sb : mapExistingSectors.values()) {
            searchResultsModel.addElement(sb);
        }
    }

    private void showDialog() {
        setupDialog = new SetupDialog(this);
        setupDialog.setVisible(true);
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
            } else 
            if (jSource.getName().equals("ShowExistingButton")) {
                parseSectorDir();
            } else          
            if (jSource.getName().equals("DownloadButton")) {
                downloadSector(setupDialog.getSelectedSector());
            } else
            if (jSource.getName().equals("StartButton")) {
                startApplication();
            } else
            if (jSource.getName().equals("CheckButton")) {
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
            if(e.getValueIsAdjusting()==false) {
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
            if(e.getClickCount()==2) {
                @SuppressWarnings("unchecked")
                JList<SectorBean> jList = (JList<SectorBean>)e.getSource();
                SectorBean sb = jList.getSelectedValue();
                if(sb.isSectorDownloaded()) {
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
                        SectorBean sb = new SectorBean(airportCode, name.toString(), mapExistingSectors.containsKey(airportCode));
                        mapFindings.put(airportCode, sb);
                    }
                }
                line = ir.readLine();
            }
            searchResultsModel.clear();
            for (SectorBean sb : mapFindings.values()) {
                searchResultsModel.addElement(sb);
            }
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    private void startApplication() {
        if (setupDialog.readInputs(data)) {
            // start application
            try {
                GuiMasterController manager = new GuiMasterController(data);
                manager.start(setupDialog);
                setupDialog.dispose();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    protected InputStreamReader openXPlaneAptDat() throws IOException {
        final File inputFile = new File("sectors/AptNav.zip");
        zif = new ZipFile(inputFile);
        Enumeration<? extends ZipEntry> entries = zif.entries();
        while(entries.hasMoreElements()) {
            ZipEntry zipentry = entries.nextElement();
            if(zipentry.getName().equals("apt.dat")) {
                return new InputStreamReader(zif.getInputStream(zipentry));
            }
        }
        throw new IllegalStateException("apt.dat not found in sectors/AtpNav.zip!");
    }    
}
