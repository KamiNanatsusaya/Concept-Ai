package com.manusai.android.domain.repository

import com.manusai.android.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun saveUserMessage(content: String)
    
    suspend fun saveAiResponse(content: String)
    
    fun getChatHistory(): Flow<List<ChatMessage>>
    
    suspend fun clearChatHistory()
}
