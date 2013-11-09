/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mathnstuff.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mewer
 */
public abstract class QueueStream {
    public static class QueueInputStream extends InputStream {
        public QueueOutputStream dual = null;
        
        public QueueInputStream() {
            dual = new QueueOutputStream(this);
        }
        
        public QueueInputStream(QueueOutputStream dual) {
            this.dual = dual;
        }

        @Override
        public int read() throws IOException {
            if (dual.open) {
                while (dual.queue.isEmpty()) {
                    synchronized (dual.queue) {
                        try {
                            dual.queue.wait();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(QueueStream.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                return dual.queue.remove();
            } else {
                return -1;
            }
        }
    }

    public static class QueueOutputStream extends OutputStream {
        protected boolean open = true;
        protected final ArrayDeque<Byte> queue = new ArrayDeque<Byte>();
        public QueueInputStream dual = null;
        
        private void setup() {
            open = true;
        }
        
        public QueueOutputStream() {
            setup();
            this.dual = new QueueInputStream(this);
        }

        public QueueOutputStream(QueueInputStream dual) {
            setup();
            this.dual = dual;
        }
        
        @Override
        public void write(int b) throws IOException {
            synchronized (queue) {
                queue.push((byte)b);
                queue.notifyAll();
            }
        }

        @Override
        public void close() throws IOException {
            super.close();
            open = false;
        }
    }
}
