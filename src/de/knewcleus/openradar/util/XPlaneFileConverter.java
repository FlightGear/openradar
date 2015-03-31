package de.knewcleus.openradar.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.fgfs.navdata.xplane.FieldIterator;

public class XPlaneFileConverter {

    public static void main(String[] args) {
        BufferedReader br = null;
        BufferedWriter bw = null;
        
        try {
            List<String> removeList = new ArrayList<String>();
            // remove taxiways
            removeList.add("110");
            removeList.add("111");
            removeList.add("112");
            removeList.add("113");
            removeList.add("114");
            removeList.add("115");
            removeList.add("116");
            removeList.add("117");

            
            br = new BufferedReader(new InputStreamReader(new FileInputStream("apt.dat"),"ISO-8859-1"));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("apt.short.dat"),"ISO-8859-1"));

            String nextLine = br.readLine();
            while(nextLine!=null) {
                if(nextLine.isEmpty()) {
                    bw.newLine();
                } else {
                    FieldIterator fieldIterator = new FieldIterator(nextLine);
                    String recordTypeString = fieldIterator.next();
                    if(!removeList.contains(recordTypeString) ) {
                        bw.write(nextLine);
                        bw.newLine();
                    }
                }
                nextLine = br.readLine();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(br!=null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(bw!=null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
    }
    
}
