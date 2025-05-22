package pandodungeons.commands.Management;

import java.util.LinkedList;
import java.util.Queue;

public class CommandQueue {
    private static CommandQueue instance;
    private final Queue<String> queue;  // Cola de jugadores

    private CommandQueue() {
        queue = new LinkedList<>();
    }

    public static synchronized CommandQueue getInstance() {
        if (instance == null) {
            instance = new CommandQueue();
        }
        return instance;
    }

    public synchronized void enqueue(String playerName) {
        queue.add(playerName);
    }

    public synchronized boolean contains(String playerName) {
        return queue.contains(playerName);
    }

    public synchronized void dequeue() {
        queue.poll();
    }

    public synchronized boolean isEmpty() {
        return !queue.isEmpty();
    }

    public synchronized String peek() {
        return queue.peek();
    }

    public synchronized int getPosition(String playerName) {
        int position = 1;
        for (String name : queue) {
            if (name.equals(playerName)) {
                return position;
            }
            position++;
        }
        return -1; // Player not found in the queue
    }
}
