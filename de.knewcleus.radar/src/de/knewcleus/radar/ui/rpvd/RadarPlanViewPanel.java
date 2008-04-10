package de.knewcleus.radar.ui.rpvd;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.CoordinateDeviceTransformation;
import de.knewcleus.fgfs.location.ICoordinateTransformation;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.Aerodrome;
import de.knewcleus.fgfs.navaids.Runway;
import de.knewcleus.radar.autolabel.ChargePotentialAutolabeller;
import de.knewcleus.radar.autolabel.ILabelPotentialGradientCalculator;
import de.knewcleus.radar.sector.Sector;
import de.knewcleus.radar.ui.Palette;
import de.knewcleus.radar.ui.RadarWorkstation;
import de.knewcleus.radar.ui.vehicles.Aircraft;
import de.knewcleus.radar.ui.vehicles.IVehicle;
import de.knewcleus.radar.ui.vehicles.IVehicleSelectionListener;
import de.knewcleus.radar.ui.vehicles.IVehicleUpdateListener;

public class RadarPlanViewPanel extends JPanel implements IVehicleUpdateListener, IVehicleSelectionListener, PropertyChangeListener {
	protected final static Logger logger=Logger.getLogger(RadarPlanViewPanel.class.getName());
	private static final long serialVersionUID = 8996959818592227638L;
	
	protected final RadarWorkstation workstation;
	protected final RadarDeviceTransformation radarDeviceTransformation;
	protected final RadarPlanViewContext radarPlanViewContext;
	
	protected final Font font=new Font(Font.SANS_SERIF,Font.PLAIN,12);
	protected final ILabelPotentialGradientCalculator labelPotentialGradientCalculator=new VehicleLabelPotentialGradientCalculator();
	protected final ChargePotentialAutolabeller autolabeller;

	protected final IMapLayer landmassLayer;
	protected final IMapLayer waterLayer;
	protected final IMapLayer restrictedLayer;
	protected final IMapLayer sectorLayer;
	protected final WaypointDisplayLayer waypointDisplayLayer;
	protected final RangeMarkLayer rangeMarkLayer=new RangeMarkLayer();
	
	protected final float scaleMarkerSize=10.0f;
	protected final float scaleMarkerDistance=10.0f*(float)Units.NM;
	protected final float centrelineLength=5.0f*(float)Units.NM;
	
	protected Map<IVehicle, IVehicleSymbol> vehicleSymbolMap=new HashMap<IVehicle, IVehicleSymbol>();
	protected double dragHookX, dragHookY;
	
	protected static int selectionRange=10;
	
	protected IVehicleSymbol activeVehicleSymbol=null;
	
