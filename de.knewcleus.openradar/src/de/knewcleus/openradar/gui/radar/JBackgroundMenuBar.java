package de.knewcleus.openradar.gui.radar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JMenuBar;

public class JBackgroundMenuBar extends JMenuBar {
    private static final long serialVersionUID = 1L;
    Color bgColor = Color.WHITE;

    public void setBackground(Color color) {
        bgColor = color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
