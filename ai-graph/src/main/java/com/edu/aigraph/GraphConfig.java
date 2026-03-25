package com.edu.aigraph;

import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class GraphConfig {


    @Bean("quickStartGraph")
    public void quickStartGraph() {
        //
        StateGraph stateGraph = new StateGraph("node1", () -> {
            return Map.of(
                    "input1", new ReplaceStrategy(),
                    "input2", new ReplaceStrategy()
            );
        });
    }
}
