package com.glimpse

import androidx.compose.ui.window.ComposeUIViewController
import com.glimpse.agora.AgoraManager
import com.glimpse.data.local.DatabaseDriverFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun MainViewController() = ComposeUIViewController {
    val dummyAgoraManager = object : AgoraManager {
        override val remoteUid: StateFlow<Int?> = MutableStateFlow<Int?>(null)
        override fun initialize(appId: String) {}
        override fun joinChannel(channelName: String) {}
        override fun leaveChannel() {}
        override fun toggleMic(muted: Boolean) {}
        override fun toggleCamera(muted: Boolean) {}
        override fun setupLocalVideo(container: Any) {}
        override fun setupRemoteVideo(container: Any, uid: Int) {}
    }

    App(
        driverFactory = DatabaseDriverFactory(),
        agoraManager = dummyAgoraManager
    )
}