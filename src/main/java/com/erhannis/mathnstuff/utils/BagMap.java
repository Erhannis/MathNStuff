/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.utils;

import java.util.HashMap;

/**
 * Wrapper around a HashMap - key a list of junk to a value.
 * @author erhannis
 */
public class BagMap<T, U> {
  public final HashMap<T[], U> map = new HashMap<>();
  
  public U put(U value, T... keys) {
    return map.put(keys, value);
  }
  
  public U get(T... keys) {
    return map.get(keys);
  }

  @Override
  public int hashCode() {
    return this.map.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof BagMap)) {
      return false;
    }
    return this.map.equals(((BagMap)obj).map);
  }
}
