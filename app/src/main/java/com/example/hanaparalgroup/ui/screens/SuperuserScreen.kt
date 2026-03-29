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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*
import com.example.hanaparalgroup.viewmodel.RemoteConfigViewModel

@Composable
fun SuperuserScreen(
    onNavigateBack: () -> Unit,
    viewModel: RemoteConfigViewModel = viewModel()
) {
    val groupCreationEnabled by viewModel.groupCreationEnabled.collectAsState()
    val maxMembersLimit by viewModel.maxMembersLimit.collectAsState()
    val announcementHeader by viewModel.announcementHeader.collectAsState()
    val maintenanceMode by viewModel.maintenanceMode.collectAsState()

    var isFetching by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            HanapAralTopBar(
                title = "Superuser Panel",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = {
                        isFetching = true
                        viewModel.fetchAndActivate()
                        // Simple delay to simulate network for UX, actual state updates via Flow
                        isFetching = false 
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Config", tint = White)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 40.dp)
            ) {
                item {
                    SuperuserBadge(modifier = Modifier.padding(20.dp))
                }

                item {
                    SectionCard(title = "Feature Controls", icon = Icons.Default.ToggleOn) {
                        ConfigToggleRow(
                            label = "Group Creation",
                            description = "Allow users to create new study groups",
                            checked = groupCreationEnabled,
                            onCheckedChange = { },
                            enabled = false
                        )
                        HorizontalDivider(color = Ink50, modifier = Modifier.padding(vertical = 4.dp))
                        ConfigToggleRow(
                            label = "Maintenance Mode",
                            description = "Show maintenance screen to regular users",
                            checked = maintenanceMode,
                            onCheckedChange = { },
                            enabled = false
                        )
                        if (maintenanceMode) {
                            Spacer(Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Danger.copy(alpha = 0.05f)),
                                border = BorderStroke(1.dp, Danger.copy(alpha = 0.2f))
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

                item {
                    SectionCard(title = "Group Limits", icon = Icons.Default.Group) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Max Members per Group", style = MaterialTheme.typography.titleSmall, color = Ink900, fontWeight = FontWeight.SemiBold)
                                Text("Managed via Firebase Console", style = MaterialTheme.typography.bodySmall, color = Ink400)
                            }
                            Surface(shape = RoundedCornerShape(12.dp), color = Ink100) {
                                Text(
                                    "$maxMembersLimit",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Ink900,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    SectionCard(title = "App Announcement Header", icon = Icons.Default.Campaign) {
                        Text("Preview of the dashboard banner:", style = MaterialTheme.typography.bodySmall, color = Ink400)
                        Spacer(Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = AccentSoft),
                            border = BorderStroke(1.dp, Accent.copy(alpha = 0.2f))
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, null, tint = Accent, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = announcementHeader.ifEmpty { "(No active announcement)" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (announcementHeader.isEmpty()) Ink400 else Ink900,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(16.dp))
                    PrimaryButton(
                        text = "Sync from Cloud",
                        onClick = { viewModel.fetchAndActivate() },
                        icon = Icons.Default.CloudDownload,
                        isLoading = isFetching,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Values are read-only here. Use the Firebase Console to make changes.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Ink300,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 40.dp)
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
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(52.dp).background(White.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AdminPanelSettings, null, tint = White, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Superuser Panel", style = MaterialTheme.typography.titleLarge, color = White, fontWeight = FontWeight.Bold)
                Text("Cloud Configuration Monitoring", style = MaterialTheme.typography.bodySmall, color = White.copy(alpha = 0.7f))
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
        modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Ink100)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(36.dp).background(Ink50, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = Ink900, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleLarge, color = Ink900, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Ink50)
            content()
        }
    }
}
