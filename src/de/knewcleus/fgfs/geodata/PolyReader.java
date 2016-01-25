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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.fgfs.geodata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import de.knewcleus.fgfs.geodata.geometry.Point;
import de.knewcleus.fgfs.geodata.geometry.Polygon;
import de.knewcleus.fgfs.geodata.geometry.Ring;

public class PolyReader {
	protected Polygon readPolygon(BufferedReader bufferedReader) throws IOException {
		String contourCountLine=bufferedReader.readLine();
		
		int contourCount=Integer.parseInt(contourCountLine);
		if (contourCount==0)
			return null;
		
		Polygon polygon=new Polygon();
		
		for (int i=0;i<contourCount;i++) {
			String pointCountLine=bufferedReader.readLine();
			int pointCount=Integer.parseInt(pointCountLine);
			Ring contour=new Ring();
			
			for (int j=0;j<pointCount;j++) {
				String pointLine=bufferedReader.readLine();
				String[] coords=pointLine.split("\\s+");
				double x,y,z=0.0;
				
				x=Double.parseDouble(coords[0]);
				y=Double.parseDouble(coords[1]);
				
				if (coords.length>2) {
					z=Double.parseDouble(coords[2]);
				}
				
				final Point point=new Point(x,y,z);
				
				contour.add(point);
			}
			
			polygon.add(contour);
		}
		
		return polygon;
	}
	
	public void readPolygons(InputStream inputStream, List<Polygon> polygons) throws IOException {
		InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
		BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
		
		Polygon polygon;
		
		while ((polygon=readPolygon(bufferedReader))!=null) {
			polygons.add(polygon);
		}
	}
}
