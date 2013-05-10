/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mathnstuff;

/**
 *
 * @author erhannis
 */
public abstract class RToRFunction {
    public abstract double evaluate(double x);
    
    public RToRFunction plus(final RToRFunction b) {
        return new RToRFunction() {
            @Override
            public double evaluate(double x) {
                return evaluate(x) + b.evaluate(x);
            }
        };
    }

    public RToRFunction minus(final RToRFunction b) {
        return new RToRFunction() {
            @Override
            public double evaluate(double x) {
                return evaluate(x) - b.evaluate(x);
            }
        };
    }

    public RToRFunction times(final RToRFunction b) {
        return new RToRFunction() {
            @Override
            public double evaluate(double x) {
                return evaluate(x) * b.evaluate(x);
            }
        };
    }

    public RToRFunction div(final RToRFunction b) {
        return new RToRFunction() {
            @Override
            public double evaluate(double x) {
                return evaluate(x) / b.evaluate(x);
            }
        };
    }
}
