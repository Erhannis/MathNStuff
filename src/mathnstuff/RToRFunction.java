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
        final RToRFunction a = this;
        return new RToRFunction() {
            @Override
            public double evaluate(double x) {
                return a.evaluate(x) + b.evaluate(x);
            }
        };
    }

    public RToRFunction minus(final RToRFunction b) {
        final RToRFunction a = this;
        return new RToRFunction() {
            @Override
            public double evaluate(double x) {
                return a.evaluate(x) - b.evaluate(x);
            }
        };
    }

    public RToRFunction times(final RToRFunction b) {
        final RToRFunction a = this;
        return new RToRFunction() {
            @Override
            public double evaluate(double x) {
                return a.evaluate(x) * b.evaluate(x);
            }
        };
    }

    public RToRFunction div(final RToRFunction b) {
        final RToRFunction a = this;
        return new RToRFunction() {
            @Override
            public double evaluate(double x) {
                return a.evaluate(x) / b.evaluate(x);
            }
        };
    }
}
