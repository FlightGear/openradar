package de.knewcleus.openradar.ui.rpvd.tracks;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.autolabel.ILabel;
import de.knewcleus.openradar.ui.core.WorkObject;
import de.knewcleus.openradar.ui.core.WorkObjectSymbol;
import de.knewcleus.openradar.ui.labels.LabelElementContainer;
import de.knewcleus.openradar.ui.labels.LabelLineLayoutManager;
import de.knewcleus.openradar.ui.labels.MultiLineLabelLayoutManager;
import de.knewcleus.openradar.ui.vehicles.CallsignLabelElement;
import de.knewcleus.openradar.ui.vehicles.CurrentLevelLabelElement;
import de.knewcleus.openradar.ui.vehicles.GroundSpeedLabelElement;
import de.knewcleus.openradar.vessels.Track;

public class TrackLabelSymbol extends WorkObjectSymbol implements ILabel {
	protected final TrackSymbol labeledSymbol;
	protected final LabelElementContainer labelContainer=new LabelElementContainer();
	protected final CallsignLabelElement callsignLabelElement;
	protected final LabelElementContainer secondLabelLine;
	protected final GroundSpeedLabelElement groundSpeedLabelElement;
	protected final CurrentLevelLabelElement currentLevelLabelElement;
	protected double grabDeltaX, grabDeltaY;
	protected double labelDeltaX=30.0/Math.sqrt(2), labelDeltaY=-30.0/Math.sqrt(2);
	
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
		return getAssociatedTrack().getCorrelatedVessel();
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
	public void paintElement(Graphics2D g) {
		labelContainer.paint(g);
	}
	
	@Override
	public void validate() {
		invalidate();
		labelContainer.setDisplayComponent(getDisplayComponent());
		labelContainer.pack();

		final Rectangle2D trackBounds=getLabeledObject().getBounds();
		
		final double trackCX, trackCY;
		trackCX=trackBounds.getCenterX();
		trackCY=trackBounds.getCenterY();
		
		final double labelCX, labelCY;
		labelCX=trackCX+labelDeltaX;
		labelCY=trackCY+labelDeltaY;
		
		final Rectangle2D labelBounds=labelContainer.getBounds2D();
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
		invalidate();
		final Rectangle2D trackBounds=getLabeledObject().getBounds();
		
		final double trackCX, trackCY;
		trackCX=trackBounds.getCenterX();
		trackCY=trackBounds.getCenterY();
		
		labelDeltaX=x-trackCX;
		labelDeltaY=y-trackCY;
		
		final Rectangle2D labelBounds=labelContainer.getBounds2D();
		labelContainer.setPosition(x-labelBounds.getWidth()/2.0, y-labelBounds.getHeight()/2.0);
		validateDependents();
		invalidate();
	}

	@Override
	public Rectangle2D getBounds2D() {
		return getBounds();
	}

	@Override
	public double getPriority() {
		return 10;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton()==MouseEvent.BUTTON1) {
			final Rectangle2D bounds=getBounds2D();
			grabDeltaX=e.getPoint().getX()-bounds.getCenterX();
			grabDeltaY=e.getPoint().getY()-bounds.getCenterY();
		}
		super.mousePressed(e);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK)==MouseEvent.BUTTON1_DOWN_MASK) {
			final double centerX, centerY;
			centerX=e.getPoint().getX()-grabDeltaX;
			centerY=e.getPoint().getY()-grabDeltaY;
			setCentroidPosition(centerX, centerY);
		}
		super.mouseDragged(e);
	}
}
