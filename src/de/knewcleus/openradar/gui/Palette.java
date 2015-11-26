/**
 * Copyright (C) 2008-2009 Ralf Gerlich
 * Copyright (C) 2012,2013 Wolfram Wagner
 *
 * This file is part of OpenRadar.
 *
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von OpenRadar.
 *
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 *
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.gui;

import java.awt.Color;
import java.awt.Font;

/**
 * Color and Font definitions based an REFGHMI of Eurocontrol
 *
 * @author Ralf Gerlich
 */
public class Palette {
	public final static Font BEACON_FONT=new Font("Geneva",Font.PLAIN,9);
    public final static Font DESKTOP_FONT=new Font("Geneva",Font.PLAIN,10);

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
	public final static Color LANDMASS=new Color(0.36f,0.34f,0.34f);
	public final static Color WATERMASS=new Color(0.39f,0.39f,0.41f);

	public final static Color TARMAC=new Color(0.30f, 0.28f, 0.28f);
	public final static Color RUNWAY = new Color(0.21f, 0.196f, 0.196f);

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
    public final static Color YELLOW=new Color(255,255,0);
    public final static Color PINK=new Color(255,70,255);
    public final static Color LIGHTBLUE=new Color(80,255,255);


    public final static Color RADAR_LIGHTBLUE=new Color(80,255,255);
    public static final Color RADAR_ORANGE = new Color(240,230,0); // new Color(255,240,0);
    public final static Color RADAR_WHITEBLUE=new Color(205,255,255);
    public final static Color RADAR_GREEN=new Color(90,255,90);
	public final static Color TRANSPARENT=new Color(0.0f,0.0f,0.0f,0.0f);

    public static final Color RADAR_SELECTED = new Color(0,255,255); //RADAR_WHITEBLUE;
    public static final Color RADAR_CONTROLLED = new Color(100,200,255); //RADAR_LIGHTBLUE;
    public static final Color RADAR_IMPORTANT = RADAR_ORANGE;
    public static final Color RADAR_UNCONTROLLED = RADAR_GREEN;
    public static final Color RADAR_GHOST = Color.GRAY;

    public static final Color CHAT_GHOST = Color.GRAY;
    public static final Color CHAT_SELECTED = RADAR_LIGHTBLUE;
    public static final Color CHAT_OWN_TO_SELECTED = RADAR_LIGHTBLUE;
    public static final Color CHAT_OWN = Color.LIGHT_GRAY;
    public static final Color CHAT_AIRPORT_MENTIONED = Color.WHITE;
    public static final Color CHAT_TEXT = Color.LIGHT_GRAY;

	// added

    public static final Color GLIDESLOPE = Color.blue;
    public static final Color RUNWAYEND_OPEN = Color.green;
    public static final Color RUNWAYEND_FORBIDDEN = Color.red;

//  public final static Color DESKTOP=new Color(0.29f,0.32f,0.29f);
    public final static Color DESKTOP=new Color(70,71,70);//new Color(77,90,77);
 //   public static final Color DESKTOP_TEXT = Color.WHITE;
    public static final Color DESKTOP_TEXT = new Color(219,255,219);
    public static final Color DESKTOP_FILTER_SELECTED = new Color(190,190,255);
    public static final Color WARNING_REARWIND = new Color(255,90,90);
    public static final Color WARNING_GUSTS = Color.ORANGE;//new Color(255,100,0)
    public static final Color LAKE = new Color(0.39f,0.39f,0.41f);
    public static final Color STREAM = new Color(0.39f,0.39f,0.41f);

    public static final Color NAVAID_HIGHLIGHT = Color.yellow;

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
