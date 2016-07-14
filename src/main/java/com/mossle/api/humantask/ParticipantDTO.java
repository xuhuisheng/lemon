package com.mossle.api.humantask;

public class ParticipantDTO {
    private String id;
    private String code;
    private String type;
    private String humanTaskId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHumanTaskId() {
        return humanTaskId;
    }

    public void setHumanTaskId(String humanTaskId) {
        this.humanTaskId = humanTaskId;
    }
}
