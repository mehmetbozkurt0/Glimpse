package com.glimpse.presentation.videocall

import androidx.compose.foundation.Image
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
import glimpse.shared.generated.resources.Res
import glimpse.shared.generated.resources.video_slash
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun VideoCallScreen(
    chatId: String,
    viewModel: VideoCallViewModel = koinInject(),
    agoraManager: AgoraManager = koinInject(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(chatId) {
        viewModel.setEvent(VideoCallEvent.JoinCall(chatId))
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.setEvent(VideoCallEvent.LeaveCall)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                VideoCallEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWarm)
    ) {
        // --- 1. ANA EKRAN ---
        if (state.remoteUid != null) {
            if (state.isRemoteCameraOff) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color(0xFF1E1E1E)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(Res.drawable.video_slash),
                        contentDescription = "Karşı taraf kamerasını kapattı",
                        modifier = Modifier.size(72.dp)
                    )
                }
            } else {
                AgoraVideoView(
                    manager = agoraManager,
                    isLocal = false,
                    remoteUid = state.remoteUid,
                    modifier = Modifier.fillMaxSize()
                )
            }
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
            Text(text = if (state.remoteUid != null) "Görüşme Aktif" else "Bağlanıyor...", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SurfaceWhite)
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(PrimaryPeach))
        }

        // --- 2. PiP KÜÇÜK EKRAN ---
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 100.dp, end = 24.dp)
                .size(width = 120.dp, height = 180.dp)
                .clip(AppShapes.medium)
                .shadow(8.dp)
                .background(Color(0xFF2C2C2C))
        ) {
            if (state.isCameraOff) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(Res.drawable.video_slash),
                        contentDescription = "Kameram Kapalı",
                        modifier = Modifier.size(40.dp)
                    )
                }
            } else {
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