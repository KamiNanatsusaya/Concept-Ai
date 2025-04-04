package com.manusai.android.data.repository

import com.manusai.android.data.local.ChatLocalDataSource
import com.manusai.android.data.mapper.ChatMessageMapper
import com.manusai.android.data.remote.ChatRemoteDataSource
import com.manusai.android.domain.model.ChatMessage
import com.manusai.android.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val localDataSource: ChatLocalDataSource,
    private val remoteDataSource: ChatRemoteDataSource,
    private val mapper: ChatMessageMapper
) : ChatRepository {

    override suspend fun saveUserMessage(content: String) {
        val timestamp = System.currentTimeMillis()
        val messageEntity = mapper.mapUserMessageToEntity(content, timestamp)
        localDataSource.insertMessage(messageEntity)
        
        // If online, also save to remote
        try {
            remoteDataSource.syncMessage(mapper.mapEntityToRemoteMessage(messageEntity))
        } catch (e: Exception) {
            // Handle offline case - will sync later
        }
    }

    override suspend fun saveAiResponse(content: String) {
        val timestamp = System.currentTimeMillis()
        val messageEntity = mapper.mapAiMessageToEntity(content, timestamp)
        localDataSource.insertMessage(messageEntity)
        
        // If online, also save to remote
        try {
            remoteDataSource.syncMessage(mapper.mapEntityToRemoteMessage(messageEntity))
        } catch (e: Exception) {
            // Handle offline case - will sync later
        }
    }

    override fun getChatHistory(): Flow<List<ChatMessage>> {
        return localDataSource.getAllMessages().map { entities ->
            entities.map { mapper.mapEntityToDomain(it) }
        }
    }

    override suspend fun clearChatHistory() {
        localDataSource.deleteAllMessages()
        
        // If online, also clear remote
        try {
            remoteDataSource.clearMessages()
        } catch (e: Exception) {
            // Handle offline case - will sync later
        }
    }
}
