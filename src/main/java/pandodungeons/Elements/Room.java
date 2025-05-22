package pandodungeons.Elements;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import org.bukkit.Location;

public class Room {
    private final Location location;
    private final int roomNumber;
    private boolean isCleared;
    private boolean entered = false;
    private Clipboard clipboard;

    public Room(Location location, int roomNumber, boolean isCleared) {
        this.location = location;
        this.roomNumber = roomNumber;
        this.isCleared = isCleared;
    }

    public Location getLocation() {
        return location;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setClipboard(Clipboard clipboard){
        this.clipboard = clipboard;
    }

    public Clipboard getClipBoard(){
        return clipboard;
    }

    public boolean isCleared() {
        return isCleared;
    }

    public void setCleared(boolean cleared) {
        isCleared = cleared;
    }
    public boolean isEntered(){
        return entered;
    }
    public void enter(){
        entered = true;
    }
}