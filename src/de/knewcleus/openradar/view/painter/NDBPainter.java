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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.painter;

import java.awt.Font;

import de.knewcleus.fgfs.navdata.impl.NDB;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.objects.NDBFrequency;
import de.knewcleus.openradar.view.objects.NDBName;
import de.knewcleus.openradar.view.objects.NDBSymbol;

public class NDBPainter extends AViewObjectPainter<NDB> {

    private final NDB ndb;

    public NDBPainter(GuiMasterController master, IMapViewerAdapter mapViewAdapter, NDB ndb) {
        super(mapViewAdapter, ndb);
        this.ndb=ndb;
        setPickable(false); // enable tooltips

        Font font = Palette.BEACON_FONT;

        NDBSymbol s = new NDBSymbol(master, ndb, 0 , 200);
        viewObjectList.add(s);

        NDBName n = new NDBName(master, ndb, font, Palette.NDB_TEXT, 0 , 200);
        viewObjectList.add(n);

        NDBFrequency f = new NDBFrequency(master, ndb, font, Palette.NDB_TEXT, 0 , 200);
        viewObjectList.add(f);
    }

    @Override
    public String getTooltipText() {
        return "<html><body>"+ndb.getName()+"<br>"+ndb.getIdentification()+" "+ndb.getFrequency()+" MHz</body></html>";
    }
}
