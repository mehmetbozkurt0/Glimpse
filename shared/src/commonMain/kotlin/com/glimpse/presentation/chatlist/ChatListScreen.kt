package com.glimpse.presentation.chatlist

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glimpse.data.local.ChatEntity
import com.glimpse.ui.theme.*
import org.koin.compose.koinInject

@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel = koinInject(),
    onNavigateToChat: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ChatListEffect.NavigateToChat -> onNavigateToChat(effect.chatId)
            }
        }
    }

    Scaffold(
        containerColor = BackgroundWarm,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Yeni sohbet */ },
                containerColor = PrimaryPeach,
                contentColor = SurfaceWhite,
                shape = CircleShape,
                modifier = Modifier.shadow(8.dp, CircleShape)
            ) {
                Text("+", fontSize = 28.sp, fontWeight = FontWeight.Medium)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Sohbetler", fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(24.dp))

            // Arama Çubuğu
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.setEvent(ChatListEvent.OnSearchQueryChanged(it)) },
                modifier = Modifier.fillMaxWidth().shadow(4.dp, CircleShape),
                placeholder = { Text("Kişi veya mesaj ara...", color = TextSecondary) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceWhite,
                    unfocusedContainerColor = SurfaceWhite,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = CircleShape
            )

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.chats) { chat ->
                    ChatItem(chat = chat, onClick = { viewModel.setEvent(ChatListEvent.OnChatClicked(chat.chatId)) })
                }
            }
        }
    }
}

@Composable
fun ChatItem(chat: ChatEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, AppShapes.large)
            .clip(AppShapes.large)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(SurfaceWhite, Color(0xFFFFFDFB))
                )
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(SoftGradientStart))

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(chat.name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(chat.lastMessage ?: "...", fontSize = 13.sp, color = TextSecondary, maxLines = 1)
        }

        Text("Şimdi", fontSize = 11.sp, color = TextSecondary)
    }
}