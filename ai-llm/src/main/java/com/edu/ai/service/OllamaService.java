package com.edu.ai.service;

import com.edu.ai.dto.ChatRequest;
import com.edu.ai.tools.TimeTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class OllamaService {
    
    private final ChatClient chatClient;
    private final ToolCallbackProvider toolCallbackProvider;

    @Autowired
    public OllamaService(@Qualifier("ollamaChatModel") OllamaChatModel chatModel, ToolCallbackProvider provider) {
        this.chatClient = ChatClient.builder(chatModel)
                // 为模型添加记忆功能
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().build()).build())
                .build();
        this.toolCallbackProvider = provider;
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

    public Flux<ChatResponse>  chatStreamMessage(String system, String user) {
        return chatClient.prompt()
                .system(system)

                // 使用记忆功能
                .advisors( a -> a.param("chatId", 10).param("userId", 10))
                .user(user)
                .stream().chatResponse();
    }


    public com.edu.ai.dto.ChatResponse askWithTools(String msg) {
        ChatResponse chatResponse = chatClient.prompt()
                .system("你是一个客服人员，需要回复用户的问题")
                //  注册本地工具
                .tools(new TimeTool())

                .user(msg)
                .call()
                .chatResponse();
        return new com.edu.ai.dto.ChatResponse(chatResponse.getResult().getOutput().getText(), "qwen3.5:35b");
    }


    public com.edu.ai.dto.ChatResponse askWithMcp(String msg) {
        ChatResponse chatResponse = chatClient.prompt()
                .system("你是一个客服人员，需要回复用户的问题")
                .user(msg)
                .toolCallbacks(toolCallbackProvider.getToolCallbacks())
                .call()
                .chatResponse();
        return new com.edu.ai.dto.ChatResponse(chatResponse.getResult().getOutput().getText(), "qwen3.5:35b");
    }



}
