package de.knewcleus.radar.ui.rpvd;

import java.awt.AWTEvent;
import java.awt.Dimension;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import de.knewcleus.fgfs.IUpdateable;
import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.Updater;
import de.knewcleus.fgfs.location.CoordinateDeviceTransformation;
import de.knewcleus.fgfs.location.ICoordinateTransformation;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.Aerodrome;
import de.knewcleus.fgfs.navaids.Runway;
import de.knewcleus.radar.aircraft.IAircraft;
import de.knewcleus.radar.aircraft.IRadarDataConsumer;
import de.knewcleus.radar.aircraft.IRadarDataProvider;
import de.knewcleus.radar.autolabel.Autolabeller;
import de.knewcleus.radar.sector.Sector;

public class RadarPlanViewPanel extends JPanel implements IUpdateable, IRadarDataConsumer {
	private static final long serialVersionUID = 8996959818592227638L;
	
	protected final IRadarDataProvider<? extends IAircraft> radarDataProvider;
	protected final Sector sector;
	protected final RadarPlanViewSettings settings;
	protected final RadarDeviceTransformation radarDeviceTransformation;
	protected final RadarPlanViewContext radarPlanViewContext;
	
	protected final Autolabeller autolabeller=new Autolabeller(1E-2,5);
	protected final Updater radarUpdater=new Updater(this,1000);

	protected final IMapLayer landmassLayer;
	protected final IMapLayer waterLayer;
	protected final IMapLayer restrictedLayer;
	protected final IMapLayer sectorLayer;
	protected final IMapLayer waypointDisplayLayer;
	protected final RangeMarkLayer rangeMarkLayer=new RangeMarkLayer();
	
	protected final float scaleMarkerSize=10.0f;
	protected final float scaleMarkerDistance=10.0f*(float)Units.NM;
	protected final float centrelineLength=5.0f*(float)Units.NM;
	
	protected Map<IAircraft, AircraftSymbol> aircraftSymbolMap=new HashMap<IAircraft, AircraftSymbol>();
	protected AircraftSymbol selectedSymbol=null;
	protected long lastMouseX,lastMouseY;
	protected boolean isDragging;
	
	protected static int selectionRange=10;
	
