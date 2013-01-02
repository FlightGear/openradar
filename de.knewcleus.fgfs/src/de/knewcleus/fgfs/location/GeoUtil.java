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
package de.knewcleus.fgfs.location;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.GeodesicUtils.GeodesicInformation;
/**
 * Wild guessing helper to determine length and heading between two points defined by geografic coordinates.
 * It is assuming that the points are close to each other and that the earth is round...
 * 
 * @author Wolfram Wagner/Ralf Gerlich
 */
public class GeoUtil {

    public static GeoUtilInfo getDistance(double lon1, double lat1, double lon2, double lat2) {

        double deltaX = lon2 - lon1;
        double deltaY = lat2 - lat1;
        double y = (lat2 + lat1)/2;
        // finally the hypothenuse
        double length = Math.sqrt(Math.pow(deltaX, 2)*Math.pow(Math.cos(Math.toRadians(y)), 2) + Math.pow(deltaY, 2)); // meter
        double lengthMeter = length * 60 * 1852 ; // meter
        
        float angle = 90f+(float)Math.toDegrees(Math.asin(-1*deltaY/length));
        if(deltaX<0) angle = 360 - angle;
        
        GeoUtilInfo result = new GeoUtilInfo(lengthMeter,angle);
        
        return result;
    }

    
    // long 100 60.05 2 0 0.00 1 2 1 15 47.61770400 007.50985600 0.00 0.00 5 4 1 1 33 47.58594200 007.53195400 1120.14 0.00 5 0 0 1
    // short 100 58.83 2 2 0.00 0 2 1 08 47.58795100 007.51691900 0.00 0.00 3 0 0 0 26 47.59164500 007.54049300 220.07 0.00 3 0 0 0
    public static void main(String[] args) {
        double x1 = 7.509856;
        double y1 = 47.61770400;
        double x2 = 7.5319540;
        double y2 = 47.58594200;
        GeoUtilInfo result1 = getDistance(x1, y1, x2, y2);
        System.out.println(result1.length / Units.FT+"\" "+result1.angle+"°");
        GeodesicUtils geodesicUtils = new GeodesicUtils(Ellipsoid.WGS84);
        GeodesicInformation result = geodesicUtils.inverse(x1, y1, x2, y2);
        System.out.println(">" + result.getLength() / Units.FT);

        x1 = 7.51691900;
        y1 = 47.58795100;
        x2 = 7.54049300;
        y2 = 47.59164500;
        result1 = getDistance(x1, y1, x2, y2);
        System.out.println(result1.length / Units.FT+"\" "+result1.angle+"°");
        result = geodesicUtils.inverse(x1, y1, x2, y2);
        System.out.println(">" + result.getLength() / Units.FT);

    }

    
    public static class GeoUtilInfo {
        public final double length;
        public final float angle;
        
        public GeoUtilInfo(double length, float angle) {
            this.length = length;
            this.angle = angle;
        }
    }
}
