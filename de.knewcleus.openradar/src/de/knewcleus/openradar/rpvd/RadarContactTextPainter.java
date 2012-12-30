package de.knewcleus.openradar.rpvd;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact.State;
import de.knewcleus.openradar.view.Converter2D;
/**
 * This class paints the radar contact information into the radar screen.
 * 
 * @author Wolfram Wagner
 */
public class RadarContactTextPainter {

    private enum Case { TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT }
    
    private TrackDisplayState trackDisplayState;
    private Point2D currentDevicePosition;
    private Line2D line;
    private volatile RoundRectangle2D background;
    private volatile Rectangle2D displayExtents;

    private final static double LENGTH = 70d;
    private final static double SPACE = 2d;

    private Point2D anchor = null;
    private double newX ;
    private double newY ;
    private Rectangle2D boundsLine1 = null;
    private Rectangle2D boundsLine2 = null;
    private Rectangle2D boundsText = null;
    
    protected volatile Path2D textContainer = new Path2D.Double();
    protected volatile Point2D textOrigin = new Point2D.Double();
    protected volatile FontMetrics fontMetrics = null;
    protected volatile Rectangle2D lastTextDisplayExtents = new Rectangle2D.Double();
    
    public RadarContactTextPainter(TrackDisplayState trackDisplayState) {
        this.trackDisplayState = trackDisplayState;
    }
    
    public Rectangle2D getDisplayExtents(Point2D currentDevicePosition) {
        this.currentDevicePosition = currentDevicePosition;
        if(boundsText==null) {
            // intial case, we don't know about the fontMetricts,so we construct a dummy 
            background = new RoundRectangle2D.Double(currentDevicePosition.getX()-24d,currentDevicePosition.getY()-24d, 20+8, 20+8,10,10);
            line = new Line2D.Double(currentDevicePosition.getX(),currentDevicePosition.getY(),currentDevicePosition.getX()+24d,currentDevicePosition.getY()-24d);
        } else {
            // construct the objects at their new place
            constructBackgroundShapes(currentDevicePosition);
        }
        displayExtents = background.getBounds2D();
        Rectangle2D.union(displayExtents, line.getBounds2D(), displayExtents);
        return displayExtents;
    }

