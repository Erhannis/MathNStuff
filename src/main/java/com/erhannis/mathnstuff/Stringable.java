/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff;

//import java.util.Random;

/**
 *
 * @author erhannis
 */
public class Stringable<T> {
    public final T val;
    public final String name;

    public Stringable(T val, String name) {
        this.val = val;
        this.name = name;
    }
    
    @Override
    public String toString() {
//        String color = String.format("%06x", RANDOM.nextInt(0x1000000));
//        return "<html><p style='width: 100%; background-color: #" + color + "'>" + name + "</p></html>";
        return name;
    }
}
