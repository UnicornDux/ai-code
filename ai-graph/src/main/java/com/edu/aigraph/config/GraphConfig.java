package com.edu.aigraph.config;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Slf4j
@Configuration
public class GraphConfig {

    @Bean("quickStartGraph")
    public CompiledGraph quickStartGraph() throws GraphStateException {

        // 使用 supplier 方式创建状态
        StateGraph stateGraph = new StateGraph("startGraph", () -> {
            OverAllState state = new OverAllState();
            state.registerKeyAndStrategy("input1", new ReplaceStrategy());
            state.registerKeyAndStrategy("input2", new ReplaceStrategy());
            state.registerKeyAndStrategy("param1", new ReplaceStrategy());
            state.registerKeyAndStrategy("param2", new ReplaceStrategy());
            state.registerKeyAndStrategy("result", new ReplaceStrategy());
            return state;
        });

        // 添加节点
        stateGraph
            .addNode("node1", AsyncNodeAction.node_async((state) -> {
                String input1 = state.value("input1", "ok");
                String input2 = state.value("input2", "success");
                log.info("node1 - input1={}, input2={}", input1, input2);
                return Map.of(
                    "param1", input1 + "_v1",
                    "param2", input2 + "_v1"
                );
            }))
            .addNode("node2", AsyncNodeAction.node_async((state) -> {
                String param1 = state.value("param1", "");
                String param2 = state.value("param2", "");
                log.info("node2 - param1={}, param2={}", param1, param2);
                return Map.of("result", param1 + " + " + param2);
            }));

        // 添加边
        stateGraph
            .addEdge(StateGraph.START, "node1")
            .addEdge("node1", "node2")
            .addEdge("node2", StateGraph.END);

        return stateGraph.compile();
    }
}
