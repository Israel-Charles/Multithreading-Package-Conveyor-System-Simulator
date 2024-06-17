package src;
import java.util.concurrent.locks.*;

// Class for the instances of the lockable conveyors
public class ConveyorObj {
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
