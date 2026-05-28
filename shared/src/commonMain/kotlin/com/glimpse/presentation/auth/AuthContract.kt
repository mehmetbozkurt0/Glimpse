package com.glimpse.presentation.auth

import com.glimpse.presentation.mvi.UiEffect
import com.glimpse.presentation.mvi.UiEvent
import com.glimpse.presentation.mvi.UiState

data class AuthState(
    val isLoading: Boolean = false,
    val isLoginMode: Boolean = true,
    val emailInput: String = "",
    val passwordInput: String = ""
) : UiState

sealed interface AuthEvent : UiEvent {
    data class OnEmailChanged(val email: String) : AuthEvent
    data class OnPasswordChanged(val password: String) : AuthEvent
    data object ToggleAuthMode : AuthEvent
    data object Submit : AuthEvent
    data object CheckSession : AuthEvent
}

sealed interface AuthEffect : UiEffect {
    data object NavigateToChatList : AuthEffect
    data class ShowError(val message: String) : AuthEffect
}