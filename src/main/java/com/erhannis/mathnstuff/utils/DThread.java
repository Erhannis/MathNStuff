/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.utils;

/**
 *
 * @author erhannis
 */
public class DThread extends Thread {
    public DThread() {
        super();
        this.setDaemon(true);
    }
    
    public DThread(Runnable r) {
        super(r);
        this.setDaemon(true);
    }
    
    public DThread(Runnable r, String name) {
        super(r, name);
        this.setDaemon(true);
    }
    
    public DThread(boolean daemon) {
        super();
        this.setDaemon(daemon);
    }
    
    public DThread(boolean daemon, Runnable r) {
        super(r);
        this.setDaemon(daemon);
    }
    
    public DThread(boolean daemon, Runnable r, String name) {
        super(r, name);
        this.setDaemon(daemon);
    }
    
    public DThread(boolean daemon, ThreadGroup tg, Runnable r, String name, long stackSize) {
        super(tg, r, name, stackSize);
        this.setDaemon(daemon);
    }
}
