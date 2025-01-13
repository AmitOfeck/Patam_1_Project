package test;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    public final String name;
    public final List<Agent> subs;
    public final List<Agent> pubs;

    Topic(String name) {
        this.name = name;
        this.subs = new ArrayList<Agent>();
        this.pubs = new ArrayList<Agent>();
        // System.out.println("Created Topic with name: " + this.name);
    }

    public String getName() {
        return this.name;
    }

    public void subscribe(Agent a) {
        if (!subs.contains(a)) {
            subs.add(a);
            // System.out.println("Agent " + a.getName() + " subscribed to topic: " + this.name);
        } else {
            // System.out.println("Agent " + a.getName() + " is already subscribed to topic: " + this.name);
        }
    }

    public void unsubscribe(Agent a) {
        if (subs.contains(a)) {
            subs.remove(a);
            // System.out.println("Agent " + a.getName() + " unsubscribed from topic: " + this.name);
        } else {
            // System.out.println("Agent " + a.getName() + " was not subscribed to topic: " + this.name);
        }
    }

    public void publish(Message msg) {
        // System.out.println("Publishing message to subscribers of topic: " + this.name);
        for (Agent agent : this.subs) {
            agent.callback(this.name, msg);
            // System.out.println("Message sent to agent: " + agent.getName());
        }
    }

    public void addPublisher(Agent a) {
        if (!pubs.contains(a)) {
            pubs.add(a);
            // System.out.println("Agent " + a.getName() + " added as publisher to topic: " + this.name);
        } else {
            // System.out.println("Agent " + a.getName() + " is already a publisher of topic: " + this.name);
        }
    }

    public void removePublisher(Agent a) {
        if (pubs.contains(a)) {
            pubs.remove(a);
            // System.out.println("Agent " + a.getName() + " removed as publisher from topic: " + this.name);
        } else {
            // System.out.println("Agent " + a.getName() + " was not a publisher of topic: " + this.name);
        }
    }

    public List<Agent> getSubs() {
        return subs;
    }

    public List<Agent> getPubs() {
        return pubs;
    }
}
