/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 *
 * @author mewer
 */
public class ImagePanel extends javax.swing.JPanel {

  /**
   * Creates new form ImagePanel
   */
  public ImagePanel() {
    initComponents();
  }

  private double scale = 1.0;
  private Image image;

  public void setImage(Image image) {
    if (this.image != image) {
      this.image = image;
      int width = image.getWidth(null);
      int height = image.getHeight(null);
      Dimension size = this.getMinimumSize();
      size.setSize(width, height);
      this.setMinimumSize(size);
      this.setPreferredSize(size);
      this.invalidate();
      this.repaint();
    }
  }

  public double getScale() {
    return scale;
  }

  public void setScale(double scale) {
    this.scale = scale;
    int width = image.getWidth(null);
    int height = image.getHeight(null);
    Dimension size = this.getMinimumSize();
    size.setSize(width * scale, height * scale);
    this.setMinimumSize(size);
    this.setPreferredSize(size);
    this.invalidate();
    this.validate();
    this.repaint();
  }

  public void zoomOut() {
    setScale(getScale() * 0.5);
  }

  public void zoomIn() {
    setScale(getScale() * 2);
  }

  public Image getImage() {
    return image;
  }

  @Override
  protected void paintComponent(Graphics g0) {
    super.paintComponent(g0); //To change body of generated methods, choose Tools | Templates.
    Graphics2D g = (Graphics2D) g0;
    g.scale(scale, scale);
    g.drawImage(image, 0, 0, this);
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}