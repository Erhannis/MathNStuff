/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mathnstuff;

import mathnstuff.NVector;

/**
 *
 * @author erhannis
 */
public interface Function<T> {
    public T evaluate(NVector x);
}
