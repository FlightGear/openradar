package de.knewcleus.openradar.ui.plaf.refghmi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;

import com.sun.java.swing.plaf.motif.MotifToggleButtonUI;

import de.knewcleus.openradar.ui.Palette;

public class REFGHMIToggleButtonUI extends MotifToggleButtonUI {
	protected final static REFGHMIToggleButtonUI refghmiToggleButtonUI=new REFGHMIToggleButtonUI();
	
    public static ComponentUI createUI(JComponent c){
    	return refghmiToggleButtonUI;
    }
    
    @Override
    public void installDefaults(AbstractButton b) {
    	super.installDefaults(b);
        LookAndFeel.installProperty(b, "opaque", Boolean.TRUE);
    }
    
    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
    	if (!b.isContentAreaFilled())
    		return;
    	
    	Color oldColor=g.getColor();
    	Dimension size=b.getSize();
    	Insets insets=b.getInsets();
    	Insets margins=b.getMargin();
    	
    	Color fillColor=Palette.getDepressedColor(b.getBackground());
    	g.setColor(fillColor);
    	g.fillRect(insets.left-margins.left, insets.top-margins.top,
    				size.width-(insets.left-margins.left)-(insets.right-margins.right),
    				size.height-(insets.top-margins.top)-(insets.bottom-margins.bottom));
    	
    	g.setColor(oldColor);
    }
}
