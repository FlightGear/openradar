package de.knewcleus.radar.autolabel.test;

import java.util.HashSet;
import java.util.Set;

import de.knewcleus.radar.autolabel.LabelCandidate;
import de.knewcleus.radar.autolabel.LabeledObject;
import de.knewcleus.radar.autolabel.ProtectedSymbol;

public class PointObject implements LabeledObject,ProtectedSymbol {
	// priorisation: top-right, bottom-left, bottom-right, right, top-left, bottom, left, top
	protected static final double[] labelDeltaX=new double[] {1.0,-1.0,1.0,1.0,-1.0,0.0,-1.0,0.0};
	protected static final double[] labelDeltaY=new double[] {-1.0,1.0,1.0,0.0,-1.0,1.0,0.0,-1.0};
	protected static final double labelWidth=0.05;
	protected static final double labelHeight=0.025;
	protected static final double labelDist=0.005;
	protected final double x,y;
	protected final double r;
	protected final Set<LabelCandidate> labelCandidates=new HashSet<LabelCandidate>(); 

	public PointObject(double x, double y, double r) {
		this.x=x;
		this.y=y;
		this.r=r;
		
		assert(labelDeltaX.length==labelDeltaY.length);
		for (int i=0;i<labelDeltaX.length;i++) {
			double cx,cy;
			
			// cx=x+labelDeltaX[i]*(r+w/2)
			// cy=y+labelDeltaY[i]*(r+h/2);
			cx=x+labelDeltaX[i]*(r+labelDist+labelWidth/2);
			cy=y+labelDeltaY[i]*(r+labelDist+labelHeight/2);
			
			double top,left,bottom,right;
			left=cx-labelWidth/2;
			right=cx+labelWidth/2;
			top=cy-labelHeight/2;
			bottom=cy+labelHeight/2;
			
			LabelCandidate labelCandidate=new SimpleLabelCandidate(this,i+1,top,bottom,left,right);
			labelCandidates.add(labelCandidate);
		}
	}
	
	@Override
	public Set<LabelCandidate> getLabelCandidates() {
		return labelCandidates;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getR() {
		return r;
	}

	@Override
	public double getBottom() {
		return y+r;
	}

	@Override
	public double getLeft() {
		return x-r;
	}

	@Override
	public double getRight() {
		return x+r;
	}

	@Override
	public double getTop() {
		return y-r;
	}
	
	@Override
	public double getOverlapPenalty() {
		return 1.0E4;
	}
	
	@Override
	public double getUnlabeledPenalty() {
		return 1.0E4;
	}
}
