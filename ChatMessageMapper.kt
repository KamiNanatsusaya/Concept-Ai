package com.manusai.android.data.mapper

import com.manusai.android.data.local.entity.ChatMessageEntity
import com.manusai.android.data.remote.model.RemoteChatMessage
import com.manusai.android.domain.model.ChatMessage
import java.util.UUID
import javax.inject.Inject

class ChatMessageMapper @Inject constructor() {
    
    fun mapEntityToDomain(entity: ChatMessageEntity): ChatMessage {
        return ChatMessage(
            id = entity.id,
            content = entity.content,
            isFromUser = entity.isFromUser,
            timestamp = entity.timestamp
        )
    }
    
    fun mapUserMessageToEntity(content: String, timestamp: Long): ChatMessageEntity {
        return ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            content = content,
            isFromUser = true,
            timestamp = timestamp,
            isSynced = false
        )
    }
    
    fun mapAiMessageToEntity(content: String, timestamp: Long): ChatMessageEntity {
        return ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            content = content,
            isFromUser = false,
            timestamp = timestamp,
            isSynced = false
        )
    }
    
    fun mapEntityToRemoteMessage(entity: ChatMessageEntity): RemoteChatMessage {
        return RemoteChatMessage(
            id = entity.id,
            content = entity.content,
            isFromUser = entity.isFromUser,
            timestamp = entity.timestamp
        )
    }
    
    fun mapRemoteMessageToEntity(remote: RemoteChatMessage): ChatMessageEntity {
        return ChatMessageEntity(
            id = remote.id,
            content = remote.content,
            isFromUser = remote.isFromUser,
            timestamp = remote.timestamp,
            isSynced = true
        )
    }
}
