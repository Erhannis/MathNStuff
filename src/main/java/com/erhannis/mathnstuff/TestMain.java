/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff;

import com.erhannis.mathnstuff.utils.Timing;

/**
 * Just some test code
 *
 * @author Erhannis
 */
public class TestMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        Timing t = new Timing();
        t.push("start");
        Thread.sleep(2000);
        t.push("loop");
        for (int i = 0; i < 3; i++) {
            t.push(""+i);
            Thread.sleep(1000);
            t.pop();
        }
        t.pop();
        Thread.sleep(500);
        t.pop();
    }

}
