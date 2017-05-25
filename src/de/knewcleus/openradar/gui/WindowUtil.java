/**
 * Copyright (C) 2017 Wolfram Wagner
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with OpenRadar. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie von der Free
 * Software Foundation, Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne
 * die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public
 * License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 * <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.gui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JFrame;

/**
 * Utility methods for window placement etc.
 * 
 * @author Wolfram Wagner
 *
 */
public class WindowUtil {

	public static void placeWindowOnMonitor(JFrame frame, int screen, boolean maximize, boolean center) {
    	GraphicsDevice gds[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
   		GraphicsDevice gd = gds[screen];

   		Rectangle bounds = frame.getBounds();
   		if(maximize) {
   			// maximize
   			
   		} else {
   			// do not maximize
   			
   		}
   		
	}
	
    public static int getCurrentScreen(JFrame frame) {
    	GraphicsDevice gds[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    	Point loc = frame.getLocation();
    	for(int i=0;i<gds.length;i++){
    		GraphicsDevice gd = gds[i];
    	    if(loc.getX() >= gd.getDefaultConfiguration().getBounds().getMinX() &&
    	       loc.getX() < gd.getDefaultConfiguration().getBounds().getMaxX() &&
    	       loc.getY() >= gd.getDefaultConfiguration().getBounds().getMinY() &&
    	       loc.getY() < gd.getDefaultConfiguration().getBounds().getMaxY()) {
	    	    	return i;
    	    }
    	}
    	return -1;
    }
}
