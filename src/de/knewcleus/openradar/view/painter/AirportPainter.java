/**
 * Copyright (C) 2012,2013 Wolfram Wagner
 *
 * This file is part of OpenRadar.
 *
 * OpenRadar is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OpenRadar. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von OpenRadar.
 *
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie von der Free
 * Software Foundation, Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 *
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne
 * die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public
 * License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 * <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.painter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import de.knewcleus.fgfs.navaids.Pavement;
import de.knewcleus.fgfs.navdata.impl.Aerodrome;
import de.knewcleus.fgfs.navdata.xplane.Runway;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.objects.AViewObject;
import de.knewcleus.openradar.view.objects.AirportCode;
import de.knewcleus.openradar.view.objects.PavementObject;
import de.knewcleus.openradar.view.objects.RunwayObject;

public class AirportPainter extends AViewObjectPainter<Aerodrome> {

    private final Aerodrome aerodrome;
    private AirportCode airportCode;

    public AirportPainter(AirportData data, IMapViewerAdapter mapViewAdapter, Aerodrome aerodrome) {
        super(mapViewAdapter, aerodrome);
        this.aerodrome = aerodrome;
        setPickable(false); // enable tooltips

        Font font = new Font("Arial", Font.PLAIN, 9);

        String code = aerodrome.getIdentification();
        airportCode = new AirportCode(data, aerodrome, font, Color.lightGray, code, 32, Integer.MAX_VALUE);
        viewObjectList.add(airportCode);
        for (Pavement p : aerodrome.getPavements()) {
            viewObjectList.add(new PavementObject(aerodrome, p));
        }
        for (Runway rwy : aerodrome.getRunways()) {
            RunwayObject s = new RunwayObject(data, rwy);
            viewObjectList.add(s);
        }
    }

    @Override
    public String getTooltipText() {
        return "<html><body>" + aerodrome.getIdentification() + " " + aerodrome.getName() + "<br>" + aerodrome.getElevation() + " m</body></html>";
    }

    @Override
    public synchronized void paint(Graphics2D g2d) {
        // paint active runways on top (last)
        for (AViewObject vo : viewObjectList) {
            if (!(vo instanceof RunwayObject)) {
                vo.paint(g2d, mapViewAdapter);
            } else {
                if (!((RunwayObject) vo).isActiveRw()) {
                    vo.paint(g2d, mapViewAdapter);
                }
            }
        }

        for (AViewObject vo : viewObjectList) {
            if ((vo instanceof RunwayObject) && ((RunwayObject) vo).isActiveRw()) {
                vo.paint(g2d, mapViewAdapter);
            }
        }
    }

}
