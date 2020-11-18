/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.networks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author erhannis
 */
public class GraphFunctions {

    /**
     * Reads in a representation of a network from a file and returns an
     * instance of that network.  See readReader for a description of the
     * syntax.
     * @param text
     * @return 
     */
    public static Network readFilename(String filename) {
        FileReader fr = null;
        try {
            fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            return readReader(br);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GraphFunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GraphFunctions.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(GraphFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new Network();
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
                    Edge bucket = new Edge(Integer.valueOf(numStrings[0].trim()), Integer.valueOf(numStrings[1].trim()), Integer.valueOf(numStrings[2].trim()), (numStrings.length >= 4 ? Integer.valueOf(numStrings[3].trim()) : 1));
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
                            mirror = new Edge(bucket.toNode, bucket.fromNode, 0, 1);
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
            Logger.getLogger(GraphFunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GraphFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Intended for use in a breadth-first search: attach one to each item in
     * the network, then use them to mark the state of the nodes.
     * @param <T> 
     */
    public static class Marker<T> {
        public static final int STATE_DARK   = 0;
        public static final int STATE_NEW    = 1;
        public static final int STATE_ACTIVE = 2;
        public static final int STATE_DONE   = 3;
        
        public int state = STATE_DARK;
        public Marker<T> previous = null;
        public T current = null;
        
        // For min-cost path finding
        public int totalCost = 0;
        
        public Marker(T current) {
            this.current = current;
        }
    }
    
    /**
     * Finds and returns a path from source to target using a breadth-first
     * search, or returns null if no path is found.
     * @param network
     * @param source
     * @param target
     * @param skipZeroResCaps
     * @return 
     */
    public static ArrayList<Node> pathBreadthFirst(Network network, Node source, Node target) {
        // Initialize markers
        HashMap<Node, Marker<Node>> findMarker = new HashMap<Node, Marker<Node>>();
        LinkedList<Marker<Node>> darkNodes = new LinkedList<Marker<Node>>();
        LinkedList<Marker<Node>> newNodes = new LinkedList<Marker<Node>>();
        LinkedList<Marker<Node>> activeNodes = new LinkedList<Marker<Node>>();
        LinkedList<Marker<Node>> doneNodes = new LinkedList<Marker<Node>>();
        for (Node i : network.nodes) {
            Marker<Node> bucket = new Marker<Node>(i);
            findMarker.put(i, bucket);
            if (i == source) {
                bucket.state = Marker.STATE_ACTIVE;
                activeNodes.add(bucket);
            } else {
                darkNodes.add(bucket);
            }
        }
        
        // Find path
        boolean foundTarget = false;
        while (!foundTarget && activeNodes.size() > 0) {
            // Dissolve outward from each active node
            Iterator<Marker<Node>> mi = activeNodes.iterator();
            Marker<Node> m = null;
            while (mi.hasNext()) {
                m = mi.next();
                for (Edge e : m.current.edgesOut) {
                    if (e.capacity == 0) {
                        continue;
                    }
                    Marker<Node> nm = findMarker.get(e.toNode);
                    if (nm.state == Marker.STATE_DARK) {
                        nm.previous = m;
                        nm.state = Marker.STATE_NEW;
                        newNodes.add(nm);
                        darkNodes.remove(nm);
                    }
                    if (e.toNode == target) {
                        foundTarget = true;
                        break;
                    }
                }
                if (foundTarget) {
                    break;
                }
                // Deactivate node
                m.state = Marker.STATE_DONE;
                doneNodes.add(m);
                mi.remove();
            }
            if (foundTarget) {
                break;
            }
            
            // Turn newly found nodes into active ones
            mi = newNodes.iterator();
            while (mi.hasNext()) {
                m = mi.next();
                m.state = Marker.STATE_ACTIVE;
                activeNodes.add(m);
                mi.remove();
            }
        }
        if (!foundTarget) {
            return null;
        }
        
        // Turn the results into a proper path
        ArrayList<Node> path = new ArrayList<Node>();
        Marker<Node> m = findMarker.get(target);
        path.add(target);
        while (m.previous != null) {
            path.add(m.previous.current);
            m = m.previous;
        }
        
        // Need to flip the path around to start from the source
        for (int i = 0; i < path.size() / 2; i++) {
            Node bucket = path.get(i);
            path.set(i, path.get((path.size() - 1) - i));
            path.set((path.size() - 1) - i, bucket);
        }
        
        return path;
    }
    
    /**
     * In a pseudo-cut network, finds the minimal cut.  Returns the IDs of the
     * set of nodes on the source-side.  If there still exists non-0 path from
     * the source to the target, returns null.
     * @param network
     * @param source
     * @param target
     * @return 
     */
    public static HashSet<Long> findCut(Network network, Node source, Node target) {
        // Initialize markers
        HashMap<Node, Marker<Node>> findMarker = new HashMap<Node, Marker<Node>>();
        LinkedList<Marker<Node>> darkNodes = new LinkedList<Marker<Node>>();
        LinkedList<Marker<Node>> newNodes = new LinkedList<Marker<Node>>();
        LinkedList<Marker<Node>> activeNodes = new LinkedList<Marker<Node>>();
        LinkedList<Marker<Node>> doneNodes = new LinkedList<Marker<Node>>();
        for (Node i : network.nodes) {
            Marker<Node> bucket = new Marker<Node>(i);
            findMarker.put(i, bucket);
            if (i == source) {
                bucket.state = Marker.STATE_ACTIVE;
                activeNodes.add(bucket);
            } else {
                darkNodes.add(bucket);
            }
        }
        
        // Traverse network
        while (activeNodes.size() > 0) {
            // Dissolve outward from each active node
            Iterator<Marker<Node>> mi = activeNodes.iterator();
            Marker<Node> m = null;
            while (mi.hasNext()) {
                m = mi.next();
                for (Edge e : m.current.edgesOut) {
                    if (e.capacity == 0) {
                        continue;
                    }
                    Marker<Node> nm = findMarker.get(e.toNode);
                    if (nm.state == Marker.STATE_DARK) {
                        nm.previous = m;
                        nm.state = Marker.STATE_NEW;
                        newNodes.add(nm);
                        darkNodes.remove(nm);
                    }
                    if (e.toNode == target) {
                        // Not a cut
                        return null;
                    }
                }
                // Deactivate node
                m.state = Marker.STATE_DONE;
                doneNodes.add(m);
                mi.remove();
            }
            
            // Turn newly found nodes into active ones
            mi = newNodes.iterator();
            while (mi.hasNext()) {
                m = mi.next();
                m.state = Marker.STATE_ACTIVE;
                activeNodes.add(m);
                mi.remove();
            }
        }
        
        HashSet<Long> result = new HashSet<Long>();
        for (Marker<Node> m : doneNodes) {
            result.add(m.current.id);
        }
        
        return result;
    }
    
    public static String pathToString(ArrayList<Node> path) {
        StringBuffer sb = new StringBuffer();
        if (path.size() > 0) {
            sb.append(path.get(0).index);
        }
        for (int i = 1; i < path.size(); i++) {
            sb.append("," + path.get(i).index);
        }
        return sb.toString();
    }
    
    /**
     * Returns an array of {network, flow}, where network is a minimally cut
     * network, and flow is the maximum flow calculated during the process.
     * @param network
     * @param source
     * @param target
     * @return 
     */
    public static Object[] maxFlowMinCut(Network network, Node source, Node target) {
        Network newNet = network.copy();
        //newNet.clean();
        int flow = 0;
        
        while (true) {
            // Find path
            ArrayList<Node> path = pathBreadthFirst(newNet, newNet.nodes.get(source.index), newNet.nodes.get(target.index));
            if (path == null) {
                // No path found; algorithm finished.
                break;
            }
            //System.out.println(pathToString(path));
            
            // Find bottleneck size
            int minCap = Integer.MAX_VALUE;
            ArrayList<Edge> edges = new ArrayList<Edge>();
            for (int i = 0; i < path.size() - 1; i++) {
                for (Edge e : path.get(i).edgesOut) {
                    if (e.toNode == path.get(i + 1)) {
                        edges.add(e);
                        if (e.capacity < minCap) {
                            minCap = e.capacity;
                        }
                        break;
                    }
                }                
            }

            // Update capacities
            Iterator<Edge> ei = edges.iterator();
            while (ei.hasNext()) {
                Edge e = ei.next();
                e.capacity -= minCap;
                e.mirror.capacity += minCap;
            }
            
            // Update the max flow value
            flow += minCap;
        }
        return new Object[]{newNet, new Integer(flow)};
    }
    
    /**
     * Assumes the first and second nodes to be source and target, respectively.<br/>
     * Returns the max flow of the network, calculated by finding the minimum cut.
     * @param network
     * @return 
     */
    public static int maxFlow(Network network) {
        return ((Integer)maxFlowMinCut(network, network.nodes.get(0), network.nodes.get(1))[1]);
    }

    /**
     * Assumes the first and second nodes to be source and target, respectively.<br/>
     * Returns a network minimally cut.
     * @param network
     * @return 
     */
    public static Network minCut(Network network, boolean preCut) {
        Network cutNet = ((Network)maxFlowMinCut(network, network.nodes.get(0), network.nodes.get(1))[0]);
        if (preCut) {
            return cutNet;
        }
        Network result = network.copy();
        HashSet<Long> sourceSide = findCut(cutNet, cutNet.nodes.get(0), cutNet.nodes.get(1));
        for (Edge e : result.edges) {
            if (sourceSide.contains(e.fromNode.id) && !sourceSide.contains(e.toNode.id)) {
                e.capacity = 0;
            }
        }
        
        return result;
    }

    public static class CostPath {
        public ArrayList<Node> path = null;
        public int cost = -1;
        
        public CostPath(ArrayList<Node> path, int cost) {
            this.path = path;
            this.cost = cost;
        }
    }
    
    /**
     * Finds and returns the min-cost path from source to target,
     * or returns null if no path is found.  Interprets capacity as cost.
     * @param network
     * @param source
     * @param target
     * @param skipZeroResCaps
     * @return 
     */
    public static CostPath pathMinCost(Network network, Node source, Node target, boolean accountForNegativeCosts) {
        // Initialize markers
        HashMap<Node, Marker<Node>> findMarker = new HashMap<Node, Marker<Node>>();
        HashSet<Marker<Node>> darkNodes = new HashSet<Marker<Node>>();
        //HashSet<Marker<Node>> newNodes = new HashSet<Marker<Node>>();
        HashSet<Marker<Node>> activeNodes = new HashSet<Marker<Node>>();
        HashSet<Marker<Node>> doneNodes = new HashSet<Marker<Node>>();
        for (Node i : network.nodes) {
            Marker<Node> bucket = new Marker<Node>(i);
            findMarker.put(i, bucket);
            if (i == source) {
                bucket.state = Marker.STATE_ACTIVE;
                bucket.totalCost = 0;
                activeNodes.add(bucket);
            } else {
                darkNodes.add(bucket);
            }
        }
        
        // Find path
        boolean foundTarget = false;
        while (!foundTarget && activeNodes.size() > 0) {
            // Find next least--total-costly step
            int cheapestCost = -1;
            Marker<Node> cheapestFrom = null;
            Marker<Node> cheapestTo = null;
            for (Marker<Node> m : activeNodes) {
                for (Edge e : m.current.edgesOut) {
                    if (e.capacity == 0) {
                        continue;
                    }
                    if ((cheapestCost == -1) || (m.totalCost + e.cost < cheapestCost)) {
                        Marker<Node> intendedTo = findMarker.get(e.toNode);
                        if (intendedTo.state == Marker.STATE_DARK || (accountForNegativeCosts && intendedTo.totalCost > m.totalCost + e.cost)) {
                            cheapestCost = m.totalCost + e.cost;
                            cheapestFrom = m;
                            cheapestTo = intendedTo;
                        }
                    }
                }
            }
            if (cheapestTo != null) {
                cheapestTo.totalCost = cheapestCost;
                cheapestTo.previous = cheapestFrom;
                cheapestTo.state = Marker.STATE_ACTIVE;
                activeNodes.add(cheapestTo);
                darkNodes.remove(cheapestTo);
                doneNodes.remove(cheapestTo); // If negative edge, the node might have already been considered done.
                boolean foundDarkNeighbor = false;
                for (Edge e : cheapestFrom.current.edgesOut) {
                    if (findMarker.get(e.toNode).state == Marker.STATE_DARK) {
                        foundDarkNeighbor = true;
                        break;
                    }
                }
                if (!foundDarkNeighbor) {
                    // Set cheapestFrom to DONE; it has no more dark neighbors
                    cheapestFrom.state = Marker.STATE_DONE;
                    doneNodes.add(cheapestFrom);
                    activeNodes.remove(cheapestFrom);
                }
                if (cheapestTo.current == target && !accountForNegativeCosts) {
                    foundTarget = true;
                }
            } else {
                if (accountForNegativeCosts) {
                    if (findMarker.get(target).previous != null) {
                        foundTarget = true;
                    }
                }
                break;
            }
        }
        if (!foundTarget) {
            return null;
        }
        
        // Turn the results into a proper path
        ArrayList<Node> path = new ArrayList<Node>();
        Marker<Node> m = findMarker.get(target);
        path.add(target);
        while (m.previous != null) {
            path.add(m.previous.current);
            m = m.previous;
        }
        
        // Need to flip the path around to start from the source
        for (int i = 0; i < path.size() / 2; i++) {
            Node bucket = path.get(i);
            path.set(i, path.get((path.size() - 1) - i));
            path.set((path.size() - 1) - i, bucket);
        }
        
        return new CostPath(path, findMarker.get(target).totalCost);
    }
    
    /**
     * Reads in a representation of a set of matching preferences from a file
     * and returns an instance of the network representing it.
     * See readReader for a description of the syntax.
     * @param text
     * @return 
     */
    public static Network readMatchFilename(String filename) {
        FileReader fr = null;
        try {
            fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            return readMatchReader(br, mcf1());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GraphFunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GraphFunctions.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(GraphFunctions.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new Network();
    }
    
    public static MatchCostFunction mcf1() {
        return new MatchCostFunction() {
            public int getCost(int a, int b) {
                return Math.max(a, b) + Math.abs(a - b);
            }
        };
    }
    
    public interface MatchCostFunction {
        public abstract int getCost(int a, int b);
    }
    
    /**
     * Reads in a match preference list from a BufferedReader, probably from
     * either readFilename or readString.  The list should be formatted in the
     * following manner.  Supposing there to be n members of each side,
     * the first n lines are the preference lists of members 1-n of the first
     * group, each containing n space separated numbers from 1-n, representing
     * from best to worst the ordered list of indices of members of the other
     * group.  n additional lines follow the first n lines, similarly
     * representing the preferences of the second group.  All numbers should be
     * integers, and zero indexed.
     * @param br
     * @return 
     */
    public static Network readMatchReader(BufferedReader br, MatchCostFunction mcf) {
        Network result = new Network();
        try {
            ArrayList<String> lines = new ArrayList<String>();
            String line = br.readLine();
            while (line != null) {
                if (line.length() > 0) {
                    lines.add(line);
                }
                line = br.readLine();
            }
            if (lines.size() % 2 != 0) {
                System.err.println("Number of lines is not even: " + lines.size());
                return null;
            }
            int groupSize = lines.size() / 2;
            int[][] menPrefs = new int[groupSize][groupSize];
            int[][] womenPrefs = new int[groupSize][groupSize];
            for (int i = 0; i < groupSize; i++) {
                line = lines.get(i).trim();
                line = line.replaceAll(" +", " ");
                line = line.replaceAll("[^0-9 ]*", "");
                String[] bits = line.split(" ");
                if (bits.length != groupSize) {
                    System.err.println("Number of prefs mismatch: " + groupSize + " expected vs. " + bits.length + " received on line " + i);
                }
                for (int j = 0; j < groupSize; j++) {
                    // For man i, index j holds the rank of woman j
                    menPrefs[i][Integer.valueOf(bits[j])] = j + 1;
                }
            }
            for (int i = 0; i < groupSize; i++) {
                line = lines.get(i + groupSize).trim();
                line = line.replaceAll(" +", " ");
                line = line.replaceAll("[^0-9 ]*", "");
                String[] bits = line.split(" ");
                if (bits.length != groupSize) {
                    System.err.println("Number of prefs mismatch: " + groupSize + " expected vs. " + bits.length + " received on line " + (i + groupSize));
                }
                for (int j = 0; j < groupSize; j++) {
                    // For woman i, index j holds the rank of man j
                    womenPrefs[i][Integer.valueOf(bits[j])] = j + 1;
                }
            }
            // Turn it into a network
            Node source = new Node(0, Network.SCALING * 0, Network.SCALING * 0.5);
            Node target = new Node(1, Network.SCALING * 1, Network.SCALING * 0.5);
            result.nodes.add(source);
            result.nodes.add(target);
            ArrayList<Node> men = new ArrayList<Node>();
            ArrayList<Node> women = new ArrayList<Node>();
            for (int i = 0; i < groupSize; i++) {
                Node bucket = new Node(2 + i, Network.SCALING * 0.3, Network.SCALING * (i / (groupSize - 1.0)));
                men.add(bucket);
                result.nodes.add(bucket);
            }
            for (int i = 0; i < groupSize; i++) {
                Node bucket = new Node(2 + groupSize + i, Network.SCALING * 0.7, Network.SCALING * (i / (groupSize - 1.0)));
                women.add(bucket);
                result.nodes.add(bucket);
            }
            for (int i = 0; i < groupSize; i++) {
                Edge bucket = new Edge(source, men.get(i), 1, 0);
                source.connections.add(men.get(i));
                source.edgesOut.add(bucket);
                men.get(i).edgesIn.add(bucket);
                result.edges.add(bucket);
            }
            for (int i = 0; i < groupSize; i++) {
                Edge bucket = new Edge(women.get(i), target, 1, 0);
                women.get(i).connections.add(target);
                women.get(i).edgesOut.add(bucket);
                target.edgesIn.add(bucket);
                result.edges.add(bucket);
            }
            for (int i = 0; i < groupSize; i++) {
                for (int j = 0; j < groupSize; j++) {
                    Edge bucket = new Edge(men.get(i), women.get(j), 1, mcf.getCost(menPrefs[i][j], womenPrefs[j][i]));
                    men.get(i).connections.add(women.get(j));
                    men.get(i).edgesOut.add(bucket);
                    women.get(j).edgesIn.add(bucket);
                    result.edges.add(bucket);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GraphFunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GraphFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
    
    /**
     * Returns an array of {network, flow, cost}, where network is a minimally cut
     * network, flow is the maximum flow calculated during the process, and cost
     * is the cost incurred = edge cost * edge flow.
     * WARNING: Edges must be one-way.  Two-way edges totally messes up the combination
     * of backflow tracking and costs.  (It'll delete existing 0-capacity connections
     * for you.)  How do I represent the idea that an edge
     * can take 2 units of flow at negative cost, but then 1 unit at a positive cost?
     * Terrible pain.
     * Also, I'm not mathematically sure that it will properly deal with capacities
     * greater than 1; the costs might not add up to be minimum, in such circumstances.
     * @param network
     * @param source
     * @param target
     * @return 
     */
    public static Object[] maxFlowMinCutMinCost(Network network, Node source, Node target) {
        Network newNet = network.copy();
        newNet.removeFakeEdges();
        newNet.prepareCostMirrors();
        //newNet.clean();
        int flow = 0;
        
        while (true) {
            // Find path
            CostPath cpath = pathMinCost(newNet, newNet.nodes.get(source.index), newNet.nodes.get(target.index), true);            
            if (cpath == null) {
                // No path found; algorithm finished.
                break;
            }
            ArrayList<Node> path = cpath.path;
            //System.out.println(pathToString(path));
            
            // Find bottleneck size
            int minCap = Integer.MAX_VALUE;
            ArrayList<Edge> edges = new ArrayList<Edge>();
            for (int i = 0; i < path.size() - 1; i++) {
                for (Edge e : path.get(i).edgesOut) {
                    if (e.toNode == path.get(i + 1)) {
                        edges.add(e);
                        if (e.capacity < minCap) {
                            minCap = e.capacity;
                        }
                        break;
                    }
                }                
            }

            // Update capacities
            Iterator<Edge> ei = edges.iterator();
            while (ei.hasNext()) {
                Edge e = ei.next();
                e.capacity -= minCap;
                e.mirror.capacity += minCap;
                // How do I convey the idea that up to minCap flow can be absorbed
                //     by the mirror at negative cost, but no more than that?
                //     I'm going to just assume that this won't be used for anything
                //     except stable matching problems....
                //e.mirror.cost
            }
            
            // Update the max flow value
            flow += minCap;
        }
        return new Object[]{newNet, new Integer(flow)};
    }

     public static Network stableMatch(Network network, boolean preCut) {
        Network cutNet = ((Network)maxFlowMinCutMinCost(network, network.nodes.get(0), network.nodes.get(1))[0]);
        if (preCut) {
            return cutNet;
        }
        Network result = network.copy();
        HashSet<Long> sourceSide = findCut(cutNet, cutNet.nodes.get(0), cutNet.nodes.get(1));
        for (Edge e : result.edges) {
            if (sourceSide.contains(e.fromNode.id) && !sourceSide.contains(e.toNode.id)) {
                e.capacity = 0;
            }
        }
        
        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Here's an example of how one could use these functions.
        Network network = readFilename("data_250.txt");
        System.out.println("Max Flow: " + maxFlow(network));
        // or
        Object[] result = maxFlowMinCut(network, network.nodes.get(0), network.nodes.get(1));
        
        Network preCutNet = ((Network)result[0]);
        System.out.println("Max Flow: " + ((Integer)result[1]));
        HashSet<Long> sourceSideIDs = findCut(preCutNet, preCutNet.nodes.get(0), preCutNet.nodes.get(1));
        LinkedList<Node> sourceSideNodes = new LinkedList<Node>();
        LinkedList<Node> targetSideNodes = new LinkedList<Node>();
        for (Node n : preCutNet.nodes) {
            if (sourceSideIDs.contains(n.id)) {
                sourceSideNodes.add(n);
            } else {
                targetSideNodes.add(n);
            }
        }
        System.out.println("Source side nodes: ");
        StringBuilder sb = new StringBuilder();
        for (Node n : sourceSideNodes) {
            sb.append(n.index + " ");
        }
        System.out.println(sb.toString());
        System.out.println();
        System.out.println("Target side nodes: ");
        sb = new StringBuilder();
        for (Node n : targetSideNodes) {
            sb.append(n.index + " ");
        }
        System.out.println(sb.toString());
    }
}