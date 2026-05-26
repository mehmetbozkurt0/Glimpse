package com.glimpse.agora

import kotlinx.coroutines.flow.StateFlow

interface AgoraManager{
    val remoteUid: StateFlow<Int?>

    fun initialize(appId: String)
    fun joinChannel(channelName: String)
    fun leaveChannel()
    fun toggleMic(muted: Boolean)
    fun toggleCamera(muted: Boolean)

    fun setupLocalVideo(container: Any)
    fun setupRemoteVideo(container: Any, uid: Int)
}