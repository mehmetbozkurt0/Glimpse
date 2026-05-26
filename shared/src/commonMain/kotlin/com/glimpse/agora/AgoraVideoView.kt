package com.glimpse.agora

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun AgoraVideoView(
    manager: AgoraManager,
    isLocal: Boolean,
    remoteUid: Int? = null,
    modifier: Modifier = Modifier
)