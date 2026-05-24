package com.glimpse.presentation.videocall

import com.glimpse.presentation.mvi.UiEffect
import com.glimpse.presentation.mvi.UiEvent
import com.glimpse.presentation.mvi.UiState

data class VideoCallState(
    val isMicMuted: Boolean = false,
    val isCameraOff: Boolean = false,
    val partnerName: String = "Bağlanıyor..."
) : UiState

sealed interface VideoCallEvent : UiEvent {
    data object ToggleMic : VideoCallEvent
    data object ToggleCamera : VideoCallEvent
    data object EndCall : VideoCallEvent
}

sealed interface VideoCallEffect : UiEffect {
    data object NavigateBack : VideoCallEffect
}