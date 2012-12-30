package de.knewcleus.openradar.gui.contacts;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;

public class ContactSettingsDialog extends JFrame {

    private static final long serialVersionUID = 1L;
    private final GuiMasterController master;
    private final RadarContactController controller;
    private volatile GuiRadarContact contact = null;
    
    private JLabel lbCallSign = new JLabel();
    private javax.swing.JScrollPane spDetails;
    private javax.swing.JTextPane tpDetails;
    private JComboBox<String> cbLanguages;

    
    public ContactSettingsDialog(GuiMasterController master, RadarContactController controller) {
        this.master = master;
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        this.addWindowListener(new DialogCloseListener());

        // Determine what the default GraphicsDevice can support.
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        boolean isUniformTranslucencySupported = gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT);
        if(isUniformTranslucencySupported) {
            this.setOpacity(0.8f);
        }

        setLayout(new GridBagLayout());

        spDetails = new javax.swing.JScrollPane();
        spDetails.setMinimumSize(new Dimension(200,160));
        spDetails.setPreferredSize(new Dimension(200,160));
        tpDetails = new javax.swing.JTextPane();

        setForeground(Palette.DESKTOP_TEXT);
        setBackground(Palette.DESKTOP);
        
        tpDetails.setToolTipText("ATC Notes: RETURN save, STRG+RETURN newline");
        tpDetails.addKeyListener(new DetailsKeyListener());
        tpDetails.setOpaque(true);
        tpDetails.setMinimumSize(new Dimension(200,200));
        tpDetails.setPreferredSize(new Dimension(200,200));
        spDetails.setViewportView(tpDetails);
        master.setDetailsArea(tpDetails);
        
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 4, 8);
        add(lbCallSign, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(spDetails, gridBagConstraints);
        
        JLabel lbLanguages = new JLabel("native Language:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 0);
        add(lbLanguages, gridBagConstraints);

        cbLanguages = new JComboBox<String>(controller.getAutoAtcLanguages());
        cbLanguages.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        add(cbLanguages, gridBagConstraints);
    }

    public void show(GuiRadarContact contact, MouseEvent e) {
        this.contact = contact;
        this.tpDetails.setText(contact.getAtcComment());
        
        lbCallSign.setText(contact.getCallSign());
        
        Dimension innerSize = getPreferredSize();
        setSize(new Dimension((int)innerSize.getWidth()+8, (int)innerSize.getHeight()+8));
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maxBounds = env.getMaximumWindowBounds();

        Point2D p = e.getLocationOnScreen();// ((JComponent) e.getSource()).getLocationOnScreen();
        p = new Point2D.Double(p.getX() - this.getWidth() - 10, p.getY());

        int lowerDistanceToScreenBorder=50;
        if(p.getY()+getHeight()>maxBounds.getHeight()-lowerDistanceToScreenBorder) {
            p = new Point2D.Double(p.getX(), maxBounds.getHeight()-getHeight() - lowerDistanceToScreenBorder);
        }
        setLocation(new Point((int) p.getX(), (int) p.getY()));
        doLayout();
        setVisible(true);
        tpDetails.requestFocus();
    }
    
    private class DialogCloseListener extends WindowAdapter {
        @Override
        public void windowClosed(WindowEvent e) {
            closeDialog();
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            closeDialog();
        }

        @Override
        public void windowLostFocus(WindowEvent e) {
            closeDialog();
        }

    }

    public void closeDialog() {
        if(isVisible()) {
            contact.setAtcComment(tpDetails.getText().trim());
            contact.setAtcLanguage(controller.getAutoAtcLanguages(cbLanguages.getSelectedIndex()));
            setVisible(false);
        }
    }

    private class DetailsKeyListener extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            JTextPane ta = (JTextPane)e.getSource();
            String currentText = ta.getText();
            if(e.getKeyChar()==KeyEvent.VK_ENTER 
                    && !e.isControlDown()) {
                int carretPos = ta.getCaretPosition()-1;
                currentText = new StringBuilder(currentText).deleteCharAt(carretPos).toString();
                master.getRadarContactManager().setAtcComment(currentText);
                ta.setText(currentText); // remove newline
                ta.setCaretPosition(carretPos);
                e.consume();

                closeDialog();
            }
            if(e.getKeyChar()==KeyEvent.VK_ENTER 
                    && e.isControlDown()) {
                int carretPos = ta.getCaretPosition();
                currentText = new StringBuilder(currentText).insert(carretPos,"\n").toString();
                master.getRadarContactManager().setAtcComment(currentText); // save and continue
                ta.setText(currentText);
                ta.setCaretPosition(carretPos+1);
                ta.requestFocus();
            }
        }
    }

}
