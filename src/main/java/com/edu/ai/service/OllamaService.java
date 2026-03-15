package com.edu.ai.service;

import com.edu.ai.dto.ChatRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OllamaService {
    
    private final ChatClient chatClient;
    
    @Autowired
    public OllamaService(@Qualifier("ollamaChatModel") OllamaChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }
    
    public com.edu.ai.dto.ChatResponse chat(ChatRequest request) {
        String prompt = request.getMessage();
        String model = request.getModel() != null ? request.getModel() : "qwen3.5:35b";
        
        ChatResponse aiResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .chatResponse();
        
        return new com.edu.ai.dto.ChatResponse(aiResponse.getResult().getOutput().getText(), model);
    }
    
    public String simpleChat(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
    
    public com.edu.ai.dto.ChatResponse chatWithSystemMessage(String systemMessage, String userMessage) {
        ChatResponse aiResponse = chatClient.prompt()
                .system(systemMessage)
                .user(userMessage)
                .call()
                .chatResponse();
        
        return new com.edu.ai.dto.ChatResponse(aiResponse.getResult().getOutput().getText(), "qwen3.5:35b");
    }
}
