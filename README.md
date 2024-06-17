# Multithreading-Package-Simulator

### Overview
The Package Management Facility Simulation is a multi-threaded Java application designed to model an automated package routing system. This simulation involves multiple routing stations and conveyors, demonstrating how packages are moved efficiently through a facility while managing synchronization and avoiding deadlocks. The application showcases key concepts in concurrent programming, such as thread management, manual synchronization, and resource sharing.

### Description:
An example Simulation could consists of five routing stations (S0 to S4) and five conveyors (C0 to C4). Each station has an input and output conveyor for moving packages from one part of the facility to another. Two adjacent stations share a conveyor, and access to these shared conveyors must be strictly regulated to prevent synchronization issues. The direction of package flow is not important in this simulation.

Each routing station performs the following tasks:

- Acquires exclusive access to its input and output conveyors.
- Moves a group of packages from the input conveyor to the output conveyor for a random amount of time.
- Releases the locks on the conveyors after moving the packages.
- Sleeps for a random period before repeating the process.
- The simulation reads configuration information from a text file (config.txt), which specifies the number of routing stations and their workloads (number of package groups to be moved). The maximum number of stations is set to 10. The simulation outputs various messages to indicate the status and actions of each routing station.

### Input Specification:
The config.txt file contains:
- The number of routing stations.
- The workload for each station (number of package groups to be moved).

### Output Specification:
The simulator outputs the following messages depending on what is currently hapenning with the program:

- "Routing Station Sx: Input conveyor assigned to conveyor number Cn."
- "Routing Station Sx: Output conveyor assigned to conveyor number Cm."
- "Routing Station Sx Has Total Workload of n Package Groups."
- "Routing Station Sx: Currently holds lock on input conveyor Cn."
- "Routing Station Sx: Currently holds lock on output conveyor Cm."
- "Routing Station Sx: Unlocks/releases input conveyor Cn."
- "Routing Station Sx: Unlocks/releases output conveyor Cm."
- "Routing Station Sx: UNABLE TO LOCK OUTPUT CONVEYOR Cn. SYNCHRONIZATION ISSUE: Station Sy currently holds the lock on output conveyor Cn – Station Sx releasing lock on input conveyor Cm."
- "# # # # Routing Station Sx: going offline – work completed! BYE! # # # #"
- "* * * * * Routing Station Sx: CURRENTLY WORKING --  HOLDS BOTH - INPUT CONVOYOR AND OUTPUT CONVEYOR * * * * *"
- "Routing Station Sx: Package group completed - n package groups remaining to move."

### Key Classes:
- PackageRoutingSim: The main class that initializes the simulation, reads the configuration file, and starts the routing station threads.
- ConveyorObj: Represents a conveyor with a ReentrantLock to manage access. Includes methods to lock and unlock the conveyor and track which station holds the lock.
- RoutingStation: Implements the Runnable interface to simulate the behavior of a routing station. Manages the station's workload, acquires and releases locks on conveyors, and outputs status messages.

### Project Structure:
- PackageRoutingSim.java: Main class to start the simulation.
- ConveyorObj.java: Class representing the conveyors.
- RoutingStation.java: Class representing the routing stations.

### Execution:
To run the simulation:

- Compile the Java files.
- Ensure the config.txt file is in the same directory as the compiled classes.
- Run the PackageRoutingSim class.

##### *This simulation provides a practical example of concurrent programming in Java, demonstrating how to manage multiple threads and synchronize their access to shared resources efficiently.*
