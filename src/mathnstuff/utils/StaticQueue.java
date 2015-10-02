/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mathnstuff.utils;

/**
 * This is related to a queue - it has a front and an end, and you insert elements at the front.
 * However, it is of fixed length, and you can (officially) only see the element at the back,
 * or null if you haven't added that many elements, or if you previously added null.  The use case
 * I made this for is: you create it with 5 spots, and add a time whenever a connection attempt is
 * made.  You can check the last element of the StaticQueue to know whether, for example, 5
 * connections have been attempted in the last 5 seconds, and stop reconnecting.
 * 
 * Note that I leave the variables public, though they officially ought not to be accessed.  It
 * probably won't do anything unexpected, though. 
 * @author mewer
 *
 */
public class StaticQueue<T> {
    public Object[] data;
    public int pos;
    
    public StaticQueue(int size) {
        data = new Object[size];
        pos = 0;
    }
    
    /**
     * Adds an item to the queue, checking for invalid data array size and doing nothing if
     * it is, indeed, invalid.
     * @param item
     */
    public void push(T item) {
        if (data != null && data.length > 0) {
            pos = (pos + 1) % data.length;
            data[pos] = item;
        }
    }

    public void pushUnchecked(T item) {
        pos = (pos + 1) % data.length;
        data[pos] = item;
    }
    
    /**
     * Shows the item at the end of the queue, checking for invalid data array size and returning
     * null if it is, indeed, invalid.
     * @param item
     */
    public T get() {
        if (data != null && data.length > 0) {
            return (T)data[(pos + 1) % data.length];
        } else {
            return null;
        }
    }
    
    public T getUnchecked() {
        return (T)data[(pos + 1) % data.length];
    }
}