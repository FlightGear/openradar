package de.knewcleus.radar.ui.rpvd.tracks;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.radar.WorkObject;
import de.knewcleus.radar.WorkObjectSymbol;
import de.knewcleus.radar.autolabel.ILabel;
import de.knewcleus.radar.ui.labels.LabelElementContainer;
import de.knewcleus.radar.ui.labels.LabelLineLayoutManager;
import de.knewcleus.radar.ui.labels.MultiLineLabelLayoutManager;
import de.knewcleus.radar.ui.vehicles.CallsignLabelElement;
import de.knewcleus.radar.ui.vehicles.CurrentLevelLabelElement;
import de.knewcleus.radar.ui.vehicles.GroundSpeedLabelElement;
import de.knewcleus.radar.vessels.Track;

public class TrackLabelSymbol extends WorkObjectSymbol implements ILabel {
	protected final TrackSymbol labeledSymbol;
	protected final LabelElementContainer labelContainer=new LabelElementContainer();
	protected final CallsignLabelElement callsignLabelElement;
	protected final LabelElementContainer secondLabelLine;
	protected final GroundSpeedLabelElement groundSpeedLabelElement;
	protected final CurrentLevelLabelElement currentLevelLabelElement;
	
	protected boolean isAutolabelled=false;

	public TrackLabelSymbol(TrackSymbol labeledSymbol) {
		this.labeledSymbol=labeledSymbol;
		
		labelContainer.setLayoutManager(new MultiLineLabelLayoutManager());
		
		callsignLabelElement=new CallsignLabelElement(getAssociatedTrack());
		secondLabelLine=new LabelElementContainer();
		groundSpeedLabelElement=new GroundSpeedLabelElement(getAssociatedTrack());
		currentLevelLabelElement=new CurrentLevelLabelElement(getAssociatedTrack());
		secondLabelLine.setLayoutManager(new LabelLineLayoutManager());
		secondLabelLine.add(groundSpeedLabelElement);
		secondLabelLine.add(currentLevelLabelElement);
		
		// TODO: let the associated vessel determine the layout, if any
		populateDefaultLabel();
	}
	
	public Track getAssociatedTrack() {
		return getLabeledObject().getAssociatedTrack();
	}

	@Override
	public WorkObject getAssociatedObject() {
		return getAssociatedTrack().getAssociatedVessel();
	}

	@Override
	public TrackSymbol getLabeledObject() {
		return labeledSymbol;
	}

	@Override
	public TrackSymbolContainer getParent() {
		return (TrackSymbolContainer) super.getParent();
	}

	@Override
	public Rectangle2D getBounds() {
		return labelContainer.getBounds2D();
	}

	protected void populateDefaultLabel() {
		labelContainer.removeAll();
		labelContainer.add(callsignLabelElement);
		labelContainer.add(secondLabelLine);
	}
	
	@Override
	public void paint(Graphics2D g) {
		labelContainer.paint(g);
	}
	
	@Override
	public void validate() {
		invalidate();
		labelContainer.setDisplayComponent(getDisplayComponent());
		labelContainer.pack();

		final Rectangle2D trackBounds=getLabeledObject().getBounds();
		final Rectangle2D labelBounds=labelContainer.getBounds2D();
		
		final double dirX=1.0/Math.sqrt(2), dirY=-1.0/Math.sqrt(2);
		final double len=30;
		
		final double parentCX, parentCY;
		parentCX=trackBounds.getCenterX();
		parentCY=trackBounds.getCenterY();
		
		final double trackDx, trackDy;
		
		if (Math.abs(dirX)*trackBounds.getHeight()>Math.abs(dirY)*trackBounds.getWidth()) {
			trackDx=trackBounds.getWidth()*Math.signum(dirX)/2.0;
			trackDy=trackDx*dirY/dirX;
		} else {
			trackDy=trackBounds.getHeight()*Math.signum(dirY)/2.0;
			trackDx=trackDy*dirX/dirY;
		}
		
		final double labelDx, labelDy;

		if (Math.abs(dirX)*labelBounds.getHeight()>Math.abs(dirY)*labelBounds.getWidth()) {
			labelDx=labelBounds.getWidth()*Math.signum(dirX)/2.0;
			labelDy=labelDx*dirY/dirX;
		} else {
			labelDy=labelBounds.getHeight()*Math.signum(dirY)/2.0;
			labelDx=labelDy*dirX/dirY;
		}
		
		final double labelCX, labelCY;
		labelCX=parentCX+trackDx+dirX*len+labelDx;
		labelCY=parentCY+trackDy+dirY*len+labelDy;
		
		labelContainer.setPosition(labelCX-labelBounds.getWidth()/2.0, labelCY-labelBounds.getHeight()/2.0);
		validateDependents();
		invalidate();
	}

	@Override
	public boolean isHit(Point2D position) {
		return getBounds2D().contains(position);
	}

	@Override
	public boolean isAutolabelled() {
		return isAutolabelled && !isActive();
	}

	@Override
	public void setCentroidPosition(double x, double y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Rectangle2D getBounds2D() {
		return getBounds();
	}

	@Override
	public double getPriority() {
		return 10;
	}

}
