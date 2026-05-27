package com.glimpse.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.glimpse.data.local.ChatEntity
import com.glimpse.data.local.GlimpseDatabase
import com.glimpse.data.local.MessageEntity
import com.glimpse.data.network.MessageDto
import com.glimpse.data.network.SupabaseClient
import com.glimpse.domain.repository.ChatRepository

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class ChatRepositoryImpl(
    database: GlimpseDatabase
) : ChatRepository {

    private val queries = database.glimpseDatabaseQueries
    private val myDeviceId = "device_B"

    private var isListening = false

    private fun generateUUID(): String {
        val chars = "0123456789abcdefhijklmnq"
        val random = kotlin.random.Random
        return buildString {
            for (i in 0..35)
                when (i) {
                    8, 13, 18, 23 -> append('-')
                    14 -> append('4')
                    19 -> append(chars[random.nextInt(4) + 8])
                    else -> append(chars[random.nextInt(16)])
                }
        }
    }

    override fun getAllChats(): Flow<List<ChatEntity>> {
        return queries.getAllChats().asFlow().mapToList(Dispatchers.IO)
    }

    override fun getMessagesByChat(chatId: String): Flow<List<MessageEntity>> {
        return queries.getMessagesByChat(chatId).asFlow().mapToList(Dispatchers.IO)
            .map { list -> list.sortedBy { it.timestamp } }
    }

    override suspend fun sendMessage(chatId: String, content: String) {
        val messageId = generateUUID()

        val currentTime = kotlin.time.Clock.System.now().toEpochMilliseconds()

        queries.insertMessage(
            messageId = messageId,
            chatId = chatId,
            senderId = myDeviceId,
            content = content,
            timestamp = currentTime,
            isMine = 1
        )

        try {
            val messageDto = MessageDto(chat_id = chatId, id = messageId, sender_id = myDeviceId, content = content)
            SupabaseClient.client.postgrest.from("messages").insert(messageDto)
        } catch (e: Exception) {
            println("GLIMPSE_HATA: ${e.message}")
        }
    }

    override suspend fun syncMessages(chatId: String) {
        try {
            val messagesFromCloud = SupabaseClient.client.postgrest.from("messages")
                .select {
                    filter { eq("chat_id", chatId) }
                    order("created_at", Order.ASCENDING)
                }
                .decodeList<MessageDto>()

            messagesFromCloud.forEach { dto ->
                val realTime = dto.created_at?.let {
                    try { Instant.parse(it).toEpochMilliseconds() } catch (e: Exception) { kotlin.time.Clock.System.now().toEpochMilliseconds() }
                } ?: kotlin.time.Clock.System.now().toEpochMilliseconds()

                queries.insertMessage(
                    messageId = dto.id ?: generateUUID(),
                    chatId = dto.chat_id,
                    senderId = dto.sender_id,
                    content = dto.content,
                    timestamp = realTime,
                    isMine = if (dto.sender_id == myDeviceId) 1L else 0L
                )
            }
        } catch (e: Exception) {
            println("GLIMPSE_HATA: Eşitleme -> ${e.message}")
        }
    }

    override suspend fun connectAndListen() {
        if (isListening) return
        isListening = true

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
                    val realTime = newMsgDto.created_at?.let {
                        try { kotlin.time.Instant.parse(it).toEpochMilliseconds() } catch (e: Exception) { kotlin.time.Clock.System.now().toEpochMilliseconds() }
                    } ?: kotlin.time.Clock.System.now().toEpochMilliseconds()

                    queries.insertMessage(
                        messageId = newMsgDto.id ?: "msg_${kotlin.random.Random.nextLong()}",
                        chatId = newMsgDto.chat_id,
                        senderId = newMsgDto.sender_id,
                        content = newMsgDto.content,
                        timestamp = realTime,
                        isMine = 0
                    )
                }
            }
        } catch (e: Exception) {
            isListening = false
            println("GLIMPSE_HATA: Canlı Yayın -> ${e.message}")
        }
    }
}