package org.cloudfoundry.samples.music.config.ai;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ContextManager {
    private final VectorStore vectorStore;
    private final Map<String, List<String>> conversationHistory;
    private final int maxHistorySize;
    private final double similarityThreshold;
    private final int maxResults;

    public ContextManager(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.conversationHistory = new ConcurrentHashMap<>();
        this.maxHistorySize = 10;
        this.similarityThreshold = 0.7;
        this.maxResults = 5;
    }

    public List<String> getContext(String sessionId, String query) {
        List<String> context = new ArrayList<>();
        
        // Get conversation history
        List<String> history = conversationHistory.getOrDefault(sessionId, new ArrayList<>());
        context.addAll(history);
        
        // Get relevant documents from vector store
        List<Document> similarDocs = vectorStore.similaritySearch(query);
        for (Document doc : similarDocs) {
            if (doc.getMetadata().getOrDefault("similarity", 0.0) >= similarityThreshold) {
                context.add(doc.getContent());
            }
        }
        
        return context;
    }

    public void addToHistory(String sessionId, String message) {
        List<String> history = conversationHistory.computeIfAbsent(sessionId, k -> new ArrayList<>());
        history.add(message);
        
        // Maintain max history size
        if (history.size() > maxHistorySize) {
            history.remove(0);
        }
    }

    public void clearHistory(String sessionId) {
        conversationHistory.remove(sessionId);
    }
} 