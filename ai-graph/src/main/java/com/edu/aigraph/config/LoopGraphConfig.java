package com.edu.aigraph.config;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.GraphRepresentation;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncEdgeAction;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.edu.aigraph.nodes.JokeEnhanceNode;
import com.edu.aigraph.nodes.JokeEvaluateNode;
import com.edu.aigraph.nodes.JokeGenerateNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Slf4j
@Configuration
public class LoopGraphConfig {

    @Bean("loopGraph")
    public CompiledGraph loopGraph(ChatClient.Builder chatClientBuilder) throws GraphStateException {

        // stateGraph
        StateGraph stateGraph = new StateGraph("loopGraph", () -> {
            // state
            OverAllState overAllState = new OverAllState();
            overAllState.registerKeyAndStrategy("topic", new ReplaceStrategy());
            return overAllState;
        });

        stateGraph.addNode("generateJoke", AsyncNodeAction.node_async(new JokeGenerateNode(chatClientBuilder)));
        stateGraph.addNode("evaluateJoke", AsyncNodeAction.node_async(new JokeEvaluateNode(chatClientBuilder, 6, 8)));
        stateGraph.addNode("enhanceJoke", AsyncNodeAction.node_async(new JokeEnhanceNode(chatClientBuilder)));

        stateGraph.addEdge(StateGraph.START, "generateJoke");
        stateGraph.addEdge("generateJoke", "evaluateJoke");
        stateGraph.addConditionalEdges("evaluateJoke", AsyncEdgeAction.edge_async((state) -> {
            // 指定用于判断的数据
            return state.value("result", "break");
        }), Map.of(
                "break", StateGraph.END,
                "continue", "enhanceJoke"
        ));
        stateGraph.addEdge("enhanceJoke", "evaluateJoke");


        GraphRepresentation graph = stateGraph.getGraph(GraphRepresentation.Type.PLANTUML);
        log.info("-----------------------------------------------");
        // 这个图的内容可以被可视化的输出,例如在这个网站中,或者可以使用支持这种格式的库进行开发
        // 可视化网站 https://www.plantuml.com/plantuml
        log.info(graph.content());
        log.info("-----------------------------------------------");



        // 当编译的时候,可以传入编译配置, 这里面可以指定对应 图状态数据的存储配置,
        // 内置了内存, redis mongo 等存储实现,如果需要实现自己的存储, 实现对应的接口即可
        // compiler graph
        return stateGraph.compile();
    }


}
