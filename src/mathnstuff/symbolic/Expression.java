/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mathnstuff.symbolic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
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
  
  /**
   * Parses an expression in prefix notation.
   * (+ a b c...)
   * (* a b c...)
   * (^ a b)
   * where a, b, c, etc. are a constant or variable name.  Things which can be
   * parsed as doubles are, otherwise they are parsed as variable names.  Careful.
   * Examples:
   * (^ (+ (^ a 2) (^ b 2)) 0.5)
   * (+ (* a b) (* 2 a c) (* b c))
   * @param expr
   * @return 
   */
  public static Expression parsePrefix(String expr) {
    String[] tokenList = expr.replaceAll("\\)", " )").split(" ");
    Stack<String> tokens = new Stack<String>();
    for (int i = tokenList.length - 1; i >= 0; i--) {
      String token = tokenList[i];
      if (token.isEmpty()) {
        continue;
      }
      tokens.push(token);
    }
    return parsePrefixSub(tokens);
  }
  
  private static Expression parsePrefixSub(Stack<String> tokens) {
    String token = tokens.pop();
    if ("(+".equals(token)) {
      Expression sub = parsePrefixSub(tokens);
      ExAdd newExp = new ExAdd();
      while (sub != null) {
        newExp.terms.add(sub); // A little non-kosher.  But should work, for now.
        sub = parsePrefixSub(tokens);
      }
      return newExp;
    } else if ("(*".equals(token)) {
      Expression sub = parsePrefixSub(tokens);
      ExMult newExp = new ExMult();
      while (sub != null) {
        newExp.terms.add(sub); // A little non-kosher.  But should work, for now.
        sub = parsePrefixSub(tokens);
      }
      return newExp;
    } else if ("(^".equals(token)) {
      Expression a = parsePrefixSub(tokens);
      Expression b = parsePrefixSub(tokens);
      String endPar = tokens.pop();
      if (!")".equals(endPar)) {
        throw new IllegalArgumentException("pow expression expected end par, got \"" + endPar + "\"");
      }
      return new ExPow(a, b);
    } else if (")".equals(token)) {
      return null;
    } else {
      try {
        double value = Double.parseDouble(token);
        return new ExConstant(value);
      } catch (NumberFormatException nfe) {
        return new ExVarScalar(token);
      }
    }
  }
  
  public abstract Expression sort();

//  public abstract Expression collapse();
  
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
