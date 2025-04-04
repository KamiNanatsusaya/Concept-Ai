package com.manusai.android.sync.conflict

import com.manusai.android.data.local.entity.ChatMessageEntity
import com.manusai.android.data.remote.model.RemoteChatMessage
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles conflict resolution during data synchronization.
 */
@Singleton
class ConflictResolver @Inject constructor() {
    
    /**
     * Resolves conflicts between local and remote messages.
     * 
     * @param localMessages List of local messages
     * @param remoteMessages List of remote messages
     * @return Pair of (messages to update locally, messages to update remotely)
     */
    fun resolveConflicts(
        localMessages: List<ChatMessageEntity>,
        remoteMessages: List<RemoteChatMessage>
    ): ConflictResolution {
        val localMessagesMap = localMessages.associateBy { it.id }
        val remoteMessagesMap = remoteMessages.associateBy { it.id }
        
        val messagesToUpdateLocally = mutableListOf<RemoteChatMessage>()
        val messagesToUpdateRemotely = mutableListOf<ChatMessageEntity>()
        
        // Find messages that exist in remote but not in local
        remoteMessagesMap.forEach { (id, remoteMessage) ->
            if (!localMessagesMap.containsKey(id)) {
                messagesToUpdateLocally.add(remoteMessage)
            }
        }
        
        // Find messages that exist in local but not in remote
        localMessagesMap.forEach { (id, localMessage) ->
            if (!remoteMessagesMap.containsKey(id)) {
                messagesToUpdateRemotely.add(localMessage)
            }
        }
        
        // Find messages that exist in both but might have conflicts
        val commonMessageIds = localMessagesMap.keys.intersect(remoteMessagesMap.keys)
        commonMessageIds.forEach { id ->
            val localMessage = localMessagesMap[id]!!
            val remoteMessage = remoteMessagesMap[id]!!
            
            // Resolve based on timestamp - newer wins
            if (localMessage.timestamp > remoteMessage.timestamp) {
                messagesToUpdateRemotely.add(localMessage)
            } else if (localMessage.timestamp < remoteMessage.timestamp) {
                messagesToUpdateLocally.add(remoteMessage)
            }
            // If timestamps are equal, no action needed
        }
        
        return ConflictResolution(
            messagesToUpdateLocally = messagesToUpdateLocally,
            messagesToUpdateRemotely = messagesToUpdateRemotely
        )
    }
}

/**
 * Represents the result of conflict resolution.
 */
data class ConflictResolution(
    val messagesToUpdateLocally: List<RemoteChatMessage>,
    val messagesToUpdateRemotely: List<ChatMessageEntity>
)
