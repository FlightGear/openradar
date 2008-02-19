package de.knewcleus.radar.sector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import de.knewcleus.fgfs.location.Position;

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
			// All contours other than the first are holes
			PolygonContour contour=new PolygonContour(i>0);
			
			for (int j=0;j<pointCount;j++) {
				String pointLine=bufferedReader.readLine();
				String[] coords=pointLine.split("\\s+");
				double x,y,z=0.0;
				
				x=Double.parseDouble(coords[0]);
				y=Double.parseDouble(coords[1]);
				
				if (coords.length>2) {
					z=Double.parseDouble(coords[2]);
				}
				
				contour.addPoint(new Position(x,y,z));
			}
			
			polygon.addContour(contour);
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
