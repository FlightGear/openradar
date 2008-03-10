package de.knewcleus.radar.ui.labels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import de.knewcleus.radar.ui.aircraft.AircraftState;

public abstract class AbstractTextLabelElement extends AbstractLabelElement implements ILabelElement {
	public AbstractTextLabelElement(ILabelDisplay labelDisplay, AircraftState aircraftState) {
		super(labelDisplay, aircraftState);
	}

	protected abstract String getText();

	@Override
	public void layout() {
		String text=getText();
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
