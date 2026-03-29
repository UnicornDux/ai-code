package com.edu.aigraph.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class JokeEnhanceNode implements NodeAction {

    private final ChatClient chatClient;

    public JokeEnhanceNode(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState t) throws Exception {
        // joke
        String joke = t.value("joke", "");

        PromptTemplate template = new PromptTemplate("Enhance the joke: {joke}, make it more lively and interesting." +
                "just return the enhanced joke. no other characters." +
                "the joke is: {joke}");
        template.add("joke", joke);
        String render = template.render();

        String content = chatClient.prompt()
                .user(render)
                .call()
                .content();


        return Map.of("newJoke", content);
    }
}
