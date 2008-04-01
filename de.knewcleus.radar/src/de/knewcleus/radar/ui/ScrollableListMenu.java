package de.knewcleus.radar.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.ListModel;
import javax.swing.border.Border;

import de.knewcleus.radar.ui.plaf.refghmi.REFGHMIBorders;

/**
 * A ScrollableListMenu presents a menu of items, one of which can be selected by the user, thereby closing the menu.
 * 
 * The list of items is generated from a ListModel, which may return Action objects.
 * 
 * @author ralfg
 *
 */
public class ScrollableListMenu extends JComponent {
	private static final long serialVersionUID = -1480809186587554356L;
	protected ListModel listModel;
	protected int firstVisibleItem;
	
	protected Rectangle contentRectangle;
	protected Dimension elementSize;
	protected Rectangle elementsRectangle;
	protected int visibleElementCount;
	protected Rectangle topArrowRectangle;
	protected Rectangle bottomArrowRectangle;
	
	protected final static Border elementBorder=new REFGHMIBorders.EtchedBorder();
	protected final static int minimumArrowHeight=20;
	protected final static int minimumArrowWidth=40;

	public ScrollableListMenu(ListModel listMenuModel) {
		this.listModel=listMenuModel;
	}

	public void ensureIndexIsVisible(int index) {
		calculateGeometry();
		if (index<0 || listModel.getSize()<=index) {
			/* Ignore invalid index */
			return;
		}
		if (listModel.getSize()<visibleElementCount) {
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
		
		assert(0<=firstVisibleItem && (listModel.getSize()<visibleElementCount || firstVisibleItem+visibleElementCount-1<listModel.getSize()));
		assert(firstVisibleItem<=index && index<firstVisibleItem+visibleElementCount);
		
		repaint();
	}

	public ListModel getListModel() {
		return listModel;
	}

	public void setListModel(ListModel listMenuModel) {
		this.listModel = listMenuModel;
	}
	
	protected void calculateGeometry() {
		// TODO: cache these values
		calculateContentRectangle();
		calculateElementSize();
		calculateElementsRectangle();
		
		/* This is the count of fully visible entries */
		visibleElementCount=elementsRectangle.height/elementSize.height;
		
		System.out.println("visibleElementCount="+visibleElementCount);
		
		calculateArrowRectangles();
	}
	
	protected void calculateContentRectangle() {
		final Insets insets=getInsets();
		final Dimension size=getSize();
		final int cw,ch;
		
		cw=size.width-(insets.left+insets.right);
		ch=size.height-(insets.top+insets.bottom);
		
		contentRectangle=new Rectangle(insets.left,insets.top,cw,ch);
		
		System.out.println("contentRectangle="+contentRectangle);
	}
	
	protected void calculateElementSize() {
		final int elementHeight=getMinimumElementHeight();
		
		elementSize=new Dimension(contentRectangle.width, elementHeight);
		
		System.out.println("elementSize="+elementSize);
	}
	
	protected void calculateElementsRectangle() {
		/* The arrows are one element high */
		final int elementsHeight=contentRectangle.height-2*elementSize.height;
		
		elementsRectangle=new Rectangle(contentRectangle.x, contentRectangle.y+elementSize.height, contentRectangle.width, elementsHeight);
		
		System.out.println("elementsRectangle="+elementsRectangle);
	}
	
	protected void calculateArrowRectangles() {
		topArrowRectangle=new Rectangle(contentRectangle.x, contentRectangle.y, elementSize.width, elementSize.height);
		bottomArrowRectangle=new Rectangle(contentRectangle.x, contentRectangle.y+contentRectangle.height-elementSize.height,
										   elementSize.width, elementSize.height);
	}
	
	protected int getMaximumTextWidth() {
		// TODO: cache this value
		final Font font=getFont();
		final FontMetrics fontMetrics=getFontMetrics(font);
		
		int maximumTextWidth=0;
		for (int i=0;i<listModel.getSize();i++) {
			Object element=listModel.getElementAt(i);
			String text=element.toString();
			maximumTextWidth=Math.max(maximumTextWidth, fontMetrics.stringWidth(text));
		}
		
		return maximumTextWidth;
	}
	
	protected int getMaximumTextHeight() {
		final Font font=getFont();
		final FontMetrics fontMetrics=getFontMetrics(font);
		
		return fontMetrics.getMaxAscent()+fontMetrics.getMaxDescent();
	}
	
	
	protected int getMinimumElementHeight() {
		final Insets elementInsets=elementBorder.getBorderInsets(this);
		
		final int textHeight=getMaximumTextHeight();
		final int elementHeight=Math.max(minimumArrowHeight, textHeight)+elementInsets.top+elementInsets.bottom;
		
		return elementHeight;
	}

	protected int getMinimumElementWidth() {
		final Insets elementInsets=elementBorder.getBorderInsets(this);
		
		return getMaximumTextWidth()+elementInsets.left+elementInsets.right;
	}
	
	@Override
	public Dimension getMinimumSize() {
		Insets insets=getInsets();
		/* We want to show at least one entry and the arrows */
		final int minimumHeight=insets.top+3*getMinimumElementHeight()+insets.bottom;
		final int minimumWidth=insets.left+getMinimumElementWidth()+insets.right;
		
		return new Dimension(minimumWidth, minimumHeight);
	}
	
	@Override
	public Dimension getPreferredSize() {
		Insets insets=getInsets();
		
		final int preferredVisibleElementCount=Math.min(listModel.getSize(), 9);
		final int preferredHeight=insets.top+(2+preferredVisibleElementCount)*getMinimumElementHeight()+insets.bottom;
		final int preferredWidth=insets.left+getMinimumElementWidth()+insets.right;
		
		return new Dimension(preferredWidth, preferredHeight);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		calculateGeometry();
		final Rectangle clipBounds=g.getClipBounds();
		
		/* Paint the background */
		g.setColor(getBackground());
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
	
	protected void paintTopArrow(Graphics g) {
		// TODO
	}
	
	protected void paintElements(Graphics g) {
		int y=elementsRectangle.y;
		final int bottom=elementsRectangle.y+elementsRectangle.height;
		int index=firstVisibleItem;
		final Font font=getFont();
		final FontMetrics fontMetrics=getFontMetrics(font);
		final int ascent=fontMetrics.getMaxAscent();
		final Insets elementInsets=elementBorder.getBorderInsets(this);
		final int textFieldWidth=elementsRectangle.width-(elementInsets.left+elementInsets.right);
		
	
		while (y<bottom && index<listModel.getSize()) {
			final Object element=listModel.getElementAt(index);
			final String text=element.toString();
			final int textWidth=fontMetrics.stringWidth(text);
			final int x=elementsRectangle.x+elementInsets.left+(textFieldWidth-textWidth)/2;
			
			g.drawString(text, x, y+ascent);
			elementBorder.paintBorder(this, g, elementsRectangle.x, y, elementsRectangle.width, elementSize.height);
			y+=elementSize.height;
			index++;
		}
	}
	
	protected void paintBottomArrow(Graphics g) {
		// TODO
	}
}
