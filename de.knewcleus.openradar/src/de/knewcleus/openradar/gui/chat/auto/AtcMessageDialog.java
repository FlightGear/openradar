package de.knewcleus.openradar.gui.chat.auto;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;

public class AtcMessageDialog extends JFrame {

    private static final long serialVersionUID = 1L;
    private final GuiMasterController master;
    private final TextManager manager;
    private final JList<AtcMessage> liMessages = new JList<AtcMessage>();

    private JLabel lbCallSign = new JLabel();
    
    public AtcMessageDialog(GuiMasterController master, TextManager manager) {
        this.master = master;
        this.manager = manager;
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

        
        JPanel jPnlContentPane = new JPanel();
        jPnlContentPane.setOpaque(false);
        jPnlContentPane.setLayout(new GridBagLayout());
        setContentPane(jPnlContentPane);

        //lbCallSign
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 4, 8);
        jPnlContentPane.add(lbCallSign, gridBagConstraints);

        liMessages.setModel(manager);
        liMessages.setSelectionForeground(liMessages.getForeground());
        liMessages.setSelectionBackground(liMessages.getBackground());
        liMessages.addMouseListener(new TextMouseListener());
        liMessages.setOpaque(false);
        liMessages.setSelectionBackground(liMessages.getBackground());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 2);
        jPnlContentPane.add(liMessages, gridBagConstraints);
        
    }

    public void setLocation(GuiRadarContact c, MouseEvent e) {

        lbCallSign.setText(c.getCallSign());
        
        Dimension innerSize = liMessages.getPreferredSize();
        setSize(new Dimension((int)innerSize.getWidth()+8, (int)innerSize.getHeight()+8));
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maxBounds = env.getMaximumWindowBounds();

        Point2D p = e.getSource() instanceof JList ?
                ((JComponent) e.getSource()).getLocationOnScreen():
                e.getLocationOnScreen();
        p = new Point2D.Double(p.getX() - this.getWidth() - 10, p.getY());

        int lowerDistanceToScreenBorder=50;
        if(p.getY()+getHeight()>maxBounds.getHeight()-lowerDistanceToScreenBorder) {
            p = new Point2D.Double(p.getX(), maxBounds.getHeight()-getHeight() - lowerDistanceToScreenBorder);
        }
        setLocation(new Point((int) p.getX(), (int) p.getY()));
        setVisible(true);
    }
    
    private class TextMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            AtcMessage msg = manager.getElementAt(liMessages.locationToIndex(e.getPoint()));
            if(msg!=null) {
                closeDialog();
                master.getMpChatManager().setAutoAtcMessage(msg);
            }
        }
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

    private void closeDialog() {
        setVisible(false);
    }
    
}
