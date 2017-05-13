/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Good for heartbeats, wait-to-commit functions, and dumb games.  :P
 * 
 * Once the timer gets very near 0, and then start/restart/stop are called, it
 * is not guaranteed the runnable will not run.
 * 
 * Once the runnable starts running, the timer is considered non-active.
 * 
 * I THINK there are no threading issues.  Which probably means there are one or
 * two lurking around.
 * 
 * @author erhannis
 */
public class TimeoutTimer {
  protected final long mDelay;
  
  protected final Object mNotifier = new Object();
  
  protected AtomicLong mTarget = new AtomicLong(Long.MAX_VALUE);
  //protected long mTarget = Long.MAX_VALUE;
  
  /**
   * Assumes daemon.
   * @param delay
   * @param runnable 
   */
  public TimeoutTimer(long delay, Runnable runnable) {
    this(delay, true, runnable);
  }
  
  public TimeoutTimer(long delay, boolean daemon, final Runnable runnable) {
    this.mDelay = delay;
    
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          long curTime;
          long wasTarget = mTarget.get();
          synchronized (mNotifier) {
            while ((curTime = System.currentTimeMillis()) < mTarget.get()) {
              try {
                mNotifier.wait(mTarget.get() - curTime);
              } catch (InterruptedException ex) {
                Logger.getLogger(TimeoutTimer.class.getName()).log(Level.SEVERE, null, ex);
              }
              wasTarget = mTarget.get();
            }
          }
          if (mTarget.compareAndSet(wasTarget, Long.MAX_VALUE)) {
            runnable.run();
          }
        }
      }
    });
    t.setDaemon(daemon);
    t.start();
  }

  /**
   * If timer is active, restart it.
   * If timer is not active, start it.
   */
  public synchronized void start() {
    mTarget.set(System.currentTimeMillis() + mDelay);
    synchronized (mNotifier) {
      mNotifier.notifyAll();
    }
  }
  
  /**
   * If timer is active, restart it.
   * If timer is not active, start it.
   * 
   * Just calls `start()`.
   */
  public synchronized void restart() {
    start();
  }
  
  /**
   * If timer is active, stop it.
   * If timer is not active, do nothing.
   */
  public synchronized void stop() {
    mTarget.set(Long.MAX_VALUE);
    synchronized (mNotifier) {
      mNotifier.notifyAll();
    }
  }
}
