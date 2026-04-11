package com.example.trackmegavit.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class VisitStatus { PENDIENTE, VISITADO }

private data class PendingVisit(
    val lugar: String,
    val direccion: String,
    val horaProgramada: String,
    val estado: VisitStatus,
)

@Composable
fun SalesAdvisorHomeScreen(onUserClick: () -> Unit) {
    val visits = remember {
        listOf(
            PendingVisit("Clinica San Felipe", "Av. Salaverry 1234", "09:00", VisitStatus.PENDIENTE),
            PendingVisit("Hospital Central", "Jr. Bolognesi 890", "10:30", VisitStatus.VISITADO),
            PendingVisit("Botica Miraflores", "Calle Alcanfores 455", "12:00", VisitStatus.PENDIENTE),
            PendingVisit("Policlinico Norte", "Av. Tomas Valle 211", "15:15", VisitStatus.PENDIENTE),
            PendingVisit("Clinica Los Olivos", "Av. Universitaria 3300", "17:00", VisitStatus.VISITADO),
        )
    }

    val total = visits.size
    val visited = visits.count { it.estado == VisitStatus.VISITADO }
    val pending = total - visited

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item { SalesTopBar(onUserClick = onUserClick) }
        item {
            SalesSummaryCard(
                total = total,
                pending = pending,
                visited = visited,
            )
        }
        item {
            Text(
                text = "Visitas pendientes de hoy",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        items(visits) { visit ->
            PendingVisitCard(
                visit = visit,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
    }
}

@Composable
private fun SalesTopBar(onUserClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface.copy(alpha = 0.95f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Default.Menu, contentDescription = "Menu principal", tint = colors.onSurface)
            Column {
                Text(
                    text = "Ruta del dia",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = colors.onSurface,
                )
                Text(
                    text = "ASESOR DE VENTAS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                    ),
                    color = colors.primary,
                )
            }
        }

        IconButton(
            onClick = onUserClick,
            modifier = Modifier
                .size(36.dp)
                .background(colors.primaryContainer, CircleShape),
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Cuenta de usuario",
                tint = colors.primary,
            )
        }
    }
}

@Composable
private fun SalesSummaryCard(total: Int, pending: Int, visited: Int) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLow),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            MetricPill(
                title = "Total",
                value = total.toString(),
                icon = Icons.Default.Place,
                modifier = Modifier.weight(1f),
            )
            MetricPill(
                title = "Pendientes",
                value = pending.toString(),
                icon = Icons.Default.Schedule,
                modifier = Modifier.weight(1f),
            )
            MetricPill(
                title = "Visitadas",
                value = visited.toString(),
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MetricPill(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = colors.primary.copy(alpha = 0.08f),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(icon, contentDescription = null, tint = colors.primary)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(title, style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun PendingVisitCard(visit: PendingVisit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val statusColor = if (visit.estado == VisitStatus.VISITADO) Color(0xFF2E7D32) else Color(0xFFB26A00)
    val statusText = if (visit.estado == VisitStatus.VISITADO) "Visitado" else "Pendiente"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceContainerLowest),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = visit.lugar,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.onSurface,
                )
                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(50.dp),
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor,
                    )
                }
            }

            Spacer(Modifier.height(6.dp))
            Text(
                text = visit.direccion,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = colors.primary,
                )
                Text(
                    text = "Hora programada: ${visit.horaProgramada}",
                    modifier = Modifier.padding(start = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onSurface,
                )
            }
        }
    }
}
