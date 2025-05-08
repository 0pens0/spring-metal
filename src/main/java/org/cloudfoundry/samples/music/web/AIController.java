package org.cloudfoundry.samples.music.web;

import java.util.*;

import org.cloudfoundry.samples.music.config.ai.MessageRetriever;
import org.cloudfoundry.samples.music.domain.Album;
import org.cloudfoundry.samples.music.domain.MessageRequest;
import org.cloudfoundry.samples.music.domain.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@RestController
@Profile("llm")
public class AIController {
    private static final Logger logger = LoggerFactory.getLogger(AIController.class);
    private final MessageRetriever messageRetriever;

    @Autowired
    public AIController(MessageRetriever messageRetriever) {
        this.messageRetriever = messageRetriever;
    }
    
    @RequestMapping(value = "/ai/rag", method = RequestMethod.POST)
    public Map<String,Object> generate(@RequestBody MessageRequest messageRequest) {
        Message[] messages = messageRequest.getMessages();
        logger.info("Getting Messages " + messages);

        String query = messages[messages.length - 1].getText();
        // Generate a session ID based on the request
        String sessionId = UUID.randomUUID().toString();
        String result = messageRetriever.retrieve(sessionId, query);

        return Map.of("text", result);
    }

    @RequestMapping(value = "/ai/addDoc", method = RequestMethod.POST)
    public String addDoc(@RequestBody Album album) {
        String text = generateVectorDoc(album);
        this.vectorStore.add(List.of(text));
        return text;
    }

    @RequestMapping(value = "/ai/deleteDoc", method = RequestMethod.POST)
    public String deleteDoc(@RequestBody String id) {
        logger.info("Deleting Album " + id);
        this.vectorStore.delete(List.of(id));
        return id;
    }
}
