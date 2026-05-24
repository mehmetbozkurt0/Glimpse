package com.glimpse.presentation.navigation

sealed interface Screen {
    data object ChatList : Screen
    data class ChatRoom(val chatId: String) : Screen
    data class VideoCall(val chatId: String) : Screen
}