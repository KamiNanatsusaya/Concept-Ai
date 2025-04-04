package com.manusai.android.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.manusai.android.domain.model.ChatMessage
import com.manusai.android.domain.usecase.ChatUseCase
import com.manusai.android.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var chatUseCase: ChatUseCase

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel(chatUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `updateInputText updates uiState correctly`() = testDispatcher.runBlockingTest {
        // Given
        val testInput = "Test message"

        // When
        viewModel.updateInputText(testInput)

        // Then
        val uiState = viewModel.uiState.first()
        assertEquals(testInput, uiState.inputText)
    }

    @Test
    fun `sendMessage adds user message to uiState`() = testDispatcher.runBlockingTest {
        // Given
        val testInput = "Test message"
        viewModel.updateInputText(testInput)
        `when`(chatUseCase.processMessage(testInput)).thenReturn("AI response")

        // When
        viewModel.sendMessage()

        // Then
        val uiState = viewModel.uiState.first()
        assertEquals("", uiState.inputText) // Input field should be cleared
        assertEquals(2, uiState.messages.size) // Should have user message and AI response
        
        val userMessage = uiState.messages[1] // Messages are in reverse order
        val aiMessage = uiState.messages[0]
        
        assertTrue(userMessage.isFromUser)
        assertEquals(testInput, userMessage.content)
        
        assertFalse(aiMessage.isFromUser)
        assertEquals("AI response", aiMessage.content)
    }

    @Test
    fun `sendMessage handles empty input`() = testDispatcher.runBlockingTest {
        // Given
        viewModel.updateInputText("")

        // When
        viewModel.sendMessage()

        // Then
        val uiState = viewModel.uiState.first()
        assertEquals(0, uiState.messages.size) // No messages should be added
    }

    @Test
    fun `sendMessage handles error from useCase`() = testDispatcher.runBlockingTest {
        // Given
        val testInput = "Test message"
        viewModel.updateInputText(testInput)
        `when`(chatUseCase.processMessage(testInput)).thenThrow(RuntimeException("Test error"))

        // When
        viewModel.sendMessage()

        // Then
        val uiState = viewModel.uiState.first()
        assertEquals(2, uiState.messages.size) // Should have user message and error message
        
        val userMessage = uiState.messages[1] // Messages are in reverse order
        val errorMessage = uiState.messages[0]
        
        assertTrue(userMessage.isFromUser)
        assertEquals(testInput, userMessage.content)
        
        assertFalse(errorMessage.isFromUser)
        assertTrue(errorMessage.content.contains("Fehler"))
    }
}
