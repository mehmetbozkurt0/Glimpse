package com.glimpse

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import com.glimpse.agora.AgoraManager
import com.glimpse.data.local.DatabaseDriverFactory
import com.glimpse.di.sharedModule
import com.glimpse.presentation.auth.AuthEffect
import com.glimpse.presentation.auth.AuthViewModel
import com.glimpse.presentation.auth.LoginScreen
import com.glimpse.presentation.auth.RegisterScreen
import com.glimpse.presentation.chatlist.ChatListScreen
import com.glimpse.presentation.chatroom.ChatRoomScreen
import com.glimpse.presentation.navigation.Screen
import com.glimpse.presentation.videocall.VideoCallScreen
import com.glimpse.ui.theme.GlimpseTheme
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.module

@Composable
fun App(
    driverFactory: DatabaseDriverFactory,
    agoraManager: AgoraManager
) {
    KoinApplication(application = {
        modules(
            module{
                single { driverFactory }
                single { agoraManager }
            },
            sharedModule
        )
    }) {
        GlimpseTheme {
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

            Crossfade(targetState = currentScreen) { screen ->
                when (screen) {
                    is Screen.Login -> {
                        val authViewModel: AuthViewModel = koinInject()
                        val authState by authViewModel.uiState.collectAsState()

                        LaunchedEffect(Unit){
                            authViewModel.uiEffect.collect { effect ->
                                when (effect) {
                                    is AuthEffect.NavigateToChatList -> {
                                        currentScreen = Screen.ChatList
                                    }
                                    is AuthEffect.ShowError -> {
                                        println("GLIMPSE_AUTH_HATA: ${effect.message}")
                                    }
                                }
                            }
                        }

                        LoginScreen(
                            state = authState,
                            onEvent = {event -> authViewModel.setEvent(event)},
                            onNavigateToRegister = { currentScreen = Screen.Register}
                        )
                    }
                    is Screen.Register -> {
                        val authViewModel: AuthViewModel = koinInject()
                        val authState by authViewModel.uiState.collectAsState()

                        LaunchedEffect(Unit) {
                            authViewModel.uiEffect.collect { effect ->
                                when (effect) {
                                    is AuthEffect.NavigateToChatList -> currentScreen = Screen.ChatList
                                    is AuthEffect.ShowError -> println("GLIMPSE_AUTH_HATA: ${effect.message}")
                                }
                            }
                        }

                        RegisterScreen(
                            state = authState,
                            onEvent = { event -> authViewModel.setEvent(event) },
                            onNavigateToLogin = { currentScreen = Screen.Login }
                        )
                    }
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