package com.glimpse.presentation.videocall

import com.glimpse.presentation.mvi.UiEffect
import com.glimpse.presentation.mvi.UiEvent
import com.glimpse.presentation.mvi.UiState

data class VideoCallState(
    val isMicMuted: Boolean = false,
    val isCameraOff: Boolean = false,
    val partnerName: String = "Bağlanıyor...",
    val remoteUid: Int? = null,
    val isRemoteCameraOff: Boolean = false
) : UiState

sealed interface VideoCallEvent : UiEvent {
    data class JoinCall(val chatId: String): VideoCallEvent
    data object LeaveCall : VideoCallEvent
    data object ToggleMic : VideoCallEvent
    data object ToggleCamera : VideoCallEvent
    data object EndCall : VideoCallEvent
}

sealed interface VideoCallEffect : UiEffect {
    data object NavigateBack : VideoCallEffect
}