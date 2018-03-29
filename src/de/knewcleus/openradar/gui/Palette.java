/**
 * Copyright (C) 2008-2009 Ralf Gerlich
 * Copyright (C) 2012,2013,2018 Wolfram Wagner
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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.gui;

import java.awt.Color;
import java.awt.Font;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;

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
    public static final Color RADAR_EMERGENCY = new Color(255,100,0);

    public static final Color CHAT_GHOST = Color.GRAY;
    public static final Color CHAT_SELECTED = RADAR_LIGHTBLUE;
    public static final Color CHAT_OWN_TO_SELECTED = RADAR_LIGHTBLUE;
    public static final Color CHAT_OWN = Color.LIGHT_GRAY;
    public static final Color CHAT_AIRPORT_MENTIONED = Color.WHITE;
    public static final Color CHAT_TEXT = Color.WHITE; // Color.LIGHT_GRAY;
	public static final Color CHAT_OUTSIDE_FS_RANGE = Color.GRAY;

	// added

    public static final Color LIGHT_GRAY = Color.LIGHT_GRAY;
    public static final Color GRAY = Color.GRAY;
    public static final Color ORANGE = Color.ORANGE;
    public static final Color BLUE = Color.BLUE;
    public static final Color RED = Color.RED;
    
    public static final Color GLIDESLOPE_ACTIVE = Color.blue;
    public static final Color GLIDESLOPE_INACTIVE = new Color(150,150,255);
    public static final Color RUNWAYEND_OPEN = Color.green;
    public static final Color RUNWAYEND_FORBIDDEN = Color.red;

    public static final Color PAVEMENT_DEFAULT = Color.lightGray;
    public static final Color PAVEMENT_CONCRETE = new Color(110, 110, 110);
    public static final Color PAVEMENT_ASPHALT = Palette.TARMAC;
    public static final Color PAVEMENT_DIRT = new Color(135, 115, 110);
    public static final Color PAVEMENT_DRYLAKEBED = new Color(135, 115, 110);
    public static final Color PAVEMENT_GRAVEL = new Color(115, 115, 110);
    public static final Color PAVEMENT_SNOWICE = new Color(120, 120, 200);
    public static final Color PAVEMENT_TRANSPARENT = new Color(135, 115, 110);
    public static final Color PAVEMENT_TURFGRASS = new Color(40, 70, 40);
    public static final Color PAVEMENT_WATER = new Color(20, 20, 80);

    
//  public final static Color DESKTOP=new Color(0.29f,0.32f,0.29f);
    public final static Color DESKTOP=new Color(70,71,70);//new Color(77,90,77);
 //   public static final Color DESKTOP_TEXT = Color.WHITE;
    public static final Color DESKTOP_TEXT = new Color(219,255,219);
    //public static final Color DESKTOP_FILTER_SELECTED = new Color(190,190,255);
    public static final Color DESKTOP_FILTER_SELECTED = new Color(100,100,255);
    public static final Color WARNING_REARWIND = new Color(255,90,90);
    public static final Color WARNING_GUSTS = Color.ORANGE;//new Color(255,100,0)
	public static final Color URBAN = new Color(80,80,80); 
    public static final Color LAKE = new Color(0.39f,0.39f,0.41f);
    public static final Color STREAM = new Color(0.39f,0.39f,0.41f);
    public static final Color TAXIWAY_LINE = new Color(177,181,64);  
    public static final Color TAXIWAY_SIGN = Color.WHITE; 
    public static final Color PARKING_POSITION = Color.LIGHT_GRAY; 
    public static final Color RUNWAY_ACTIVE = new Color(60,60,80); 
    public static final Color RUNWAY_ENDNUMBER = Color.LIGHT_GRAY; 

    public static final Color ATC_ACTIVE = new Color(0,230,0);
    public static final Color ATC_RANGE = new Color(0,127,0);
    public static final Color NAVAID_HIGHLIGHT = Color.yellow;
    public static final Color VOR_TEXT = Color.LIGHT_GRAY;
    public static final Color NDB_TEXT = Color.LIGHT_GRAY;
    public static final Color FIX_TEXT = Color.LIGHT_GRAY;
    public static final Color FIX_ICON = Color.LIGHT_GRAY;
    public static final Color HELIPAD_TEXT = Color.LIGHT_GRAY;
    public static final Color AIRPORT_TEXT = Color.LIGHT_GRAY;
    public static final Color DISTANCE_CIRCLE_MINOR = Color.GRAY;
    public static final Color DISTANCE_CIRCLE_PLAIN = Color.LIGHT_GRAY;
    public static final Color DISTANCE_CIRCLE_IMPORTANT = Color.WHITE;
    public static final Color RADAR_RANGE_CIRCLE = Color.RED;
    
    // Metar
    public static final Color METAR_LIFR = new Color(218,0,213); 
    public static final Color METAR_IFR = new Color(200,0,0); 
    public static final Color METAR_MVFR = new Color(0,0,200); 
    public static final Color METAR_VFR = new Color(0,150,0); 

    // Wind and Crosswind Info
    public static final Color WIND_5KN_LINE = Color.GRAY; 
    public static final Color WIND_STRONG = Color.RED; 
    public static final Color WIND_MIDDLE = Color.ORANGE; 
    public static final Color WIND_WEAK = Color.GREEN; 
    public static final Color GUSTS_STRONG = Color.MAGENTA; 
    public static final Color GUSTS_MIDDLE = new Color(170, 0, 0); 
    public static final Color GUSTS_WEAK = new Color(0, 64, 0); 
    
    // Chat
    public static final Color CHAT_TIMESTAMP = CHAT_TEXT; 
    public static final Color CHAT_CALLSIGN = CHAT_TEXT; 
    public static final Color CHAT_MESSAGE = CHAT_TEXT; 
    public static final Color CHAT_MESSAGE_LONG = Color.BLUE; 
    public static final Color CHAT_MESSAGE_TOO_LONG = Color.RED; 
    public static final Color CHAT_BUTTON_ALL = Color.BLUE; 
    public static final Color CHAT_BUTTON_SELECTED = Color.WHITE; 
    public static final Color CHAT_BACKGROUND = new Color(30, 35, 30); 
    public static final Color CHAT_FILTER_NONE = Color.BLUE; 
    public static final Color CHAT_FILTER_FREQUENCY = Color.BLUE; 
    public static final Color CHAT_FILTER_RANGE = Color.BLUE; 
    public static final Color CHAT_FILTER_VISIBLE = Color.WHITE; 
    public static final Color CHAT_FILTER_SELECTED_USER = Color.WHITE; 
    
    // Flightplan Dialog
    public static final Color FPD_CLICKABLE = Color.blue; 
    public static final Color FPD_BUTTON_TEXT = Color.black; 
    public static final Color FPD_SQUAWK_OK = Color.black; 
    public static final Color FPD_SQUAWK_ERROR = Color.red; 
    public static final Color FPD_Online = Color.blue; 
    public static final Color FPD_Offline = Color.GRAY; 
    
    // Flight Strip Section
    public static final Color SECTION_BACKGROUND = DESKTOP; 
    public static final Color SECTION_HEADER     = Color.LIGHT_GRAY; 
    public static final Color SECTION_TITLE      = Color.BLACK; 
    
    // Flight Strip
	public final static Font STRIP_FONT = new Font("Geneva",Font.PLAIN,11); 
	public final static Font STRIP_FONT_BOLD = new Font("Geneva",Font.BOLD,11); 
    public static final Color STRIP_BACKGROUND_NEW = new Color(215,255,215); 
    public static final Color STRIP_BACKGROUND_FLIGHT = Color.WHITE; 
    public static final Color STRIP_BACKGROUND_ATC = Color.WHITE; 
    public static final Color STRIP_BACKGROUND_SELECTED = new Color(215,215,255); 
    public static final Color STRIP_BACKGROUND_OFFERED = new Color(255,255,127); 
    public static final Color STRIP_DEFAULT = Color.BLACK;  
    public static final Color STRIP_INACTIVE = Color.GRAY;  
    public static final Color STRIP_SELECTION = Color.BLUE;  
    public static final Color STRIP_EMERGENCY = Color.RED;  
    public static final Color STRIP_CALLSIGN = Color.blue;  
    public static final Color STRIP_AIRCRAFT = Color.blue;  
    public static final Color STRIP_SQUAWK = Color.blue;  
    public static final Color STRIP_EDIT_TEXT = Color.BLACK; 
    public static final Color STRIP_EDIT_TEXT_FOKUSED = Color.BLUE; 
    public static final Color STRIP_EDIT_BACKGROUND = Color.WHITE; 
    public static final Color STRIP_EDIT_BACKGROUND_FOKUSED = Color.YELLOW; 
    public static final int STRIP_COLUMN_SPACE = 40; 
    
    // ORCam
    public static final Color ORCAM_OFFLINE = new Color(200,0,0); 
    public static final Color ORCAM_FOLLOW = Color.blue; 
    public static final Color ORCAM_SETPOS = Color.blue; 
    public static final Color ORCAM_STOP = Color.black; 
    
    public static final Color CONTACT_GROUND     = getDepressedColor(Color.RED);
    public static final Color CONTACT_TRANSITION = getDepressedColor(Color.YELLOW);
    public static final Color CONTACT_FL100      = getDepressedColor(Color.GREEN);
    public static final Color CONTACT_FL245      = getDepressedColor(Color.BLUE);
    public static final Color CONTACT_FL400      = getDepressedColor(Color.MAGENTA);
    
    // other
    
    public static final Color SECTORBEAN_FOREGOUND = new Color(50,50,50); 
    public static final Color SECTORBEAN_BACKGROUND = new Color(110,152,203);
    
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
	
	public static Color getAltitudeColor(GuiRadarContact contact) {
		Double altitude = contact.getAltitude();
		if (altitude <= contact.getAirportData().getElevationFt()) return CONTACT_GROUND;
		if (altitude <= contact.getAirportData().getTransitionAlt()) 
			return calcColor (altitude, contact.getAirportData().getElevationFt(), contact.getAirportData().getTransitionAlt(), CONTACT_GROUND, CONTACT_TRANSITION);
		if (altitude <= 10000) 
			return calcColor (altitude, contact.getAirportData().getTransitionAlt(), 10000, CONTACT_TRANSITION, CONTACT_FL100);
		if (altitude <= 24500) 
			return calcColor (altitude, 10000, 24500, CONTACT_FL100, CONTACT_FL245);
		if (altitude <= 40000) 
			return calcColor (altitude, 24500, 40000, CONTACT_FL245, CONTACT_FL400);
		return CONTACT_FL400;
	}

	protected static Color calcColor(double value, double minValue, double maxValue, Color minColor, Color maxColor) {
		double f = (value - minValue) / (maxValue - minValue);
		int r = minColor.getRed(); 
		int g = minColor.getGreen();
		int b = minColor.getBlue();
		r = (int) Math.round((maxColor.getRed()   - r) * f + r);
		g = (int) Math.round((maxColor.getGreen() - g) * f + g);
		b = (int) Math.round((maxColor.getBlue()  - b) * f + b);
		return new Color(r,g,b);
	}
	
}
