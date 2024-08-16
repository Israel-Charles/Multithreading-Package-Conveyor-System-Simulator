package src;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;



// Main function that controls the flow of the program
public class PackageRoutingSim {
    static int MAX_STATIONS = 10; // maximum number of routing stations and conveyors that can be configured

    public static void main(String args[]) {
        try {
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

            // Outputing the input file
            System.out.println("Configuration/Input:");
            System.out.println("\tNumber of Stations: " + numStations);

            for (int i = 0; i < numStations; i++){
                System.out.println("\tStation " + (i + 1) + ": " + configFile.get(i + 1) + " package(s)");
            }

            System.out.println("\n*** PACKAGE MANAGEMENT FACILITY SIMULATION BEGINS *** \n");

            // Create an array of ConveyorV1 objects
            ConveyorObj[] conveyorArr = new ConveyorObj[numStations];
            for (int i = 0; i < numStations; i++) {
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

            System.out.println("\n*** ALL WORKLOAD COMPLETED - SIMULATION IS SHUTTING DOWN ***");
            System.out.println("*** THE PACKAGE MANAGEMENT FACILITY SIMULATION HAS ENDED ***");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
