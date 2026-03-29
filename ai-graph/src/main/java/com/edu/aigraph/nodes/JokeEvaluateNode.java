package com.edu.aigraph.nodes;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class JokeEvaluateNode implements NodeAction {

    private final ChatClient chatClient;
    private final int maxTimes;
    private final int minScore;

    public JokeEvaluateNode(
            ChatClient.Builder chatClient,
            int maxTimes,
            int minScope
    ) {
        this.chatClient = chatClient.build();
        this.maxTimes = maxTimes;
        this.minScore = minScope;
    }


    @Override
    public Map<String, Object> apply(OverAllState t) throws Exception {
        // get joke
        String joke = t.value("joke", "");
        int times = t.value("times", 0);

        PromptTemplate template = new PromptTemplate("You are a joke evaluator expert. Evaluate the joke with a score between 0 and 10" +
                " and return the score only." +
                " the joke is :{joke}");

        template.add("joke", joke);
        String prompt = template.render();

        String content = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        int i = Integer.parseInt(content.trim());
        String result = "continue";
        if (i > minScore || times > maxTimes) {
            result = "break";
        }
        times++;

        // trim the \n and whitespace character
        return Map.of("result", result, "times", times);

    }
}
