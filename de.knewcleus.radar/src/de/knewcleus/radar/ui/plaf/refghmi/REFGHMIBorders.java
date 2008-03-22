package de.knewcleus.radar.ui.plaf.refghmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.border.Border;

import de.knewcleus.radar.ui.Palette;


public class REFGHMIBorders {
	protected static Color getHightlightColor(Component c) {
		Color background=c.getBackground();
		final int r,g,b;
		
		r=Math.min(255,background.getRed()+20);
		g=Math.min(255,background.getGreen()+20);
		b=Math.min(255,background.getBlue()+20);
		return new Color(r,g,b);
	}
	
	public static class ButtonBorder implements Border {
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
			
			Color highlight=getHightlightColor(c);
			g.setColor((drawPressed?Palette.SHADOW:highlight));
			g.drawLine(0, 0, w-1, 0);
			g.drawLine(0, 0, 0, h-1);
			
			g.setColor((drawPressed?highlight:Palette.SHADOW));
			g.drawLine(w-1, h-1, 0, h-1);
			g.drawLine(w-1, h-1, w-1, 0);
		}
	}
	
	public static class SliderBorder implements Border {
		protected final Insets borderInsets=new Insets(1,1,1,1);
		
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
			g.setColor(Palette.SHADOW);
			g.drawLine(0, 0, w-1, 0);
			g.drawLine(0, 0, 0, h-1);
			
			g.setColor(getHightlightColor(c));
			g.drawLine(w-1, h-1, 0, h-1);
			g.drawLine(w-1, h-1, w-1, 0);
		}
	}
}
