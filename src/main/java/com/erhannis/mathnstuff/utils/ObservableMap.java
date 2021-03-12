/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.utils;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * (Fairly) thread-safe Observable Map. Triggers callbacks when the stored value is
 * changed. All methods are synchronized, and do not
 * themselves trigger any asynchronous behavior. (If the callbacks e.g. start
 * threads or something, that's on them.)
 *
 * Throwables thrown from any callback are logged, and otherwise ignored.
 * 
 * Mechanism used to check for changes depends on checkIdentical, in constructor.
 * 
 * Note: the map is modified BEFORE the callbacks are run, so is should be ok to
 * call .get() from inside a callback.
 * 
 * @author erhannis
 */
public class ObservableMap<KEY, VAL> {
  /**
   * Gonna copy JavaFX's model, here
   */
  public static class Change<KEY, VAL> {
    public final boolean wasRemoved;
    public final boolean wasAdded;
    public final KEY key;
    public final VAL valueRemoved;
    public final VAL valueAdded;

    public Change(boolean wasRemoved, boolean wasAdded, KEY key, VAL valueRemoved, VAL valueAdded) {
      this.wasRemoved = wasRemoved;
      this.wasAdded = wasAdded;
      this.key = key;
      this.valueRemoved = valueRemoved;
      this.valueAdded = valueAdded;
    }
  }

  private HashMap<Object, Consumer<Change<KEY, VAL>>> subscriptions = new HashMap<>();
  private HashMap<KEY, VAL> map = new HashMap<>();

  private boolean checkIdentical = false;

  /**
   * Defaults `checkIdentical` to false.
   */
  public ObservableMap() {
    this(false);
  }

  /**
   * If `checkIdentical`, uses `==` to check if values have changed.  Else, uses `Objects.equals()`.
   * @param checkIdentical 
   */
  public ObservableMap(boolean checkIdentical) {
    this.checkIdentical = checkIdentical;
  }
  
  /**
   * Put value. If this adds an entry, or changes an existing entry (by != ),
   * synchronously and sequentially runs callbacks.
   *
   * @param key
   * @param newValue
   * @return previous value, or null
   */
  public synchronized VAL put(KEY key, VAL newValue) {
    VAL oldVal = null;
    if (!map.containsKey(key) || ((checkIdentical && newValue != map.get(key)) || (!checkIdentical && Objects.equals(newValue, map.get(key))))) {
      Change<KEY, VAL> change;
      if (map.containsKey(key)) {
        // So value is different
        oldVal = map.get(key);
        map.put(key, newValue);
        change = new Change<KEY, VAL>(true, true, key, oldVal, newValue);
      } else {
        map.put(key, newValue);
        change = new Change<KEY, VAL>(false, true, key, null, newValue);
      }
      for (Consumer<Change<KEY, VAL>> callback : subscriptions.values()) {
        try {
          callback.accept(change);
        } catch (Throwable t) {
          t.printStackTrace();
        }
      }
    }
    return oldVal;
  }

  /**
   * Remove key. Assuming there is an entry to be removed, synchronously and
   * sequentially runs callbacks.
   *
   * @param key
   * @return previous value, or null
   */
  public synchronized VAL remove(KEY key) {
    VAL oldVal = null;
    if (map.containsKey(key)) {
      oldVal = map.remove(key);
      Change<KEY, VAL> change = new Change<KEY, VAL>(true, false, key, oldVal, null);
      for (Consumer<Change<KEY, VAL>> callback : subscriptions.values()) {
        try {
          callback.accept(change);
        } catch (Throwable t) {
          t.printStackTrace();
        }
      }
    }
    return oldVal;
  }

  /**
   * Get a copy of the current map.
   *
   * @return
   */
  public synchronized HashMap<KEY, VAL> get() {
    return new HashMap<KEY, VAL>(map);
  }

  /**
   * Get a value from the current map.
   *
   * @param key
   * @return
   */
  public synchronized VAL get(KEY key) {
    return map.get(key);
  }

  /**
   * Checks if current map contains the given key.
   *
   * @param key
   * @return
   */
  public synchronized boolean containsKey(KEY key) {
    return map.containsKey(key);
  }

  /**
   * Subscribes to changes. Returns token for unsubscription.
   *
   * @param callback
   * @return unsubscription token
   */
  public synchronized Object subscribe(Consumer<Change<KEY, VAL>> callback) {
    Object token = new Object();
    subscriptions.put(token, callback);
    return token;
  }

  /**
   * Subscribes to changes. Synchronously calls callback with ALL CURRENT MAP
   * ENTRIES, one call for each entry. May be more than you bargained for.
   * Returns token for unsubscription.
   *
   * @param callback
   * @return unsubscription token
   */
  public synchronized Object subscribeWithGet(Consumer<Change<KEY, VAL>> callback) {
    Object token = new Object();
    subscriptions.put(token, callback);
    for (Entry<KEY, VAL> entry : map.entrySet()) {
      try {
        callback.accept(new Change<KEY, VAL>(false, true, entry.getKey(), null, entry.getValue()));
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
    return token;
  }

  /**
   * Unsubscribe, using the token returned from either subscribe method.
   *
   * @param token
   */
  public synchronized void unsubscribe(Object token) {
    subscriptions.remove(token);
  }

  public synchronized void unsubscribeAll() {
    subscriptions.clear();
  }
}
