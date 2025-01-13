package test;

public class IncAgent implements Agent {
    private final String agentName;
    private final Topic inputTopic;
    private final Topic outputTopic;

    public IncAgent(String[] inputTopics, String[] outputTopics) {
        if (inputTopics.length < 1 || outputTopics.length < 1) {
            throw new IllegalArgumentException("IncAgent requires at least 1 input topic and 1 output topic.");
        }

        this.agentName = "IncAgent";
        TopicManagerSingleton.TopicManager topicManager = TopicManagerSingleton.get();
        this.inputTopic = topicManager.getTopic(inputTopics[0]);
        this.outputTopic = topicManager.getTopic(outputTopics[0]);

        // System.out.println("IncAgent subscribing to input topic: " + inputTopics[0]);
        this.inputTopic.subscribe(this);

        // System.out.println("IncAgent adding publisher to output topic: " + outputTopics[0]);
        this.outputTopic.addPublisher(this);
    }

    @Override
    public String getName() {
        // System.out.println("Getting name of agent: " + this.agentName);
        return agentName;
    }

    @Override
    public void reset() {
        // System.out.println("IncAgent " + agentName + " reset called. No state to reset.");
    }

    @Override
    public void close() {
        // System.out.println("IncAgent " + agentName + " unsubscribing from input topic and removing publisher from output topic.");
        inputTopic.unsubscribe(this);
        outputTopic.removePublisher(this);
    }

    @Override
    public void callback(String topicName, Message message) {
        // System.out.println("IncAgent " + agentName + " received callback for topic: " + topicName + " with message: " + message);
        if (topicName.equals(inputTopic.name)) {
            double incrementedValue = message.asDouble + 1;
            // System.out.println("IncAgent " + agentName + " incremented value: " + message.asDouble + " to: " + incrementedValue);
            outputTopic.publish(new Message(incrementedValue));
        }
    }
}
