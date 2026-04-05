package com.example.trackmegavit

enum class UserRole {
    ADMINISTRADOR,
    SUPERVISOR,
    ASESOR_VENTAS,
}

data class UserSession(
    val email: String,
    val role: UserRole,
)

sealed interface LoginResult {
    data class Success(val session: UserSession) : LoginResult
    data class Error(val message: String) : LoginResult
}
