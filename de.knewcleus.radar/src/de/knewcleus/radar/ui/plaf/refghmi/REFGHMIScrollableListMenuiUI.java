package de.knewcleus.radar.ui.plaf.refghmi;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.border.Border;

import de.knewcleus.radar.ui.ScrollableListMenu;
import de.knewcleus.radar.ui.plaf.ScrollableListMenuUI;

public class REFGHMIScrollableListMenuiUI extends ScrollableListMenuUI {
	protected ScrollableListMenu menu;
	protected Color arrowColor=Color.WHITE;
	protected Rectangle contentRectangle;
	protected Dimension elementSize;
	protected Rectangle elementsRectangle;
	protected int firstVisibleItem;
	protected int visibleElementCount;
	protected Rectangle topArrowRectangle;
	protected Rectangle bottomArrowRectangle;
	
	protected final static Border border=new REFGHMIBorders.EtchedBorder();
	protected final static Border elementBorder=new REFGHMIBorders.EtchedBorder();
	protected final static Insets arrowInsets=new Insets(2,2,2,2);
	protected final static Insets elementInsets=new Insets(2,2,2,2);
	protected final static int minimumArrowHeight=20;
	protected final static int minimumArrowWidth=40;
	
	public REFGHMIScrollableListMenuiUI(ScrollableListMenu menu) {
	}
	
	public static ScrollableListMenuUI createUI(JComponent c) {
		return new REFGHMIScrollableListMenuiUI((ScrollableListMenu)c);
	}
	
	@Override
	public void installUI(JComponent c) {
		menu=(ScrollableListMenu)c;
	}

	public void ensureIndexIsVisible(int index) {
		calculateGeometry();
		if (index<0 || menu.getListModel().getSize()<=index) {
			/* Ignore invalid index */
			return;
		}
		if (menu.getListModel().getSize()<visibleElementCount) {
			firstVisibleItem=0;
			return;
		}
		if (firstVisibleItem<=index && index<firstVisibleItem+visibleElementCount) {
			/* The item already is visible */
			return;
		}
		
		/* Scroll the visible items as short as possible */
		if (index<firstVisibleItem) {
			/* We need to scroll up */
			firstVisibleItem=index;
		}
		if (index>=firstVisibleItem+visibleElementCount) {
			firstVisibleItem=index-visibleElementCount+1;
		}
		
		assert(0<=firstVisibleItem && (menu.getListModel().getSize()<visibleElementCount || firstVisibleItem+visibleElementCount-1<menu.getListModel().getSize()));
		assert(firstVisibleItem<=index && index<firstVisibleItem+visibleElementCount);
		
		menu.repaint();
	}
	
	protected void calculateGeometry() {
		// TODO: cache these values
		calculateContentRectangle();
		calculateElementSize();
		calculateElementsRectangle();
		
		/* This is the count of fully visible entries */
		visibleElementCount=elementsRectangle.height/elementSize.height;
		
		calculateArrowRectangles();
	}
	
	protected void calculateContentRectangle() {
		final Insets insets=menu.getInsets();
		final Dimension size=menu.getSize();
		final int cw,ch;
		
		cw=size.width-(insets.left+insets.right);
		ch=size.height-(insets.top+insets.bottom);
		
		contentRectangle=new Rectangle(insets.left,insets.top,cw,ch);
	}
	
	protected void calculateElementSize() {
		final int elementHeight=getMinimumElementHeight();
		
		elementSize=new Dimension(contentRectangle.width, elementHeight);
	}
	
	protected void calculateElementsRectangle() {
		/* The arrows are one element high */
		final int elementsHeight=contentRectangle.height-2*elementSize.height;
		
		elementsRectangle=new Rectangle(contentRectangle.x, contentRectangle.y+elementSize.height, contentRectangle.width, elementsHeight);
	}
	
	protected void calculateArrowRectangles() {
		topArrowRectangle=new Rectangle(contentRectangle.x, contentRectangle.y, elementSize.width, elementSize.height);
		bottomArrowRectangle=new Rectangle(contentRectangle.x, contentRectangle.y+contentRectangle.height-elementSize.height,
										   elementSize.width, elementSize.height);
	}
	
	protected int getMaximumTextWidth() {
		// TODO: cache this value
		final Font font=menu.getFont();
		final FontMetrics fontMetrics=menu.getFontMetrics(font);
		
		int maximumTextWidth=0;
		for (int i=0;i<menu.getListModel().getSize();i++) {
			Object element=menu.getListModel().getElementAt(i);
			String text=element.toString();
			maximumTextWidth=Math.max(maximumTextWidth, fontMetrics.stringWidth(text));
		}
		
		return maximumTextWidth;
	}
	
	protected int getMaximumTextHeight() {
		final Font font=menu.getFont();
		final FontMetrics fontMetrics=menu.getFontMetrics(font);
		
		return fontMetrics.getMaxAscent()+fontMetrics.getMaxDescent();
	}
	
	
	protected int getMinimumElementHeight() {
		final Insets elementBorderInsets=elementBorder.getBorderInsets(menu);
		
		final int textHeight=getMaximumTextHeight();
		final int elementHeight=Math.max(minimumArrowHeight+(arrowInsets.top+arrowInsets.bottom),
										 textHeight+(elementInsets.top+elementInsets.bottom));
		
		return elementHeight+(elementBorderInsets.top+elementBorderInsets.bottom);
	}

