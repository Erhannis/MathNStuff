/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.symbolic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author erhannis
 */
public class ExMult extends Expression {
  public ArrayList<Expression> terms = new ArrayList<Expression>();
  
  public ExMult(Expression... terms) {
    Collections.addAll(this.terms, terms);
  }

  private ExMult(ArrayList<Expression> terms) {
    this.terms = terms;
  }
  
  @Override
  public double eval(Map<String, Double> varValues) {
    double result = 1;
    for (Expression term : terms) {
      result *= term.eval(varValues);
    }
    return result;
  }
  
  @Override
  public Expression sort() {
    ArrayList<Expression> newTerms = new ArrayList<Expression>();
    for (Expression term : terms) {
      newTerms.add(term.sort());
    }
    Collections.sort(newTerms, new Comparator<Expression>() {
      @Override
      public int compare(Expression o1, Expression o2) {
        // I want constants in front
        if (o1 instanceof ExConstant) {
          if (o2 instanceof ExConstant) {
            return o1.toString().compareTo(o2.toString());
          } else {
            return -1;
          }
        } else if (o2 instanceof ExConstant) {
          return 1;
        } else {
          return o1.toString().compareTo(o2.toString());
        }
      }
    });
    return new ExMult(newTerms);
  }
  
  @Override
  public Expression collapse() {
    ExMult newEx = new ExMult();
    for (Expression term : terms) {
      term = term.collapse();
      if (term instanceof ExMult) {
        newEx.terms.addAll(((ExMult)term).terms);
      } else {
        newEx.terms.add(term);
      }
    }
    return newEx;
  }

  /**
   * Combines scalars, expands products of sums.
   * If only one term, returns that.
   * //TODO Should maybe turn (b^a)(b^c) into (b^(a+c))?
   * //TODO Should maybe turn a*a into (a^2)?
   * //TODO Could maybe reduce multiplied fractions
   * @return 
   */
  @Override
  public Expression reduce() {
    ExMult collapsed = (ExMult)this.collapse();
    ExMult subSimplified = new ExMult();
    
    // Simplify sub expressions
    ExConstant factor = new ExConstant(1);
    subSimplified.terms.add(factor);
    for (Expression term : collapsed.terms) {
      term = term.collapse(); //TODO May be very redundant
      term = term.reduce();
      term = term.sort();
      if (term instanceof ExMult) {
        subSimplified.terms.addAll(((ExMult)term).terms);
      } else if (term instanceof ExConstant) {
        factor.value *= ((ExConstant)term).value;
      } else {
        subSimplified.terms.add(term);
      }
    }
    if (factor.value == 1) {
      subSimplified.terms.remove(factor);
    }
    if (subSimplified.terms.size() == 1) {
      return subSimplified.terms.get(0);
    }
    
    // Expand
    for (int i = 0; i < subSimplified.terms.size(); i++) {
      Expression term = subSimplified.terms.get(i);
      if (term instanceof ExAdd) {
        ExAdd curTerm = (ExAdd)term;
        ExMult others = new ExMult();
        for (int j = 0; j < subSimplified.terms.size(); j++) {
          if (j == i) continue;
          others.terms.add(subSimplified.terms.get(j));
        }
        ExAdd newAdd = new ExAdd();
        for (int j = 0; j < curTerm.terms.size(); j++) {
          ExMult newMult = new ExMult();
          newMult.terms.add(curTerm.terms.get(j));
          newMult.terms.addAll(others.terms);
          newAdd.terms.add(newMult);
        }
        return newAdd.reduce();
      }
    }
    // No expansion happened.
    
    //TODO Anything else?

    return subSimplified;
  }

  /**
   * Assumes that this Expression has already been reduced (and therefore that the
   * factor, if there is one, is out front).
   * @return 
   */
  public String toStringNoFactor() {
    if (terms.size() > 0) {
      if (terms.get(0) instanceof ExConstant) {
        // Has factor
        if (terms.size() == 2) {
          // Is factor times expression
          return terms.get(1).toString();
        } else {
          // Is factor times several things
          StringBuilder sb = new StringBuilder();
          sb.append("(" + terms.get(1).toString() + ")");
          for (int i = 2; i < terms.size(); i++) {
            sb.append("*(" + terms.get(i).toString() + ")");
          }
          return sb.toString();
        }
      } else {
        // Does not have factor
        if (terms.size() == 1) {
          // Is single inner expression (which shouldn't actually happen...error, for information)
          throw new IllegalArgumentException("Found an illegal (though harmless) state!  Reduced ExMult had single subexpression!");
        } else {
          // Has several expressions
          return this.toString();
        }
      }
    } else {
      // Kinda weird, but technically right, I think?
      return "1";
    }
  }
  
  /**
   * Assumes that this Expression has already been reduced (and therefore that the
   * factor, if there is one, is out front).
   * @return 
   */
  public double getFactor() {
    if (terms.size() > 0 && terms.get(0) instanceof ExConstant) {
      return ((ExConstant)terms.get(0)).value;
    } else {
      return 1;
    }
  }

  /**
   * Assumes that this Expression has already been reduced (and therefore that the
   * factor, if there is one, is out front).
   * @return 
   */
  public Expression getWithoutFactor() {
    if (terms.size() > 0 && terms.get(0) instanceof ExConstant) {
      if (terms.size() == 2) {
        return terms.get(1);
      } else {
        ExMult newMult = new ExMult();
        for (int i = 1; i <  terms.size(); i++) {
          newMult.terms.add(terms.get(i));
        }
        return newMult;
      }
    } else {
      //TODO Hmm, like, I like the idea of consistently returning copies, but this may be excessive.
      //       Also, has the side effect of sorting it.
      return this.sort();
    }
  }
  
  @Override
  public String toString() {
    if (terms.size() > 0) {
      StringBuilder sb = new StringBuilder();
      sb.append("(" + terms.get(0).toString() + ")");
      for (int i = 1; i < terms.size(); i++) {
        sb.append("*(" + terms.get(i).toString() + ")");
      }
      return sb.toString();
    } else {
      // Kinda weird, but technically right, I think?
      return "1";
    }
  }
  
  @Override
  public String toStringSimple() {
    if (terms.size() > 0) {
      StringBuilder sb = new StringBuilder();
      if (Expression.addParentheses(terms.get(0))) {
        sb.append("(" + terms.get(0).toStringSimple() + ")");
      } else {
        sb.append(terms.get(0).toStringSimple());
      }
      for (int i = 1; i < terms.size(); i++) {
        if (Expression.addParentheses(terms.get(i))) {
          sb.append(" * (" + terms.get(i).toStringSimple() + ")");
        } else {
          sb.append(" * " + terms.get(i).toStringSimple());
        }
      }
      return sb.toString();
    } else {
      // Kinda weird, but technically right, I think?
      return "1";
    }
  }
}
