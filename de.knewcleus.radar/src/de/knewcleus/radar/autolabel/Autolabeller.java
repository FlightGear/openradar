package de.knewcleus.radar.autolabel;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public abstract class Autolabeller {

	protected final Set<LabeledObject> labeledObjects = new HashSet<LabeledObject>();

	protected final Set<DisplayObject> displayObjects = new HashSet<DisplayObject>();
	protected final Deque<LabeledObject> objectsToProcess = new ArrayDeque<LabeledObject>();
	protected final double minimumDisplacement;
	protected final double maximumDisplacement;
	
	public Autolabeller(double minimumDisplacement, double maximumDisplacement) {
		this.minimumDisplacement=minimumDisplacement;
		this.maximumDisplacement=maximumDisplacement;
	}

	public synchronized void addLabeledObject(LabeledObject object) {
		labeledObjects.add(object);
		objectsToProcess.addLast(object);
	}

	public synchronized void removeLabeledObject(LabeledObject object) {
		labeledObjects.remove(object);
		objectsToProcess.remove(object);
	}

	public Set<LabeledObject> getLabeledObjects() {
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
		
		LabeledObject labeledObject=objectsToProcess.removeFirst();
		if (!labeledObject.isLocked()) {
			updateLabel(labeledObject);
		}
		
		objectsToProcess.addLast(labeledObject);
	}

	protected abstract void updateLabel(LabeledObject labeledObject);
}