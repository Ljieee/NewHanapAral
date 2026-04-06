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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hanaparalgroup.data.repository.AppConfigRepository
import com.example.hanaparalgroup.data.repository.AppConfigRepository.AppConfig
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*
import com.example.hanaparalgroup.viewmodel.AppConfigViewModel
import kotlinx.coroutines.launch

@Composable
fun SuperuserScreen(onNavigateBack: () -> Unit) {
    // ── Source of truth: live Firestore config ─────────────────────────────────
    val configViewModel: AppConfigViewModel = viewModel()
    val liveConfig by configViewModel.config.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // ── Local editable state — seeded from Firestore once it loads ─────────────
    var groupCreationEnabled by remember { mutableStateOf(true) }
    var maxMembersOverride   by remember { mutableStateOf(15) }
    var announcementHeader   by remember { mutableStateOf("📢 Study Smart, Excel Together!") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var maintenanceMode      by remember { mutableStateOf(false) }
    var seeded               by remember { mutableStateOf(false) }

    // Seed local state from Firestore exactly once (when first real data arrives)
    LaunchedEffect(liveConfig) {
        if (!seeded) {
            groupCreationEnabled = liveConfig.groupCreationEnabled
            maxMembersOverride   = liveConfig.maxMembersPerGroup
            announcementHeader   = liveConfig.announcementHeader
            notificationsEnabled = liveConfig.notificationsEnabled
            maintenanceMode      = liveConfig.maintenanceMode
            seeded = true
        }
    }

    var isSaving by remember { mutableStateOf(false) }

    // ── Save to Firestore ──────────────────────────────────────────────────────
    fun saveConfig() {
        isSaving = true
        scope.launch {
            val result = AppConfigRepository.saveConfig(
                AppConfig(
                    groupCreationEnabled = groupCreationEnabled,
                    maxMembersPerGroup   = maxMembersOverride,
                    announcementHeader   = announcementHeader,
                    notificationsEnabled = notificationsEnabled,
                    maintenanceMode      = maintenanceMode
                )
            )
            isSaving = false
            if (result.isSuccess) {
                snackbarHostState.showSnackbar("✓ Settings saved — all users updated instantly!")
            } else {
                snackbarHostState.showSnackbar("✗ Failed to save. Check your connection.")
            }
        }
    }

    Scaffold(
        topBar = {
            HanapAralTopBar(
                title = "Superuser Panel",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = { saveConfig() }) {
                        Icon(Icons.Default.CloudUpload, contentDescription = "Save Config", tint = White)
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData   = data,
                    containerColor = if (data.visuals.message.startsWith("✓")) Positive else Danger,
                    contentColor   = White,
                    shape          = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = Ink50
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier       = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item { SuperuserBadge(modifier = Modifier.padding(20.dp)) }

                // ── Live config indicator ──────────────────────────────────────
                item {
                    LiveConfigStrip(
                        maintenanceMode = liveConfig.maintenanceMode,
                        modifier        = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }

                // ── Feature Toggles ────────────────────────────────────────────
                item {
                    SectionCard(title = "Feature Controls", icon = Icons.Default.ToggleOn) {
                        ConfigToggleRow(
                            label       = "Group Creation",
                            description = "Allow users to create new study groups",
                            checked     = groupCreationEnabled,
                            onCheckedChange = { groupCreationEnabled = it }
                        )
                        HorizontalDivider(color = Ink200, modifier = Modifier.padding(vertical = 4.dp))
                        ConfigToggleRow(
                            label       = "Push Notifications",
                            description = "Enable FCM notifications for all users",
                            checked     = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it }
                        )
                        HorizontalDivider(color = Ink200, modifier = Modifier.padding(vertical = 4.dp))
                        ConfigToggleRow(
                            label       = "Maintenance Mode",
                            description = "Show maintenance screen to regular users",
                            checked     = maintenanceMode,
                            onCheckedChange = { maintenanceMode = it }
                        )
                        if (maintenanceMode) {
                            Spacer(Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape    = RoundedCornerShape(12.dp),
                                colors   = CardDefaults.cardColors(containerColor = Danger.copy(alpha = 0.1f)),
                                border   = BorderStroke(1.dp, Danger.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, null, tint = Danger, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Save to put ALL regular users into maintenance mode!",
                                        style      = MaterialTheme.typography.bodySmall,
                                        color      = Danger,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Group Limits ───────────────────────────────────────────────
                item {
                    SectionCard(title = "Group Limits", icon = Icons.Default.Group) {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Max Members per Group",
                                    style      = MaterialTheme.typography.titleSmall,
                                    color      = Ink900
                                )
                                Text(
                                    "New groups will respect this limit",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Ink400
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Ink900
                            ) {
                                Text(
                                    "$maxMembersOverride",
                                    style      = MaterialTheme.typography.titleMedium,
                                    color      = White,
                                    fontWeight = FontWeight.Bold,
                                    modifier   = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Slider(
                            value         = maxMembersOverride.toFloat(),
                            onValueChange = { maxMembersOverride = it.toInt() },
                            valueRange    = 5f..50f,
                            steps         = 44,
                            colors        = SliderDefaults.colors(
                                thumbColor        = Ink900,
                                activeTrackColor  = Ink900,
                                inactiveTrackColor = Ink100
                            )
                        )
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("5 min", style = MaterialTheme.typography.labelSmall, color = Ink300)
                            Text("50 max", style = MaterialTheme.typography.labelSmall, color = Ink300)
                        }
                    }
                }

                // ── Announcement Header ────────────────────────────────────────
                item {
                    SectionCard(title = "App Announcement Header", icon = Icons.Default.Campaign) {
                        Text(
                            "This header appears at the top of the Dashboard for all users.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Ink400
                        )
                        Spacer(Modifier.height(12.dp))
                        BrandedTextField(
                            value         = announcementHeader,
                            onValueChange = { announcementHeader = it },
                            label         = "Announcement Header",
                            leadingIcon   = Icons.Default.Edit,
                            singleLine    = false,
                            maxLines      = 3
                        )
                        Spacer(Modifier.height(12.dp))
                        Text("Preview:", style = MaterialTheme.typography.labelSmall, color = Ink300)
                        Spacer(Modifier.height(6.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = CardDefaults.cardColors(
                                containerColor = Accent.copy(alpha = 0.1f)
                            ),
                            border = BorderStroke(1.dp, Accent.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text       = announcementHeader.ifEmpty { "(empty – banner hidden)" },
                                style      = MaterialTheme.typography.bodyMedium,
                                color      = if (announcementHeader.isEmpty()) Ink300 else Ink900,
                                fontWeight = FontWeight.SemiBold,
                                modifier   = Modifier.padding(14.dp)
                            )
                        }
                    }
                }

                // ── Info note ──────────────────────────────────────────────────
                item {
                    Card(
                        modifier  = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        shape     = RoundedCornerShape(14.dp),
                        colors    = CardDefaults.cardColors(containerColor = AccentSoft),
                        border    = BorderStroke(1.dp, Accent.copy(alpha = 0.2f)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, null, tint = Accent, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Changes are written to Firestore and take effect instantly for all users — no app restart needed.",
                                style = MaterialTheme.typography.labelSmall,
                                color = Accent
                            )
                        }
                    }
                }

                // ── Save / Reset ───────────────────────────────────────────────
                item {
                    Spacer(Modifier.height(8.dp))
                    PrimaryButton(
                        text      = "Save & Apply to All Users",
                        onClick   = { saveConfig() },
                        icon      = Icons.Default.CloudUpload,
                        isLoading = isSaving,
                        modifier  = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedSecondaryButton(
                        text    = "Reset to Defaults",
                        onClick = {
                            groupCreationEnabled = true
                            maxMembersOverride   = 15
                            announcementHeader   = "📢 Study Smart, Excel Together!"
                            notificationsEnabled = true
                            maintenanceMode      = false
                        },
                        icon     = Icons.Default.RestartAlt,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            if (isSaving) LoadingOverlay(message = "Saving settings…")
        }
    }
}

// ── Live config indicator strip ────────────────────────────────────────────────
@Composable
private fun LiveConfigStrip(maintenanceMode: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (maintenanceMode) DangerLight else PositiveLight
        ),
        border = BorderStroke(
            1.dp,
            if (maintenanceMode) Danger.copy(alpha = 0.4f) else Positive.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PulsingDot(color = if (maintenanceMode) Danger else Positive)
            Spacer(Modifier.width(8.dp))
            Text(
                text  = if (maintenanceMode) "LIVE · Maintenance mode ON" else "LIVE · App running normally",
                style = MaterialTheme.typography.labelMedium,
                color = if (maintenanceMode) Danger else Positive,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ── Private composables ────────────────────────────────────────────────────────
@Composable
private fun SuperuserBadge(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = Ink900)
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
                    style      = MaterialTheme.typography.titleLarge,
                    color      = White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Changes save to Firestore · Apply instantly to all users",
                    style = MaterialTheme.typography.bodySmall,
                    color = White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title    : String,
    icon     : androidx.compose.ui.graphics.vector.ImageVector,
    modifier : Modifier = Modifier,
    content  : @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
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
                Text(
                    title,
                    style      = MaterialTheme.typography.titleLarge,
                    color      = Ink900,
                    fontWeight = FontWeight.Bold
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Ink200)
            content()
        }
    }
}