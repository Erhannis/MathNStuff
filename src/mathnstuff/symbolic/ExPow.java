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
public class ExPow extends Expression {
  public Expression base;
  public Expression exp;
  
  public ExPow(Expression base, Expression exp) {
    this.base = base;
    this.exp = exp;
  }

  @Override
  public double eval(Map<String, Double> varValues) {
    return Math.pow(base.eval(varValues), exp.eval(varValues));
  }
  
  @Override
  public String toString() {
    return "(" + base + ")^(" + exp + ")";
  }
}
