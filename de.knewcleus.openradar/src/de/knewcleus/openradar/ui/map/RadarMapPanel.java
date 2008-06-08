package de.knewcleus.openradar.ui.map;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import de.knewcleus.fgfs.location.IMapProjection;
import de.knewcleus.openradar.ui.core.DisplayElement;
import de.knewcleus.openradar.ui.core.DisplayElementContainer;
import de.knewcleus.openradar.ui.core.SymbolActivationManager;
import de.knewcleus.openradar.ui.rpvd.RadarPlanViewSettings;

public abstract class RadarMapPanel extends JComponent implements Scrollable {
	private static final long serialVersionUID = 242911155359395299L;
	
	protected IMapProjection projection;
	protected final RadarPlanViewSettings settings;
	protected double scale=1.0;
	protected final DisplayElementContainer displayElementContainer=new DisplayElementContainer();
	protected final SymbolActivationManager symbolFocusManager=new SymbolActivationManager(displayElementContainer);

	protected final List<IMapLayer> mapLayers=new Vector<IMapLayer>();

	public RadarMapPanel(RadarPlanViewSettings settings, IMapProjection mapTransformation) {
		this.settings=settings;
		this.projection=mapTransformation;
		displayElementContainer.setDisplayComponent(this);
		displayElementContainer.setSymbolActivationManager(symbolFocusManager);
		setDoubleBuffered(true);
		enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
		addMouseListener(symbolFocusManager);
		addMouseMotionListener(symbolFocusManager);
	}
	
	public void setProjection(IMapProjection projection) {
		if (this.projection.equals(projection))
			return;
		this.projection = projection;
		displayElementContainer.validate();
		repaint();
	}
	
	public IMapProjection getProjection() {
		return projection;
	}
	
	public RadarPlanViewSettings getSettings() {
		return settings;
	}
	
	public void setScale(double scale) {
		this.scale = scale;
		displayElementContainer.validate();
		revalidate();
		repaint();
	}
	
	public double getScale() {
		return scale;
	}
	
	@Override
	protected void processComponentEvent(ComponentEvent e) {
		if (e.getID()==ComponentEvent.COMPONENT_RESIZED || e.getID()==ComponentEvent.COMPONENT_SHOWN) {
			displayElementContainer.validate();
			repaint();
		}
		super.processComponentEvent(e);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d=(Graphics2D)g;
		paintMapBackground(g2d);
		paintSymbols(g2d);
	}
	
	public AffineTransform getMapTransformation() {
		final AffineTransform scaleXForm=AffineTransform.getScaleInstance(scale, -scale);
		final AffineTransform translateXForm=AffineTransform.getTranslateInstance(getWidth()/2.0, getHeight()/2.0);
		translateXForm.concatenate(scaleXForm);
		return translateXForm;
	}
	
	protected void paintMapBackground(Graphics2D g2d) {
		final AffineTransform mapTransformation=getMapTransformation();
		for (IMapLayer layer: getMapLayers()) {
			layer.draw(g2d, mapTransformation, getProjection());
		}
	}
	
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

	@Override
	public boolean isOpaque() {
		return true;
	}
	
	public void add(IMapLayer layer) {
		mapLayers.add(layer);
	}
	
	public void add(IMapLayer layer, int index) {
		mapLayers.add(index, layer);
	}
	
	public void remove(IMapLayer layer) {
		mapLayers.remove(layer);
	}
	
	public List<IMapLayer> getMapLayers() {
		return mapLayers;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (orientation==SwingConstants.VERTICAL) {
			return visibleRect.height;
		} else {
			return visibleRect.width;
		}
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 1;
	}
}
