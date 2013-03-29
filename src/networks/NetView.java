/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package networks;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import mathnstuff.MeMath;

/**
 *
 * @author mewer12
 */
public class NetView extends JPanel {

    public Network network = null;
    public double xTrans = 0;
    public double yTrans = 0;
    public double xScale = 1;
    public double yScale = 1;
    public Ellipse2D.Double origin = new Ellipse2D.Double(-4, -4, 8, 8);
    public int mouseWasX = -1;
    public int mouseWasY = -1;
    public ArrayList<Node> selected = new ArrayList<Node>();
    public HashSet<Edge> highlightedEdges = new HashSet<Edge>();
    public static final Color COLOR_BACKGROUND = Color.gray;
    public static final Color COLOR_ORIGIN = Color.black;
    public static final Color COLOR_NODE = Color.blue;
    public static final Color COLOR_SELNODE = Color.green.darker();
    public static final Color COLOR_CONNECTION = Color.orange.darker();
    public static final Color COLOR_CONNECTION_DIR = Color.orange;
    public static final Color COLOR_CONNECTION_CAPACITY = COLOR_CONNECTION;
    public static final Color COLOR_CONNECTION_COST = Color.green;
    public boolean hideFakeEdges = false;
    public boolean hideFakeEdgeMirrors = false;
    public boolean hideNonFakeEdges = false;
    public boolean hideNonFakePairs = false;
    public boolean showEdgeCapacities = true;
    public boolean showEdgeCosts = true;

    public NetView(Network network) {
        this.network = network;
        this.setFocusable(true);
    }
    
    public Color alterColorIfHighlighted(Edge e, Color c) {
        if (highlightedEdges.contains(e)) {
            return c.brighter();
        } else {
            return c;
        }
    }

    @Override
    protected void paintComponent(Graphics g1) {
        super.paintComponent(g1);
        Graphics2D g = (Graphics2D) g1;
        g.setColor(COLOR_BACKGROUND);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(COLOR_ORIGIN);
        //TODO Fix scaling to be more natural
        g.translate(xTrans, yTrans);
        g.scale(xScale, yScale);
        g.draw(origin);
        g.setColor(COLOR_NODE);
        FontMetrics fMetrics = g.getFontMetrics();
        double tOffset = fMetrics.getHeight() / 4.0;
        for (Node n : network.nodes) {
            //TODO Optimize this.
            g.draw(new Ellipse2D.Double(n.x - 4, n.y - 4, 8, 8));
            for (Edge e : n.edgesOut) {
                if (hideFakeEdges && e.capacity == 0) {
                    continue;
                }
                if (hideNonFakeEdges && e.capacity != 0) {
                    continue;
                }
                if (hideNonFakePairs && (e.capacity != 0 || (e.mirror != null && e.mirror.capacity != 0))) {
                    continue;
                }
                if (hideFakeEdgeMirrors && e.mirror != null && e.mirror.capacity == 0) {
                    continue;
                }
                Node c = e.toNode;
                //TODO Optimize: only draw connection once?
                // Making this directional
                double norm = Math.sqrt(MeMath.sqr(c.x - n.x) + MeMath.sqr(c.y - n.y));
                if (norm == 0) {
                    continue;
                }
                double dx = ((c.y - n.y) / norm);
                double dy = ((n.x - c.x) / norm);
                g.setColor(alterColorIfHighlighted(e, COLOR_CONNECTION));
                g.draw(new java.awt.geom.Line2D.Double(n.x + dx, n.y + dy, c.x + dx, c.y + dy));
                double mx = (c.x - n.x) / 5;
                double my = (c.y - n.y) / 5;
                g.setColor(alterColorIfHighlighted(e, COLOR_CONNECTION_DIR));
                g.draw(new java.awt.geom.Line2D.Double(c.x + dx - mx, c.y + dy - my, c.x + dx, c.y + dy));
                //g.drawString(Integer.toString(e.capacity), (float)n.x, (float)(n.y + tOffset));
                //g.drawString(Integer.toString(e.capacity), (float)n.x, (float)n.y);
                if (showEdgeCapacities) {
                    g.setColor(alterColorIfHighlighted(e, COLOR_CONNECTION_CAPACITY));
                    g.drawString(Integer.toString(e.capacity), (float)((((3 * n.x) + c.x) / 4.0) + (10 * dx) - (fMetrics.stringWidth(Integer.toString(e.capacity))) / 2.0), (float)((((3 * n.y) + c.y) / 4.0) + (10 * dy) + tOffset));
//                    g.drawString(Integer.toString(e.capacity), (float)((((3 * c.x) + n.x) / 4.0) + (10 * dx) - (fMetrics.stringWidth(Integer.toString(e.capacity))) / 2.0), (float)((((3 * c.y) + n.y) / 4.0) + (10 * dy) + tOffset));
                }
                if (showEdgeCosts) {
                    g.setColor(alterColorIfHighlighted(e, COLOR_CONNECTION_COST));
                    g.drawString(Integer.toString(e.cost), (float)((((3 * n.x) + (2 * c.x)) / 5.0) + (10 * dx) - (fMetrics.stringWidth(Integer.toString(e.capacity))) / 2.0), (float)((((3 * n.y) + (2 * c.y)) / 5.0) + (10 * dy) + tOffset));
//                    g.drawString(Integer.toString(e.capacity), (float)((((3 * c.x) + n.x) / 4.0) + (10 * dx) - (fMetrics.stringWidth(Integer.toString(e.capacity))) / 2.0), (float)((((3 * c.y) + n.y) / 4.0) + (10 * dy) + tOffset));
                }
            }
            g.setColor(COLOR_NODE);
        }
        g.setColor(COLOR_SELNODE);
        for (Node n : selected) {
            //TODO Optimize this.
            g.draw(new Ellipse2D.Double(n.x - 4, n.y - 4, 8, 8));
        }
    }
    public static final double PRESCALE_FACTOR = -0.2;
    public static final double SCALE_BASE = 2;

