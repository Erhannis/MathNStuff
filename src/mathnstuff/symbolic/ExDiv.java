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
    return new ExDiv(top.sort(), bot.sort());
  }

  @Override
  public Expression collapse() {
    //TODO Maybe something I could do, here
    return new ExDiv(top.collapse(), bot.collapse());
  }

  /**
   * //TODO Could turn constant divisors into factors....
   * @return 
   */
  @Override
  public Expression reduce() {
    return new ExDiv(top.reduce(), bot.reduce());
  }

  @Override
  public String toString() {
    return "(" + top + ")/(" + bot + ")";
  }

  @Override
  public String toStringSimple() {
    StringBuilder sb = new StringBuilder();
    if (Expression.addParentheses(top)) {
      sb.append("(" + top.toStringSimple() + ")/");
    } else {
      sb.append(top.toStringSimple() + "/");
    }
    if (Expression.addParentheses(bot)) {
      sb.append("(" + bot.toStringSimple() + ")");
    } else {
      sb.append(bot.toStringSimple());
    }
    return sb.toString();
  }
}
