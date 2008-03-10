/**
 * 
 */
package de.knewcleus.radar.ui.plaf.refghmi;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.border.Border;

import de.knewcleus.radar.ui.Palette;


class ButtonBorder implements Border {
	protected final Insets borderInsets=new Insets(3,3,3,3);
	
	@Override
	public Insets getBorderInsets(Component c) {
		return borderInsets;
	}
	
	@Override
	public boolean isBorderOpaque() {
		return true;
	}
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
		boolean drawPressed=false;
		if (c instanceof AbstractButton) {
			AbstractButton b=(AbstractButton)c;
			ButtonModel model=b.getModel();
			drawPressed=(model.isArmed() && model.isPressed()) || b.isSelected();
		}
		
		g.setColor((drawPressed?Palette.SHADOW:Palette.WFAWN_HIGHLIGHT));
		g.drawLine(0, 0, w-1, 0);
		g.drawLine(0, 0, 0, h-1);
		
		g.setColor((drawPressed?Palette.WFAWN_HIGHLIGHT:Palette.SHADOW));
		g.drawLine(w-1, h-1, 0, h-1);
		g.drawLine(w-1, h-1, w-1, 0);
	}
}