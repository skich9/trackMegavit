package com.example.trackmegavit.feature.admin.presentation
import com.example.trackmegavit.feature.auth.domain.UserRole
import com.example.trackmegavit.feature.auth.domain.UserSession
import com.example.trackmegavit.feature.admin.domain.AsesorInfo
import com.example.trackmegavit.feature.admin.domain.LugarVisitado
import com.example.trackmegavit.feature.admin.data.AdminRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// â”€â”€ Pantalla de inicio para Admin y Supervisor â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Composable
fun AdminHomeScreen(
    session: UserSession,
    onUserClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    var asesores by remember { mutableStateOf<List<AsesorInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }
    var lugares by remember { mutableStateOf<List<LugarVisitado>>(emptyList()) }
    var isLoadingLugares by remember { mutableStateOf(true) }
    var errorLugares by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            asesores = AdminRepository.fetchAsesoresActivos()
        } catch (e: Exception) {
            errorMsg = "Error al cargar asesores: ${e.message}"
        }
        isLoading = false
        try {
            lugares = AdminRepository.fetchLugaresVisitadosHoy()
        } catch (e: Exception) {
            errorLugares = "Error al cargar lugares: ${e.message}"
        }
        isLoadingLugares = false
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item { AdminTopBar(session = session, onUserClick = onUserClick) }
        item { AdminStatsRow(count = asesores.size, isLoading = isLoading) }
        item { AsesorSectionHeader(count = if (isLoading) 0 else asesores.size) }

        when {
            isLoading -> item { AdminLoadingCard() }
            errorMsg.isNotEmpty() -> item { AdminErrorCard(message = errorMsg) }
            asesores.isEmpty() -> item { AdminEmptyState() }
            else -> items(asesores, key = { it.idUsuario }) { asesor ->
                AsesorCard(
                    asesor = asesor,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }
        }

        item { LugaresSectionHeader(count = if (isLoadingLugares) 0 else lugares.size) }
        item {
            LugaresVisitadosMap(
                lugares = lugares,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }

        when {
            isLoadingLugares -> item { LugaresLoadingCard() }
            errorLugares.isNotEmpty() -> item { AdminErrorCard(message = errorLugares) }
            lugares.isEmpty() -> item { LugaresEmptyState() }
            else -> item {
                LugaresVisitadosCard(
                    lugares = lugares,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun LugaresVisitadosMap(
    lugares: List<LugarVisitado>,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val fallbackPoints = remember {
        listOf(
            LugarMapPoint("Botica San Miguel", -12.0713, -77.0847),
            LugarMapPoint("Clinica Stella Maris", -12.0751, -77.0534),
            LugarMapPoint("Hospital Central", -12.0608, -77.0442),
            LugarMapPoint("Farmacia Miraflores", -12.1206, -77.0297),
        )
    }
    val mapPoints = remember(lugares, fallbackPoints) {
        if (lugares.isEmpty()) {
            fallbackPoints
        } else {
            val coords = listOf(
                GeoPoint(-12.0713, -77.0847),
                GeoPoint(-12.0751, -77.0534),
                GeoPoint(-12.0608, -77.0442),
                GeoPoint(-12.1206, -77.0297),
            )
            lugares.take(coords.size).mapIndexed { index, lugar ->
                LugarMapPoint(lugar.nombreLugar, coords[index].latitude, coords[index].longitude)
            }
        }
    }
    val centerPoint = remember(mapPoints) {
        GeoPoint(
            mapPoints.map { it.latitude }.average(),
            mapPoints.map { it.longitude }.average(),
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFDCE9E0)),
    ) {
        AndroidView(
            modifier = Modifier.matchParentSize(),
            factory = {
                Configuration.getInstance().userAgentValue = context.packageName
                MapView(it).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    minZoomLevel = 10.0
                    maxZoomLevel = 19.0
                }
            },
            update = { mapView ->
                mapView.controller.setZoom(12.3)
                mapView.controller.setCenter(centerPoint)

                mapView.overlays.removeAll { overlay -> overlay is Marker }
                mapPoints.forEach { point ->
                    mapView.overlays.add(
                        Marker(mapView).apply {
                            position = GeoPoint(point.latitude, point.longitude)
                            title = point.name
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                    )
                }
                mapView.invalidate()
            },
        )

        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color.White.copy(alpha = 0.9f),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(Icons.Default.Place, contentDescription = null, tint = colors.tertiary, modifier = Modifier.size(15.dp))
                Text(
                    "LUGARES DE HOY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp,
                    ),
                    color = colors.tertiary,
                )
            }
        }
    }
}

private data class LugarMapPoint(
    val name: String,
    val latitude: Double,
    val longitude: Double,
)

// â”€â”€ Barra superior â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Composable
private fun AdminTopBar(session: UserSession, onUserClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val roleLabel = when (session.role) {
        UserRole.ADMINISTRADOR -> "ADMINISTRADOR"
        UserRole.SUPERVISOR    -> "SUPERVISOR"
        else                   -> ""
    }
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
                    text = "Panel de Control",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                    ),
                    color = colors.onSurface,
                )
                Text(
                    text = roleLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontSize = 10.sp,
                    ),
                    color = colors.primary,
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = session.email,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 90.dp),
            )
            IconButton(
                onClick = onUserClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colors.primaryContainer),
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Cuenta de usuario",
                    tint = colors.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

