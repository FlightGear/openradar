package de.knewcleus.radar.ui.labels;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;

public class LabelLine implements IActiveLabelElement {
	public enum Justification {
		LEADING,
		CENTER,
		JUSTIFY,
		TRAILING
	};
	
	protected final List<ILabelElement> elements;
	protected final Justification justification;
	protected final int gap;
	protected Dimension elementsSize;
	protected Dimension minimumSize=null;
	protected Rectangle bounds;
	protected int ascent=0;
	
	public LabelLine(List<ILabelElement> elements) {
		this(elements,Justification.LEADING);
	}
	
	public LabelLine(List<ILabelElement> elements, Justification justification) {
		this(elements,justification,2);
	}
	
	public LabelLine(List<ILabelElement> elements, Justification justification, int gap) {
		this.elements=elements;
		this.justification=justification;
		this.gap=gap;
	}
	
	@Override
	public void layout() {
		int maximumAscent=0,maximumDescent=0;
		int minimumWidth=0;
		
		for (ILabelElement element: elements) {
			element.layout();
			Dimension dimension=element.getMinimumSize();
			int ascent=element.getAscent();
			maximumAscent=Math.max(maximumAscent,ascent);
			maximumDescent=Math.max(maximumDescent,dimension.height-ascent);
			minimumWidth+=dimension.width;
		}
		
		elementsSize=new Dimension(minimumWidth,maximumAscent+maximumDescent);
		
		if (elements.size()>1) {
			minimumWidth+=gap*(elements.size()-1);
		}
		
		minimumSize=new Dimension(minimumWidth,maximumAscent+maximumDescent);
		ascent=maximumAscent;
	}
	
	@Override
	public int getAscent() {
		return ascent;
	}
	
	@Override
	public Dimension getMinimumSize() {
		return minimumSize;
	}
	
	@Override
	public void setBounds(Rectangle newBounds) {
		this.bounds=newBounds;
		int x=newBounds.x, y=newBounds.y;
		int w=newBounds.width, h=newBounds.height;
		
		int baselineY=y+h-ascent;
		
		int xStart=x; /* For justified or left-aligned lines */
		int widthExcess=w-elementsSize.width;
		int elementGap=gap;
		
		switch (justification) {
		case TRAILING:
			xStart=x+widthExcess;
			break;
		case CENTER:
			xStart=x+widthExcess/2;
			break;
		}
		
		if (justification==Justification.JUSTIFY && elements.size()>1) {
			elementGap=widthExcess/(elements.size()-1);
		}
		
		for (ILabelElement element: elements) {
			int elementAscent=element.getAscent();
			Dimension elementDimension=element.getMinimumSize();
			int elementY=baselineY-elementAscent;
			element.setBounds(new Rectangle(xStart,elementY,elementDimension.width,elementDimension.height));
			xStart+=elementDimension.width+elementGap;
		}
	}
	
	@Override
	public Rectangle getBounds() {
		return bounds;
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		for (ILabelElement element: elements) {
			element.paint(g2d);
		}
	}
	
	@Override
	public void processMouseEvent(MouseEvent event) {
		for (ILabelElement element: elements) {
			Rectangle elementBounds=element.getBounds();
			if (elementBounds.contains(event.getPoint())) {
				if (element instanceof IActiveLabelElement) {
					event.translatePoint(-elementBounds.x, -elementBounds.y);
					((IActiveLabelElement)element).processMouseEvent(event);
					event.translatePoint(elementBounds.x, elementBounds.y);
				}
				break;
			}
		}
	}
}
