package de.knewcleus.openradar.ui.plaf.refghmi;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

public class REFGHMICheckBoxUI extends REFGHMIRadioButtonUI {
	protected final static REFGHMICheckBoxUI refghmiCheckBoxUI=new REFGHMICheckBoxUI();
	protected final static String propertyPrefix = "CheckBox"+".";
	
    public static ComponentUI createUI(JComponent c){
    	return refghmiCheckBoxUI;
    }
    
    @Override
    protected String getPropertyPrefix() {
    	return propertyPrefix;
    }
}
