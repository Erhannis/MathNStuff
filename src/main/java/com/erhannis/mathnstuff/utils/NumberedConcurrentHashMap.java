/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Basically for tracking requests, where you can't pass objects directly.<br/>
 * Looking at you, Android.<br/>
 * Take a ticket, pass the number with your request, and the fulfiller can fetch
 * your data from the map.
 * Thread safe, as much as this likely can be, anyway.<br/>
 * //TODO Eh...maybe I shouldn't expose most of the Map functionality directly
 * 
 * @author erhannis
 */
public class NumberedConcurrentHashMap<T> extends ConcurrentHashMap<Integer, T> {
    private final AtomicInteger numbers = new AtomicInteger(0);
    
    public int takeTicket(T t) {
        int n = numbers.getAndIncrement();
        put(n, t);
        return n;
    }
}
