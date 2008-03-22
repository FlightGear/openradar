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
			boolean depressed=false;
			if (c instanceof AbstractButton) {
				AbstractButton b=(AbstractButton)c;
				ButtonModel model=b.getModel();
				depressed=(model.isArmed() && model.isPressed()) || model.isSelected();
			}
			
			Color highlight=Palette.getHightlightColor(c.getBackground());
			REFGHMIUtils.drawEtch(g, highlight, Palette.SHADOW, x, y, w, h, depressed);
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
			REFGHMIUtils.drawEtch(g, Palette.getHightlightColor(c.getBackground()), Palette.SHADOW, x, y, w, h, true);
		}
	}
}
