package com.glimpse.presentation.chatroom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import glimpse.shared.generated.resources.Res
import glimpse.shared.generated.resources.user

import com.glimpse.data.local.MessageEntity
import com.glimpse.ui.theme.*
import org.koin.compose.koinInject

val GlassmorphismBarColor = SurfaceWhite.copy(alpha = 0.75f)
val SenderBubbleColor = Color(0xFFE0D8FB)
val ReceiverBubbleColor = Color(0xFFF7F3F0)
val SendButtonColor = Color(0xFFFFD4B2)
val OnlineGreen = Color(0xFF4CAF50)

@Composable
fun ChatRoomScreen(
    chatId: String,
    viewModel: ChatRoomViewModel = koinInject(),
    onNavigateBack: () -> Unit,
    onNavigateToVideoCall: (String) -> Unit
) {
    LaunchedEffect(chatId) {
        viewModel.initChat(chatId)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                ChatRoomEffect.NavigateBack -> onNavigateBack()
                is ChatRoomEffect.NavigateToVideoCall -> onNavigateToVideoCall(effect.chatId)
            }
        }
    }

    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFF6EAE4),
                        Color(0xFFF7F3F2),
                        Color(0xFFEBE4F7)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 130.dp, bottom = 120.dp, start = 16.dp, end = 16.dp),
            reverseLayout = true
        ) {
            items(state.messages.reversed()) { message ->
                MessageBubbleLayout(message = message)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Bugün",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier
                            .background(Color(0xFFF7F3F0).copy(alpha = 0.8f), CircleShape)
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }

        ChatRoomTopBar(
            partnerName = state.partnerName,
            modifier = Modifier.align(Alignment.TopCenter),
            onBackClick = { viewModel.setEvent(ChatRoomEvent.OnBackClicked) },
            onVideoCallClick = { viewModel.setEvent(ChatRoomEvent.OnVideoCallClicked(chatId)) }
        )

        ChatRoomBottomBar(
            message = state.currentMessage,
            modifier = Modifier.align(Alignment.BottomCenter),
            onMessageChange = { viewModel.setEvent(ChatRoomEvent.OnMessageChanged(it)) },
            onSendClick = { viewModel.setEvent(ChatRoomEvent.SendMessage(chatId)) }
        )
    }
}

@Composable
fun ChatRoomTopBar(
    partnerName: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onVideoCallClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp), ambientColor = Color.Black.copy(alpha = 0.05f))
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(GlassmorphismBarColor)
            .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Text("<-", fontSize = 24.sp, color = TextPrimary)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(modifier = Modifier.size(46.dp)) {
            Image(
                painter = painterResource(Res.drawable.user),
                contentDescription = "Profil",
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(OnlineGreen)
                    .border(2.dp, SurfaceWhite, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = partnerName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = "Çevrimiçi", fontSize = 12.sp, color = TextSecondary)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("🎥", fontSize = 20.sp, modifier = Modifier.clickable { onVideoCallClick() })
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .border(1.5.dp, TextPrimary, CircleShape)
                    .clickable { /* Bilgi Ekranı */ },
                contentAlignment = Alignment.Center
            ) {
                Text("i", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }
}

@Composable
fun MessageBubbleLayout(message: MessageEntity) {
    val isMine = message.isMine == 1L

    val bubbleShape = if (isMine) {
        RoundedCornerShape(topStart = 24.dp, topEnd = 4.dp, bottomStart = 24.dp, bottomEnd = 24.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 24.dp)
    }

    val bubbleColor = if (isMine) SenderBubbleColor else ReceiverBubbleColor

    val timeString = remember(message.timestamp) {
        if (message.timestamp == 0L) {
            "..."
        } else {
            val instant = Instant.fromEpochMilliseconds(message.timestamp)
            val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            val hour = localDateTime.hour.toString().padStart(2, '0')
            val minute = localDateTime.minute.toString().padStart(2, '0')
            "$hour:$minute"
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .shadow(2.dp, bubbleShape)
                .clip(bubbleShape)
                .background(bubbleColor)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message.content,
                color = TextPrimary,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = timeString, fontSize = 11.sp, color = TextSecondary)

            if (isMine) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "✓✓", fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun ChatRoomBottomBar(
    message: String,
    modifier: Modifier = Modifier,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(16.dp, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp), ambientColor = Color.Black.copy(alpha = 0.08f))
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(GlassmorphismBarColor)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .border(1.5.dp, TextPrimary, CircleShape)
                .clickable { /* Ek ekleme */ },
            contentAlignment = Alignment.Center
        ) {
            Text("+", fontSize = 24.sp, color = TextPrimary)
        }

        Spacer(modifier = Modifier.width(12.dp))

        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Bir mesaj yazın...", color = TextSecondary) },
            trailingIcon = { Text("😊", fontSize = 20.sp, modifier = Modifier.padding(end = 12.dp)) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF7F3F0).copy(alpha = 0.5f),
                unfocusedContainerColor = Color(0xFFF7F3F0).copy(alpha = 0.5f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = TextPrimary
            ),
            shape = CircleShape,
            maxLines = 4
        )

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SendButtonColor)
                .clickable(enabled = message.isNotBlank()) { onSendClick() },
            contentAlignment = Alignment.Center
        ) {
            Text("➤", fontSize = 20.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}