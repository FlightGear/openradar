package de.knewcleus.openradar.view.navdata;

import java.util.ArrayList;
import java.util.List;

import de.knewcleus.fgfs.navdata.NavDataStreamException;
import de.knewcleus.fgfs.navdata.model.INavDataStream;
import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.IView;
import de.knewcleus.openradar.view.LayeredView;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class NavPointProvider {
	protected final IMapViewerAdapter mapViewerAdapter;
	protected final LayeredView navPointLayer;
	protected final AirportData data;
	
	private List<INavPointListener> navPointListeners = new ArrayList<INavPointListener>();
	
	public NavPointProvider(IMapViewerAdapter mapViewerAdapter, LayeredView navPointLayer, AirportData data) {
		this.mapViewerAdapter = mapViewerAdapter;
		this.navPointLayer = navPointLayer;
		this.data = data;
	}
	
	public IView provideNavPoint(INavPoint point) {
		return new NavPointView(mapViewerAdapter, data, point);
	}
	
	public void addViews(INavDataStream<? extends INavPoint> stream) throws NavDataStreamException {
		INavPoint point;
		while ((point=stream.readDatum())!=null) {
			navPointLayer.pushView(provideNavPoint(point));

			for(INavPointListener l : navPointListeners) {
	            l.navPointAdded(point);
	        }
		}
	}
	
	public void addNavPointListener(INavPointListener l) {
	    navPointListeners.add(l);
	}
	
    public void removeNavPointListener(INavPointListener l) {
        navPointListeners.remove(l);
    }
}
