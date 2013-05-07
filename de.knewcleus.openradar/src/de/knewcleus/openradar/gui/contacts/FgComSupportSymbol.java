package de.knewcleus.openradar.gui.contacts;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.beans.Transient;

import javax.swing.JComponent;

import de.knewcleus.openradar.view.Converter2D;

public class FgComSupportSymbol extends JComponent {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private boolean active = false;

    public void setActive(boolean b) {
        this.active = b;
    }

    @Override
    @Transient
    public Dimension getMinimumSize() {
        return new Dimension(12,12);
    }

    @Override
    @Transient
    public Dimension getMaximumSize() {
        return new Dimension(12,12);
    }

    @Override
    @Transient
    public Dimension getPreferredSize() {
        return new Dimension(12,12);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if(active) {
            drawFgComAntenna((Graphics2D) g, 0, 0);
        }
    }

    public void drawFgComAntenna(Graphics2D g2d, double x, double y) {
        Point2D tipPoint = new Point2D.Double(Math.round(x),Math.round(y));

        // headset
        Point2D center = new Point2D.Double(Math.round(x+4),Math.round(y+5));
        Point2D point2 = Converter2D.getMapDisplayPoint(center, 90, 3);
        Point2D point3 = Converter2D.getMapDisplayPoint(center, 270, 3);

        g2d.draw(new Arc2D.Double(tipPoint.getX(), tipPoint.getY(),8,10,0,270,Arc2D.OPEN));
        g2d.fill(new Ellipse2D.Double(point2.getX()-1, point2.getY()-1,3,3));
        g2d.fill(new Ellipse2D.Double(point3.getX()-1, point3.getY()-1,3,3));
    }

}
