/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package networks;

/**
 * Represents an edge in a network.  The redundancy in fromIndex/fromNode etc.
 * is useful in different circumstances.
 * @author erhannis
 */
public class Edge {
    public long id = Network.r.nextLong();
    public int fromIndex = -1;
    public int toIndex = -1;
    public Node fromNode = null;
    public Node toNode = null;
    public int capacity = -1;
    //public int flow = 0;
    public Edge mirror = null;
    
    public Edge(int fromIndex, int toIndex, int capacity) {
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.capacity = capacity;
    }

    public Edge(Node fromNode, Node toNode, int capacity) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.capacity = capacity;
    }
    
    public Edge() {
    }
}
