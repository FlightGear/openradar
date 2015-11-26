/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2012 Wolfram Wagner
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.mouse;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Timer;

import de.knewcleus.fgfs.util.IOutputIterator;
import de.knewcleus.openradar.view.IPickable;
import de.knewcleus.openradar.view.PickVisitor;

/**
 * A mouse interaction manager provides {@link MouseInteractionEvent} instances
 * to {@link IMouseTargetView} instances.
 * 
 * @author Ralf Gerlich
 *
 */
public class MouseButtonManager extends MouseAdapter {
	protected enum State {
		RELEASED, PRESSED, HOLDING, DRAGGING;
	}
	
	protected State currentState = State.RELEASED;
	protected long lastClickTime = 0;
	protected int clickCount = 0;
	protected Point pressPoint = null;
	protected IMouseTargetView concernedView = null;
	
	protected final MouseInteractionManager interactionManager;
	protected final ButtonType buttonType;
	
	protected final HoldTimerListener holdTimerListener = new HoldTimerListener();
	protected final Timer holdTimer;
	
	public MouseButtonManager(MouseInteractionManager interactionManager,
			ButtonType buttonType) {
		this.interactionManager=interactionManager;
		this.buttonType = buttonType;
		holdTimer= new Timer(interactionManager.getHoldDelayMillis(), holdTimerListener);
		holdTimer.setRepeats(false);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		assert(currentState==State.RELEASED);
		pressPoint = e.getPoint();
		transitionTo(State.PRESSED, e.getPoint(), e.getWhen());
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		assert(currentState!=State.RELEASED);
		transitionTo(State.RELEASED, e.getPoint(), e.getWhen());
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		assert(currentState!=State.RELEASED);
		transitionTo(State.DRAGGING, e.getPoint(), e.getWhen());
	}
	
	protected void leaveState(State oldState, Point point, long when) {
		switch (oldState) {
		case PRESSED:
			holdTimer.stop();
			break;
		case HOLDING:
			sendEvent(MouseInteractionEvent.Type.END_HOLD, point, when);
			break;
		case DRAGGING:
			sendEvent(MouseInteractionEvent.Type.END_DRAG, point, when);
			break;
        default:
            break;
		}
	}

	protected void performTransition(State oldState, State newState, Point point, long when) {
		if (oldState==State.PRESSED && newState==State.RELEASED) {
			if (lastClickTime + interactionManager.getMultiClickDelayMillis() < when) {
				clickCount = 0;
			}
			clickCount ++;
			lastClickTime = when;
			sendEvent(MouseInteractionEvent.Type.CLICK, point, when);
		} else if (oldState==State.DRAGGING && newState==State.DRAGGING) {
			sendEvent(MouseInteractionEvent.Type.DRAG, point, when);
		}
	}
	
	protected void enterState(State newState, Point point, long when) {
		switch (newState) {
		case RELEASED:
			updateConcernedView(point);
			break;
		case PRESSED:
			updateConcernedView(point);
			holdTimer.start();
			break;
		case HOLDING:
			sendEvent(MouseInteractionEvent.Type.START_HOLD, point, when);
			break;
		case DRAGGING:
			sendEvent(MouseInteractionEvent.Type.START_DRAG, point, when);
			break;
		}
	}
	
	protected void transitionTo(State newState, Point point, long when) {
		final State oldState = currentState;
		if (oldState!=newState) {
			/* First leave the old state */
			leaveState(oldState, point, when);
		}
		/* Now perform the transition */
		performTransition(oldState, newState, point, when);
		currentState = newState;
		if (oldState!=newState) {
			/* Then enter the new state */
			enterState(newState, point, when);
		}
	}
	
	protected void sendEvent(MouseInteractionEvent.Type type, Point point, long when) {
		if (concernedView==null) {
			return;
		}
		final MouseInteractionEvent event;
		event = new MouseInteractionEvent(
				concernedView,
				buttonType,
				type,
				clickCount,
				point,
				when);
		concernedView.processMouseInteractionEvent(event);
	}
	
	protected void updateConcernedView(Point point) {
		final MouseTargetPickIterator iterator = new MouseTargetPickIterator();
		final PickVisitor pickVisitor = new PickVisitor(point, iterator);
		interactionManager.getRootView().accept(pickVisitor);
		concernedView = iterator.getTopTarget();
	}
	
	protected class HoldTimerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (currentState==State.PRESSED) {
				transitionTo(State.HOLDING, pressPoint, e.getWhen());
			}
		}
	};
	
	protected class MouseTargetPickIterator implements IOutputIterator<IPickable> {
		protected IMouseTargetView topTarget = null;
		
		@Override
		public void next(IPickable v) {
			if (v instanceof IMouseTargetView) {
				topTarget = (IMouseTargetView)v;
			}
		}
		
		@Override
		public boolean wantsNext() {
			return true;
		}
		
		public IMouseTargetView getTopTarget() {
			return topTarget;
		}
	}
}
