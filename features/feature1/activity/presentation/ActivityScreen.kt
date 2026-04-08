package com.example.trackmegavit.feature.activity.presentation

import com.example.trackmegavit.core.ui.MdmColors
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// â”€â”€ Activity Tracking screen â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Composable
fun ActivityScreen(onUserClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    var selectedOutcome by remember { mutableStateOf("VISITADO") }
    var visitType      by remember { mutableStateOf("Muestras de producto") }
    var observations   by remember { mutableStateOf("") }
    val advisors = remember {
        listOf(
            SalesAdvisorLocation("Ana Torres", -12.0528, -77.0322, "Lima Centro"),
            SalesAdvisorLocation("Luis Paredes", -12.0674, -77.0428, "Lince"),
            SalesAdvisorLocation("Carla Mena", -12.0871, -77.0504, "Miraflores"),
            SalesAdvisorLocation("Jorge Salas", -12.0950, -77.0266, "San Isidro"),
        )
    }
    val advisorOptions = remember(advisors) { listOf("Todos los asesores") + advisors.map { it.name } }
    var selectedAdvisor by remember { mutableStateOf(advisorOptions.first()) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background),
            contentPadding = PaddingValues(bottom = 96.dp),
        ) {
            item { ActivityTopBar(onUserClick = onUserClick) }
            item {
                AdvisorSelector(
                    selectedAdvisor = selectedAdvisor,
                    advisorOptions = advisorOptions,
                    onAdvisorSelected = { selectedAdvisor = it },
                )
            }
            item {
                MapSection(
                    advisors = advisors,
                    selectedAdvisor = selectedAdvisor,
                )
            }
            item { StatsRow() }
            item { DailyItinerarySection() }
            item {
                LogActivityForm(
                    selectedOutcome  = selectedOutcome,
                    onOutcomeSelected = { selectedOutcome = it },
                    visitType        = visitType,
                    onVisitTypeChanged = { visitType = it },
                    observations     = observations,
                    onObservationsChanged = { observations = it },
                )
            }
        }

        // FAB
        FloatingActionButton(
            onClick = {},
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            containerColor = colors.tertiary,
            contentColor   = Color.White,
            shape          = CircleShape,
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Registrar actividad")
        }
    }
}

@Composable
private fun AdvisorSelector(
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

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                advisorOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onAdvisorSelected(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

// â”€â”€ Top app bar â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Composable
private fun ActivityTopBar(onUserClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface.copy(alpha = 0.95f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Default.Menu, contentDescription = "Menu principal", tint = colors.onSurface)
            Text(
                "Seguimiento de actividad",
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

// â”€â”€ Mapa de asesores â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Composable
private fun MapSection(
    advisors: List<SalesAdvisorLocation>,
    selectedAdvisor: String,
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val filteredAdvisors = remember(advisors, selectedAdvisor) {
        if (selectedAdvisor == "Todos los asesores") {
            advisors
        } else {
            advisors.filter { it.name == selectedAdvisor }
        }
    }
    val mapAdvisors = if (filteredAdvisors.isEmpty()) advisors else filteredAdvisors
    val centerPoint = remember(mapAdvisors) {
        GeoPoint(
            mapAdvisors.map { it.latitude }.average(),
            mapAdvisors.map { it.longitude }.average(),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .height(220.dp)
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
                mapView.controller.setZoom(12.8)
                mapView.controller.setCenter(centerPoint)

                mapView.overlays.removeAll { overlay -> overlay is Marker }
                mapAdvisors.forEach { advisor ->
                    mapView.overlays.add(
                        Marker(mapView).apply {
                            position = GeoPoint(advisor.latitude, advisor.longitude)
                            title = advisor.name
                            subDescription = "Ubicacion actual: ${advisor.zone}"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                    )
                }
                mapView.invalidate()
            },
        )

        // Insignia de rastreo activo
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(14.dp)
                .background(Color.White.copy(alpha = 0.88f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 7.dp),
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    Icons.Default.MyLocation,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(15.dp),
                )
                Text(
                    "RASTREO EN VIVO ACTIVO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp,
                    ),
                    color = colors.primary,
                )
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 18.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(50.dp),
            color = colors.surface.copy(alpha = 0.92f),
            tonalElevation = 4.dp,
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    "${mapAdvisors.size} asesores en campo",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = colors.onSurface,
                )
                Icon(Icons.Default.Groups, contentDescription = null, tint = colors.primary)
            }
        }
    }
}

