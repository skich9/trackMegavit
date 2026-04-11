package com.example.trackmegavit.feature.reports.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import org.osmdroid.views.overlay.Polyline
import java.text.SimpleDateFormat
import java.util.*

// ── Reports screen ────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(onUserClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme

    val advisors = remember {
        listOf(
            ReportAdvisor(
                name = "Ana Torres",
                routePoints = listOf(
                    GeoPoint(-12.0528, -77.0322),
                    GeoPoint(-12.0552, -77.0344),
                    GeoPoint(-12.0580, -77.0368),
                    GeoPoint(-12.0620, -77.0394),
                    GeoPoint(-12.0674, -77.0428),
                ),
            ),
            ReportAdvisor(
                name = "Luis Paredes",
                routePoints = listOf(
                    GeoPoint(-12.0674, -77.0428),
                    GeoPoint(-12.0698, -77.0444),
                    GeoPoint(-12.0720, -77.0460),
                    GeoPoint(-12.0748, -77.0476),
                    GeoPoint(-12.0800, -77.0490),
                ),
            ),
            ReportAdvisor(
                name = "Carla Mena",
                routePoints = listOf(
                    GeoPoint(-12.0871, -77.0504),
                    GeoPoint(-12.0890, -77.0520),
                    GeoPoint(-12.0910, -77.0504),
                    GeoPoint(-12.0930, -77.0474),
                    GeoPoint(-12.0950, -77.0452),
                ),
            ),
            ReportAdvisor(
                name = "Jorge Salas",
                routePoints = listOf(
                    GeoPoint(-12.0950, -77.0266),
                    GeoPoint(-12.0930, -77.0280),
                    GeoPoint(-12.0908, -77.0296),
                    GeoPoint(-12.0888, -77.0310),
                    GeoPoint(-12.0870, -77.0320),
                ),
            ),
        )
    }

    val advisorOptions = remember(advisors) { advisors.map { it.name } }
    var selectedAdvisor by remember { mutableStateOf(advisorOptions.first()) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
    )
    val selectedDateLabel = remember(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            SimpleDateFormat("dd MMM yyyy", Locale("es", "PE")).format(Date(it))
        } ?: "Seleccionar fecha"
    }

    var showDownloadMenu by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(bottom = 32.dp),
    ) {
        item { ReportsTopBar(onUserClick = onUserClick) }
        item {
            ReportAdvisorSelector(
                selectedAdvisor = selectedAdvisor,
                advisorOptions = advisorOptions,
                onAdvisorSelected = { selectedAdvisor = it },
            )
        }
        item {
            DatePickerButton(
                label = selectedDateLabel,
                onClick = { showDatePicker = true },
            )
        }
        item {
            val advisor = advisors.find { it.name == selectedAdvisor } ?: advisors.first()
            RouteMapSection(routePoints = advisor.routePoints)
        }
        item { SendReportSection() }
        item {
            DownloadReportSection(
                expanded = showDownloadMenu,
                onToggle = { showDownloadMenu = !showDownloadMenu },
                onDismiss = { showDownloadMenu = false },
            )
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private data class ReportAdvisor(
    val name: String,
    val routePoints: List<GeoPoint>,
)

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun ReportsTopBar(onUserClick: () -> Unit) {
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
            Icon(Icons.Default.Assessment, contentDescription = null, tint = colors.onSurface)
            Text(
                "Reportes",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                ),
                color = colors.onSurface,
            )
        }
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

// ── Advisor selector ──────────────────────────────────────────────────────────

@Composable
private fun ReportAdvisorSelector(
    selectedAdvisor: String,
    advisorOptions: List<String>,
    onAdvisorSelected: (String) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            "Asesor de ventas",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.onSurfaceVariant,
        )
        Spacer(Modifier.height(6.dp))
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onSurface),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        selectedAdvisor,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = colors.primary)
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                advisorOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = { onAdvisorSelected(option); expanded = false },
                    )
                }
            }
        }
    }
}

// ── Date picker button ────────────────────────────────────────────────────────

@Composable
private fun DatePickerButton(label: String, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Text(
            "Fecha del reporte",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.onSurfaceVariant,
        )
        Spacer(Modifier.height(6.dp))
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onSurface),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    )
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = colors.primary)
            }
        }
    }
}

