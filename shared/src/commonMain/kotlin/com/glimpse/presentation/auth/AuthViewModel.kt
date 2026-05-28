package com.glimpse.presentation.auth

import androidx.lifecycle.viewModelScope
import com.glimpse.domain.repository.AuthRepository
import com.glimpse.presentation.mvi.BaseViewModel
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : BaseViewModel<AuthState, AuthEvent, AuthEffect>() {

    override fun createInitialState(): AuthState = AuthState()

    init {
        setEvent(AuthEvent.CheckSession)
    }

    override fun handleEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.CheckSession -> {
                viewModelScope.launch {
                    val isLoggedIn = authRepository.checkSession()
                    if (isLoggedIn) {
                        setEffect { AuthEffect.NavigateToChatList }
                    }
                }
            }
            AuthEvent.OnGoogleSignInClick -> {
                viewModelScope.launch {
                    authRepository.signInWithGoogle()
                }
            }
            is AuthEvent.OnEmailChanged -> {
                setState { copy(emailInput = event.email, errorMessage = null) }
            }
            is AuthEvent.OnPasswordChanged -> {
                setState { copy(passwordInput = event.password, errorMessage = null) }
            }
            AuthEvent.ToggleAuthMode -> {
                setState { copy(isLoginMode = !isLoginMode, errorMessage = null) }
            }
            AuthEvent.ClearError -> {
                setState { copy(errorMessage = null) }
            }
            AuthEvent.Submit -> {
                val email = uiState.value.emailInput
                val password = uiState.value.passwordInput
                val isLogin = uiState.value.isLoginMode

                if (email.isBlank() || password.isBlank()) {
                    setEffect { AuthEffect.ShowError("E-posta ve şifre boş bırakılamaz.") }
                    return
                }

                setState { copy(isLoading = true, errorMessage = null) }

                viewModelScope.launch {
                    val result = if (isLogin) {
                        authRepository.signIn(email, password)
                    } else {
                        authRepository.signUp(email, password)
                    }

                    setState { copy(isLoading = false) }

                    result.onSuccess {
                        setEffect { AuthEffect.NavigateToChatList }
                    }.onFailure { error ->
                        // Hatanın tam tipini ve mesajını konsola da yazdıralım
                        println("GLIMPSE_DETAYLI_HATA: Tip -> ${error::class.simpleName}, Mesaj -> ${error.message}")
                        error.printStackTrace()

                        // Ekrana hatanın tipini de basalım ki bilelim ne olduğunu
                        val errorText = error.message ?: "Sistemsel Hata: ${error::class.simpleName}"
                        setState { copy(errorMessage = errorText) }
                    }
                }
            }
        }
    }
}