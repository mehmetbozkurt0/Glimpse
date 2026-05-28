package com.glimpse.presentation.navigation

sealed interface Screen {
    data object Login : Screen
    data object Register : Screen
    data object ChatList : Screen
    data class ChatRoom(val chatId: String) : Screen
    data class VideoCall(val chatId: String) : Screen
}