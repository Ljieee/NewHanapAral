package com.example.hanaparalgroup.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*
import com.example.hanaparalgroup.viewmodel.RemoteConfigViewModel

// ── Placeholder data models (UI only) ────────────────────────────────────────
data class GroupPreview(
    val id: String,
    val name: String,
    val subject: String,
    val memberCount: Int,
    val maxMembers: Int,
    val adminName: String,
    val isJoined: Boolean
)

private val sampleGroups = listOf(
    GroupPreview("1", "Algorithm Avengers", "Data Structures & Algorithms", 8, 15, "Juan dela Cruz", true),
    GroupPreview("2", "Web Wizards", "Web Development", 12, 15, "Maria Santos", false),
    GroupPreview("3", "DB Detectives", "Database Management", 5, 10, "Carlos Reyes", true),
    GroupPreview("4", "OOP Olympians", "Object-Oriented Programming", 14, 15, "Ana Gomez", false),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToGroups: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSuperuser: () -> Unit,
    onNavigateToCreateGroup: () -> Unit,
    onNavigateToGroupDetail: (String) -> Unit,
    viewModel: RemoteConfigViewModel = viewModel()
) {
    val userName = "Alex Aropo"
    val unreadCount = 3

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("My Groups", "Discover")

    // Remote Config & Auth values from ViewModel
    val groupCreationEnabled by viewModel.groupCreationEnabled.collectAsState()
    val announcementHeader by viewModel.announcementHeader.collectAsState()
    val maintenanceMode by viewModel.maintenanceMode.collectAsState()
    val isSuperuser by viewModel.isSuperuser.collectAsState()

    if (maintenanceMode) {
        MaintenanceScreen()
        return
    }

    Scaffold(
        topBar = {
            DashboardTopBar(
                userName = userName,
                unreadCount = unreadCount,
                isSuperuser = isSuperuser, // Pass the role state
                onProfileClick = onNavigateToProfile,
                onNotifClick = onNavigateToNotifications,
                onSuperuserClick = onNavigateToSuperuser
            )
        },
        floatingActionButton = {
            // Only show if Admin has enabled this feature in Remote Config
            if (groupCreationEnabled) {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToCreateGroup,
                    containerColor = Ink900,
                    contentColor = White,
                    shape = RoundedCornerShape(14.dp),
                    icon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("New Group", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold) }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = Ink50
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ── Remote Config Announcement ───────────────────────────────────
            if (announcementHeader.isNotEmpty()) {
                item {
                    AnnouncementBanner(text = announcementHeader)
                }
            }

            // ── Hero Banner ──────────────────────────────────────────────────
            item {
                HeroBanner(
                    userName = userName,
                    onNavigateToGroups = onNavigateToGroups
                )
            }

            // ── Stats Row ────────────────────────────────────────────────────
            item {
                StatsRow(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            // ── Tab Selector ─────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Study Groups",
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink900,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onNavigateToGroups) {
                        Text("See all", color = Ink400, style = MaterialTheme.typography.labelSmall)
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Ink400, modifier = Modifier.size(14.dp))
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Pill tabs
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .background(Ink100, RoundedCornerShape(10.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    tabs.forEachIndexed { idx, label ->
                        val isSelected = selectedTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) White else Color.Transparent)
                                .clickable { selectedTab = idx }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Ink900 else Ink400,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // ── Groups List ──────────────────────────────────────────────────
            val displayed = if (selectedTab == 0)
                sampleGroups.filter { it.isJoined }
            else
                sampleGroups.filter { !it.isJoined }

            if (displayed.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            icon = Icons.Outlined.Groups,
                            title = if (selectedTab == 0) "No Groups Yet" else "No Groups to Discover",
                            subtitle = if (selectedTab == 0)
                                "You haven't joined any groups. Explore and join one!"
                            else
                                "All available groups are already full. Check back later!",
                            action = if (selectedTab == 0) "Browse Groups" else null,
                            onAction = if (selectedTab == 0) ({ selectedTab = 1 }) else null
                        )
                    }
                }
            } else {
                items(displayed, key = { it.id }) { group ->
                    StudyGroupCard(
                        groupName = group.name,
                        subject = group.subject,
                        memberCount = group.memberCount,
                        maxMembers = viewModel.maxMembersLimit.collectAsState().value, 
                        adminName = group.adminName,
                        isJoined = group.isJoined,
                        onClick = { onNavigateToGroupDetail(group.id) },
                        onJoinClick = { /* join logic */ },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }

            // ── Recent Activity ──────────────────────────────────────────────
            item {
                Spacer(Modifier.height(20.dp))
                SectionHeader(
                    title = "Recent Activity",
                    action = "View All",
                    onActionClick = onNavigateToNotifications,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(10.dp))
            }

            val sampleNotifs = listOf(
                Triple("New Member", "Carlo joined Algorithm Avengers", "2m ago"),
                Triple("Study Reminder", "DB Detectives session at 3 PM today", "1h ago"),
                Triple("Announcement", "Web Wizards: New study material uploaded", "3h ago"),
            )

            items(sampleNotifs) { (title, body, time) ->
                val type = when {
                    title.contains("Member")   -> NotificationType.NEW_MEMBER
                    title.contains("Reminder") -> NotificationType.REMINDER
                    else                       -> NotificationType.ANNOUNCEMENT
                }
                NotificationItem(
                    title = title,
                    body = body,
                    timeAgo = time,
                    type = type,
                    isRead = false,
                    onClick = { onNavigateToNotifications() },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AnnouncementBanner(text: String) {
    Surface(
        color = AccentSoft,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Campaign, null, tint = Accent, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = Ink900,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun MaintenanceScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(Ink50),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Ink100, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Build, null, tint = Ink900, modifier = Modifier.size(36.dp))
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "Under Maintenance",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Ink900
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "HanapAral is currently undergoing scheduled maintenance to improve your experience. We'll be back shortly!",
                style = MaterialTheme.typography.bodyMedium,
                color = Ink400,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

// ── Dashboard Top Bar ─────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(
    userName: String,
    unreadCount: Int,
    isSuperuser: Boolean,
    onProfileClick: () -> Unit,
    onNotifClick: () -> Unit,
    onSuperuserClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(White, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = null,
                        tint = Ink900,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    "HanapAral",
                    style = MaterialTheme.typography.titleLarge,
                    color = White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
            }
        },
        actions = {
            // Notification bell with badge
            Box {
                IconButton(onClick = onNotifClick) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = White)
                }
                if (unreadCount > 0) {
                    Badge(
                        containerColor = Danger,
                        contentColor = White,
                        modifier = Modifier
                            .offset(x = (-4).dp, y = 4.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(unreadCount.toString(), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            IconButton(onClick = onProfileClick) {
                AvatarInitials(
                    name = userName,
                    size = 32.dp,
                    backgroundColor = White.copy(alpha = 0.15f),
                    textColor = White
                )
            }
            // ── Superuser UI Logic: Only show Admin icon if the user is a superuser ──
            if (isSuperuser) {
                IconButton(onClick = onSuperuserClick) {
                    Icon(
                        Icons.Default.AdminPanelSettings, 
                        contentDescription = "Superuser", 
                        tint = White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Ink900)
    )
}

// ── Hero Banner ───────────────────────────────────────────────────────────────
@Composable
private fun HeroBanner(userName: String, onNavigateToGroups: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Ink900)
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Column {
            Text(
                text = "Hello, ${userName.split(" ").first()} 👋",
                style = MaterialTheme.typography.bodyMedium,
                color = White.copy(alpha = 0.55f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Ready to\nstudy today?",
                style = MaterialTheme.typography.headlineLarge,
                color = White,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 32.sp,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(20.dp))
            OutlinedButton(
                onClick = onNavigateToGroups,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, White.copy(alpha = 0.25f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = White.copy(alpha = 0.1f),
                    contentColor = White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(6.dp))
                Text("Find Groups", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
            }
        }

        Icon(
            imageVector = Icons.Default.Groups,
            contentDescription = null,
            tint = White.copy(alpha = 0.06f),
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 12.dp)
        )
    }
}

// ── Stats Row ─────────────────────────────────────────────────────────────────
@Composable
private fun StatsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            label = "My Groups",
            value = "2",
            icon = Icons.Default.Groups,
            color = Ink900,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Available",
            value = "12",
            icon = Icons.Default.Explore,
            color = Ink900,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Alerts",
            value = "3",
            icon = Icons.Default.Notifications,
            color = Ink900,
            modifier = Modifier.weight(1f)
        )
    }
}
