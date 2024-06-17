import java.io.*;
import java.util.*;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

// Class for the instances of the lockable conveyors
class ConveyorObj {
    private final int conveyorID;
    private final ReentrantLock conveyorLock = new ReentrantLock();
    private int lockedBy = -1; // -1 indicates no station holds the lock

    // Constructor method - build a conveyor object instance
    public ConveyorObj(int conveyorID) {
        this.conveyorID = conveyorID;
    }

    // Getter function for ID
    public int getID(){
        return conveyorID;
    }
    
    // Method to get the ID of the station that holds the lock
    public int getLockedBy() {
        return lockedBy;
    }

    // Method for routing stations to acquire a conveyor object lock
    public boolean lockConveyor(int stationID) {
        if (conveyorLock.tryLock()) {
            lockedBy = stationID;
            return true;
        }
        else{
            return false;
        }
    }

    // Method for routing stations to release a conveyor lock
    public void unlockConveyor() {
        conveyorLock.unlock();
        lockedBy = -1;
    }
}

// Class for the instances of the runnable object/thread for the Routing Stations
class RoutingStation implements Runnable {
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
            System.out.println("\n* * * * * Routing Station S" + stationID + ": CURRENTLY HOLDS BOTH - INPUT CONVOYOR AND OUTPUT CONVEYOR * * * * *" +
            "\n\t\t\t\t\t* * * CURRENTLY WORKING - MOVING PACKAGES. * * *");
            Thread.sleep(randomSeed.nextInt(1000)); // Hold the conveyors for a random period of time to simulate work flow
            workload--;
            System.out.println("\n\t* * * Routing Station S" + stationID + ": Package group completed - " + workload + " package groups remaining to move. * * *\n");
            if (workload == 0) {
                System.out.println("\n# # # # Routing Station S" + stationID + ": going offline – work completed! BYE! # # # #\n");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // The run() method - this is what a Station does
    @Override
    public void run() {
        System.out.println("\n% % % % % ROUTING STATION S" + stationID + " Coming Online - Initializing Conveyors % % % % %\n");

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
                        outputConveyor.getID() + " – " + "Station S" + stationID + " releasing lock on input conveyor C" + inputConveyor.getID());

                        inputConveyor.unlockConveyor();
                        
                        System.out.println("\n\tRouting Station S" + stationID + ": Unlocks/releases input conveyor C" + inputConveyor.getID());
                        goToSleep();
                    }
                }
            }
        }

        System.out.println("\n@ @ @ @ @ @ @ ROUTING STATION S" + stationID + ": OFFLINE @ @ @ @ @ @ @\n");
    }
}

// Main function that controls the flow of the program
public class PackageRoutingSim {
    static int MAX_STATIONS = 10; // maximum number of routing stations and conveyors that can be configured

    public static void main(String args[]) {
        try {
            System.out.println("********** PACKAGE MANAGEMENT FACILITY SIMULATION BEGINS ********** \n");

            // Array list to store the integers from config.txt
            List<Integer> configFile = new ArrayList<>();

            // Create thread pool of MAX size
            ExecutorService threadController = Executors.newFixedThreadPool(MAX_STATIONS);

            // Read config.txt file into the config array
            try (BufferedReader reader = new BufferedReader(new FileReader("config.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    configFile.add(Integer.parseInt(line.trim()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Close configuration file - we're all done with it.
            // No explicit close needed here as it's handled by try-with-resources

            // Save the first integer in the config.txt file as the number of routing stations in the simulation run
            int numStations = configFile.get(0);

            // Create an array of ConveyorV1 objects
            ConveyorObj[] conveyorArr = new ConveyorObj[MAX_STATIONS];
            for (int i = 0; i < MAX_STATIONS; i++) {
                conveyorArr[i] = new ConveyorObj(i);
            }

            // Create the routing stations for this simulation run
            for (int i = 0; i < numStations; i++) {
                int workload = configFile.get(i + 1);
                ConveyorObj inputConveyor = conveyorArr[i];
                ConveyorObj outputConveyor = conveyorArr[(i + 1) % numStations];
                RoutingStation station = new RoutingStation(i, inputConveyor, outputConveyor, workload);
                threadController.execute(station);
            }

            // Application shutdown
            threadController.shutdown(); // Start the shutdown process - no new threads (tasks) will be started after this call.
            threadController.awaitTermination(1, TimeUnit.MINUTES); // Blocking technique

            System.out.println("\n********** ALL WORKLOAD COMPLETED - SIMULATION IS SHUTTING DOWN **********");
            System.out.println("********** THE PACKAGE MANAGEMENT FACILITY SIMULATION HAS ENDED **********");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
