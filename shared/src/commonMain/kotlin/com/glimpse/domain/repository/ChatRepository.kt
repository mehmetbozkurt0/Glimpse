package com.glimpse.domain.repository

import com.glimpse.data.local.ChatEntity
import com.glimpse.data.local.MessageEntity
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getAllChats(): Flow<List<ChatEntity>>
    fun getMessagesByChat(chatId: String): Flow<List<MessageEntity>>

    suspend fun sendMessage(chatId: String, content: String)
    suspend fun connectAndListen()
}