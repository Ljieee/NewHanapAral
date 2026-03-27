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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*

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
    onNavigateToGroupDetail: (String) -> Unit
) {
    val userName = "Alex Aropo"   // placeholder; replaced by Firestore data later
    val unreadCount = 3           // placeholder for FCM badge

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("My Groups", "Discover")

    Scaffold(
        topBar = {
            DashboardTopBar(
                userName = userName,
                unreadCount = unreadCount,
                onProfileClick = onNavigateToProfile,
                onNotifClick = onNavigateToNotifications,
                onSuperuserClick = onNavigateToSuperuser
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateGroup,
                containerColor = Action,
                contentColor = Surface,
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("New Group", style = MaterialTheme.typography.labelLarge)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = Background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
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
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            // ── Tab Selector ─────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Study Groups",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                    TextButton(onClick = onNavigateToGroups) {
                        Text("See All", color = Action, style = MaterialTheme.typography.labelLarge)
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Action, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Pill tabs
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .background(SurfaceAlt, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    tabs.forEachIndexed { idx, label ->
                        val isSelected = selectedTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Brand else Color.Transparent)
                                .clickable { selectedTab = idx }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) Surface else TextSecondary
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
                        maxMembers = group.maxMembers,
                        adminName = group.adminName,
                        isJoined = group.isJoined,
                        onClick = { onNavigateToGroupDetail(group.id) },
                        onJoinClick = { /* Balanag: join logic */ },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            }

            // ── Quick Notification preview ───────────────────────────────────
            item {
                Spacer(Modifier.height(24.dp))
                SectionHeader(
                    title = "Recent Activity",
                    action = "View All",
                    onActionClick = onNavigateToNotifications,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            val sampleNotifs = listOf(
                Triple("New Member", "Carlo joined Algorithm Avengers", "2m ago"),
                Triple("Study Reminder", "DB Detectives session at 3 PM today", "1h ago"),
                Triple("Announcement", "Web Wizards: New study material uploaded", "3h ago"),
            )

            items(sampleNotifs) { (title, body, time) ->
                val type = when {
                    title.contains("Member") -> NotificationType.NEW_MEMBER
                    title.contains("Reminder") -> NotificationType.REMINDER
                    else -> NotificationType.ANNOUNCEMENT
                }
                NotificationItem(
                    title = title,
                    body = body,
                    timeAgo = time,
                    type = type,
                    isRead = false,
                    onClick = { onNavigateToNotifications() },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                )
            }
        }
    }
}

// ── Dashboard Top Bar ─────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(
    userName: String,
    unreadCount: Int,
    onProfileClick: () -> Unit,
    onNotifClick: () -> Unit,
    onSuperuserClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.School,
                    contentDescription = null,
                    tint = ActionLight,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "HanapAral",
                    style = MaterialTheme.typography.titleLarge,
                    color = Surface,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            // Notification bell with badge
            Box {
                IconButton(onClick = onNotifClick) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Surface)
                }
                if (unreadCount > 0) {
                    Badge(
                        containerColor = Alert,
                        contentColor = Surface,
                        modifier = Modifier.offset(x = (-4).dp, y = 4.dp).align(Alignment.TopEnd)
                    ) {
                        Text(unreadCount.toString(), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            // Avatar / profile
            IconButton(onClick = onProfileClick) {
                AvatarInitials(
                    name = userName,
                    size = 34.dp,
                    backgroundColor = ActionLight
                )
            }
            // Superuser (admin hidden action via long press on logo or settings icon)
            IconButton(onClick = onSuperuserClick) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = "Superuser", tint = Surface.copy(alpha = 0.7f))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Brand)
    )
}

// ── Hero Banner ───────────────────────────────────────────────────────────────
@Composable
private fun HeroBanner(userName: String, onNavigateToGroups: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(BrandDark, Brand, GradientEnd)
                )
            )
    ) {
        // Decorative circles
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = Surface.copy(alpha = 0.07f),
                radius = 160f,
                center = Offset(size.width * 0.85f, size.height * 0.2f)
            )
            drawCircle(
                color = Action.copy(alpha = 0.1f),
                radius = 100f,
                center = Offset(size.width * 0.75f, size.height * 0.9f)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(24.dp)
        ) {
            Text(
                text = "Hello, ${userName.split(" ").first()} 👋",
                style = MaterialTheme.typography.headlineSmall,
                color = Surface.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Ready to\nstudy today?",
                style = MaterialTheme.typography.displaySmall,
                color = Surface,
                fontWeight = FontWeight.Black,
                lineHeight = 34.sp
            )
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onNavigateToGroups,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Surface.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Surface.copy(alpha = 0.15f),
                    contentColor = Surface
                ),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Find Groups", style = MaterialTheme.typography.labelLarge)
            }
        }

        // Decorative icon
        Icon(
            imageVector = Icons.Default.Groups,
            contentDescription = null,
            tint = Surface.copy(alpha = 0.08f),
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 20.dp)
        )
    }
}

// ── Stats Row ─────────────────────────────────────────────────────────────────
@Composable
private fun StatsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            label = "My Groups",
            value = "2",
            icon = Icons.Default.Groups,
            color = Brand,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Available",
            value = "12",
            icon = Icons.Default.Explore,
            color = Action,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Alerts",
            value = "3",
            icon = Icons.Default.Notifications,
            color = Alert,
            modifier = Modifier.weight(1f)
        )
    }
}