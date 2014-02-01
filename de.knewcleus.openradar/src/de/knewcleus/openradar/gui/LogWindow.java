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
package de.knewcleus.openradar.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import de.knewcleus.openradar.gui.setup.AirportData;
/**
 * This window is an in-game log window for the fgcom background processes
 * 
 * @author Wolfram Wagner
 *
 */
public class LogWindow extends JFrame implements FocusListener {

    private static final long serialVersionUID = 1L;
    private List<Image> icons = new ArrayList<Image>();

    private JTabbedPane jTabbedPane = new JTabbedPane();
    
    private Map<String,JTextArea> map = new TreeMap<String, JTextArea>();

    private final Object modLock = new Object();
    
    public LogWindow() {
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setTitle("OpenRadar - Log Window");

        File iconDir = new File("res/icons");
        if(iconDir.exists()) {
            File[] files = iconDir.listFiles();
            for(File f : files) {
                if(f.getName().matches("OpenRadar.*\\.ico") || f.getName().matches("OpenRadar.*\\.png")
                  || f.getName().matches("OpenRadar.*\\.gif") || f.getName().matches("OpenRadar.*\\.jpg")) {
                    icons.add(new ImageIcon(f.getAbsolutePath()).getImage());
                }
            }
            if(!icons.isEmpty()) {
                setIconImages(icons);
            }
        }
        
        Rectangle maxBounds = AirportData.MAX_WINDOW_SIZE;

        this.setLocation((int) maxBounds.getWidth() / 2 - 300, (int) maxBounds.getHeight() / 2 - 200);
        this.setSize(600, 600);
        //this.setUndecorated(true);
        // Determine what the default GraphicsDevice can support.
//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice gd = ge.getDefaultScreenDevice();
//        boolean isUniformTranslucencySupported = gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT);
//        if(isUniformTranslucencySupported) {
//            this.setOpacity(0.8f);
//        }
        
        
        this.getContentPane().setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill=GridBagConstraints.BOTH;
        gridBagConstraints.weightx=1;
        gridBagConstraints.weighty=1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        this.getContentPane().add(jTabbedPane, gridBagConstraints);

    }
    
    public void addText(String tab, String text) {
        synchronized(modLock) {
            JTextArea textArea = map.get(tab);
            if(textArea==null) {
                javax.swing.JScrollPane scrollPane = new JScrollPane();
                textArea = new JTextArea();
                scrollPane.setViewportView(textArea);
                jTabbedPane.addTab(tab, scrollPane);
    
                textArea.setEditable(false);
                textArea.setForeground(Color.black);
                map.put(tab, textArea);
            }
            
            String oldText = textArea.getText();
            String newText = oldText+text;
            while(newText.length()>100*1000) {
                if(newText.substring(0,100).contains("\n")) {
                    newText = newText.substring(newText.indexOf("\n")+1);
                } else {
                    newText = newText.substring(100);
                }
            }
            textArea.setText(newText);
            textArea.setForeground(Color.black);
            textArea.invalidate();
        }
    }

    public void removeLogs() {
        synchronized (modLock) {
            map.clear();
            jTabbedPane.removeAll();
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
        //setVisible(false);
    }
}
