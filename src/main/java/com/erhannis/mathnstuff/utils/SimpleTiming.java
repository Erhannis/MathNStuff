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
public class SimpleTiming {
    public long startTime;
    public long stopTime;
    
    public SimpleTiming() {
        start();
    }
    
    public void start() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Stops the timer and returns elapsed milliseconds.<br/>
     * ( Sets stopTime and returns (stopTime - startTime). )
     * @return 
     */
    public long stop() {
        stopTime = System.currentTimeMillis();
        return stopTime - startTime;
    }
    
    /**
     * Returns (stopTime - startTime), without altering them
     * @return 
     */
    public long peek() {
        return stopTime - startTime;
    }
    
    public long lap() {
        stopTime = System.currentTimeMillis();
        try {
            return stopTime - startTime;
        } finally {
            // I wonder if this actually saves any cycles, or if it's just pointlessly arcane?
            startTime = stopTime;
        }
    }
}
