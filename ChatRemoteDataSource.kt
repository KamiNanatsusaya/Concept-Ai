package com.manusai.android.data.remote

import com.manusai.android.data.remote.model.RemoteChatMessage
import javax.inject.Inject

class ChatRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun syncMessage(message: RemoteChatMessage) {
        // In a real implementation, this would call an API service
        // For the prototype, we'll simulate the API call
        // apiService.syncMessage(message)
    }
    
    suspend fun clearMessages() {
        // In a real implementation, this would call an API service
        // For the prototype, we'll simulate the API call
        // apiService.clearMessages()
    }
    
    suspend fun fetchMessages(): List<RemoteChatMessage> {
        // In a real implementation, this would call an API service
        // For the prototype, we'll return an empty list
        return emptyList()
    }
}
