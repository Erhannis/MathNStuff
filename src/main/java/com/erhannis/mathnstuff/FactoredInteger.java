/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff;

/**
 *
 * @author mewer
 */
public class FactoredInteger {

    public int primeCount;
    public int[] primes;
    public int[] factors;
    public boolean isZero;

    private void init() {
        primes = new int[primeCount];
        int i = 0;
        int k = 1;
        while (i < primeCount) {
            if (isPrime(k)) {
                primes[i] = k;
                i++;
            }
            k++;
        }

        factors = new int[primeCount];

        isZero = false;
    }

    private boolean isPrime(int a) {
        for (int i = 0; i < primeCount; i++) {
            if (primes[i] == 0) {
                // All primes yet calculated do not divide a.
                return true;
            }
            if (MeMath.divides(a, primes[i])) {
                return false;
            }
        }
        return true;
    }    
    
    public FactoredInteger(int primeCount) {
        this.primeCount = primeCount;
    }

    public FactoredInteger multiply(FactoredInteger b) {
        FactoredInteger c = new FactoredInteger(Math.max(this.primeCount, b.primeCount));
        if (this.isZero || b.isZero) {
            c.isZero = true;
            return c;
        }
        for (int i = 0; i < this.primeCount; i++) {
            c.factors[i] += this.factors[i];
        }
        for (int i = 0; i < b.primeCount; i++) {
            c.factors[i] += b.factors[i];
        }
        return c;
    }

    public FactoredInteger multiplyIP(FactoredInteger b) {
        if (b.isZero) {
            this.isZero = true;
        }
        if (this.isZero) {
            return this;
        }
        for (int i = 0; i < Math.min(this.primeCount, b.primeCount); i++) {
            this.factors[i] += b.factors[i];
        }
        return this;
    }
}