	public RadarPlanViewPanel(final IRadarDataProvider<? extends IAircraft> radarDataProvider, Sector sector, RadarPlanViewSettings settings) {
		super(true); /* Is double buffered */
		this.radarDataProvider=radarDataProvider;
		this.sector=sector;
		this.settings=settings;
		
		radarDeviceTransformation=new RadarDeviceTransformation(settings);
		radarPlanViewContext=new RadarPlanViewContext(settings,radarDeviceTransformation);
		
		landmassLayer=new PolygonMapLayer(Palette.LANDMASS,sector.getLandmassPolygons());
		waterLayer=new PolygonMapLayer(Palette.WATERMASS,sector.getWaterPolygons());
		restrictedLayer=new PolygonMapLayer(Palette.RESTRICTED,sector.getRestrictedPolygons());
		sectorLayer=new PolygonMapLayer(Palette.SECTOR,sector.getSectorPolygons());
		waypointDisplayLayer=new WaypointDisplayLayer(sector);

		setBackground(Palette.WATERMASS);
		
		radarDataProvider.registerRadarDataConsumer(this);
		for (IAircraft aircraft: radarDataProvider) {
			AircraftSymbol aircraftSymbol=new AircraftSymbol(radarPlanViewContext,aircraft);
			aircraftSymbolMap.put(aircraft, aircraftSymbol);
			autolabeller.addLabeledObject(aircraftSymbol);
			aircraftSymbol.update(0);
		}
		
		radarUpdater.start();
		
		enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK|AWTEvent.MOUSE_EVENT_MASK|AWTEvent.COMPONENT_EVENT_MASK);
	}
	
	@Override
	protected void processMouseMotionEvent(MouseEvent e) {
		if (e.getID()==MouseEvent.MOUSE_MOVED) {
			if (checkForSelectionChange())
				repaint();
		} else if (e.getID()==MouseEvent.MOUSE_DRAGGED) {
			double dx,dy;
			
			dx=e.getX()-lastMouseX;
			dy=e.getY()-lastMouseY;
			
			if (isDragging) {
				selectedSymbol.getLabel().move(dx, dy);
				selectedSymbol.setLocked(true);
				repaint();
			}
		}
		lastMouseX=e.getX();
		lastMouseY=e.getY();
		super.processMouseMotionEvent(e);
	}
	
	@Override
	protected void processComponentEvent(ComponentEvent e) {
		if (e.getID()==ComponentEvent.COMPONENT_RESIZED) {
			radarDeviceTransformation.update(getWidth(), getHeight());
			prepareForDrawing();
		}
		super.processComponentEvent(e);
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		if (e.getID()==MouseEvent.MOUSE_PRESSED && e.getButton()==1) {
			isDragging=(selectedSymbol!=null && selectedSymbol.getLabel().containsPosition(e.getX(), e.getY()));
		} else if (e.getID()==MouseEvent.MOUSE_RELEASED && e.getButton()==1) {
			isDragging=false;
		}
		super.processMouseEvent(e);
	}
	
	private boolean checkForSelectionChange() {
		if (!isShowing())
			return false;
		Point locOnScreen=getLocationOnScreen();
		Point currentLocation=MouseInfo.getPointerInfo().getLocation();
		int x,y;
		
		x=currentLocation.x-locOnScreen.x;
		y=currentLocation.y-locOnScreen.y;
		
		if (selectedSymbol!=null && selectedSymbol.containsPosition(x, y)) {
			return false; // The old selection has priority
		}
		
		for (AircraftSymbol aircraftSymbol: aircraftSymbolMap.values()) {
			if (aircraftSymbol.containsPosition(x, y)) {
				if (selectedSymbol!=null) {
					selectedSymbol.setSelected(false);
				}
				selectedSymbol=aircraftSymbol;
				aircraftSymbol.setSelected(true);
				return true;
			}
		}
		
		if (selectedSymbol!=null) {
			selectedSymbol.setSelected(false);
		}
		selectedSymbol=null;
		return true;
	}
	
	public RadarPlanViewSettings getSettings() {
		return settings;
	}
	
	public RadarDeviceTransformation getRadarDeviceTransformation() {
		return radarDeviceTransformation;
	}
	
	private void prepareForDrawing() {
		Graphics2D g2d=(Graphics2D)getGraphics();
		
		for (AircraftSymbol aircraftSymbol: aircraftSymbolMap.values()) {
			aircraftSymbol.prepareForDrawing(g2d);
		}
		
		IDeviceTransformation mapTransformation=new CoordinateDeviceTransformation(settings.getMapTransformation(), radarDeviceTransformation);
		
		landmassLayer.prepareForDrawing(mapTransformation);
		waterLayer.prepareForDrawing(mapTransformation);
		waypointDisplayLayer.prepareForDrawing(mapTransformation);
		sectorLayer.prepareForDrawing(mapTransformation);
		restrictedLayer.prepareForDrawing(mapTransformation);
		
		long startTimeLabel=System.currentTimeMillis();
		/* Labelling should not take more than 100ms per update. */
		while (System.currentTimeMillis()<startTimeLabel+100) {
			autolabeller.updateOneLabel();
		}
	}
	
	@Override
	protected synchronized void paintComponent(Graphics g) {
		Graphics2D g2d=(Graphics2D)g;
		
		super.paintComponent(g);
		
		setFont(settings.getFont());
		
		if (settings.isShowingCoastline()) {
			landmassLayer.draw(g2d);
			waterLayer.draw(g2d);
		} else {
			Rectangle clipRect=g2d.getClipBounds();
			g2d.setColor(Palette.LANDMASS);
			g2d.fillRect(clipRect.x,clipRect.y,clipRect.width,clipRect.height);
		}
		if (settings.isShowingWaypoints()) {
			waypointDisplayLayer.draw(g2d);
			drawRunwayCentrelines(g2d,radarDeviceTransformation,settings.getMapTransformation());
		}
		if (settings.isShowingSector()) {
			sectorLayer.draw(g2d);
		}
		if (settings.isShowingMilitary()) {
			restrictedLayer.draw(g2d);
		}
		if (settings.isShowingRings()) {
			rangeMarkLayer.draw(g2d, radarDeviceTransformation);
		}
		drawScaleMarkers(g2d, radarDeviceTransformation);
		
		if (settings.getSpeedVectorMinutes()>0) {
			for (AircraftSymbol aircraftSymbol: aircraftSymbolMap.values()) {
				aircraftSymbol.drawHeadingVector(g2d);
			}
		}
		if (settings.getTrackHistoryLength()>0) {
			for (AircraftSymbol aircraftSymbol: aircraftSymbolMap.values()) {
				aircraftSymbol.drawTrail(g2d);
			}
		}
		for (AircraftSymbol aircraftSymbol: aircraftSymbolMap.values()) {
			if (aircraftSymbol==selectedSymbol) {
				continue;
			}
			aircraftSymbol.drawSymbol(g2d);
			aircraftSymbol.drawLabel(g2d);
		}
		
		if (selectedSymbol!=null) {
			selectedSymbol.drawSymbol(g2d);
			selectedSymbol.drawLabel(g2d);
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
		Set<Aerodrome> aerodromes=sector.getFixDB().getFixes(Aerodrome.class);
		
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
	public synchronized void radarTargetAcquired(IAircraft aircraft) {
		AircraftSymbol aircraftSymbol=new AircraftSymbol(radarPlanViewContext,aircraft);
		aircraftSymbolMap.put(aircraft, aircraftSymbol);
		autolabeller.addLabeledObject(aircraftSymbol);
		aircraftSymbol.update(0);
	}
	
	@Override
	public synchronized void radarTargetLost(IAircraft aircraft) {
		AircraftSymbol aircraftSymbol=aircraftSymbolMap.get(aircraft);
		autolabeller.removeLabeledObject(aircraftSymbol);
		aircraftSymbolMap.remove(aircraft);
	}
	
	public synchronized void update(double dt) {
		for (IAircraft aircraft: radarDataProvider) {
			AircraftSymbol aircraftSymbol=aircraftSymbolMap.get(aircraft);
			aircraftSymbol.update(dt);
		}
		
		prepareForDrawing();

		if (!isDragging)
			checkForSelectionChange();
		repaint();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(400,400);
	}
	
	@Override
	public boolean isOpaque() {
		return true;
	}
}
