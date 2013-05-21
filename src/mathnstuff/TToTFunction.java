/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mathnstuff;

import java.util.HashMap;

/**
 *
 * @author erhannis
 */
public abstract class TToTFunction<F, T> {
    public abstract T evaluate(F x);
    
    public TToTFunction<F, T> memoize() {
        final TToTFunction<F, T> parent = this;
        return new TToTFunction<F, T>() {
            public HashMap<F, T> cache = new HashMap<F, T>();
            
            @Override
            public T evaluate(F x) {
                if (cache.containsKey(x)) {
                    return cache.get(x);
                } else {
                    T result = parent.evaluate(x);
                    cache.put(x, result);
                    return result;
                }
            }
        };
    }
}
