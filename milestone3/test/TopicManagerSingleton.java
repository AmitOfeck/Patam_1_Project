package test;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;

public class TopicManagerSingleton {

    public static class TopicManager {
        private final static TopicManager instance = new TopicManager();

        public final Map<String, Topic> topics;

        private TopicManager() {
            this.topics = new HashMap<>();
            // System.out.println("TopicManager instance created.");
        }

        public Topic getTopic(String name) {
            // System.out.println("Getting topic with name: " + name);
            return topics.computeIfAbsent(name, (topicName) -> {
                // System.out.println("Topic " + topicName + " does not exist, creating a new one.");
                return new Topic(topicName);
            });
        }

        public Collection<Topic> getTopics() {
            // System.out.println("Retrieving all topics.");
            return new ArrayList<>(topics.values());
        }

        public void clear() {
            // System.out.println("Clearing all topics.");
            this.topics.clear();
        }
    }

    public static TopicManager get() {
        // System.out.println("Accessing TopicManager instance.");
        return TopicManager.instance;
    }

}
