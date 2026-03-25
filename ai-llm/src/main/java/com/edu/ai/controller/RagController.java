package com.edu.ai.controller;


import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("rag")
public class RagController {

    private final VectorStore vectorStore;

    RagController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostMapping("import")
    public String importData(@RequestParam("data") String data){
        Document doc = Document.builder()
                .text(data)
                .build();
        vectorStore.add(List.of(doc));
        return "success";
    }



    @PostMapping("search")
    public List<Document> search(@RequestParam("query")String query) {
        SearchRequest queryRequest = SearchRequest.builder()
                .query(query) // 相似度检测的文本
                .topK(10) // 相似度最高的 10 条
                .similarityThreshold(0.8) // 相似度的阈值
                .build();

        return vectorStore.similaritySearch(queryRequest);
    }
}
