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
public class ExConstant extends Expression {
  public double value = 0;
  
  public ExConstant(double value) {
    this.value = value;
  }

  @Override
  public double eval(Map<String, Double> varValues) {
    return value;
  }
  
  @Override
  public Expression sort() {
    // Could maybe just return `this`
    return new ExConstant(value);
  }
  
  @Override
  public Expression collapse() {
    // Could maybe just return `this`
    return new ExConstant(value);
  }

  @Override
  public Expression reduce() {
    // Could maybe just return `this`
    return new ExConstant(value);
  }

  @Override
  public String toString() {
    return Double.toString(value);
  }

  @Override
  public String toStringSimple() {
    return Double.toString(value);
  }
}
