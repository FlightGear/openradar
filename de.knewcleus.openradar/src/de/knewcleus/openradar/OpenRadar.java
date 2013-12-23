/**
 * Copyright (C) 2012 Wolfram Wagner
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
package de.knewcleus.openradar;

import java.awt.EventQueue;
import java.util.Locale;

import javax.swing.UIManager;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.setup.SetupController;

/**
 * This class is the starter of OpenRadar.
 *
 * @author Wolfram Wagner
 */
public class OpenRadar {

    static String propertiesFile = null;
    static String autoStartAirport=null;

    private static Logger log;
    
    public static void main(String[] args) {

        PropertyConfigurator.configureAndWatch( "data/log4j.properties", 60*1000 );
        //System.setProperty("log4j.configurationFile","data/log4j.xml");
        log = LogManager.getLogger(OpenRadar.class); 
        
        
        for(String arg : args) {
            int pos = arg.indexOf("=");
            if(pos>0) {
                String key = arg.substring(0,pos);
                String value = arg.substring(pos+1);
                if("--properties".equalsIgnoreCase(key)) {
                    propertiesFile = value;
                }
                if("--airport".equalsIgnoreCase(key)) {
                    autoStartAirport = value;
                }
            }
        }

        Locale.setDefault(Locale.US);

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            UIManager.put("desktop", Palette.DESKTOP);
            UIManager.put("SplitPane.background", Palette.DESKTOP);
            UIManager.put("SplitPane.foreground", Palette.DESKTOP_TEXT);
            UIManager.put("SplitPane.highlight", Palette.DESKTOP_TEXT);

        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e1) {
                log.error("Error while setting look and feel!",e);
            }
        }

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new SetupController(propertiesFile, autoStartAirport);
            }
        });

    }
}