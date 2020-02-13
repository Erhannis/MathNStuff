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
  
  /**
   * Update the value via the function.  Return the old value.
   * @param key
   * @param func
   * @return 
   */
  public U apply(T key, TToTFunction<U, U> func) {
    U val = this.get(key);
    this.put(key, func.evaluate(val));
    return val;
  }
}