package de.knewcleus.radar.autolabel;

import java.util.Collection;

public class ChargePotentialGradientCalculator {
	protected final ChargedSymbol chargedSymbol;
	protected final double centerX,centerY;
	protected final double symbolCharge;
	
	protected double gradientX=0.0;
	protected double gradientY=0.0;
	
	public ChargePotentialGradientCalculator(ChargedSymbol chargedSymbol) {
		this.chargedSymbol=chargedSymbol;
		this.centerX=(chargedSymbol.getLeft()+chargedSymbol.getRight())/2.0;
		this.centerY=(chargedSymbol.getTop()+chargedSymbol.getBottom())/2.0;
		
		double w,h;
		
		w=chargedSymbol.getRight()-chargedSymbol.getLeft();
		h=chargedSymbol.getBottom()-chargedSymbol.getTop();
		
		symbolCharge=chargedSymbol.getChargeDensity()*w*h;
	}
	
	private void addCharge(double q, double cx, double cy) {
		final double dx=cx-centerX,dy=cy-centerY;
		final double r2=dx*dx+dy*dy;
		final double r=Math.sqrt(r2);
		
		final double magnitude=q/r2;
		gradientX+=-magnitude*dx/r;
		gradientY+=-magnitude*dy/r;
	}
	
	private void addLineCharge(double q, double px, double py, double p0x, double p0y, double nx, double ny) {
		final double dx=px-p0x,dy=py-p0y;
		final double r=Math.abs(dx*nx+dy*ny);
		
		final double magnitude=q/(r*r);
		gradientX+=magnitude*nx;
		gradientY+=magnitude*ny;
	}
	
	public void addCharge(ChargedSymbol charge) {
		final double cx=(charge.getLeft()+charge.getRight())/2.0;
		final double cy=(charge.getTop()+charge.getBottom())/2.0;
		final double w=charge.getRight()-charge.getLeft();
		final double h=charge.getBottom()-charge.getTop();
		final double q=charge.getChargeDensity()*w*h;

		addCharge(q,cx,cy);
		
		/*
		 * overlap in y-direction:
		 * 
		 * max(l1,l2)<=min(u1,u2)
		 * l1<=u2 && l2<=u1
		 */
		if (charge.getTop()<=chargedSymbol.getBottom() && chargedSymbol.getTop()<=charge.getBottom()) {
			/* Overlap in y-direction */
			if (chargedSymbol.getLeft()>charge.getRight()) {
				/* Add deflection by right edge */
				addLineCharge(q, chargedSymbol.getLeft(), 0, charge.getRight(), 0, 1.0, 0.0);
			} else if (chargedSymbol.getRight()<charge.getLeft()) {
				/* Add deflection by left edge */
				addLineCharge(q, chargedSymbol.getRight(),0, charge.getLeft(), 0, -1.0, 0.0);
			}
		}
		
		if (charge.getLeft()<=chargedSymbol.getRight() && chargedSymbol.getLeft()<=charge.getRight()) {
			/* Overlap in x-direction */
			if (chargedSymbol.getBottom()<charge.getTop()) {
				/* Add deflection by top edge */
				addLineCharge(q, 0, chargedSymbol.getBottom(), 0, charge.getTop(), 0, -1);
			} else if (chargedSymbol.getTop()>charge.getBottom()) {
				/* Add deflection by bottom edge */
				addLineCharge(q, 0, chargedSymbol.getTop(), 0, charge.getBottom(), 0, 1);
			}
		}
	}
	
	public void addCharges(Collection<? extends ChargedSymbol> charges) {
		for (ChargedSymbol charge: charges)
			addCharge(charge);
	}
	
	public PotentialGradient getGradient() {
		return new PotentialGradient(symbolCharge*gradientX,symbolCharge*gradientY);
	}
}
