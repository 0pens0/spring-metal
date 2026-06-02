package org.springframework.ai.bindings;

import java.util.Locale;
import java.util.Map;

import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.boot.BindingsPropertiesProcessor;

/**
 * Maps Cloud Foundry/Tanzu service bindings to Spring AI properties.
 *
 * Supported types:
 *  - genai: maps to spring.ai.openai.base-url and api-key (OpenAI-compatible endpoint)
 *  - openai: same as genai
 *  - ollama: maps to spring.ai.ollama.base-url
 */
public class TanzuBindingsPropertiesProcessor implements BindingsPropertiesProcessor {

    @Override
    public void process(Bindings bindings, Map<String, Object> properties) {
        for (Binding b : bindings.getBindings()) {
            String type = b.getType() == null ? "" : b.getType().toLowerCase(Locale.ROOT);
            Map<String, String> secret = b.getSecret();
            if (secret == null) continue;

            if ("genai".equals(type) || "openai".equals(type)) {
                String baseUrl = first(secret, "base-url", "base_url", "url", "uri", "endpoint");
                String apiKey  = first(secret, "api-key", "apikey", "api_key", "token", "auth-token");
                String chatModel = first(secret, "chat-model", "chat_model");
                String embeddingModel = first(secret, "embedding-model", "embedding_model");
                if (baseUrl != null) {
                    properties.put("spring.ai.openai.base-url", baseUrl);
                }
                if (apiKey != null) {
                    properties.put("spring.ai.openai.api-key", apiKey);
                }
                if (chatModel != null) {
                    properties.put("spring.ai.openai.chat.options.model", chatModel);
                }
                if (embeddingModel != null) {
                    properties.put("spring.ai.openai.embedding.options.model", embeddingModel);
                }
            } else if ("ollama".equals(type)) {
                String baseUrl = first(secret, "base-url", "base_url", "url", "uri", "endpoint");
                if (baseUrl != null) {
                    properties.put("spring.ai.ollama.base-url", baseUrl);
                }
            }
        }
    }

    private static String first(Map<String, String> map, String... keys) {
        for (String k : keys) {
            if (map.containsKey(k)) return map.get(k);
        }
        return null;
    }
}


