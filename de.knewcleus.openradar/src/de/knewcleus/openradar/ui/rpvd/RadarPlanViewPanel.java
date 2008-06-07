package de.knewcleus.openradar.ui.rpvd;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.CoordinateDeviceTransformation;
import de.knewcleus.fgfs.location.ICoordinateTransformation;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.Aerodrome;
import de.knewcleus.fgfs.navaids.DesignatedPoint;
import de.knewcleus.fgfs.util.GeometryConversionException;
import de.knewcleus.openradar.sector.Sector;
import de.knewcleus.openradar.ui.Palette;
import de.knewcleus.openradar.ui.RadarWorkstation;
import de.knewcleus.openradar.ui.core.DisplayElement;
import de.knewcleus.openradar.ui.map.RadarMapPanel;
import de.knewcleus.openradar.ui.rpvd.tracks.TrackSymbolContainer;
import de.knewcleus.openradar.vessels.ITrackUpdateListener;
import de.knewcleus.openradar.vessels.Track;

public class RadarPlanViewPanel extends RadarMapPanel implements PropertyChangeListener, ITrackUpdateListener {
	protected final static Logger logger=Logger.getLogger(RadarPlanViewPanel.class.getName());
	private static final long serialVersionUID = 8996959818592227638L;

	protected final RadarWorkstation workstation;

	protected final Map<Track, DisplayElement> trackSymbolMap=new HashMap<Track, DisplayElement>(); 

	protected final IMapLayer landmassLayer;
	protected final IMapLayer restrictedLayer;
	protected final IMapLayer sectorLayer;
	protected final IMapLayer pavementLayer;
	protected final WaypointDisplayLayer waypointDisplayLayer;
	protected final IMapLayer rangeMarkLayer=new RangeMarkLayer();
	protected final List<IMapLayer> mapLayers;

	protected final float scaleMarkerSize=10.0f;
	protected final float scaleMarkerDistance=10.0f*(float)Units.NM;
	protected final float centrelineLength=5.0f*(float)Units.NM;

	protected static int selectionRange=10;

	public RadarPlanViewPanel(RadarWorkstation workstation, ICoordinateTransformation mapTransformation) throws GeometryConversionException {
		super(workstation.getRadarPlanViewSettings(), mapTransformation);
		setDoubleBuffered(true); /* Is double buffered */
		this.workstation=workstation;

		workstation.getRadarPlanViewSettings().addPropertyChangeListener(this);

		final Sector sector=workstation.getSector();
		landmassLayer=new LandmassMapLayer("Shoreline",
				Palette.LANDMASS, sector.getLandmassPolygons(),
				Palette.WATERMASS, sector.getWaterPolygons());
		restrictedLayer=new GeometryMapLayer("Restricted", Palette.RESTRICTED,sector.getRestrictedPolygons());
		sectorLayer=new GeometryMapLayer("Sector", Palette.SECTOR,sector.getSectorPolygons());
		pavementLayer=new GeometryMapLayer("Pavement", Palette.PAVEMENT,sector.getPavementPolygons());
		waypointDisplayLayer=new WaypointDisplayLayer("Waypoints", sector);
		mapLayers=Arrays.asList(landmassLayer, restrictedLayer, sectorLayer, pavementLayer, waypointDisplayLayer, rangeMarkLayer);

		Set<Aerodrome> aerodromes=sector.getFixDB().getFixes(Aerodrome.class);
		waypointDisplayLayer.getFixesWithDesignator().addAll(aerodromes);
		Set<DesignatedPoint> designatedPoints=sector.getFixDB().getFixes(DesignatedPoint.class);
		waypointDisplayLayer.getFixesWithDesignator().addAll(designatedPoints);

		setBackground(Palette.WATERMASS);

		// TODO: set the font in the UI look and feel
		setFont(new Font(Font.SANS_SERIF,Font.PLAIN,12));

		enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK|AWTEvent.MOUSE_EVENT_MASK|AWTEvent.COMPONENT_EVENT_MASK);

		workstation.getTargetManager().registerTrackUpdateListener(this);
	}
	
	public List<IMapLayer> getMapLayers() {
		return mapLayers;
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

		for (IMapLayer layer: getMapLayers()) {
			layer.draw(g2d, mapTransformation);
		}
		drawScaleMarkers(g2d, getDeviceTransformation());
	}

	@Override
	public synchronized void tracksUpdated(Set<Track> targets) {
		for (Track track: targets) {
			DisplayElement symbol=trackSymbolMap.get(track);
			if (symbol==null) {
				symbol=new TrackSymbolContainer(track);
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

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(400,400);
	}
}
