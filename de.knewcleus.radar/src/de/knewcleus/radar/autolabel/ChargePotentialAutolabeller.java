package de.knewcleus.radar.autolabel;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ChargePotentialAutolabeller extends Autolabeller {
	public ChargePotentialAutolabeller(double minimumDisplacement, double maximumDisplacement) {
		super(minimumDisplacement, maximumDisplacement);
	}
	
	@Override
	protected void updateLabel(LabeledObject labeledObject) {
		final Label label=labeledObject.getLabel();
		if (label==null) {
			/* At some times labelled objects do not display their labels */
			return;
		}
		
		/* First determine the simple charge potential gradient (Coloumb-Force) */
		ChargePotentialGradientCalculator gradientCalculator=new ChargePotentialGradientCalculator(label);
		
		for (LabeledObject object: labeledObjects) {
			if (object==labeledObject)
				continue; // skip the object itself
			gradientCalculator.addCharge(object);
			final Label objectLabel=object.getLabel();
			if (objectLabel!=null) {
				gradientCalculator.addCharge(object.getLabel());
			}
		}
		
		for (DisplayObject charge: displayObjects) {
			gradientCalculator.addCharge(charge);
		}
		
		/* Then determine the potential gradient from the labelled object itself */
		final Rectangle2D labelBounds=label.getBounds2D();
		final Point2D center=new Point2D.Double(labelBounds.getCenterX(), labelBounds.getCenterY());
		PotentialGradient labelGradient=labeledObject.getPotentialGradient(center);
		
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
