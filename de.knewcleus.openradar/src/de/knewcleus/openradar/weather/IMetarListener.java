package de.knewcleus.openradar.weather;

public interface IMetarListener {

    public void registerNewMetar(MetarData metar);
}
