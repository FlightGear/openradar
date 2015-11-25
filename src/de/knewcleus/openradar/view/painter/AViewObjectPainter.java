/**
 * Copyright (C) 2012,2013,2015 Wolfram Wagner
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

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.fgfs.navdata.impl.Aerodrome;
import de.knewcleus.fgfs.navdata.impl.DME;
import de.knewcleus.fgfs.navdata.impl.Glideslope;
import de.knewcleus.fgfs.navdata.impl.Intersection;
import de.knewcleus.fgfs.navdata.impl.Localizer;
import de.knewcleus.fgfs.navdata.impl.MarkerBeacon;
import de.knewcleus.fgfs.navdata.impl.NDB;
import de.knewcleus.fgfs.navdata.impl.RunwayEnd;
import de.knewcleus.fgfs.navdata.impl.VOR;
import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.fgfs.navdata.xplane.Helipad;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.setup.AdditionalFix;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.groundnet.TaxiSign;
import de.knewcleus.openradar.view.groundnet.TaxiWaySegment;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.objects.AViewObject;

/**
 * This class has got the role to compose the view objects out of the data model
 * class and provide their paint operations. The views use subclasses of this
 * class when they construct, what needs to be painted and redirect the painting
 * itself too.
 *
 * @author Wolfram Wagner
 *
 */

public abstract class AViewObjectPainter<T> {

    protected IMapViewerAdapter mapViewAdapter;
    protected T dataObject;
    private boolean pickable = false;


    protected volatile Rectangle2D displayExtents = new Rectangle2D.Double(0,0,0,0);
    protected List<AViewObject> viewObjectList = new ArrayList<AViewObject>();

    public static AViewObjectPainter<?> getPainterForNavpoint(IMapViewerAdapter mapViewAdapter, GuiMasterController master, Object navPoint) {
        AViewObjectPainter<?> viewObjectPainter = null;

        AirportData data = master.getAirportData();
        
        // the individual paints are redirected to Painters
        if(navPoint instanceof Aerodrome) {
            viewObjectPainter = new AirportPainter(data, mapViewAdapter, (Aerodrome) navPoint);
            data.getNavaidDB().registerNavaid((Aerodrome) navPoint);
        }
        else if(navPoint instanceof RunwayEnd) viewObjectPainter = new RunwayEndPainter(mapViewAdapter, data, (RunwayEnd) navPoint);
        else if(navPoint instanceof Helipad) viewObjectPainter = new HelipadPainter(mapViewAdapter, (Helipad) navPoint);
        else if(navPoint instanceof NDB) {
            viewObjectPainter = new NDBPainter(master, mapViewAdapter, (NDB) navPoint);
            data.getNavaidDB().registerNavaid((NDB) navPoint);
        }
        else if(navPoint instanceof VOR) {
            viewObjectPainter = new VORPainter(master, mapViewAdapter, (VOR) navPoint);
            data.getNavaidDB().registerNavaid((VOR) navPoint);
        }
        else if(navPoint instanceof Localizer) viewObjectPainter = new LocalizerPainter(mapViewAdapter, (Localizer) navPoint);
        else if(navPoint instanceof Glideslope) viewObjectPainter = new DummyPainter(mapViewAdapter,(INavPoint)navPoint); // painted by runway end
        else if(navPoint instanceof MarkerBeacon) viewObjectPainter = new MarkerBeaconPainter(mapViewAdapter, data, (MarkerBeacon) navPoint);
        else if(navPoint instanceof DME) viewObjectPainter = new DMEPainter(mapViewAdapter, (DME) navPoint);
        else if(navPoint instanceof Intersection) {
            viewObjectPainter = new IntersectionPainter(master, mapViewAdapter, (Intersection) navPoint);
            data.getNavaidDB().registerNavaid((Intersection) navPoint);
        }
        else if(navPoint instanceof AdditionalFix) {
            viewObjectPainter = new IntersectionPainter(master, mapViewAdapter, (AdditionalFix) navPoint);
            // registration is done when new points are read in, to allow direct usage in the stdroutes...
            // so this is not needed here: data.getNavaidDB().registerNavaid((AdditionalFix) navPoint);
        }

        else if(navPoint instanceof TaxiWaySegment) viewObjectPainter = new TaxiWayPainter(data,mapViewAdapter, (TaxiWaySegment) navPoint);
        else if(navPoint instanceof TaxiSign) viewObjectPainter = new TaxiSignPainter(mapViewAdapter, (TaxiSign) navPoint);

        else if(navPoint instanceof AirportData) viewObjectPainter = new AtcObjectsPainter(mapViewAdapter, (AirportData) navPoint);

        else {
            throw new IllegalStateException("Unknown object type to paint "+navPoint.getClass()+" ! Please add a painter!");
        }
        return viewObjectPainter;
    }

    public AViewObjectPainter(IMapViewerAdapter mapViewAdapter, T dataObject) {
        this.mapViewAdapter=mapViewAdapter;
        this.dataObject=dataObject;
    }

    public synchronized void updateDisplayPosition(Point2D displayPosition) {
        mapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
        displayExtents = null;
        for(AViewObject vo : viewObjectList) {
            Rectangle2D ode = vo.updateDisplayPosition(displayPosition, mapViewAdapter);
            if(ode!=null) {
                if(displayExtents == null) {
                    displayExtents = ode;
                } else {
                    Rectangle2D.union(ode, displayExtents, displayExtents);
                }
            }
        }
        if(displayExtents==null) {
            displayExtents = new Rectangle2D.Double(0,0,0,0);
        } else {
            mapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
        }
    }

    public synchronized void paint(Graphics2D g2d) {
        for(AViewObject vo : viewObjectList) {
            vo.paint(g2d, mapViewAdapter);
        }
    }

    public synchronized Rectangle2D getDisplayExtents() {
        return displayExtents;
    }

    public synchronized void setPickable(boolean b) {
        this.pickable=b;
    }

    public synchronized boolean isPickable() {
        return pickable;
    }

    public String getTooltipText(Point2D p) {
        for(AViewObject vo : viewObjectList) {
            if(vo.contains(p)) {
                return getTooltipText();
            }
        }
        return null;
    }

    public String getTooltipText() {
        return null;
    }

    public boolean hasToBePainted() {
        return viewObjectList.size()>0;
    }

    public void mouseClicked(MouseEvent e) {  }
}
