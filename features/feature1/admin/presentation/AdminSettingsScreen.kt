package com.example.trackmegavit.feature.admin.presentation

import com.example.trackmegavit.feature.auth.domain.UserSession
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AdminSettingsScreen(
    session: UserSession,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onLogout: () -> Unit,
    onChangePassword: suspend (String) -> Result<Unit>,
    onShowMessage: suspend (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var isUpdatingPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Configuracion de cuenta",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = session.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        OutlinedButton(
            onClick = onToggleTheme,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(if (darkTheme) "Cambiar a modo claro" else "Cambiar a modo oscuro")
                Icon(
                    imageVector = if (darkTheme) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                    contentDescription = null,
                )
            }
        }

        OutlinedButton(
            onClick = { showChangePasswordDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Restablecer contrasena")
                Icon(Icons.Default.LockReset, contentDescription = null)
            }
        }

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Cerrar sesion")
                Icon(Icons.Default.Logout, contentDescription = null)
            }
        }
    }

    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isUpdatingPassword) {
                    showChangePasswordDialog = false
                    newPassword = ""
                }
            },
            title = { Text("Restablecer contrasena") },
            text = {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva contrasena") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !isUpdatingPassword,
                    onClick = {
                        val password = newPassword.trim()
                        if (password.length < 6) {
                            scope.launch {
                                onShowMessage("La contrasena debe tener al menos 6 caracteres")
                            }
                            return@TextButton
                        }

                        scope.launch {
                            isUpdatingPassword = true
                            val result = onChangePassword(password)
                            isUpdatingPassword = false
                            if (result.isSuccess) {
                                showChangePasswordDialog = false
                                newPassword = ""
                                onShowMessage("Contrasena actualizada")
                            } else {
                                onShowMessage("No se pudo actualizar la contrasena")
                            }
                        }
                    },
                ) {
                    Text(if (isUpdatingPassword) "Actualizando..." else "Actualizar")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isUpdatingPassword,
                    onClick = {
                        showChangePasswordDialog = false
                        newPassword = ""
                    },
                ) {
                    Text("Cancelar")
                }
            },
        )
    }
}
