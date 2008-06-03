package de.knewcleus.openradar.ui.vehicles;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import de.knewcleus.openradar.ui.Palette;

public class PopupComponent extends JComponent {
	private static final long serialVersionUID = -4642648532599545660L;
	protected static final PopupComponentBorder border=new PopupComponentBorder();
	
	protected String title;
	protected Popup popup;

	public PopupComponent(String title) {
		this.title=title;
		setBorder(border);
		setFont(Palette.BEACON_FONT);
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public Dimension getPreferredSize() {
		Dimension d=super.getPreferredSize();
		final Insets insets=getInsets();
		final Insets borderInsets=getBorder().getBorderInsets(this);
		
		final Font font=getFont();
		final FontMetrics fm=getFontMetrics(font);
		
		int titleWidth=fm.stringWidth(getTitle());
		int titleBarWidth=titleWidth;
		
		if (insets!=null) {
			titleBarWidth+=insets.left+insets.right;
		}
		
		if (borderInsets!=null) {
			titleBarWidth+=borderInsets.left+borderInsets.right;
		}
		
		if (titleBarWidth>d.width) {
			d.width=titleBarWidth;
		}
		
		return d;
	}
	
	@Override
	public Dimension getMinimumSize() {
		Dimension d=super.getMinimumSize();
		final Insets insets=getInsets();
		final Insets borderInsets=getBorder().getBorderInsets(this);
		
		final Font font=getFont();
		final FontMetrics fm=getFontMetrics(font);
		
		int titleWidth=fm.stringWidth(getTitle());
		int titleBarWidth=titleWidth;
		
		if (insets!=null) {
			titleBarWidth+=insets.left+insets.right;
		}
		
		if (borderInsets!=null) {
			titleBarWidth+=borderInsets.left+borderInsets.right;
		}
		
		if (titleBarWidth>d.width) {
			d.width=titleBarWidth;
		}
		
		return d;
	}
	
	public void show(Component invoker, int x, int y) {
		assert(popup==null);
		final PopupFactory popupFactory=PopupFactory.getSharedInstance();
		
		final Point invokerOnScreen=invoker.getLocationOnScreen();
		x+=invokerOnScreen.x;
		y+=invokerOnScreen.y;
		setSize(getPreferredSize());
		popup=popupFactory.getPopup(invoker, this, x, y);
		handler.install(invoker);
		popup.show();
	}
	
	public void cancel() {
		assert(popup!=null);
		handler.uninstall();
		popup.hide();
		popup=null;
	}
	
	protected Handler handler=new Handler();
	
	protected class Handler implements WindowListener, ComponentListener, AWTEventListener {
		protected Window grabbedWindow;

		public void install(Component invoker) {
			Component parent=invoker;
			
			while (parent!=null && !(parent instanceof Window)) {
				parent=parent.getParent();
			}
			
			grabbedWindow=(Window)parent;
			
			if (grabbedWindow!=null) {
				grabbedWindow.addWindowListener(this);
				grabbedWindow.addComponentListener(this);
			}
			
			final Toolkit toolkit=Toolkit.getDefaultToolkit();
			toolkit.addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK);
		}
		
		public void uninstall() {
			if (grabbedWindow!=null) {
				grabbedWindow.removeComponentListener(this);
				grabbedWindow.removeWindowListener(this);
				grabbedWindow=null;
			}
			
			final Toolkit toolkit=Toolkit.getDefaultToolkit();
			toolkit.removeAWTEventListener(this);
		}
		
		protected boolean isInPopup(Component comp) {
			while (comp!=null) {
				if (comp instanceof PopupComponent)
					return true;
				comp=comp.getParent();
			}
			return false;
		}
		
		protected void cancelPopup() {
			uninstall();
			popup.hide();
			popup=null;
		}

		@Override
		public void eventDispatched(AWTEvent event) {
			switch (event.getID()) {
			case MouseEvent.MOUSE_PRESSED: {
				final MouseEvent me=(MouseEvent)event;
				final Component src=(Component)event.getSource();
				/*
				 * REFGHMI cancels popups with B3 as well.
				 */
				if (me.getButton()==MouseEvent.BUTTON3 || !isInPopup(src)) {
					cancelPopup();
					break;
				}
				break;
			}
			}
		}

		@Override
		public void windowClosed(WindowEvent e) {
			cancelPopup();
		}

		@Override
		public void windowClosing(WindowEvent e) {
			cancelPopup();
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			cancelPopup();
		}

		@Override
		public void windowIconified(WindowEvent e) {
			cancelPopup();
		}

		@Override
		public void componentHidden(ComponentEvent e) {
			cancelPopup();
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			cancelPopup();
		}

		@Override
		public void componentResized(ComponentEvent e) {
			cancelPopup();
		}

		@Override
		public void componentShown(ComponentEvent e) {
			cancelPopup();
		}
		
		@Override
		public void windowActivated(WindowEvent e) {}

		@Override
		public void windowDeiconified(WindowEvent e) {}

		@Override
		public void windowOpened(WindowEvent e) {}
	}
}
