package org.cloudfoundry.samples.music.config.ai;

import org.springframework.ai.context.ContextProvider;
import org.springframework.ai.context.MemoryContextProvider;
import org.springframework.ai.context.VectorStoreContextProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("llm")
public class McpConfiguration {

    @Bean
    public ContextProvider contextProvider(VectorStore vectorStore) {
        // Create a composite context provider that uses both memory and vector store
        MemoryContextProvider memoryProvider = new MemoryContextProvider();
        VectorStoreContextProvider vectorProvider = new VectorStoreContextProvider(vectorStore);
        
        return new CompositeContextProvider(memoryProvider, vectorProvider);
    }
} 