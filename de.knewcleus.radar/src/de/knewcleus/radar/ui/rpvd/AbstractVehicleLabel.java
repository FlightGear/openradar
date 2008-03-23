package de.knewcleus.radar.ui.rpvd;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import de.knewcleus.radar.autolabel.Label;
import de.knewcleus.radar.autolabel.LabeledObject;
import de.knewcleus.radar.ui.Palette;
import de.knewcleus.radar.ui.labels.LabelElementContainer;

public abstract class AbstractVehicleLabel extends LabelElementContainer implements Label, IVehicleLabel {
	protected double hookX, hookY;
	protected double dirX, dirY;

	protected final LabelElementContainer labelLines[];

	public AbstractVehicleLabel(int maxLabelLines) {
		labelLines=new LabelElementContainer[maxLabelLines];
	}
	
	protected void clearAllLines() {
		for (int i=0;i<labelLines.length;i++) {
			labelLines[i].removeAll();
		}
	}

	@Override
	public void updatePosition() {
		final Rectangle2D vehicleSymbolBounds=getVehicleSymbol().getSymbolBounds();
		final Rectangle2D labelBounds=getBounds2D();
		final double symcx,symcy,symw,symh;
		final double labelx,labely,labelw,labelh;
		
		symcx=vehicleSymbolBounds.getCenterX();
		symcy=vehicleSymbolBounds.getCenterY();
		symw=vehicleSymbolBounds.getWidth();
		symh=vehicleSymbolBounds.getHeight();
		
		labelw=labelBounds.getWidth();
		labelh=labelBounds.getHeight();
		
		labelx=symcx+dirX*(symw+labelw)/2-labelw/2+hookX;
		labely=symcy+dirY*(symh+labelh)/2-labelh/2+hookY;
		
		setPosition(labelx, labely);
	}
	
	@Override
	public boolean isOpaque() {
		return getVehicleSymbol().getVehicle().isSelected();
	}
	
	@Override
	public Color getForegroundColor() {
		return (getVehicleSymbol().getVehicle().isSelected()?
				getSelectedTextColor():getNormalTextColor());
	}
	
	@Override
	public Color getBackgroundColor() {
		return (getVehicleSymbol().getVehicle().isSelected()?
				getSelectedBackgroundColor():Palette.TRANSPARENT);
	}

	@Override
	public LabeledObject getAssociatedObject() {
		return getVehicleSymbol();
	}

	@Override
	public double getHookX() {
		return hookX;
	}

	@Override
	public double getHookY() {
		return hookY;
	}

	public void setInitialHookPosition(double dx, double dy) {
		hookX=dx;
		hookY=dy;
		
		final double len=Math.sqrt(hookX*hookX+hookY*hookY);
		
		if (len>1E-3) {
			dirX=hookX/len;
			dirY=hookY/len;
		} else {
			dirX=1.0;
			dirY=0.0;
		}
		
		if (len<AircraftSymbol.minLabelDist) {
			hookX=dirX*AircraftSymbol.minLabelDist;
			hookY=dirY*AircraftSymbol.minLabelDist;
		} else if (len>AircraftSymbol.maxLabelDist) {
			hookX=dirX*AircraftSymbol.maxLabelDist;
			hookY=dirY*AircraftSymbol.maxLabelDist;
		}
	}

	public void setHookPosition(double dx, double dy) {
		setInitialHookPosition(dx, dy);
		updatePosition();
	}

	@Override
	public void move(double dx, double dy) {
		setHookPosition(hookX+dx, hookY+dy);
	}
	
	public abstract Color getNormalTextColor();
	public abstract Color getSelectedTextColor();
	public abstract Color getSelectedBackgroundColor();

	public abstract IVehicleSymbol getVehicleSymbol();

	public abstract void updateLabelContents();
}