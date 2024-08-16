package src;
import java.util.Random;

// Class for the instances of the runnable object/thread for the Routing Stations
public class RoutingStation implements Runnable {
    private final int stationID;
    private final ConveyorObj inputConveyor;
    private final ConveyorObj outputConveyor;
    private int workload;
    private static final Random randomSeed = new Random();

    // RoutingStation constructor method
    public RoutingStation(int stationID, ConveyorObj inputConveyor, ConveyorObj outputConveyor, int workload) {
        this.stationID = stationID;
        this.inputConveyor = inputConveyor;
        this.outputConveyor = outputConveyor;
        this.workload = workload;

        System.out.println("\tRouting Station S" + stationID + ": Input conveyor assigned to conveyor number C" + inputConveyor.getID() + ".");
        System.out.println("\tRouting Station S" + stationID + ": Output conveyor assigned to conveyor number C" + outputConveyor.getID() + ".");
        System.out.println("\tRouting Station S" + stationID + " Has Total Workload of " + workload + " Package Groups.\n");
    }

    // Method for threads to go to sleep
    public void goToSleep() {
        try {
            Thread.sleep(randomSeed.nextInt(500)); // Sleep a random time up to 500ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method for simulating Routing Station work during which the station is moving packages
    public void doWork() {
        try {
            System.out.println("\n*** Routing Station S" + stationID + ": CURRENTLY HOLDS BOTH - INPUT CONVOYOR AND OUTPUT CONVEYOR ***" +
            "\n*** Routing Station S" + stationID + ": CURRENTLY WORKING - MOVING PACKAGES. ***");
            Thread.sleep(randomSeed.nextInt(1000)); // Hold the conveyors for a random period of time to simulate work flow
            workload--;
            System.out.println("\n\t*** Routing Station S" + stationID + ": Package group completed - " + workload + " package groups remaining to move. ***\n");
            if (workload == 0) {
                System.out.println("\n*** Routing Station S" + stationID + ": going offline – work completed! BYE! ***\n");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // The run() method - this is what a Station does
    @Override
    public void run() {
        System.out.println("\n*** ROUTING STATION S" + stationID + " Coming Online - Initializing Conveyors ***\n");

        // Run the simulation on the station for its entire workload
        while (workload > 0) {
            // Loop until both locks are acquired
            boolean isInputConveyorLocked = false;
            boolean isOutputConveyorLocked = false;

            while (!(isInputConveyorLocked && isOutputConveyorLocked)) {
                isInputConveyorLocked = inputConveyor.lockConveyor(stationID);
                if (isInputConveyorLocked) {
                    System.out.println("\n\tRouting Station S" + stationID + ": Currently holds lock on input conveyor C" + inputConveyor.getID());
                    isOutputConveyorLocked = outputConveyor.lockConveyor(stationID);
                    if (isOutputConveyorLocked) {
                        System.out.println("\n\tRouting Station S" + stationID + ": Currently holds lock on output conveyor C" + outputConveyor.getID());
                        doWork();
                        
                        inputConveyor.unlockConveyor();
                        
                        System.out.println("\n\tRouting Station S" + stationID + ": Unlocks/releases input conveyor C" + inputConveyor.getID());
                        
                        outputConveyor.unlockConveyor();
                        
                        System.out.println("\n\tRouting Station S" + stationID + ": Unlocks/releases output conveyor C" + outputConveyor.getID());
                    } else {
                        System.out.println("\n\tRouting Station S" + stationID + ": UNABLE TO LOCK OUTPUT CONVEYOR C" + outputConveyor.getID() + 
                        ".\n\t\tSYNCHRONIZATION ISSUE: Station S" + outputConveyor.getLockedBy() + " currently holds the lock on output\n\t\tconveyor C" + 
                        outputConveyor.getID() + " - " + "Station S" + stationID + " releasing lock on input conveyor C" + inputConveyor.getID());

                        inputConveyor.unlockConveyor();
                        
                        System.out.println("\n\tRouting Station S" + stationID + ": Unlocks/releases input conveyor C" + inputConveyor.getID());
                        goToSleep();
                    }
                }
            }
        }

        System.out.println("\n*** ROUTING STATION S" + stationID + ": OFFLINE ***\n");
    }
}
