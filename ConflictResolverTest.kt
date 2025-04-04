package com.manusai.android.test

import com.manusai.android.sync.conflict.ConflictResolver
import com.manusai.android.data.local.entity.ChatMessageEntity
import com.manusai.android.data.remote.model.RemoteChatMessage
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ConflictResolverTest {

    private lateinit var conflictResolver: ConflictResolver

    @Before
    fun setup() {
        conflictResolver = ConflictResolver()
    }

    @Test
    fun `resolveConflicts adds remote messages not in local`() {
        // Given
        val localMessages = listOf(
            createLocalMessage("1", "Local message 1", 1000L)
        )
        val remoteMessages = listOf(
            createRemoteMessage("1", "Local message 1", 1000L),
            createRemoteMessage("2", "Remote message 2", 2000L)
        )

        // When
        val resolution = conflictResolver.resolveConflicts(localMessages, remoteMessages)

        // Then
        assertEquals(1, resolution.messagesToUpdateLocally.size)
        assertEquals("2", resolution.messagesToUpdateLocally[0].id)
        assertEquals(0, resolution.messagesToUpdateRemotely.size)
    }

    @Test
    fun `resolveConflicts adds local messages not in remote`() {
        // Given
        val localMessages = listOf(
            createLocalMessage("1", "Local message 1", 1000L),
            createLocalMessage("2", "Local message 2", 2000L)
        )
        val remoteMessages = listOf(
            createRemoteMessage("1", "Local message 1", 1000L)
        )

        // When
        val resolution = conflictResolver.resolveConflicts(localMessages, remoteMessages)

        // Then
        assertEquals(0, resolution.messagesToUpdateLocally.size)
        assertEquals(1, resolution.messagesToUpdateRemotely.size)
        assertEquals("2", resolution.messagesToUpdateRemotely[0].id)
    }

    @Test
    fun `resolveConflicts handles timestamp conflicts correctly`() {
        // Given
        val localMessages = listOf(
            createLocalMessage("1", "Local version", 2000L), // Newer
            createLocalMessage("2", "Same timestamp", 3000L),
            createLocalMessage("3", "Older version", 1000L) // Older
        )
        val remoteMessages = listOf(
            createRemoteMessage("1", "Remote version", 1000L), // Older
            createRemoteMessage("2", "Same timestamp", 3000L),
            createRemoteMessage("3", "Newer version", 2000L) // Newer
        )

        // When
        val resolution = conflictResolver.resolveConflicts(localMessages, remoteMessages)

        // Then
        // Local message 1 is newer, should update remote
        // Message 2 has same timestamp, no update needed
        // Remote message 3 is newer, should update local
        assertEquals(1, resolution.messagesToUpdateLocally.size)
        assertEquals("3", resolution.messagesToUpdateLocally[0].id)
        
        assertEquals(1, resolution.messagesToUpdateRemotely.size)
        assertEquals("1", resolution.messagesToUpdateRemotely[0].id)
    }

    @Test
    fun `resolveConflicts handles empty lists correctly`() {
        // Given
        val emptyList = emptyList<ChatMessageEntity>()
        val remoteMessages = listOf(
            createRemoteMessage("1", "Remote message", 1000L)
        )

        // When
        val resolution = conflictResolver.resolveConflicts(emptyList, remoteMessages)

        // Then
        assertEquals(1, resolution.messagesToUpdateLocally.size)
        assertEquals(0, resolution.messagesToUpdateRemotely.size)
    }

    // Helper functions
    private fun createLocalMessage(id: String, content: String, timestamp: Long): ChatMessageEntity {
        return ChatMessageEntity(
            id = id,
            content = content,
            isFromUser = true,
            timestamp = timestamp,
            isSynced = false
        )
    }

    private fun createRemoteMessage(id: String, content: String, timestamp: Long): RemoteChatMessage {
        return RemoteChatMessage(
            id = id,
            content = content,
            isFromUser = true,
            timestamp = timestamp
        )
    }
}
