/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mathnstuff.symbolic;

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
  
  
}
