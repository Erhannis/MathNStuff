/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff;

import java.util.Objects;

/**
 *
 * @author erhannis
 */
public class Pair<T, U> {

    public T a;
    public U b;

    public Pair(T a, U b) {
        this.a = a;
        this.b = b;
    }

    public static <T, U> Pair<T, U> gen(T t, U u) {
        return new Pair<T, U>(t, u);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Pair)) return false;
        Pair<T, U> p = (Pair<T, U>)obj;
        return (Objects.equals(this.a, p.a) && Objects.equals(this.b, p.b));
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    @Override
    public String toString() {
        return "P{"+a+","+b+"}";
    }
}
