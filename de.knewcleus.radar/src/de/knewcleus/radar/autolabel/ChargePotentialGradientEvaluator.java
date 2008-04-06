package de.knewcleus.radar.autolabel;

import java.awt.geom.Rectangle2D;
import java.util.Collection;

public class ChargePotentialGradientEvaluator {
	protected final DisplayObject chargedSymbol;
	protected final double symbolCharge;
	
	protected double gradientX=0.0;
	protected double gradientY=0.0;
	
	protected final double epsilon=1E-4;
	
	public ChargePotentialGradientEvaluator(DisplayObject chargedSymbol) {
		this.chargedSymbol=chargedSymbol;
		final Rectangle2D bounds=chargedSymbol.getBounds2D();
		final double w,h;
		
		w=bounds.getWidth();
		h=bounds.getHeight();
		
		symbolCharge=chargedSymbol.getPriority()*w*h;
	}
	
	private void addCharge(double q, double cx, double cy) {
		final Rectangle2D bounds=chargedSymbol.getBounds2D();
		final double dx=cx-bounds.getCenterX(),dy=cy-bounds.getCenterY();
		final double r2=dx*dx+dy*dy;
		final double r=Math.sqrt(r2);
		
		if (r<epsilon)
			return;
		final double magnitude=q/r2;
		gradientX+=-magnitude*dx/r;
		gradientY+=-magnitude*dy/r;
	}
	
	private void addLineCharge(double q, double px, double py, double p0x, double p0y, double nx, double ny) {
		final double dx=px-p0x,dy=py-p0y;
		final double r=Math.abs(dx*nx+dy*ny);
		
		if (r<epsilon)
			return;
		
		final double magnitude=q/(r*r);
		gradientX+=magnitude*nx;
		gradientY+=magnitude*ny;
	}
	
	public void addCharge(DisplayObject charge) {
		final Rectangle2D symbolBounds=chargedSymbol.getBounds2D();
		final Rectangle2D chargeBounds=charge.getBounds2D();
		final double cx=chargeBounds.getCenterX();
		final double cy=chargeBounds.getCenterY();
		final double w=chargeBounds.getWidth();
		final double h=chargeBounds.getHeight();
		final double q=charge.getPriority()*w*h;

		addCharge(q,cx,cy);
		
		if (chargeBounds.getMinY()<=symbolBounds.getMaxY() && symbolBounds.getMinY()<=chargeBounds.getMaxY()) {
			/* Overlap in y-direction */
			if (symbolBounds.getMinY()>chargeBounds.getMaxX()) {
				/* Add deflection by right edge */
				addLineCharge(q, symbolBounds.getMinY(), 0, chargeBounds.getMaxX(), 0, 1.0, 0.0);
			} else if (symbolBounds.getMaxX()<chargeBounds.getMinY()) {
				/* Add deflection by left edge */
				addLineCharge(q, symbolBounds.getMaxX(),0, chargeBounds.getMinY(), 0, -1.0, 0.0);
			}
		}
		
		if (chargeBounds.getMinY()<=symbolBounds.getMaxX() && symbolBounds.getMinY()<=chargeBounds.getMaxX()) {
			/* Overlap in x-direction */
			if (symbolBounds.getMaxY()<chargeBounds.getMinY()) {
				/* Add deflection by top edge */
				addLineCharge(q, 0, symbolBounds.getMaxY(), 0, chargeBounds.getMinY(), 0, -1);
			} else if (symbolBounds.getMinY()>chargeBounds.getMaxY()) {
				/* Add deflection by bottom edge */
				addLineCharge(q, 0, symbolBounds.getMinY(), 0, chargeBounds.getMaxY(), 0, 1);
			}
		}
	}
	
	public void addCharges(Collection<? extends DisplayObject> charges) {
		for (DisplayObject charge: charges)
			addCharge(charge);
	}
	
	public PotentialGradient getGradient() {
		return new PotentialGradient(symbolCharge*gradientX,symbolCharge*gradientY);
	}
}
