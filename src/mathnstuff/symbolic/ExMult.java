/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mathnstuff.symbolic;

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
    double result = 0;
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
        return o1.toString().compareTo(o2.toString());
      }
    });
    return new ExMult(newTerms);
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
}
