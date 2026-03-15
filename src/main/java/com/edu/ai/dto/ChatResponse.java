package com.edu.ai.dto;

public class ChatResponse {
    private String response;
    private String model;
    private long timestamp;
    
    public ChatResponse() {}
    
    public ChatResponse(String response, String model) {
        this.response = response;
        this.model = model;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        this.response = response;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
