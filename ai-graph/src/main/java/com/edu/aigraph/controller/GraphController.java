package com.edu.aigraph.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("graph")
public class GraphController {

    private final CompiledGraph quickStartGraph;
    private final CompiledGraph llmGraph;

    GraphController(
            @Qualifier("quickStartGraph") CompiledGraph quickStartGraph,
            @Qualifier("llmGraph") CompiledGraph llmGraph) {
        this.quickStartGraph = quickStartGraph;
        this.llmGraph = llmGraph;
    }

    @PostMapping("start")
    public String graph() {
        Map<String, Object> result = quickStartGraph.invoke(Map.of(
                "input1", "hello",
                "input2", "world"
        )).map(OverAllState::data).orElse(new HashMap<>());
        log.info("result = {}", result.get("result"));
        return "success: " + result.get("result");
    }

    @PostMapping("llm")
    public String llmGraph(@RequestParam String question, @RequestParam(required = false) String context) {
        Map<String, Object> result = llmGraph.invoke(Map.of(
                "question", question,
                "context", context != null ? context : ""
        )).map(OverAllState::data).orElse(new HashMap<>());
        log.info("llm result = {}", result.get("final_answer"));
        return "success: " + result.get("final_answer");
    }
}
