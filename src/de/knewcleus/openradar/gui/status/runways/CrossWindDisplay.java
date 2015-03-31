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
package de.knewcleus.openradar.gui.status.runways;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
/**
 * This class paints the cross wind display, the bar showing the strengt and direction of the cross wind fraction.
 * 
 * @author Wolfram Wagner
 */
public class CrossWindDisplay extends JComponent {

    private static final long serialVersionUID = 1L;
    private GuiRunway rw;

    public CrossWindDisplay(GuiRunway rw) {
        this.rw = rw;
        this.setPreferredSize(new Dimension(67, 10));
        this.setMinimumSize(new Dimension(67, 10));
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        float totalHeight = this.getHeight() / 2 * 2; // even value
        float totalWidth = this.getWidth() / 2 * 2 - 1; // odd value

        float spaceAround = 2;

        // the following values include the border
        float barHeight = this.getHeight() - 2 * spaceAround;
        float barWidth = this.getWidth() - 2 * spaceAround;

        // the outer rectangle
        g2d.draw(new Rectangle2D.Float(spaceAround, spaceAround, barWidth, barHeight));
        // the display bar

        // gusts
        double windSpeed = rw.getCrossWindGusts();
        if (windSpeed > 0) {
            windSpeed = Math.abs(rw.getCrossWindGusts());
            if (windSpeed > 10) {
                g2d.setColor(Color.magenta);
            } else if (windSpeed > 5) {
                g2d.setColor(new Color(170, 0, 0));
            } else {
                g2d.setColor(new Color(0, 64, 0));
            }
            windSpeed = windSpeed > 10 ? 10f : windSpeed;

            double barLength = ((barWidth - 3) / 2 / 10 * windSpeed) + 1;

            if (rw.getWindDeviation() > 0) {
                // wind from left
                Rectangle2D r2d = new Rectangle2D.Double(spaceAround + totalWidth / 2, spaceAround + 1, barLength, barHeight - 1);
                g2d.fill(r2d);
            } else {
                // wind from right
                Rectangle2D r2d = new Rectangle2D.Double(spaceAround + 1 + barWidth / 2 - barLength, spaceAround + 1, barLength, barHeight - 1);
                g2d.fill(r2d);
            }
        }

        // normal wind

        windSpeed = Math.abs(rw.getCrossWindSpeed());
        if (windSpeed > 0) {
            if (windSpeed > 10) {
                g2d.setColor(Color.red);
            } else if (windSpeed > 5) {
                g2d.setColor(Color.orange);
            } else {
                g2d.setColor(Color.green);
            }
            windSpeed = windSpeed > 10 ? 10f : windSpeed;
            double barLength = ((barWidth - 3) / 2 / 10 * windSpeed) + 1;
            if (rw.getWindDeviation() > 0) {
                // wind from right
                Rectangle2D r2d = new Rectangle2D.Double(spaceAround + totalWidth / 2, spaceAround + 1, barLength, barHeight - 1);
                g2d.fill(r2d);
            } else {
                // wind from left
                Rectangle2D r2d = new Rectangle2D.Double(spaceAround + 1 + barWidth / 2 - barLength, spaceAround + 1, barLength, barHeight - 1);
                g2d.fill(r2d);
            }
        }
        g2d.setColor(Color.GRAY);
        // -5kn
        g2d.drawLine((int) (spaceAround + totalWidth / 4), 0, (int) (spaceAround + totalWidth / 4), (int) totalHeight);
        // middle
        g2d.drawLine((int) (spaceAround + totalWidth / 2), 0, (int) (spaceAround + totalWidth / 2), (int) totalHeight);
        // +5kn
        g2d.drawLine((int) (spaceAround + 3 * totalWidth / 4), 0, (int) (spaceAround + 3 * totalWidth / 4), (int) totalHeight);
    }

}
