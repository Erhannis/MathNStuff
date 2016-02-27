/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mathnstuff.symbolic;

import java.util.HashMap;
import java.util.Map;
import mathnstuff.DoubleFunction;
import mathnstuff.NVector;

/**
 *
 * @author erhannis
 */
public abstract class Expression {
  /**
   * Parse an expression, without regard for most order of operations (save for parentheses).
   * @param expr
   * @return 
   */
  public static Expression parseNoOpsOrder(String expr) {
    return null;
  }
  
//  public abstract Expression sort();

  /**
   * I'll have to change it once I introduce vectors, etc.
   * @param varValues
   * @return 
   */
  public abstract double eval(Map<String, Double> varValues);
  
  public DoubleFunction toFunction(final String... varOrder) {
    return new DoubleFunction() {
      @Override
      public double evaluate(NVector x) {
        HashMap<String, Double> varValues = new HashMap<String, Double>();
        for (int i = 0; i < varOrder.length; i++) {
          varValues.put(varOrder[i], x.coords[i]);
        }
        return Expression.this.eval(varValues);
      }
    };
  }
}
