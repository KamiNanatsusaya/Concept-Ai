package com.manusai.android.domain.usecase

import com.manusai.android.domain.repository.ChatRepository
import com.manusai.android.llm.service.LlmService
import javax.inject.Inject

class ChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val llmService: LlmService
) {
    suspend fun processMessage(userMessage: String): String {
        // Save user message to repository
        chatRepository.saveUserMessage(userMessage)
        
        // Process with LLM
        val response = llmService.generateResponse(userMessage)
        
        // Save AI response to repository
        chatRepository.saveAiResponse(response)
        
        return response
    }
    
    suspend fun getChatHistory() = chatRepository.getChatHistory()
    
    suspend fun clearChatHistory() = chatRepository.clearChatHistory()
}
