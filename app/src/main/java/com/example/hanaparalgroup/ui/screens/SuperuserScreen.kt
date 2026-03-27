package com.example.hanaparalgroup.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*

@Composable
fun SuperuserScreen(onNavigateBack: () -> Unit) {
    // Remote Config values (Cativo: replace with actual Firebase Remote Config bindings)
    var groupCreationEnabled by remember { mutableStateOf(true) }
    var maxMembersOverride by remember { mutableStateOf(15) }
    var announcementHeader by remember { mutableStateOf("📢 Study Smart, Excel Together!") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var maintenanceMode by remember { mutableStateOf(false) }

    var isSaving by remember { mutableStateOf(false) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            HanapAralTopBar(
                title = "Superuser Panel",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = {
                        isSaving = true
                        // Cativo: push changes to Firebase Remote Config
                        isSaving = false
                        showSuccessSnackbar = true
                    }) {
                        Icon(Icons.Default.CloudUpload, contentDescription = "Push Config", tint = ActionLight)
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Success,
                    contentColor = Surface,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = Background
    ) { padding ->
        LaunchedEffect(showSuccessSnackbar) {
            if (showSuccessSnackbar) {
                snackbarHostState.showSnackbar("✓ Config pushed successfully!")
                showSuccessSnackbar = false
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // ── Admin badge ──────────────────────────────────────────────
                item {
                    SuperuserBadge(modifier = Modifier.padding(20.dp))
                }

                // ── Feature Toggles ──────────────────────────────────────────
                item {
                    SectionCard(title = "Feature Controls", icon = Icons.Default.ToggleOn) {
                        ConfigToggleRow(
                            label = "Group Creation",
                            description = "Allow users to create new study groups",
                            checked = groupCreationEnabled,
                            onCheckedChange = { groupCreationEnabled = it }
                        )
                        HorizontalDivider(color = Divider, modifier = Modifier.padding(vertical = 4.dp))
                        ConfigToggleRow(
                            label = "Push Notifications",
                            description = "Enable FCM notifications for all users",
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it }
                        )
                        HorizontalDivider(color = Divider, modifier = Modifier.padding(vertical = 4.dp))
                        ConfigToggleRow(
                            label = "Maintenance Mode",
                            description = "Show maintenance screen to regular users",
                            checked = maintenanceMode,
                            onCheckedChange = { maintenanceMode = it }
                        )
                        if (maintenanceMode) {
                            Spacer(Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Alert.copy(alpha = 0.1f)),
                                border = BorderStroke(1.dp, Alert.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, null, tint = Alert, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "All regular users will see a maintenance screen!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Alert,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Member Limits ────────────────────────────────────────────
                item {
                    SectionCard(title = "Group Limits", icon = Icons.Default.Group) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Max Members per Group", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text("Applied app-wide without update", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Brand.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    "$maxMembersOverride",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Brand,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))

                        // Preset buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(10, 15, 20, 30).forEach { value ->
                                FilterChip(
                                    selected = maxMembersOverride == value,
                                    onClick = { maxMembersOverride = value },
                                    label = { Text("$value", style = MaterialTheme.typography.labelLarge) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Brand,
                                        selectedLabelColor = Surface,
                                        containerColor = SurfaceAlt,
                                        labelColor = TextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = maxMembersOverride == value,
                                        selectedBorderColor = Brand,
                                        borderColor = Divider
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Slider(
                            value = maxMembersOverride.toFloat(),
                            onValueChange = { maxMembersOverride = it.toInt() },
                            valueRange = 5f..50f,
                            steps = 44,
                            colors = SliderDefaults.colors(
                                thumbColor = Brand,
                                activeTrackColor = Brand,
                                inactiveTrackColor = SurfaceAlt
                            )
                        )
                    }
                }

                // ── Global Announcement Header ───────────────────────────────
                item {
                    SectionCard(title = "App Announcement Header", icon = Icons.Default.Campaign) {
                        Text(
                            "This header appears at the top of the Dashboard for all users.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(12.dp))
                        BrandedTextField(
                            value = announcementHeader,
                            onValueChange = { announcementHeader = it },
                            label = "Announcement Header",
                            leadingIcon = Icons.Default.Edit,
                            singleLine = false,
                            maxLines = 3
                        )
                        Spacer(Modifier.height(12.dp))
                        // Preview
                        Text("Preview:", style = MaterialTheme.typography.labelSmall, color = TextHint)
                        Spacer(Modifier.height(6.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Action.copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, Action.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = announcementHeader.ifEmpty { "(empty – banner hidden)" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (announcementHeader.isEmpty()) TextHint else TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }

                // ── Push Config Button ───────────────────────────────────────
                item {
                    Spacer(Modifier.height(8.dp))
                    PrimaryButton(
                        text = "Push Configuration",
                        onClick = {
                            isSaving = true
                            // Cativo: Firebase Remote Config update
                            isSaving = false
                            showSuccessSnackbar = true
                        },
                        icon = Icons.Default.CloudUpload,
                        isLoading = isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    ActionButton(
                        text = "Reset to Defaults",
                        onClick = {
                            groupCreationEnabled = true
                            maxMembersOverride = 15
                            announcementHeader = "📢 Study Smart, Excel Together!"
                            notificationsEnabled = true
                            maintenanceMode = false
                        },
                        icon = Icons.Default.RestartAlt,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SuperuserBadge(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Brand
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(Surface.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AdminPanelSettings, null, tint = Surface, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    "Superuser Panel",
                    style = MaterialTheme.typography.titleLarge,
                    color = Surface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Changes apply instantly · No app restart needed",
                    style = MaterialTheme.typography.bodySmall,
                    color = Surface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Brand.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = Brand, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Divider)
            content()
        }
    }
}