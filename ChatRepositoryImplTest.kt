package com.manusai.android.test

import com.manusai.android.data.local.ChatLocalDataSource
import com.manusai.android.data.mapper.ChatMessageMapper
import com.manusai.android.data.remote.ChatRemoteDataSource
import com.manusai.android.data.repository.ChatRepositoryImpl
import com.manusai.android.domain.model.ChatMessage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class ChatRepositoryImplTest {

    @Mock
    private lateinit var localDataSource: ChatLocalDataSource

    @Mock
    private lateinit var remoteDataSource: ChatRemoteDataSource

    @Mock
    private lateinit var mapper: ChatMessageMapper

    private lateinit var repository: ChatRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = ChatRepositoryImpl(localDataSource, remoteDataSource, mapper)
    }

    @Test
    fun `saveUserMessage saves message to local data source`() = runBlocking {
        // Given
        val content = "Test message"
        val messageEntity = mockMessageEntity(content, true)
        `when`(mapper.mapUserMessageToEntity(content, any())).thenReturn(messageEntity)

        // When
        repository.saveUserMessage(content)

        // Then
        verify(localDataSource).insertMessage(messageEntity)
    }

    @Test
    fun `saveAiResponse saves message to local data source`() = runBlocking {
        // Given
        val content = "AI response"
        val messageEntity = mockMessageEntity(content, false)
        `when`(mapper.mapAiMessageToEntity(content, any())).thenReturn(messageEntity)

        // When
        repository.saveAiResponse(content)

        // Then
        verify(localDataSource).insertMessage(messageEntity)
    }

    @Test
    fun `getChatHistory returns mapped domain models`() = runBlocking {
        // Given
        val messageEntities = listOf(
            mockMessageEntity("Message 1", true),
            mockMessageEntity("Message 2", false)
        )
        val domainMessages = listOf(
            mockDomainMessage("Message 1", true),
            mockDomainMessage("Message 2", false)
        )
        
        `when`(localDataSource.getAllMessages()).thenReturn(flowOf(messageEntities))
        `when`(mapper.mapEntityToDomain(messageEntities[0])).thenReturn(domainMessages[0])
        `when`(mapper.mapEntityToDomain(messageEntities[1])).thenReturn(domainMessages[1])

        // When
        val result = repository.getChatHistory().first()

        // Then
        assertEquals(domainMessages, result)
    }

    @Test
    fun `clearChatHistory clears messages from local data source`() = runBlocking {
        // When
        repository.clearChatHistory()

        // Then
        verify(localDataSource).deleteAllMessages()
    }

    // Helper functions
    private fun mockMessageEntity(content: String, isFromUser: Boolean) = 
        com.manusai.android.data.local.entity.ChatMessageEntity(
            id = "test-id",
            content = content,
            isFromUser = isFromUser,
            timestamp = 123456789L
        )

    private fun mockDomainMessage(content: String, isFromUser: Boolean) = 
        ChatMessage(
            id = "test-id",
            content = content,
            isFromUser = isFromUser,
            timestamp = 123456789L
        )

    // Helper function for timestamp matching
    private fun <T> any(): T {
        return org.mockito.Mockito.any()
    }
}
