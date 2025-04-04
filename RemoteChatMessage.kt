package com.manusai.android.data.remote.model

data class RemoteChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long
)
