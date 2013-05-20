/**
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
package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.impl.Aerodrome;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class AirportCode extends AViewObject {

    private final AirportData data;
    private final Aerodrome aerodrome;
    private final Color defaultColor;
    private final int defaultMaxScale;

    private String activeText;

    public AirportCode(AirportData data, Aerodrome aerodrome, Font font, Color color, String text, int minScaleText, int maxScaleText) {
        super(font, color, text, minScaleText, maxScaleText);
        this.data = data;
        this.aerodrome = aerodrome;
        this.defaultColor=color;
        this.defaultMaxScale = maxScaleText;
        this.activeText = text;
    }

    @Override
    public synchronized void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        if(aerodrome.isHighlighted()) {
            setMaxScaleText(Integer.MAX_VALUE);
            setColor(Palette.NAVAID_HIGHLIGHT);
        } else {
            setMaxScaleText(defaultMaxScale);
            setColor(defaultColor);
        }

        setTextCoordinates(newDisplayPosition);

        if(aerodrome.isHighlighted() || data.getRadarObjectFilterState("APT")) {
            setText(activeText);
        } else {
            setText(null);
        }
    }
}
