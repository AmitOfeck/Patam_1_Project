package test;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    public final String name;
    private final List<Agent> subs;  
    private final List<Agent> pubs;  

    public Topic(String name) {
        this.name = name;
        this.subs = new ArrayList<>();
        this.pubs = new ArrayList<>();
    }

    public void subscribe(Agent a) {
        if (!subs.contains(a)) {  
            subs.add(a);
        }
    }

    public void unsubscribe(Agent a) {
        subs.remove(a);
    }

    public void publish(Message m) {
        for (Agent agent : subs) {
            agent.callback(name, m);  
        }
    }

    public void addPublisher(Agent a) {
        if (!pubs.contains(a)) {  
            pubs.add(a);
        }
    }

    public void removePublisher(Agent a) {
        pubs.remove(a);
    }
}
