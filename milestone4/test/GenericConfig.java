package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class GenericConfig implements Config {
    private String configurationFilePath;
    private final List<ParallelAgent> parallelAgents;

    public GenericConfig() {
        this.parallelAgents = new ArrayList<>();
    }

    @Override
    public void create() {
        if (configurationFilePath == null || configurationFilePath.isEmpty()) {
            throw new IllegalStateException("Configuration file path is not set.");
        }

        try (BufferedReader fileReader = new BufferedReader(new FileReader(new File(configurationFilePath)))) {
            List<String> fileLines = new ArrayList<>();
            String currentLine;

            // System.out.println("Reading configuration file: " + configurationFilePath);
            while ((currentLine = fileReader.readLine()) != null) {
                fileLines.add(currentLine.trim());
                // System.out.println("Read line: " + currentLine);
            }

            if (fileLines.size() % 3 != 0) {
                throw new IllegalArgumentException("Invalid configuration format: Each agent must have three lines (class, subs, pubs).");
            }

            for (int i = 0; i < fileLines.size(); i += 3) {
                String agentClassName = fileLines.get(i);
                String[] subscriptionTopics = fileLines.get(i + 1).split(",");
                String[] publicationTopics = fileLines.get(i + 2).split(",");

                // System.out.println("Creating agent of class: " + agentClassName);
                Class<?> agentClass = Class.forName(agentClassName);
                Constructor<?> agentConstructor = agentClass.getConstructor(String[].class, String[].class);
                Agent createdAgent = (Agent) agentConstructor.newInstance((Object) subscriptionTopics, (Object) publicationTopics);

                ParallelAgent parallelAgentWrapper = new ParallelAgent(createdAgent);
                parallelAgents.add(parallelAgentWrapper);

                // System.out.println("Added agent: " + createdAgent.getName());
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to create configuration from file", exception);
        }
    }

    @Override
    public String getName() {
        // System.out.println("Returning configuration name: GenericConfig");
        return "GenericConfig";
    }

    @Override
    public int getVersion() {
        // System.out.println("Returning configuration version: 1");
        return 1;
    }

    @Override
    public void close() {
        // System.out.println("Closing all agents in configuration.");
        for (ParallelAgent parallelAgent : parallelAgents) {
            // System.out.println("Closing agent: " + parallelAgent.getName());
            parallelAgent.close();
        }
    }

    public void setConfFile(String filePath) {
        // System.out.println("Setting configuration file path to: " + filePath);
        this.configurationFilePath = filePath;
    }
}
