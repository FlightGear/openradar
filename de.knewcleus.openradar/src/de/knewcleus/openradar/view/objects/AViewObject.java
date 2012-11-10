package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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

    protected Color color = Color.white;
    protected Font font;
    protected Stroke stroke = null;
    protected boolean fillPath = false;
    protected Path2D path;
    protected volatile Point2D textCoordinates = new Point2D.Double();
    protected Rectangle2D displayExtents;
    protected String text = null;

    protected volatile int minScalePath = 0;
    protected volatile int maxScalePath = Integer.MAX_VALUE;
    protected volatile int minScaleText = 0;
    protected volatile int maxScaleText = Integer.MAX_VALUE;

    protected volatile Rectangle2D textBounds = null;

    protected Point2D currentDisplayPosition = null;

    public AViewObject(Color color) {
        this.color = color;
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

    public void setTextCoordinates(Point2D textCoordinates) {
        this.textCoordinates = textCoordinates;
    }

    public void setMinScalePath(int minScalePath) {
        this.minScalePath = minScalePath;
    }

    public void setMaxScalePath(int maxScalePath) {
        this.maxScalePath = maxScalePath;
    }

    public void setMinScaleText(int minScaleText) {
        this.minScaleText = minScaleText;
    }

    public void setMaxScaleText(int maxScaleText) {
        this.maxScaleText = maxScaleText;
    }

    public void paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
        g2d.setColor(color);
        double currentScale = mapViewAdapter.getLogicalScale();
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
        if (text != null) {
            FontMetrics fm = g2d.getFontMetrics();
            textBounds = fm.getStringBounds(text, g2d);
        }
        if (text != null && minScaleText < currentScale && maxScaleText > currentScale) {
            if (font != null)
                g2d.setFont(font);
            g2d.drawString(text, (float) textCoordinates.getX(), (float) textCoordinates.getY());
        }
    }

    public Rectangle2D updateDisplayPosition(Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
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

    protected abstract void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter);

}
