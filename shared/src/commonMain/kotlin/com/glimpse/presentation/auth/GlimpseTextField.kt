package com.glimpse.presentation.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.glimpse.ui.theme.AppShapes
import com.glimpse.ui.theme.BackgroundWarm
import com.glimpse.ui.theme.PrimaryPeach
import com.glimpse.ui.theme.TextSecondary

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