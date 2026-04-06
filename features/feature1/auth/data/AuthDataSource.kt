package com.example.trackmegavit.feature.auth.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Modelos de tablas de Supabase para autenticación ─────────────────────────

@Serializable
data class UsuarioRow(
    @SerialName("id_usuario") val id: Int = 0,
    val nickname: String = "",
    val nombre: String = "",
    @SerialName("apellido_paterno") val apellidoPaterno: String? = null,
    @SerialName("apellido_materno") val apellidoMaterno: String? = null,
    val ci: String? = null,
    val contrasenia: String = "",
    val activo: Boolean? = null,
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
    val activo: Boolean? = null,
)