    public void paint(Graphics2D g2d) {
        Font font = new Font("Arial", Font.PLAIN, 10);
        g2d.setFont(font);
        if(fontMetrics==null) {
            fontMetrics = g2d.getFontMetrics();
        }

//        g2d.setColor(Color.blue);
//        g2d.draw(displayExtents);
        // create composite for transparency of background
        
        String textLine1 = String.format("%s %2s",trackDisplayState.guiContact.getCallSign(),trackDisplayState.guiContact.getMagnCourse());
        String textLine2 = String.format("%1s %2s %3s", trackDisplayState.guiContact.getFlightLevel(),trackDisplayState.guiContact.getAirSpeed(),trackDisplayState.guiContact.getVerticalSpeedD()>0?"+":"-");
        boundsLine1 = fontMetrics.getStringBounds(textLine1, g2d);
        boundsLine2 = fontMetrics.getStringBounds(textLine2, g2d);
        boundsText = new Rectangle2D.Double(boundsLine1.getX(), boundsLine1.getY()-boundsLine1.getHeight(), Math.max(boundsLine1.getWidth(), boundsLine2.getWidth()), boundsLine1.getHeight()+boundsLine2.getHeight());

        if(anchor==null) constructBackgroundShapes(currentDevicePosition);
        
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.60f);
        g2d.setComposite(ac);
        g2d.setColor(Palette.LANDMASS);
        g2d.fill(background);
        lastTextDisplayExtents = background.getBounds2D();

        ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f);
        g2d.setComposite(ac);

        if(trackDisplayState.guiContact.isSelected()) {
            // SELECTED
            g2d.setColor(Palette.RADAR_SELECTED);

        } else if(!trackDisplayState.guiContact.isActive()) {
            // INCACTIVE GHOSTS
            g2d.setColor(Palette.RADAR_GHOST);

        } else if(trackDisplayState.guiContact.isNeglect()) {
            // BAD GUYS
            g2d.setColor(Palette.RADAR_GHOST);

        } else if(trackDisplayState.guiContact.getState()==State.IMPORTANT) {
            // CONTROLLED left column
            g2d.setColor(Palette.RADAR_CONTROLLED);

        } else if(trackDisplayState.guiContact.getState()==State.CONTROLLED) {
            // WATCHED middle column
            g2d.setColor(Palette.RADAR_IMPORTANT);

        } else {
            // UNCONTROLLED right column
            g2d.setColor(Palette.RADAR_UNCONTROLLED);
//            // SELECTED
//            g2d.setColor(Palette.RADAR_SELECTED);
//            //g2d.setColor(new Color(205,255,255));
//            g2d.setColor(Palette.YELLOW);
//        } else if(!trackDisplayState.guiContact.isActive()) {
//            // INCACTIVE GHOSTS
//            g2d.setColor(Color.GRAY);
//        } else if(trackDisplayState.guiContact.isNeglect()) {
//            // BAD GUYS
//            g2d.setColor(Color.GRAY);
//        } else if(trackDisplayState.guiContact.getState()==State.IMPORTANT) {
//            // IMPORTANT
//            g2d.setColor(Palette.GREEN);
//            g2d.setColor(Palette.LIGHTBLUE);
//        } else if(trackDisplayState.guiContact.getState()==State.CONTROLLED) {
//            // CONTROLLED
//            g2d.setColor(Palette.YELLOW);
//            g2d.setColor(new Color(90,255,90));
//        } else {
//            g2d.setColor(Palette.GREEN);
//            g2d.setColor(new Color(205,255,255));
        }
        
        g2d.drawString(textLine1,(float)(newX+SPACE),(float)(newY+boundsLine1.getHeight()));
        g2d.drawString(textLine2,(float)(newX+SPACE),(float)(newY+boundsText.getHeight()));
        
        line = new Line2D.Double(currentDevicePosition.getX(),currentDevicePosition.getY(),anchor.getX(),anchor.getY());
        
        g2d.draw(line);
    }

    private void constructBackgroundShapes(Point2D currentDevicePosition) {
        
        double trueCourse = trackDisplayState.guiContact.getGroundSpeedD()>1 ? trackDisplayState.guiContact.getTrueCourseD() : 0d;
        Case layout = getCase(trueCourse);
        
        
        // first text line will start at (dx, dy)
        anchor = Converter2D.getMapDisplayPoint(currentDevicePosition, trueCourse + 135, LENGTH);
        newX = -1; 
        newY = -1;

        switch(layout) {
        case TOP_LEFT:
            newX = anchor.getX()+SPACE;
            newY = anchor.getY()+SPACE;
            break;
        case TOP_RIGHT:
            newX = anchor.getX()-boundsText.getWidth()-SPACE;
            newY = anchor.getY()+SPACE;
            break;
        case BOTTOM_RIGHT:
            newX = anchor.getX()-boundsText.getWidth()+SPACE;
            newY = anchor.getY()-boundsText.getHeight()-2*SPACE;
            break;
        case BOTTOM_LEFT:
            newX = anchor.getX()+SPACE;
            newY = anchor.getY()-boundsText.getHeight()-2*SPACE;
            break;
        }
        
        background = new RoundRectangle2D.Double(newX-SPACE,newY-SPACE, boundsText.getWidth()+4*SPACE, boundsText.getHeight()+4*SPACE,10d,10d);
    }
    
    private Case getCase(double trueCourseD) {
        if(trueCourseD>0 && trueCourseD<=90) return Case.TOP_LEFT;
        if(trueCourseD>90 && trueCourseD<=180) return Case.TOP_RIGHT;
        if(trueCourseD>180 && trueCourseD<=270) return Case.BOTTOM_RIGHT;
        return Case.BOTTOM_LEFT;
    }

    public boolean contains(Point2D devicePoint) {
        return background.contains(devicePoint);
    }

}
