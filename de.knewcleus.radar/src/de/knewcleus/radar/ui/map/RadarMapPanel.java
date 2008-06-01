package de.knewcleus.radar.ui.map;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import javax.swing.JComponent;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.ICoordinateTransformation;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.radar.DisplayElement;
import de.knewcleus.radar.DisplayElementContainer;
import de.knewcleus.radar.WorkObjectSymbol;
import de.knewcleus.radar.ui.rpvd.RadarPlanViewSettings;

public abstract class RadarMapPanel extends JComponent {
	private static final long serialVersionUID = 242911155359395299L;
	
	protected ICoordinateTransformation mapTransformation;
	protected final RadarMapDeviceTransformation deviceTransformation=new RadarMapDeviceTransformation();
	protected final RadarPlanViewSettings settings;
	protected double xRange=30*Units.NM;
	protected final DisplayElementContainer displayElementContainer=new DisplayElementContainer();
	protected DisplayElement activeElement=null;

	public RadarMapPanel(RadarPlanViewSettings settings, ICoordinateTransformation mapTransformation) {
		this.settings=settings;
		this.mapTransformation=mapTransformation;
		displayElementContainer.setDisplayComponent(this);
		setXRange(settings.getRange()*Units.NM);
		setDoubleBuffered(true);
		enableEvents(AWTEvent.COMPONENT_EVENT_MASK|AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}
	
	public void setMapTransformation(ICoordinateTransformation mapTransformation) {
		if (this.mapTransformation.equals(mapTransformation))
			return;
		this.mapTransformation = mapTransformation;
		displayElementContainer.validate();
	}
	
	public ICoordinateTransformation getMapTransformation() {
		return mapTransformation;
	}
	
	public IDeviceTransformation getDeviceTransformation() {
		return deviceTransformation;
	}
	
	public RadarPlanViewSettings getSettings() {
		return settings;
	}
	
	public double getXRange() {
		return xRange;
	}
	
	public void setXRange(double range) {
		xRange = range;
		deviceTransformation.validate();
		displayElementContainer.validate();
		repaint();
	}
	
	@Override
	protected void processComponentEvent(ComponentEvent e) {
		if (e.getID()==ComponentEvent.COMPONENT_RESIZED || e.getID()==ComponentEvent.COMPONENT_SHOWN) {
			deviceTransformation.validate();
			displayElementContainer.validate();
		}
		super.processComponentEvent(e);
	}
	
	@Override
	protected void processMouseMotionEvent(MouseEvent e) {
		if (activeElement==null || !activeElement.isHit(e.getPoint())) {
			/* The active element has precedence,
			 * only if it is not still hit, search for the next-best hit
			 */
			final Deque<DisplayElement> hitElements=new ArrayDeque<DisplayElement>();
			
			getHitObjects(e.getPoint(), hitElements);
			
			if (activeElement instanceof WorkObjectSymbol) {
				((WorkObjectSymbol)activeElement).deactivateSymbol();
			}
			
			if (!hitElements.isEmpty()) {
				activeElement=hitElements.getLast();
				if (activeElement instanceof WorkObjectSymbol) {
					((WorkObjectSymbol)activeElement).activateSymbol();
				}
			}
		}
		if (!e.isConsumed()) {
			super.processMouseMotionEvent(e);
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d=(Graphics2D)g;
		paintMapBackground(g2d);
		paintSymbols(g2d);
	}
	
	protected abstract void paintMapBackground(Graphics2D g);
	
	protected synchronized void paintSymbols(Graphics2D g) {
		displayElementContainer.paint(g);
	}
	
	public synchronized void add(DisplayElement symbol) {
		displayElementContainer.add(symbol);
		symbol.invalidate();
	}
	
	public synchronized void remove(DisplayElement symbol) {
		symbol.invalidate();
		displayElementContainer.remove(symbol);
	}
	
	public void getHitObjects(Point2D position, Collection<DisplayElement> elements) {
		displayElementContainer.getHitObjects(position, elements);
	}

	public void validate() {
		displayElementContainer.validate();
	}

	@Override
	public boolean isOpaque() {
		return true;
	}
	
	protected class RadarMapDeviceTransformation implements IDeviceTransformation {
		protected double centerX, centerY;
		protected double scale;
		
		public void validate() {
			final int width=getWidth();
			final int height=getHeight();
			centerX=width/2;
			centerY=height/2;
			scale=width/xRange;
		}
		
		@Override
		public Position fromDevice(Point2D point) {
			return new Position((point.getX()-centerX)/scale, (centerY-point.getY())/scale, 0.0);
		}

		@Override
		public Position fromDeviceRelative(Point2D dimension) {
			return new Position(dimension.getX()/scale, -dimension.getY()/scale, 0.0);
		}

		@Override
		public Point2D toDevice(Position pos) {
			return new Point2D.Double(centerX+pos.getX()*scale, centerY-pos.getY()*scale);
		}

		@Override
		public Point2D toDeviceRelative(Vector3D dimension) {
			return new Point2D.Double(dimension.getX()*scale, -dimension.getY()*scale);
		}
		
	}
}
