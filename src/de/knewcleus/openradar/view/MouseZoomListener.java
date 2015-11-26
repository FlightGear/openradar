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
package de.knewcleus.openradar.view;

import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseZoomListener implements MouseWheelListener {
	protected final IViewerAdapter viewerAdapter;
	protected final ZoomFilter zoomFilter = new ZoomFilter();
	
	public MouseZoomListener(IViewerAdapter viewerAdapter) {
		this.viewerAdapter = viewerAdapter;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		double scale = zoomFilter.getRequestedScale();
		scale*=Math.pow(1.2, e.getWheelRotation());
		zoomFilter.setScale(scale, e.getPoint());
	}

	/**
	 * This class exists to filter too many repaint requests. It saves only the last request and performs it, when it is ready with the last repaint.
	 * So OR responds much faster on mouse wheel turns. 
	 * 
	 * @author Wolfram Wagner
	 *
	 */
	private class ZoomFilter implements Runnable{
	    
	    private double scale = -1;
	    private Point point = null;
	    private final Thread thread; 
	    
	    public ZoomFilter() {
	        thread = new Thread(this, "OpenRadar - MouseWheelZoomFilter" );
	        thread.start();
        }
	    
	    public synchronized double getRequestedScale() {
	        return scale==-1 ? viewerAdapter.getLogicalScale() : scale;
	    }
	    
	    synchronized void setScale(double scale, Point p) {
//	        System.out.println("Set scale to "+scale);
	        this.scale=scale;
	        this.point=p;
	        thread.interrupt();
	    }
	    
	    public void run() {
	        while(true) {
                Point p=null;
                double s=1;
                
	            synchronized(this) {
	                if(point!=null) {
	                    p=point;
	                    s=scale;
	                    scale=-1;
	                    point=null;
	                }
	            }
	            if(p!=null) {
	                viewerAdapter.setLogicalScale(s, p);
//	                System.out.println("Applying scale: "+s);
	            } 
	            if(p==null) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    }
                }
	        }
	    }
	}
}
