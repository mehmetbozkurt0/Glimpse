package com.glimpse

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import com.glimpse.data.local.DatabaseDriverFactory
import com.glimpse.di.sharedModule
import com.glimpse.presentation.chatlist.ChatListScreen
import com.glimpse.presentation.chatroom.ChatRoomScreen
import com.glimpse.presentation.navigation.Screen
import com.glimpse.presentation.videocall.VideoCallScreen
import com.glimpse.ui.theme.GlimpseTheme
import org.koin.compose.KoinApplication
import org.koin.dsl.module

@Composable
fun App(driverFactory: DatabaseDriverFactory) {
    KoinApplication(application = {
        modules(
            module { single { driverFactory } },
            sharedModule
        )
    }) {
        GlimpseTheme {
            var currentScreen by remember { mutableStateOf<Screen>(Screen.ChatList) }

            Crossfade(targetState = currentScreen) { screen ->
                when (screen) {
                    is Screen.ChatList -> {
                        ChatListScreen(
                            onNavigateToChat = { chatId ->
                                currentScreen = Screen.ChatRoom(chatId)
                            }
                        )
                    }
                    is Screen.ChatRoom -> {
                        ChatRoomScreen(
                            chatId = screen.chatId,
                            onNavigateBack = {
                                currentScreen = Screen.ChatList
                            },
                            onNavigateToVideoCall = { chatId ->
                                currentScreen = Screen.VideoCall(chatId)
                            }
                        )
                    }
                    is Screen.VideoCall -> {
                        VideoCallScreen(
                            chatId = screen.chatId,
                            onNavigateBack = {
                                currentScreen = Screen.ChatRoom(screen.chatId)
                            }
                        )
                    }
                }
            }
        }
    }
}