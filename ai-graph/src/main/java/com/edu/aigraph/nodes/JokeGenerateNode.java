package com.edu.aigraph.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class JokeGenerateNode implements NodeAction {

    private final ChatClient chatClient;

    public JokeGenerateNode(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState t) throws Exception {

        // get joke topic
        String topic = t.value("topic", "");

        //
        PromptTemplate template = new PromptTemplate("Generate a joke about point at topic, the return result just contains the joke" +
                " topic:{topic}");
        template.add("topic", topic);
        String prompt = template.render();

        String content = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        // store joke
        return Map.of("joke", content);
    }
}
