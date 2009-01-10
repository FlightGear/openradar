package de.knewcleus.openradar.rpvd;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import de.knewcleus.fgfs.util.IOutputIterator;
import de.knewcleus.openradar.view.IPickable;
import de.knewcleus.openradar.view.IView;
import de.knewcleus.openradar.view.PickVisitor;

public class MouseSelectionListener implements MouseMotionListener {
	protected final IRadarMapViewerAdapter radarMapViewerAdapter;
	protected final IView rootView;
	
	public MouseSelectionListener(IRadarMapViewerAdapter radarMapViewerAdapter, IView rootView) {
		this.radarMapViewerAdapter = radarMapViewerAdapter;
		this.rootView = rootView;
	}

	@Override
	public void mouseDragged(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {
		final TrackDisplayIterator iterator = new TrackDisplayIterator();
		final PickVisitor pickVisitor = new PickVisitor(e.getPoint(), iterator);
		rootView.accept(pickVisitor);
		final ITrackSelectionManager selectionManager = radarMapViewerAdapter.getTrackSelectionManager();
		if (iterator.getTopTrackDisplay()==null) {
			selectionManager.deselect();
		} else {
			selectionManager.selectTrack(iterator.getTopTrackDisplay().getTrackDisplayState());
		}
	}
	
	protected class TrackDisplayIterator implements IOutputIterator<IPickable> {
		protected ITrackDisplay topTrackDisplay = null;
		
		@Override
		public void next(IPickable v) {
			if (v instanceof ITrackDisplay) {
				topTrackDisplay = (ITrackDisplay)v;
			}
		}
		
		@Override
		public boolean wantsNext() {
			return true;
		}
		
		public ITrackDisplay getTopTrackDisplay() {
			return topTrackDisplay;
		}
	}

}
