/**
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
package de.knewcleus.openradar.view.objects;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class NDBSymbol extends AViewObject {

    private static BufferedImage image;
    private Point2D displayPosition;
    AirportData data;
    
    public NDBSymbol(AirportData data, int minScale, int maxScale) {
        super(Palette.CRD_BACKGROUND);
        this.data = data;
        this.minScalePath=minScale;
        this.maxScalePath=maxScale;
        try {
            image = ImageIO.read(new File("res/NDB.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        this.displayPosition = newDisplayPosition;
  
        int scale = (int)mapViewAdapter.getLogicalScale();
        scale = scale==0 ? 1 : scale; 
        scale = (int)Math.round(30d * 10d/scale);
        if(scale<20) scale=20;
        if(scale>30) scale=30;
        
        path = new Path2D.Double();
            path.append(new Rectangle2D.Double((int)displayPosition.getX()-scale/2, (int)displayPosition.getY()-scale/2, scale,scale), false);
    }

    
    
    @Override
    public void paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
        if(data.getRadarObjectFilterState("NDB")) {
            int scale = (int)mapViewAdapter.getLogicalScale();
            scale = scale==0 ? 1 : scale; 
            int size = (int) Math.round(30d * 10d/scale);
            if(size<20) size=20;
            if(size>30) size=30;
            
            if(displayPosition!=null && scale>minScalePath && scale<maxScalePath) {
                g2d.drawImage(image.getScaledInstance(size, -1, Image.SCALE_SMOOTH), (int)displayPosition.getX()-size/2, (int)displayPosition.getY()-size/2, (ImageObserver) null);
            }
        }
    }
}
