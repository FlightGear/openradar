/**
 * Copyright (C) 2015 Tim Wootton
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
package de.knewcleus.openradar.view.stdroutes;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

/**
 * restriction .position Navaid code or geo coordinates ("lat,long" or
 * point,angle,range) .maxspeed (optional) Maximum speed in kts to be displayed.
 * .notabove (optional) Maximum permitted altitude text (typically should be
 * FLxxx or xxxft) .notbelow (optional) Minimum permitted altitude text .font
 * (optional) default if unspecified is Ariel .fontSize (optional) default if
 * unspecified is 10 .stroke (optional) default is normal line alternatives:
 * "dashed","dots", or a numeric spec
 *
 *
 * @author Tim Wootton
 *
 */
public class StdRouteRestriction extends AStdRouteElement {

	// private final Float fontSize;
	private final String maxspeed;
	private final String notabove;
	private final String notbelow;
	private volatile Rectangle2D bounds;
	private static Logger log = LogManager.getLogger(StdRouteRestriction.class);

	public StdRouteRestriction(AirportData data, StdRoute route, IMapViewerAdapter mapViewAdapter,
			AStdRouteElement previous, String position, String maxspeed, String notabove, String notbelow,
			StdRouteAttributes attributes) {
		super(data, mapViewAdapter, route.getPoint(position, previous), null, attributes);

		// this.fontSize = (fontSize != null) ? Float.parseFloat(fontSize) : 10;

		this.maxspeed = (maxspeed != null) ? maxspeed.concat("kt") : null;
		this.notabove = notabove;
		this.notbelow = notbelow;
	}

	@Override
	public synchronized Rectangle2D paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
		String slot2Text = "";
		String slot4Text = "";
		boolean slot1Line = false;
		boolean slot3Line = false;
		boolean slot5Line = false;
		boolean boxSides = false;

		/*
		 * The table below shows what appears in each of the 5 graphic component
		 * areas (slots) of the restriction object depending on which of the 3
		 * inputs, maxspeed, notabove and notbelow are provided, and also
		 * dependent on how their values relate to each other. There are 11
		 * supported combinations however the 1st and last are not particularly
		 * useful, so in practice there are 9.
		 * 
		 * 
		 * If nothing defined 1 2 3 4 5
		 * 
		 * If only maxspeed defined 1 |---------| 2 | MAX | 3 | | 4 | 240kt | 5
		 * |---------|
		 * 
		 * If only notabove defined 1 2 3 --------- 4 3000ft 5
		 * 
		 * If only notbelow defined 1 2 FL120 3 --------- 4 5
		 * 
		 * If maxspeed and notabove defined 1 --------- 2 3000ft 3 4 240kt 5
		 * 
		 * If maxspeed and notbelow defined 1 2 FL120 3 --------- 4 240kt 5
		 * 
		 * If notabove and notbelow defined with notabove < notbelow restricted
		 * between 3000ft and FL120 ok below 3000ft and above FL120 1 2 FL120 3
		 * --------- 4 3000ft 5
		 * 
		 * 
		 * If notabove and notbelow defined with notabove > notbelow ok between
		 * 3000ft and FL120 restricted below 3000 and above FL120 1 --------- 2
		 * FL120 3 4 3000ft 5 ---------
		 * 
		 * If notabove and notbelow defined with notabove == notbelow ok only at
		 * exact altitude restricted above and below 1 --------- 2 FL120 3
		 * --------- 4 5
		 * 
		 * If maxspeed and notabove and notbelow defined with notabove ==
		 * notbelow ok only at exact altitude restricted above and below 1
		 * --------- 2 FL120 3 --------- 4 240kt 5
		 * 
		 * If maxspeed and notabove and notbelow defined with notabove <>
		 * notbelow Invalid combination 1 2 ERR ALL3&DIFF 3 4 IN RESTRICTION 5
		 */

		if (notabove != null) {
			if (maxspeed != null) {
				slot1Line = true;
				slot2Text = notabove;
			} else {
				slot4Text = notabove;
				slot3Line = true;
			}
		}

		if (notbelow != null) {
			slot2Text = notbelow;
			slot3Line = true;
			if (notabove != null) {
				if (altInFeet(notabove) > altInFeet(notbelow)) {
					slot1Line = true;
					slot2Text = notabove;
					slot3Line = false;
					slot4Text = notbelow;
					slot5Line = true;
				} else if (altInFeet(notabove) == altInFeet(notbelow)) {
					slot1Line = true;
					slot4Text = "";
				}
			}

		}

