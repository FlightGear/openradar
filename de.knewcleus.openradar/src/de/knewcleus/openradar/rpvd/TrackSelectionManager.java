package de.knewcleus.openradar.rpvd;

public class TrackSelectionManager implements ITrackSelectionManager {
	protected TrackDisplayState selectedTrack = null;
	
	@Override
	public TrackDisplayState getSelectedTrack() {
		return selectedTrack;
	}
	
	@Override
	public void selectTrack(TrackDisplayState track) {
		if (selectedTrack==track) {
			return;
		}
		if (selectedTrack!=null) {
			selectedTrack.setSelected(false);
		}
		selectedTrack = track;
		if (selectedTrack!=null) {
			selectedTrack.setSelected(true);
		}
	}
	
	@Override
	public void deselect() {
		selectTrack(null);
	}
}
