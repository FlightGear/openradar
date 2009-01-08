package de.knewcleus.openradar.view.map.test;

import de.knewcleus.fgfs.navdata.NavDataStreamException;
import de.knewcleus.fgfs.navdata.model.INavDataStream;
import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.openradar.view.IView;
import de.knewcleus.openradar.view.LayeredView;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class NavPointProvider {
	protected final IMapViewerAdapter mapViewerAdapter;
	protected final LayeredView navPointLayer;

	public NavPointProvider(IMapViewerAdapter mapViewerAdapter, LayeredView navPointLayer) {
		this.mapViewerAdapter = mapViewerAdapter;
		this.navPointLayer = navPointLayer;
	}
	
	public IView provideNavPoint(INavPoint point) {
		return new NavPointView(mapViewerAdapter, point);
	}
	
	public void addViews(INavDataStream<? extends INavPoint> stream) throws NavDataStreamException {
		INavPoint point;
		while ((point=stream.readDatum())!=null) {
			navPointLayer.pushView(provideNavPoint(point));
		}
	}
	
}
