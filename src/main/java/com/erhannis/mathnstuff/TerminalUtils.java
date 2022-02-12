/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erhannis.mathnstuff;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

/**
 *
 * @author erhannis
 */
public class TerminalUtils {
    public static String promptString(String msg) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(msg);
        return scanner.nextLine();
    }

    public static Character promptChar(String msg) throws IOException {
        System.out.print(msg);
        int c = System.in.read();
        if (c < 0) {
            return null;
        }
        return (Character)(char)c;
    }
    
    public static enum YNC {
        Y,N,C
    }
    public static YNC promptYNC(String msg) throws IOException {
        Character c = promptChar(msg + "[y/n/C] ");
        if (Objects.equals(c, 'y')) {
            return YNC.Y;
        } else if (Objects.equals(c, 'n')) {
            return YNC.N;
        } else {
            return YNC.C;
        }
    }

    public static YNC promptYN(String msg) throws IOException {
        Character c = promptChar(msg + "[y/N] ");
        if (Objects.equals(c, 'y')) {
            return YNC.Y;
        } else {
            return YNC.N;
        }
    }
}
