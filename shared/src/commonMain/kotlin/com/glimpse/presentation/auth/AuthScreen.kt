package com.glimpse.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
fun AuthScreen(
    state: AuthState,
    onEvent: (AuthEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWarm),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. LOGO ALANI
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(SurfaceWhite, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✨",
                    fontSize = 40.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. BAŞLIKLAR
            Text(
                text = "Glimpse",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Meaningful connections, without the noise.",
                fontSize = 16.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 3. FORM KARTI
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.large,
                shadowElevation = 2.dp,
                color = SurfaceWhite.copy(alpha = 0.9f)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // E-posta Input
                    GlimpseTextField(
                        value = state.emailInput,
                        onValueChange = { onEvent(AuthEvent.OnEmailChanged(it)) },
                        placeholder = "E-posta Adresi",
                        leadingEmoji = "✉️"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Şifre Input
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

                    // Şifremi Unuttum
                    TextButton(
                        onClick = { /* İleride eklenecek */ },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Şifremi Unuttum", color = TextSecondary, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // GİRİŞ YAP BUTONU
                    Button(
                        onClick = { onEvent(AuthEvent.Submit) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryPeach,
                            contentColor = TextPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = SurfaceWhite, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = if (state.isLoginMode) "Giriş Yap" else "Kayıt Ol",
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("veya", color = DividerColor, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    // KAYIT OL BUTONU
                    OutlinedButton(
                        onClick = { onEvent(AuthEvent.ToggleAuthMode) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, DividerColor)
                    ) {
                        Text(
                            text = if (state.isLoginMode) "Yeni Hesap Oluştur" else "Zaten hesabım var",
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GlimpseTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingEmoji: String,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: () -> Unit = {}
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = TextSecondary.copy(alpha = 0.6f)) },
        leadingIcon = {
            Text(
                text = leadingEmoji,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onPasswordToggle) {
                    Text(
                        text = if (passwordVisible) "👁️" else "🙈",
                        fontSize = 20.sp
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Email),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = BackgroundWarm,
            unfocusedContainerColor = BackgroundWarm,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = PrimaryPeach
        ),
        shape = AppShapes.medium
    )
}