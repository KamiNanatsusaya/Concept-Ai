package com.manusai.android.data.remote

import com.manusai.android.data.remote.model.RemoteChatMessage
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("messages")
    suspend fun getMessages(): List<RemoteChatMessage>
    
    @POST("messages")
    suspend fun syncMessage(@Body message: RemoteChatMessage)
    
    @DELETE("messages")
    suspend fun clearMessages()
}
