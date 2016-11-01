/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.networks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author erhannis
 */
public class Network {
    public ArrayList<Node> nodes = new ArrayList<Node>();
    public ArrayList<Edge> edges = new ArrayList<Edge>();
    public static final int SCALING = 400;
    public static final Random r = new Random();

    /**
     * Iterates through the list of nodes and reassigns them an index number
     * accordingly.
     */
    public void reIndex() {
        for (int i = 0; i < nodes.size(); i++) {
            // Reset the index of the node.
            nodes.get(i).index = i;
            // Now update the connecting edges accordingly.
            for (Edge e : nodes.get(i).edgesIn) {
                e.toIndex = i;
            }
            for (Edge e : nodes.get(i).edgesOut) {
                e.fromIndex = i;
            }
        }
    }    
    
    /**
     * Returns a copy of the network.  Reindexes both in the process (iterates
     * through the list of nodes and reassigns them an index number
     * accordingly).  This shouldn't affect anything else.
     * @return 
     */
    public Network copy() {
        Network result = GraphFunctions.readString(this.toFile());
        for (int i = 0; i < nodes.size(); i++) {
            result.nodes.get(i).id = nodes.get(i).id;
        }
        for (int i = 0; i < edges.size(); i++) {
            result.edges.get(i).id = edges.get(i).id;
        }
        return result;
    }
    
//    /**
//     * Initializes flow values to zero
//     */
//    public void clean() {
//        for (Edge e : edges) {
//            e.flow = 0;
//        }
//    }
    
    /**
     * Returns a string representation of the network.  The syntax of the output
     * is as follows, first a list of nodes, then a list of edges:<br/>
     * node0x node0y</br>
     * ...<br/>
     * nodeNx nodeNy<br/>
     * edge0FromNode# edge0ToNode# edge0Capacity<br/>
     * ...<br/>
     * edgeMFromNode# edgeMToNode# edgeMCapacity<br/>
     * 
     * The node numbers and capacities are be integers, but the node coords
     * can be real numbers (usually less than 10).
     * @return 
     */
    public String toFile() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodes.size(); i++) {
            // Inline reindexing.
            nodes.get(i).index = i;
            for (Edge e : nodes.get(i).edgesIn) {
                e.toIndex = i;
            }
            for (Edge e : nodes.get(i).edgesOut) {
                e.fromIndex = i;
            }

            sb.append((nodes.get(i).x / SCALING) + "\t" + (nodes.get(i).y / SCALING) + "\n");
        }
        for (int i = 0; i < edges.size(); i++) {
            sb.append(edges.get(i).fromIndex + "\t" + edges.get(i).toIndex + "\t" + edges.get(i).capacity + "\t" + edges.get(i).cost + "\n");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
    
    /**
     * Assigns random IDs to nodes and edges, so that copies can still be
     * compared.
     */
    public void assignIDs() {
        for (Node n : nodes) {
            //TODO Um, I left this blank, and this function is unused.  I thought I used it?
            //         Maybe not.  I guess all nodes and edges get a unique random id.
        }
    }
    
    public void removeFakeEdges() {
        HashSet<Edge> delete = new HashSet<Edge>();
        for (Node n : nodes) {
            for (Edge e : n.edgesIn) {
                if (e.mirror != null && e.mirror.capacity == 0) {
                    e.mirror = null;
                }
                if (e.capacity == 0) {
                    delete.add(e);
                }
            }
            n.edgesIn.removeAll(delete);
            delete.clear();
            for (Edge e : n.edgesOut) {
                if (e.mirror != null && e.mirror.capacity == 0) {
                    e.mirror = null;
                }
                if (e.capacity == 0) {
                    delete.add(e);
                }
            }
            n.edgesOut.removeAll(delete);
            delete.clear();
        }
        for (Edge e : edges) {
            if (e.mirror != null && e.mirror.capacity == 0) {
                e.mirror = null;
            }
            if (e.capacity == 0) {
                delete.add(e);
            }
        }
        edges.removeAll(delete);
    }
    
    public void prepareCostMirrors() {
        LinkedList<Edge> mirrors = new LinkedList<Edge>();
        for (Edge e : edges) {
            Edge mirror = new Edge(e.toNode, e.fromNode, 0, -e.cost);
            mirror.fromIndex = e.fromIndex;
            mirror.toIndex = e.toIndex;
            mirror.fromNode.connections.add(mirror.toNode);
            mirror.toNode.edgesIn.add(mirror);
            mirror.fromNode.edgesOut.add(mirror);
            e.mirror = mirror;
            mirror.mirror = e;
            mirrors.add(mirror);
        }
        edges.addAll(mirrors);
    }
}
