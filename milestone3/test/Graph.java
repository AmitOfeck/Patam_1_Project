package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.Map;
import test.TopicManagerSingleton;
import test.TopicManagerSingleton.TopicManager;

public class Graph extends ArrayList<Node> {

    private Map<String, Node> nodesMap = new HashMap<>();

    public Graph() {
        super();
        // System.out.println("Graph initialized.");
    }

    public boolean hasCycles() {
        for (Node currentNode : this) {
            if (currentNode.hasCycles()) {
                // System.out.println("Cycle detected in node: " + currentNode.getName());
                return true;
            }
        }
        // System.out.println("No cycles detected in the graph.");
        return false;
    }

    public void clearGraph() {
        // System.out.println("Clearing graph...");
        for (Node currentNode : this) {
            currentNode.getEdges().clear();
            // System.out.println("Cleared edges for node: " + currentNode.getName());
        }
        this.clear();
        nodesMap.clear();
        // System.out.println("Graph cleared.");
    }

    public void createFromTopics() {
        // System.out.println("Creating graph from topics...");
        clearGraph();
        TopicManager topicManager = TopicManagerSingleton.get();
        Collection<Topic> allTopics = topicManager.getTopics();

        for (Topic topic : allTopics) {
            // System.out.println("Processing topic: " + topic.getName());
            Node topicNode = getOrCreateNode("T" + topic.name);

            for (Agent subscriber : topic.subs) {
                // System.out.println("Adding edge from topic " + topic.getName() + " to agent: " + subscriber.getName());
                Node agentNode = getOrCreateNode("A" + subscriber.getName());
                topicNode.addEdge(agentNode);
            }

            for (Agent publisher : topic.pubs) {
                // System.out.println("Adding edge from agent " + publisher.getName() + " to topic: " + topic.getName());
                Node agentNode = getOrCreateNode("A" + publisher.getName());
                agentNode.addEdge(topicNode);
            }
        }
        // System.out.println("Graph created from topics.");
    }

    private Node getOrCreateNode(String nodeName) {
        Node node = nodesMap.get(nodeName);
        if (node == null) {
            // System.out.println("Node does not exist, creating new node: " + nodeName);
            node = new Node(nodeName);
            this.add(node);
            nodesMap.put(nodeName, node);
        } else {
            // System.out.println("Node already exists: " + nodeName);
        }
        return node;
    }
}
