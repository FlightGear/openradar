/**
 * Copyright (C) 2013 Wolfram Wagner
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
package de.knewcleus.openradar.rpvd.contact;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.JMenuItem;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.setup.AirportData;
/**
 * This class encapsulates the different ways a layout of the data block can be done.
 * The available data layouts appear in the map menu and can be selected anytime at runtime.
 *
 * @author Wolfram Wagner
 *
 */

public class DatablockLayoutManager {

    private AirportData data;
    private volatile ADatablockLayout activeLayout;
    private List<ADatablockLayout> layoutList = new ArrayList<ADatablockLayout>();
    private Map<String,ADatablockLayout> layoutModes = new TreeMap<String,ADatablockLayout>();
    private volatile MenuActionListener menuActionListener;

    public DatablockLayoutManager(AirportData data) {
        this.data=data;

        ADatablockLayout l = new TraditionalLayout(this);
        layoutList.add(l);
        layoutModes.put(l.getName(),l);
        activeLayout=l;

        l = new SimulationLayout(this);
        layoutList.add(l);
        layoutModes.put(l.getName(),l);

        l = new PureSimulationLayout(this);
        layoutList.add(l);
        layoutModes.put(l.getName(),l);

    }

    public synchronized void setData(AirportData data) {
        this.data=data;
    }

    public synchronized AirportData getData() {
        return data;
    }

    public List<ADatablockLayout> getLayoutModes() {
        return layoutList;
    }

    public List<String> getDisplayTextList() {
        ArrayList<String> result = new ArrayList<String>();
        for(ADatablockLayout l : layoutList) {
            result.add(l.getMenuText());
        }
        return result;
    }


    public synchronized ADatablockLayout getActiveLayout() {
        return activeLayout;
    }

    public synchronized void setActiveLayout(GuiMasterController master, ADatablockLayout activeLayout) {
        if(this.activeLayout!=activeLayout) {
            this.activeLayout = activeLayout;
            if(master!=null) {
                data.storeAirportData(master);
            }
        }
    }

    public synchronized void setActiveLayout(GuiMasterController master, String newLayout) {
        ADatablockLayout l =  layoutModes.get(newLayout);
        if(l!=null) {
            setActiveLayout(master, l);
        }
    }
//    public synchronized void setActiveLayoutByMenuText(GuiMasterController master, String menuText) {
//        for(ADatablockLayout l : layoutList) {
//            if(l.getMenuText().equals(menuText)) {
//                setActiveLayout(master, l);
//            }
//        }
//    }
//

    public synchronized ActionListener getActionListener(GuiMasterController master) {
        if(menuActionListener==null) {
            menuActionListener=new MenuActionListener(master);
        }
        return menuActionListener;
    }

    private class MenuActionListener implements ActionListener {

        private final GuiMasterController master;

        public MenuActionListener(GuiMasterController master) {
            this.master=master;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String name = ((JMenuItem)e.getSource()).getName();
            setActiveLayout(master, name);
        }

    }

    public void restoreSelectedLayoutFrom(GuiMasterController master, Properties p) {
        String name = p.getProperty("radar.datablockLayout","");
        ADatablockLayout l = layoutModes.get(name);
        if(l!=null) {
            setActiveLayout(null, l); // null avoids saving of properties, while they are not read completely
        }
    }

    public synchronized void addSelectedLayoutTo(Properties p) {
        p.setProperty("radar.datablockLayout", getActiveLayout().getName());

    }

    public int getIndexOfActiveLayout() {
        return layoutList.indexOf(activeLayout);
    }
}
