package de.knewcleus.radar.autolabel;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ChargePotentialAutolabeller extends Autolabeller {
	protected final ILabelPotentialGradientCalculator labelPotentialGradientCalculator;
	
	public ChargePotentialAutolabeller(ILabelPotentialGradientCalculator labelPotentialGradientCalculator, double minimumDisplacement, double maximumDisplacement) {
		super(minimumDisplacement, maximumDisplacement);
		this.labelPotentialGradientCalculator=labelPotentialGradientCalculator;
	}
	
	@Override
	protected void updateLabel(ILabel label) {
		/* First determine the simple charge potential gradient (Coloumb-Force) */
		ChargePotentialGradientEvaluator gradientCalculator=new ChargePotentialGradientEvaluator(label);
		
		for (ILabel otherLabel: labels) {
			if (otherLabel==label)
				continue; // skip the label itself
			gradientCalculator.addCharge(otherLabel);
			final DisplayObject otherObject=otherLabel.getLabeledObject();
			gradientCalculator.addCharge(otherObject);
		}
		
		for (DisplayObject charge: displayObjects) {
			gradientCalculator.addCharge(charge);
		}
		
		/* Then determine the potential gradient from the labelled object itself */
		final Rectangle2D labelBounds=label.getBounds2D();
		final Point2D center=new Point2D.Double(labelBounds.getCenterX(), labelBounds.getCenterY());
		PotentialGradient labelGradient=labelPotentialGradientCalculator.getPotentialGradient(label, center);
		
		PotentialGradient totalGradient=labelGradient.add(gradientCalculator.getGradient());
		
		if (totalGradient.magntitude()<minimumDisplacement) {
			totalGradient=new PotentialGradient();
		}
		if (totalGradient.magntitude()>=maximumDisplacement) {
			totalGradient=totalGradient.normalise().scale(maximumDisplacement);
		}
		
		label.setCentroidPosition(labelBounds.getCenterX()+totalGradient.getDx(),labelBounds.getCenterY()+totalGradient.getDy());
	}
}
