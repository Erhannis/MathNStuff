/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Wrapper around a HashMap - key a list of junk to a value.
 * 
 * Note that this calls Arrays.asList(keys) whenever you call get, put, remove, or containsKey.
 * 
 * @author erhannis
 */
public class ListMap<T, U> {
  public final HashMap<List<T>, U> map;
  
  public ListMap() {
    map = new HashMap<>();
  }

  public ListMap(HashMap<List<T>, U> map) {
    this.map = map;
  }
  
  public U put(U value, T... keys) {
    return map.put(Arrays.asList(keys), value);
  }
  
  public U get(T... keys) {
    return map.get(Arrays.asList(keys));
  }

  public U remove(T... keys) {
    return map.remove(Arrays.asList(keys));
  }
  
  public boolean containsKey(T... keys) {
    return map.containsKey(Arrays.asList(keys));
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
    if (!(obj instanceof ListMap)) {
      return false;
    }
    return this.map.equals(((ListMap)obj).map);
  }
}
