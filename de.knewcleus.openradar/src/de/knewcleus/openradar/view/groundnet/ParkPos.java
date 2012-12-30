package de.knewcleus.openradar.view.groundnet;

public class ParkPos extends TaxiPoint {

    private String name;
    private String type;
    private String number;
    private double heading;
    private double radius;
    private String airlineCodes;
    
    public ParkPos(String index, String type, String name, String number, String lat, String lon, String heading, String radius, String airlineCodes) {
        super(index,lat,lon,false,"parkPos");
        this.name=name;
        this.type=type;
        this.number= number;
        this.heading=Double.parseDouble(heading);
        this.radius=Double.parseDouble(radius);
        this.airlineCodes=airlineCodes;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

    public double getHeading() {
        return heading;
    }

    public double getRadius() {
        return radius;
    }

    public String getAirlineCodes() {
        return airlineCodes;
    }

}
