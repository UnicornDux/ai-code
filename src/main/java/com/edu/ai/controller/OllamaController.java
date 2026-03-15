package com.edu.ai.controller;

import com.edu.ai.dto.ChatRequest;
import com.edu.ai.dto.ChatResponse;
import com.edu.ai.service.OllamaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ollama")
public class OllamaController {
    
    private final OllamaService ollamaService;
    
    @Autowired
    public OllamaController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }
    
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            ChatResponse response = ollamaService.chat(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ChatResponse("Error: " + e.getMessage(), "error"));
        }
    }
    
    @GetMapping("/simple")
    public ResponseEntity<String> simpleChat(@RequestParam String message) {
        try {
            String response = ollamaService.simpleChat(message);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage());
        }
    }
    
    @PostMapping("/chat/system")
    public ResponseEntity<ChatResponse> chatWithSystem(@RequestParam String system, @RequestParam String user) {
        try {
            ChatResponse response = ollamaService.chatWithSystemMessage(system, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ChatResponse("Error: " + e.getMessage(), "error"));
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        try {
            String response = ollamaService.simpleChat("Hello, please respond with 'Connection successful'");
            return ResponseEntity.ok("Ollama connection test: " + response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Connection failed: " + e.getMessage());
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Ollama service is running");
    }
}
