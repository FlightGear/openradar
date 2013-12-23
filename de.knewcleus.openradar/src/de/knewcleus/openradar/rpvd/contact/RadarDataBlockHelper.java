/**
 * Copyright (C) 2013 Wolfram Wagner
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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.rpvd.contact;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.rpvd.TrackDisplayState;
/**
 * This class is responsible for the layout of the data text block.
 * It takes its content from GuiRadarContact, splits it into lines
 * and determines the measures...
 *
 * @author Wolfram Wagner
 *
 */
public class RadarDataBlockHelper {

    private final GuiMasterController master;
    private final DatablockLayoutManager datablockLayoutManager;
    private final TrackDisplayState trackDisplayState;
    private volatile String text;
    private List<String> lineList = new ArrayList<String>();
    private List<Double> lineYOffsetList = new ArrayList<Double>();
    private List<Double> lineWidthList = new ArrayList<Double>();

    private volatile Rectangle2D bounds = null;
    private volatile Double lineHeight = null;

    private final double SPACE;
    /**
     * This class implements a layout that does trying to get closer to the reality. There is no current heading
     * displayed and it reduces the amount of displayed data, when no or the wrong squawk code is transmitted.
     * Contacts without a transmitter are displayed like assigned/identified contacts.
     *
     * @author Wolfram Wagner
     *
     */
    public RadarDataBlockHelper(GuiMasterController master,TrackDisplayState trackDisplayState, double space) {
        this.master=master;
        this.datablockLayoutManager = master.getAirportData().getDatablockLayoutManager();
        this.trackDisplayState=trackDisplayState;
        this.SPACE = space;
    }

    /*
     * Called as first method before displaying a text. Retrieves the text and determines the base values.
     */
    public void initializeDisplay() {
        text = datablockLayoutManager.getActiveLayout().getDataBlockText(master, trackDisplayState.getGuiContact());
        lineList.clear();
        StringTokenizer st = new StringTokenizer(text,"\n");
        while(st.hasMoreElements()) {
            lineList.add(st.nextToken().trim());
        }
        lineYOffsetList.clear();
        lineWidthList.clear();
        bounds = null;
        lineHeight = null;
    }

    public boolean isTextEmpty() {
        return text.isEmpty();
    }

    public int getLineCount(Graphics2D g2d) {
        if(bounds == null) {
            calculateBounds(g2d);
        }
        return lineList.size();
    }

    public String getLine(int index) {
        return lineList.get(index);
    }

    public double getLineYOffset(Graphics2D g2d,int index) {
        if(bounds == null) {
            calculateBounds(g2d);
        }
        return lineYOffsetList.get(index);
    }

    public double getLineHeight(Graphics2D g2d) {
        if(bounds == null) {
            calculateBounds(g2d);
        }
        return lineHeight;
    }

    public double getLineWidth(Graphics2D g2d,int index) {
        if(bounds == null) {
            calculateBounds(g2d);
        }
        return lineWidthList.get(index);
    }

    public Iterator<String> getLineIterator() {
        return lineList.iterator();
    }

    public Rectangle2D getBounds(Graphics2D g2d) {
        if(bounds == null) {
            calculateBounds(g2d);
        }
        return bounds;
    }

    private void calculateBounds(Graphics2D g2d) {
        // internal vertical zero is directly above first line
        for(String line : lineList) {
            Rectangle2D lb = g2d.getFontMetrics().getStringBounds(line,g2d);
            if(bounds==null) {
                // first line
                bounds = new Rectangle2D.Double(0,
                                                0,
                                                lb.getWidth(),
                                                lb.getHeight());
                lineHeight = lb.getHeight();
                lineYOffsetList.add(-1*lb.getY()); // y is negative
                lineWidthList.add(lb.getWidth());
            } else {
                // from second line
                bounds = new Rectangle2D.Double(0,
                                                0,
                                                bounds.getWidth()>lb.getWidth()?bounds.getWidth():lb.getWidth(),
                                                bounds.getHeight()+lb.getHeight());
                lineHeight = lineHeight>lb.getHeight()?lineHeight:lb.getHeight();
                lineYOffsetList.add(lineYOffsetList.get(lineYOffsetList.size()-1)+SPACE-lb.getY()); // y is negative
                lineWidthList.add(lb.getWidth());
            }
        }
    }

    public Color getBackgroundColor(GuiRadarContact guiContact, boolean hightlighted) {
        return datablockLayoutManager.getActiveLayout().getBackgroundColor(guiContact, hightlighted);
    }


    public Color getColor(GuiRadarContact guiContact) {
        return datablockLayoutManager.getActiveLayout().getColor(guiContact);
    }

    public Font getFont() {
        return datablockLayoutManager.getActiveLayout().getFont();
    }
}
