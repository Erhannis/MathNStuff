/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.utils;

import java.io.Serializable;

/**
 * The T is optional; a factory is allowed to discard (or not require) input.
 * ...But it was handy to ALLOW it.
 * 
 * I'm still a little conflicted about it.
 * 
 * @author MEwer
 */
public interface Factory<T, U> {
  public U construct(T input);
  
  public static abstract class SerializableFactory<F> implements Factory<Object, F>, Serializable {}
  public static interface Factories {
    public static class IntArrFactory extends SerializableFactory<int[]> {
      public int[] value = new int[0]; // Means the space is empty.
      @Override
      public int[] construct(Object ignored) {
        return value;
      }
    }
  }
}
