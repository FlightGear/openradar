package de.knewcleus.openradar;

import java.awt.EventQueue;

import javax.swing.UIManager;

import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.setup.SetupController;

/**
 * This class is the starter of OpenRadar.
 * 
 * @author Wolfram Wagner
 */
public class OpenRadar {

    public static void main(String[] args) {
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
                e1.printStackTrace();
            }
        }

        EventQueue.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                new SetupController();
            }
        });        
        
    }
}
