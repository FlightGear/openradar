package de.knewcleus.openradar.view.layout;

public class Insets2D {
	protected double topInset, bottomInset, leftInset, rightInset;

	public Insets2D() {
		this(0.0, 0.0, 0.0, 0.0);
	}
	
	public Insets2D(double topInset, double bottomInset, double leftInset, double rightInset) {
		this.topInset = topInset;
		this.bottomInset = bottomInset;
		this.leftInset = leftInset;
		this.rightInset = rightInset;
	}
	
	public Insets2D(Insets2D copy) {
		this(copy.topInset, copy.bottomInset, copy.leftInset, copy.rightInset);
	}

	public double getTopInset() {
		return topInset;
	}

	public void setTopInset(double topInset) {
		this.topInset = topInset;
	}

	public double getBottomInset() {
		return bottomInset;
	}

	public void setBottomInset(double bottomInset) {
		this.bottomInset = bottomInset;
	}

	public double getLeftInset() {
		return leftInset;
	}

	public void setLeftInset(double leftInset) {
		this.leftInset = leftInset;
	}

	public double getRightInset() {
		return rightInset;
	}

	public void setRightInset(double rightInset) {
		this.rightInset = rightInset;
	}
	
	public double getHorizontalInsets() {
		return leftInset + rightInset;
	}
	
	public double getVerticalInsets() {
		return topInset + bottomInset;
	}
	
	public static Insets2D add(Insets2D src1, Insets2D src2, Insets2D dest) {
		if (dest==null) {
			dest = new Insets2D();
		}
		dest.setLeftInset(src1.getLeftInset()+src2.getLeftInset());
		dest.setRightInset(src1.getRightInset()+src2.getRightInset());
		dest.setTopInset(src1.getTopInset()+src2.getTopInset());
		dest.setBottomInset(src1.getBottomInset()+src2.getBottomInset());
		
		return dest;
	}
}
