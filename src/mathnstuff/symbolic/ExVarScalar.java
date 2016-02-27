/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mathnstuff.symbolic;

import java.util.Map;

/**
 *
 * @author erhannis
 */
public class ExVarScalar extends Expression {
  public String name;
  
  public ExVarScalar(String name) {
    this.name = name;
  }

  @Override
  public double eval(Map<String, Double> varValues) {
    return varValues.get(name);
  }
  
  @Override
  public Expression sort() {
    // Could maybe just return `this`
    return new ExVarScalar(name);
  }
  
  @Override
  public String toString() {
    return name;
  }
}
