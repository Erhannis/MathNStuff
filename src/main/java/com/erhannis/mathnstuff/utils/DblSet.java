/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.PrimitiveIterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.ToDoubleFunction;

/**
 * Designed for use in describing parameter spaces, like "x in range 0-10 step 0.5, union [3.1415]."
 * Or like, giving a large range, but with particular granularity near the ends.
 * 
 * Calling nextDouble() or hasNext() finishes the set, doing some calculations.
 * Calling the other functions after that probably won't do much.
 * 
 * Calling a function does a union, by default.  Like, DblSet.ri(0,4).ri(0,0.5,1)
 * should yield [0,0.5,1,2,3,4].
 * @author erhannis
 */
public class DblSet implements PrimitiveIterator.OfDouble {
  private transient boolean finished = false;
  private transient PrimitiveIterator.OfDouble iterator;
  
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
    iterator = values.stream().mapToDouble(new ToDoubleFunction<Double>() {
      @Override
      public double applyAsDouble(Double t) {
        return t;
      }
    }).sorted().iterator();
    finished = true;
  }
  
  @Override
  public double nextDouble() {
    if (!finished) {finish();}
    return iterator.nextDouble();
  }

  @Override
  public boolean hasNext() {
    if (!finished) {finish();}
    return iterator.hasNext();
  }

  @Override
  public void forEachRemaining(DoubleConsumer action) {
    if (!finished) {finish();}
    iterator.forEachRemaining(action);
  }

  @Override
  public Double next() {
    if (!finished) {finish();}
    return iterator.next();
  }

  @Override
  public void forEachRemaining(Consumer<? super Double> action) {
    if (!finished) {finish();}
    iterator.forEachRemaining(action);
  }

  @Override
  public void remove() {
    if (!finished) {finish();}
    //TODO Uh.  Shrug?  Could remove from `values`, maybe....
    iterator.remove();
  }
}
