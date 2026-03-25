package com.edu.ai.service;

import com.edu.ai.dto.ChatRequest;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.ollama.OllamaChatModel;
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
    private OllamaChatModel chatModel;

    @Test
    void testChatModelNotNull() {
        assertNotNull(chatModel);
    }

    @Test
    void testChatRequestCreation() {
        ChatRequest request = new ChatRequest("Hello", "qwen3.5:35b");
        assertNotNull(request);
        assertEquals("Hello", request.getMessage());
        assertEquals("qwen3.5:35b", request.getModel());
    }

    @Test
    void testChatRequestWithDefaultModel() {
        ChatRequest request = new ChatRequest("Hello");
        assertNotNull(request);
        assertEquals("Hello", request.getMessage());
        assertNull(request.getModel());
    }
}
