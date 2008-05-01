package de.knewcleus.radar.ui.rpvd;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.CoordinateDeviceTransformation;
import de.knewcleus.fgfs.location.ICoordinateTransformation;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.Aerodrome;
import de.knewcleus.fgfs.navaids.Runway;
import de.knewcleus.radar.DisplayElement;
import de.knewcleus.radar.sector.Sector;
import de.knewcleus.radar.ui.Palette;
import de.knewcleus.radar.ui.RadarWorkstation;
import de.knewcleus.radar.ui.map.RadarMapPanel;
import de.knewcleus.radar.ui.rpvd.tracks.TrackSymbolContainer;
import de.knewcleus.radar.vessels.ITrackUpdateListener;
import de.knewcleus.radar.vessels.Track;

public class RadarPlanViewPanel extends RadarMapPanel implements PropertyChangeListener, ITrackUpdateListener {
	protected final static Logger logger=Logger.getLogger(RadarPlanViewPanel.class.getName());
	private static final long serialVersionUID = 8996959818592227638L;

	protected final RadarWorkstation workstation;

	protected final Map<Track, DisplayElement> trackSymbolMap=new HashMap<Track, DisplayElement>(); 

	protected final IMapLayer landmassLayer;
	protected final IMapLayer waterLayer;
	protected final IMapLayer restrictedLayer;
	protected final IMapLayer sectorLayer;
	protected final WaypointDisplayLayer waypointDisplayLayer;
	protected final RangeMarkLayer rangeMarkLayer=new RangeMarkLayer();

	protected final float scaleMarkerSize=10.0f;
	protected final float scaleMarkerDistance=10.0f*(float)Units.NM;
	protected final float centrelineLength=5.0f*(float)Units.NM;

	protected static int selectionRange=10;

	public RadarPlanViewPanel(RadarWorkstation workstation, ICoordinateTransformation mapTransformation) {
		super(workstation.getRadarPlanViewSettings(), mapTransformation);
		setDoubleBuffered(true); /* Is double buffered */
		this.workstation=workstation;

		workstation.getRadarPlanViewSettings().addPropertyChangeListener(this);

		final Sector sector=workstation.getSector();
		landmassLayer=new PolygonMapLayer(Palette.LANDMASS,sector.getLandmassPolygons());
		waterLayer=new PolygonMapLayer(Palette.WATERMASS,sector.getWaterPolygons());
		restrictedLayer=new PolygonMapLayer(Palette.RESTRICTED,sector.getRestrictedPolygons());
		sectorLayer=new PolygonMapLayer(Palette.SECTOR,sector.getSectorPolygons());
		waypointDisplayLayer=new WaypointDisplayLayer(sector);

		Set<Aerodrome> aerodromes=sector.getFixDB().getFixes(Aerodrome.class);
		waypointDisplayLayer.getFixesWithDesignator().addAll(aerodromes);

		setBackground(Palette.WATERMASS);

		// TODO: set the font in the UI look and feel
		setFont(new Font(Font.SANS_SERIF,Font.PLAIN,12));

		enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK|AWTEvent.MOUSE_EVENT_MASK|AWTEvent.COMPONENT_EVENT_MASK);

		workstation.getTargetManager().registerTrackUpdateListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getOldValue().equals(evt.getNewValue())) {
			return;
		}

		if (evt.getSource()==getSettings()) {
			String propertyName=evt.getPropertyName();
			final RadarPlanViewSettings settings=getSettings();

			if (propertyName.equals(RadarPlanViewSettings.RANGE_PROPERTY)) {
				setXRange(getSettings().getRange()*Units.NM);
			} else if (propertyName.equals(RadarPlanViewSettings.IS_AUTOMATIC_LABELING_ENABLED_PROPERTY) ||
					propertyName.equals(RadarPlanViewSettings.STANDARD_LABEL_POSITION_PROPERTY) ||
					propertyName.equals(RadarPlanViewSettings.STANDARD_LABEL_DISTANCE_PROPERTY)) {

				if (propertyName.equals(RadarPlanViewSettings.IS_AUTOMATIC_LABELING_ENABLED_PROPERTY)) {
					/* Make all labels autolabelled */
					// TODO
				}
				if (!settings.isAutomaticLabelingEnabled()) {
					/* auto labelling disabled, reset all labels */
					resetAllLabels();
				}
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

		// TODO: set new label positions
	}

	private double getStandardLabelDistance() {
		final RadarPlanViewSettings settings=getSettings();
		final double distance=10;

		switch (settings.getStandardLabelDistance()) {
		// TODO
		}

		return distance;
	}

	public RadarPlanViewSettings getSettings() {
		return workstation.getRadarPlanViewSettings();
	}

	public Sector getSector() {
		return workstation.getSector();
	}

	@Override
	protected void paintMapBackground(Graphics2D g2d) {
		IDeviceTransformation mapTransformation=new CoordinateDeviceTransformation(getMapTransformation(), getDeviceTransformation());

		Rectangle clipRect=g2d.getClipBounds();
		g2d.setColor((getSettings().isShowingCoastline()?Palette.WATERMASS:Palette.LANDMASS));
		g2d.fillRect(clipRect.x,clipRect.y,clipRect.width,clipRect.height);
		if (getSettings().isShowingCoastline()) {
			landmassLayer.draw(g2d,mapTransformation);
			waterLayer.draw(g2d,mapTransformation);
		}
		if (getSettings().isShowingWaypoints()) {
			waypointDisplayLayer.draw(g2d,mapTransformation);
			drawRunwayCentrelines(g2d,getDeviceTransformation(),getMapTransformation());
		}
		if (getSettings().isShowingSector()) {
			sectorLayer.draw(g2d,mapTransformation);
		}
		if (getSettings().isShowingMilitary()) {
			restrictedLayer.draw(g2d,mapTransformation);
		}
		if (getSettings().isShowingRings()) {
			rangeMarkLayer.draw(g2d, getDeviceTransformation());
		}
		drawScaleMarkers(g2d, getDeviceTransformation());
	}

	@Override
	public synchronized void tracksUpdated(Set<Track> targets) {
		for (Track track: targets) {
			DisplayElement symbol=trackSymbolMap.get(track);
			if (symbol==null) {
				symbol=new TrackSymbolContainer(track);
				symbol.setDisplayComponent(this);
				add(symbol);
				trackSymbolMap.put(track, symbol);
			} else {
				symbol.validate();
			}
		}
	}

	@Override
	public synchronized void trackLost(Track target) {
		if (!trackSymbolMap.containsKey(target))
			return;
		final DisplayElement symbol=trackSymbolMap.get(target);
		trackSymbolMap.remove(target);
		remove(symbol);
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
	public Dimension getPreferredSize() {
		return new Dimension(400,400);
	}
}
