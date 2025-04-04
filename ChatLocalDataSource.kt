package com.manusai.android.data.local

import com.manusai.android.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatLocalDataSource @Inject constructor(
    private val chatDao: ChatDao
) {
    fun getAllMessages(): Flow<List<ChatMessageEntity>> {
        return chatDao.getAllMessages()
    }
    
    suspend fun insertMessage(message: ChatMessageEntity) {
        chatDao.insertMessage(message)
    }
    
    suspend fun deleteAllMessages() {
        chatDao.deleteAllMessages()
    }
}
