package de.knewcleus.radar.ui.labels;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

public class LabelLineLayoutManager implements ILabelLayoutManager {
	protected Justification justification;
	protected final double gap;
	
	public LabelLineLayoutManager() {
		this(Justification.LEADING);
	}
	
	public LabelLineLayoutManager(Justification justification) {
		this(justification,2);
	}
	
	public LabelLineLayoutManager(Justification justification, double gap) {
		this.justification=justification;
		this.gap=gap;
	}

	public Justification getJustification() {
		return justification;
	}

	public double getGap() {
		return gap;
	}

	public void setJustification(Justification justification) {
		this.justification = justification;
	}

	@Override
	public double getAscent(LabelElementContainer element) {
		double maxAscent=0;
		
		for (LabelElement child: element.getChildren()) {
			maxAscent=Math.max(maxAscent,child.getAscent());
		}
		
		return maxAscent;
	}

	@Override
	public Dimension2D getMinimumSize(LabelElementContainer element) {
		double maxHeight=0;
		double width=0;
		int elements=0;

		for (LabelElement child: element.getChildren()) {
			Dimension2D minSize=child.getMinimumSize();
			width+=minSize.getWidth();
			maxHeight=Math.max(maxHeight,minSize.getHeight());
			elements++;
		}
		
		if (elements!=0) {
			width+=gap*(elements-1);
		}
		
		Dimension minSize=new Dimension();
		minSize.setSize(width, maxHeight);
		return minSize;
	}

	@Override
	public void layout(LabelElementContainer element) {
		Rectangle2D targetBounds=element.getBounds();
		
		double usedGap=gap;
		double x=targetBounds.getMinX();
		double yBaseline=targetBounds.getMinY()+element.getAscent();
		
		final Dimension2D minSize=element.getMinimumSize();
		
		switch (justification) {
		case CENTER:
			x=targetBounds.getCenterX()-minSize.getWidth()/2.0;
			break;
		case TRAILING:
			x=targetBounds.getMaxX()-minSize.getWidth();
			break;
		case JUSTIFY:
			int elementCount=element.getChildren().size();
			if (elementCount>0) {
				usedGap=(targetBounds.getWidth()-minSize.getWidth())/(elementCount-1);
			}
			break;
		}
		
		for (LabelElement child: element.getChildren()) {
			double ascent=child.getAscent();
			final Dimension2D childDimension=child.getMinimumSize();
			child.setBounds(new Rectangle2D.Double(x,yBaseline-ascent,childDimension.getWidth(),childDimension.getHeight()));
			x+=childDimension.getWidth()+usedGap;
		}
	}

}
