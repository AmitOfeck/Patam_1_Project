package test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelAgent implements Agent {
    private final Agent agent;

    public ParallelAgent(Agent a) {
        this.agent = a;
        // System.out.println("ParallelAgent created for agent: " + a.getName());
    }

    @Override
    public String getName() {
        // System.out.println("Getting name of agent: " + agent.getName());
        return agent.getName();
    }

    @Override
    public void reset() {
        // System.out.println("Resetting agent: " + agent.getName());
        agent.reset();
    }

    @Override
    public void close() {
        // System.out.println("Closing agent: " + agent.getName());
        agent.close();
    }

    @Override
    public void callback(String topic, Message message) {
        // System.out.println("Agent " + agent.getName() + " received callback for topic: " + topic + " with message: " + message);
        agent.callback(topic, message);
    }
}
