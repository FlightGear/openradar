/**
 * Copyright (C) 2013,2015 Wolfram Wagner
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
package de.knewcleus.openradar.view.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.knewcleus.fgfs.geodata.Feature;
import de.knewcleus.fgfs.geodata.FeatureDefinition;
import de.knewcleus.fgfs.geodata.GeodataException;
import de.knewcleus.fgfs.geodata.IGeodataLayer;

public class GeoDataTruncater  implements IGeodataLayer {

    private final IGeodataLayer original;
    private List<Feature> resultList = new ArrayList<Feature>();
    private final Iterator<Feature> resultIterator;

    public GeoDataTruncater(IGeodataLayer original) throws GeodataException {
        this.original=original;
        processTruncation();
        resultIterator = resultList.iterator();
    }
    
    private void processTruncation() throws GeodataException {
//        Feature f;
//        while ((f = original.getNextFeature())!=null) {
//            f.getGeometry().
//        }
    }

    @Override
    public int getRecordCount() {
        return resultList.size();
    }

    @Override
    public FeatureDefinition getFeatureDefinition() {
        return original.getFeatureDefinition();
    }

    @Override
    public Feature getNextFeature() throws GeodataException {
        return resultIterator.next();
    }

    @Override
    public boolean hasNext() {
        return resultIterator.hasNext();
    }
}
