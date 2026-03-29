package com.edu.aigraph.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("graph")
public class GraphController {

    private final CompiledGraph quickStartGraph;
    private final CompiledGraph llmGraph;
    private final CompiledGraph loopGraph;

    GraphController(
            @Qualifier("quickStartGraph") CompiledGraph quickStartGraph,
            @Qualifier("llmGraph") CompiledGraph llmGraph,
            @Qualifier("loopGraph") CompiledGraph loopGraph
    ) {
        this.quickStartGraph = quickStartGraph;
        this.llmGraph = llmGraph;
        this.loopGraph = loopGraph;
    }

    @GetMapping("loopGraph")
    public String loop(@RequestParam("topic") String topic) {

       // 隔离用户的数据的存储,需要传入一个 RunableConfig
       // 获取标识用户会话的 ID

        String session_id = "xxxxx";
        Map<String, Object> result = loopGraph
                .invoke(Map.of("topic", "love"),
                    RunnableConfig
                        .builder()
                        .threadId(session_id)
                        .build()
                )
                .map(OverAllState::data)
                .orElse(new HashMap<>());
        return "success" + result.getOrDefault("newJoke", result.get("joke"));
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
