package de.knewcleus.radar.autolabel;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Autolabeller {
	protected final Set<LabeledObject> labeledObjects=new HashSet<LabeledObject>();
	protected final Set<ChargedSymbol> chargedSymbols=new HashSet<ChargedSymbol>();
	protected final Deque<LabeledObject> objectsToProcess=new ArrayDeque<LabeledObject>();
	protected final Random random=new Random();
	protected final double resistance;
	protected final double maxDisplacement;
	
	public Set<LabeledObject> getLabeledObjects() {
		return Collections.unmodifiableSet(labeledObjects);
	}
	
	public Autolabeller(double resistance, double maxDisplacement) {
		this.resistance=resistance;
		this.maxDisplacement=maxDisplacement;
	}
	
	public synchronized void addLabeledObject(LabeledObject object) {
		labeledObjects.add(object);
		
		objectsToProcess.addLast(object);
	}
	
	public synchronized void addChargedSymbol(ChargedSymbol symbol) {
		chargedSymbols.add(symbol);
	}
	
	public synchronized void removeLabeledObject(LabeledObject object) {
		labeledObjects.remove(object);
		objectsToProcess.remove(object);
	}
	
	public synchronized void removeChargedSymbol(ChargedSymbol symbol) {
		chargedSymbols.remove(symbol);
	}
	
	public synchronized void updateOneLabel() {
		if (objectsToProcess.isEmpty())
			return;
		
		LabeledObject labeledObject=objectsToProcess.removeFirst();
		if (labeledObject.isLocked()) {
			objectsToProcess.addLast(labeledObject);
			return;
		}
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
		
		for (ChargedSymbol charge: chargedSymbols) {
			gradientCalculator.addCharge(charge);
		}
		
		/* Then determine the potential gradient from the labelled object itself */
		final Rectangle2D labelBounds=label.getBounds2D();
		final Point2D center=new Point2D.Double(labelBounds.getCenterX(), labelBounds.getCenterY());
		PotentialGradient labelGradient=labeledObject.getPotentialGradient(center);
		
		PotentialGradient totalGradient=labelGradient.add(gradientCalculator.getGradient());
		
		if (totalGradient.magntitude()<resistance) {
			totalGradient=new PotentialGradient();
		}
		if (totalGradient.magntitude()>=maxDisplacement) {
			totalGradient=totalGradient.normalise().scale(maxDisplacement);
		}
		
		label.setCentroidPosition(labelBounds.getCenterX()+totalGradient.getDx(),labelBounds.getCenterY()+totalGradient.getDy());
		
		objectsToProcess.addLast(labeledObject);
	}
}
