package cz.muni.fi.openradar.vessels.hla;

import certi.rti.impl.CertiLogicalTime;
import certi.rti.impl.CertiRtiAmbassador;
import de.knewcleus.openradar.vessels.PositionUpdate;
import de.knewcleus.openradar.vessels.SSRMode;
import hla.rti.*;
import hla.rti.jlc.*;
import java.io.File;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsnDataFetcher extends Thread {

    private AttributeHandleSet attributes;
    int aircraftTypeHandle;
    int latitudeHandle;
    int longitudeHandle;
    int altitudeHandle;
    int trueHeadingHandle;
    int pitchHandle;
    int rollHandle;
    HlaRegistry hlaRegistry;
    Set<PositionUpdate> positionUpdates = new HashSet<PositionUpdate>();

    public AsnDataFetcher(HlaRegistry hlaRegistry) {
        this.hlaRegistry = hlaRegistry;
    }

    @Override
    public void run() {
        //////////////////////////////
        // 1. get a link to the RTI //
        //////////////////////////////
        System.out.println("1. get a link to the RTI");

        RtiFactory factory;
        RTIambassador rtia = null;
        try {
            factory = RtiFactoryFactory.getRtiFactory();
            rtia = factory.createRtiAmbassador();
        } catch (RTIinternalError ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("   done");

        ////////////////////////////
        // 2. create a federation //
        ////////////////////////////
        System.out.println("2. create a federation");

        try {
            File fom = new File("AviationSimNet-v3.1.fed");
            rtia.createFederationExecution("VirtualAir", fom.toURI().toURL());
        } catch (CouldNotOpenFED ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ErrorReadingFED ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RTIinternalError ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConcurrentAccessAttempted ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FederationExecutionAlreadyExists ex) {
            //System.out.println("2. federation already exist");    
        }

        System.out.println("   done");

        ////////////////////////////
        // 3. join the federation //
        ////////////////////////////
        System.out.println("3. join the federation");

        FederateAmbassador mya = new MyFederateAmbassador();
        try {
            rtia.joinFederationExecution("java-01", "VirtualAir", mya);
        } catch (FederateAlreadyExecutionMember ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FederationExecutionDoesNotExist ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SaveInProgress ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RestoreInProgress ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RTIinternalError ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConcurrentAccessAttempted ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("   done");
        try {
            ((MyFederateAmbassador) mya).initialize(rtia, positionUpdates);
        } catch (NameNotFound ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FederateNotExecutionMember ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RTIinternalError ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ObjectClassNotDefined ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AttributeNotDefined ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SaveInProgress ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RestoreInProgress ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConcurrentAccessAttempted ex) {
            Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {
            try {
                positionUpdates.clear();
                ((CertiRtiAmbassador) rtia).tick(1.0, 1.0);
                hlaRegistry.fireRadarDataUpdated(positionUpdates);
            } catch (RTIinternalError ex) {
                Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ConcurrentAccessAttempted ex) {
                Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /*
        try {
        rtia.resignFederationExecution(ResignAction.DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES);
        } catch (FederateOwnsAttributes ex) {
        Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FederateNotExecutionMember ex) {
        Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidResignAction ex) {
        Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RTIinternalError ex) {
        Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConcurrentAccessAttempted ex) {
        Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
        try {
        rtia.destroyFederationExecution("VirtualAir");
        } catch (RTIinternalError ex) {
        Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ConcurrentAccessAttempted ex) {
        Logger.getLogger(AsnDataFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }
        } catch (FederatesCurrentlyJoined ex) {
        } catch (FederationExecutionDoesNotExist ex) {
        }
         */
    }

    private class MyFederateAmbassador extends NullFederateAmbassador {

        Set<PositionUpdate> positionUpdates;
        RTIambassador rtia;

        public void initialize(RTIambassador rtia, Set<PositionUpdate> positionUpdates) throws NameNotFound, FederateNotExecutionMember, RTIinternalError, ObjectClassNotDefined, AttributeNotDefined, SaveInProgress, RestoreInProgress, ConcurrentAccessAttempted {
            this.rtia = rtia;
            this.positionUpdates = positionUpdates;

            int aircraftHandle = rtia.getObjectClassHandle("aircraft");
            aircraftTypeHandle = rtia.getAttributeHandle("aircraftType", aircraftHandle);
            latitudeHandle = rtia.getAttributeHandle("latitude", aircraftHandle);
            longitudeHandle = rtia.getAttributeHandle("longitude", aircraftHandle);
            altitudeHandle = rtia.getAttributeHandle("altitudeMSL", aircraftHandle);
            trueHeadingHandle = rtia.getAttributeHandle("trueHeading", aircraftHandle);
            pitchHandle = rtia.getAttributeHandle("pitch", aircraftHandle);
            rollHandle = rtia.getAttributeHandle("roll", aircraftHandle);

            attributes = RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
            attributes.add(aircraftTypeHandle);
            attributes.add(latitudeHandle);
            attributes.add(longitudeHandle);
            attributes.add(altitudeHandle);
            attributes.add(trueHeadingHandle);
            attributes.add(pitchHandle);
            attributes.add(rollHandle);

            rtia.subscribeObjectClassAttributes(aircraftHandle, attributes);
        }

        @Override
        public void discoverObjectInstance(int theObject, int theObjectClass, String objectName) throws CouldNotDiscover, ObjectClassNotKnown, FederateInternalError {
            System.out.println("DISCOVER " + objectName);
        }

        @Override
        public void removeObjectInstance(int theObject, byte[] userSuppliedTag) throws ObjectNotKnown, FederateInternalError {
            System.out.println("REMOVE");
        }

        @Override
        public void reflectAttributeValues(int theObject, ReflectedAttributes theAttributes, byte[] userSuppliedTag, LogicalTime theTime, EventRetractionHandle retractionHandle) throws ObjectNotKnown, AttributeNotKnown, FederateOwnsAttributes, InvalidFederationTime, FederateInternalError {
            System.out.println("REFLECT " + ((CertiLogicalTime) theTime).getTime());

            double longitude = 0;
            double latitude = 0;
            double trueCourse = 0;
            double altitude = 0;

            try {
                for (int i = 0; i < theAttributes.size(); i++) {
                    if (theAttributes.getAttributeHandle(i) == aircraftTypeHandle) {
                        System.out.println("Aircraft type: " + EncodingHelpers.decodeString(theAttributes.getValue(i)));
                    } else if (theAttributes.getAttributeHandle(i) == latitudeHandle) {
                        System.out.println("Latitude: " + EncodingHelpers.decodeDouble(theAttributes.getValue(i)));
                        latitude = EncodingHelpers.decodeDouble(theAttributes.getValue(i));
                    } else if (theAttributes.getAttributeHandle(i) == longitudeHandle) {
                        System.out.println("Longitude: " + EncodingHelpers.decodeDouble(theAttributes.getValue(i)));
                        longitude = EncodingHelpers.decodeDouble(theAttributes.getValue(i));
                    } else if (theAttributes.getAttributeHandle(i) == altitudeHandle) {
                        System.out.println("Altitude: " + EncodingHelpers.decodeFloat(theAttributes.getValue(i)));
                        altitude = EncodingHelpers.decodeFloat(theAttributes.getValue(i));
                    } else if (theAttributes.getAttributeHandle(i) == trueHeadingHandle) {
                        System.out.println("True heading: " + EncodingHelpers.decodeFloat(theAttributes.getValue(i)));
                        trueCourse = EncodingHelpers.decodeFloat(theAttributes.getValue(i));
                    } else if (theAttributes.getAttributeHandle(i) == pitchHandle) {
                        System.out.println("Pitch: " + EncodingHelpers.decodeFloat(theAttributes.getValue(i)));
                    } else if (theAttributes.getAttributeHandle(i) == rollHandle) {
                        System.out.println("Roll:" + EncodingHelpers.decodeFloat(theAttributes.getValue(i)));
                    }
                }

                positionUpdates.add(new PositionUpdate(theObject, ((CertiLogicalTime) theTime).getTime(), longitude, latitude, 0, trueCourse, SSRMode.MODES, rtia.getObjectInstanceName(theObject), altitude));
            } catch (ArrayIndexOutOfBounds ex) {
            } catch (FederateNotExecutionMember ex) {
            } catch (RTIinternalError ex) {
            }
        }
    }
}
