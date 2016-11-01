/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.networks;

import java.util.HashSet;

/**
 * Represents a node in a network.  The variables "connections," "edgesIn," and
 * "edgesOut" are technically unnecessary (well, I suppose they technically all
 * are), but are used for efficiency.
 * @author erhannis
 */
public class Node {
    public int index = -1;
    public long id = Network.r.nextLong();
    public double x = -1;
    public double y = -1;
    public HashSet<Node> connections = new HashSet<Node>();
    public HashSet<Edge> edgesIn = new HashSet<Edge>();
    public HashSet<Edge> edgesOut = new HashSet<Edge>();
    
    public Node(int index, double x, double y) {
        this.index = index;
        this.x = x;
        this.y = y;
    }
    
    public Node(int index) {
        this.index = index;
    }
    
    public Node() {
    }
}
