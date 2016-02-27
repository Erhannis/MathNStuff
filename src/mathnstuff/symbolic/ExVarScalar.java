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
public class ExVarScalar extends Expression {
  public double value = 0;
  
  public ExVarScalar(double value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return Double.toString(value);
  }
}
