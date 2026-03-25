package com.edu.aigraph.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("graph")
public class GraphController {

   private final CompiledGraph startGraph;

   GraphController(@Qualifier("quickStartGraph") CompiledGraph graph) {
       this.startGraph = graph;
   }

    @PostMapping("start")
    public String graph() {
        Map<String, Object> result = startGraph.invoke(Map.of(
                "input1", "hello",
                "input2", "world"
        )).map(OverAllState::data).orElse(new HashMap<>());
        log.info("result = {}", result.get("result"));
        return "success: " + result.get("result");
    }
}
