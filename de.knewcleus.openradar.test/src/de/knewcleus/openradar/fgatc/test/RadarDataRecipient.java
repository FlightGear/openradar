package de.knewcleus.openradar.fgatc.test;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.radardata.IRadarDataProvider;
import de.knewcleus.openradar.radardata.IRadarDataRecipient;
import de.knewcleus.openradar.radardata.ISSRData;

public class RadarDataRecipient implements IRadarDataRecipient {

	@Override
	public void acceptRadarData(IRadarDataProvider provider, IRadarDataPacket radarData) {
		final ISSRData ssrData = radarData.getSSRData();
		
		System.out.println("Packet from "+radarData.getTrackingIdentifier());
		System.out.println("\ttimestamp   "+radarData.getTimestamp());
		System.out.println("\tposition    "+radarData.getPosition());
		System.out.println("\tgroundspeed "+radarData.getCalculatedVelocity() / Units.KNOTS);
		System.out.println("\ttrue course "+radarData.getCalculatedTrueCourse());
		if (ssrData!=null) {
			System.out.println("\tmode A      active="+ssrData.hasMarkXModeACode()+" code="+ssrData.getMarkXModeACode());
			System.out.println("\tmode C      active="+ssrData.hasMarkXModeCElevation()+" elevation="+ssrData.getMarkXModeCElevation());
			System.out.println("\tSPI         active="+ssrData.hasMarkXSPI());
		} else {
			System.out.println("\t*** no SSR data***");
		}
	}

}
