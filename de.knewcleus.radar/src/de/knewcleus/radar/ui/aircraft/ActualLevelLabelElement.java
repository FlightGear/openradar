package de.knewcleus.radar.ui.aircraft;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodToCartTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.radar.ui.labels.AbstractTextLabelElement;
import de.knewcleus.radar.ui.labels.ILabelDisplay;

public class ActualLevelLabelElement extends AbstractTextLabelElement {
	private static final GeodToCartTransformation geodToCartTransformation=new GeodToCartTransformation(Ellipsoid.WGS84);

	public ActualLevelLabelElement(ILabelDisplay labelDisplay, AircraftState aircraftState) {
		super(labelDisplay, aircraftState);
	}

	@Override
	protected String getText() {
		final Position currentPosition=aircraftState.getPositionBuffer().getLast();
		final Position currentGeodPosition=geodToCartTransformation.backward(currentPosition);
		return String.format("%03d",(int)Math.ceil(currentGeodPosition.getZ()/Units.FT/100.0));
	}

}
