import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

class RoutingStation implements Runnable {
    private static final Random random = new Random();

    private final int id;
    private final int inputConveyor;
    private final int outputConveyor;
    private int workload;
    private final ReentrantLock inputLock;
    private final ReentrantLock outputLock;

    public RoutingStation(int id, int inputConveyor, int outputConveyor, int workload, ReentrantLock[] locks) {
        this.id = id;
        this.inputConveyor = inputConveyor;
        this.outputConveyor = outputConveyor;
        this.workload = workload;
        this.inputLock = locks[inputConveyor];
        this.outputLock = locks[outputConveyor];
        print("Has Total Workload of " + workload + " Package Groups.");
    }

    @Override
    public void run() {
        while (workload > 0) {
            try {
                // Attempt to lock the input conveyor
                inputLock.lock();
                print("Currently holds lock on input conveyor C" + inputConveyor);

                // Attempt to lock the output conveyor
                if (outputLock.tryLock()) {
                    try {
                        print("Currently holds lock on output conveyor C" + outputConveyor);
                        print("* * CURRENTLY HARD AT WORK MOVING PACKAGES. * *");
                        // Simulate moving packages
                        Thread.sleep(random.nextInt(1000));

                        workload--;
                        print("Package group completed - " + workload + " package groups remaining to move.");

                    } finally {
                        outputLock.unlock();
                        print("Unlocks/releases output conveyor C" + outputConveyor);
                    }
                } else {
                    print("UNABLE TO LOCK OUTPUT CONVEYOR C" + outputConveyor + ". SYNCHRONIZATION ISSUE: Another station currently holds the lock on output conveyor C" + outputConveyor + " – releasing lock on input conveyor C" + inputConveyor);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                inputLock.unlock();
                print("Unlocks/releases input conveyor C" + inputConveyor);
            }

            // Sleep for a random time before the next workload
            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        print("# # going offline – work completed! BYE! # #");
    }

    private void print(String message) {
        System.out.println("Routing Station S" + id + ": " + message);
    }
}

public class PackageRoutingSimV0 {
    private static final int MAX_STATIONS = 10;

    public static void main(String[] args) {
        ReentrantLock[] conveyorLocks = new ReentrantLock[MAX_STATIONS];
        for (int i = 0; i < MAX_STATIONS; i++) {
            conveyorLocks[i] = new ReentrantLock();
        }

        ExecutorService executorService = Executors.newFixedThreadPool(MAX_STATIONS);

        try (BufferedReader reader = new BufferedReader(new FileReader("config.txt"))) {
            int numberOfStations = Integer.parseInt(reader.readLine().trim());
            for (int i = 0; i < numberOfStations; i++) {
                int workload = Integer.parseInt(reader.readLine().trim());
                int inputConveyor = i;
                int outputConveyor = (i + 1) % numberOfStations;
                RoutingStation station = new RoutingStation(i, inputConveyor, outputConveyor, workload, conveyorLocks);
                executorService.submit(station);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
