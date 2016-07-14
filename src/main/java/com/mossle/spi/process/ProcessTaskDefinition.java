package com.mossle.spi.process;

import java.util.ArrayList;
import java.util.List;

public class ProcessTaskDefinition {
    private String key;
    private String name;
    private String assignee;
    private List<ParticipantDefinition> participantDefinitions = new ArrayList<ParticipantDefinition>();

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public List<ParticipantDefinition> getParticipantDefinitions() {
        return participantDefinitions;
    }

    public void setParticipantDefinitions(
            List<ParticipantDefinition> participantDefinitions) {
        this.participantDefinitions = participantDefinitions;
    }

    public void addParticipantDefinition(String type, String value,
            String status) {
        ParticipantDefinition participantDefinition = new ParticipantDefinition();
        participantDefinition.setType(type);
        participantDefinition.setValue(value);
        participantDefinition.setStatus(status);
        this.participantDefinitions.add(participantDefinition);
    }
}
