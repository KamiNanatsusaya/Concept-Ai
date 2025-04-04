package com.manusai.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manusai.android.domain.model.ChatMessage
import com.manusai.android.domain.usecase.ChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCase: ChatUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val userMessage = _uiState.value.inputText.trim()
        if (userMessage.isBlank()) return

        // Add user message to the list
        val userChatMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            content = userMessage,
            isFromUser = true,
            timestamp = System.currentTimeMillis()
        )
        
        _uiState.update { 
            it.copy(
                messages = listOf(userChatMessage) + it.messages,
                inputText = "",
                isProcessing = true
            ) 
        }

        // Process with LLM
        viewModelScope.launch {
            try {
                val response = chatUseCase.processMessage(userMessage)
                
                // Add AI response to the list
                val aiChatMessage = ChatMessage(
                    id = System.currentTimeMillis().toString(),
                    content = response,
                    isFromUser = false,
                    timestamp = System.currentTimeMillis()
                )
                
                _uiState.update { 
                    it.copy(
                        messages = listOf(aiChatMessage) + it.messages,
                        isProcessing = false
                    ) 
                }
            } catch (e: Exception) {
                // Handle error
                val errorMessage = ChatMessage(
                    id = System.currentTimeMillis().toString(),
                    content = "Entschuldigung, es ist ein Fehler aufgetreten: ${e.message}",
                    isFromUser = false,
                    timestamp = System.currentTimeMillis()
                )
                
                _uiState.update { 
                    it.copy(
                        messages = listOf(errorMessage) + it.messages,
                        isProcessing = false
                    ) 
                }
            }
        }
    }
}

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isProcessing: Boolean = false
)