private data class SalesAdvisorLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val zone: String,
)

// â”€â”€ Stats row â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Composable
private fun StatsRow() {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        StatCard(modifier = Modifier.weight(1f), label = "PROGRAMADAS", value = "08", valueColor = colors.primary)
        StatCard(modifier = Modifier.weight(1f), label = "COMPLETADAS", value = "03", valueColor = colors.tertiary)

        // Tarjeta de distancia con acento lateral
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surfaceContainerLow)
                .height(IntrinsicSize.Min),
        ) {
            Row {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(colors.primary),
                )
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "DISTANCIA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 9.sp,
                        ),
                        color = colors.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "4.2",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.primary,
                        )
                        Text(
                            " km",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.primary,
                            modifier = Modifier.padding(bottom = 5.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier, label: String, value: String, valueColor: Color) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        color = colors.surfaceContainerLow,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 9.sp,
                ),
                color = colors.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = valueColor,
            )
        }
    }
}

// â”€â”€ Daily itinerary â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Composable
private fun DailyItinerarySection() {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        // Section header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(
                "Itinerario diario",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.onSurface,
            )
            Surface(
                color = Color.White.copy(alpha = 0.6f),
                shape = RoundedCornerShape(50.dp),
            ) {
                Text(
                    "Hoy",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = colors.onSurface,
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // Active visit
        ActiveVisitCard()
        Spacer(Modifier.height(8.dp))

        // Scheduled
        ScheduledVisitItem(
            name     = "Dr. Marcus Thorne",
            location = "Clinica St. Mary â€¢ 14:30",
            icon     = Icons.Default.MedicalServices,
        )
        Spacer(Modifier.height(8.dp))
        ScheduledVisitItem(
            name     = "Central Pharma Labs",
            location = "Oficina de compras â€¢ 16:00",
            icon     = Icons.Default.LocalPharmacy,
        )
        Spacer(Modifier.height(8.dp))

        // Completed
        CompletedVisitItem(name = "Dra. Sarah Jenkins", sub = "Completada â€¢ 09:15 a. m.")
        Spacer(Modifier.height(12.dp))

        // Dashed CTA
        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            shape  = RoundedCornerShape(50.dp),
            border = BorderStroke(2.dp, colors.outlineVariant),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onSurfaceVariant),
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier              = Modifier.padding(vertical = 4.dp),
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = null)
                Text(
                    "PROGRAMAR NUEVA VISITA",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun ActiveVisitCard() {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color    = colors.surfaceContainerLowest,
        shape    = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Gold left border
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(colors.tertiary),
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.Top,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Dr. Elena Rodriguez",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = colors.primary,
                        )
                        Text(
                            "Hospital General â€¢ Oncologia",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = colors.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        color = MdmColors.TertiaryFixed,
                        shape = RoundedCornerShape(50.dp),
                    ) {
                        Text(
                            "SIGUIENTE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 9.sp,
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MdmColors.OnTertiaryFixed,
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    VisitActionButton(
                        modifier       = Modifier.weight(1f),
                        label          = "NAVEGAR",
                        icon           = Icons.Default.Directions,
                        containerColor = colors.primaryContainer,
                        contentColor   = Color.White,
                    )
                    VisitActionButton(
                        modifier       = Modifier.weight(1f),
                        label          = "LLEGADA",
                        icon           = Icons.Default.Login,
                        containerColor = colors.primary,
                        contentColor   = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
private fun VisitActionButton(
    modifier: Modifier,
    label: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
) {
    Button(
        onClick = {},
        modifier = modifier,
        shape  = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize   = 10.sp,
                ),
            )
        }
    }
}

@Composable
private fun ScheduledVisitItem(name: String, location: String, icon: ImageVector) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color    = colors.surfaceContainerHigh.copy(alpha = 0.5f),
        shape    = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceContainerHighest),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = colors.onSurfaceVariant, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    location,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = colors.onSurfaceVariant,
                )
            }
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = colors.outline, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun CompletedVisitItem(name: String, sub: String) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth().alpha(0.70f),
        color    = colors.surfaceContainerLowest.copy(alpha = 0.5f),
        shape    = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MdmColors.EmeraldLight),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.LineThrough,
                    ),
                    color = colors.onSurface,
                )
                Text(
                    sub,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = colors.onSurfaceVariant,
                )
            }
        }
    }
}

