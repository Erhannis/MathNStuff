/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import com.erhannis.mathnstuff.utils.Factory;

/**
 * Sparsely stores elements of an array.  I'm not sure how quick it is.
 * Also, I could probably write/have written it with float coordinates, but,
 * eh.
 * @author MEwer
 */
public class SparseArray<T> implements Serializable {
  private int dims;
  private Factory<T> defaultConstructor;
  
  private HashMap<FungibleCoords, T> data = new HashMap<FungibleCoords, T>();
  
  public static class FungibleCoords implements Serializable {
    private int[] coords;
    private int hash;
    public FungibleCoords(int... coords) {
      this.coords = coords;
      int hash = 0;
      for (int i = 0; i < coords.length; i++) {
        hash *= 17;
        hash += coords[i];
      }
      this.hash = hash;
    }

    public int[] getCoords() {
      return Arrays.copyOf(coords, coords.length);
    }
    
    /**
     * Returns the actual coords object.  Please don't change it, or it'll
     * probably break something.
     * @return 
     */
    public int[] getCoordsUnsafe() {
      return coords;
    }
    
    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (!(obj instanceof FungibleCoords)) {
        return false;
      }
      if (this.coords.length != ((FungibleCoords)obj).coords.length) {
        return false;
      }
      for (int i = 0; i < this.coords.length; i++) {
        if (this.coords[i] != ((FungibleCoords)obj).coords[i]) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      return hash;
    }
  }
  
  public SparseArray(int dims, Factory<T> defaultConstructor) {
    this.dims = dims;
    this.defaultConstructor = defaultConstructor;
  }
  
  public int getDims() {
    return dims;
  }
  
  public T get(int... coords) {
    T o = data.get(new FungibleCoords(coords));
    if (o == null) {
      return defaultConstructor.construct();
    }
    return o;
  }
  
  /**
   * Puts value at coords.  I realize that "set" sounds confusingly like "put".
   * However, I wanted one that did not care about returning the previous value,
   * for slight efficiency.
   * @param value
   * @param coords 
   */
  public void set(T value, int... coords) {
    data.put(new FungibleCoords(coords), value);
  }
  
  public T put(T value, int... coords) {
    T o = data.get(new FungibleCoords(coords));
    data.put(new FungibleCoords(coords), value);
    return o;
  }
  
  public T remove(int... coords) {
    return data.remove(new FungibleCoords(coords));
  }
  
  public Iterator<T> getIterator() {
    return data.values().iterator();
  }
  
  public Iterator<Entry<FungibleCoords, T>> getEntryIterator() {
    return data.entrySet().iterator();
  }
  
  public Set<Entry<FungibleCoords, T>> getEntrySet() {
    return data.entrySet();
  }
}
