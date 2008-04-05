package de.knewcleus.radar.ui.plaf.refghmi;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.ListModel;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;

import de.knewcleus.radar.ui.ListMenu;
import de.knewcleus.radar.ui.plaf.ListMenuUI;

public class REFGHMIListMenuiUI extends ListMenuUI {
	protected ListMenu menu;
	
	protected final static Border elementBorder=new REFGHMIBorders.EtchedBorder();
	protected final static Insets textMargins=new Insets(2,2,2,2);
	
	protected final static String propertyPrefix="ListMenu";
	
	public REFGHMIListMenuiUI(ListMenu menu) {
	}
	
	public static ListMenuUI createUI(JComponent c) {
		return new REFGHMIListMenuiUI((ListMenu)c);
	}
	
	protected String getPropertyPrefix() {
		return propertyPrefix;
	}
	
	@Override
	public void installUI(JComponent c) {
		menu=(ListMenu)c;
		
		installDefaults(menu);
		installListeners(menu);
	}
	
	protected void installDefaults(ListMenu menu) {
        // load shared instance defaults
        String pp = getPropertyPrefix();

        LookAndFeel.installColorsAndFont(menu, pp + "background",
                                         pp + "foreground", pp + "font");
        LookAndFeel.installBorder(menu, pp + "border");
	}
	
	protected void installListeners(ListMenu menu) {
	}
	
	@Override
	public void uninstallUI(JComponent c) {
		menu=(ListMenu)c;
		
		uninstallListeners(menu);
		
		menu=null;
	}
	
	protected void uninstallListeners(ListMenu menu) {
	}
	
	@Override
	public Dimension getMinimumSize(JComponent c) {
		final ListModel listModel=menu.getModel();
		final int elementCount=(listModel!=null?listModel.getSize():0);
		
		final Dimension elementSize=getElementSize();
		
		return new Dimension(elementSize.width, elementCount*elementSize.height);
	}
	
	@Override
	public Dimension getPreferredSize(JComponent c) {
		return getMinimumSize(c);
	}
	
	protected Dimension getElementSize() {
		final Font font=menu.getFont();
		final FontMetrics fontMetrics=menu.getFontMetrics(font);
		
		// TODO: cache this value
		int maximumTextWidth=0;
		for (int i=0;i<menu.getModel().getSize();i++) {
			Object element=menu.getModel().getElementAt(i);
			String text=element.toString();
			maximumTextWidth=Math.max(maximumTextWidth, fontMetrics.stringWidth(text));
		}
		
		final Insets elementBorderInsets=elementBorder.getBorderInsets(menu);
		final int elementWidth=elementBorderInsets.left+textMargins.left+maximumTextWidth+textMargins.right+elementBorderInsets.right;
		
		return new Dimension(elementWidth, getElementHeight());
	}
	
	protected int getElementHeight() {
		final Font font=menu.getFont();
		final FontMetrics fontMetrics=menu.getFontMetrics(font);
		final int maximumTextHeight=fontMetrics.getMaxAscent()+fontMetrics.getMaxDescent();
		final Insets elementBorderInsets=elementBorder.getBorderInsets(menu);
		final int elementHeight=elementBorderInsets.top+textMargins.top+maximumTextHeight+textMargins.bottom+elementBorderInsets.bottom;

		return elementHeight;
	}
	
	protected int getYForIndex(int index) {
		return index*getElementHeight();
	}
	
	@Override
	public Rectangle getCellBounds(int index0, int index1) {
		menu.validate();
		int y=index0*getElementHeight();
		int h=(index1-index0+1)*getElementHeight();
		return new Rectangle(0, y, menu.getWidth(), h);
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		final int visibleCount=menu.getVisibleListElements();
		final Dimension elementSize=getElementSize();
		return new Dimension(elementSize.width, elementSize.height*visibleCount);
	}

	public void ensureIndexIsVisible(int index) {
		final Rectangle rect=getCellBounds(index, index);
		menu.scrollRectToVisible(rect);
	}
	
	@Override
	public void paint(Graphics g, JComponent c) {
		final Rectangle clipBounds=g.getClipBounds();
		
		g.setColor(c.getBackground());
		g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
		
		final Dimension size=c.getSize();
		final int elementHeight=getElementHeight();
		final Insets elementBorderInsets=elementBorder.getBorderInsets(menu);
		
		final Font font=menu.getFont();
		final FontMetrics fontMetrics=menu.getFontMetrics(font);
		final int ascent=fontMetrics.getMaxAscent();
		
		final int textFieldWidth=size.width-(elementBorderInsets.left+elementBorderInsets.right)-(textMargins.left+textMargins.right);
		
		int firstVisibleElement=clipBounds.y/elementHeight;
		int lastVisibleElement=(clipBounds.y+clipBounds.height)/elementHeight;
		
		lastVisibleElement=Math.min(menu.getModel().getSize()-1,lastVisibleElement);

		int y=firstVisibleElement*elementHeight;
		
		for (int index=firstVisibleElement; index<=lastVisibleElement; index++) {
			final Object element=menu.getModel().getElementAt(index);
			final String text=element.toString();
			final int textWidth=fontMetrics.stringWidth(text);
			final int x=elementBorderInsets.left+textMargins.left+(textFieldWidth-textWidth)/2;
			
			g.setColor(menu.getForeground());
			g.drawString(text, x, y+elementBorderInsets.top+textMargins.top+ascent);
			elementBorder.paintBorder(menu, g, 0, y, size.width, elementHeight);
			y+=elementHeight;
		}
	}
}
