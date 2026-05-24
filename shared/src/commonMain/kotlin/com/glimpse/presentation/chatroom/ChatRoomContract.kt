package com.glimpse.presentation.chatroom

import com.glimpse.data.local.MessageEntity
import com.glimpse.presentation.mvi.UiEffect
import com.glimpse.presentation.mvi.UiEvent
import com.glimpse.presentation.mvi.UiState


data class ChatRoomState(
    val isLoading: Boolean = true,
    val messages: List<MessageEntity> = emptyList(),
    val currentMessage: String = "",
    val partnerName: String = "Sohbet"
) : UiState

sealed interface ChatRoomEvent : UiEvent {
    data class OnMessageChanged(val message: String) : ChatRoomEvent
    data class SendMessage(val chatId: String) : ChatRoomEvent
    data object OnBackClicked : ChatRoomEvent
    data class OnVideoCallClicked(val chatId: String) : ChatRoomEvent
}

sealed interface ChatRoomEffect : UiEffect {
    data object NavigateBack : ChatRoomEffect
    data class NavigateToVideoCall(val chatId: String) : ChatRoomEffect
}