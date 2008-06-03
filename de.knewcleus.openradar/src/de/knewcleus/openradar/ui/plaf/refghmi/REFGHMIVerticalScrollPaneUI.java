package de.knewcleus.openradar.ui.plaf.refghmi;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.knewcleus.openradar.ui.VerticalScrollPane;
import de.knewcleus.openradar.ui.plaf.VerticalScrollPaneUI;

public class REFGHMIVerticalScrollPaneUI extends VerticalScrollPaneUI {
	protected VerticalScrollPane scrollPane;
	
	protected REFGHMIArrowButton decreaseButton=new REFGHMIArrowButton(SwingConstants.NORTH);
	protected REFGHMIArrowButton increaseButton=new REFGHMIArrowButton(SwingConstants.SOUTH);
	
	protected final Handler handler=new Handler();
	
	protected final DefaultBoundedRangeModel boundedRangeModel=new DefaultBoundedRangeModel();
	
	public REFGHMIVerticalScrollPaneUI(VerticalScrollPane scrollPane) {
		decreaseButton.addActionListener(handler);
		increaseButton.addActionListener(handler);
	}
	
	public static VerticalScrollPaneUI createUI(JComponent c) {
		return new REFGHMIVerticalScrollPaneUI((VerticalScrollPane)c);
	}

	@Override
	public void installUI(JComponent c) {
		scrollPane=(VerticalScrollPane)c;
		
		scrollPane.setLayout(handler);
		scrollPane.add(decreaseButton);
		scrollPane.add(scrollPane.getViewport());
		scrollPane.add(increaseButton);
		
		installDefaults(scrollPane);
		installListeners(scrollPane);
	}
	
	protected void installDefaults(VerticalScrollPane scrollPane) {
        // load shared instance defaults
        String pp = getPropertyPrefix();

        LookAndFeel.installColorsAndFont(scrollPane, pp + "background",
                                         pp + "foreground", pp + "font");
        LookAndFeel.installBorder(scrollPane, pp + "border");
	}
	
	protected void installListeners(VerticalScrollPane scrollPane) {
		final JViewport viewport=scrollPane.getViewport();
		viewport.addChangeListener(handler);
		scrollPane.addMouseWheelListener(handler);
	}
	
	@Override
	public void uninstallUI(JComponent c) {
		scrollPane=(VerticalScrollPane)c;
		
		scrollPane.remove(decreaseButton);
		scrollPane.remove(scrollPane.getViewport());
		scrollPane.remove(increaseButton);
		uninstallListeners(scrollPane);
		
		scrollPane=null;
	}
	
	protected void uninstallListeners(VerticalScrollPane scrollPane) {
		scrollPane.setLayout(null);
		scrollPane.getViewport().removeChangeListener(handler);
		scrollPane.removeMouseWheelListener(handler);
	}
	
	protected void scroll(int steps) {
		final int unitIncrement=scrollPane.getUnitIncrement(steps);
		
		boundedRangeModel.setValue(boundedRangeModel.getValue()+steps*unitIncrement);
		final JViewport viewport=scrollPane.getViewport();
		final Point viewPosition=viewport.getViewPosition(); 
		viewPosition.y=boundedRangeModel.getValue();
		viewport.setViewPosition(viewPosition);
	}
	
	protected class Handler implements ActionListener, ChangeListener, LayoutManager, MouseWheelListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource()==decreaseButton) {
				scroll(-1);
			} else if (e.getSource()==increaseButton) {
				scroll(1);
			}
		}
		
		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource()==scrollPane.getViewport()) {
				final JViewport viewport=scrollPane.getViewport();
				final Dimension viewSize=viewport.getViewSize();
				final Rectangle viewRect=viewport.getViewRect();
				
				boundedRangeModel.setMinimum(0);
				boundedRangeModel.setMaximum(viewSize.height);
				boundedRangeModel.setExtent(viewRect.height);
				boundedRangeModel.setValue(viewRect.y);
				
				decreaseButton.setEnabled(boundedRangeModel.getValue()>boundedRangeModel.getMinimum());
				increaseButton.setEnabled(boundedRangeModel.getValue()<boundedRangeModel.getMaximum());
			}
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			scroll(e.getWheelRotation());
		}
		
		@Override
		public void addLayoutComponent(String name, Component comp) {}
		
		@Override
		public void removeLayoutComponent(Component comp) {}
		
		@Override
		public Dimension minimumLayoutSize(Container parent) {
			final Dimension decreaseButtonSize=decreaseButton.getPreferredSize();
			final Dimension viewportSize=scrollPane.getViewport().getMinimumSize();
			final Dimension increaseButtonSize=increaseButton.getPreferredSize();
			
			final int height=decreaseButtonSize.height+viewportSize.height+increaseButtonSize.height;
			final int width=Math.max(Math.max(decreaseButtonSize.width, increaseButtonSize.width), viewportSize.width);
			
			final Insets insets=parent.getInsets();
			
			return new Dimension(insets.left+width+insets.right, insets.top+height+insets.bottom);
		}
		
		@Override
		public Dimension preferredLayoutSize(Container parent) {
			final Dimension decreaseButtonSize=decreaseButton.getPreferredSize();
			final Dimension viewportSize=scrollPane.getViewport().getPreferredSize();
			final Dimension increaseButtonSize=increaseButton.getPreferredSize();
			
			final int height=decreaseButtonSize.height+viewportSize.height+increaseButtonSize.height;
			final int width=Math.max(Math.max(decreaseButtonSize.width, increaseButtonSize.width), viewportSize.width);
			
			final Insets insets=parent.getInsets();
			
			return new Dimension(insets.left+width+insets.right, insets.top+height+insets.bottom);
		}
		
		@Override
		public void layoutContainer(Container parent) {
			final Dimension size=parent.getSize();
			final Dimension decreaseButtonSize=decreaseButton.getPreferredSize();
			final Dimension increaseButtonSize=increaseButton.getPreferredSize();
			final Insets insets=parent.getInsets();
			
			final int viewportHeight=size.height-decreaseButtonSize.height-increaseButtonSize.height-(insets.top+insets.bottom);
			
			decreaseButton.setBounds(insets.left, insets.top, size.width, decreaseButtonSize.height);
			scrollPane.getViewport().setBounds(insets.left, insets.top+decreaseButtonSize.height, size.width, viewportHeight);
			increaseButton.setBounds(insets.left, size.height-insets.bottom-increaseButtonSize.height, size.width, increaseButtonSize.height);
		}
	}
}
