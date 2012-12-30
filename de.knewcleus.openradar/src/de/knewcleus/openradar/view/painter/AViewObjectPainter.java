package de.knewcleus.openradar.view.painter;

import java.awt.Graphics2D;
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
    protected volatile Rectangle2D displayExtents = new Rectangle2D.Double(0,0,0,0);
    protected List<AViewObject> viewObjectList = new ArrayList<AViewObject>();
    
    public static AViewObjectPainter<?> getPainterForNavpoint(IMapViewerAdapter mapViewAdapter, AirportData data, Object navPoint) {
        AViewObjectPainter<?> viewObjectPainter = null;
        
        // the individual paints are redirected to Painters
        if(navPoint instanceof Aerodrome) viewObjectPainter = new AirportPainter(data, mapViewAdapter, (Aerodrome) navPoint);
        else if(navPoint instanceof RunwayEnd) viewObjectPainter = new RunwayEndPainter(mapViewAdapter, data, (RunwayEnd) navPoint);
        else if(navPoint instanceof Helipad) viewObjectPainter = new HelipadPainter(mapViewAdapter, (Helipad) navPoint);
        else if(navPoint instanceof NDB) viewObjectPainter = new NDBPainter(data, mapViewAdapter, (NDB) navPoint);
        else if(navPoint instanceof VOR) viewObjectPainter = new VORPainter(data, mapViewAdapter, (VOR) navPoint);
        else if(navPoint instanceof Localizer) viewObjectPainter = new LocalizerPainter(mapViewAdapter, (Localizer) navPoint);
        else if(navPoint instanceof Glideslope) viewObjectPainter = new DummyPainter(mapViewAdapter,(INavPoint)navPoint); // painted by runway end 
        else if(navPoint instanceof MarkerBeacon) viewObjectPainter = new MarkerBeaconPainter(mapViewAdapter, (MarkerBeacon) navPoint);
        else if(navPoint instanceof DME) viewObjectPainter = new DMEPainter(mapViewAdapter, (DME) navPoint);
        else if(navPoint instanceof Intersection) viewObjectPainter = new IntersectionPainter(data, mapViewAdapter, (Intersection) navPoint);
        
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
        if(displayExtents==null) displayExtents = new Rectangle2D.Double(0,0,0,0);
        mapViewAdapter.getUpdateManager().markRegionDirty(getDisplayExtents());
    }
    
    public synchronized void paint(Graphics2D g2d) {
        for(AViewObject vo : viewObjectList) {
            vo.paint(g2d, mapViewAdapter);
        }
    }
    
    public synchronized Rectangle2D getDisplayExtents() {
        return displayExtents;
    }
}