    public void repaintWV() {
        //parent.jTextArea1.setText(network.toFile());
        this.repaint();
    }

    public void init() {
        // Add initial node
//        Node node = new Node();
//        node.x = 32;
//        node.y = 32;
//        network.nodes.add(node);

        this.addMouseWheelListener(new MouseWheelListener() {

            public void mouseWheelMoved(MouseWheelEvent e) {
                double factor = Math.pow(SCALE_BASE, PRESCALE_FACTOR * e.getWheelRotation());
                xScale *= factor;
                yScale *= factor;
                xTrans = e.getX() - ((e.getX() - xTrans) * factor);
                yTrans = e.getY() - ((e.getY() - yTrans) * factor);
                repaintWV();
            }
        });
        this.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    double clickX = (e.getX() - xTrans) / xScale;
                    double clickY = (e.getY() - yTrans) / yScale;
                    if (e.getClickCount() > 1) {
                        Node node = new Node(network.nodes.size(), clickX, clickY);
                        network.nodes.add(node);
                    } else {
                        double dist = -1;
                        Node nearest = null;
                        for (Node n : network.nodes) {
                            double newDist = MeMath.sqr(n.x - clickX) + MeMath.sqr(n.y - clickY);
                            if (nearest == null || newDist < dist) {
                                nearest = n;
                                dist = newDist;
                            }
                        }
                        if (nearest == null) {
                            return;
                        }
                        if (e.isControlDown()) {
//                            if (!selected.add(nearest)) {
//                                selected.remove(nearest);
//                            }
                            if (!selected.contains(nearest)) {
                                selected.add(nearest);
                            } else {
                                selected.remove(nearest);
                            }
                        } else {
                            if (!selected.contains(nearest) || selected.size() != 1) {
                                selected.clear();
                                selected.add(nearest);
                            } else {
                                selected.clear();
                            }
                        }
                    }
                    repaintWV();
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem mitemToggleConnection = new JMenuItem("Toggle Connection");
                    JMenuItem mitemRemoveNode = new JMenuItem("Remove");
                    mitemToggleConnection.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent event) {
                            Object[] nodes = selected.toArray();
                            if (((Node) nodes[0]).connections.contains(nodes[1])) {
                                for (int i = 0; i < network.edges.size(); i++) {
                                    if (network.edges.get(i).fromNode == nodes[0] && network.edges.get(i).toNode == nodes[1]) {
                                        network.edges.get(i).fromNode.edgesOut.remove(network.edges.get(i));
                                        network.edges.get(i).toNode.edgesIn.remove(network.edges.get(i));
                                        network.edges.remove(i);
                                        break;
                                    }
                                }
                                ((Node) nodes[0]).connections.remove(nodes[1]);
                                //((Node) nodes[1]).connections.remove(nodes[0]);
                            } else {
                                //TODO Allow set capacity
                                Edge bucket = new Edge(((Node) nodes[0]), ((Node) nodes[1]), 1, 1);
                                bucket.fromIndex = bucket.fromNode.index;
                                bucket.toIndex = bucket.toNode.index;
//                                bucket.fromNode.edgesOut.add(bucket);
//                                bucket.toNode.edgesIn.add(bucket);
//                                network.edges.add(bucket);
//                                ((Node) nodes[0]).connections.add(((Node) nodes[1]));
//                                //((Node) nodes[1]).connections.add(((Node) nodes[0]));
                                bucket.fromNode.connections.add(bucket.toNode);
                                bucket.toNode.edgesIn.add(bucket);
                                bucket.fromNode.edgesOut.add(bucket);
                                network.edges.add(bucket);
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
                                    network.edges.add(mirror);
                                }
                            }
                            repaintWV();
                        }
                    });

                    mitemRemoveNode.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            for (Node n : selected) {
                                for (Node c : network.nodes) {
                                    c.connections.remove(n);
                                }
                                Iterator<Edge> ei = network.edges.iterator();
                                while (ei.hasNext()) {
                                    Edge edge = ei.next();
                                    if (edge.fromNode == n || edge.toNode == n) {
                                        edge.fromNode.edgesOut.remove(edge);
                                        edge.toNode.edgesIn.remove(edge);
                                        ei.remove();
                                    }
                                }
                                network.nodes.remove(n);
                            }
                            selected.clear();
                            // I don't think the node
                            //network.reIndex();
                            repaintWV();
                        }
                    });
                    if (selected.size() == 2) {
                        menu.add(mitemToggleConnection);
                    }
                    if (selected.size() > 0) {
                        menu.add(mitemRemoveNode);
                    }
                    menu.show(NetView.this, e.getX(), e.getY());
                }
            }

            public void mousePressed(MouseEvent e) {
                requestFocus();
                if (e.getButton() == MouseEvent.BUTTON1) {
                    mouseWasX = e.getX();
                    mouseWasY = e.getY();
                }
            }

            public void mouseReleased(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void mouseEntered(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void mouseExited(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        this.addMouseMotionListener(new MouseMotionListener() {

            public void mouseDragged(MouseEvent e) {
                xTrans += e.getX() - mouseWasX;
                yTrans += e.getY() - mouseWasY;
                mouseWasX = e.getX();
                mouseWasY = e.getY();
                repaintWV();
            }

            public void mouseMoved(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        this.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent ke) {
                switch (ke.getKeyChar()) {
                    case '=':
                    case '+':
                        if (selected.size() >= 2) {
                            for (int i = 0; i < selected.size() - 1; i++) {
                                Edge ed = null;
                                for (Edge e : selected.get(i).edgesOut) {
                                    if (e.toNode == selected.get(i + 1)) {
                                        ed = e;
                                        break;
                                    }
                                }
                                if (ed != null) {
                                    ed.capacity++;
                                } else {
                                    Edge bucket = new Edge(selected.get(i), selected.get(i + 1), 1, 1);
                                    bucket.fromIndex = bucket.fromNode.index;
                                    bucket.toIndex = bucket.toNode.index;
                                    bucket.fromNode.edgesOut.add(bucket);
                                    bucket.toNode.edgesIn.add(bucket);
                                    network.edges.add(bucket);
                                    selected.get(i).connections.add(selected.get(i + 1));
                                }
                            }
                            repaintWV();
                        }
                        break;
                    case '-':
                        if (selected.size() >= 2) {
                            for (int i = 0; i < selected.size() - 1; i++) {
                                Edge ed = null;
                                for (Edge e : selected.get(i).edgesOut) {
                                    if (e.toNode == selected.get(i + 1)) {
                                        ed = e;
                                        break;
                                    }
                                }
                                if (ed != null) {
                                    ed.capacity--;
                                    if (ed.capacity == 0) {
                                        for (int j = 0; j < network.edges.size(); j++) {
                                            if (network.edges.get(j).fromNode == selected.get(i) && network.edges.get(j).toNode == selected.get(i + 1)) {
                                                network.edges.get(j).fromNode.edgesOut.remove(network.edges.get(j));
                                                network.edges.get(j).toNode.edgesIn.remove(network.edges.get(j));
                                                network.edges.remove(j);
                                                break;
                                            }
                                        }
                                        selected.get(i).connections.remove(selected.get(i + 1));
                                    }
                                }
                            }
                            repaintWV();
                        }
                        break;
                    case '0':
                        if (selected.size() >= 2) {
                            for (int i = 0; i < selected.size() - 1; i++) {
                                Edge ed = null;
                                for (Edge e : selected.get(i).edgesOut) {
                                    if (e.toNode == selected.get(i + 1)) {
                                        ed = e;
                                        break;
                                    }
                                }
                                if (ed == null) {
                                    Edge bucket = new Edge(selected.get(i), selected.get(i + 1), 0, 1);
                                    bucket.fromIndex = bucket.fromNode.index;
                                    bucket.toIndex = bucket.toNode.index;
                                    bucket.fromNode.edgesOut.add(bucket);
                                    bucket.toNode.edgesIn.add(bucket);
                                    network.edges.add(bucket);
                                    selected.get(i).connections.add(selected.get(i + 1));
                                }
                            }
                            repaintWV();
                        }
                        break;
                    case 'h':
                        if (selected.size() >= 2) {
                            ArrayList<Edge> eds = new ArrayList<Edge>();
                            boolean allHighlighted = true;
                            for (int i = 0; i < selected.size() - 1; i++) {
                                for (Edge e : selected.get(i).edgesOut) {
                                    if (e.toNode == selected.get(i + 1)) {
                                        eds.add(e);
                                        if (!highlightedEdges.contains(e)) {
                                            allHighlighted = false;
                                        }
                                        break;
                                    }
                                }
                            }
                            if (allHighlighted) {
                                highlightedEdges.removeAll(eds);
                            } else {
                                highlightedEdges.addAll(eds);
                            }
                            repaintWV();
                        }
                        break;
                    case 'H':
                        highlightedEdges.clear();
                        repaintWV();
                        break;
                    default:
                        break;
                }
            }

            public void keyPressed(KeyEvent ke) {
            }

            public void keyReleased(KeyEvent ke) {
            }
        });
    }
}
