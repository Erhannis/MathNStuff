/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mathnstuff.utils;

import java.io.Serializable;

/**
 *
 * @author MEwer
 */
public interface Factory<T> {
  public T construct();
  
  public static abstract class SerializableFactory<F> implements Factory<F>, Serializable {}
  public static interface Factories {
    public static class IntArrFactory extends SerializableFactory<int[]> {
      public int[] value = new int[0]; // Means the space is empty.
      @Override
      public int[] construct() {
        return value;
      }
    }
  }
}
