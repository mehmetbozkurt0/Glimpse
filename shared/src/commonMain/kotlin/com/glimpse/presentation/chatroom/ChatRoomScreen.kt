package com.glimpse.presentation.chatroom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glimpse.data.local.MessageEntity
import com.glimpse.ui.theme.*
import org.koin.compose.koinInject
import androidx.compose.runtime.LaunchedEffect

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

    Scaffold(
        containerColor = BackgroundWarm,
        topBar = {
            ChatRoomTopBar(
                partnerName = state.partnerName,
                onBackClick = { viewModel.setEvent(ChatRoomEvent.OnBackClicked) },
                onVideoCallClick = { viewModel.setEvent(ChatRoomEvent.OnVideoCallClicked(chatId)) }
            )
        },
        bottomBar = {
            ChatRoomBottomBar(
                message = state.currentMessage,
                onMessageChange = { viewModel.setEvent(ChatRoomEvent.OnMessageChanged(it)) },
                onSendClick = { viewModel.setEvent(ChatRoomEvent.SendMessage(chatId)) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            reverseLayout = true
        ) {
            items(state.messages.reversed()) { message ->
                MessageBubble(message = message)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ChatRoomTopBar(
    partnerName: String,
    onBackClick: () -> Unit,
    onVideoCallClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite)
            .padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Text("<", fontSize = 28.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(SoftGradientStart)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = partnerName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = "Çevrimiçi", fontSize = 12.sp, color = PrimaryPeach)
        }

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(AccentPink)
                .clickable { onVideoCallClick() }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Kamera", color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
    }
}

@Composable
fun MessageBubble(message: MessageEntity) {
    val isMine = message.isMine == 1L

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .wrapContentWidth(if (isMine) Alignment.End else Alignment.Start)
                .clip(AppShapes.large)
                .background(if (isMine) PrimaryPeach else SurfaceWhite)
                .padding(16.dp)
        ) {
            Text(
                text = message.content,
                color = if (isMine) SurfaceWhite else TextPrimary,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun ChatRoomBottomBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWarm)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Mesaj yaz...", color = TextSecondary) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceWhite,
                unfocusedContainerColor = SurfaceWhite,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = PrimaryPeach
            ),
            shape = CircleShape,
            maxLines = 4
        )

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(if (message.isNotBlank()) PrimaryPeach else DividerColor)
                .clickable(enabled = message.isNotBlank()) { onSendClick() },
            contentAlignment = Alignment.Center
        ) {
            Text("->", color = SurfaceWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
    }
}