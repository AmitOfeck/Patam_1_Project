package test;

import java.util.function.BinaryOperator;
import test.TopicManagerSingleton.TopicManager;

public class BinOpAgent implements Agent {

    private final String agentName;
    private final TopicManager topicManager;
    private double inputValue1;
    private double inputValue2;
    private final Topic inputTopic1;
    private final Topic inputTopic2;
    private final Topic outputTopic;
    private final BinaryOperator<Double> operation;

    public BinOpAgent(String name, String firstTopic, String secondTopic, String outputTopicName, BinaryOperator<Double> operation) {
        this.agentName = name;
        this.topicManager = TopicManagerSingleton.get();
        this.inputTopic1 = topicManager.getTopic(firstTopic);
        this.inputTopic2 = topicManager.getTopic(secondTopic);
        this.outputTopic = topicManager.getTopic(outputTopicName);
        this.operation = operation;

        // System.out.println("Agent " + name + " subscribing to topics: " + firstTopic + ", " + secondTopic);
        this.inputTopic1.subscribe(this);
        this.inputTopic2.subscribe(this);

        // System.out.println("Agent " + name + " adding publisher to output topic: " + outputTopicName);
        this.outputTopic.addPublisher(this);

        reset();
    }

    @Override
    public String getName() {
        return this.agentName;
    }

    @Override
    public void close() {
        // System.out.println("Agent " + agentName + " closing.");
    }

    @Override
    public void reset() {
        this.inputValue1 = 0.0;
        this.inputValue2 = 0.0;
        // System.out.println("Agent " + agentName + " reset values: inputValue1 = " + inputValue1 + ", inputValue2 = " + inputValue2);
    }

    @Override
    public void callback(String topic, Message message) {
        if (Double.isNaN(message.asDouble)) {
            // System.out.println("Agent " + agentName + " received NaN value, skipping.");
            return;
        }

        if (topic.equals(this.inputTopic1.name)) {
            inputValue1 = message.asDouble;
            // System.out.println("Agent " + agentName + " updated inputValue1 to: " + inputValue1);
        } else if (topic.equals(this.inputTopic2.name)) {
            inputValue2 = message.asDouble;
            // System.out.println("Agent " + agentName + " updated inputValue2 to: " + inputValue2);
        }

        // Perform the binary operation
        double result = this.operation.apply(this.inputValue1, this.inputValue2);
        // System.out.println("Agent " + agentName + " calculated result: " + result);

        // Publishing result to output topic
        if (this.outputTopic.getPubs().contains(this)) {
            // System.out.println("Agent " + agentName + " publishing result to output topic.");
            this.outputTopic.publish(new Message(result));
        }
    }

}
