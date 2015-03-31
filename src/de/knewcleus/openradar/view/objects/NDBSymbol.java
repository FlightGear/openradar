/**
 * Copyright (C) 2012,2013 Wolfram Wagner
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

import java.awt.Color;
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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.knewcleus.fgfs.navdata.impl.Intersection;
import de.knewcleus.fgfs.navdata.impl.NDB;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class NDBSymbol extends AViewObject {

    private static BufferedImage image;
    private Point2D displayPosition;
    private GuiMasterController master;
    private Intersection ndb;
    private int defaultMaxScale;
    private Color defaultColor;

    private final static Logger log = LogManager.getLogger(NDBSymbol.class);
    
    public NDBSymbol(GuiMasterController master, NDB ndb, int minScale, int maxScale) {
        super(Palette.CRD_BACKGROUND);
        this.master = master;
        this.minScalePath=minScale;
        this.maxScalePath=maxScale;
        this.ndb = ndb;
        this.defaultMaxScale = maxScale;
        this.defaultColor = color;
        try {
            image = ImageIO.read(new File("res/NDB.png"));
        } catch (IOException e) {
            log.error("Error while loading NDB image!",e);
        }
    }

    @Override
    protected void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        this.displayPosition = newDisplayPosition;

        Color highLightColor = master.getAirportData().getNavaidDB().getNavaidHighlightColor(master,ndb);

        if(highLightColor!=null) {
            this.maxScalePath=Integer.MAX_VALUE;
            this.color = highLightColor;
        } else {
            this.maxScalePath=defaultMaxScale;
            this.color = defaultColor;
        }

        int scale = (int)mapViewAdapter.getLogicalScale();
        scale = scale==0 ? 1 : scale;
        scale = (int)Math.round(20d * 10d/scale);
        if(scale<15) scale=15;
        if(scale>30) scale=30;

        path = new Path2D.Double();
        path.append(new Rectangle2D.Double((int)displayPosition.getX()-scale/2, (int)displayPosition.getY()-scale/2, scale,scale), false);
    }



    @Override
    public void paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {

        int scale = (int)mapViewAdapter.getLogicalScale();
        scale = scale==0 ? 1 : scale;
        int size = (int) Math.round(20d * 10d/scale);
        if(size<15) size=15;
        if(size>30) size=30;

        if( (ndb.isHighlighted() || master.getAirportData().getNavaidDB().isPartOfRoute(master, ndb)) || (master.getAirportData().getRadarObjectFilterState("NDB") && (scale>minScalePath && scale<maxScalePath))) {
            g2d.drawImage(image.getScaledInstance(size, -1, Image.SCALE_SMOOTH), (int)displayPosition.getX()-size/2, (int)displayPosition.getY()-size/2, (ImageObserver) null);
        }
}
 }
