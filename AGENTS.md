# AGENTS.md - AI Code Development Guide

## Project Overview
- **Type**: Spring Boot 3.5.11 Multi-module Application
- **Language**: Java 17  
- **Build**: Gradle 8.14.4 (with wrapper)  
- **Framework**: Spring AI 1.1.2 for LLM integration  

### Modules
- `ai-llm`: LLM service (Ollama, DeepSeek, OpenAI, RAG, MCP client) - package `com.edu.ai`
- `ai-graph`: AI Graph workflows - package `com.edu.aigraph`
- `ai-mcp-server`: MCP server - package `com.edu.mcp`

---

## Build Commands
```bash
./gradlew build                          # Build all
./gradlew test                           # Run all tests
./gradlew test --tests "com.edu.ai.controller.OllamaControllerTest"  # Test class
./gradlew test --tests "com.edu.ai.controller.OllamaControllerTest.testChat"  # Single test
./gradlew :ai-mcp-server:test            # Module tests
./gradlew build -x test                  # Skip tests
./gradlew bootRun                        # Run app
./gradlew :ai-llm:bootRun                # Run module
```

### Linting/Formatting (Add to build.gradle)
```groovy
plugins { id 'com.diffplug.spotless' version '6.25.0' }
spotless { java { target 'src/**/*.java'; googleJavaFormat(); removeUnusedImports() } }
```
Run: `./gradlew spotlessApply`

---

## Code Style

### Naming
- Classes: `PascalCase` (`OllamaController`, `ChatRequest`)
- Methods: `camelCase` (`simpleChat()`, `chatWithSystemMessage()`)  
- Variables: `camelCase` (`chatClient`, `ollamaService`)
- Constants: `SCREAMING_SNAKE_CASE` (`MAX_RETRY_COUNT`)
- Packages: `lowercase` (`com.edu.ai.controller`)
- DTOs: `PascalCase` suffix (`ChatRequest`, `ChatResponse`)

### Imports
Group with blank lines: 1) Java/Jakarta, 2) Spring, 3) Third-party, 4) Project (`com.edu.*`)

### Dependencies
- Constructor injection (preferred)
- `private final` fields
- `@Qualifier` for multiple beans

### Controllers
- `@RestController` with `@RequestMapping` at class level
- Return `ResponseEntity<T>`
- Try-catch with appropriate HTTP status (200, 400, 500)

### Error Handling
- Controller-level exception handling
- Log errors with framework logging
- Return user-friendly messages

### DTOs
- JavaBean naming (getters/setters)
- Multiple constructors for convenience

---

## Testing

### Structure
- Tests in `src/test/java` mirroring main structure
- `@WebMvcTest` for controller tests
- `@MockBean` for services, `@Autowired` for `MockMvc`

### Patterns
- Descriptive names: `test<WhatIsBeingTested>`
- Use `when().thenReturn()` for mocks
- Use `any()` matcher when appropriate

---

## Lombok
Configured: `compileOnly 'org.projectlombok:lombok'`
Use `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`

---

## Module Notes
- **ai-llm**: `com.edu.ai` - RAG with Redis vector store, MCP client
- **ai-graph**: `com.edu.aigraph` - Uses `@Slf4j` for logging
- **ai-mcp-server**: `com.edu.mcp` - Tool definitions in `com.edu.mcp.tool`

---

## Git
- Imperative mood: "Add feature" not "Added feature"
- Short summary (50 chars max), explain what/why in body

---

## Common Issues
- **Package mismatch**: Ensure imports match module packages
- **Missing beans**: Check `@Qualifier` for multiple beans
- **Test failures**: Verify mock setup with `when().thenReturn()`

---

## Configuration
`application.yaml`:
```yaml
spring:
  ai:
    ollama:
      base-url: http://192.168.80.111:11434
    deepseek:
      api-key: ${DEEPSEEK_API_KEY}
```

Exclude auto-configurations:
```java
@SpringBootApplication(exclude = {
    DeepSeekChatAutoConfiguration.class,
    OpenAiChatAutoConfiguration.class
})
```