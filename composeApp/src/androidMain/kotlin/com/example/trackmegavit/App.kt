package com.example.trackmegavit

import com.example.trackmegavit.core.ui.MdmTrackProTheme
import com.example.trackmegavit.feature.auth.domain.LoginResult
import com.example.trackmegavit.feature.auth.domain.UserRole
import com.example.trackmegavit.feature.auth.domain.UserSession
import com.example.trackmegavit.feature.auth.data.AuthRepository
import com.example.trackmegavit.feature.auth.presentation.LoginScreen
import com.example.trackmegavit.feature.home.presentation.SalesAdvisorHomeScreen
import com.example.trackmegavit.feature.admin.presentation.AdminHomeScreen
import com.example.trackmegavit.feature.admin.presentation.AdminSettingsScreen
import com.example.trackmegavit.feature.activity.presentation.ActivityScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

enum class Screen { HOME, ACTIVITY, REPORTS, ADMIN }

private data class AuthUiState(
    val session: UserSession? = null,
    val failedAttempts: Int = 0,
    val lockedUntil: TimeSource.Monotonic.ValueTimeMark? = null,
)

private const val MAX_FAILED_ATTEMPTS = 3
private val LOCK_DURATION: Duration = 20.seconds

@Composable
@Preview
fun App() {
    val isDarkSystemTheme = isSystemInDarkTheme()
    var darkTheme by remember { mutableStateOf(isDarkSystemTheme) }

    MdmTrackProTheme(darkTheme = darkTheme) {
        val scope = rememberCoroutineScope()
        var authState by remember { mutableStateOf(AuthUiState()) }
        var current by remember { mutableStateOf(Screen.HOME) }
        var userMenuExpanded by remember { mutableStateOf(false) }
        var showChangePasswordDialog by remember { mutableStateOf(false) }
        var newPassword by remember { mutableStateOf("") }
        var isUpdatingPassword by remember { mutableStateOf(false) }
        val snackbarHostState = remember { SnackbarHostState() }

        val isLocked = authState.lockedUntil?.hasNotPassedNow() == true
        val currentSession = authState.session

        if (currentSession == null) {
            LoginScreen(
                isLocked = isLocked,
                lockSeconds = if (isLocked) authState.lockedUntil?.remainingLockSeconds() ?: 0 else 0,
                onLogin = { username, password ->
                    when (val result = AuthRepository.signIn(username, password)) {
                        is LoginResult.Success -> {
                            authState = AuthUiState(session = result.session)
                            current = allowedScreensFor(result.session.role).first()
                            result
                        }
                        is LoginResult.Error -> {
                            val attempts = authState.failedAttempts + 1
                            authState = if (attempts >= MAX_FAILED_ATTEMPTS) {
                                authState.copy(
                                    failedAttempts = 0,
                                    lockedUntil = TimeSource.Monotonic.markNow() + LOCK_DURATION,
                                )
                            } else {
                                authState.copy(failedAttempts = attempts)
                            }
                            result
                        }
                    }
                },
            )
            return@MdmTrackProTheme
        }

        val allowedScreens = allowedScreensFor(currentSession.role)
        if (current !in allowedScreens) {
            current = allowedScreens.first()
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                MdmBottomNav(
                    current = current,
                    allowedScreens = allowedScreens,
                    onSelect = { current = it },
                )
            },
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                when (current) {
                    Screen.HOME -> when (currentSession.role) {
                        UserRole.ADMINISTRADOR, UserRole.SUPERVISOR ->
                            AdminHomeScreen(
                                session = currentSession,
                                onUserClick = { userMenuExpanded = true },
                            )
                        UserRole.ASESOR_VENTAS ->
                            SalesAdvisorHomeScreen(onUserClick = { userMenuExpanded = true })
                    }
                    Screen.ACTIVITY -> ActivityScreen(onUserClick = { userMenuExpanded = true })
                    Screen.REPORTS -> ReportsScreenAndroid(onUserClick = { userMenuExpanded = true })
                    Screen.ADMIN -> AdminSettingsScreen(
                        session = currentSession,
                        darkTheme = darkTheme,
                        onToggleTheme = { darkTheme = !darkTheme },
                        onLogout = {
                            scope.launch {
                                AuthRepository.signOut()
                                authState = AuthUiState()
                                current = Screen.HOME
                                snackbarHostState.showSnackbar("Sesion cerrada correctamente")
                            }
                        },
                        onChangePassword = { password ->
                            AuthRepository.changePassword(password)
                        },
                        onShowMessage = { message ->
                            snackbarHostState.showSnackbar(message)
                        }
                    )
                }

                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    DropdownMenu(
                        expanded = userMenuExpanded,
                        onDismissRequest = { userMenuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(currentSession.email) },
                            onClick = {},
                            enabled = false,
                        )
                        DropdownMenuItem(
                            text = { Text("Cambiar contrasena") },
                            leadingIcon = {
                                Icon(Icons.Default.LockReset, contentDescription = null)
                            },
                            onClick = {
                                userMenuExpanded = false
                                showChangePasswordDialog = true
                            },
                        )
                        DropdownMenuItem(
                            text = {
                                Text(if (darkTheme) "Usar modo claro" else "Usar modo oscuro")
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (darkTheme) {
                                        Icons.Default.Brightness7
                                    } else {
                                        Icons.Default.Brightness4
                                    },
                                    contentDescription = null,
                                )
                            },
                            onClick = {
                                darkTheme = !darkTheme
                                userMenuExpanded = false
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar sesion") },
                            leadingIcon = {
                                Icon(Icons.Default.Logout, contentDescription = null)
                            },
                            onClick = {
                                userMenuExpanded = false
                                scope.launch {
                                    AuthRepository.signOut()
                                    authState = AuthUiState()
                                    current = Screen.HOME
                                    snackbarHostState.showSnackbar("Sesion cerrada correctamente")
                                }
                            },
                        )
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
                        title = { Text("Cambiar contrasena") },
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
                                            snackbarHostState.showSnackbar(
                                                "La contrasena debe tener al menos 6 caracteres",
                                            )
                                        }
                                        return@TextButton
                                    }

                                    scope.launch {
                                        isUpdatingPassword = true
                                        val result = AuthRepository.changePassword(password)
                                        isUpdatingPassword = false
                                        if (result.isSuccess) {
                                            showChangePasswordDialog = false
                                            newPassword = ""
                                            snackbarHostState.showSnackbar("Contrasena actualizada")
                                        } else {
                                            snackbarHostState.showSnackbar(
                                                "No se pudo actualizar la contrasena",
                                            )
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
        }
    }
}

