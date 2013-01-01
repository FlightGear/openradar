/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
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

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import de.knewcleus.openradar.view.IView;

public class MouseInteractionManager extends MouseAdapter {
	public enum Handedness {
		LEFT_HANDED, RIGHT_HANDED;
	};
	
	protected final static int leftButtonNumber=MouseEvent.BUTTON1;
	protected final static int leftButtonMask=MouseEvent.BUTTON1_DOWN_MASK;
	protected final static int rightButtonNumber=MouseEvent.BUTTON3;
	protected final static int rightButtonMask=MouseEvent.BUTTON3_DOWN_MASK;

	protected final IView rootView;
	protected final MouseButtonManager actionButtonManager=new MouseButtonManager(this, ButtonType.ACTION_BUTTON);
	protected final MouseButtonManager informationButtonManager=new MouseButtonManager(this, ButtonType.INFORMATION_BUTTON);
	
	protected int holdDelayMillis = 300;
	protected int multiClickDelayMillis = 300;

	protected Handedness handedness=Handedness.LEFT_HANDED;

	public MouseInteractionManager(IView rootView) {
		this.rootView=rootView;
	}

	public void install(Component component) {
		component.addMouseMotionListener(this);
		component.addMouseListener(this);
	}
	
	public void uninstall(Component component) {
		component.removeMouseListener(this);
		component.removeMouseMotionListener(this);
	}
	
	public void setHandedness(Handedness handedness) {
		this.handedness = handedness;
	}
	
	public Handedness getHandedness() {
		return handedness;
	}
	
	public IView getRootView() {
		return rootView;
	}
	
	public int getHoldDelayMillis() {
		return holdDelayMillis;
	}
	
	public void setHoldDelayMillis(int holdDelayMillis) {
		this.holdDelayMillis = holdDelayMillis;
	}
	
	public int getMultiClickDelayMillis() {
		return multiClickDelayMillis;
	}
	
	public void setInterclickDelayMillis(int interclickDelayMillis) {
		this.multiClickDelayMillis = interclickDelayMillis;
	}
	
	protected MouseButtonManager getLeftButtonManager() {
		switch (handedness) {
		case LEFT_HANDED:
			return actionButtonManager;
		case RIGHT_HANDED:
			return informationButtonManager;
		}
		/* Should never be reached */
		throw new RuntimeException("Invalid handedness "+handedness);
	}
	
	protected MouseButtonManager getRightButtonManager() {
		switch (handedness) {
		case LEFT_HANDED:
			return informationButtonManager;
		case RIGHT_HANDED:
			return actionButtonManager;
		}
		/* Should never be reached */
		throw new RuntimeException("Invalid handedness "+handedness);
	}
	
	protected MouseButtonManager getButtonManager(int buttonNumber) {
		if (buttonNumber==leftButtonNumber) {
			return getLeftButtonManager();
		} else if (buttonNumber==rightButtonNumber) {
			return getRightButtonManager();
		} else {
			return null;
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		final MouseButtonManager manager=getButtonManager(e.getButton());
		if (manager!=null) {
			manager.mousePressed(e);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		final MouseButtonManager manager=getButtonManager(e.getButton());
		if (manager!=null) {
			manager.mouseReleased(e);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if ((e.getModifiersEx() & leftButtonMask)==leftButtonMask) {
			getLeftButtonManager().mouseDragged(e);
		}
		if ((e.getModifiersEx() & rightButtonMask)==rightButtonMask) {
			getLeftButtonManager().mouseDragged(e);
		}
	}
}
