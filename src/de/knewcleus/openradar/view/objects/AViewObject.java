/**
 * Copyright (C) 2012,2015-2016 Wolfram Wagner
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
package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

/**
 * This abstract class is the parent for the individual objects (Text/Symbols) that are painted for Navdata Objects.
 * Its abstract painter counterpart is AViewObjectPainter.
 * The painter paints all objects that belong to one object, where subclasses of this class are one thing to display.
 *
 * @author Wolfram Wagner
 *
 */
public abstract class AViewObject {

    protected Color color;
    protected Font font;
    protected Stroke stroke = null;
    protected boolean fillPath = false;
    protected volatile Path2D path;
    protected volatile Point2D textCoordinates = new Point2D.Double();
    protected volatile Rectangle2D displayExtents;
    protected volatile String text = null;

    protected volatile int minScalePath = 0;
    protected volatile int maxScalePath = Integer.MAX_VALUE;
    protected volatile int minScaleText = 0;
    protected volatile int maxScaleText = Integer.MAX_VALUE;

    protected volatile Rectangle2D textBounds = null;

    protected Point2D currentDisplayPosition = null;

    public AViewObject(Color color) {
        this(null,color,null,0,Integer.MAX_VALUE);
    }

    public AViewObject(Font font, Color color, String text, int minScaleText, int maxScaleText) {
        this(font, color, text);
        this.minScaleText = minScaleText;
        this.maxScaleText = maxScaleText;
    }

    public AViewObject(Font font, Color color, String text) {
        this.font = font;
        this.color = color;
        this.textCoordinates = textCoordinates;
        this.text = text;
    }

    public synchronized void setTextCoordinates(Point2D textCoordinates) {
        this.textCoordinates = textCoordinates;
    }

    public synchronized void setMinScalePath(int minScalePath) {
        this.minScalePath = minScalePath;
    }

    public synchronized void setMaxScalePath(int maxScalePath) {
        this.maxScalePath = maxScalePath;
    }

    public synchronized void setMinScaleText(int minScaleText) {
        this.minScaleText = minScaleText;
    }

    public synchronized void setMaxScaleText(int maxScaleText) {
        this.maxScaleText = maxScaleText;
    }

    public synchronized void setColor(Color color) {
        this.color=color;
    }

    public synchronized void setText(String text) {
        this.text=text;
    }

    public synchronized void setStroke(Stroke stroke) {
        this.stroke=stroke;
    }

	public synchronized void updateLogicalPosition() {}

    public synchronized Rectangle2D updateDisplayPosition(Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        // maybe path can be shifted from old to new position?
        // until then

        constructPath(currentDisplayPosition, newDisplayPosition, mapViewAdapter);

        currentDisplayPosition = newDisplayPosition;
        displayExtents = null;
        if (path != null || text != null) {
            displayExtents = path != null ? path.getBounds2D() : null;
            if (text != null) {
                if (textBounds == null) {
                    textBounds = new Rectangle2D.Double(-50, +50, 100, 100);
                }
                Rectangle2D textExtents = new Rectangle2D.Double(textCoordinates.getX() + textBounds.getX(), textCoordinates.getY() + textBounds.getY(),
                        textBounds.getWidth(), textBounds.getHeight());
                if (displayExtents == null) {
                    displayExtents = textExtents;
                } else {
                    Rectangle2D.union(textExtents, displayExtents, displayExtents);
                }
            }
        }

        return displayExtents;
    }

    public synchronized void paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
    	
        g2d.setColor(color);
        double currentScale = mapViewAdapter.getLogicalScale();

        // Path
        if (path != null && minScalePath < currentScale && maxScalePath > currentScale) {
            Stroke originalStroke = g2d.getStroke();
            if (stroke != null)
                g2d.setStroke(stroke);
            if (fillPath) {
                g2d.fill(path);
            } else {
                g2d.draw(path);
            }
            g2d.setStroke(originalStroke);
        }
        // Text
        if (text != null && minScaleText < currentScale && maxScaleText > currentScale) {
            if (font != null) {
                g2d.setFont(font);
            }
            FontMetrics fm = g2d.getFontMetrics();
            textBounds = fm.getStringBounds(text, g2d);
        
          	g2d.drawString(text, (float) textCoordinates.getX(), (float) textCoordinates.getY());
        }
    }

    public synchronized boolean contains(Point2D p) {
        return displayExtents!=null ? displayExtents.contains(p) : false;
    }

    
    protected abstract void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter);

    
    protected boolean showNavaid(AirportData data, String globalToggleKey, Color highlightColor, String name) {
        boolean global = data.getRadarObjectFilterState(globalToggleKey); // e.g. FIX
        boolean highlighted = highlightColor!=null;
        boolean isFix = "FIX".equalsIgnoreCase(globalToggleKey);
        boolean runwayFix = isFix && name.matches("[\\w]{4}[\\d]{1}");

        if(!global && !highlighted) {
            // globally disabled and not hightlighted
            return false;
        }
        // globally enabled or at least highlighted
        if(runwayFix && !highlighted) {
            // hide non highlighted runwayFixes
            return false;
        }
        return true;
    }

    public void mouseClicked(MouseEvent e) {   }
}