// â”€â”€ Log Activity form â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

@Composable
private fun LogActivityForm(
    selectedOutcome: String,
    onOutcomeSelected: (String) -> Unit,
    visitType: String,
    onVisitTypeChanged: (String) -> Unit,
    observations: String,
    onObservationsChanged: (String) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        color  = colors.surfaceContainerLow,
        shape  = RoundedCornerShape(24.dp),
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Registrar actividad",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = colors.primary,
                    )
                    Text(
                        "Registrando resultado de visita para la ubicacion actual",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.Assignment, contentDescription = null, tint = colors.primary)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Outcome status
            FormLabel("ESTADO DE RESULTADO")
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("VISITADO", "OCUPADO", "AUSENTE").forEach { outcome ->
                    val isSelected = selectedOutcome == outcome
                    Button(
                        onClick = { onOutcomeSelected(outcome) },
                        modifier = Modifier.weight(1f),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) colors.primary else colors.surfaceContainerHighest,
                            contentColor   = if (isSelected) Color.White else colors.onSurfaceVariant,
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp),
                    ) {
                        Text(outcome, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Visit type (simple custom dropdown)
            FormLabel("TIPO DE VISITA")
            Spacer(Modifier.height(8.dp))
            VisitTypeDropdown(visitType, onVisitTypeChanged)

            Spacer(Modifier.height(16.dp))

            // Observations
            FormLabel("OBSERVACIONES Y NOTAS")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value       = observations,
                onValueChange = onObservationsChanged,
                placeholder = { Text("Ingresa detalles de la visita...", color = colors.onSurfaceVariant) },
                modifier    = Modifier.fillMaxWidth(),
                shape       = RoundedCornerShape(16.dp),
                minLines    = 3,
                colors      = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = Color.Transparent,
                    unfocusedBorderColor    = Color.Transparent,
                    focusedContainerColor   = colors.surfaceContainerHighest,
                    unfocusedContainerColor = colors.surfaceContainerHighest,
                ),
            )

            Spacer(Modifier.height(16.dp))

            // Evidence / Photos
            FormLabel("EVIDENCIA / FOTOS")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(BorderStroke(2.dp, colors.outlineVariant), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Adjuntar foto",
                            tint = colors.outline,
                            modifier = Modifier.size(24.dp),
                        )
                        Text(
                            "ADJUNTAR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize   = 9.sp,
                            ),
                            color = colors.outline,
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.surfaceContainerHigh),
                )
            }

            Spacer(Modifier.height(24.dp))

            // Submit
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(50.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor   = Color.White,
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
            ) {
                Text(
                    "ENVIAR REPORTE DE VISITA",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontSize = 10.sp,
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun VisitTypeDropdown(current: String, onSelected: (String) -> Unit) {
    val colors  = MaterialTheme.colorScheme
    val options = listOf("Muestras de producto", "Toma de pedido", "Introduccion inicial")
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surfaceContainerHighest)
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(current, style = MaterialTheme.typography.bodyMedium, color = colors.onSurface)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = colors.onSurfaceVariant)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text    = { Text(opt) },
                    onClick = { onSelected(opt); expanded = false },
                )
            }
        }
    }
}
