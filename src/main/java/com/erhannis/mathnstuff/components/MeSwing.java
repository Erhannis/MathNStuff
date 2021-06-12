/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.components;

import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

/**
 *
 * @author erhannis
 */
public class MeSwing {
    /**
     * "For stronger security, it is recommended that the returned character array be cleared after use by setting each character to zero."
     * See {@link #clearChars(char[])}
     * @return 
     */
    public static char[] showPasswordDialog() {
        return showPasswordDialog("Enter Password");
    }

    /**    
     * "For stronger security, it is recommended that the returned character array be cleared after use by setting each character to zero."<br/>
     * See {@link #clearChars(char[])}
     * @param prompt
     * @return 
     */
    public static char[] showPasswordDialog(String prompt) {
        JPasswordField pf = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(null, pf, prompt, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okCxl == JOptionPane.OK_OPTION) {
            return pf.getPassword();
        }
        return null;
    }
    
    public static void clearChars(char[] chars) {
        Arrays.fill(chars, (char)0);
    }
}
