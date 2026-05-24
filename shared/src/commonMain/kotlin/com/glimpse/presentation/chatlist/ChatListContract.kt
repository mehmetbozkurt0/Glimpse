package com.glimpse.presentation.chatlist

import com.glimpse.data.local.ChatEntity
import com.glimpse.presentation.mvi.UiEffect
import com.glimpse.presentation.mvi.UiEvent
import com.glimpse.presentation.mvi.UiState

data class ChatListState(
    val isLoading: Boolean = true,
    val chats: List<ChatEntity> = emptyList(),
    val searchQuery: String = ""
): UiState

sealed interface ChatListEvent: UiEvent {
    data class OnSearchQueryChanged(val query: String): ChatListEvent
    data class OnChatClicked(val chatId: String): ChatListEvent
    data object RefreshChats: ChatListEvent
}

sealed interface ChatListEffect: UiEffect {
    data class NavigateToChat(val chatId: String): ChatListEffect
}