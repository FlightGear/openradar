package de.knewcleus.radar.ui.rpvd;

import java.awt.Color;

public enum AircraftTaskState {
	OTHER(Palette.BEACON,Palette.BEACON,Palette.BLACK,Palette.CRD_BACKGROUND),
	NOT_CONCERNED(Palette.BEACON,Palette.BEACON,Palette.BLACK,Palette.CRD_BACKGROUND),
	ADVANCED_INFORMATION(Palette.BLACK,Palette.ADV_TEXT,Palette.BLACK,Palette.ADV_TEXT),
	ASSUMED(Palette.BLACK,Palette.WHITE,Palette.BLACK,Palette.WHITE),
	CONCERNED(Palette.BLACK,Palette.CONCERNED,Palette.BLACK,Palette.CONCERNED);
	
	private final Color symbolColor;
	private final Color normalTextColor;
	private final Color selectedTextColor;
	private final Color selectedBackgroundColor;
	
	private AircraftTaskState(Color symbolColor, Color normalTextColor, Color selectedTextColor, Color selectedBackgroundColor)
	{
		this.symbolColor=symbolColor;
		this.normalTextColor=normalTextColor;
		this.selectedTextColor=selectedTextColor;
		this.selectedBackgroundColor=selectedBackgroundColor;
	}
	
	public Color getSymbolColor() {
		return symbolColor;
	}
	
	public Color getNormalTextColor() {
		return normalTextColor;
	}
	
	public Color getSelectedTextColor() {
		return selectedTextColor;
	}
	
	public Color getSelectedBackgroundColor() {
		return selectedBackgroundColor;
	}
}
