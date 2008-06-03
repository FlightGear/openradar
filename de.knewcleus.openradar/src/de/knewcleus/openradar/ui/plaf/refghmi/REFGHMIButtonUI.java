package de.knewcleus.openradar.ui.plaf.refghmi;

import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.sun.java.swing.plaf.motif.MotifButtonUI;

import de.knewcleus.openradar.ui.Palette;

public class REFGHMIButtonUI extends MotifButtonUI {
	protected final static REFGHMIButtonUI refghmiButtonUI=new REFGHMIButtonUI();
	
    public static ComponentUI createUI(JComponent c){
    	return refghmiButtonUI;
    }
    
    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
    	fillContentArea(g, b, Palette.getDepressedColor(b.getBackground()));
    }
}
