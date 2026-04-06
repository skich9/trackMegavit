package com.example.trackmegavit.feature.auth.data

import com.example.trackmegavit.BuildConfig
import com.example.trackmegavit.core.network.SupabaseClientProvider
import com.example.trackmegavit.feature.auth.domain.LoginResult
import com.example.trackmegavit.feature.auth.domain.UserRole
import com.example.trackmegavit.feature.auth.domain.UserSession
import io.github.jan.supabase.postgrest.from

// ── Repositorio de autenticación ──────────────────────────────────────────────
object AuthRepository {

    private val client get() = SupabaseClientProvider.client

    suspend fun signIn(nickname: String, password: String): LoginResult {
        if (BuildConfig.SUPABASE_ANON_KEY.isBlank()) {
            return LoginResult.Error("Falta SUPABASE_ANON_KEY en local.properties.")
        }

        return try {
            // 1. Buscar usuario por nickname
            val usuario = client.from("usuarios")
                .select()
                .decodeList<UsuarioRow>()
                .firstOrNull { it.nickname.equals(nickname, ignoreCase = true) }
                ?: return LoginResult.Error("Usuario no encontrado: $nickname")

            // 2. Validar contraseña
            if (usuario.contrasenia != password) {
                return LoginResult.Error("Contraseña incorrecta.")
            }

            // 3. Buscar asignación de rol para este usuario
            val asignacion = client.from("asignacion_rol")
                .select {
                    filter {
                        eq("id_usuario", usuario.id)
                    }
                }
                .decodeList<AsignacionRolRow>()
                .firstOrNull()
                ?: return LoginResult.Error("El usuario no tiene un rol asignado en asignacion_rol.")

            // 4. Obtener el nombre del rol
            val rolRow = client.from("rol")
                .select {
                    filter {
                        eq("id_rol", asignacion.idRol)
                    }
                }
                .decodeList<RolRow>()
                .firstOrNull()
                ?: return LoginResult.Error("No se encontró el rol con id ${asignacion.idRol}.")

            // 5. Parsear el rol
            val role = parseRole(rolRow.rol)
                ?: return LoginResult.Error("Rol '${rolRow.rol}' no es reconocido por la aplicación.")

            LoginResult.Success(
                UserSession(
                    email = usuario.nickname,
                    role = role,
                )
            )
        } catch (e: Exception) {
            LoginResult.Error(
                "Error de autenticación: ${e.message ?: "Conexión con Supabase fallida"}"
            )
        }
    }

    suspend fun signOut(): Result<Unit> = runCatching { /* sesión local, no requiere llamada */ }

    suspend fun changePassword(newPassword: String): Result<Unit> = runCatching {
        throw Exception("Cambio de contraseña no disponible aún. Contacta al administrador.")
    }

    private fun parseRole(roleString: String?): UserRole? =
        when (roleString?.trim()?.lowercase()) {
            "admin", "administrador"                                    -> UserRole.ADMINISTRADOR
            "supervisor"                                                -> UserRole.SUPERVISOR
            "asesor_venta", "asesor_ventas", "asesor ventas", "asesor" -> UserRole.ASESOR_VENTAS
            else                                                        -> null
        }
}