private fun allowedScreensFor(role: UserRole): List<Screen> {
    return when (role) {
        UserRole.ADMINISTRADOR -> listOf(
            Screen.HOME,
            Screen.ACTIVITY,
            Screen.REPORTS,
            Screen.ADMIN,
        )
        UserRole.SUPERVISOR -> listOf(
            Screen.HOME,
            Screen.ACTIVITY,
            Screen.REPORTS,
            Screen.ADMIN,
        )
        UserRole.ASESOR_VENTAS -> listOf(
            Screen.HOME,
            Screen.ACTIVITY,
        )
    }
}

@Composable
private fun MdmBottomNav(
    current: Screen,
    allowedScreens: List<Screen>,
    onSelect: (Screen) -> Unit,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.97f),
        tonalElevation = 0.dp,
    ) {
        val itemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
            selectedTextColor = MaterialTheme.colorScheme.onSurface,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            indicatorColor = MaterialTheme.colorScheme.primary,
        )
        val labelStyle = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            letterSpacing = 0.5.sp,
        )

        val navItems = listOf(
            Triple(Screen.HOME, Icons.Default.Home, "INICIO"),
            Triple(Screen.ACTIVITY, Icons.Default.GpsFixed, "ACTIVIDAD"),
            Triple(Screen.REPORTS, Icons.Default.Assessment, "REPORTES"),
            Triple(Screen.ADMIN, Icons.Default.Settings, "CONFIG"),
        )

        navItems
            .filter { it.first in allowedScreens }
            .forEach { (screen, icon, label) ->
                NavigationBarItem(
                    selected = current == screen,
                    onClick = { onSelect(screen) },
                    icon = { Icon(icon, contentDescription = label) },
                    label = { Text(label, style = labelStyle) },
                    colors = itemColors,
                )
            }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
    }
}

private fun TimeSource.Monotonic.ValueTimeMark.hasNotPassedNow(): Boolean {
    return this > TimeSource.Monotonic.markNow()
}

private fun TimeSource.Monotonic.ValueTimeMark.remainingLockSeconds(): Int {
    val remaining = this - TimeSource.Monotonic.markNow()
    return remaining.inWholeSeconds.coerceAtLeast(0).toInt()
}
