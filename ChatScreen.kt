package com.manusai.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.manusai.android.domain.model.ChatMessage
import com.manusai.android.ui.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ) {
        // Chat messages
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true
        ) {
            items(uiState.messages) { message ->
                ChatMessageItem(message)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        // Input field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = uiState.inputText,
                onValueChange = { viewModel.updateInputText(it) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Nachricht eingeben...") }
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = { viewModel.sendMessage() },
                enabled = uiState.inputText.isNotBlank() && !uiState.isProcessing
            ) {
                Text("Senden")
            }
        }
        
        // Loading indicator
        if (uiState.isProcessing) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = if (message.isFromUser) MaterialTheme.colorScheme.primaryContainer 
                else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = if (message.isFromUser) "Du" else "Manus AI",
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
