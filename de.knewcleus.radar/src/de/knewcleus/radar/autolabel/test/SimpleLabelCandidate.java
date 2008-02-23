package de.knewcleus.radar.autolabel.test;

import de.knewcleus.radar.autolabel.BoundedSymbol;
import de.knewcleus.radar.autolabel.LabelCandidate;
import de.knewcleus.radar.autolabel.LabeledObject;
import de.knewcleus.radar.autolabel.ProtectedSymbol;

public class SimpleLabelCandidate implements BoundedSymbol, LabelCandidate {
	protected final LabeledObject associatedObject;
	protected final double cost;
	protected final double top,bottom,left,right;

	public SimpleLabelCandidate(LabeledObject associatedObject, double cost, double top, double bottom, double left, double right) {
		this.associatedObject=associatedObject;
		this.cost=cost;
		this.top=top;
		this.bottom=bottom;
		this.left=left;
		this.right=right;
	}
	
	@Override
	public LabeledObject getAssociatedObject() {
		return associatedObject;
	}
	
	@Override
	public double getCost() {
		return cost;
	}
	
	@Override
	public double getBottom() {
		return bottom;
	}

	@Override
	public double getLeft() {
		return left;
	}

	@Override
	public double getRight() {
		return right;
	}

	@Override
	public double getTop() {
		return top;
	}
	
	@Override
	public String toString() {
		return "[object="+associatedObject+", left="+left+", right="+right+", top="+top+", bottom="+bottom+"]";
	}
}
