package com.edu.ai.controller;

import com.edu.ai.dto.ChatRequest;
import com.edu.ai.dto.ChatResponse;
import com.edu.ai.service.OllamaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OllamaController.class)
class OllamaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OllamaService ollamaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testChat() throws Exception {
        ChatRequest request = new ChatRequest("Hello, how are you?");
        ChatResponse response = new ChatResponse("I'm doing well, thank you!", "llama3.2");

        when(ollamaService.chat(any(ChatRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/ollama/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("I'm doing well, thank you!"))
                .andExpect(jsonPath("$.model").value("llama3.2"));
    }

    @Test
    void testSimpleChat() throws Exception {
        when(ollamaService.simpleChat("Hello")).thenReturn("Hi there!");

        mockMvc.perform(get("/api/ollama/simple")
                .param("message", "Hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hi there!"));
    }

    @Test
    void testChatWithSystem() throws Exception {
        ChatResponse response = new ChatResponse("Response with system message", "llama3.2");

        when(ollamaService.chatWithSystemMessage("You are a helpful assistant", "Help me"))
                .thenReturn(response);

        mockMvc.perform(post("/api/ollama/chat/system")
                .param("system", "You are a helpful assistant")
                .param("user", "Help me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Response with system message"));
    }

    @Test
    void testTestConnection() throws Exception {
        when(ollamaService.simpleChat(any())).thenReturn("Connection successful");

        mockMvc.perform(get("/api/ollama/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ollama connection test: Connection successful"));
    }

    @Test
    void testHealth() throws Exception {
        mockMvc.perform(get("/api/ollama/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ollama service is running"));
    }
}
