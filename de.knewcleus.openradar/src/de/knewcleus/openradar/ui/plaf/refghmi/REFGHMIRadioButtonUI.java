package de.knewcleus.openradar.ui.plaf.refghmi;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonUI;

public class REFGHMIRadioButtonUI extends BasicRadioButtonUI {
	protected final static REFGHMIRadioButtonUI refghmiRadioButtonUI=new REFGHMIRadioButtonUI();
	
    public static ComponentUI createUI(JComponent c){
    	return refghmiRadioButtonUI;
    }
}
