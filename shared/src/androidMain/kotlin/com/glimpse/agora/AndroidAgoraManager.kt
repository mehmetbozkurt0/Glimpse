package com.glimpse.agora

import android.content.Context
import android.view.SurfaceView
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import io.agora.rtc2.video.VideoEncoderConfiguration

class AndroidAgoraManager(private val context: Context) : AgoraManager {
    private var rtcEngine: RtcEngine? = null

    private val _remoteUid = MutableStateFlow<Int?>(null)
    override val remoteUid: StateFlow<Int?> = _remoteUid.asStateFlow()

    private val _isRemoteVideoMuted = MutableStateFlow(false)
    override val isRemoteVideoMuted: StateFlow<Boolean> = _isRemoteVideoMuted.asStateFlow()

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            _remoteUid.value = uid
            _isRemoteVideoMuted.value = false
        }
        override fun onUserOffline(uid: Int, reason: Int) {
            if (_remoteUid.value == uid) _remoteUid.value = null
        }
        override fun onUserMuteVideo(uid: Int, muted: Boolean) {
            if (_remoteUid.value == uid) {
                _isRemoteVideoMuted.value = muted
            }
        }
    }

    override fun initialize(appId: String) {
        try {
            val config = RtcEngineConfig()
            config.mContext = context
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            rtcEngine = RtcEngine.create(config)

            rtcEngine?.enableVideo()

            rtcEngine?.setVideoEncoderConfiguration(
                VideoEncoderConfiguration(
                    VideoEncoderConfiguration.VD_640x360,
                    VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                    VideoEncoderConfiguration.STANDARD_BITRATE,
                    VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE
                )
            )

            rtcEngine?.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun joinChannel(channelName: String) {
        rtcEngine?.joinChannel(null, channelName, "Extra Data", 0)
        rtcEngine?.startPreview()
    }

    override fun leaveChannel() {
        rtcEngine?.leaveChannel()
        _remoteUid.value = null
    }

    override fun toggleMic(muted: Boolean) { rtcEngine?.muteLocalAudioStream(muted) }
    override fun toggleCamera(muted: Boolean) { rtcEngine?.muteLocalVideoStream(muted) }

    override fun setupLocalVideo(container: Any) {
        val surfaceView = container as? SurfaceView ?: return
        rtcEngine?.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0))
    }

    override fun setupRemoteVideo(container: Any, uid: Int) {
        val surfaceView = container as? SurfaceView ?: return
        rtcEngine?.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid))
    }
}