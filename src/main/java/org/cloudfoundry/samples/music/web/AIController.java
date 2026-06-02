package org.cloudfoundry.samples.music.web;

import java.util.*;

import org.cloudfoundry.samples.music.config.ai.MessageRetriever;
import org.cloudfoundry.samples.music.domain.MessageRequest;
import org.cloudfoundry.samples.music.domain.Message;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AIController {
    private final MessageRetriever messageRetriever;
    private final VectorStore vectorStore;

    @Autowired
    public AIController(VectorStore vectorStore, MessageRetriever messageRetriever) {
        this.messageRetriever = messageRetriever;
        this.vectorStore = vectorStore;
    }

    @RequestMapping(value = "/ai/rag", method = RequestMethod.POST)
    public Map<String,Object> generate(@RequestBody MessageRequest messageRequest) {
        Message[] messages = messageRequest.getMessages();
        String query = messages[messages.length - 1].getText();
        String result = messageRetriever.retrieve(query);
        return Map.of("text",result);
    }
}



