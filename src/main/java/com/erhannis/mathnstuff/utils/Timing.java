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
public class Timing {
    public long startTime;
    public long stopTime;
    
    public Timing() {
        start();
    }
    
    public void start() {
        startTime = System.currentTimeMillis();
    }
    
    public long stop() {
        stopTime = System.currentTimeMillis();
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
