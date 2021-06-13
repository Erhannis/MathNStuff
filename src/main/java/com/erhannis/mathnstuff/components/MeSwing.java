/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.components;

import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 *
 * @author erhannis
 */
public class MeSwing {
    /**
     * "For stronger security, it is recommended that the returned character array be cleared after use by setting each character to zero."<br/>
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
        final JPasswordField passwordField = new JPasswordField();
        JOptionPane pane = new JOptionPane(passwordField, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {
            @Override
            public void selectInitialValue() {
                passwordField.requestFocusInWindow();
            }
        };
        pane.createDialog(null, prompt).setVisible(true);
        if (pane.getValue() == (Integer)JOptionPane.OK_OPTION) {
            return passwordField.getPassword();
        } else {
            return null;
        }
    }
    
    public static void clearChars(char[] chars) {
        Arrays.fill(chars, (char)0);
    }
}
