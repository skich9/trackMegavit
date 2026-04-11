package com.example.trackmegavit

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import android.util.Log
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class AdvisorRoute(
    val name: String,
    val points: List<GeoPoint>,
)

private data class DownloadOption(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreenAndroid(onUserClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme

    val advisors = remember {
        listOf(
            AdvisorRoute("Ana Torres", listOf(
                GeoPoint(-12.0528, -77.0322), GeoPoint(-12.0550, -77.0343), GeoPoint(-12.0598, -77.0380), GeoPoint(-12.0674, -77.0428)
            )),
            AdvisorRoute("Luis Paredes", listOf(
                GeoPoint(-12.0674, -77.0428), GeoPoint(-12.0710, -77.0455), GeoPoint(-12.0748, -77.0476), GeoPoint(-12.0800, -77.0490)
            )),
            AdvisorRoute("Carla Mena", listOf(
                GeoPoint(-12.0871, -77.0504), GeoPoint(-12.0905, -77.0490), GeoPoint(-12.0930, -77.0474), GeoPoint(-12.0950, -77.0452)
            )),
            AdvisorRoute("Jorge Salas", listOf(
                GeoPoint(-12.0950, -77.0266), GeoPoint(-12.0918, -77.0288), GeoPoint(-12.0894, -77.0307), GeoPoint(-12.0870, -77.0320)
            )),
        )
    }

    val advisorNames = remember(advisors) { advisors.map { it.name } }
    var selectedAdvisor by remember { mutableStateOf(advisorNames.first()) }

    var advisorExpanded by remember { mutableStateOf(false) }
    var dateDialogOpen by remember { mutableStateOf(false) }
    var downloadExpanded by remember { mutableStateOf(false) }

    val dateState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    val dateLabel = remember(dateState.selectedDateMillis) {
        dateState.selectedDateMillis?.let {
            SimpleDateFormat("dd MMM yyyy", Locale("es", "PE")).format(Date(it))
        } ?: "Seleccionar fecha"
    }

    val selectedRoute = advisors.firstOrNull { it.name == selectedAdvisor }?.points.orEmpty()

    Log.d("TrackMegavit", "ReportsScreenAndroid LOADED")
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface.copy(alpha = 0.95f))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.Assessment, contentDescription = null, tint = colors.onSurface)
                    Text(
                        "Reportes",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
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
                    Icon(Icons.Default.Person, contentDescription = null, tint = colors.primary, modifier = Modifier.size(20.dp))
                }
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text("Asesor de ventas", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Box {
                    OutlinedButton(
                        onClick = { advisorExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(selectedAdvisor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                    DropdownMenu(expanded = advisorExpanded, onDismissRequest = { advisorExpanded = false }) {
                        advisorNames.forEach { name ->
                            DropdownMenuItem(text = { Text(name) }, onClick = {
                                selectedAdvisor = name
                                advisorExpanded = false
                            })
                        }
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text("Fecha del reporte", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = colors.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                OutlinedButton(onClick = { dateDialogOpen = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                            Text(dateLabel)
                        }
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
            }
        }

        item {
            ReportsRouteMap(points = selectedRoute)
        }

        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text("Enviar reporte del dia", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = colors.onSurface)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(50.dp), contentPadding = PaddingValues(vertical = 14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("POR CORREO", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                    OutlinedButton(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(50.dp), contentPadding = PaddingValues(vertical = 14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("COMPARTIR", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        item {
            val options = listOf(
                DownloadOption(Icons.Default.Today, "Reporte diario", "Resumen del dia seleccionado"),
                DownloadOption(Icons.Default.DateRange, "Reporte semanal", "Resumen de la semana en curso"),
                DownloadOption(Icons.Default.CalendarMonth, "Reporte mensual", "Resumen del mes completo"),
            )

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Descargar reporte", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = colors.onSurface)
                    Surface(color = colors.tertiaryContainer, shape = RoundedCornerShape(50.dp)) {
                        Text("EXPERIMENTAL", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp), color = colors.onTertiaryContainer)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Box {
                    Button(
                        onClick = { downloadExpanded = !downloadExpanded },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.tertiary, contentColor = Color.White),
                        contentPadding = PaddingValues(vertical = 14.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.FileDownload, contentDescription = null)
                            Spacer(Modifier.size(8.dp))
                            Text("DESCARGAR REPORTE", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                            Spacer(Modifier.size(8.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(expanded = downloadExpanded, onDismissRequest = { downloadExpanded = false }) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                leadingIcon = { Icon(option.icon, contentDescription = null) },
                                text = {
                                    Column {
                                        Text(option.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                        Text(option.subtitle, style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
                                    }
                                },
                                onClick = { downloadExpanded = false },
                            )
                        }
                    }
                }
            }
        }
    }

    if (dateDialogOpen) {
        DatePickerDialog(
            onDismissRequest = { dateDialogOpen = false },
            confirmButton = { TextButton(onClick = { dateDialogOpen = false }) { Text("Confirmar") } },
            dismissButton = { TextButton(onClick = { dateDialogOpen = false }) { Text("Cancelar") } },
        ) {
            DatePicker(state = dateState)
        }
    }
}

@Composable
private fun ReportsRouteMap(points: List<GeoPoint>) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val mapPoints = if (points.isEmpty()) listOf(GeoPoint(-12.0674, -77.0428), GeoPoint(-12.0748, -77.0476)) else points
    val center = remember(mapPoints) {
        GeoPoint(mapPoints.map { it.latitude }.average(), mapPoints.map { it.longitude }.average())
    }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Recorrido del dia", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = colors.onSurface)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFE7ECE9)),
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
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
                    mapView.controller.setZoom(13.2)
                    mapView.controller.setCenter(center)
                    mapView.overlays.clear()

                    val polyline = Polyline(mapView).apply {
                        setPoints(mapPoints)
                        outlinePaint.color = android.graphics.Color.parseColor("#2C7A4D")
                        outlinePaint.strokeWidth = 9f
                    }
                    mapView.overlays.add(polyline)

                    mapPoints.firstOrNull()?.let { start ->
                        mapView.overlays.add(Marker(mapView).apply {
                            position = start
                            title = "Inicio"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        })
                    }
                    mapPoints.lastOrNull()?.let { end ->
                        mapView.overlays.add(Marker(mapView).apply {
                            position = end
                            title = "Fin"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        })
                    }
                    mapView.invalidate()
                },
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp)
                    .background(Color.White.copy(alpha = 0.88f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Timeline, contentDescription = null, tint = colors.primary, modifier = Modifier.size(14.dp))
                    Text("RECORRIDO DEL DIA", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp), color = colors.primary)
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(14.dp)
                    .background(Color.White.copy(alpha = 0.88f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = colors.tertiary, modifier = Modifier.size(14.dp))
                    Text("4.2 km", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = colors.onSurface)
                }
            }
        }
    }
}
