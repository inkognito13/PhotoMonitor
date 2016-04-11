package su.tarasov.watchdir;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Tarasov
 *         Date: 04/10/2016
 *         Time: 23:40
 */
public class EventPool {
    private List<Event> content;
    private boolean available;

    public EventPool() {
        content = new ArrayList<Event>();
        available = false;
    }

    public synchronized Event get() {
        while (available == false) {
            try {
                wait();
            }
            catch (InterruptedException e) {
            }
        }
        if (content.size()==1){
            available = false;    
        }
        notifyAll();
        return content.remove(0);
    }
    public synchronized void put(Event event) {
        content.add(event);
        available = true;
        notifyAll();
    }
}
