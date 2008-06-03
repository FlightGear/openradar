package de.knewcleus.openradar.ui.rpvd;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.MapTransformationHelper;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.openradar.ui.Palette;

public class RangeMarkLayer {
	protected double rangeMarks=10.0*Units.NM;
	protected static Stroke rangeMarkStroke=new BasicStroke(0.0f);

	public void draw(Graphics2D g2d, IDeviceTransformation mapTransformation) {
		g2d.setColor(Palette.WINDOW_BLUE);
		g2d.setStroke(rangeMarkStroke);

		Rectangle2D clipRect=g2d.getClipBounds().getBounds2D();

		Point2D topLeftLocal=new Point2D.Double(clipRect.getMinX(),clipRect.getMinY());
		Point2D bottomRightLocal=new Point2D.Double(clipRect.getMaxX(),clipRect.getMaxY());
		
		Position topLeft=mapTransformation.fromDevice(topLeftLocal);
		Position bottomRight=mapTransformation.fromDevice(bottomRightLocal);

		double rSquaredMax=(Math.max(topLeft.getX()*topLeft.getX(), bottomRight.getX()*bottomRight.getX())+
				Math.max(topLeft.getY()*topLeft.getY(),bottomRight.getY()*bottomRight.getY()));

		double rMax=Math.sqrt(rSquaredMax);
		double rMin=Math.max(0, Math.max(Math.max(topLeft.getX(),-bottomRight.getX()),Math.max(-topLeft.getY(), bottomRight.getY())));
		
		rMin=Math.max(rangeMarks,Math.floor(rMin/rangeMarks)*rangeMarks);
		rMax=Math.ceil(rMax/rangeMarks)*rangeMarks;
		
		MapTransformationHelper mapTransformationHelper=new MapTransformationHelper(mapTransformation);
		for (double r=rMin;r<=rMax;r+=rangeMarks) {
			Rectangle2D markBox=mapTransformationHelper.toLocal(new Position(-r,-r,0.0), new Position(r,r,0.0));
			Shape mark=new Ellipse2D.Double(markBox.getX(),markBox.getY(),markBox.getWidth(), markBox.getHeight());
			g2d.draw(mark);
		}
	}

}
