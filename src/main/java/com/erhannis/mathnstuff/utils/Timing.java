/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.utils;

import com.erhannis.mathnstuff.Pair;
import com.erhannis.mathnstuff.Pair;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import org.apache.commons.lang3.time.StopWatch;

/**
 * Tracks and logs timing stacks.  Push at the start of a block, pop at the end.
 * Wrote this in C#, originally; ChatGPT translated most of it for me
 * @author Erhannis
 */
import java.util.LinkedList;

public class Timing {
    private LinkedList<Pair<String, StopWatch>> stack = new LinkedList<>();

    public Timing push(String name, Object o) {
        StopWatch sw = new StopWatch();
        sw.start();
        stack.addLast(new Pair<>(name, sw));

        StringBuilder s = new StringBuilder();
        s.append("--> ");
        boolean first = true;
        for (Pair<String, StopWatch> p : stack) {
            if (!first) {
                s.append(".").append(p.a);
            } else {
                s.append(p.a);
                first = false;
            }
        }
        if (o != null) {
            s.append(" ").append(o);
        }
        //THINK Permit logger?
        System.out.println(s.toString());

        return this;
    }

    public Timing push(String name) {
        return push(name, null);
    }

    public Timing log(Object o) {
        StringBuilder s = new StringBuilder();
        s.append("--- ");
        boolean first = true;
        for (Pair<String, StopWatch> p : stack) {
            if (!first) {
                s.append(".").append(p.a);
            } else {
                s.append(p.a);
                first = false;
            }
        }
        if (o != null) {
            s.append(" ").append(o);
        }
        //DITTO
        System.out.println(s.toString());

        return this;
    }

    public Timing log() {
        return log(null);
    }

    public Timing pop(Object o) {
        Pair<String, StopWatch> p0 = stack.getLast();
        p0.b.stop();

        StringBuilder s = new StringBuilder();
        s.append("<-- ");
        boolean first = true;
        for (Pair<String, StopWatch> p : stack) {
            if (!first) {
                s.append(".").append(p.a);
            } else {
                s.append(p.a);
                first = false;
            }
        }
        Duration ts = Duration.ofMillis(p0.b.getTime());
        String elapsedTime = String.format(" [%d:%02d:%02d:%02d.%03d]", ts.toDaysPart(), ts.toHoursPart(), ts.toMinutesPart(), ts.toSecondsPart(), ts.toMillisPart());
        s.append(elapsedTime);
        if (o != null) {
            s.append(" ").append(o);
        }
        System.out.println(s.toString());

        stack.removeLast();

        return this;
    }

    public Timing pop() {
        return pop(null);
    }
}
