package de.knewcleus.openradar.view.mouse;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Timer;

public class MouseInteractionManager extends MouseAdapter {
	protected enum State {
		RELEASED, PRESSED, HOLDING, DRAGGING;
	}
	
	protected final int buttonNumber;
	protected final int buttonMask;
	
	protected int holdDelayMillis = 700;
	protected int interclickDelayMillis = 300;
	
	protected State currentState = State.RELEASED;
	protected long lastClickTime = 0;
	protected int clickCount = 0;
	
	protected final HoldTimerListener holdTimerListener = new HoldTimerListener();
	protected final Timer holdTimer = new Timer(holdDelayMillis, holdTimerListener);
	
	public MouseInteractionManager(int buttonNumber, int buttonMask) {
		this.buttonNumber = buttonNumber;
		this.buttonMask = buttonMask;
		holdTimer.setRepeats(false);
	}
	
	public void install(Component component) {
		component.addMouseMotionListener(this);
		component.addMouseListener(this);
	}
	
	public void uninstall(Component component) {
		component.removeMouseListener(this);
		component.removeMouseMotionListener(this);
	}
	
	public int getHoldDelayMillis() {
		return holdDelayMillis;
	}
	
	public void setHoldDelayMillis(int holdDelayMillis) {
		this.holdDelayMillis = holdDelayMillis;
		holdTimer.setInitialDelay(holdDelayMillis);
	}
	
	public int getInterclickDelayMillis() {
		return interclickDelayMillis;
	}
	
	public void setInterclickDelayMillis(int interclickDelayMillis) {
		this.interclickDelayMillis = interclickDelayMillis;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton()!=buttonNumber) {
			/* Ignore events for buttons other than ours */
			return;
		}
		assert(currentState==State.RELEASED);
		transitionTo(State.PRESSED);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton()!=buttonNumber) {
			/* Ignore events for buttons other than ours */
			return;
		}
		assert(currentState!=State.RELEASED);
		transitionTo(State.RELEASED);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if ((e.getModifiersEx() & buttonMask) != buttonMask) {
			/* Ignore events for buttons other than ours */
			return;
		}
		assert(currentState!=State.RELEASED);
		transitionTo(State.DRAGGING);
	}
	
	protected void leaveState(State oldState) {
		switch (oldState) {
		case PRESSED:
			holdTimer.stop();
			break;
		case HOLDING:
			System.out.println("end hold");
			break;
		case DRAGGING:
			System.out.println("end drag");
			break;
		}
	}
	
	protected void performTransition(State oldState, State newState) {
		if (oldState==State.PRESSED && newState==State.RELEASED) {
			final long currentTime = System.currentTimeMillis();
			if (lastClickTime + interclickDelayMillis < currentTime) {
				clickCount = 0;
			}
			clickCount ++;
			System.out.println("click count="+clickCount);
			lastClickTime = currentTime;
		}
	}
	
	protected void enterState(State newState) {
		switch (newState) {
		case PRESSED:
			holdTimer.start();
			break;
		case HOLDING:
			System.out.println("start hold");
			break;
		case DRAGGING:
			System.out.println("start drag");
			break;
		}
	}
	
	protected void transitionTo(State newState) {
		final State oldState = currentState;
		if (oldState!=newState) {
			/* First leave the old state */
			leaveState(oldState);
		}
		/* Now perform the transition */
		performTransition(oldState, newState);
		currentState = newState;
		if (oldState!=newState) {
			/* Then enter the new state */
			enterState(newState);
		}
	}
	
	protected class HoldTimerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (currentState==State.PRESSED) {
				transitionTo(State.HOLDING);
			}
		}
	};
}