	protected int getMinimumElementWidth() {
		final Insets elementBorderInsets=elementBorder.getBorderInsets(menu);
		
		final int textWidth=getMaximumTextWidth();
		final int elementWidth=Math.max(minimumArrowWidth+(arrowInsets.left+arrowInsets.right),
										textWidth+(elementInsets.left+elementInsets.right));
		
		return elementWidth+(elementBorderInsets.left+elementBorderInsets.right);
	}
	
	@Override
	public Dimension getMinimumSize(JComponent c) {
		Insets insets=c.getInsets();
		/* We want to show at least one entry and the arrows */
		final int minimumHeight=insets.top+3*getMinimumElementHeight()+insets.bottom;
		final int minimumWidth=insets.left+getMinimumElementWidth()+insets.right;
		
		System.out.println("Minimum size="+minimumWidth+","+minimumHeight);
		
		return new Dimension(minimumWidth, minimumHeight);
	}
	
	@Override
	public Dimension getPreferredSize(JComponent c) {
		Insets insets=c.getInsets();
		
		final int preferredVisibleElementCount=Math.min(menu.getListModel().getSize(), 9);
		final int preferredHeight=insets.top+(2+preferredVisibleElementCount)*getMinimumElementHeight()+insets.bottom;
		final int preferredWidth=insets.left+getMinimumElementWidth()+insets.right;
		
		return new Dimension(preferredWidth, preferredHeight);
	}
	
	@Override
	public void paint(Graphics g, JComponent c) {
		calculateGeometry();
		final Rectangle clipBounds=g.getClipBounds();
		
		/* Paint the background */
		g.setColor(menu.getBackground());
		g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
		
		if (clipBounds.intersects(topArrowRectangle)){
			paintTopArrow(g);
		}
		if (clipBounds.intersects(elementsRectangle)){
			paintElements(g);
		}
		if (clipBounds.intersects(bottomArrowRectangle)){
			paintBottomArrow(g);
		}
	}
	
	protected void paintArrow(Graphics g, int xc, int y, int width, int height) {
		g.setColor(arrowColor);
		
		Polygon arrow=new Polygon();
		arrow.addPoint(xc,y);
		arrow.addPoint(xc-width/2,y+height);
		arrow.addPoint(xc+width/2,y+height);
		
		g.fillPolygon(arrow);
		
	}
	
	protected void paintTopArrow(Graphics g) {
		elementBorder.paintBorder(menu, g, topArrowRectangle.x, topArrowRectangle.y, topArrowRectangle.width, topArrowRectangle.height);
		final Insets elementInsets=elementBorder.getBorderInsets(menu);
		final int arrowHeight=topArrowRectangle.height-
				(elementInsets.top+elementInsets.bottom)-
				(arrowInsets.top+arrowInsets.bottom);
		final int arrowWidth=topArrowRectangle.width-
				(elementInsets.left+elementInsets.right)-
				(arrowInsets.top+arrowInsets.bottom);
		
		final int xc=topArrowRectangle.x+topArrowRectangle.width/2+arrowInsets.left;
		final int y=topArrowRectangle.y+arrowInsets.top;
		
		paintArrow(g, xc, y, arrowWidth, arrowHeight);
	}
	
	protected void paintElements(Graphics g) {
		final Rectangle clipBounds=g.getClipBounds();
		g.setClip(elementsRectangle);
		int y=elementsRectangle.y;
		final int bottom=elementsRectangle.y+elementsRectangle.height;
		int index=firstVisibleItem;
		final Font font=menu.getFont();
		final FontMetrics fontMetrics=menu.getFontMetrics(font);
		final int ascent=fontMetrics.getMaxAscent();
		final Insets elementBorderInsets=elementBorder.getBorderInsets(menu);
		final int textFieldWidth=elementsRectangle.width-
				(elementBorderInsets.left+elementBorderInsets.right)-
				(elementInsets.left+elementInsets.right);
		
	
		while (y<bottom && index<menu.getListModel().getSize()) {
			final Rectangle elementBounds=new Rectangle(elementsRectangle.x,y,elementSize.width, elementSize.height);
			if (!clipBounds.intersects(elementBounds))
				continue;
			final Object element=menu.getListModel().getElementAt(index);
			final String text=element.toString();
			final int textWidth=fontMetrics.stringWidth(text);
			final int x=elementBounds.x+elementBorderInsets.left+elementInsets.left+(textFieldWidth-textWidth)/2;
			
			g.setColor(menu.getForeground());
			g.drawString(text, x, y+elementBorderInsets.top+elementInsets.top+ascent);
			elementBorder.paintBorder(menu, g, elementsRectangle.x, elementBounds.y, elementBounds.width, elementBounds.height);
			y+=elementSize.height;
			index++;
		}
		g.setClip(clipBounds);
	}
	
	protected void paintBottomArrow(Graphics g) {
		elementBorder.paintBorder(menu, g, bottomArrowRectangle.x, bottomArrowRectangle.y, bottomArrowRectangle.width, bottomArrowRectangle.height);
		final Insets elementInsets=elementBorder.getBorderInsets(menu);
		final int arrowHeight=bottomArrowRectangle.height-
				(elementInsets.top+elementInsets.bottom)-
				(arrowInsets.top+arrowInsets.bottom);
		final int arrowWidth=bottomArrowRectangle.width-
				(elementInsets.left+elementInsets.right)-
				(arrowInsets.top+arrowInsets.bottom);
		
		final int xc=bottomArrowRectangle.x+bottomArrowRectangle.width/2+arrowInsets.left;
		final int y=bottomArrowRectangle.y+arrowHeight+arrowInsets.top;
		
		paintArrow(g, xc, y, arrowWidth, -arrowHeight);
	}
}
