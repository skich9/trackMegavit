package com.example.trackmegavit

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Modelos de tablas ─────────────────────────────────────────────────────────

@Serializable
data class UsuarioRow(
    @SerialName("id_usuario") val id: Int = 0,
    val nickname: String = "",
    val nombre: String = "",
    @SerialName("apellido_paterno") val apellidoPaterno: String? = null,
    @SerialName("apellido_materno") val apellidoMaterno: String? = null,
    val ci: String? = null,
    val contrasenia: String = "",
    @SerialName("create_at") val createdAt: String? = null,
    @SerialName("update_at") val updatedAt: String? = null,
)

@Serializable
data class RolRow(
    @SerialName("id_rol") val id: Int = 0,
    val rol: String = "",
)

@Serializable
data class AsignacionRolRow(
    @SerialName("id_asignacion_rol") val id: Int = 0,
    @SerialName("id_usuario") val idUsuario: Int = 0,
    @SerialName("id_rol") val idRol: Int = 0,
)

// ── Servicio ──────────────────────────────────────────────────────────────────

object SupabaseAuthService {

    private val client by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY,
        ) {
            install(Auth)
            install(Postgrest)
        }
    }

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
                ?: return LoginResult.Error(
                    "Rol '${rolRow.rol}' no es reconocido por la aplicación."
                )

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
            "admin", "administrador" -> UserRole.ADMINISTRADOR
            "supervisor"            -> UserRole.SUPERVISOR
            "asesor_venta", "asesor_ventas", "asesor ventas", "asesor" -> UserRole.ASESOR_VENTAS
            else                    -> null
        }
}
