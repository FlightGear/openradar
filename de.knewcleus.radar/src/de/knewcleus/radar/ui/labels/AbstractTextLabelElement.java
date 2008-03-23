package de.knewcleus.radar.ui.labels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

public abstract class AbstractTextLabelElement extends LabelElement {
	protected abstract String getText();
	
	@Override
	public double getAscent() {
		final Component displayComponent=getDisplayComponent();
		final FontMetrics fm=displayComponent.getFontMetrics(displayComponent.getFont());
		return fm.getMaxAscent();
	}
	
	@Override
	public Dimension2D getMinimumSize() {
		String text=getText();
		
		if (text==null || text.length()==0) {
			/* Special case were we have no text at all */
			return new Dimension(0,0);
		}
		
		final Component displayComponent=getDisplayComponent();
		FontMetrics fm=displayComponent.getFontMetrics(displayComponent.getFont());
		
		int height=fm.getMaxAscent()+fm.getMaxDescent();
		int width=fm.stringWidth(text);
		
		return new Dimension(width,height);
	}

	@Override
	public void paint(Graphics2D g2d) {
		final Rectangle2D bounds=getBounds2D();
		String text=getText();
		
		g2d.drawString(text, (float)bounds.getMinX(), (float)(bounds.getMinY()+getAscent()));
	}
}
