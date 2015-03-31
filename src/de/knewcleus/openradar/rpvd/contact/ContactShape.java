/**
 * Copyright (C) 2013 Wolfram Wagner
 *
 * This file is part of OpenRadar.
 *
 * OpenRadar is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OpenRadar. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von OpenRadar.
 *
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie von der Free
 * Software Foundation, Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 *
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne
 * die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public
 * License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 * <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.rpvd.contact;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;

public class ContactShape {

    public enum Symbol {FilledDot,Asterix,FilledDiamond,EmptyDiamond,EmptySquare,Letter}
    private Path2D path = new Path2D.Double();
    private Symbol type;
    private GuiRadarContact contact;

    private double x=0;
    private double y=0;
    private double size = 6;

    private boolean tailVisible = true;

    public synchronized void modify(Symbol type, GuiRadarContact contact, double size) {
        this.contact=contact;
        this.type=type;
        this.size=size;
    }

    public synchronized void setCenter(Point2D center) {
        this.x=center.getX();
        this.y=center.getY();
    }

    public synchronized void paintShape(Graphics2D g2d) {
        double s1 = size/2;
        double s2 = size/2;//*0.8;

        switch(type) {
        case Asterix:
            tailVisible=true;
            path.reset();
            path.append(new Line2D.Double(x-s1,y,x+s1,y),false);
            path.append(new Line2D.Double(x,y-s1,x,y+s1),false);
            path.append(new Line2D.Double(x-s2,y-s2,x+s2,y+s2),false);
            path.append(new Line2D.Double(x-s2,y+s2,x+s2,y-s2),false);
            g2d.draw(path);
            break;
        case EmptyDiamond:
            tailVisible=true;
            path.reset();
            path.append(new Line2D.Double(x-s1,y,x,y+s1),false);
            path.append(new Line2D.Double(x,y+s1,x+s1,y),true);
            path.append(new Line2D.Double(x+s1,y,x,y-s1),true);
            path.append(new Line2D.Double(x,y-s1,x-s1,y),true);
            g2d.draw(path);
            break;
        case EmptySquare:
            tailVisible=true;
            path.reset();
            path.append(new Line2D.Double(x-s1,y+s1,x+s1,y+s1),false);
            path.append(new Line2D.Double(x+s1,y+s1,x+s1,y-s1),true);
            path.append(new Line2D.Double(x+s1,y-s1,x-s1,y-s1),true);
            path.append(new Line2D.Double(x-s1,y-s1,x-s1,y+s1),true);
            g2d.draw(path);
            break;
        case FilledDiamond:
            tailVisible=true;
            path.reset();
            path.append(new Line2D.Double(x-s1,y,x,y+s1),false);
            path.append(new Line2D.Double(x,y+s1,x+s1,y),true);
            path.append(new Line2D.Double(x+s1,y,x,y-s1),true);
            path.append(new Line2D.Double(x,y-s1,x-s1,y),true);
            g2d.fill(path);
            break;
        case Letter:
            tailVisible=true;
            String letter = contact.getAtcLetter();
            Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(letter, g2d);
            g2d.drawString(letter, (float) (x-bounds.getWidth()/2), (float)(y+bounds.getHeight()/2));
            break;
        default:
            // Type.FilledDot
            tailVisible=true;
            path.reset();
            path.append(new Ellipse2D.Double(x-s1,y-s1,size,size),false);
            g2d.fill(path);
            break;

        }
    }

    public Rectangle2D getBounds2D() {
        return path.getBounds2D();
    }

    public boolean contains(Point2D devicePoint) {
        return getBounds2D().contains(devicePoint);
    }

    public synchronized boolean isTailVisible() {
        return tailVisible;
    }
}
