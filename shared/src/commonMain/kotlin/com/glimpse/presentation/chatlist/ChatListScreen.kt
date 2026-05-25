package com.glimpse.presentation.chatlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import glimpse.shared.generated.resources.Res
import glimpse.shared.generated.resources.user

import com.glimpse.data.local.ChatEntity
import com.glimpse.ui.theme.*
import org.koin.compose.koinInject

val TopBottomBarColor = Color(0xFFFFFDFC).copy(alpha = 0.95f)
val FabBackgroundColor = Color(0xFFE8DDFF)
val ActiveTabBackground = Color(0xFFFFDCC8)
val ActiveTabText = Color(0xFFD67352)
val BadgeColor = Color(0xFFFFD1B3)

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFF0EB),
                        Color(0xFFFFFDFB),
                        Color(0xFFF7F3FF)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 130.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.setEvent(ChatListEvent.OnSearchQueryChanged(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    placeholder = { Text("Sohbetlerde ara...", color = TextSecondary) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5EFEB).copy(alpha = 0.5f),
                        unfocusedContainerColor = Color(0xFFF5EFEB).copy(alpha = 0.5f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = PrimaryPeach
                    ),
                    shape = CircleShape
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Sohbet Listesi
            itemsIndexed(state.chats) { index, chat ->
                val mockUnread = if (index == 0) 2 else if (index == 2) 1 else null

                ChatItem(
                    chat = chat,
                    unreadCount = mockUnread,
                    onClick = { viewModel.setEvent(ChatListEvent.OnChatClicked(chat.chatId)) }
                )
            }

            if (state.chats.isEmpty()) {
                item {
                    Text(
                        text = "Henüz bir sohbetin yok.",
                        color = TextSecondary,
                        modifier = Modifier.padding(start = 24.dp, top = 32.dp)
                    )
                }
            }
        }

        TopAppBarSection(modifier = Modifier.align(Alignment.TopCenter))

        CustomBottomNavigationBar(modifier = Modifier.align(Alignment.BottomCenter))

        FloatingActionButton(
            onClick = { /* Yeni sohbet */ },
            containerColor = FabBackgroundColor,
            contentColor = TextPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 24.dp)
        ) {
            Text("📝", fontSize = 20.sp)
        }
    }
}

@Composable
fun TopAppBarSection(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(TopBottomBarColor)
            .padding(top = 48.dp, bottom = 16.dp, start = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(Res.drawable.user),
            contentDescription = "Profil",
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Text(
            text = "Glimpse",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF7A4A35)
        )

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable { /* Ayarlar */ },
            contentAlignment = Alignment.Center
        ) {
            Text("⚙️", fontSize = 22.sp)
        }
    }
}

@Composable
fun CustomBottomNavigationBar(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableStateOf(0) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(TopBottomBarColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem("Chats", "💬", selectedTab == 0, { selectedTab = 0 })
        BottomNavItem("Circle", "👥", selectedTab == 1, { selectedTab = 1 })
        BottomNavItem("Moments", "✨", selectedTab == 2, { selectedTab = 2 })
        BottomNavItem("Profile", "👤", selectedTab == 3, { selectedTab = 3 })
    }
}

@Composable
fun BottomNavItem(label: String, iconSymbol: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) ActiveTabBackground else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = iconSymbol, fontSize = 20.sp, color = if (isSelected) ActiveTabText else TextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) ActiveTabText else TextSecondary
        )
    }
}

@Composable
fun ChatItem(chat: ChatEntity, unreadCount: Int?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Avatar
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFE5D5C5))
        )

        Spacer(modifier = Modifier.width(16.dp))

        // İsim ve Mesaj
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chat.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = chat.lastMessage ?: "...",
                fontSize = 14.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            Text(text = "10:42", fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(6.dp))

            if (unreadCount != null) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(BadgeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = unreadCount.toString(), fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            } else {
                Spacer(modifier = Modifier.size(24.dp))
            }
        }
    }
}