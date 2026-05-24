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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glimpse.ui.theme.*
import org.koin.compose.koinInject

@Composable
fun VideoCallScreen(
    chatId: String,
    viewModel: VideoCallViewModel = koinInject(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

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
        // Üst Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.partnerName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            // Çevrimiçi/Kayıt noktası
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(PrimaryPeach)
            )
        }

        // PiP
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 100.dp, end = 24.dp)
                .size(width = 110.dp, height = 160.dp)
                .clip(AppShapes.medium)
                .background(SurfaceWhite)
        ) {
            Text(
                text = if (state.isCameraOff) "Kamera Kapalı" else "Sen",
                modifier = Modifier.align(Alignment.Center),
                color = TextSecondary,
                fontSize = 12.sp
            )
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
            // Mikrofon Butonu
            ControlButton(
                text = if (state.isMicMuted) "Sessiz" else "Mikrofon",
                isActive = !state.isMicMuted,
                activeColor = BackgroundWarm,
                onClick = { viewModel.setEvent(VideoCallEvent.ToggleMic) }
            )

            // Aramayı Sonlandır Butonu
            ControlButton(
                text = "Kapat",
                isActive = true,
                activeColor = Color(0xFFFF8A8A),
                textColor = SurfaceWhite,
                onClick = { viewModel.setEvent(VideoCallEvent.EndCall) }
            )

            // Kamera Butonu
            ControlButton(
                text = if (state.isCameraOff) "Kamera Yok" else "Kamera",
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