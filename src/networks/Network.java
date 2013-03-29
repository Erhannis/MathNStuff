/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package networks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author erhannis
 */
public class Network {
    public ArrayList<Node> nodes = new ArrayList<Node>();
    public ArrayList<Edge> edges = new ArrayList<Edge>();
    public static final int SCALING = 1;//400;
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
        Network result = readString(this.toFile());
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
            sb.append(edges.get(i).fromIndex + "\t" + edges.get(i).toIndex + "\t" + edges.get(i).capacity + "\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
    
    /**
     * Assigns random IDs to nodes and edges, so that copies can still be
     * compared.
     */
    public void assignIDs() {
        for (Node n : nodes) {
            
        }
    }

    /**
     * Reads in a string representation of a network and returns an instance
     * of that network.  See readReader for a description of the syntax.
     * @param text
     * @return 
     */
    public static Network readString(String text) {
        StringReader sr = new StringReader(text);
        BufferedReader br = new BufferedReader(sr);
        return readReader(br);
    }
    
    /**
     * Reads in a network from a BufferedReader, probably from either
     * readFilename or readString.  The network should be formatted in the
     * following manner, first with a list of nodes, then a list of edges:<br/>
     * node0x node0y</br>
     * ...<br/>
     * nodeNx nodeNy<br/>
     * edge0FromNode# edge0ToNode# edge0Capacity<br/>
     * ...<br/>
     * edgeMFromNode# edgeMToNode# edgeMCapacity<br/>
     * 
     * The node numbers and capacities should be integers, but the node coords
     * can be real numbers (preferably less than 10).
     * @param br
     * @return 
     */
    public static Network readReader(BufferedReader br) {
        Network result = new Network();
        try {
            ArrayList<String> lines = new ArrayList<String>();
            String line = br.readLine();
            while (line != null) {
                lines.add(line);
                line = br.readLine();
            }
            boolean stillOnNodes = true;
            for (int i = 0; i < lines.size(); i++) {
                String[] numStrings = lines.get(i).split("\t");
                if (numStrings.length != 2) {
                    stillOnNodes = false;
                }
                if (numStrings.length <= 1) {
                    System.err.println("Line containing " + numStrings.length + " elements encountered");
                    break;
                }
                if (stillOnNodes) {
                    result.nodes.add(new Node(i, Network.SCALING * Double.valueOf(numStrings[0].trim()), Network.SCALING * Double.valueOf(numStrings[1].trim())));
                } else {
                    Edge bucket = new Edge(Integer.valueOf(numStrings[0].trim()), Integer.valueOf(numStrings[1].trim()), Integer.valueOf(numStrings[2].trim()));
                    bucket.fromNode = result.nodes.get(bucket.fromIndex);
                    bucket.toNode = result.nodes.get(bucket.toIndex);
                    if (bucket.fromNode.connections.contains(bucket.toNode)) {
                        // Such an edge already exists; add bucket's capacity to the existing node's.
                        for (Edge e : bucket.fromNode.edgesOut) {
                            if (e.toNode == bucket.toNode) {
                                e.capacity += bucket.capacity;
                                break;
                            }
                        }
                    } else {
                        bucket.fromNode.connections.add(bucket.toNode);
                        bucket.toNode.edgesIn.add(bucket);
                        bucket.fromNode.edgesOut.add(bucket);
                        result.edges.add(bucket);
                        Edge mirror = null;
                        for (Edge e : bucket.toNode.edgesOut) {
                            if (e.toNode == bucket.fromNode) {
                                mirror = e;
                                break;
                            }
                        }
                        if (mirror == null) {
                            mirror = new Edge(bucket.toNode, bucket.fromNode, 0);
                            mirror.fromIndex = bucket.fromIndex;
                            mirror.toIndex = bucket.toIndex;
                            mirror.fromNode.connections.add(mirror.toNode);
                            mirror.toNode.edgesIn.add(mirror);
                            mirror.fromNode.edgesOut.add(mirror);
                            bucket.mirror = mirror;
                            mirror.mirror = bucket;
                            result.edges.add(mirror);
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
