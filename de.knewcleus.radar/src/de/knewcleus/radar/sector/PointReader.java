package de.knewcleus.radar.sector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.navaids.NamedFixDB;

public abstract class PointReader {
	public void readPoints(NamedFixDB fixDB, InputStream inputStream) throws IOException {
		InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
		BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
		
		String line;
		
		while ((line=bufferedReader.readLine())!=null) {
			String elements[]=line.split("\\s+",4);
			
			double lat,lon;
			String id=elements[0];
			
			lat=Double.parseDouble(elements[1]);
			lon=Double.parseDouble(elements[2]);
			
			Position pos=new Position(lon,lat,0.0);
			
			String rest=(elements.length<4?null:elements[3]);
			
			processLine(fixDB,pos,id,rest);
		}
	}
	
	protected abstract void processLine(NamedFixDB fixDB, Position pos, String id, String rest);
}
