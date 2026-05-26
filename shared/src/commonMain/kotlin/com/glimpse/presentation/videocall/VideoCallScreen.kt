package com.glimpse.presentation.videocall

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glimpse.agora.AgoraManager
import com.glimpse.agora.AgoraVideoView
import com.glimpse.ui.theme.*
import org.koin.compose.koinInject

@Composable
fun VideoCallScreen(
    chatId: String,
    viewModel: VideoCallViewModel = koinInject(),
    agoraManager: AgoraManager = koinInject(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val remoteUid by agoraManager.remoteUid.collectAsState()

    LaunchedEffect(chatId) {
        agoraManager.initialize("0fcc8a88705740f3b1c35b131336a73c")
        agoraManager.joinChannel(chatId)
    }

    DisposableEffect(Unit) {
        onDispose {
            agoraManager.leaveChannel()
        }
    }

    LaunchedEffect(state.isMicMuted) { agoraManager.toggleMic(state.isMicMuted) }
    LaunchedEffect(state.isCameraOff) { agoraManager.toggleCamera(state.isCameraOff) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                VideoCallEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    // Tam ekran bir kutu
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWarm)
    ) {
        if (remoteUid != null) {
            AgoraVideoView(
                manager = agoraManager,
                isLocal = false,
                remoteUid = remoteUid,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Bağlantı kuruluyor...", color = SurfaceWhite, fontSize = 18.sp)
            }
        }

        // Üst Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 48.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = if (remoteUid != null) "Görüşme Aktif" else "Bağlanıyor...", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(PrimaryPeach))
        }

        // PiP
        if (!state.isCameraOff) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 100.dp, end = 24.dp)
                    .size(width = 120.dp, height = 180.dp)
                    .clip(AppShapes.medium)
                    .shadow(8.dp)
            ) {
                AgoraVideoView(
                    manager = agoraManager,
                    isLocal = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Alt Kontrol Barı
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .clip(AppShapes.large)
                .background(SurfaceWhite.copy(alpha = 0.85f))
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlButton(
                text = if (state.isMicMuted) "Ses Aç" else "Sessiz",
                isActive = !state.isMicMuted,
                activeColor = BackgroundWarm,
                onClick = { viewModel.setEvent(VideoCallEvent.ToggleMic) }
            )

            ControlButton(
                text = "Kapat",
                isActive = true,
                activeColor = Color(0xFFFF8A8A),
                textColor = SurfaceWhite,
                onClick = { viewModel.setEvent(VideoCallEvent.EndCall) }
            )

            ControlButton(
                text = if (state.isCameraOff) "Kam. Aç" else "Kapat",
                isActive = !state.isCameraOff,
                activeColor = BackgroundWarm,
                onClick = { viewModel.setEvent(VideoCallEvent.ToggleCamera) }
            )
        }
    }
}

@Composable
fun ControlButton(
    text: String,
    isActive: Boolean,
    activeColor: Color,
    textColor: Color = TextPrimary,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(if (isActive) activeColor else DividerColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isActive) textColor else TextSecondary
        )
    }
}