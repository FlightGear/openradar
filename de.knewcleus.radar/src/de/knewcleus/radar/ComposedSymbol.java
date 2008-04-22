package de.knewcleus.radar;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

public abstract class ComposedSymbol extends Symbol {
	protected Rectangle2D currentBounds=null;
	
	@Override
	public Rectangle2D getBounds() {
		return currentBounds;
	}
	
	@Override
	public void paint(Graphics2D g) {
		final Rectangle clipBounds=g.getClipBounds();
		for (Symbol part: getSymbolParts()) {
			final Rectangle2D symbolBounds=part.getBounds();
			if (symbolBounds==null)
				continue;
			if (!clipBounds.intersects(symbolBounds))
				continue;
			part.paint(g);
		}
	}
	
	@Override
	public void validate() {
		for (Symbol part: getSymbolParts()) {
			part.validate();
		}

		for (Symbol part: getSymbolParts()) {
			final Rectangle2D childBounds=part.getBounds();
			if (childBounds==null)
				continue;
			if (currentBounds==null) {
				currentBounds=(Rectangle2D)childBounds.clone();
			} else {
				Rectangle2D.union(currentBounds, childBounds, currentBounds);
			}
		}
	}
	
	public abstract Collection<Symbol> getSymbolParts();
}
