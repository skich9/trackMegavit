package com.example.trackmegavit

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Activity Tracking screen ──────────────────────────────────────────────────

@Composable
fun ActivityScreen() {
    val colors = MaterialTheme.colorScheme
    var selectedOutcome by remember { mutableStateOf("VISITED") }
    var visitType      by remember { mutableStateOf("Product Samples") }
    var observations   by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background),
            contentPadding = PaddingValues(bottom = 96.dp),
        ) {
            item { ActivityTopBar() }
            item { MapSection() }
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
            Icon(Icons.Default.Edit, contentDescription = "Log")
        }
    }
}

// ── Top app bar ───────────────────────────────────────────────────────────────

@Composable
private fun ActivityTopBar() {
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
            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = colors.onSurface)
            Text(
                "Activity Tracking",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                ),
                color = colors.onSurface,
            )
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(colors.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = "Profile",
                tint = colors.primary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

// ── Map placeholder ───────────────────────────────────────────────────────────

@Composable
private fun MapSection() {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .height(220.dp)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF2A5C38),
                        Color(0xFF3B6B45),
                        Color(0xFF4D8059),
                        Color(0xFF2A5C38),
                    )
                )
            ),
    ) {
        // "LIVE TRACKER ACTIVE" badge
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
                    "LIVE TRACKER ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp,
                    ),
                    color = colors.primary,
                )
            }
        }

        // CTA button
        Button(
            onClick = {},
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 18.dp)
                .fillMaxWidth(),
            shape  = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor   = Color.White,
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp),
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier              = Modifier.padding(vertical = 4.dp),
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Text(
                    "START FIELD JOURNEY",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                    ),
                )
            }
        }
    }
}

// ── Stats row ─────────────────────────────────────────────────────────────────

@Composable
private fun StatsRow() {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        StatCard(modifier = Modifier.weight(1f), label = "SCHEDULED", value = "08", valueColor = colors.primary)
        StatCard(modifier = Modifier.weight(1f), label = "COMPLETED", value = "03", valueColor = colors.tertiary)

        // Distance card – left accent border
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
                        "DISTANCE",
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

// ── Daily itinerary ───────────────────────────────────────────────────────────

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
                "Daily Itinerary",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.onSurface,
            )
            Surface(
                color = Color.White.copy(alpha = 0.6f),
                shape = RoundedCornerShape(50.dp),
            ) {
                Text(
                    "Today",
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
            location = "St. Mary Clinic • 14:30",
            icon     = Icons.Default.MedicalServices,
        )
        Spacer(Modifier.height(8.dp))
        ScheduledVisitItem(
            name     = "Central Pharma Labs",
            location = "Procurement Office • 16:00",
            icon     = Icons.Default.LocalPharmacy,
        )
        Spacer(Modifier.height(8.dp))

        // Completed
        CompletedVisitItem(name = "Dr. Sarah Jenkins", sub = "Completed • 09:15 AM")
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
                    "SCHEDULE NEW VISIT",
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
                            "General Hospital • Oncology",
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
                            "NEXT UP",
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
                        label          = "NAVIGATE",
                        icon           = Icons.Default.Directions,
                        containerColor = colors.primaryContainer,
                        contentColor   = Color.White,
                    )
                    VisitActionButton(
                        modifier       = Modifier.weight(1f),
                        label          = "CHECK-IN",
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

// ── Log Activity form ─────────────────────────────────────────────────────────

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
                        "Log Activity",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                        color = colors.primary,
                    )
                    Text(
                        "Recording visit outcome for current location",
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
            FormLabel("OUTCOME STATUS")
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("VISITED", "BUSY", "OUT").forEach { outcome ->
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
            FormLabel("VISIT TYPE")
            Spacer(Modifier.height(8.dp))
            VisitTypeDropdown(visitType, onVisitTypeChanged)

            Spacer(Modifier.height(16.dp))

            // Observations
            FormLabel("OBSERVATIONS & NOTES")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value       = observations,
                onValueChange = onObservationsChanged,
                placeholder = { Text("Enter visit details...", color = colors.onSurfaceVariant) },
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
            FormLabel("EVIDENCE / PHOTOS")
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
                            contentDescription = "Attach photo",
                            tint = colors.outline,
                            modifier = Modifier.size(24.dp),
                        )
                        Text(
                            "ATTACH",
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
                    "SUBMIT VISIT REPORT",
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
    val options = listOf("Product Samples", "Order Collection", "Initial Intro")
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
