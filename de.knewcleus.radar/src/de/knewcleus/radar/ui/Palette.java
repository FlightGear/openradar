package de.knewcleus.radar.ui;

import java.awt.Color;
import java.awt.Font;

public class Palette {
	public final static Font BEACON_FONT=new Font("Geneva",Font.PLAIN,12);
	
	public final static Color BLACK=Color.BLACK;
	public final static Color WHITE=Color.WHITE;
	public final static Color ADV_GROUND=new Color(0.70f,0.50f,0.50f);
	public final static Color ADV_TEXT=new Color(0.92f,0.68f,0.68f);
	public final static Color BEACON=new Color(0.65f,0.65f,0.65f);
	public final static Color CONCERNED=new Color(0.72f,0.61f,0.28f);
	public final static Color CONFLICT=new Color(1.00f,0.10f,0.10f);
	public final static Color WARNING=new Color(1.00f,0.41f,0.12f);
	public final static Color COORD=new Color(1.00f,0.41f,0.60f);
	public final static Color CRD_BACKGROUND=new Color(0.48f,0.45f,0.45f);
	public final static Color CRD_FRAME_HIGHLIGHT=new Color(0.75f,0.75f,0.75f);
	public final static Color DESKTOP=new Color(0.29f,0.32f,0.29f);
	public final static Color LANDMASS=new Color(0.36f,0.34f,0.34f);
	public final static Color WATERMASS=new Color(0.39f,0.39f,0.41f);
	
	/* REFGHMI defines specific colors for all combinations of
	 * (Land,Water)x(NotSector,Sector)x(NotRestricted,Restricted).
	 * 
	 * These can be approximated quite well by drawing opaque land and water and
	 * superimposing transparent colors for sector and restricted areas on that.
	 * 
	 * The error is negligible and will not be visible at all as the REFGHMI colors
	 * are never shown near our colors. In fact, the REFGHMI reference colors will not
	 * be shown at all ;-)
	 */
	public final static Color SECTOR=new Color(0.553f, 0.543f, 0.494f, 0.169f);
	public final static Color RESTRICTED=new Color(0.390f, 0.081f, 0.086f, 0.151f);
	
	/**
	 * REFGHMI also specifies individual colors for risks and conflict overlays in the vertical aid window (VAW).
	 * 
	 * Similarily to the sector and restriction colors we use transparent risk and conflict colors.
	 */
	public final static Color VAW_CONFLICT=new Color(0.730f, 0.217f, 0.233f, 0.280f);
	public final static Color VAW_RISK=new Color(0.565f, 0.523f, 0.297f, 0.550f);
	
	public final static Color WINDOW_BLUE=new Color(0.44f,0.55f, 0.66f);
	public final static Color WINDOW_FAWN=new Color(0.68f,0.57f,0.41f);
	public final static Color WFAWN_RESTRICTION=new Color(0.65f,0.21f,0.20f);
	public final static Color SHADOW=new Color(0.20f,0.20f,0.20f);
	public final static Color GREEN=new Color(0.46f,0.88f,0.31f);
	public final static Color TRANSPARENT=new Color(0.0f,0.0f,0.0f,0.0f);
	
	public static Color getHightlightColor(Color c) {
		final int r,g,b;
		
		r=Math.min(255,c.getRed()+51);
		g=Math.min(255,c.getGreen()+51);
		b=Math.min(255,c.getBlue()+51);
		return new Color(r,g,b);
	}
	
	public static Color getDepressedColor(Color c) {
		final int r,g,b;
		
		r=Math.max(0,c.getRed()-51);
		g=Math.max(0,c.getGreen()-51);
		b=Math.max(0,c.getBlue()-51);
		return new Color(r,g,b);
	}
}
