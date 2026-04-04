package com.example.trackmegavit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    isLocked: Boolean,
    lockSeconds: Int,
    onLogin: (username: String, password: String) -> Boolean,
) {
    val colors = MaterialTheme.colorScheme
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        colors.primaryContainer.copy(alpha = 0.55f),
                        colors.background,
                        colors.surfaceContainerLow,
                    )
                )
            ),
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = colors.surface,
            tonalElevation = 1.dp,
            shadowElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(colors.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = colors.primary,
                        )
                    }
                    Column {
                        Text(
                            text = "TRACKMEGAVIT",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.4).sp,
                            ),
                            color = colors.onSurface,
                        )
                        Text(
                            text = "SECURE ACCESS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.8.sp,
                            ),
                            color = colors.primary,
                        )
                    }
                }

                Spacer(Modifier.height(2.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        message = ""
                    },
                    label = { Text("Usuario") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLocked,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.surfaceContainerLowest,
                        unfocusedContainerColor = colors.surfaceContainerLowest,
                    ),
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        message = ""
                    },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    enabled = !isLocked,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    visualTransformation = if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña",
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.surfaceContainerLowest,
                        unfocusedContainerColor = colors.surfaceContainerLowest,
                    ),
                )

                if (isLocked) {
                    Text(
                        text = "Demasiados intentos. Intenta de nuevo en ${lockSeconds}s.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.error,
                    )
                } else if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.error,
                    )
                }

                Button(
                    onClick = {
                        val user = username.trim()
                        if (user.isEmpty() || password.isEmpty()) {
                            message = "Usuario y contraseña son obligatorios"
                            return@Button
                        }
                        val ok = onLogin(user, password)
                        if (!ok) {
                            message = "Credenciales inválidas"
                        }
                    },
                    enabled = !isLocked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = Color.White,
                    ),
                ) {
                    Text(
                        text = "INICIAR SESION",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                        ),
                    )
                }

                TextButton(
                    onClick = {
                        username = "admin"
                        password = "TrackMegavit123"
                        message = "Demo cargada"
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = !isLocked,
                ) {
                    Text("Usar credenciales demo")
                }
            }
        }
    }
}
