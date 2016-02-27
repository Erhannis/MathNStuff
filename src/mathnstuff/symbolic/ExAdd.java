/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mathnstuff.symbolic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author erhannis
 */
public class ExAdd extends Expression {
  public ArrayList<Expression> terms = new ArrayList<Expression>();
  
  public ExAdd(Expression... terms) {
    Collections.addAll(this.terms, terms);
  }

  private ExAdd(ArrayList<Expression> terms) {
    this.terms = terms;
  }
  
  @Override
  public double eval(Map<String, Double> varValues) {
    double result = 0;
    for (Expression term : terms) {
      result += term.eval(varValues);
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
        return o1.toString().compareTo(o2.toString());
      }
    });
    return new ExAdd(newTerms);
  }

  @Override
  public Expression collapse() {
    ExAdd newEx = new ExAdd();
    for (Expression term : terms) {
      term = term.collapse();
      if (term instanceof ExAdd) {
        newEx.terms.addAll(((ExAdd)term).terms);
      } else {
        newEx.terms.add(term);
      }
    }
    return newEx;
  }

  /**
   * Combines scalars, combines like terms (may be buggy).
   * If only one term, returns that.
   * //TODO Could maybe reduce added fractions
   * @return 
   */
  @Override
  public Expression reduce() {
    ExAdd collapsed = (ExAdd)this.collapse();
    ExAdd newEx = new ExAdd();
    ExConstant offset = new ExConstant(0);
    HashMap<String, Double> coefficients = new HashMap<String, Double>();
    HashMap<String, Expression> uniqExpressions = new HashMap<String, Expression>();
    for (Expression term : collapsed.terms) {
      term = term.collapse(); // TODO May be very redundant
      term = term.reduce();
      if (term instanceof ExConstant) {
        offset.value += ((ExConstant)term).value;
      } else if (term instanceof ExMult) {
        ExMult mTerm = (ExMult)term;
        String sig = mTerm.toStringNoFactor();
        double factor = mTerm.getFactor();
        Double oldCoef = coefficients.get(sig);
        if (oldCoef != null) {
          factor += oldCoef;
        } else {
          uniqExpressions.put(sig, mTerm.getWithoutFactor());
        }
        coefficients.put(sig, factor);
      } else {
        String sig = term.toString();
        double factor = 1;
        Double oldCoef = coefficients.get(sig);
        if (oldCoef != null) {
          factor += oldCoef;
        } else {
          uniqExpressions.put(sig, term);
        }
        coefficients.put(sig, factor);
      }
    }
    if (offset.value != 0) {
      newEx.terms.add(offset);
    }
    for (Entry<String, Expression> entry : uniqExpressions.entrySet()) {
      if (coefficients.get(entry.getKey()) == 0) {
        // Nothing
      } else if (coefficients.get(entry.getKey()) == 1) {
        newEx.terms.add(entry.getValue());
      } else {
        newEx.terms.add(new ExMult(new ExConstant(coefficients.get(entry.getKey())), entry.getValue()));
      }
    }
    if (newEx.terms.size() == 1) {
      return newEx.terms.get(0);
    }
    return newEx;
  }

  @Override
  public String toString() {
    //TODO Cache answer/set up change flags (all over)
    if (terms.size() > 0) {
      StringBuilder sb = new StringBuilder();
      sb.append("(" + terms.get(0).toString() + ")");
      for (int i = 1; i < terms.size(); i++) {
        sb.append("+(" + terms.get(i).toString() + ")");
      }
      return sb.toString();
    } else {
      // Kinda weird, but technically right, I think?
      return "0";
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
          sb.append(" + (" + terms.get(i).toStringSimple() + ")");
        } else {
          sb.append(" + " + terms.get(i).toStringSimple());
        }
      }
      return sb.toString();
    } else {
      // Kinda weird, but technically right, I think?
      return "0";
    }
  }
}
