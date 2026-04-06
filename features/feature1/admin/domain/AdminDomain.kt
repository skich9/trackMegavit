package com.example.trackmegavit.feature.admin.domain

// ── Modelos de dominio del panel de administración ───────────────────────────

data class AsesorInfo(
    val idUsuario: Int,
    val nickname: String,
    val nombre: String,
    val apellidoPaterno: String?,
    val apellidoMaterno: String?,
)

data class LugarVisitado(
    val id: Int,
    val idUsuario: Int,
    val nombreLugar: String,
    val direccion: String?,
    val fechaVisita: String,
    val horaVisita: String?,
    val resultado: String,
)
