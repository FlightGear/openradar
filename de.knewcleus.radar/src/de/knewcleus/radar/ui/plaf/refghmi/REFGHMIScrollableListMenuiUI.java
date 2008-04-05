package de.knewcleus.radar.ui.plaf.refghmi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.knewcleus.radar.ui.ScrollableListMenu;
import de.knewcleus.radar.ui.plaf.ScrollableListMenuUI;

public class REFGHMIScrollableListMenuiUI extends ScrollableListMenuUI {
	protected ScrollableListMenu menu;
	
	protected REFGHMIArrowButton decreaseButton=new REFGHMIArrowButton(SwingConstants.NORTH);
	protected JViewport viewport=new JViewport();
	protected ListMenuView view=new ListMenuView();
	protected REFGHMIArrowButton increaseButton=new REFGHMIArrowButton(SwingConstants.SOUTH);
	
	protected final ListMenuLayoutManager layoutManager=new ListMenuLayoutManager();
	protected final Handler handler=new Handler();
	
	protected final DefaultBoundedRangeModel boundedRangeModel=new DefaultBoundedRangeModel();
	
	protected final static Color arrowColor=Color.WHITE;
	
	protected final static Border border=new REFGHMIBorders.EtchedBorder();
	protected final static Border elementBorder=new REFGHMIBorders.EtchedBorder();
	protected final static Insets arrowInsets=new Insets(2,2,2,2);
	protected final static Insets textMargins=new Insets(2,2,2,2);
	protected final static int minimumArrowHeight=20;
	protected final static int minimumArrowWidth=40;
	
	protected final static String propertyPrefix="ScrollableListMenu";
	
	public REFGHMIScrollableListMenuiUI(ScrollableListMenu menu) {
		decreaseButton.setForeground(arrowColor);
		increaseButton.setForeground(arrowColor);
		viewport.setView(view);
		
		decreaseButton.addActionListener(handler);
		increaseButton.addActionListener(handler);
	}
	
	public static ScrollableListMenuUI createUI(JComponent c) {
		return new REFGHMIScrollableListMenuiUI((ScrollableListMenu)c);
	}
	
	protected String getPropertyPrefix() {
		return propertyPrefix;
	}
	
	@Override
	public void installUI(JComponent c) {
		menu=(ScrollableListMenu)c;
		
		view.invalidate();
		menu.add(decreaseButton);
		menu.add(viewport);
		menu.add(increaseButton);
		
		installDefaults(menu);
		installListeners(menu);
	}
	
	protected void installDefaults(ScrollableListMenu menu) {
        // load shared instance defaults
        String pp = getPropertyPrefix();

        LookAndFeel.installColorsAndFont(menu, pp + "background",
                                         pp + "foreground", pp + "font");
        LookAndFeel.installBorder(menu, pp + "border");
	}
	
	protected void installListeners(ScrollableListMenu menu) {
		menu.setLayout(layoutManager);
		viewport.addChangeListener(handler);
		view.addMouseMotionListener(handler);
		menu.addMouseWheelListener(handler);
	}
	
	@Override
	public void uninstallUI(JComponent c) {
		menu=(ScrollableListMenu)c;
		menu.remove(decreaseButton);
		menu.remove(viewport);
		menu.remove(increaseButton);
		
		uninstallListeners(menu);
		
		menu=null;
	}
	
	protected void uninstallListeners(ScrollableListMenu menu) {
		menu.setLayout(null);
		viewport.removeChangeListener(handler);
		view.removeMouseMotionListener(handler);
		menu.removeMouseWheelListener(handler);
	}
	
	protected Dimension getElementSize() {
		final Font font=menu.getFont();
		final FontMetrics fontMetrics=menu.getFontMetrics(font);
		
		// TODO: cache this value
		int maximumTextWidth=0;
		for (int i=0;i<menu.getListModel().getSize();i++) {
			Object element=menu.getListModel().getElementAt(i);
			String text=element.toString();
			maximumTextWidth=Math.max(maximumTextWidth, fontMetrics.stringWidth(text));
		}
		
		final Insets elementBorderInsets=elementBorder.getBorderInsets(menu);
		final int elementWidth=elementBorderInsets.left+textMargins.left+maximumTextWidth+textMargins.right+elementBorderInsets.right;
		final Dimension decreaseButtonSize=decreaseButton.getMinimumSize();
		final Dimension increaseButtonSize=increaseButton.getMinimumSize();
		final int arrowWidth=Math.max(decreaseButtonSize.width, increaseButtonSize.width);
		
		return new Dimension(Math.max(arrowWidth, elementWidth), getElementHeight());
	}
	
	protected int getElementHeight() {
		final Font font=menu.getFont();
		final FontMetrics fontMetrics=menu.getFontMetrics(font);
		final int maximumTextHeight=fontMetrics.getMaxAscent()+fontMetrics.getMaxDescent();
		final Insets elementBorderInsets=elementBorder.getBorderInsets(menu);
		final int elementHeight=elementBorderInsets.top+textMargins.top+maximumTextHeight+textMargins.bottom+elementBorderInsets.bottom;
		final Dimension decreaseButtonSize=decreaseButton.getMinimumSize();
		final Dimension increaseButtonSize=increaseButton.getMinimumSize();
		final int arrowHeight=Math.max(decreaseButtonSize.height, increaseButtonSize.height);

		return Math.max(arrowHeight,elementHeight);
	}
	
	protected int getYForIndex(int index) {
		return index*getElementHeight();
	}
	
	@Override
	public Rectangle getCellBounds(int index0, int index1) {
		final Dimension size=viewport.getViewSize();
		int y=index0*getElementHeight();
		int h=(index1-index0+1)*getElementHeight();
		return new Rectangle(0, y, size.width, h);
	}

