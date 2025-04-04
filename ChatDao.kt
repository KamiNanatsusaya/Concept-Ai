package com.manusai.android.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.manusai.android.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)
    
    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllMessages()
    
    @Query("SELECT * FROM chat_messages WHERE isSynced = 0")
    suspend fun getUnsyncedMessages(): List<ChatMessageEntity>
    
    @Query("UPDATE chat_messages SET isSynced = 1 WHERE id = :messageId")
    suspend fun markMessageAsSynced(messageId: String)
}
