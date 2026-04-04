package com.example.trackmegavit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Home / Dashboard screen ───────────────────────────────────────────────────

@Composable
fun HomeScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item { HomeTopBar() }
        item { KpiSection() }
        item { QuickActionsCard() }
        item { VisitPerformanceChart() }
        item { RecentActivitySection() }
        item { MonthlyMilestoneCard() }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun HomeTopBar() {
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
            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = colors.onSurface)
            Column {
                Text(
                    text = "MDM Track Pro",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                    ),
                    color = colors.onSurface,
                )
                Text(
                    text = "LABORATORY",
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
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Active Session: 4h 12m",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                ),
                color = colors.onSurfaceVariant,
            )
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
}

// ── KPI cards ─────────────────────────────────────────────────────────────────

@Composable
private fun KpiSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        KpiCard(
            modifier = Modifier.weight(1f),
            label = "Visits Today",
            value = "8/12",
            sub = "4 remaining",
            icon = Icons.Default.GpsFixed,
        )
        KpiCard(
            modifier = Modifier.weight(1f),
            label = "Samples",
            value = "24",
            sub = "Avg 3.0/visit",
            icon = Icons.Default.Science,
        )
        KpiCard(
            modifier = Modifier.weight(1f),
            label = "Compliance",
            value = "96%",
            sub = "+2% last week",
            icon = Icons.Default.VerifiedUser,
        )
    }
}

@Composable
private fun KpiCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    sub: String,
    icon: ImageVector,
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        color = colors.surfaceContainerLow,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
            Text(
                text = sub,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// ── Quick actions ─────────────────────────────────────────────────────────────

@Composable
private fun QuickActionsCard() {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        color = colors.primaryContainer,
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            QuickActionButton(
                label = "Check-in",
                icon = Icons.Default.Login,
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
            )
            QuickActionButton(
                label = "New Visit",
                icon = Icons.Default.AddCircle,
                containerColor = Color.White,
                contentColor = colors.primary,
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
) {
    Button(
        onClick = {},
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(icon, contentDescription = null)
                Text(
                    label,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

// ── Weekly bar chart ──────────────────────────────────────────────────────────

@Composable
private fun VisitPerformanceChart() {
    val colors = MaterialTheme.colorScheme
    val days = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    val heights = listOf(0.60f, 0.85f, 0.95f, 0.40f, 0.70f, 0.20f, 0.15f)
    val active = listOf(true, true, true, true, true, false, false)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        color = colors.surfaceContainerLow,
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column {
                    Text(
                        "Visit Performance",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.onSurface,
                    )
                    Text(
                        "WEEKLY COMPLETION STATUS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 9.sp,
                        ),
                        color = colors.onSurfaceVariant,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .background(colors.primary.copy(alpha = 0.1f), RoundedCornerShape(50.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Box(Modifier.size(6.dp).background(colors.primary, CircleShape))
                    Text(
                        "COMPLETED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 0.5.sp,
                        ),
                        color = colors.primary,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Bars
            val maxBarHeight = 140.dp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
            ) {
                days.forEachIndexed { i, day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(
                            modifier = Modifier
                                .height(maxBarHeight)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.55f)
                                    .height(maxBarHeight * heights[i])
                                    .background(
                                        color = if (active[i]) colors.primary else colors.surfaceContainerHighest,
                                        shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp),
                                    ),
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            day,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                letterSpacing = 0.5.sp,
                            ),
                            color = colors.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

// ── Recent activity ───────────────────────────────────────────────────────────

@Composable
private fun RecentActivitySection() {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Recent Activity",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.onSurface,
            )
            TextButton(onClick = {}) {
                Text(
                    "VIEW ALL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 9.sp,
                    ),
                    color = colors.primary,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        RecentActivityItem("Dr. Sarah Miller",  "10:30 AM • 3 Samples", isCompleted = true)
        Spacer(Modifier.height(8.dp))
        RecentActivityItem("Riverside Clinic",  "09:15 AM • 7 Samples", isCompleted = true)
        Spacer(Modifier.height(8.dp))
        RecentActivityItem("Dr. James Wilson",  "Scheduled • 11:45 AM", isCompleted = false)
    }
}

@Composable
private fun RecentActivityItem(name: String, sub: String, isCompleted: Boolean) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.surfaceContainerLowest,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isCompleted) MdmColors.EmeraldLight else colors.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Pending,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    sub,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                    ),
                    color = colors.onSurfaceVariant,
                )
            }
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = colors.outlineVariant, modifier = Modifier.size(20.dp))
        }
    }
}

// ── Monthly milestone ─────────────────────────────────────────────────────────

@Composable
private fun MonthlyMilestoneCard() {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.primary)
            .padding(20.dp),
    ) {
        Column {
            Text(
                "UPCOMING MILESTONE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 9.sp,
                ),
                color = Color.White.copy(alpha = 0.75f),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Monthly Reward Eligibility",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
            Spacer(Modifier.height(14.dp))
            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.White.copy(alpha = 0.25f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.80f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White),
                )
            }
        }
    }
}
