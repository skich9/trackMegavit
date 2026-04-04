package com.example.trackmegavit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

enum class Screen { HOME, ACTIVITY, REPORTS, ADMIN }

private data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val failedAttempts: Int = 0,
    val lockedUntil: TimeSource.Monotonic.ValueTimeMark? = null,
)

private const val MAX_FAILED_ATTEMPTS = 3
private val LOCK_DURATION: Duration = 20.seconds

@Composable
@Preview
fun App() {
    MdmTrackProTheme {
        var authState by remember { mutableStateOf(AuthUiState()) }
        var current by remember { mutableStateOf(Screen.HOME) }
        val isLocked = authState.lockedUntil?.hasNotPassedNow() == true

        if (!authState.isAuthenticated) {
            LoginScreen(
                isLocked = isLocked,
                lockSeconds = if (isLocked) {
                    authState.lockedUntil?.remainingLockSeconds() ?: 0
                } else {
                    0
                },
                onLogin = { username, password ->
                    val success = isValidCredentials(username, password)
                    if (success) {
                        authState = AuthUiState(isAuthenticated = true)
                    } else {
                        val attempts = authState.failedAttempts + 1
                        authState = if (attempts >= MAX_FAILED_ATTEMPTS) {
                            authState.copy(
                                failedAttempts = 0,
                                lockedUntil = TimeSource.Monotonic.markNow() + LOCK_DURATION,
                            )
                        } else {
                            authState.copy(failedAttempts = attempts)
                        }
                    }
                    success
                },
            )
            return@MdmTrackProTheme
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = { MdmBottomNav(current) { current = it } },
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                when (current) {
                    Screen.HOME     -> HomeScreen()
                    Screen.ACTIVITY -> ActivityScreen()
                    Screen.REPORTS  -> PlaceholderScreen("Reports")
                    Screen.ADMIN    -> PlaceholderScreen("Admin")
                }
            }
        }
    }
}

private fun isValidCredentials(username: String, password: String): Boolean {
    // Login temporal local; luego puede migrar a backend seguro.
    return username.trim().equals("admin", ignoreCase = true) && password == "TrackMegavit123"
}

private fun TimeSource.Monotonic.ValueTimeMark.remainingLockSeconds(): Int {
    val remaining = this - TimeSource.Monotonic.markNow()
    return remaining.inWholeSeconds.coerceAtLeast(0).toInt()
}

@Composable
private fun MdmBottomNav(current: Screen, onSelect: (Screen) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.97f),
        tonalElevation = 0.dp,
    ) {
        val itemColors = NavigationBarItemDefaults.colors(
            selectedIconColor   = MaterialTheme.colorScheme.onPrimary,
            selectedTextColor   = MaterialTheme.colorScheme.onSurface,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            indicatorColor      = MaterialTheme.colorScheme.primary,
        )
        val labelStyle = MaterialTheme.typography.labelSmall.copy(
            fontWeight   = FontWeight.Bold,
            fontSize     = 9.sp,
            letterSpacing = 0.5.sp,
        )

        NavigationBarItem(
            selected = current == Screen.HOME,
            onClick  = { onSelect(Screen.HOME) },
            icon     = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label    = { Text("HOME", style = labelStyle) },
            colors   = itemColors,
        )
        NavigationBarItem(
            selected = current == Screen.ACTIVITY,
            onClick  = { onSelect(Screen.ACTIVITY) },
            icon     = { Icon(Icons.Default.GpsFixed, contentDescription = "Activity") },
            label    = { Text("ACTIVITY", style = labelStyle) },
            colors   = itemColors,
        )
        NavigationBarItem(
            selected = current == Screen.REPORTS,
            onClick  = { onSelect(Screen.REPORTS) },
            icon     = { Icon(Icons.Default.Assessment, contentDescription = "Reports") },
            label    = { Text("REPORTS", style = labelStyle) },
            colors   = itemColors,
        )
        NavigationBarItem(
            selected = current == Screen.ADMIN,
            onClick  = { onSelect(Screen.ADMIN) },
            icon     = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
            label    = { Text("ADMIN", style = labelStyle) },
            colors   = itemColors,
        )
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
    }
}