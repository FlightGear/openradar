package de.knewcleus.openradar.ui.rpvd;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class TagLayout {
	protected final Font font;
	protected final FontRenderContext fontRenderContext;
	protected final float dirX,dirY;
	protected final float tagDistance;
	protected final List<TextLayout> lines=new ArrayList<TextLayout>();
	protected boolean dirty=true;
	
	protected final float tagAnchorX,tagAnchorY;
	protected float tagWidth=0.0f,tagHeight=0.0f;
	protected float tagRadius;
	protected float tagCenterX,tagCenterY;
	protected float tagLeft,tagTop;
	
	public TagLayout(Font font, FontRenderContext fontRenderContext,
					 float dirX, float dirY,
					 float tagDistance) {
		this.font=font;
		this.fontRenderContext=fontRenderContext;
		float l=(float)Math.sqrt(dirX*dirX+dirY*dirY);
		this.dirX=dirX/l;
		this.dirY=dirY/l;
		this.tagDistance=tagDistance;
		
		/* Calculate the tag anchor.
		 * The tag anchor is the position of the point where the
		 * line between tag and object enters the tag.
		 */
		tagAnchorX=dirX*tagDistance;
		tagAnchorY=dirY*tagDistance;
	}
	
	public void addLine(String line) {
		addLine(new TextLayout(line,font,fontRenderContext));
	}
	
	public void addLine(TextLayout layout) {
		dirty=true;
		tagWidth=Math.max(tagWidth,layout.getAdvance());
		tagHeight+=layout.getAscent()+layout.getDescent()+layout.getLeading();
		lines.add(layout);
	}
	
	protected void calculate() {
		if (!dirty)
			return;
		
		/* Calculate the tag center.
		 * The idea is that the center of the tag lies in direction (dirx,diry) as seen
		 * from the object position.
		 */
		// FIXME: Do a proper calculation by intersecting with the bounding box
		tagRadius=Math.max(tagWidth, tagHeight)/2.0f;
		tagCenterX=tagAnchorX+dirX*tagRadius;
		tagCenterY=tagAnchorY+dirY*tagRadius;
		
		/* Calculate the tag base.
		 * The tag base is the relative position of the top left edge of the tag.
		 */
		tagLeft=tagCenterX-tagWidth/2.0f;
		tagTop=tagCenterY-tagHeight/2.0f;
		
		dirty=false;
	}
	
	public void drawTagLine(Graphics2D g2d, Point2D object, float objectRadius) {
		calculate();
		float lineStartX=(float)object.getX()+dirX*objectRadius;
		float lineStartY=(float)object.getY()+dirY*objectRadius;
		float lineEndX=(float)object.getX()+tagAnchorX;
		float lineEndY=(float)object.getY()+tagAnchorY;
		
		Path2D tagLine=new Path2D.Double();
		
		tagLine.moveTo(lineStartX,lineStartY);
		tagLine.lineTo(lineEndX,lineEndY);
		
		g2d.draw(tagLine);
	}
	
	public void drawTag(Graphics2D g2d, Point2D object) {
		calculate();
		
		float baseX=(float)object.getX()+tagLeft;
		float y=(float)object.getY()+tagTop;
		
		for (TextLayout line:lines) {
			y+=line.getAscent();
			
			float x=(line.isLeftToRight()?baseX:baseX+tagWidth-line.getAdvance());
			
			line.draw(g2d, x, y);
			
			y+=line.getDescent()+line.getLeading();
		}
	}
	
	public Rectangle2D getTagBounds(Point2D object) {
		calculate();
		return new Rectangle2D.Double(object.getX()+tagLeft,object.getY()+tagTop,tagWidth,tagHeight);
	}
}
