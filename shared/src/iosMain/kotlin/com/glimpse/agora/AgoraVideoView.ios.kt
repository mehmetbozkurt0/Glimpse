package com.glimpse.agora

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun AgoraVideoView(
    manager: AgoraManager,
    isLocal: Boolean,
    remoteUid: Int?,
    modifier: Modifier
) {
    UIKitView(
        factory = { UIView() },
        modifier = modifier,
        update = { }
    )
}