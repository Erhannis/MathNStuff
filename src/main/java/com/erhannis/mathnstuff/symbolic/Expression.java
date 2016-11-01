/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.symbolic;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import com.erhannis.mathnstuff.DoubleFunction;
import com.erhannis.mathnstuff.NVector;

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

  public abstract Expression collapse();
  
  /**
   * Attempts to reduce and simplify the expression.  This is pretty open-ended.
   * In actuality, it may make things more complicated, by multiplying things out
   * and actually squaring things, etc.
   * @return 
   */
  public abstract Expression reduce();
  
  /**
   * I'll have to change it once I introduce vectors, etc.
   * //TODO It sometimes requires multiple iterations to fully resolve.  Grr.
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
  
  /**
   * The toString method on most of the other things added parentheses all over;
   * makes it hard to read.  This one will try not to add as many parentheses.
   * @return 
   */
  public abstract String toStringSimple();
  
  protected static boolean addParentheses(Expression e) {
    if (e instanceof ExConstant) {
      return false;
    } else if (e instanceof ExVarScalar) {
      return false;
    } else {
      return true;
    }
  }
  
  private static Random rand = new Random();
  
  private static synchronized double nextDouble() {
    return rand.nextDouble();
  }
  
  private static synchronized int nextInt(int n) {
    return rand.nextInt(n);
  }
  
  private static synchronized int nextTaper(double rate) {
    int i = 0;
    while (nextDouble() < rate) {
      i++;
    }
    return i;
  }
  
  private static final String[] VAR_NAMES = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
  
  /**
   * Generates a kindof-random expression.
   * @return 
   */
  public static Expression randomExpression(int level) {
    double r = nextDouble();
    if (level > 0 && (1.0 / level) < r) {
      // Leaf
      switch (nextInt(2)) {
        case 0:
          // Constant
          int value = nextInt(20) - 10;
          if (value >= 0) {
            // Zeros usually aren't interesting in an expression, and often break things
            value++;
          }
          return new ExConstant(value);
        case 1:
        default:
          // Variable
          int i = Math.min(VAR_NAMES.length - 1, nextTaper(0.5));
          String varName = VAR_NAMES[i];
          return new ExVarScalar(varName);
      }
    } else {
      // Branch
      switch (nextInt(4)) {
        case 0:
        {
          // Add
          int count = nextInt(10 - 2) + 2;
          Expression[] exprs = new Expression[count];
          for (int i = 0; i < count; i++) {
            exprs[i] = randomExpression(level + 1);
          }
          return new ExAdd(exprs);
        }
        case 1:
        {
          // Mult
          int count = nextInt(10 - 2) + 2;
          Expression[] exprs = new Expression[count];
          for (int i = 0; i < count; i++) {
            exprs[i] = randomExpression(level + 1);
          }
          return new ExAdd(exprs);
        }
        case 2:
        {
          return new ExPow(randomExpression(level + 1), randomExpression(level + 1));
        }
        case 3:
        default:
        {
          return new ExDiv(randomExpression(level + 1), randomExpression(level + 1));
        }
      }
    }
  }
}
