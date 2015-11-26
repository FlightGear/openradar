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

import de.knewcleus.fgfs.navdata.impl.VOR;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class VORSymbol extends AViewObject {

    private GuiMasterController master;
    private VOR vor;
    private Color defaultColor;

    public enum VORType {
        VOR, VOR_DME, VORTAC
    }

    private VORType vorType = VORType.VOR;
    private static BufferedImage imageVOR;
    private static BufferedImage imageVORTME;
    private static BufferedImage imageVORTAC;
    private Point2D displayPosition;

    private final static Logger log = LogManager.getLogger(VORSymbol.class);
    
    public VORSymbol(GuiMasterController master, VOR vor, VORType vorType) {
        super(Palette.CRD_BACKGROUND);
        this.master = master;
        this.vor = vor;
        this.defaultColor = color;

        this.vorType = vorType;

        try {
            imageVOR = ImageIO.read(new File("res/VOR.png"));
            imageVORTME = ImageIO.read(new File("res/VOR-DME.png"));
            imageVORTAC = ImageIO.read(new File("res/VORTAC.png"));
        } catch (IOException e) {
            log.error("Error while loading VOR symbols!",e);
        }
    }

    @Override
    protected void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        this.displayPosition = newDisplayPosition;

        Color highLightColor = master.getAirportData().getNavaidDB().getNavaidHighlightColor(master,vor);

        if(highLightColor!=null) {
            this.color = highLightColor;
        } else {
            this.color = defaultColor;
        }

        int scale = (int)mapViewAdapter.getLogicalScale();
        scale = scale==0 ? 1 : scale;
        scale = 20 * 10/scale;
        if(scale<15) scale=15;
        if(scale>30) scale=30;

        path = new Path2D.Double();
        path.append(new Rectangle2D.Double(displayPosition.getX()- scale/2, displayPosition.getY()- scale/2, scale, scale), false);
    }

    @Override
    public void paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {

        if(vor.isHighlighted() || master.getAirportData().getNavaidDB().isPartOfRoute(master, vor) || master.getAirportData().getRadarObjectFilterState("VOR")) {
            // super.paint(g2d, mapViewAdapter);
            int scale = (int)mapViewAdapter.getLogicalScale();
            scale = scale==0 ? 1 : scale;
            scale = 20 * 10/scale;
            if(scale<15) scale=15;
            if(scale>30) scale=30;


            if (vor.isHighlighted() || master.getAirportData().getNavaidDB().isPartOfRoute(master, vor) || displayPosition != null) {
                Image image=imageVOR;
                switch (vorType) {
                case VOR:
                    image = imageVOR.getScaledInstance(scale, -1, Image.SCALE_SMOOTH);
                    break;
                case VOR_DME:
                    image = imageVORTME.getScaledInstance(scale, -1, Image.SCALE_SMOOTH);
                    break;
                case VORTAC:
                    image = imageVORTAC.getScaledInstance(scale, -1, Image.SCALE_SMOOTH);
                    break;
                }
                g2d.drawImage(image, (int) displayPosition.getX() - scale/2, (int) displayPosition.getY() - scale/2, (ImageObserver) null);
            }
        }
    }

}
