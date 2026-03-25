# AGENTS.md - AI Code Development Guide

## Project Overview

- **Type**: Spring Boot 3.5.11 Multi-module Application
- **Language**: Java 17
- **Build System**: Gradle 8.14.4 (with wrapper)
- **Primary Framework**: Spring AI 1.1.2 for LLM integration
- **Modules**: `ai-code` (main), `ai-mcp-server` (MCP server submodule)

---

## Build Commands

### Core Gradle Commands

```bash
# Build entire project
./gradlew build

# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests "com.edu.ai.controller.OllamaControllerTest"

# Run a single test method
./gradlew test --tests "com.edu.ai.controller.OllamaControllerTest.testChat"

# Run tests in a specific module
./gradlew :ai-mcp-server:test

# Clean and rebuild
./gradlew clean build

# Build without running tests
./gradlew build -x test

# Run the application
./gradlew bootRun

# Run specific module
./gradlew :ai-mcp-server:bootRun

# Check dependencies
./gradlew dependencies

# Gradle wrapper upgrade
./gradlew wrapper --gradle-version=8.14.4
```

---

## Code Style Guidelines

### Java Naming Conventions

| Element | Convention | Example |
|---------|-----------|---------|
| Classes | PascalCase | `OllamaController`, `ChatRequest` |
| Methods | camelCase | `simpleChat()`, `chatWithSystemMessage()` |
| Variables | camelCase | `chatClient`, `ollamaService` |
| Constants | SCREAMING_SNAKE | `MAX_RETRY_COUNT` |
| Packages | lowercase | `com.edu.ai.controller` |
| DTOs | PascalCase with suffix | `ChatRequest`, `ChatResponse` |

### Package Structure

```
com.edu.ai/
├── AicodeApplication.java      # Main application entry
├── controller/                 # REST controllers
├── service/                    # Business logic
├── dto/                        # Data transfer objects
├── config/                     # Configuration classes
├── exception/                  # Custom exceptions
└── tools/                      # AI tools
```

### Import Organization

Organize imports in this order (IDE can handle this automatically):

1. Java/Jakarta standard library
2. Spring framework imports
3. Third-party libraries
4. Project imports (com.edu.ai.*)
5. Blank line between groups

```java
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.edu.ai.dto.ChatRequest;
import com.edu.ai.service.OllamaService;
```

### Field Declaration

- Always use constructor injection (preferred over `@Autowired` on fields)
- Use `private final` for dependency-injected fields
- Use `@Qualifier` when multiple beans of same type exist

```java
@Service
public class OllamaService {
    private final ChatClient chatClient;
    
    @Autowired
    public OllamaService(@Qualifier("ollamaChatModel") OllamaChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }
}
```

### Controller Patterns

- Use `@RestController` for REST endpoints
- Use `@RequestMapping` at class level for path prefix
- Return `ResponseEntity<T>` for flexibility
- Wrap exceptions in try-catch, return appropriate HTTP status

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

### DTO Conventions

- Use JavaBean naming (getters/setters with `get`/`set` prefix)
- Provide multiple constructors for convenience
- Use descriptive field names

```java
public class ChatRequest {
    private String message;
    private String model;
    
    public ChatRequest() {}
    
    public ChatRequest(String message) {
        this.message = message;
    }
    
    public ChatRequest(String message, String model) {
        this.message = message;
        this.model = model;
    }
    // getters and setters...
}
```

### Error Handling

- Catch exceptions at controller level for REST endpoints
- Log errors appropriately using a logging framework
- Return user-friendly error messages
- Use appropriate HTTP status codes:
  - `200 OK` - Success
  - `400 Bad Request` - Invalid input
  - `500 Internal Server Error` - Server-side errors

---

## Testing Guidelines

### Test Class Structure

- Place tests in `src/test/java` mirroring `src/main/java` structure
- Use `@WebMvcTest` for controller tests (loads only web layer)
- Use `@MockBean` to mock service dependencies
- Use `@Autowired` for `MockMvc` and `ObjectMapper`

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
        // test implementation
    }
}
```

### Test Method Naming

- Use descriptive names: `test<WhatIsBeingTested>`
- Use `when()` from Mockito to set up mocks
- Use `any()` matcher when argument doesn't matter

```java
@Test
void testChat() throws Exception {
    when(ollamaService.chat(any(ChatRequest.class))).thenReturn(response);
    
    mockMvc.perform(post("/api/ollama/chat")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.response").value("I'm doing well!"));
}
```

### Service Test Patterns

```java
@SpringBootTest
class OllamaServiceTest {
    @MockBean
    private OllamaChatModel chatModel;
    
    @Test
    void testSimpleChat() {
        when(chatModel.call(any())).thenReturn(expectedResponse);
        // test implementation
    }
}
```

---

## Lombok Usage

Lombok is configured but use judiciously:

```groovy
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'
```

- Use `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` as needed
- Always add `lombok.config` if you need specific Lombok behavior

---

## Spring Boot Configuration

### Application Properties

Configuration is in `src/main/resources/application.yaml`:

```yaml
spring:
  ai:
    ollama:
      base-url: http://192.168.80.111:11434
    deepseek:
      api-key: ${DEEPSEEK_API_KEY}
      base-url: https://api.deepseek.com
```

### Excluding Auto-configurations

When excluding Spring AI auto-configurations:

```java
@SpringBootApplication(exclude = {
    DeepSeekChatAutoConfiguration.class,
    OpenAiChatAutoConfiguration.class,
    // ...
})
```

---

## Git Conventions

### Commit Messages

- Use imperative mood: "Add feature" not "Added feature"
- First line: Short summary (50 chars max)
- Body: Explain what and why (optional)

### File Organization

- Main code: `src/main/java`
- Tests: `src/test/java`
- Resources: `src/main/resources`
- Configuration: `src/main/resources/application.yaml`

---

## Common Patterns

### REST API Response Wrapper

Return consistent response structure:

```java
public ResponseEntity<ChatResponse> endpoint(@RequestBody Request request) {
    try {
        return ResponseEntity.ok(service.process(request));
    } catch (Exception e) {
        return ResponseEntity.internalServerError()
                .body(new ChatResponse("Error: " + e.getMessage(), "error"));
    }
}
```

### Dependency Injection

Prefer constructor injection over field injection:

```java
@Service
public class MyService {
    private final DepA depA;
    private final DepB depB;
    
    @Autowired
    public MyService(DepA depA, DepB depB) {
        this.depA = depA;
        this.deB = depB;
    }
}
```
