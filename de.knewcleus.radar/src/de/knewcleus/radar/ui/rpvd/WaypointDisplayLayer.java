package de.knewcleus.radar.ui.rpvd;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.navaids.INavaidDatabase;
import de.knewcleus.fgfs.navaids.NamedFix;

public class WaypointDisplayLayer implements IMapLayer {
	protected final INavaidDatabase scenario;
	protected float fixSize=5.0f;
	protected float tagDistance=5.0f;
	protected float tagDirX=1.0f;
	protected float tagDirY=-1.0f;
	protected IDeviceTransformation deviceTransformation;

	protected Stroke fixStroke=new BasicStroke(0.0f);
	
	protected Set<NamedFix> fixesWithDesignator=new HashSet<NamedFix>();

	public WaypointDisplayLayer(INavaidDatabase scenario) {
		this.scenario=scenario;
	}
	
	@Override
	public void prepareForDrawing(IDeviceTransformation transform) {
		this.deviceTransformation=transform;
	}
	
	public void draw(Graphics2D g2d) {
		Collection<NamedFix> fixes=scenario.getFixDB().getFixes();
		
		g2d.setFont(Palette.BEACON_FONT);
		g2d.setColor(Palette.BEACON);
		g2d.setStroke(fixStroke);
		
		for (NamedFix fix: fixes) {
			Point2D pos=deviceTransformation.toDevice(fix.getPosition());
			
			Path2D fixMarker=new Path2D.Double();
			fixMarker.moveTo(pos.getX(), pos.getY()-fixSize);
			fixMarker.lineTo(pos.getX()-fixSize*sin(toRadians(60.0)), pos.getY()+fixSize*cos(toRadians(60.0)));
			fixMarker.lineTo(pos.getX()+fixSize*sin(toRadians(60.0)), pos.getY()+fixSize*cos(toRadians(60.0)));
			fixMarker.closePath();
			g2d.draw(fixMarker);
			
			if (true || fixesWithDesignator.contains(fix)) {
				TagLayout tagLayout=new TagLayout(g2d.getFont(),g2d.getFontRenderContext(),tagDirX,tagDirY,tagDistance+fixSize);
				tagLayout.addLine(fix.getID());
				
				tagLayout.drawTag(g2d, pos);
			}
		}
	}
}
