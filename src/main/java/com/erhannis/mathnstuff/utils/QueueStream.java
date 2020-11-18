/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.mathnstuff.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains two paired classes: and InputStream and an OutputStream.
 * Creating one also creates its dual (the other end).  Writing to the
 * QueueOutputStream adds bytes to the queue, which can be read by
 * @author mewer
 */
public abstract class QueueStream {
    public static class QueueInputStream extends InputStream {
        public QueueOutputStream dual = null;
        
        /**
         * Don't forget to call close() on the dual!
         */
        public QueueInputStream() {
            dual = new QueueOutputStream(this);
        }
        
        protected QueueInputStream(QueueOutputStream dual) {
            this.dual = dual;
        }

        @Override
        public int read() throws IOException {
            while (dual.queue.isEmpty()) {
                if (!dual.open) {
                    return -1;
                }
                synchronized (dual.queue) {
                    try {
                        dual.queue.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(QueueStream.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (!dual.open) {
                    return -1;
                }
            }
            return dual.queue.remove();
        }

        @Override
        public void close() throws IOException {
            super.close();
            dual.close();
        }
    }

    public static class QueueOutputStream extends OutputStream {
        protected boolean open = true;
        protected final ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
        public QueueInputStream dual = null;
        
        private void setup() {
            open = true;
        }
        
        /**
         * Don't forget to call close()!
         */
        public QueueOutputStream() {
            setup();
            this.dual = new QueueInputStream(this);
        }

        protected QueueOutputStream(QueueInputStream dual) {
            setup();
            this.dual = dual;
        }
        
        @Override
        public void write(int b) throws IOException {
            synchronized (queue) {
                queue.add(b & 0xFF);
                queue.notifyAll();
            }
        }

        @Override
        public void close() throws IOException {
            super.close();
            open = false;
            synchronized (queue) {
                queue.notifyAll();
            }
        }
    }
}
