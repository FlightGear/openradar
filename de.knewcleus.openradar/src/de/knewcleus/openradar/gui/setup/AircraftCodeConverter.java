package de.knewcleus.openradar.gui.setup;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class AircraftCodeConverter {

    private List<AircraftDefinition> aircraftList = new ArrayList<AircraftDefinition>();

    public AircraftCodeConverter() {
        BufferedReader ir = null;

        try {
            ir = new BufferedReader(new FileReader("data/aircraftCodes.txt"));
            String line = ir.readLine().trim();
            while(line!=null) {
                if(!line.startsWith("#")) {
                    StringTokenizer st = new StringTokenizer(line,",");
                    String regex = st.nextToken();
                    String icao = st.nextToken();
                    aircraftList.add(new AircraftDefinition(regex, icao));
                }
                line = ir.readLine();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if(ir!=null) {
                try {
                    ir.close();
                } catch (IOException e) {}
            }
        }
    }

    public String convert(String modelName) {
        for(AircraftDefinition ad : aircraftList) {
            if(modelName.matches(ad.regex)) {
                return ad.icao;
            }
        }
        // todo log warning
        return modelName.length()>7 ? modelName.substring(0,7):modelName;
    }

    private class AircraftDefinition {
        public AircraftDefinition(String regex, String icao) {
            this.regex=regex;
            this.icao=icao;
        }

        public final String regex;
        public final String icao;
    }
}
