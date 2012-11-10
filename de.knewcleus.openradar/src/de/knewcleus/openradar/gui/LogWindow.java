package de.knewcleus.openradar.gui;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
/**
 * This window is an in-game log window for the fgcom background processes
 * 
 * @author Wolfram Wagner
 *
 */
public class LogWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTabbedPane jTabbedPane = new JTabbedPane();
    
    private Map<String,JTextArea> map = new TreeMap<String, JTextArea>();

   
    public LogWindow() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle("OpenRadar - Log Window");
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maxBounds = env.getMaximumWindowBounds();

        this.setLocation((int) maxBounds.getWidth() / 2 - 300, (int) maxBounds.getHeight() / 2 - 300);
        this.setSize(600, 600);

        
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
    
    public synchronized void addText(String tab, String text) {
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
            if(newText.contains("\n")) {
                newText = newText.substring(newText.indexOf("\n"));
            } else {
                newText = newText.substring(100);
            }
        }
        textArea.setText(newText);
        textArea.setForeground(Color.black);
        textArea.invalidate();
    }
}
