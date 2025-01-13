package test;

import test.TopicManagerSingleton.TopicManager;

public class PlusAgent implements Agent {
    private final String agentName;
    private final Topic[] subscribedTopics;
    private final Topic outputTopic;
    private double firstValue = 0;
    private double secondValue = 0;

    public PlusAgent(String[] inputTopics, String[] outputTopics) {
        if (inputTopics.length < 2 || outputTopics.length < 1) {
            throw new IllegalArgumentException("PlusAgent requires at least 2 input topics and 1 output topic.");
        }

        this.agentName = "PlusAgent";
        TopicManager topicManager = TopicManagerSingleton.get();
        this.subscribedTopics = new Topic[]{topicManager.getTopic(inputTopics[0]), topicManager.getTopic(inputTopics[1])};
        this.outputTopic = topicManager.getTopic(outputTopics[0]);

        // System.out.println("PlusAgent subscribing to topics: " + inputTopics[0] + ", " + inputTopics[1]);
        this.subscribedTopics[0].subscribe(this);
        this.subscribedTopics[1].subscribe(this);

        // System.out.println("PlusAgent adding publisher to output topic: " + outputTopics[0]);
        this.outputTopic.addPublisher(this);
    }

    @Override
    public String getName() {
        // System.out.println("Getting name of agent: " + this.agentName);
        return this.agentName;
    }

    @Override
    public void reset() {
        this.firstValue = 0;
        this.secondValue = 0;
        // System.out.println("Agent " + agentName + " reset values: firstValue = " + firstValue + ", secondValue = " + secondValue);
    }

    @Override
    public void close() {
        // System.out.println("Agent " + agentName + " unsubscribing from topics and removing publisher.");
        subscribedTopics[0].unsubscribe(this);
        subscribedTopics[1].unsubscribe(this);
        outputTopic.removePublisher(this);
    }

    @Override
    public void callback(String topicName, Message message) {
        // System.out.println("Agent " + agentName + " received callback for topic: " + topicName + " with message: " + message);
        if (topicName.equals(subscribedTopics[0].name)) {
            firstValue = message.asDouble;
            // System.out.println("Agent " + agentName + " updated firstValue to: " + firstValue);
        } else if (topicName.equals(subscribedTopics[1].name)) {
            secondValue = message.asDouble;
            // System.out.println("Agent " + agentName + " updated secondValue to: " + secondValue);
        }

        if (!Double.isNaN(firstValue) && !Double.isNaN(secondValue)) {
            double result = firstValue + secondValue;
            // System.out.println("Agent " + agentName + " calculated result: " + result);
            outputTopic.publish(new Message(result));
        }
    }
}
