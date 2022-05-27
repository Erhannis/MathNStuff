/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper around a HashMap - key a set of junk to a value.
 * 
 * Note that this creates a new HashSet whenever you call get, put, remove, or containsKey.
 * 
 * @author erhannis
 */
public class BagMap<T, U> {
  public final HashMap<Set<T>, U> map;
  
  public BagMap() {
    map = new HashMap<>();
  }

  public BagMap(HashMap<Set<T>, U> map) {
    this.map = map;
  }
  
  public U put(U value, T... keys) {
    return map.put(new HashSet<T>(Arrays.asList(keys)), value);
  }
  
  public U get(T... keys) {
    return map.get(new HashSet<T>(Arrays.asList(keys)));
  }

  public U remove(T... keys) {
    return map.remove(new HashSet<T>(Arrays.asList(keys)));
  }
  
  public boolean containsKey(T... keys) {
    return map.containsKey(new HashSet<T>(Arrays.asList(keys)));
  }

  public boolean containsValue(U value) {
    return map.containsValue(value);
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
