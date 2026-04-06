package com.example.trackmegavit.feature.admin.data

import com.example.trackmegavit.core.network.SupabaseClientProvider
import com.example.trackmegavit.feature.admin.domain.AsesorInfo
import com.example.trackmegavit.feature.admin.domain.LugarVisitado
import com.example.trackmegavit.feature.auth.data.AsignacionRolRow
import com.example.trackmegavit.feature.auth.data.RolRow
import com.example.trackmegavit.feature.auth.data.UsuarioRow
import io.github.jan.supabase.postgrest.from

// ── Repositorio de datos del panel de administración ─────────────────────────
object AdminRepository {

    private val client get() = SupabaseClientProvider.client

    // ── Asesores activos ──────────────────────────────────────────────────────

    suspend fun fetchAsesoresActivos(): List<AsesorInfo> {
        // 1. Obtener todos los roles y filtrar los de tipo asesor
        val roles = client.from("rol").select().decodeList<RolRow>()
        val asesorRolIds = roles
            .filter { isAsesorRol(it.rol) }
            .map { it.id }
            .toSet()

        // 2. Obtener asignaciones activas con rol de asesor
        val asignaciones = client.from("asignacion_rol")
            .select()
            .decodeList<AsignacionRolRow>()
            .filter { it.activo != false && it.idRol in asesorRolIds }

        if (asignaciones.isEmpty()) return emptyList()

        // 3. Obtener usuarios correspondientes
        val asesorUserIds = asignaciones.map { it.idUsuario }.toSet()
        return client.from("usuarios")
            .select()
            .decodeList<UsuarioRow>()
            .filter { it.id in asesorUserIds }
            .map { user ->
                AsesorInfo(
                    idUsuario = user.id,
                    nickname = user.nickname,
                    nombre = user.nombre,
                    apellidoPaterno = user.apellidoPaterno,
                    apellidoMaterno = user.apellidoMaterno,
                )
            }
    }

    // ── Lugares visitados hoy ─────────────────────────────────────────────────

    suspend fun fetchLugaresVisitadosHoy(): List<LugarVisitado> {
        val hoy = java.time.LocalDate.now().toString()
        return try {
            client.from("visitas")
                .select {
                    filter {
                        eq("fecha_visita", hoy)
                    }
                }
                .decodeList<VisitaRow>()
                .map { row ->
                    LugarVisitado(
                        id = row.id,
                        idUsuario = row.idUsuario,
                        nombreLugar = row.nombreLugar,
                        direccion = row.direccion,
                        fechaVisita = row.fechaVisita,
                        horaVisita = row.horaVisita,
                        resultado = row.resultado,
                    )
                }
        } catch (e: Exception) {
            // Si la tabla 'visitas' aún no existe en Supabase, retornar vacío
            if (isMissingTableError(e)) emptyList() else throw e
        }
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    private fun isAsesorRol(rol: String): Boolean =
        rol.trim().lowercase().contains("asesor")

    private fun isMissingTableError(error: Exception): Boolean {
        val msg = error.message.orEmpty()
        return "PGRST205" in msg || "Could not find the table" in msg
    }
}
