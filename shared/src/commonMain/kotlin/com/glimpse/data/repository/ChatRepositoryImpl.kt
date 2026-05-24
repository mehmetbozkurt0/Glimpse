package com.glimpse.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.glimpse.data.local.ChatEntity
import com.glimpse.data.local.GlimpseDatabase
import com.glimpse.data.local.MessageEntity
import com.glimpse.data.network.NetworkClient
import com.glimpse.domain.repository.ChatRepository
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

class ChatRepositoryImpl(
    database: GlimpseDatabase,
    private val networkClient: NetworkClient
) : ChatRepository {

    private val queries = database.glimpseDatabaseQueries
    private var webSocketSession: WebSocketSession? = null

    override fun getAllChats(): Flow<List<ChatEntity>> {
        return queries.getAllChats().asFlow().mapToList(Dispatchers.IO)
    }

    override fun getMessagesByChat(chatId: String): Flow<List<MessageEntity>> {
        return queries.getMessagesByChat(chatId).asFlow().mapToList(Dispatchers.IO)
    }

    override suspend fun sendMessage(chatId: String, content: String) {
        val messageId = "msg_${kotlin.random.Random.nextLong()}"
        queries.insertMessage(
            messageId = messageId,
            chatId = chatId,
            senderId = "me",
            content = content,
            timestamp = 0L, // İleride gerçek zaman damgası eklenecek
            isMine = 1
        )

        // 2. Sonra WebSocket üzerinden backend'e iletiyoruz
        try {
            webSocketSession?.send(Frame.Text("""{"chatId":"$chatId", "content":"$content"}"""))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun connectAndListen() {
        try {
            webSocketSession = networkClient.client.webSocketSession(host = "10.0.2.2", port = 8080, path = "/chat")

            for (frame in webSocketSession!!.incoming) {
                if (frame is Frame.Text) {
                    val receivedText = frame.readText()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace() // Bağlantı koparsa veya hata olursa
        }
    }
}