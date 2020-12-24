/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.ToDoubleFunction;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * Designed for use in describing parameter spaces, like "x in range 0-10 step 0.5, union [3.1415]."
 * Or like, giving a large range, but with particular granularity near the ends.
 * 
 * Calling any of {iterator, forEach, spliterator, stream, dstream} finishes the set, doing some calculations.
 * Calling the other functions after that probably won't do much.
 * 
 * Calling a function does a union, by default.  Like, DblSet.ri(0,4).ri(0,0.5,1)
 * should yield [0,0.5,1,2,3,4].
 * @author erhannis
 */
public class DblSet implements Iterable<Double> {
  private transient boolean finished = false;
  private transient Stream<Double> stream;
  
  private HashSet<Double> values = new HashSet<>();

  /**
   * Returns new DblSet().  Yeah, nearly pointless, but feels like a sliiight streamlining.
   * @return 
   */
  public static DblSet n() {
    return new DblSet();
  }
  
  /**
   * range, exclusive
   * @param min inclusive
   * @param max exclusive
   * @return 
   */
  public DblSet r(double min, double max) {
    for (double d = min; d < max; d++) {
      values.add(d);
    }
    return this;
  }

  /**
   * range, inclusive
   * @param min inclusive
   * @param max inclusive
   * @return 
   */
  public DblSet ri(double min, double max) {
    for (double d = min; d <= max; d++) {
      values.add(d);
    }
    return this;
  }

  /**
   * range, exclusive
   * @param min inclusive
   * @param step step
   * @param max exclusive
   * @return 
   */
  public DblSet r(double min, double step, double max) {
    for (double d = min; d < max; d += step) {
      values.add(d);
    }
    return this;
  }

  /**
   * range, inclusive
   * @param min inclusive
   * @param max inclusive
   * @return 
   */
  public DblSet ri(double min, double step, double max) {
    for (double d = min; d <= max; d += step) {
      values.add(d);
    }
    return this;
  }
  
  /**
   * values
   * @param values
   * @return 
   */
  public DblSet v(double... values) {
    for (double d : values) {
      this.values.add(d);
    }
    return this;
  }
  
  /**
   * exclude. Removes values found in `exclude`.
   * @return 
   */
  public DblSet X(DblSet exclude) {
    this.values.removeAll(exclude.values);
    return this;
  }
  
  /**
   * union.  Adds all values in `union`.
   * @param union
   * @return 
   */
  public DblSet U(DblSet union) {
    this.values.addAll(union.values);
    return this;
  }
 
  /**
   * intersection.  Keep only the values found in `intersection`.
   * @param intersection
   * @return 
   */
  public DblSet I(DblSet intersection) {
    this.values.retainAll(intersection.values);
    return this;
  }
 
  
  private void finish() {
    stream = values.stream().sorted();
    finished = true;
  }

  @Override
  public Iterator<Double> iterator() {
    if (!finished) {finish();}
    return stream.iterator();
  }

  @Override
  public void forEach(Consumer<? super Double> action) {
    if (!finished) {finish();}
    stream.forEach(action);
  }

  @Override
  public Spliterator<Double> spliterator() {
    if (!finished) {finish();}
    return stream.spliterator();
  }
  
  public Stream<Double> stream() {
    if (!finished) {finish();}
    return stream;
  }
  
  public DoubleStream dstream() {
    if (!finished) {finish();}
    return stream.mapToDouble(new ToDoubleFunction<Double>() {
      @Override
      public double applyAsDouble(Double t) {
        return t;
      }
    });
  }
}
