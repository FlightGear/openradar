package de.knewcleus.radar.ui.rpvd;

public enum StandardLabelPosition {
	TOPLEFT(-1,-1),
	TOP(0,-1),
	TOPRIGHT(1,-1),
	RIGHT(1,0),
	BOTTOMRIGHT(1,1),
	BOTTOM(0,1),
	BOTTOMLEFT(-1,1),
	LEFT(-1,0);
	
	private final int dx,dy;
	
	private StandardLabelPosition(int dx, int dy) {
		this.dx=dx;
		this.dy=dy;
	}
	
	public int getDx() {
		return dx;
	}
	
	public int getDy() {
		return dy;
	}
}
