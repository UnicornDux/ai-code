package com.edu.aigraph.config;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class LlmGraphConfig {

    @Autowired
    private OllamaChatModel ollamaChatModel;

    @Bean("llmGraph")
    public CompiledGraph llmGraph() throws GraphStateException {
        ChatClient chatClient = ChatClient.builder(ollamaChatModel).build();

        StateGraph stateGraph = new StateGraph("llmGraph", () -> {
            OverAllState state = new OverAllState();
            state.registerKeyAndStrategy("question", new ReplaceStrategy());
            state.registerKeyAndStrategy("context", new ReplaceStrategy());
            state.registerKeyAndStrategy("llm_response", new ReplaceStrategy());
            state.registerKeyAndStrategy("final_answer", new ReplaceStrategy());
            return state;
        });

        stateGraph
            .addNode("processInput", AsyncNodeAction.node_async((state) -> {
                String question = state.value("question", "");
                String context = state.value("context", "");
                System.out.println("processInput - question=" + question + ", context=" + context);
                return Map.of(
                    "question", question,
                    "context", context != null ? context : "没有提供上下文"
                );
            }))
            .addNode("llmNode", AsyncNodeAction.node_async((state) -> {
                String question = state.value("question", "");
                String context = state.value("context", "");

                String prompt = buildPrompt(question, context);
                String response = chatClient.prompt()
                        .user(prompt)
                        .call()
                        .content();

                System.out.println("llmNode - question=" + question + ", response=" + response);
                return Map.of("llm_response", response);
            }))
            .addNode("formatOutput", AsyncNodeAction.node_async((state) -> {
                String llmResponse = state.value("llm_response", "");
                String formatted = "【回答】\n" + llmResponse;
                System.out.println("formatOutput - final=" + formatted);
                return Map.of("final_answer", formatted);
            }));

        stateGraph
            .addEdge(StateGraph.START, "processInput")
            .addEdge("processInput", "llmNode")
            .addEdge("llmNode", "formatOutput")
            .addEdge("formatOutput", StateGraph.END);

        return stateGraph.compile();
    }

    private String buildPrompt(String question, String context) {
        return String.format("""
            基于以下上下文信息回答问题。

            上下文：
            %s

            问题：%s

            请给出准确、简洁的回答。
            """, context, question);
    }
}
