package org.cloudfoundry.samples.music.config.ai;

import java.util.List;

import org.cloudfoundry.samples.music.domain.Album;
import org.cloudfoundry.samples.music.repositories.jpa.JpaAlbumRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;

public class VectorStoreInitializer implements ApplicationRunner {
    private final VectorStore vectorStore;

    public VectorStoreInitializer(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(org.springframework.boot.ApplicationArguments args) throws Exception {
        // no-op bootstrap; repository content can be added via UI
    }
}



