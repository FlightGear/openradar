package de.knewcleus.radar.ui.labels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import de.knewcleus.radar.ui.aircraft.Aircraft;

public abstract class AbstractTextLabelElement extends AbstractLabelElement implements ILabelElement {
	public AbstractTextLabelElement(ILabelDisplay labelDisplay, Aircraft aircraft) {
		super(labelDisplay, aircraft);
	}

	protected abstract String getText();

	@Override
	public void layout() {
		String text=getText();
		
		if (text==null || text.length()==0) {
			/* Special case were we have no text at all */
			minimumSize=new Dimension(0,0);
			return;
		}
		
		final Component displayComponent=labelDisplay.getDisplayComponent();
		FontMetrics fm=displayComponent.getFontMetrics(displayComponent.getFont());
		
		ascent=fm.getMaxAscent();
		
		int height=ascent+fm.getMaxDescent();
		int width=fm.stringWidth(text);
		
		minimumSize=new Dimension(width,height);
	}

	@Override
	public void paint(Graphics2D g2d) {
		String text=getText();
		
		g2d.drawString(text, bounds.x, bounds.y+ascent);
	}
}
