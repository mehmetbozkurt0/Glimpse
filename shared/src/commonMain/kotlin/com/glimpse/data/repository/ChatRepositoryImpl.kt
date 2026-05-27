package com.glimpse.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.glimpse.data.local.ChatEntity
import com.glimpse.data.local.GlimpseDatabase
import com.glimpse.data.local.MessageEntity
import com.glimpse.data.network.MessageDto
import com.glimpse.data.network.SupabaseClient
import com.glimpse.domain.repository.ChatRepository

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class ChatRepositoryImpl(
    database: GlimpseDatabase
) : ChatRepository {

    private val queries = database.glimpseDatabaseQueries
    private val myDeviceId = "device_B"

    override fun getAllChats(): Flow<List<ChatEntity>> {
        return queries.getAllChats().asFlow().mapToList(Dispatchers.IO)
    }

    override fun getMessagesByChat(chatId: String): Flow<List<MessageEntity>> {
        return queries.getMessagesByChat(chatId).asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun sendMessage(chatId: String, content: String) {
        val messageId = "msg_${kotlin.random.Random.nextLong()}"

        // 1. Yerel Veritabanına Kaydet
        queries.insertMessage(
            messageId = messageId,
            chatId = chatId,
            senderId = myDeviceId,
            content = content,
            timestamp = 0L,
            isMine = 1
        )

        // 2. Supabase Bulutuna Gönder
        try {
            val messageDto = MessageDto(
                chat_id = chatId,
                sender_id = myDeviceId,
                content = content
            )
            SupabaseClient.client.postgrest.from("messages").insert(messageDto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun connectAndListen() {
        try {
            val supabase = SupabaseClient.client

            supabase.realtime.connect()

            val channel = supabase.channel("public-messages")
            val changes = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                table = "messages"
            }

            channel.subscribe()

            changes.collect { action ->
                val newMsgDto = action.decodeRecord<MessageDto>()

                if (newMsgDto.sender_id != myDeviceId) {
                    queries.insertMessage(
                        messageId = newMsgDto.id ?: "msg_${kotlin.random.Random.nextLong()}",
                        chatId = newMsgDto.chat_id,
                        senderId = newMsgDto.sender_id,
                        content = newMsgDto.content,
                        timestamp = 0L,
                        isMine = 0
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}