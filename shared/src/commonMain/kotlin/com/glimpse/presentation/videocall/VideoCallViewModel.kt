package com.glimpse.presentation.videocall

import com.glimpse.presentation.mvi.BaseViewModel

class VideoCallViewModel : BaseViewModel<VideoCallState, VideoCallEvent, VideoCallEffect>() {

    override fun createInitialState(): VideoCallState = VideoCallState()

    override fun handleEvent(event: VideoCallEvent) {
        when (event) {
            VideoCallEvent.ToggleMic -> {
                setState { copy(isMicMuted = !isMicMuted) }
            }
            VideoCallEvent.ToggleCamera -> {
                setState { copy(isCameraOff = !isCameraOff) }
            }
            VideoCallEvent.EndCall -> {
                setEffect { VideoCallEffect.NavigateBack }
            }
        }
    }
}