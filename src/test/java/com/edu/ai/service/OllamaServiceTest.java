package com.edu.ai.service;

import com.edu.ai.dto.ChatRequest;
import com.edu.ai.dto.ChatResponse;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class OllamaServiceTest {

    @MockitoBean
    private ChatClient chatClient;

    @MockitoBean
    private ChatClient.CallPromptResponseSpec callPromptResponseSpec;

    @MockitoBean
    private ChatClient.CallPromptSpec callPromptSpec;

    @InjectMocks
    private OllamaService ollamaService;

    @Test
    void testChat() {
        ChatRequest request = new ChatRequest("Hello");
        
        AssistantMessage assistantMessage = new AssistantMessage("Hi there!");
        Generation generation = new Generation(assistantMessage);
        org.springframework.ai.chat.model.ChatResponse aiResponse = 
                new org.springframework.ai.chat.model.ChatResponse(List.of(generation));

        when(chatClient.prompt()).thenReturn(callPromptSpec);
        when(callPromptSpec.user(any())).thenReturn(callPromptResponseSpec);
        when(callPromptResponseSpec.call()).thenReturn(callPromptResponseSpec);
        when(callPromptResponseSpec.chatResponse()).thenReturn(aiResponse);

        ChatResponse result = ollamaService.chat(request);

        assertNotNull(result);
        assertEquals("Hi there!", result.getResponse());
        assertEquals("llama3.2", result.getModel());
    }

    @Test
    void testSimpleChat() {
        when(chatClient.prompt()).thenReturn(callPromptSpec);
        when(callPromptSpec.user(any())).thenReturn(callPromptResponseSpec);
        when(callPromptResponseSpec.call()).thenReturn(callPromptResponseSpec);
        when(callPromptResponseSpec.content()).thenReturn("Simple response");

        String result = ollamaService.simpleChat("Test message");

        assertEquals("Simple response", result);
    }

    @Test
    void testChatWithSystemMessage() {
        AssistantMessage assistantMessage = new AssistantMessage("System response");
        Generation generation = new Generation(assistantMessage);
        org.springframework.ai.chat.model.ChatResponse aiResponse = 
                new org.springframework.ai.chat.model.ChatResponse(List.of(generation));

        when(chatClient.prompt()).thenReturn(callPromptSpec);
        when(callPromptSpec.system(any())).thenReturn(callPromptSpec);
        when(callPromptSpec.user(any())).thenReturn(callPromptResponseSpec);
        when(callPromptResponseSpec.call()).thenReturn(callPromptResponseSpec);
        when(callPromptResponseSpec.chatResponse()).thenReturn(aiResponse);

        ChatResponse result = ollamaService.chatWithSystemMessage("System", "User");

        assertNotNull(result);
        assertEquals("System response", result.getResponse());
        assertEquals("llama3.2", result.getModel());
    }
}
