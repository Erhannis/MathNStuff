/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.utils;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * (Fairly) thread-safe Observable.  Triggers callbacks when the stored value is changed,
 * as measured by == .  All methods are synchronized, and do not themselves trigger any
 * asynchronous behavior.  (If the callbacks e.g. start threads or something, that's on them.)
 *
 * @author erhannis
 */
public class Observable<T> {
  private HashMap<Object, Consumer<T>> subscriptions = new HashMap<>();
  private T value;

  /**
   * Set value.  If the value has changed (via != ), synchronously and sequentially runs callbacks.
   * @param newValue 
   */
  public synchronized void set(T newValue) {
    if (newValue != value) {
      value = newValue;
      for (Consumer<T> callback : subscriptions.values()) {
        try {
          callback.accept(value);
        } catch (Throwable t) {
          t.printStackTrace();
        }
      }
    }
  }
  
  /**
   * Get current value.
   * @return 
   */
  public synchronized T get() {
    return value;
  }

  /**
   * Subscribes to changes.  Returns token for unsubscription.
   * @param callback
   * @return 
   */
  public synchronized Object subscribe(Consumer<T> callback) {
    Object token = new Object();
    subscriptions.put(token, callback);
    return token;
  }

  /**
   * Subscribes to changes.  Synchronously calls callback with current value.  Returns token for unsubscription.
   * @param callback
   * @return 
   */
  public synchronized Object subscribeWithGet(Consumer<T> callback) {
    Object token = new Object();
    subscriptions.put(token, callback);
    try {
      callback.accept(value);
    } catch (Throwable t) {
      t.printStackTrace();
    }
    return token;
  }
  
  public synchronized void unsubscribe(Object token) {
    subscriptions.remove(token);
  }
  
  public synchronized void unsubscribeAll() {
    subscriptions.clear();
  }
}
