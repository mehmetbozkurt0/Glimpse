package com.glimpse.data.network

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: String? = null,
    val chat_id: String,
    val sender_id: String,
    val content: String,
    val created_at: String? = null
)