	public void ensureIndexIsVisible(int index) {
		final Rectangle rect=getCellBounds(index, index);
		menu.scrollRectToVisible(rect);
	}
	
	protected class Handler implements ChangeListener, ActionListener, MouseMotionListener, MouseWheelListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==decreaseButton || e.getSource()==increaseButton) {
				final int adjustment;
				if (e.getSource()==decreaseButton) {
					adjustment=-getElementHeight();
				} else {
					adjustment=getElementHeight();
				}
				boundedRangeModel.setValue(boundedRangeModel.getValue()+adjustment);
				
				System.out.println("scrolling "+boundedRangeModel+" adj="+adjustment);
				
				viewport.setViewPosition(new Point(0, boundedRangeModel.getValue()));
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
		}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource()==viewport) {
				final Dimension viewSize=view.getSize();
				final Rectangle viewRect=viewport.getViewRect();
				
				boundedRangeModel.setMinimum(0);
				boundedRangeModel.setMaximum(viewSize.height);
				boundedRangeModel.setExtent(viewRect.height);
				boundedRangeModel.setValue(viewRect.y);
				
				System.out.println("viewport state changed "+boundedRangeModel);
				// TODO: update button state
			}
		}
	}
	
	protected class ListMenuLayoutManager implements LayoutManager {
		@Override
		public void addLayoutComponent(String name, Component comp) {}
		@Override
		public void removeLayoutComponent(Component comp) {}
		
		@Override
		public Dimension minimumLayoutSize(Container parent) {
			final Dimension decreaseButtonSize=decreaseButton.getMinimumSize();
			final Dimension viewportSize=viewport.getMinimumSize();
			final Dimension increaseButtonSize=increaseButton.getMinimumSize();
			
			/* We ensure that buttons and elements are equally high */
			final int elementHeight=getElementHeight();
			
			final int minimumWidth=Math.max(
					Math.max(decreaseButtonSize.width, increaseButtonSize.width),
					viewportSize.width);
			
			final Insets insets=menu.getInsets();
			final int height,width;
			
			width=insets.left+minimumWidth+insets.right;
			height=insets.top+elementHeight+viewportSize.height+elementHeight+insets.bottom;
			
			return new Dimension(width, height);
		}
		
		@Override
		public Dimension preferredLayoutSize(Container parent) {
			final Dimension decreaseButtonSize=decreaseButton.getPreferredSize();
			final Dimension viewportSize=viewport.getPreferredSize();
			final Dimension increaseButtonSize=increaseButton.getPreferredSize();
			
			/* We ensure that buttons and elements are equally high */
			final int elementHeight=getElementHeight();
			
			final int minimumWidth=Math.max(
					Math.max(decreaseButtonSize.width, increaseButtonSize.width),
					viewportSize.width);
			
			final Insets insets=menu.getInsets();
			final int height,width;
			
			width=insets.left+minimumWidth+insets.right;
			height=insets.top+elementHeight+viewportSize.height+elementHeight+insets.bottom;
			
			return new Dimension(width, height);
		}
		
		@Override
		public void layoutContainer(Container parent) {
			final Insets insets=menu.getInsets();
			final Dimension size=parent.getSize();
			final int elementHeight=getElementHeight();
			
			final int x,y,w,h;
			
			x=insets.left;
			y=insets.top;
			w=size.width-(insets.left+insets.right);
			h=size.height-(insets.top+insets.bottom);
			
			decreaseButton.setBounds(x, y                     , w, elementHeight);
			increaseButton.setBounds(x, y+h-elementHeight, w, elementHeight);
			viewport.setBounds(x, y+elementHeight, w, h-2*elementHeight);
		}
	}
	
	protected class ListMenuView extends JComponent {
		private static final long serialVersionUID = -3953303234927585156L;
		
		@Override
		public void paintComponent(Graphics g) {
			final Rectangle clipBounds=g.getClipBounds();
			final Dimension size=getSize();
			final int elementHeight=getElementHeight();
			final Font font=menu.getFont();
			final FontMetrics fontMetrics=menu.getFontMetrics(font);
			final Insets elementBorderInsets=elementBorder.getBorderInsets(menu);
			final int textFieldWidth=size.width-(elementBorderInsets.left+elementBorderInsets.right)-(textMargins.left+textMargins.right);
			final int ascent=fontMetrics.getMaxAscent();
			
			int firstVisibleElement=clipBounds.y/elementHeight;
			int lastVisibleElement=(clipBounds.y+clipBounds.height)/elementHeight;
			
			lastVisibleElement=Math.min(menu.getListModel().getSize()-1,lastVisibleElement);

			int y=firstVisibleElement*elementHeight;
			
			for (int index=firstVisibleElement; index<=lastVisibleElement; index++) {
				final Object element=menu.getListModel().getElementAt(index);
				final String text=element.toString();
				final int textWidth=fontMetrics.stringWidth(text);
				final int x=elementBorderInsets.left+textMargins.left+(textFieldWidth-textWidth)/2;
				
				g.setColor(menu.getForeground());
				g.drawString(text, x, y+elementBorderInsets.top+textMargins.top+ascent);
				elementBorder.paintBorder(menu, g, 0, y, size.width, elementHeight);
				y+=elementHeight;
			}
		}
		
		@Override
		public Dimension getMinimumSize() {
			final Dimension elementSize=getElementSize();
			return new Dimension(elementSize.width, elementSize.height);
		}
		
		@Override
		public Dimension getPreferredSize() {
			final Dimension elementSize=getElementSize();
			return new Dimension(elementSize.width, menu.getListModel().getSize()*elementSize.height);
		}
	}
}
