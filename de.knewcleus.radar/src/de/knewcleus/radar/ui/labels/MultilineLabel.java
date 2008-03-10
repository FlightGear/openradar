package de.knewcleus.radar.ui.labels;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;

public class MultilineLabel implements IActiveLabelElement {
	protected final List<? extends ILabelElement> lines;
	protected final int gap;
	protected Dimension elementsSize;
	protected Dimension minimumSize;
	protected Rectangle bounds;
	
	public MultilineLabel(List<? extends ILabelElement> lines) {
		this(lines,2);
	}
	
	public MultilineLabel(List<? extends ILabelElement> lines, int gap) {
		this.lines=lines;
		this.gap=gap;
	}

	@Override
	public int getAscent() {
		return minimumSize.height;
	}

	@Override
	public Dimension getMinimumSize() {
		return minimumSize;
	}

	@Override
	public void layout() {
		int maximumWidth=0;
		int totalHeight=0;
		
		for (ILabelElement element: lines) {
			element.layout();
			Dimension elementDimension=element.getMinimumSize();
			maximumWidth=Math.max(maximumWidth,elementDimension.width);
			totalHeight+=elementDimension.height;
		}
		
		elementsSize=new Dimension(maximumWidth,totalHeight);
		
		if (lines.size()>1) {
			totalHeight+=gap*(lines.size()-1);
		}
		
		minimumSize=new Dimension(maximumWidth,totalHeight);
	}

	@Override
	public void setBounds(Rectangle newBounds) {
		this.bounds=newBounds;
		int x,y,w,h;
		
		x=newBounds.x;
		y=newBounds.y;
		w=newBounds.width;
		h=newBounds.height;
		
		int excessHeight=h-elementsSize.height;
		int elementGap=gap;
		
		if (lines.size()>1) {
			elementGap=excessHeight/(lines.size()-1);
		}
		
		for (ILabelElement element: lines) {
			Dimension elementDimension=element.getMinimumSize();
			element.setBounds(new Rectangle(x,y,w,elementDimension.height));
			y+=elementDimension.height+elementGap;
		}
	}
	
	@Override
	public Rectangle getBounds() {
		return bounds;
	}

	@Override
	public void paint(Graphics2D g2d) {
		for (ILabelElement element: lines) {
			element.paint(g2d);
		}
	}
	
	@Override
	public void processMouseEvent(MouseEvent event) {
		for (ILabelElement element: lines) {
			Rectangle elementBounds=element.getBounds();
			if (elementBounds.contains(event.getPoint())) {
				if (element instanceof IActiveLabelElement) {
					IActiveLabelElement activeLabelElement=(IActiveLabelElement)element;
					if (activeLabelElement.isEnabled()) {
						((IActiveLabelElement)element).processMouseEvent(event);
						event.consume();
					}
				}
				break;
			}
		}
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
}
