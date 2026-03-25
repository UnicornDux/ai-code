# AGENTS.md - AI Code Development Guide

## Project Overview
- **Type**: Spring Boot 3.5.11 Multi-module Application
- **Language**: Java 17  
- **Build**: Gradle 8.14.4 (with wrapper)  
- **Framework**: Spring AI 1.1.2 for LLM integration  

### Modules
| Module | Package | Description |
|--------|---------|-------------|
| `ai-llm` | `com.edu.ai` | LLM service (Ollama, DeepSeek, OpenAI, RAG, MCP client) |
| `ai-graph` | `com.edu.aigraph` | AI Graph workflows using Alibaba Graph |
| `ai-mcp-server` | `com.edu.mcp` | MCP server with tool definitions in `com.edu.mcp.tool` |

---

## Build Commands

### Full Build & Test
```bash
./gradlew build                          # Build all modules
./gradlew build -x test                  # Build without tests
./gradlew clean build                    # Clean and rebuild
```

### Running Tests
```bash
./gradlew test                           # Run all tests
./gradlew :ai-llm:test                   # Test specific module
./gradlew test --tests "com.edu.ai.controller.OllamaControllerTest"  # Test class
./gradlew test --tests "com.edu.ai.controller.OllamaControllerTest.testChat"  # Single test
./gradlew test --tests "*ControllerTest" # Pattern matching
./gradlew --continue                     # Continue on test failure
```

### Running Applications
```bash
./gradlew bootRun                        # Run root application
./gradlew :ai-llm:bootRun                # Run ai-llm module
./gradlew :ai-mcp-server:bootRun         # Run MCP server
./gradlew :ai-graph:bootRun              # Run ai-graph module
```

### Linting/Formatting (Spotless)
```groovy
// build.gradle
plugins { id 'com.diffplug.spotless' version '6.25.0' }
spotless {
    java {
        target 'src/**/*.java'
        googleJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
    }
}
```
```bash
./gradlew spotlessApply      # Apply formatting
./gradlew spotlessCheck      # Check formatting
```

---

## Code Style

### Naming Conventions
- **Classes/Interfaces**: `PascalCase` (`OllamaController`, `ChatRequest`)
- **Methods**: `camelCase` (`simpleChat()`, `chatWithSystemMessage()`)
- **Variables**: `camelCase` (`chatClient`, `ollamaService`)
- **Constants**: `SCREAMING_SNAKE_CASE` (`MAX_RETRY_COUNT`)
- **Packages**: `lowercase` (`com.edu.ai.controller`)
- **DTOs**: `PascalCase` suffix (`ChatRequest`, `ChatResponse`)

### Import Organization
Order with blank lines between groups:
1. Java/Jakarta (`java.util`, `jakarta.servlet`)
2. Spring Framework (`org.springframework.*`)
3. Third-party (`org.springframework.ai.*`, `com.fasterxml.jackson`)
4. Project (`com.edu.*`)

### Field Declaration
```java
private final ChatClient chatClient;
private final OllamaChatModel chatModel;

@Autowired
public OllamaService(@Qualifier("ollamaChatModel") OllamaChatModel chatModel) {
    this.chatModel = chatModel;
}
```

### Controllers
```java
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
}
```

### Error Handling
- Controller-level try-catch with `ResponseEntity.internalServerError()` (500)
- Return user-friendly error messages
- Log errors using `@Slf4j` ( Lombok) or `Logger`
- Consider `@ControllerAdvice` for global exception handling

### DTOs
- JavaBean style with getter/setter pairs
- Multiple constructors for convenience
- Lombok `@Data` acceptable but manual getters/setters shown above is preferred

---

## Testing

### Test Structure
- Location: `src/test/java` mirroring main structure
- Naming: `test<WhatIsBeingTested>` pattern

### Controller Tests
```java
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
        when(ollamaService.chat(any(ChatRequest.class)))
            .thenReturn(new ChatResponse("response", "model"));
        
        mockMvc.perform(post("/api/ollama/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
```

### Service Tests
```java
@ExtendWith(MockitoExtension.class)
class OllamaServiceTest {
    @Mock
    private ChatClient chatClient;
    
    @Test
    void testSimpleChat() {
        when(chatClient.prompt().user(anyString()).call().content())
            .thenReturn("response");
        // test implementation
    }
}
```

---

## Project Configuration

### Application YAML Locations
- `ai-llm/src/main/resources/application.yaml` - LLM configuration
- `ai-mcp-server/src/main/resources/application.yaml` - MCP server config
- `ai-graph/src/main/resources/application.yaml` - Graph config

### Key Configuration (ai-llm)
```yaml
spring:
  application:
    name: ai-code
  ai:
    ollama:
      base-url: http://192.168.80.111:11434
      chat:
        options:
          model: qwen3.5:35b
    vectorstore:
      redis:
        initialize-schema: true
        prefix: mini_rag_of
        index-name: mini_rag_index
    mcp:
      client:
        annotation-scanner:
          enabled: true
        sse:
          connections:
            server1: http://localhost:8002
  data:
    redis:
      host: 192.168.122.10
      port: 6380
      password: redis123!@#
```

### Auto-configuration Exclusions (ai-llm)
```java
@SpringBootApplication(exclude = {
    DeepSeekChatAutoConfiguration.class,
    OpenAiChatAutoConfiguration.class,
    OpenAiAudioSpeechAutoConfiguration.class,
    OpenAiAudioTranscriptionAutoConfiguration.class,
    OpenAiEmbeddingAutoConfiguration.class,
    OpenAiImageAutoConfiguration.class,
    OpenAiModerationAutoConfiguration.class,
})
```

---

## Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| Package mismatch | Ensure imports match module packages (`com.edu.ai.*` vs `com.edu.mcp.*`) |
| Missing beans | Use `@Qualifier` when multiple beans of same type exist |
| Test failures | Verify mock setup with `when().thenReturn()` and `any()` matchers |
| Connection refused | Check Ollama base-url in application.yaml matches running instance |

---

## Git Conventions
- **Commit style**: Imperative mood ("Add feature" not "Added feature")
- **Subject**: Max 50 characters, describe what/why
- **Body**: Explain changes and motivation
