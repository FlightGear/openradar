package de.knewcleus.radar.ui.plaf.refghmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.UIManager;

import de.knewcleus.radar.ui.Palette;

public class REFGHMIIcons {
	public static class CheckBoxIcon implements Icon {
		private	Color controlColor = UIManager.getColor("control");
		private Color highlightColor = Palette.getHightlightColor(controlColor);
		private Color depressedColor = Palette.getDepressedColor(controlColor);
		private Color shadowColor = Palette.SHADOW;
		
		protected final int csize=13;

		@Override
		public int getIconHeight() {
			return csize;
		}

		@Override
		public int getIconWidth() {
			return csize;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			AbstractButton b=(AbstractButton)c;
			ButtonModel model=b.getModel();

			boolean depressed=model.isPressed() || model.isSelected();

			int w=getIconWidth();
			int h=getIconHeight();
			
			g.setColor(depressed?depressedColor:controlColor);
			g.fillRect(x,y,w,h);
			
			g.setColor(depressed?shadowColor:highlightColor);
			g.drawLine(x, y, x+w-1, y);
			g.drawLine(x, y, x, y+h-1);
			
			g.setColor(depressed?highlightColor:shadowColor);
			g.drawLine(x+w-1, y, x+w-1, y+h-1);
			g.drawLine(x, y+h-1, x+w-1, y+h-1);
		}
	}
}
