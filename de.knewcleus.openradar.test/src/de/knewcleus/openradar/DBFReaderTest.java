package de.knewcleus.openradar;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import de.knewcleus.fgfs.geodata.DBFReader;
import de.knewcleus.fgfs.geodata.DataFormatException;

public class DBFReaderTest {
	public static void main(String[] args) throws IOException, DataFormatException {
		for (String filename: args) {
			FileInputStream fileInputStream=new FileInputStream(filename);
			DataInputStream dataInputStream=new DataInputStream(fileInputStream);
			DBFReader reader=new DBFReader(dataInputStream);
			
			for (int i=0;i<reader.getRecordCount();i++) {
				Object[] row=reader.readRow(i);
				System.out.println(Arrays.toString(row));
			}
		}
	}

}
