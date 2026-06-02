package org.cloudfoundry.samples.music.config.ai;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfiguration {

    @Bean
    @ConditionalOnBean(VectorStore.class)
    public VectorStoreInitializer vectorStoreInitializer(VectorStore vectorStore) {
        return new VectorStoreInitializer(vectorStore);
    }

    @Bean
    @ConditionalOnBean({VectorStore.class, ChatModel.class})
    public MessageRetriever messageRetriever(VectorStore vectorStore, ChatModel chatModel) {
        return new MessageRetriever(vectorStore, chatModel);
    }
}


