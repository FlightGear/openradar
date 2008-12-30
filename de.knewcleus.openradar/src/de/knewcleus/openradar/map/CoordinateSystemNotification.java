package de.knewcleus.openradar.map;

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotifier;

/**
 * A coordinate system notification is sent whenever the coordinate system of a map changes,
 * so that views can update their extents.
 * 
 * @author Ralf Gerlich
 *
 */
public class CoordinateSystemNotification implements INotification {
	protected final IMapViewAdapter source;
	protected final boolean transformationChanged;
	protected final boolean projectionChanged;

	public CoordinateSystemNotification(IMapViewAdapter source, boolean transformationChanged, boolean projectionChanged) {
		this.source = source;
		this.transformationChanged = transformationChanged;
		this.projectionChanged = projectionChanged;
	}

	@Override
	public INotifier getSource() {
		return source;
	}
	
	public boolean isTransformationChanged() {
		return transformationChanged;
	}
	
	public boolean isProjectionChanged() {
		return projectionChanged;
	}
}
