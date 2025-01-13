package test;

import java.util.*;

public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;

    Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
        // System.out.println("Node created: " + name);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        // System.out.println("Setting node name from " + this.name + " to " + name);
        this.name = name;
    }

    public List<Node> getEdges() {
        return this.edges;
    }

    public void setNode(List<Node> l) {
        // System.out.println("Setting edges for node: " + this.name);
        this.edges = l;
    }

    public Message getMsg() {
        return this.msg;
    }

    public void setMsg(Message msg) {
        // System.out.println("Setting message for node: " + this.name);
        this.msg = msg;
    }

    public void addEdge(Node node) {
        if (!edges.contains(node)) {
            edges.add(node);
            // System.out.println("Edge added from " + this.name + " to " + node.getName());
        } else {
            // System.out.println("Edge already exists from " + this.name + " to " + node.getName());
        }
    }

    public void removeEdge(Node node) {
        if (edges.contains(node)) {
            edges.remove(node);
            // System.out.println("Edge removed from " + this.name + " to " + node.getName());
        } else {
            // System.out.println("Edge does not exist from " + this.name + " to " + node.getName());
        }
    }

    public boolean hasCycles() {
        Set<Node> visited = new HashSet<>();
        Set<Node> stack = new HashSet<>();
        // System.out.println("Checking for cycles in node: " + this.name);

        for (Node node : edges) {
            if (!visited.contains(node)) {
                // System.out.println("Visiting edge from " + this.name + " to " + node.getName());
                if (detectCycleRecursively(node, visited, stack)) {
                    // System.out.println("Cycle detected via node: " + node.getName());
                    return true;
                }
            }
        }
        // System.out.println("No cycles detected for node: " + this.name);
        return false;
    }

    private boolean detectCycleRecursively(Node current, Set<Node> visited, Set<Node> stack) {
        stack.add(current);
        visited.add(current);
        // System.out.println("Visiting node: " + current.getName());

        for (Node node : current.getEdges()) {
            if (!visited.contains(node)) {
                if (detectCycleRecursively(node, visited, stack)) {
                    return true;
                }
            } else if (stack.contains(node)) {
                // System.out.println("Cycle detected, returning from node: " + current.getName());
                return true;
            }
        }
        stack.remove(current);
        // System.out.println("Backtracking from node: " + current.getName());
        return false;
    }
}
