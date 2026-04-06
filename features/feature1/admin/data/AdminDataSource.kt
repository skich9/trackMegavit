package com.example.trackmegavit.feature.admin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Modelo de tabla de Supabase para visitas ──────────────────────────────────

@Serializable
data class VisitaRow(
    @SerialName("id_visita") val id: Int = 0,
    @SerialName("id_usuario") val idUsuario: Int = 0,
    @SerialName("nombre_lugar") val nombreLugar: String = "",
    val direccion: String? = null,
    @SerialName("fecha_visita") val fechaVisita: String = "",
    @SerialName("hora_visita") val horaVisita: String? = null,
    val resultado: String = "",
)
