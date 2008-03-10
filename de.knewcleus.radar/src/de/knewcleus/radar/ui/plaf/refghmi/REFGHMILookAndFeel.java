package de.knewcleus.radar.ui.plaf.refghmi;

import javax.swing.UIDefaults;
import javax.swing.plaf.metal.MetalLookAndFeel;

import de.knewcleus.radar.ui.Palette;


public class REFGHMILookAndFeel extends MetalLookAndFeel {
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
	protected void initComponentDefaults(UIDefaults table) {
		super.initComponentDefaults(table);
		ButtonBorder buttonBorder=new ButtonBorder();
		Object[] defaults={
			"Button.border", buttonBorder,
			"Button.foreground", Palette.BLACK,
			"Button.background", Palette.WINDOW_FAWN,
			"Button.select", Palette.WFAWN_DEPRESSED,
			"ToggleButton.border", buttonBorder,
			"ToggleButton.foreground", Palette.BLACK,
			"ToggleButton.background", Palette.WINDOW_BLUE,
			"ToggleButton.select", Palette.WBLUE_DEPRESSED
		};
		table.putDefaults(defaults);
	}
}
