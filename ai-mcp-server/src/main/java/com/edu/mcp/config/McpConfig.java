package com.edu.mcp.config;

import com.edu.mcp.tool.TimeTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(TimeTool timeTool) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(timeTool)
                .build();
    }

}
