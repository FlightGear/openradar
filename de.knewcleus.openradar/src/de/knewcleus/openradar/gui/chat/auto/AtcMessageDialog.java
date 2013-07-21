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

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;

public class AtcMessageDialog extends JFrame {

    private static final long serialVersionUID = 1L;
    private final GuiMasterController master;
    private final TextManager manager;
    private final JPanel pnlMessages = new JPanel();
    private JScrollPane spList;

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

        spList = new JScrollPane();
        spList.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 2);
        jPnlContentPane.add(spList, gridBagConstraints);

        spList.setViewportView(pnlMessages);

        pnlMessages.setLayout(new GridBagLayout());

        for(int i=0;i<manager.getSize();i++) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = i;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);

            String msg = manager.getElementAt(i).getDisplayMessage();
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            if(msg.isEmpty()) {
                pnlMessages.add(new JSeparator(), gridBagConstraints);
            } else {
                JLabel lb = new JLabel("<html>"+msg+"</html>");
                lb.setName(""+i);
                lb.addMouseListener(new TextMouseListener());
                pnlMessages.add(lb, gridBagConstraints);
            }
        }
        pnlMessages.setOpaque(false);

        doLayout();
    }

    public void setLocation(GuiRadarContact c, MouseEvent e) {

        lbCallSign.setText(c.getCallSign());

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maxBounds = env.getMaximumWindowBounds();

        // size scrollpane
        Dimension preferredSize = pnlMessages.getPreferredSize();
        spList.setPreferredSize(preferredSize.getHeight()>maxBounds.getHeight() ?
                new Dimension(pnlMessages.getPreferredSize().width+30, maxBounds.height-40) : pnlMessages.getPreferredSize());

        // side dialog
        Dimension innerSize = getContentPane().getPreferredSize();
        setSize(new Dimension((int)innerSize.getWidth()+8, (int)innerSize.getHeight()+8));

        Point2D p = /*e.getSource() instanceof JList ?
                ((JComponent) e.getSource()).getLocationOnScreen():*/
                e.getLocationOnScreen();
        p = new Point2D.Double(p.getX() - 100 , p.getY()); //- this.getWidth() - 10

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
            AtcMenuChatMessage msg = manager.getElementAt(Integer.parseInt(((JLabel)e.getSource()).getName()));
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
