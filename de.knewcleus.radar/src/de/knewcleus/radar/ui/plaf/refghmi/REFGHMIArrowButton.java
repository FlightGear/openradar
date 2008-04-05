package de.knewcleus.radar.ui.plaf.refghmi;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import de.knewcleus.radar.ui.Palette;

public class REFGHMIArrowButton extends JButton {
	private static final long serialVersionUID = 3576739070722626192L;
	protected final static int minimumArrowSize=10;
	protected final static int preferredArrowSize=20;
	protected final static Insets arrowInsets=new Insets(1,1,1,1);
	protected final Border border=new REFGHMIBorders.ButtonBorder();
	
	protected int direction;

	public REFGHMIArrowButton(int direction) {
		this.direction=direction;
		setBorder(border);
		setForeground(Palette.WHITE);
	}
	
	public int getDirection() {
		return direction;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
		repaint();
	}
	
	@Override
	public Dimension getMinimumSize() {
		final Insets insets=getInsets();
		return new Dimension(
					minimumArrowSize+(insets.left+insets.right)+(arrowInsets.left+arrowInsets.right), 
					minimumArrowSize+(insets.top+insets.bottom)+(arrowInsets.top+arrowInsets.bottom));
	}
	
	@Override
	public Dimension getPreferredSize() {
		final Insets insets=getInsets();
		return new Dimension(
					preferredArrowSize+(insets.left+insets.right)+(arrowInsets.left+arrowInsets.right), 
					preferredArrowSize+(insets.top+insets.bottom)+(arrowInsets.top+arrowInsets.bottom));
	}
	
	@Override
	public void paint(Graphics g) {
		final Rectangle clipBounds=g.getClipBounds();
		final Dimension size=getSize();
		g.setColor(getBackground());
		g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
		getBorder().paintBorder(this, g, 0, 0, size.width, size.height);
		
		g.setColor(getForeground());
		
		final Insets insets=getInsets();
		final int x0=insets.left+arrowInsets.left;
		final int y0=insets.top+arrowInsets.top;
		final int w=size.width-(insets.left+insets.right)-(arrowInsets.left+arrowInsets.right);
		final int h=size.height-(insets.top+insets.bottom)-(arrowInsets.top+arrowInsets.bottom);
		
		final Polygon arrow=new Polygon();
		
		switch (direction) {
		case SwingConstants.NORTH:
			arrow.addPoint(x0+w/2, y0);
			arrow.addPoint(x0,y0+h);
			arrow.addPoint(x0+w,y0+h);
			break;
		case SwingConstants.SOUTH:
			arrow.addPoint(x0+w/2, y0+h);
			arrow.addPoint(x0+w,y0);
			arrow.addPoint(x0,y0);
			break;
		case SwingConstants.EAST:
			arrow.addPoint(x0+w, y0+h/2);
			arrow.addPoint(x0, y0+h);
			arrow.addPoint(x0, y0);
			break;
		case SwingConstants.WEST:
			arrow.addPoint(x0, y0+h/2);
			arrow.addPoint(x0+w, y0);
			arrow.addPoint(x0+w, y0+h);
			break;
		}
		
		g.fillPolygon(arrow);
	}
}