	public RadarPlanViewPanel(RadarWorkstation workstation) {
		super();
		setDoubleBuffered(true); /* Is double buffered */
		this.workstation=workstation;
		
		autolabeller=new ChargePotentialAutolabeller(labelPotentialGradientCalculator, 1E-1,3E-1);
		
		workstation.getRadarPlanViewSettings().addPropertyChangeListener(this);
		
		radarDeviceTransformation=new RadarDeviceTransformation(getSettings());
		radarPlanViewContext=new RadarPlanViewContext(this, getSettings(),radarDeviceTransformation);
		
		final Sector sector=workstation.getSector();
		landmassLayer=new PolygonMapLayer(Palette.LANDMASS,sector.getLandmassPolygons());
		waterLayer=new PolygonMapLayer(Palette.WATERMASS,sector.getWaterPolygons());
		restrictedLayer=new PolygonMapLayer(Palette.RESTRICTED,sector.getRestrictedPolygons());
		sectorLayer=new PolygonMapLayer(Palette.SECTOR,sector.getSectorPolygons());
		waypointDisplayLayer=new WaypointDisplayLayer(sector);
		
		Set<Aerodrome> aerodromes=sector.getFixDB().getFixes(Aerodrome.class);
		waypointDisplayLayer.getFixesWithDesignator().addAll(aerodromes);

		setBackground(Palette.WATERMASS);
		setFont(font);

		/*
		 * We just register as a state consumer here. With the next update cycle we will collect all the
		 * vehicle states.
		 */
		workstation.getVehicleManager().registerVehicleUpdateListener(this);
		
		workstation.getVehicleSelectionManager().registerVehicleSelectionListener(this);
		
		enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK|AWTEvent.MOUSE_EVENT_MASK|AWTEvent.COMPONENT_EVENT_MASK);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getOldValue().equals(evt.getNewValue())) {
			return;
		}
		
		if (evt.getSource()==getSettings()) {
			String propertyName=evt.getPropertyName();
			final RadarPlanViewSettings settings=getSettings();
			
			if (!propertyName.equals(RadarPlanViewSettings.IS_AUTOMATIC_LABELING_ENABLED_PROPERTY) &&
					!propertyName.equals(RadarPlanViewSettings.STANDARD_LABEL_POSITION_PROPERTY) &&
					!propertyName.equals(RadarPlanViewSettings.STANDARD_LABEL_DISTANCE_PROPERTY))
			{
				return;
			}
			
			if (propertyName.equals(RadarPlanViewSettings.IS_AUTOMATIC_LABELING_ENABLED_PROPERTY)) {
				/* Reset all labels into unlocked state */
				for (IVehicleSymbol vehicleSymbol: vehicleSymbolMap.values()) {
					vehicleSymbol.setLocked(false);
				}
			}
			if (!settings.isAutomaticLabelingEnabled()) {
				/* auto labelling disabled, reset all labels */
				resetAllLabels();
			}
			repaint();
		}
	}
	
	private void resetAllLabels() {
		final RadarPlanViewSettings settings=getSettings();
		final StandardLabelPosition standardLabelPosition=settings.getStandardLabelPosition();
		final double distance=getStandardLabelDistance();
		
		double dx,dy;
		
		dx=standardLabelPosition.getDx();
		dy=standardLabelPosition.getDy();
		
		final double length=Math.sqrt(dx*dx+dy*dy);
		dx*=distance/length;
		dy*=distance/length;
		
		for (IVehicleSymbol vehicleSymbol: vehicleSymbolMap.values()) {
			final Rectangle2D symbolBounds=vehicleSymbol.getBounds2D();
			vehicleSymbol.getLabel().setCentroidPosition(symbolBounds.getCenterX()+dx, symbolBounds.getCenterY()+dy);
		}
	}
	
	private double getStandardLabelDistance() {
		final RadarPlanViewSettings settings=getSettings();
		final double distance;
		
		switch (settings.getStandardLabelDistance()) {
		case SMALL:
			distance=AbstractVehicleLabel.minLabelDist;
			break;
		default:
		case MEDIUM:
			distance=AbstractVehicleLabel.meanLabelDist;
			break;
		case LONG:
			distance=AbstractVehicleLabel.maxLabelDist;
			break;
		}
		
		return distance;
	}
	
	protected boolean updateSymbolInsideStatus(IVehicleSymbol symbol, double x, double y) {
		symbol.setInside(symbol.containsPoint(x, y));
		final IVehicleLabel label=symbol.getLabel();
		if (label!=null) {
			label.setInside(label.containsPoint(x, y));
		}
		return symbol.isActive() || (label!=null && label.isActive());
	}
	
	private synchronized void checkActivationChange() {
		if (!isShowing())
			return;
		Point locOnScreen=getLocationOnScreen();
		Point currentLocation=MouseInfo.getPointerInfo().getLocation();
		int x,y;
		
		x=currentLocation.x-locOnScreen.x;
		y=currentLocation.y-locOnScreen.y;
		
		checkActivationChange(x, y);
	}
	
	protected void checkActivationChange(double x, double y) {
		if (activeVehicleSymbol!=null && updateSymbolInsideStatus(activeVehicleSymbol, x, y)) {
			/* The active symbol is still active, so nothing changes */
			return;
		}
		/* We don't have an active symbol or it is not active anymore, so let's check the rest */
		for (IVehicleSymbol symbol: vehicleSymbolMap.values()) {
			if (updateSymbolInsideStatus(symbol, x, y)) {
				/* This is the newly active symbol */
				workstation.getVehicleSelectionManager().select(symbol.getVehicle());
				return;
			}
		}
		
		/* No active symbol found */
		workstation.getVehicleSelectionManager().deselect();
	}
	
	@Override
	protected void processMouseMotionEvent(MouseEvent e) {
		final int buttonMask = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK;
		switch (e.getID()) {
		case MouseEvent.MOUSE_MOVED:
			checkActivationChange(e.getX(), e.getY());
			e.consume();
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if ((e.getModifiersEx()&buttonMask)==MouseEvent.BUTTON1_DOWN_MASK) {
				e.consume();
				if (activeVehicleSymbol==null)
					break;
				final IVehicleLabel label=activeVehicleSymbol.getLabel();
				if (label==null)
					break;
				if (!label.isPressed())
					break;
				final Rectangle2D oldLabelBounds=label.getBounds2D();
				final Rectangle2D oldSymbolBounds=activeVehicleSymbol.getBounds2D();
				final Rectangle2D oldBounds=new Rectangle2D.Double();
				/* Make sure that the leading line is also redrawn */
				Rectangle2D.union(oldLabelBounds, oldSymbolBounds, oldBounds);
				
				final double newx=e.getX()+dragHookX;
				final double newy=e.getY()+dragHookY;
				label.setCentroidPosition(newx, newy);
				activeVehicleSymbol.setLocked(true);
				
				final Rectangle2D newLabelBounds=label.getBounds2D();
				final Rectangle2D newSymbolBounds=activeVehicleSymbol.getBounds2D();
				final Rectangle2D newBounds=new Rectangle2D.Double();
				/* Make sure that the leading line is also redrawn */
				Rectangle2D.union(newLabelBounds, newSymbolBounds, newBounds);
				
				repaint(oldBounds.getBounds());
				repaint(newBounds.getBounds());
			}
		}
		
		if (!e.isConsumed()) {
			super.processMouseMotionEvent(e);
		}
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		final IVehicleLabel activeVehicleLabel=(activeVehicleSymbol!=null?activeVehicleSymbol.getLabel():null);
		switch (e.getID()) {
		case MouseEvent.MOUSE_PRESSED:
		case MouseEvent.MOUSE_RELEASED:
			if (e.getButton()==MouseEvent.BUTTON1) {
				e.consume();
				if (activeVehicleSymbol==null && activeVehicleLabel==null)
					break;
				final boolean isPressed=(e.getModifiersEx()&MouseEvent.BUTTON1_DOWN_MASK)!=0;
				activeVehicleSymbol.setPressed(isPressed);
				if (activeVehicleLabel!=null) {
					activeVehicleLabel.setPressed(isPressed);
				}
			}
			break;
		}
		
		/* Forward events to active label */
		if (activeVehicleLabel!=null && activeVehicleLabel.isInside()) {
			activeVehicleLabel.processMouseEvent(e);
		}
		
		if (!e.isConsumed())
			super.processMouseEvent(e);
	}
	
	@Override
	protected void processComponentEvent(ComponentEvent e) {
		if (e.getID()==ComponentEvent.COMPONENT_RESIZED) {
			radarDeviceTransformation.update(getWidth(), getHeight());
			for (IVehicleSymbol symbol: vehicleSymbolMap.values()) {
				symbol.updatePosition();
			}
		}
		super.processComponentEvent(e);
	}
	
	public RadarPlanViewSettings getSettings() {
		return workstation.getRadarPlanViewSettings();
	}
	
	public Sector getSector() {
		return workstation.getSector();
	}
	
	public RadarDeviceTransformation getRadarDeviceTransformation() {
		return radarDeviceTransformation;
	}
	
	@Override
	protected synchronized void paintComponent(Graphics g) {
		Graphics2D g2d=(Graphics2D)g;
		
		super.paintComponent(g);
		
		long startTime=System.currentTimeMillis();
		
		if (getSettings().isAutomaticLabelingEnabled()) {
			long maxUpdateTimeMillis=100;
	
			while (System.currentTimeMillis()<startTime+maxUpdateTimeMillis) {
				autolabeller.updateOneLabel();
			}
		}
		
		IDeviceTransformation mapTransformation=new CoordinateDeviceTransformation(getSettings().getMapTransformation(), radarDeviceTransformation);
		
		if (getSettings().isShowingCoastline()) {
			landmassLayer.draw(g2d,mapTransformation);
			waterLayer.draw(g2d,mapTransformation);
		} else {
			Rectangle clipRect=g2d.getClipBounds();
			g2d.setColor(Palette.LANDMASS);
			g2d.fillRect(clipRect.x,clipRect.y,clipRect.width,clipRect.height);
		}
		if (getSettings().isShowingWaypoints()) {
			waypointDisplayLayer.draw(g2d,mapTransformation);
			drawRunwayCentrelines(g2d,radarDeviceTransformation,getSettings().getMapTransformation());
		}
		if (getSettings().isShowingSector()) {
			sectorLayer.draw(g2d,mapTransformation);
		}
		if (getSettings().isShowingMilitary()) {
			restrictedLayer.draw(g2d,mapTransformation);
		}
		if (getSettings().isShowingRings()) {
			rangeMarkLayer.draw(g2d, radarDeviceTransformation);
		}
		drawScaleMarkers(g2d, radarDeviceTransformation);
		
		if (getSettings().getSpeedVectorMinutes()>0) {
			for (IVehicleSymbol vehicleSymbol: vehicleSymbolMap.values()) {
				vehicleSymbol.paintHeadingVector(g2d);
			}
		}
		if (getSettings().getTrackHistoryLength()>0) {
			for (IVehicleSymbol vehicleSymbol: vehicleSymbolMap.values()) {
				vehicleSymbol.paintTrail(g2d);
			}
		}
	
		for (IVehicleSymbol vehicleSymbol: vehicleSymbolMap.values()) {
			if (vehicleSymbol==activeVehicleSymbol) {
				continue;
			}
			vehicleSymbol.paintSymbol(g2d);
			vehicleSymbol.paintLabel(g2d);
		}
		
		if (activeVehicleSymbol!=null) {
			activeVehicleSymbol.paintSymbol(g2d);
			activeVehicleSymbol.paintLabel(g2d);
		}
	}
	
	private void drawScaleMarkers(Graphics2D g2d, IDeviceTransformation mapTransformation) {
		Rectangle bounds=g2d.getClipBounds();
		
		g2d.setColor(Palette.WINDOW_BLUE);
		
		if (bounds.getMinX()<=scaleMarkerSize && bounds.getMaxX()>=scaleMarkerSize) {
			/* Draw left scale markers */
			Position bottom,top;
			bottom=mapTransformation.fromDevice(new Point2D.Double(0.0,bounds.getMaxY()));
			top=mapTransformation.fromDevice(new Point2D.Double(0.0,bounds.getMinY()));
			
			int bottomNumber=(int)Math.floor(bottom.getY()/scaleMarkerDistance);
			int topNumber=(int)Math.ceil(top.getY()/scaleMarkerDistance);
			
			for (int i=bottomNumber;i<=topNumber;i++) {
				double y=i*scaleMarkerDistance;
				
				Path2D marker=new Path2D.Double();
				
				Point2D position=mapTransformation.toDevice(new Position(0.0,y,0.0));
				
				marker.moveTo(0.0,position.getY()-scaleMarkerSize*.25f);
				marker.lineTo(0.0,position.getY()+scaleMarkerSize*.25f);
				marker.lineTo(0.0+scaleMarkerSize,position.getY());
				marker.closePath();
				
				g2d.fill(marker);
			}
		}
		
		if (bounds.getMinY()<=getHeight()-scaleMarkerSize && bounds.getMaxY()>=getHeight()) {
			/* Draw bottom scale markers */
			Position left,right;
			left=mapTransformation.fromDevice(new Point2D.Double(bounds.getMinX(),getHeight()));
			right=mapTransformation.fromDevice(new Point2D.Double(bounds.getMaxX(),getHeight()));
			
			int leftNumber=(int)Math.floor(left.getX()/scaleMarkerDistance);
			int rightNumber=(int)Math.ceil(right.getX()/scaleMarkerDistance);
			
			for (int i=leftNumber;i<=rightNumber;i++) {
				double x=i*scaleMarkerDistance;
				
				Path2D marker=new Path2D.Double();
				
				Point2D position=mapTransformation.toDevice(new Position(x,0.0,0.0));
				
				marker.moveTo(position.getX()-scaleMarkerSize*.25f,getHeight()-1.0);
				marker.lineTo(position.getX()+scaleMarkerSize*.25f,getHeight()-1.0);
				marker.lineTo(position.getX(),getHeight()-scaleMarkerSize-1.0);
				marker.closePath();
				
				g2d.fill(marker);
			}
		}
	}
	
	private void drawRunwayCentrelines(Graphics2D g2d, IDeviceTransformation deviceTransformation, ICoordinateTransformation transform) {
		Set<Aerodrome> aerodromes=workstation.getSector().getFixDB().getFixes(Aerodrome.class);
		
		g2d.setColor(Palette.BEACON);
		
		for (Aerodrome aerodrome: aerodromes) {
			for (Runway runway: aerodrome.getRunways()) {
				Position pos=transform.forward(runway.getCenter());
				double dx,dy;
				double length=runway.getLength();
				double heading=runway.getTrueHeading()/Units.RAD;
				
				dx=Math.sin(heading)*(centrelineLength+length/2.0);
				dy=Math.cos(heading)*(centrelineLength+length/2.0);
				
				Position end1=new Position(pos.getX()+dx,pos.getY()+dy,0.0);
				Position end2=new Position(pos.getX()-dx,pos.getY()-dy,0.0);
				
				Point2D deviceEnd1=deviceTransformation.toDevice(end1);
				Point2D deviceEnd2=deviceTransformation.toDevice(end2);
				
				Line2D line=new Line2D.Double(deviceEnd1,deviceEnd2);
				
				g2d.draw(line);
			}
		}
	}
	
	@Override
	public synchronized void vehicleLost(IVehicle lostVehicle) {
		IVehicleSymbol vehicleSymbol=vehicleSymbolMap.get(lostVehicle);
		autolabeller.removeLabeledObject(vehicleSymbol);
		vehicleSymbolMap.remove(lostVehicle);
		logger.fine("Vehicle lost:"+lostVehicle);
	}
	
	@Override
	public synchronized void vehicleUpdated(Set<IVehicle> updatedVehicles) {
		logger.fine("Vehicle state update");
		final RadarPlanViewSettings settings=getSettings();
		final StandardLabelPosition labelPosition=settings.getStandardLabelPosition();
		final double distance=getStandardLabelDistance();
		
		for (IVehicle vehicle: updatedVehicles) {
			IVehicleSymbol vehicleSymbol;
			final boolean isNew=!vehicleSymbolMap.containsKey(vehicle);
			if (!isNew) {
				vehicleSymbol=vehicleSymbolMap.get(vehicle);
			} else {
				vehicleSymbol=new AircraftSymbol(radarPlanViewContext,(Aircraft)vehicle);
				autolabeller.addLabeledObject(vehicleSymbol);
				vehicleSymbolMap.put(vehicle, vehicleSymbol);
				logger.fine("Vehicle state acquired:"+vehicle);
			}
			vehicleSymbol.getLabel().updateLabelContents();
			vehicleSymbol.updatePosition();
			if (isNew) {
				final Rectangle2D symbolBounds=vehicleSymbol.getBounds2D();
				vehicleSymbol.getLabel().setCentroidPosition(symbolBounds.getCenterX()+distance*labelPosition.getDx(),
						 									 symbolBounds.getCenterY()+distance*labelPosition.getDy());
			}
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				checkActivationChange();
				
				repaint();
			}
		});
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(400,400);
	}
	
	@Override
	public boolean isOpaque() {
		return true;
	}

	@Override
	public void vehicleSelectionChanged(IVehicle oldSelection, IVehicle newSelection) {
		if (oldSelection!=null) {
			final IVehicleSymbol symbol=vehicleSymbolMap.get(oldSelection);
			if (symbol!=null) {
				final IVehicleLabel label=symbol.getLabel();
				if (label!=null) {
					label.updateLabelContents();
				}
			}
		}
		if (newSelection!=null) {
			final IVehicleSymbol symbol=vehicleSymbolMap.get(newSelection);
			if (symbol!=null) {
				final IVehicleLabel label=symbol.getLabel();
				if (label!=null) {
					label.updateLabelContents();
				}
			}
			activeVehicleSymbol=symbol;
		} else {
			activeVehicleSymbol=null;
		}
		repaint();
	}
}
