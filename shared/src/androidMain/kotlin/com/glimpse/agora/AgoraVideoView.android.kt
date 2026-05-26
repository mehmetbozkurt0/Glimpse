package com.glimpse.agora

import android.view.SurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun AgoraVideoView(
    manager: AgoraManager,
    isLocal: Boolean,
    remoteUid: Int?,
    modifier: Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            SurfaceView(context).apply {
                setZOrderMediaOverlay(isLocal)
            }
        },
        update = { view ->
            if (isLocal) {
                manager.setupLocalVideo(view)
            } else if (remoteUid != null) {
                manager.setupRemoteVideo(view, remoteUid)
            }
        }
    )
}