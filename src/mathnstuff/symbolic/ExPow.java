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
  public Expression sort() {
    // Could maybe just return `this`
    return new ExPow(base.sort(), exp.sort());
  }

  @Override
  public Expression collapse() {
    // Could maybe just return `this`
    return new ExPow(base.sort(), exp.sort());
  }

  /**
   * If the power is an integer constant, say, 0 < n < 10, turns into a literal product.
   * Also, if base and exp are constants, perform calculation.
   * //TODO Could turn ^-1 into div.
   * //TODO Could turn (0.5a)^2 into (0.5^2)(a^2)
   * @return 
   */
  @Override
  public Expression reduce() {
    ExPow newEx = new ExPow(base.collapse().reduce(), exp.collapse().reduce());
    if (newEx.exp instanceof ExConstant) {
      ExConstant e = (ExConstant)newEx.exp;
      if (newEx.base instanceof ExConstant) {
        ExConstant b = (ExConstant)newEx.base;
        return new ExConstant(Math.pow(b.value, e.value));
      } else {
        if (((int)e.value) == e.value && e.value > 0 && e.value < 10) {
          if (e.value == 1) {
            return newEx.base;
          } else {
            ExMult newMult = new ExMult();
            for (int i = 0; i < e.value; i++) {
              //TODO This is a little sketchy, adding references, but so far all this code makes copies, rather than in place.
              newMult.terms.add(newEx.base);
            }
            return newMult;
          }
        } else {
          return newEx;
        }
      }
    } else {
      return newEx;
    }
  }

  @Override
  public String toString() {
    return "(" + base + ")^(" + exp + ")";
  }

  @Override
  public String toStringSimple() {
    StringBuilder sb = new StringBuilder();
    if (Expression.addParentheses(base)) {
      sb.append("(" + base.toStringSimple() + ")^");
    } else {
      sb.append(base.toStringSimple() + "^");
    }
    if (Expression.addParentheses(exp)) {
      sb.append("(" + exp.toStringSimple() + ")");
    } else {
      sb.append(exp.toStringSimple());
    }
    return sb.toString();
  }
}
