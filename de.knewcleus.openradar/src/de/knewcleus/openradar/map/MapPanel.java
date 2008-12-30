package de.knewcleus.openradar.map;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;

public class MapPanel extends JComponent implements INotificationListener {
	private static final long serialVersionUID = -3173711704273558768L;
	
	protected final IMap map;

	public MapPanel(IMap map) {
		this.map = map;
		setDoubleBuffered(true);
		setBackground(Color.BLACK);
		map.registerListener(this);
		enableEvents(AWTEvent.MOUSE_WHEEL_EVENT_MASK);
	}
	
	public IMap getMap() {
		return map;
	}
	
	@Override
	public void acceptNotification(INotification notification) {
		if (notification instanceof ViewNotification) {
			IView source = ((ViewNotification)notification).getSource();
			if (source instanceof IBoundedView) {
				final Rectangle2D extents = ((IBoundedView)source).getDisplayExtents();
				repaint(extents.getBounds());
			} else {
				repaint();
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final Graphics2D g2d = (Graphics2D)g;
		if (g2d.getClip()==null) {
			final Dimension dimension=getSize();
			g2d.clipRect(0, 0, dimension.width, dimension.height);
		}
		ViewPaintVisitor viewPaintVisitor=new ViewPaintVisitor(g2d);
		map.accept(viewPaintVisitor);
	}
	
	@Override
	protected void processMouseWheelEvent(MouseWheelEvent e) {
		double scale=map.getLogicalScale();
		scale*=Math.pow(1.1, e.getWheelRotation());
		map.setLogicalScale(scale);
		super.processMouseWheelEvent(e);
	}
}