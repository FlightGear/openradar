package de.knewcleus.openradar.ui.labels;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

public class MultiLineLabelLayoutManager implements ILabelLayoutManager {
	protected Justification justification;
	protected double gap;

	public MultiLineLabelLayoutManager() {
		this(Justification.LEADING);
	}
	
	public MultiLineLabelLayoutManager(Justification justification) {
		this(justification,2);
	}
	
	public MultiLineLabelLayoutManager(Justification justification, double gap) {
		this.justification=justification;
		this.gap=gap;
	}
	
	@Override
	public double getAscent(LabelElementContainer element) {
		return getMinimumSize(element).getHeight();
	}

	@Override
	public Dimension2D getMinimumSize(LabelElementContainer element) {
		double height=0;
		double maxWidth=0;
		int elementCount=0;
		
		for (LabelElement child: element.getChildren()) {
			elementCount++;
			Dimension2D childSize=child.getMinimumSize();
			maxWidth=Math.max(maxWidth,childSize.getWidth());
			height+=childSize.getHeight();
		}
		
		if (elementCount>0) {
			height+=gap*(elementCount-1);
		}
		
		Dimension minSize=new Dimension();
		minSize.setSize(maxWidth, height);
		
		return minSize;
	}

	@Override
	public void layout(LabelElementContainer element) {
		Rectangle2D targetBounds=element.getBounds2D();
		
		double usedGap=gap;
		double x=targetBounds.getMinX();
		double y=targetBounds.getMinY();
		
		final Dimension2D minSize=element.getMinimumSize();
		int elementCount=element.getChildren().size();
		if (elementCount>0) {
			usedGap=(targetBounds.getWidth()-minSize.getWidth())/(elementCount-1);
		}
		
		for (LabelElement child: element.getChildren()) {
			final Dimension2D childDimension=child.getMinimumSize();
			double childX=x;
			double childWidth=childDimension.getWidth();
			
			switch (justification) {
			case TRAILING:
				childX=targetBounds.getMaxX()-childDimension.getWidth();
				break;
			case CENTER:
				childX=targetBounds.getCenterX()-childWidth/2.0;
				break;
			case JUSTIFY:
				childWidth=targetBounds.getWidth();
				break;
			}
			child.setBounds2D(new Rectangle2D.Double(childX,y,childWidth,childDimension.getHeight()));
			y+=childDimension.getHeight()+usedGap;
		}
	}

}
