/*
 * Copyright 2023-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.samples.music.config.ai;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 *
 * @author Christian Tzolov
 */
public class MessageRetriever {

	private static final Logger logger = LoggerFactory.getLogger(MessageRetriever.class);
	
	@Value("classpath:/prompts/system-qa.st")
	private Resource systemPrompt;
	
	private final ChatClient chatClient;
	private final ContextManager contextManager;

	public MessageRetriever(ChatModel chatModel, ContextManager contextManager) {
		this.chatClient = ChatClient.builder(chatModel).build();
		this.contextManager = contextManager;
	}

	public String retrieve(String sessionId, String message) {
		// Get context from conversation history and vector store
		List<String> context = contextManager.getContext(sessionId, message);
		
		// Create system message with context
		String contextText = context.stream()
			.collect(Collectors.joining("\n"));
		
		SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemPrompt);
		Message systemMessage = systemPromptTemplate.createMessage(Map.of("documents", contextText));
		
		// Get response from chat model
		String response = chatClient.prompt()
			.messages(systemMessage)
			.user(message)
			.call()
			.content();
			
		// Add to conversation history
		contextManager.addToHistory(sessionId, message);
		contextManager.addToHistory(sessionId, response);
		
		return response;
	}
}