// â”€â”€ Tarjetas de estadÃ­sticas â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Composable
private fun AdminStatsRow(count: Int, isLoading: Boolean) {
    val displayCount = if (isLoading) "â€”" else "$count"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        AdminStatCard(
            modifier = Modifier.weight(1f),
            label = "Asesores activos",
            value = displayCount,
            icon = Icons.Default.Group,
        )
        AdminStatCard(
            modifier = Modifier.weight(1f),
            label = "En campo hoy",
            value = displayCount,
            icon = Icons.Default.GpsFixed,
        )
    }
}

@Composable
private fun AdminStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        color = colors.surfaceContainerLow,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = label.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        fontSize = 9.sp,
                    ),
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    icon,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = colors.onSurface,
            )
        }
    }
}

// â”€â”€ Encabezado de secciÃ³n â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Composable
private fun AsesorSectionHeader(count: Int) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Asesores de Ventas Activos",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.onSurface,
        )
        Surface(
            color = colors.primaryContainer,
            shape = RoundedCornerShape(50.dp),
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                color = colors.primary,
            )
        }
    }
}

// â”€â”€ Tarjeta de asesor â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Composable
private fun AsesorCard(asesor: AsesorInfo, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val iniciales = buildString {
        asesor.nombre.firstOrNull()?.uppercaseChar()?.let { append(it) }
        asesor.apellidoPaterno?.firstOrNull()?.uppercaseChar()?.let { append(it) }
    }.ifEmpty { "?" }
    val nombreCompleto = listOfNotNull(
        asesor.nombre.ifBlank { null },
        asesor.apellidoPaterno,
        asesor.apellidoMaterno,
    ).joinToString(" ")

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.surfaceContainerLowest,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Avatar con iniciales
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(colors.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = iniciales,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.primary,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombreCompleto,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "@${asesor.nickname}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = colors.onSurfaceVariant,
                )
            }

            // Indicador de estado activo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E7D32)),
                )
                Text(
                    text = "Activo",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                    ),
                    color = Color(0xFF2E7D32),
                )
            }
        }
    }
}

// â”€â”€ Estados auxiliares â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Composable
private fun AdminLoadingCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Text(
                "Cargando asesores...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AdminErrorCard(message: String) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        color = colors.errorContainer,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = colors.error)
            Text(message, style = MaterialTheme.typography.bodySmall, color = colors.error)
        }
    }
}

@Composable
private fun AdminEmptyState() {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                Icons.Default.Group,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = colors.outlineVariant,
            )
            Text(
                "No hay asesores activos",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = colors.onSurfaceVariant,
            )
            Text(
                "Asigna asesores de ventas en la plataforma",
                style = MaterialTheme.typography.bodySmall,
                color = colors.outline,
            )
        }
    }
}

// -- Encabezado de seccion de lugares ----------------------------------------------------------

@Composable
private fun LugaresSectionHeader(count: Int) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                Icons.Default.Place,
                contentDescription = null,
                tint = colors.tertiary,
                modifier = Modifier.size(20.dp),
            )
            Text(
                "Lugares Visitados Hoy",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.onSurface,
            )
        }
        Surface(
            color = colors.tertiaryContainer,
            shape = RoundedCornerShape(50.dp),
        ) {
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                color = colors.tertiary,
            )
        }
    }
}

// -- Tarjeta de lugares visitados ---------------------------------------------------------------

@Composable
private fun LugaresVisitadosCard(lugares: List<LugarVisitado>, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = colors.surfaceContainerLowest,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            lugares.forEachIndexed { index, lugar ->
                LugarVisitadoItem(lugar = lugar)
                if (index < lugares.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = colors.outlineVariant.copy(alpha = 0.4f),
                    )
                }
            }
        }
    }
}

@Composable
private fun LugarVisitadoItem(lugar: LugarVisitado) {
    val colors = MaterialTheme.colorScheme
    val resultadoColor = when (lugar.resultado.uppercase()) {
        "VISITADO" -> Color(0xFF2E7D32)
        "OCUPADO"  -> colors.tertiary
        "AUSENTE"  -> colors.error
        else       -> colors.onSurfaceVariant
    }
    val resultadoIcon = when (lugar.resultado.uppercase()) {
        "VISITADO" -> Icons.Default.CheckCircle
        "OCUPADO"  -> Icons.Default.AccessTime
        "AUSENTE"  -> Icons.Default.Cancel
        else       -> Icons.Default.Circle
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(resultadoColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                resultadoIcon,
                contentDescription = null,
                tint = resultadoColor,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lugar.nombreLugar,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!lugar.direccion.isNullOrBlank()) {
                Text(
                    text = lugar.direccion,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Surface(
                color = resultadoColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(50.dp),
            ) {
                Text(
                    text = lugar.resultado,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = resultadoColor,
                )
            }
            if (!lugar.horaVisita.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = lugar.horaVisita,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = colors.onSurfaceVariant,
                )
            }
        }
    }
}

// -- Estados auxiliares para lugares ------------------------------------------------------------

@Composable
private fun LugaresLoadingCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
            )
            Text(
                "Cargando lugares...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LugaresEmptyState() {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        color = colors.surfaceContainerLowest,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Icon(
                Icons.Default.LocationOff,
                contentDescription = null,
                tint = colors.outlineVariant,
                modifier = Modifier.size(32.dp),
            )
            Column {
                Text(
                    "Sin visitas registradas",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = colors.onSurfaceVariant,
                )
                Text(
                    "No hay lugares visitados hoy",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.outline,
                )
            }
        }
    }
}
