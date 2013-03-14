/**
 *  GasStation Event class
 *  This class provides methods to log  gas station actions 
 *  and provides a convenient sleep function for calling threads.
 *  Written for COMP2310 Assignment 1 by Peter Strazdins RSCS ANU 08/12
 */

import java.util.Random;

public class GasStationEvent {
    public final int MaxEventDef = 30;
    public final String MaxEventName = "GasStationMaxEvent";
    protected int maxEvent;            // number of events for the simulation
    protected int nEvents;             // count of event

    public final String EventSeedName = "GasStationEventSeed";
    protected long randomSeed;         // random number seed and generator 
    protected Random r;                // use to calculate sleep times 
    protected int nextSleepTime;       // between events, also seed is 
                                       // available for client classes
    public final String SleepFactorName = "GasStationSleepFactor";
    protected final int SleepFactorDef = 10; // ms
    protected final int sleepFactor;   // sleep times in 0..sleepFactor-1

    protected int T, N, M, NS;         // station parameters

    GasStationEvent(int vT, int vN, int vM, int vNS) {
        String envVarVal;

        T = vT; N = vN; M = vM; NS = vNS;

		nEvents = 0;

        envVarVal = System.getenv(EventSeedName); 
        if (envVarVal == null) {
	    	randomSeed = System.nanoTime() % 1000;
		} else {
	    	randomSeed = Integer.parseInt(envVarVal);
		}
		
		r = new Random(randomSeed);        
        envVarVal = System.getenv(MaxEventName);
        
        if (envVarVal == null) {
	    	maxEvent = MaxEventDef;
		} else {
	    	maxEvent = Integer.parseInt(envVarVal);
	    }

        envVarVal = System.getenv(SleepFactorName);
        
        if (envVarVal == null) {
	    	sleepFactor = SleepFactorDef;
		} else {
	    	sleepFactor = Integer.parseInt(envVarVal);
	    }

		System.out.println("GasStation: tank sz " + T + ", " + N +
                           " cars (" + NS + " std), " + M + " pumps; " +
                           maxEvent + " evts, seed " +
                           randomSeed + ", mx sleep " + 
                           sleepFactor + "ms");
    }

    // return same random number seed that this object uses for clients
    public long getSimRandomSeed() {
	return randomSeed;
    }

    // sleep for some random time in 0..SleepFactor-1 ms
    synchronized public void sleepEvents( ) {
        int nextSleepTime;
	    nextSleepTime = (int) (r.nextDouble() * sleepFactor);
	    //	    System.out.println(nextSleepTime);
	    if (nextSleepTime > 0) 
		try {
		    Thread.sleep(nextSleepTime);
		} catch (InterruptedException ie) {};
    }


    // log respective gas station actions with their parameters 
    // in a consistent format
    synchronized public void logPay (int cId, int amount) {
       System.out.println("cust. " + cId + ": pays for:     " + amount + " units");
       checkEvents();
    }

    synchronized public void logStart(int cId, int amount, int pumpId) {
		System.out.println("cust. " + cId + ": pump started: " + amount + " units, @pump " + pumpId);
		checkEvents();
    }

    synchronized public void logGas(int cId, int amount, int pumpId) {
		System.out.println("cust. " + cId + ": receives gas: " + amount + " units, @pump " + pumpId);
		checkEvents();
    }

    synchronized public void logDrive(int cId) {
       System.out.println("cust. " + cId + ": drives ");
       checkEvents();
    }

    synchronized public void logRequest(int cId) {
	synchronized (this) {
	    System.out.println("cust. " + cId + ": requests to pay");
	    checkEvents();
	}
		sleepEvents();
    }

   synchronized  public void logOtherEvent(String eventDescription) {
       System.out.println(eventDescription);
       checkEvents();
    }


   // check for termination of events
   private void checkEvents() {
       nEvents++;
       if (nEvents == maxEvent) {
	   		System.out.println("GasStation simulation terminated after " + nEvents + " events");
	   		System.exit(0);
       }
   }

} //GasStationEvent
