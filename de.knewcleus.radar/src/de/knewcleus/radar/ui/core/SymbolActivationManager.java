package de.knewcleus.radar.ui.core;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Deque;
import java.util.LinkedList;
import java.util.logging.Logger;

public class SymbolActivationManager implements MouseMotionListener, MouseListener {
	protected final static Logger logger=Logger.getLogger(SymbolActivationManager.class.getName());
	protected final DisplayElementContainer elementContainer;
	protected WorkObjectSymbol mouseContainingSymbol=null;
	protected WorkObjectSymbol focusedSymbol=null;

	public SymbolActivationManager(DisplayElementContainer elementContainer) {
		this.elementContainer=elementContainer;
	}

	public DisplayElementContainer getElementContainer() {
		return elementContainer;
	}

	public WorkObjectSymbol getFocusedSymbol() {
		return focusedSymbol;
	}

	/**
	 * Request to assign the focus to the given symbol.
	 * 
	 * If another symbol already has the focus, false is returned and
	 * the focus does not change. Otherwise the focus is assigned to the given
	 * symbol and true is returned.
	 * 
	 * @param symbol	The symbol requesting the focus.
	 * @return	true if and only if the assignment of focus was successful.
	 */
	public boolean requestFocus(WorkObjectSymbol symbol) {
		logger.info("focus requested by "+symbol);
		if (focusedSymbol==symbol) {
			logger.info("symbol already has the focus");
			return true;
		}
		if (focusedSymbol==null) {
			focusedSymbol=symbol;
			logger.info("focus assigned to "+symbol);
			return true;
		}
		logger.info("focus assignment denied");
		return false;
	}

	/**
	 * Release the focus from the given symbol.
	 * 
	 * @param symbol	The symbol releasing the focus.
	 */
	public void releaseFocus(WorkObjectSymbol symbol) {
		assert(focusedSymbol==symbol):"Symbol trying to release the focus while it didn't have it in first place";
		focusedSymbol=null;
		logger.info("focus released by "+symbol);
	}

	public void mouseMoved(MouseEvent e) {
		checkMouseEntryExit(e);

		if (focusedSymbol!=null && !e.isConsumed())
			focusedSymbol.mouseMoved(e);
	}

	public void mouseDragged(MouseEvent e) {
		checkMouseEntryExit(e);

		if (focusedSymbol!=null && !e.isConsumed())
			focusedSymbol.mouseDragged(e);
	}

	public void mouseClicked(MouseEvent e) {
		if (focusedSymbol!=null && !e.isConsumed())
			focusedSymbol.mouseClicked(e);
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {
		if (mouseContainingSymbol!=null) {
			mouseContainingSymbol.mouseExited(e);
		}
		/* force focused symbol to release focus */
		if (focusedSymbol!=null) {
			releaseFocus(focusedSymbol);
		}
	}

	public void mousePressed(MouseEvent e) {
		if (focusedSymbol!=null && !e.isConsumed())
			focusedSymbol.mousePressed(e);
	}

	public void mouseReleased(MouseEvent e) {
		if (focusedSymbol!=null && !e.isConsumed())
			focusedSymbol.mouseReleased(e);
	}

	protected void checkMouseEntryExit(MouseEvent e) {
		if (mouseContainingSymbol!=null && !mouseContainingSymbol.isHit(e.getPoint())) {
			/* The mouse has left the containing symbol */
			mouseContainingSymbol.mouseExited(e);
			logger.info("mouse exited "+mouseContainingSymbol);
			mouseContainingSymbol=null;
		}

		/* The currently containing symbol has precedence, so we
		 * only check for a newly entered symbol whenn there is no
		 * symbol currently containing the mouse.
		 */
		if (mouseContainingSymbol==null) {
			/* Check whether the mouse has entered a new symbol */
			final Deque<DisplayElement> hitElements=new LinkedList<DisplayElement>();
			elementContainer.getHitObjects(e.getPoint(), hitElements);
			while (!hitElements.isEmpty()) {
				final DisplayElement element=hitElements.removeLast();
				if (element instanceof WorkObjectSymbol) {
					mouseContainingSymbol=(WorkObjectSymbol)element;
					logger.info("mouse entered "+mouseContainingSymbol);
					mouseContainingSymbol.mouseEntered(e);
					break;
				}
			}
		}
	}
}
