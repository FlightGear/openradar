package de.knewcleus.openradar.ui.plaf.refghmi;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

import com.sun.java.swing.plaf.motif.MotifSliderUI;

import de.knewcleus.openradar.ui.Palette;


public class REFGHMISliderUI extends MotifSliderUI {
	protected final static String propertyPrefix="Slider"+".";
	
	public REFGHMISliderUI(JSlider slider) {
		super(slider);
	}

	public static ComponentUI createUI(JComponent b)    {
		return new REFGHMISliderUI((JSlider)b);
	}
	
	public String getPropertyPrefix() {
		return propertyPrefix;
	}

	@Override
	public void paintThumb(Graphics g) {
		int x,y,w,h;

		x=thumbRect.x;
		y=thumbRect.y;
		w=thumbRect.width;
		h=thumbRect.height;

		final Color thumbColor=UIManager.getColor(getPropertyPrefix()+"control");
		final Color hightlightColor=Palette.getHightlightColor(thumbColor);
		g.setColor(thumbColor);

		g.fillRect(x,y,w,h);

		REFGHMIUtils.drawEtch(g, hightlightColor, Palette.SHADOW, x, y, w, h, false);
	}
}
