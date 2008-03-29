package de.knewcleus.radar.ui.vehicles;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

import de.knewcleus.radar.ui.Palette;
import de.knewcleus.radar.ui.plaf.refghmi.REFGHMIUtils;

public class PopupComponentBorder implements Border {
	protected final static int TEXT_SPACING=2;
	protected final static int BORDER_WIDTH=1;
	
	@Override
	public Insets getBorderInsets(Component c) {
		FontMetrics fm=c.getFontMetrics(c.getFont());
		
		int titleHeight=fm.getMaxAscent()+fm.getMaxDescent();
		
		Insets insets=new Insets(BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH);
		insets.top+=titleHeight+2*TEXT_SPACING+2*BORDER_WIDTH;
		insets.left+=TEXT_SPACING;
		insets.right+=TEXT_SPACING;
		return insets;
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		final PopupComponent popup=(PopupComponent)c;
		final String title=popup.getTitle();
		
		final Font font=c.getFont();
		final FontMetrics fm=c.getFontMetrics(font);
		
		int titleHeight=fm.getMaxAscent()+fm.getMaxDescent();
		int titleWidth=fm.stringWidth(title);
		
		int topHeight=titleHeight+2*TEXT_SPACING+2*BORDER_WIDTH;
		
		int textX=x+(width-titleWidth)/2;
		int textY=y+fm.getMaxAscent()+BORDER_WIDTH+TEXT_SPACING;
		
		Color highlight=Palette.getHightlightColor(c.getBackground());
		REFGHMIUtils.drawEtch(g, highlight, Palette.SHADOW, x, y, width, topHeight, false);
		REFGHMIUtils.drawEtch(g, highlight, Palette.SHADOW, x, y+topHeight, width, height-topHeight, false);

		g.drawString(title, textX, textY);
	}
}
