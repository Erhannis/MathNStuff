/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.components;

/**
 *
 * @author erhannis
 */
public class ProgressDialog extends javax.swing.JDialog {

  public ProgressDialog() {
    this(null, false, "Please hold...", "Operation in progress");
  }

  public ProgressDialog(String message) {
    this(null, false, message, "Operation in progress");
  }
  
  public ProgressDialog(java.awt.Frame parent) {
    this(parent, true, "Please hold...", "Operation in progress");
  }
  
  public ProgressDialog(java.awt.Frame parent, String message) {
    this(parent, true, message, "Operation in progress");
  }
  
  /**
   * Creates new form ProgressDialog
   */
  public ProgressDialog(java.awt.Frame parent, boolean modal, String message, String title) {
    super(parent, modal);
    initComponents();
    this.setTitle(title);
    lMessage.setText(message);
    this.pack();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    lMessage = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

    lMessage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lMessage.setText("(message)");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(lMessage)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(lMessage)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel lMessage;
  // End of variables declaration//GEN-END:variables
}