/**
* Copyright (C) 2015 Andreas Vogel <a-v-o@freie-ressourcen.de>
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
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne
 * die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public
 * License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 * <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.stdroutes;

import org.jdom2.Element;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class StdRouteAttributes {

    private final BasicStroke stroke;
    private final Color color;
    private final Font font;

    private Stroke stroke_backup;
    private Color color_backup;
    private Font font_backup;

    public StdRouteAttributes () {
        this.stroke = new BasicStroke(2);
        this.color = Color.gray;
        this.font = new Font("Arial", Font.PLAIN, 10);
        this.stroke_backup = null;
        this.color_backup = null;
        this.font_backup = null;
    }

    public StdRouteAttributes (StdRouteAttributes DefaultValues, String stroke, String color, String font, String fontSize) {
        this.stroke = stringToStroke(DefaultValues,stroke, "1");
        this.color = stringToColor(DefaultValues,color);
        this.font = stringToFont(DefaultValues,font,fontSize);
        this.stroke_backup = null;
        this.color_backup = null;
        this.font_backup = null;
    }

    public StdRouteAttributes (StdRouteAttributes DefaultValues, Element element) {
        this.stroke = this.stringToStroke (DefaultValues, element.getAttributeValue("stroke"), element.getAttributeValue("lineWidth"));
        this.color = this.stringToColor (DefaultValues, element.getAttributeValue("color"));
        this.font = this.stringToFont (DefaultValues, element.getAttributeValue("font"), element.getAttributeValue("fontSize"));
        this.stroke_backup = null;
        this.color_backup = null;
        this.font_backup = null;
    }

    protected BasicStroke stringToStroke (StdRouteAttributes DefaultValues, String stroke, String lineWidth) {
        // --- stroke ---
        if(stroke!=null) {
            Float width = lineWidth !=null ? Float.parseFloat(lineWidth) : DefaultValues.stroke.getLineWidth ();
            if(stroke.contains(",")) {
                // after the comma follows the linewidth of the stroke, we need to parse and remove it
                int sep = stroke.indexOf(",");
                width = Float.parseFloat(stroke.substring(sep+1));
                stroke = stroke.substring(0,sep);
            }
            if("line".equalsIgnoreCase(stroke)) {
                return new BasicStroke(width);

            } else if("dashed".equalsIgnoreCase(stroke)) {
                float[] dashPattern = { 10, 10 };
                return new BasicStroke(width, BasicStroke.CAP_BUTT,
                                       BasicStroke.JOIN_MITER, 10,
                                       dashPattern, 0);
            } else if("dots".equalsIgnoreCase(stroke)) {
                    float[] dashPattern = { width, 2 * width};
                    return new BasicStroke(width, BasicStroke.CAP_BUTT,
                                           BasicStroke.JOIN_MITER, 10,
                                           dashPattern, 0);
            } else if(stroke!=null && stroke.contains("-")) {
                // this variant allows to define own patterns like 10-5-2-5

                StringTokenizer st = new StringTokenizer(stroke,"-");
                ArrayList<Float> pattern = new ArrayList<Float>();
                while(st.hasMoreElements()) {
                    pattern.add(Float.parseFloat(st.nextToken().trim()));
                }
                float[] patternArray=new float[pattern.size()];
                for (int i = 0; i < pattern.size(); i++) {
                    Float f = pattern.get(i);
                    patternArray[i] = (f != null ? f : 0);
                }
                return new BasicStroke(width, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 10,
                        patternArray, 0);
            } else {
                return DefaultValues.stroke;
            }
        } else {
            return DefaultValues.stroke;
        }
    }

    protected Color stringToColor (StdRouteAttributes DefaultValues, String color) {
        if (color != null) {
            StringTokenizer rgb = new StringTokenizer(color, ",");
            int r = Integer.parseInt(rgb.nextToken());
            int g = Integer.parseInt(rgb.nextToken());
            int b = Integer.parseInt(rgb.nextToken());
            return new Color(r, g, b);
        } else {
            return DefaultValues.color;
        }
    }

    protected Font stringToFont(StdRouteAttributes DefaultValues, String font, String fontSize) {
        String afont = font!=null ? font : DefaultValues.font.getName ();
        int afontSize = fontSize !=null ? Math.round (Float.parseFloat(fontSize)) : DefaultValues.font.getSize ();
        return new Font(afont, Font.PLAIN, afontSize);
    }

    public synchronized void applyAttributes (Graphics2D g2d, Boolean selected) {
        this.stroke_backup = g2d.getStroke();
        this.color_backup = g2d.getColor();
        this.font_backup = g2d.getFont();
        g2d.setStroke(this.stroke);
        g2d.setColor(selected ? this.color.brighter() : this.color);
        g2d.setFont(this.font);
    }

    public synchronized void restoreAttributes (Graphics2D g2d) {
//        this.stroke_backup = g2d.getStroke();
//        this.color_backup = g2d.getColor();
//        this.font_backup = g2d.getFont();
        if(stroke_backup!=null) {
            g2d.setStroke(this.stroke_backup);
            this.stroke_backup = null;
        }
        if(stroke_backup!=null) {
            g2d.setColor(this.color_backup);
            this.color_backup = null;
        }
        if(stroke_backup!=null) {
            g2d.setFont(this.font_backup);
            this.font_backup = null;
        }
    }

    public synchronized Color getColor () {
        return this.color;
    }

}
