package org.cloudfoundry.samples.music.config.ai;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CompositeContextProvider {
    private final VectorStore vectorStore;
    private final Map<String, List<String>> memoryContext;
    private final int maxMemoryMessages;
    private final double similarityThreshold;
    private final int maxResults;

    public CompositeContextProvider(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.memoryContext = new ConcurrentHashMap<>();
        this.maxMemoryMessages = 10;
        this.similarityThreshold = 0.7;
        this.maxResults = 5;
    }

    public List<String> getContext(String sessionId, String query) {
        List<String> context = new ArrayList<>();
        
        // Get memory context
        List<String> memoryMessages = memoryContext.getOrDefault(sessionId, new ArrayList<>());
        context.addAll(memoryMessages);
        
        // Get vector store context
        List<Document> similarDocs = vectorStore.similaritySearch(query);
        for (Document doc : similarDocs) {
            if (doc.getMetadata().getOrDefault("similarity", 0.0) >= similarityThreshold) {
                context.add(doc.getContent());
            }
        }
        
        return context;
    }

    public void addToMemory(String sessionId, String message) {
        List<String> messages = memoryContext.computeIfAbsent(sessionId, k -> new ArrayList<>());
        messages.add(message);
        
        // Maintain max messages limit
        if (messages.size() > maxMemoryMessages) {
            messages.remove(0);
        }
    }

    public void clearMemory(String sessionId) {
        memoryContext.remove(sessionId);
    }
} 