		if (maxspeed != null) {
			slot4Text = maxspeed;
			if ((notabove != null) && (notbelow != null) && (altInFeet(notabove) != altInFeet(notbelow))) {
				slot1Line = false;
				slot3Line = false;
				slot5Line = false;
				slot2Text = "ERR ALL3&DIFF";
				slot4Text = "IN RESTRICTION";
			} else if ((notabove == null) && (notbelow == null)) {
				slot2Text = "MAX";
				slot1Line = true;
				slot5Line = true;
				boxSides = true;
			}
		}

		// if(color!=null) {
		// g2d.setColor(color);
		// }
		// Font origFont = g2d.getFont();
		// if(font != null) {
		// g2d.setFont(new Font(font,Font.PLAIN,Math.round(fontSize)));
		// }
		//
		// Stroke origStroke = g2d.getStroke();
		// if(stroke!=null) {
		// g2d.setStroke(stroke);
		// }

		BasicStroke bs = (BasicStroke) g2d.getStroke();
		Float linewidth = bs.getLineWidth();

		FontMetrics fm = g2d.getFontMetrics();
		Rectangle2D slot2Bounds = fm.getStringBounds(slot2Text, g2d);
		Rectangle2D slot4Bounds = fm.getStringBounds(slot4Text, g2d);

		Point2D displayPoint = getDisplayPoint(geoReferencePoint);
		int midlineY = (int) (displayPoint.getY()); // y co-ord of the middle
													// line of the object
													// every graphical component
													// of a restriction
													// will have it's y co-rd
													// relative to this
		int margin = 0;
		int slot1Y = (int) (midlineY - (slot2Bounds.getHeight() + fm.getDescent() + (2 * margin) + (linewidth)));
		int slot5Y = (int) (midlineY + (slot4Bounds.getHeight() + fm.getDescent() + (2 * margin) + (linewidth)));
		int widestX = (int) (slot2Bounds.getWidth() > slot4Bounds.getWidth() ? slot2Bounds.getWidth()
				: slot4Bounds.getWidth());
		int lineStartX = (int) (displayPoint.getX() - ((widestX / 2) + margin + linewidth));
		int lineEndX = (int) (displayPoint.getX() + ((widestX / 2) + margin + linewidth));
		int textHeight = (int) (slot2Bounds.getHeight() + slot4Bounds.getHeight());
		bounds = new Rectangle2D.Double(displayPoint.getX(), displayPoint.getY(), widestX + (2 * margin),
				textHeight + (4 * margin) + (4 * linewidth));

		if (slot1Line) // upper line
			g2d.drawLine(lineStartX, slot1Y, lineEndX, slot1Y);

		// upper text
		g2d.drawString(slot2Text, (int) (displayPoint.getX() - slot2Bounds.getCenterX()),
				midlineY - (fm.getDescent() + margin + (linewidth / 2)));

		if (slot3Line) // middle line
			g2d.drawLine(lineStartX, midlineY, lineEndX, midlineY);

		// lower text
		g2d.drawString(slot4Text, (int) (displayPoint.getX() - slot4Bounds.getCenterX()),
				(int) (midlineY + slot4Bounds.getHeight() + margin + (linewidth / 2)));

		if (slot5Line) // lower line
			g2d.drawLine(lineStartX, slot5Y, lineEndX, slot5Y);

		if (boxSides) { // draw box sides
			g2d.drawLine(lineStartX, slot1Y, lineStartX, slot5Y);
			g2d.drawLine(lineEndX, slot1Y, lineEndX, slot5Y);
		}

		// g2d.setFont(origFont);
		// g2d.setStroke(origStroke);

		return bounds;
	}

	@Override
	public Point2D getEndPoint() {
		return geoReferencePoint;
	}

	@Override
	public synchronized boolean contains(Point e) {
		if (bounds == null)
			return false;
		return bounds.contains(e);
	}

	private int altInFeet(String alt) {
		if (alt != null) {
			alt = alt.replace("AMSL", ""); // Remove some common clutter to get
											// a numeric
			alt = alt.replace(",", ""); // that we can parse
			alt = alt.replace("SFC", "0"); // Assuming the surface and
			alt = alt.replace("GND", "0"); // the ground are 0ft is wrong,
											// but in the unlikely event that
											// your ground
											// is below 0ft _and_ you're trying
											// to mark a
											// restriction there, the workaround
											// is to
											// specify the actual height in -ve
											// feet
			try {
				return Integer.parseInt(alt); // plain integer e.g. 3000
			} catch (NumberFormatException e) {
				try {
					return Integer.parseInt(alt.replace("ft", "")); // ft
																	// specified
																	// e.g.
																	// 3000ft
				} catch (NumberFormatException e1) {
					try {
						return Integer.parseInt(alt.replace("FL", "")) * 100;// Flight
																				// Level
																				// specified
																				// e.g.
																				// FL120
					} catch (NumberFormatException e2) {
						log.error("unable to determine altitude from:" + alt);
						return 0;
					}

				}
			}
		} else
			return 0;
	}
}
