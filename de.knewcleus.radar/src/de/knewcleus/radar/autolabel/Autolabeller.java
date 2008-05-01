package de.knewcleus.radar.autolabel;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public abstract class Autolabeller {

	protected final Set<ILabel> labels = new HashSet<ILabel>();

	protected final Set<DisplayObject> displayObjects = new HashSet<DisplayObject>();
	protected final Deque<ILabel> labelsToProcess = new ArrayDeque<ILabel>();
	protected final double minimumDisplacement;
	protected final double maximumDisplacement;
	
	public Autolabeller(double minimumDisplacement, double maximumDisplacement) {
		this.minimumDisplacement=minimumDisplacement;
		this.maximumDisplacement=maximumDisplacement;
	}

	public synchronized void addLabel(ILabel label) {
		labels.add(label);
		labelsToProcess.addLast(label);
	}

	public synchronized void removeLabel(ILabel label) {
		labels.remove(label);
		labelsToProcess.remove(label);
	}

	public Set<ILabel> getLabels() {
		return Collections.unmodifiableSet(labels);
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
		if (labelsToProcess.isEmpty())
			return;
		
		final ILabel label=labelsToProcess.removeFirst();
		if (label!=null && label.isAutolabelled()) {
			updateLabel(label);
		}
		
		labelsToProcess.addLast(label);
	}

	protected abstract void updateLabel(ILabel label);
}