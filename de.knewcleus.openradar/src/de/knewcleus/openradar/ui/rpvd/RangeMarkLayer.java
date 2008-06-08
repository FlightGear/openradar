package de.knewcleus.openradar.ui.rpvd;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.IMapProjection;
import de.knewcleus.fgfs.location.MapTransformationHelper;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.openradar.ui.Palette;
import de.knewcleus.openradar.ui.map.IMapLayer;

public class RangeMarkLayer implements IMapLayer {
	protected double rangeMarks=10.0*Units.NM;
	protected static Stroke rangeMarkStroke=new BasicStroke(0.0f);
	protected boolean visible=false;

	@Override
	public String getName() {
		return "R-Rings";
	}
	
	@Override
	public boolean isVisible() {
		return visible;
	}
	
	@Override
	public void setVisible(boolean visible) {
		this.visible=visible;
	}
	
	public void draw(Graphics2D g2d, AffineTransform mapTransformation, IMapProjection projection) {
		if (!isVisible())
			return;
		g2d.setColor(Palette.WINDOW_BLUE);
		g2d.setStroke(rangeMarkStroke);

		final AffineTransform oldTransformation=g2d.getTransform();
		g2d.transform(mapTransformation);
		Rectangle2D clipRect=g2d.getClipBounds().getBounds2D();

		Point2D topLeft=new Point2D.Double(clipRect.getMinX(),clipRect.getMinY());
		Point2D bottomRight=new Point2D.Double(clipRect.getMaxX(),clipRect.getMaxY());
		
		double rSquaredMax=(Math.max(topLeft.getX()*topLeft.getX(), bottomRight.getX()*bottomRight.getX())+
				Math.max(topLeft.getY()*topLeft.getY(),bottomRight.getY()*bottomRight.getY()));

		double rMax=Math.sqrt(rSquaredMax);
		double rMin=Math.max(0, Math.max(Math.max(topLeft.getX(),-bottomRight.getX()),Math.max(-topLeft.getY(), bottomRight.getY())));
		
		rMin=Math.max(rangeMarks,Math.floor(rMin/rangeMarks)*rangeMarks);
		rMax=Math.ceil(rMax/rangeMarks)*rangeMarks;
		
		MapTransformationHelper mapTransformationHelper=new MapTransformationHelper(projection);
		for (double r=rMin;r<=rMax;r+=rangeMarks) {
			Rectangle2D markBox=mapTransformationHelper.toLocal(new Position(-r,-r,0.0), new Position(r,r,0.0));
			Shape mark=new Ellipse2D.Double(markBox.getX(),markBox.getY(),markBox.getWidth(), markBox.getHeight());
			g2d.draw(mark);
		}
		g2d.setTransform(oldTransformation);
	}

}