// ── Route map ─────────────────────────────────────────────────────────────────

@Composable
private fun RouteMapSection(routePoints: List<GeoPoint>) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val centerPoint = remember(routePoints) {
        GeoPoint(
            routePoints.map { it.latitude }.average(),
            routePoints.map { it.longitude }.average(),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            "Recorrido del dia",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.onSurface,
        )
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .height(240.dp)
                .background(Color(0xFFE7ECE9)),
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
                    mapView.controller.setZoom(13.5)
                    mapView.controller.setCenter(centerPoint)
                    mapView.overlays.clear()

                    // Polyline route
                    val polyline = Polyline(mapView).apply {
                        setPoints(routePoints)
                        outlinePaint.color = android.graphics.Color.parseColor("#2C7A4D")
                        outlinePaint.strokeWidth = 10f
                        outlinePaint.isAntiAlias = true
                    }
                    mapView.overlays.add(polyline)

                    // Start marker
                    routePoints.firstOrNull()?.let { start ->
                        mapView.overlays.add(
                            Marker(mapView).apply {
                                position = start
                                title = "Inicio del recorrido"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            },
                        )
                    }

                    // End marker
                    routePoints.lastOrNull()?.let { end ->
                        mapView.overlays.add(
                            Marker(mapView).apply {
                                position = end
                                title = "Fin del recorrido"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            },
                        )
                    }

                    mapView.invalidate()
                },
            )

            // Live badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp)
                    .background(Color.White.copy(alpha = 0.88f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 7.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        Icons.Default.Timeline,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(15.dp),
                    )
                    Text(
                        "RECORRIDO DEL DIA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp,
                        ),
                        color = colors.primary,
                    )
                }
            }

            // Distance badge bottom-end
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(14.dp)
                    .background(Color.White.copy(alpha = 0.88f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 7.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        Icons.Default.DirectionsWalk,
                        contentDescription = null,
                        tint = colors.tertiary,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        "4.2 km recorridos",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                        ),
                        color = colors.onSurface,
                    )
                }
            }
        }
    }
}

// ── Send report ───────────────────────────────────────────────────────────────

@Composable
private fun SendReportSection() {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            "Enviar reporte del dia",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.onSurface,
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = {},
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = Color.White,
                ),
                contentPadding = PaddingValues(vertical = 14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(
                        "POR CORREO",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                        ),
                    )
                }
            }
            OutlinedButton(
                onClick = {},
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary),
                contentPadding = PaddingValues(vertical = 14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text(
                        "COMPARTIR",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                        ),
                    )
                }
            }
        }
    }
}

// ── Download report — experimental ───────────────────────────────────────────

private data class DownloadOption(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
)

@Composable
private fun DownloadReportSection(
    expanded: Boolean,
    onToggle: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    val downloadOptions = remember {
        listOf(
            DownloadOption(Icons.Default.Today,        "Reporte diario",   "Resumen de actividad del dia seleccionado"),
            DownloadOption(Icons.Default.DateRange,    "Reporte semanal",  "Resumen de la semana en curso"),
            DownloadOption(Icons.Default.CalendarMonth,"Reporte mensual",  "Resumen del mes completo"),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                "Descargar reporte",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.onSurface,
            )
            Surface(
                color = colors.tertiaryContainer,
                shape = RoundedCornerShape(50.dp),
            ) {
                Text(
                    "EXPERIMENTAL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 8.sp,
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = colors.onTertiaryContainer,
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            "Genera y descarga el reporte en formato PDF",
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
        )
        Spacer(Modifier.height(10.dp))

        Box {
            Button(
                onClick = onToggle,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.tertiary,
                    contentColor = Color.White,
                ),
                contentPadding = PaddingValues(vertical = 14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "DESCARGAR REPORTE",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                        ),
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(20.dp))
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismiss,
            ) {
                downloadOptions.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    option.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                )
                                Text(
                                    option.subtitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.onSurfaceVariant,
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(
                                option.icon,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(22.dp),
                            )
                        },
                        onClick = onDismiss,
                    )
                }
            }
        }
    }
}
