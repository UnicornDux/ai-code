package com.edu.ai.controller;

import com.edu.ai.tools.TimeTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.LayoutQueue;

@RequestMapping("ragadvisor")
public class RagWithAdvisorController {


    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    RagWithAdvisorController(VectorStore vectorStore, ChatClient.Builder chatClientBuilder){
        this.vectorStore = vectorStore;
        // 向量文档检索器
        VectorStoreDocumentRetriever vectorStoreDocumentRetriever = VectorStoreDocumentRetriever
                .builder()
                .vectorStore(vectorStore)
                .topK(5)
                .similarityThreshold(0.5)
                .build();

        RetrievalAugmentationAdvisor advisor = RetrievalAugmentationAdvisor
                .builder()
                // 文档检索器
                .documentRetriever(vectorStoreDocumentRetriever)
                .build();
        this.chatClient = chatClientBuilder
                .defaultAdvisors(advisor)
                .build();
    }


    //
    // 由于已经为 chatClient 设置了文档检索器，
    // 它会自动调用对应的内容，完成检索后交给大模型来处理成结果返回
    @PostMapping("vsearch")
    public String search(@RequestParam("query") String query){
        return chatClient.prompt()
                .system("你是一个客服人员，需要回复用户的问题")
                //  注册本地工具
                .tools(new TimeTool())
                .user(query)
                .call()
                .content();

    }





}
