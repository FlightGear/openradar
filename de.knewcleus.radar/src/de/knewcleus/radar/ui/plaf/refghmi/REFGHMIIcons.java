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
		private	Color controlColor = UIManager.getColor(getPropertyPrefix()+"control");
		private Color highlightColor = Palette.getHightlightColor(controlColor);
		private Color depressedColor = Palette.getDepressedColor(controlColor);
		private Color shadowColor = Palette.SHADOW;
		
		protected static final int csize=13;
		
		protected static final String propertyPrefix="CheckBox"+".";
		
		public String getPropertyPrefix() {
			return propertyPrefix;
		}

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

			boolean depressed=(model.isArmed() && model.isPressed()) || model.isSelected();

			int w=getIconWidth();
			int h=getIconHeight();
			
			g.setColor(depressed?depressedColor:controlColor);
			g.fillRect(x,y,w,h);
			
			REFGHMIUtils.drawEtch(g, highlightColor, shadowColor, x, y, w, h, depressed);
		}
	}
	
	public static class RadioButtonIcon extends CheckBoxIcon {
		protected final static String propertPrefix="RadioButton"+".";
		@Override
		public String getPropertyPrefix() {
			return propertPrefix;
		}
	}
}
