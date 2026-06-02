package org.cloudfoundry.samples.music.config.ai;

import java.util.List;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

public class MessageRetriever {
    private final VectorStore vectorStore;
    private final ChatModel chatModel;

    public MessageRetriever(VectorStore vectorStore, ChatModel chatModel) {
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
    }

    public String retrieve(String query) {
        var docs = vectorStore.similaritySearch(SearchRequest.query(query).withTopK(4));
        String context = docs.stream().map(d -> d.getContent()).reduce("", (a,b) -> a + "\n" + b);
        String promptText = "Use the following context to answer the question. If context is empty, answer from general knowledge.\n\nContext:\n" + context + "\n\nQuestion: " + query;
        return chatModel.call(new Prompt(promptText)).getResult().getOutput().getContent();
    }
}



