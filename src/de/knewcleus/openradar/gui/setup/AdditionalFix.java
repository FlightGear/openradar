/**
 * Copyright (C) 2013 Wolfram Wagner
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
package de.knewcleus.openradar.gui.setup;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.model.IIntersection;

public class AdditionalFix implements IIntersection{

    private final String id;
    private final Point2D geoPos;
    private boolean hightlighted = false;

    public AdditionalFix(String id, Point2D point) {
        this.id=id;
        geoPos = point;
    }

    @Override
    public String getIdentification() {
        return id;
    }

    @Override
    public Point2D getGeographicPosition() {
        return geoPos;
    }

    @Override
    public synchronized boolean isHighlighted() {
        return hightlighted;
    }

    @Override
    public void setHighlighted(boolean highlighted) {
         this.hightlighted=highlighted;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AdditionalFix) || obj ==null) return false;
        AdditionalFix otherOne = (AdditionalFix)obj;
        return this.id.equals(otherOne.id);
    }
}
