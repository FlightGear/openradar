package de.knewcleus.radar.ui.plaf.refghmi;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.plaf.ComponentUI;

import com.sun.java.swing.plaf.motif.MotifSliderUI;

import de.knewcleus.radar.ui.Palette;


public class REFGHMISliderUI extends MotifSliderUI {
	public REFGHMISliderUI(JSlider slider) {
		super(slider);
	}

	public static ComponentUI createUI(JComponent b)    {
		return new REFGHMISliderUI((JSlider)b);
	}

	@Override
	public void paintThumb(Graphics g) {
		int x,y,w,h;

		x=thumbRect.x;
		y=thumbRect.y;
		w=thumbRect.width;
		h=thumbRect.height;

		g.setColor(slider.getBackground());

		g.fillRect(x,y,w,h);

		g.setColor(Palette.getHightlightColor(slider.getBackground()));
		g.drawLine(x, y, x+w-1, y);
		g.drawLine(x, y, x, y+h-1);

		g.setColor(Palette.SHADOW);
		g.drawLine(x+w-1, y, x+w-1, y+h-1);
		g.drawLine(x, y+h-1, x+w-1, y+h-1);
	}
}
