package com.glimpse.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glimpse.ui.theme.AppShapes
import com.glimpse.ui.theme.BackgroundWarm
import com.glimpse.ui.theme.DividerColor
import com.glimpse.ui.theme.PrimaryPeach
import com.glimpse.ui.theme.SurfaceWhite
import com.glimpse.ui.theme.TextPrimary
import com.glimpse.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    state: AuthState,
    onEvent: (AuthEvent) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    LaunchedEffect(Unit) {
        if (!state.isLoginMode) onEvent(AuthEvent.ToggleAuthMode)
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundWarm), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            Box(modifier = Modifier.size(100.dp).background(SurfaceWhite, CircleShape), contentAlignment = Alignment.Center) {
                Text(text = "✨", fontSize = 40.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Tekrar Hoş Geldin", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = "Glimpse'e giriş yap", fontSize = 16.sp, color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
            Spacer(modifier = Modifier.height(48.dp))

            Surface(modifier = Modifier.fillMaxWidth(), shape = AppShapes.large, shadowElevation = 2.dp, color = SurfaceWhite.copy(alpha = 0.9f)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                    GlimpseTextField(
                        value = state.emailInput,
                        onValueChange = { onEvent(AuthEvent.OnEmailChanged(it)) },
                        placeholder = "E-posta Adresi",
                        leadingEmoji = "✉️"
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    var passwordVisible by remember { mutableStateOf(false) }
                    GlimpseTextField(
                        value = state.passwordInput,
                        onValueChange = { onEvent(AuthEvent.OnPasswordChanged(it)) },
                        placeholder = "Şifre",
                        leadingEmoji = "🔒",
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onPasswordToggle = { passwordVisible = !passwordVisible }
                    )

                    TextButton(onClick = { /* TODO: Şifremi unuttum */ }, modifier = Modifier.align(Alignment.End)) {
                        Text("Şifremi Unuttum", color = TextSecondary, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.errorMessage != null) {
                        Text(
                            text = state.errorMessage,
                            color = Color(0xFFE57373),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Button(
                        onClick = { onEvent(AuthEvent.Submit) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPeach, contentColor = TextPrimary)
                    ) {
                        if (state.isLoading) CircularProgressIndicator(color = SurfaceWhite, modifier = Modifier.size(24.dp))
                        else Text(text = "Giriş Yap", fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = DividerDefaults.Thickness,
                            color = DividerColor
                        )
                        Text(
                            text = "veya",
                            color = TextSecondary.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = DividerDefaults.Thickness,
                            color = DividerColor
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { onEvent(AuthEvent.OnGoogleSignInClick) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, DividerColor)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🌐", fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                            Text(
                                text = "Google ile Devam Et",
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }


                    Spacer(modifier = Modifier.height(24.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Hesabın yok mu? ", color = TextSecondary, fontSize = 14.sp)
                        Text(
                            text = "Kayıt Ol",
                            color = PrimaryPeach,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }
        }
    }
}