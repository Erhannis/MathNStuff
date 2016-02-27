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
public class ExDiv extends Expression {
  public Expression top;
  public Expression bot;
  
  public ExDiv(Expression top, Expression bot) {
    this.top = top;
    this.bot = bot;
  }

  @Override
  public double eval(Map<String, Double> varValues) {
    return top.eval(varValues) / bot.eval(varValues);
  }
  
  @Override
  public Expression sort() {
    // Could maybe just return `this`
    return new ExDiv(top.sort(), bot.sort());
  }

  @Override
  public Expression collapse() {
    // Could maybe just return `this`
    //TODO Maybe something I could do, here
    return new ExDiv(top.sort(), bot.sort());
  }

  /**
   * //TODO Could turn constant divisors into factors....
   * @return 
   */
  @Override
  public Expression reduce() {
    return new ExDiv(top.sort(), bot.sort());
  }

  @Override
  public String toString() {
    return "(" + top + ")/(" + bot + ")";
  }
}
