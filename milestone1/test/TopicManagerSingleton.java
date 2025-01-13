package test;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class TopicManagerSingleton {

    public static class TopicManager {

        private final ConcurrentHashMap<String, Topic> topicsMap;  

        private TopicManager() {
            topicsMap = new ConcurrentHashMap<>();
        }

        public synchronized Topic getTopic(String name) {
            return topicsMap.computeIfAbsent(name, Topic::new);
        }

        public Collection<Topic> getTopics() {
            return topicsMap.values();
        }

        public void clear() {
            topicsMap.clear();
        }
    }

    private static final TopicManager instance = new TopicManager();

    public static TopicManager get() {
        return instance;
    }
}
