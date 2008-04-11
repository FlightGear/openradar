package de.knewcleus.radar.autolabel;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public abstract class Autolabeller {

	protected final Set<ILabeledObject> labeledObjects = new HashSet<ILabeledObject>();

	protected final Set<DisplayObject> displayObjects = new HashSet<DisplayObject>();
	protected final Deque<ILabeledObject> objectsToProcess = new ArrayDeque<ILabeledObject>();
	protected final double minimumDisplacement;
	protected final double maximumDisplacement;
	
	public Autolabeller(double minimumDisplacement, double maximumDisplacement) {
		this.minimumDisplacement=minimumDisplacement;
		this.maximumDisplacement=maximumDisplacement;
	}

	public synchronized void addLabeledObject(ILabeledObject object) {
		labeledObjects.add(object);
		objectsToProcess.addLast(object);
	}

	public synchronized void removeLabeledObject(ILabeledObject object) {
		labeledObjects.remove(object);
		objectsToProcess.remove(object);
	}

	public Set<ILabeledObject> getLabeledObjects() {
		return Collections.unmodifiableSet(labeledObjects);
	}

	public synchronized void addDisplayObject(DisplayObject object) {
		displayObjects.add(object);
	}

	public synchronized void removeDisplayObject(DisplayObject object) {
		displayObjects.remove(object);
	}

	public Set<DisplayObject> getDisplayObjects() {
		return Collections.unmodifiableSet(displayObjects);
	}

	public synchronized void updateOneLabel() {
		if (objectsToProcess.isEmpty())
			return;
		
		final ILabeledObject labeledObject=objectsToProcess.removeFirst();
		final ILabel label=labeledObject.getLabel();
		if (label!=null && label.isAutolabelled()) {
			updateLabel(label);
		}
		
		objectsToProcess.addLast(labeledObject);
	}

	protected abstract void updateLabel(ILabel label);
}