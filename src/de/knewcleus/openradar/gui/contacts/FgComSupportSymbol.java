/**
 * Copyright (C) 2014-2015 Wolfram Wagner 
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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
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
