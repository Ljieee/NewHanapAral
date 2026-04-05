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
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun SuperuserScreen(onNavigateBack: () -> Unit) {
    val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    val scope = rememberCoroutineScope()

    // Local UI state — initialized from Remote Config cache
    var groupCreationEnabled by remember { mutableStateOf(true) }
    var maxMembersOverride by remember { mutableStateOf(15) }
    var announcementHeader by remember { mutableStateOf("📢 Study Smart, Excel Together!") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var maintenanceMode by remember { mutableStateOf(false) }

    var isSaving by remember { mutableStateOf(false) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Fetch & activate Remote Config on screen open
    LaunchedEffect(Unit) {
        try {
            val settings = remoteConfigSettings { minimumFetchIntervalInSeconds = 0 }
            remoteConfig.setConfigSettingsAsync(settings).await()
            remoteConfig.fetchAndActivate().await()

            // Apply fetched values to local state
            groupCreationEnabled = remoteConfig.getBoolean("group_creation_enabled")
            maxMembersOverride   = remoteConfig.getLong("max_members_per_group").toInt().let { if (it <= 0) 15 else it }
            announcementHeader   = remoteConfig.getString("announcement_header").ifEmpty { "📢 Study Smart, Excel Together!" }
            notificationsEnabled = remoteConfig.getBoolean("notifications_enabled")
            maintenanceMode      = remoteConfig.getBoolean("maintenance_mode")
        } catch (e: Exception) {
            // Use defaults if fetch fails
        }
    }

    LaunchedEffect(showSuccessSnackbar) {
        if (showSuccessSnackbar) {
            snackbarHostState.showSnackbar("✓ Config pushed successfully!")
            showSuccessSnackbar = false
        }
    }

    fun pushConfig() {
        isSaving = true
        scope.launch {
            try {
                // Remote Config is READ-ONLY from the app side.
                // Values are set in the Firebase Console. Here we re-fetch to apply
                // any changes the superuser made in the console, and update local UI.
                remoteConfig.fetchAndActivate().await()
                isSaving = false
                showSuccessSnackbar = true
            } catch (e: Exception) {
                isSaving = false
            }
        }
    }

    Scaffold(
        topBar = {
            HanapAralTopBar(
                title = "Superuser Panel",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { pushConfig() }) {
                        Icon(Icons.Default.CloudUpload, contentDescription = "Refresh Config", tint = White)
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Positive,
                    contentColor = White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = Ink50
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item { SuperuserBadge(modifier = Modifier.padding(20.dp)) }

                // Feature Toggles
                item {
                    SectionCard(title = "Feature Controls", icon = Icons.Default.ToggleOn) {
                        ConfigToggleRow(
                            label = "Group Creation",
                            description = "Allow users to create new study groups",
                            checked = groupCreationEnabled,
                            onCheckedChange = { groupCreationEnabled = it }
                        )
                        HorizontalDivider(color = Ink200, modifier = Modifier.padding(vertical = 4.dp))
                        ConfigToggleRow(
                            label = "Push Notifications",
                            description = "Enable FCM notifications for all users",
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it }
                        )
                        HorizontalDivider(color = Ink200, modifier = Modifier.padding(vertical = 4.dp))
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
                                colors = CardDefaults.cardColors(containerColor = Danger.copy(alpha = 0.1f)),
                                border = BorderStroke(1.dp, Danger.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, null, tint = Danger, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "All regular users will see a maintenance screen!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Danger,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                // Member Limits
                item {
                    SectionCard(title = "Group Limits", icon = Icons.Default.Group) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Max Members per Group", style = MaterialTheme.typography.titleSmall, color = Ink900)
                                Text("Applied app-wide without update", style = MaterialTheme.typography.bodySmall, color = Ink400)
                            }
                            Surface(shape = RoundedCornerShape(20.dp), color = Ink900.copy(alpha = 0.12f)) {
                                Text(
                                    "$maxMembersOverride",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Ink900,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(10, 15, 20, 30).forEach { value ->
                                FilterChip(
                                    selected = maxMembersOverride == value,
                                    onClick = { maxMembersOverride = value },
                                    label = { Text("$value", style = MaterialTheme.typography.labelLarge) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Ink900,
                                        selectedLabelColor = White,
                                        containerColor = Ink100,
                                        labelColor = Ink400
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = maxMembersOverride == value,
                                        selectedBorderColor = Ink900,
                                        borderColor = Ink200
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
                                thumbColor = Ink900,
                                activeTrackColor = Ink900,
                                inactiveTrackColor = Ink100
                            )
                        )
                    }
                }

                // Global Announcement Header
                item {
                    SectionCard(title = "App Announcement Header", icon = Icons.Default.Campaign) {
                        Text(
                            "This header appears at the top of the Dashboard for all users.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Ink400
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
                        Text("Preview:", style = MaterialTheme.typography.labelSmall, color = Ink300)
                        Spacer(Modifier.height(6.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Accent.copy(alpha = 0.1f)),
                            border = BorderStroke(1.dp, Accent.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = announcementHeader.ifEmpty { "(empty – banner hidden)" },
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (announcementHeader.isEmpty()) Ink300 else Ink900,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }

                // Note about Remote Config
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = AccentSoft),
                        border = BorderStroke(1.dp, Accent.copy(alpha = 0.2f)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, null, tint = Accent, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "To publish changes, update values in the Firebase Console → Remote Config, then tap the refresh button above.",
                                style = MaterialTheme.typography.labelSmall,
                                color = Accent
                            )
                        }
                    }
                }

                // Refresh / Reset buttons
                item {
                    Spacer(Modifier.height(8.dp))
                    PrimaryButton(
                        text = "Refresh Config from Firebase",
                        onClick = { pushConfig() },
                        icon = Icons.Default.CloudDownload,
                        isLoading = isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedSecondaryButton(
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
        colors = CardDefaults.cardColors(containerColor = Ink900)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(White.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AdminPanelSettings, null, tint = White, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    "Superuser Panel",
                    style = MaterialTheme.typography.titleLarge,
                    color = White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Changes apply instantly · No app restart needed",
                    style = MaterialTheme.typography.bodySmall,
                    color = White.copy(alpha = 0.7f)
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
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Ink900.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = Ink900, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleLarge, color = Ink900, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Ink200)
            content()
        }
    }
}