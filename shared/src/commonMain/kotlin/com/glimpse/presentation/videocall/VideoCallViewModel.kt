package com.glimpse.presentation.videocall

import androidx.lifecycle.viewModelScope
import com.glimpse.agora.AgoraManager
import com.glimpse.presentation.mvi.BaseViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class VideoCallViewModel(
    private val agoraManager: AgoraManager
) : BaseViewModel<VideoCallState, VideoCallEvent, VideoCallEffect>() {

    override fun createInitialState(): VideoCallState = VideoCallState()

    init {
        agoraManager.remoteUid.onEach { uid ->
            setState { copy(remoteUid = uid) }
        }.launchIn(viewModelScope)

        agoraManager.isRemoteVideoMuted.onEach { isMuted ->
            setState { copy(isRemoteCameraOff = isMuted) }
        }.launchIn(viewModelScope)
    }

    override fun handleEvent(event: VideoCallEvent) {
        when (event) {
            is VideoCallEvent.JoinCall -> {
                agoraManager.initialize("0fcc8a88705740f3b1c35b131336a73c")
                agoraManager.joinChannel(event.chatId)
            }
            VideoCallEvent.LeaveCall -> {
                agoraManager.leaveChannel()
            }
            VideoCallEvent.ToggleMic -> {
                val newState = !uiState.value.isMicMuted
                agoraManager.toggleMic(newState)
                setState { copy(isMicMuted = newState) }
            }
            VideoCallEvent.ToggleCamera -> {
                val newState = !uiState.value.isCameraOff
                agoraManager.toggleCamera(newState)
                setState { copy(isCameraOff = newState) }
            }
            VideoCallEvent.EndCall -> {
                agoraManager.leaveChannel()
                setEffect { VideoCallEffect.NavigateBack }
            }
        }
    }
}