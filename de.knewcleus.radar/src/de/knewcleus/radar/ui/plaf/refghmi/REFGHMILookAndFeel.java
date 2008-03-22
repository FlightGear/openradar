package de.knewcleus.radar.ui.plaf.refghmi;

import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;

import com.sun.java.swing.plaf.motif.MotifLookAndFeel;

import de.knewcleus.radar.ui.Palette;

public class REFGHMILookAndFeel extends MotifLookAndFeel {
	private static final long serialVersionUID = 5013355281933396612L;

	@Override
	public String getDescription() {
		return "Eurocontrol Reference Ground Human Machine Interface";
	}

	@Override
	public String getID() {
		return "REFGHMI";
	}

	@Override
	public String getName() {
		return "REFGHMI";
	}

	@Override
	public boolean isNativeLookAndFeel() {
		return false;
	}

	@Override
	public boolean isSupportedLookAndFeel() {
		return true;
	}
	
	@Override
	protected void initSystemColorDefaults(UIDefaults table) {
		super.initSystemColorDefaults(table);
		
		/* Override some defaults */
		Object[] defaults=new Object[] {
                "activeCaption", new ColorUIResource(Palette.WINDOW_BLUE),
    	    "activeCaptionText", new ColorUIResource(Palette.BLACK),
		  "activeCaptionBorder", new ColorUIResource(Palette.WINDOW_BLUE),
              "inactiveCaption", new ColorUIResource(Palette.WINDOW_BLUE),
          "inactiveCaptionText", new ColorUIResource(Palette.BLACK),
        "inactiveCaptionBorder", new ColorUIResource(Palette.WINDOW_BLUE),
                       "window", new ColorUIResource(Palette.WINDOW_BLUE),
                      "control", new ColorUIResource(Palette.WINDOW_BLUE),
                  "controlText", new ColorUIResource(Palette.BLACK)
		};
		
		table.putDefaults(defaults);
	}
	
	@Override
	protected void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);
		String basePackage="de.knewcleus.radar.ui.plaf.refghmi.";
		Object[] defaults={
				      "SliderUI", basePackage+"REFGHMISliderUI",
				      "ButtonUI", basePackage+"REFGHMIButtonUI",
				"ToggleButtonUI", basePackage+"REFGHMIToggleButtonUI",
				 "RadioButtonUI", basePackage+"REFGHMIRadioButtonUI",
				    "CheckBoxUI", basePackage+"REFGHMICheckBoxUI"
		};
		table.putDefaults(defaults);
	}
	
	@Override
	protected void initComponentDefaults(UIDefaults table) {
		super.initComponentDefaults(table);
		Border buttonBorder=new REFGHMIBorders.ButtonBorder();
		Border sliderBorder=new REFGHMIBorders.SliderBorder();
        Object checkBoxIcon = new UIDefaults.LazyValue() {
            public Object createValue(UIDefaults table) {
                return new REFGHMIIcons.CheckBoxIcon();
            }
        };
		Object[] defaults={
			"Button.border", buttonBorder,
			"Slider.border", sliderBorder,
			"ToggleButton.border", buttonBorder,
			"ToggleButton.foreground", Palette.BLACK,
			"ToggleButton.background", Palette.WINDOW_BLUE,
			"CheckBox.icon", checkBoxIcon,
			"RadioButton.icon", checkBoxIcon
		};
		table.putDefaults(defaults);
	}
}
