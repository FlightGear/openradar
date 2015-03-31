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
package de.knewcleus.openradar.view.painter;

import java.awt.Color;
import java.awt.Font;

import de.knewcleus.fgfs.navdata.impl.VOR;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.objects.VORFrequency;
import de.knewcleus.openradar.view.objects.VORName;
import de.knewcleus.openradar.view.objects.VORSymbol;
import de.knewcleus.openradar.view.objects.VORSymbol.VORType;

public class VORPainter extends AViewObjectPainter<VOR> {

    private final VOR vor;

    public VORPainter(GuiMasterController master, IMapViewerAdapter mapViewAdapter, VOR vor) {
        super(mapViewAdapter, vor);
        this.vor=vor;
        setPickable(false); // enable tooltips

        Font font = Palette.BEACON_FONT;

        VORSymbol.VORType vorType = VORType.VOR;
        if(vor.getName().contains("DME")) vorType = VORType.VOR_DME;
        else if(vor.getName().contains("TAC")) vorType = VORType.VORTAC;

        VORSymbol s = new VORSymbol(master, vor, vorType);
        viewObjectList.add(s);

        VORName n = new VORName(master, vor, font, Color.lightGray, 0 , Integer.MAX_VALUE);
        viewObjectList.add(n);

        VORFrequency f = new VORFrequency(master, vor, font, Color.lightGray, 0 , Integer.MAX_VALUE);
        viewObjectList.add(f);
    }

    @Override
    public String getTooltipText() {
        return "<html><body>"+vor.getName()+"<br>"+vor.getIdentification()+" "+vor.getFrequency()+" MHz</body></html>";
    }
}
