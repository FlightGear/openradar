package de.knewcleus.openradar.view.objects;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class VORSymbol extends AViewObject {

    public enum VORType {
        VOR, VOR_DME, VORTAC
    }

    private VORType vorType = VORType.VOR;
    private static BufferedImage imageVOR;
    private static BufferedImage imageVORTME;
    private static BufferedImage imageVORTAC;
    private Point2D displayPosition;

    public VORSymbol(VORType vorType) {
        super(Palette.CRD_BACKGROUND);
        this.vorType = vorType;
        try {
            imageVOR = ImageIO.read(new File("res/VOR.png"));
            imageVORTME = ImageIO.read(new File("res/VOR-DME.png"));
            imageVORTAC = ImageIO.read(new File("res/VORTAC.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        this.displayPosition = newDisplayPosition;

        path = new Path2D.Double();
        path.append(new Rectangle2D.Double(displayPosition.getX(), displayPosition.getY(), 50d, 50d), false);
    }

    @Override
    public void paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
        // super.paint(g2d, mapViewAdapter);
        int scale = (int)mapViewAdapter.getLogicalScale();
        scale = scale==0 ? 1 : scale; 
        scale = 30 * 10/scale;
        if(scale<20) scale=20;
        if(scale>30) scale=30;
        
        
        if (displayPosition != null) {
            Image image=imageVOR; 
            switch (vorType) {
            case VOR:
                image = imageVOR.getScaledInstance(scale, -1, Image.SCALE_SMOOTH);
                break;
            case VOR_DME:
                image = imageVORTME.getScaledInstance(scale, -1, Image.SCALE_SMOOTH);
                break;
            case VORTAC:
                image = imageVORTAC.getScaledInstance(scale, -1, Image.SCALE_SMOOTH);
                break;
            }
            g2d.drawImage(image, (int) displayPosition.getX() - scale/2, (int) displayPosition.getY() - scale/2, (ImageObserver) null);
        }
    }

}
