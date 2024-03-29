/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff;

import com.erhannis.mathnstuff.utils.Factory;
import java.util.HashMap;

/**
 * HashMap, but when you call "get" and it isn't in the map, a new one is created
 * via the Factory and stored in the map, then returned.
 * 
 * Not thread safe.
 * 
 * @author MEwer
 */
public class FactoryHashMap<T, U> extends HashMap<T, U> {
  private Factory<T, U> defaultConstructor;
  
  public FactoryHashMap(Factory<T, U> defaultConstructor) {
    this.defaultConstructor = defaultConstructor;
  }

  /**
   * If there's a U for key, return that.  Otherwise, create a new U with the
   * constructor, store it for key, and return it.
   * Warning: will throw a ClassCastException if `key` is not a T.
   * @param key
   * @return 
   */
  @Override
  public U get(Object key) {
    if (containsKey(key)) {
      return super.get(key);
    } else {
      U value = defaultConstructor.construct((T)key);
      super.put((T)key, value);
      return value;
    }
  }
}