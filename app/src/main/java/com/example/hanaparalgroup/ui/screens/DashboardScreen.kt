package com.example.hanaparalgroup.ui.screens

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
import androidx.compose.ui.unit.*
import com.example.hanaparalgroup.data.models.StudyGroup
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// ── Local notification model (for Recent Activity only) ───────────────────────
private data class RecentNotif(
    val id: String,
    val title: String,
    val body: String,
    val timeAgo: String,
    val type: NotificationType
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
    val currentUid = Firebase.auth.currentUser?.uid ?: ""
    val scope = rememberCoroutineScope()

    // ── Live state ─────────────────────────────────────────────────────────────
    var userName       by remember { mutableStateOf("") }
    var allGroups      by remember { mutableStateOf<List<StudyGroup>>(emptyList()) }
    var recentNotifs   by remember { mutableStateOf<List<RecentNotif>>(emptyList()) }
    var unreadCount    by remember { mutableStateOf(0) }
    var joiningIds     by remember { mutableStateOf<Set<String>>(emptySet()) }
    var selectedTab    by remember { mutableIntStateOf(0) }
    val tabs = listOf("My Groups", "Discover")

    // ── Load user name from Firestore ──────────────────────────────────────────
    DisposableEffect(currentUid) {
        val userListener = Firebase.firestore.collection("users").document(currentUid)
            .addSnapshotListener { snap, _ ->
                userName = snap?.getString("name") ?: Firebase.auth.currentUser?.displayName ?: ""
            }
        onDispose { userListener.remove() }
    }

    // ── Real-time groups listener ──────────────────────────────────────────────
    DisposableEffect(Unit) {
        val groupsListener = Firebase.firestore.collection("groups")
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    allGroups = snap.documents.mapNotNull { it.toObject(StudyGroup::class.java) }
                }
            }
        onDispose { groupsListener.remove() }
    }

    // ── Real-time notifications listener (top 3 for recent activity) ──────────
    DisposableEffect(Unit) {
        val notifListener = Firebase.firestore.collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(3)
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    recentNotifs = snap.documents.mapNotNull { doc ->
                        try {
                            val type = when (doc.getString("type")) {
                                "NEW_MEMBER"   -> NotificationType.NEW_MEMBER
                                "ANNOUNCEMENT" -> NotificationType.ANNOUNCEMENT
                                "REMINDER"     -> NotificationType.REMINDER
                                else           -> NotificationType.NEW_MEMBER
                            }
                            val groupName = doc.getString("groupName") ?: "a group"
                            val timestamp = doc.getLong("timestamp") ?: 0L
                            val title = when (type) {
                                NotificationType.NEW_MEMBER   -> "New Member Joined"
                                NotificationType.ANNOUNCEMENT -> "Group Announcement"
                                NotificationType.REMINDER     -> "Study Reminder"
                            }
                            val body = when (type) {
                                NotificationType.NEW_MEMBER   -> "Someone joined $groupName."
                                NotificationType.ANNOUNCEMENT -> "New announcement in $groupName."
                                NotificationType.REMINDER     -> "Upcoming session in $groupName."
                            }
                            RecentNotif(
                                id      = doc.id,
                                title   = title,
                                body    = body,
                                timeAgo = formatDashTimeAgo(timestamp),
                                type    = type
                            )
                        } catch (_: Exception) { null }
                    }
                    unreadCount = snap.documents.size   // treat all fetched as unread for badge
                }
            }
        onDispose { notifListener.remove() }
    }

    // ── Derived stats ──────────────────────────────────────────────────────────
    val myGroups       = allGroups.filter { it.members.contains(currentUid) }
    val discoverGroups = allGroups.filter { !it.members.contains(currentUid) }
    val displayedGroups = if (selectedTab == 0) myGroups else discoverGroups

    Scaffold(
        topBar = {
            DashboardTopBar(
                userName        = userName,
                unreadCount     = unreadCount,
                onProfileClick  = onNavigateToProfile,
                onNotifClick    = onNavigateToNotifications,
                onSuperuserClick = onNavigateToSuperuser
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick        = onNavigateToCreateGroup,
                containerColor = Ink900,
                contentColor   = White,
                shape          = RoundedCornerShape(14.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp)) },
                text = { Text("New Group", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold) }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = Ink50
    ) { innerPadding ->
        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ── Hero Banner ────────────────────────────────────────────────────
            item {
                HeroBanner(
                    userName           = userName,
                    onNavigateToGroups = onNavigateToGroups
                )
            }

            // ── Stats Row (live counts) ────────────────────────────────────────
            item {
                StatsRow(
                    myGroupsCount    = myGroups.size,
                    availableCount   = discoverGroups.size,
                    notifCount       = unreadCount,
                    modifier         = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            // ── Tab selector ───────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        "Study Groups",
                        style      = MaterialTheme.typography.titleMedium,
                        color      = Ink900,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onNavigateToGroups) {
                        Text("See all", color = Ink400, style = MaterialTheme.typography.labelSmall)
                        Icon(Icons.Default.ChevronRight, null, tint = Ink400, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
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
                                text       = label,
                                style      = MaterialTheme.typography.labelMedium,
                                color      = if (isSelected) Ink900 else Ink400,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // ── Group list ─────────────────────────────────────────────────────
            if (displayedGroups.isEmpty()) {
                item {
                    Box(
                        modifier         = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            icon     = Icons.Outlined.Groups,
                            title    = if (selectedTab == 0) "No Groups Yet" else "No Groups to Discover",
                            subtitle = if (selectedTab == 0)
                                "You haven't joined any groups. Explore and join one!"
                            else
                                "All available groups are already full. Check back later!",
                            action   = if (selectedTab == 0) "Browse Groups" else null,
                            onAction = if (selectedTab == 0) ({ selectedTab = 1 }) else null
                        )
                    }
                }
            } else {
                items(displayedGroups, key = { it.groupId }) { group ->
                    val isJoined            = group.members.contains(currentUid)
                    val isJoiningThisGroup  = joiningIds.contains(group.groupId)
                    StudyGroupCard(
                        groupName   = group.groupName,
                        subject     = group.description,
                        memberCount = group.members.size,
                        maxMembers  = group.maxMembers,
                        adminName   = group.adminId,
                        isJoined    = isJoined,
                        onClick     = { onNavigateToGroupDetail(group.groupId) },
                        onJoinClick = {
                            if (!isJoiningThisGroup && !isJoined) {
                                joiningIds = joiningIds + group.groupId
                                scope.launch {
                                    try {
                                        Firebase.firestore.collection("groups")
                                            .document(group.groupId)
                                            .update("members", FieldValue.arrayUnion(currentUid))
                                            .await()
                                        Firebase.firestore.collection("notifications").add(
                                            mapOf(
                                                "type"      to "NEW_MEMBER",
                                                "groupId"   to group.groupId,
                                                "groupName" to group.groupName,
                                                "userId"    to currentUid,
                                                "timestamp" to System.currentTimeMillis()
                                            )
                                        ).await()
                                    } catch (_: Exception) {
                                    } finally {
                                        joiningIds = joiningIds - group.groupId
                                    }
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }

            // ── Recent Activity ────────────────────────────────────────────────
            item {
                Spacer(Modifier.height(20.dp))
                SectionHeader(
                    title        = "Recent Activity",
                    action       = "View All",
                    onActionClick = onNavigateToNotifications,
                    modifier     = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(10.dp))
            }

            if (recentNotifs.isEmpty()) {
                item {
                    Box(
                        modifier         = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No recent activity yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Ink300
                        )
                    }
                }
            } else {
                items(recentNotifs, key = { it.id }) { notif ->
                    NotificationItem(
                        title    = notif.title,
                        body     = notif.body,
                        timeAgo  = notif.timeAgo,
                        type     = notif.type,
                        isRead   = false,
                        onClick  = { onNavigateToNotifications() },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// ── Helpers ────────────────────────────────────────────────────────────────────
private fun formatDashTimeAgo(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60_000     -> "Just now"
        diff < 3_600_000  -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        else              -> "${diff / 86_400_000}d ago"
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
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(White, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.School, null, tint = Ink900, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    "HanapAral",
                    style          = MaterialTheme.typography.titleLarge,
                    color          = White,
                    fontWeight     = FontWeight.Bold,
                    letterSpacing  = (-0.3).sp
                )
            }
        },
        actions = {
            Box {
                IconButton(onClick = onNotifClick) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = White)
                }
                if (unreadCount > 0) {
                    Badge(
                        containerColor = Danger,
                        contentColor   = White,
                        modifier       = Modifier
                            .offset(x = (-4).dp, y = 4.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(unreadCount.toString(), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            IconButton(onClick = onProfileClick) {
                AvatarInitials(
                    name            = userName.ifEmpty { "?" },
                    size            = 32.dp,
                    backgroundColor = White.copy(alpha = 0.15f),
                    textColor       = White
                )
            }
            IconButton(onClick = onSuperuserClick) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = "Superuser", tint = White.copy(alpha = 0.5f))
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
                text  = if (userName.isEmpty()) "Hello 👋" else "Hello, ${userName.split(" ").first()} 👋",
                style = MaterialTheme.typography.bodyMedium,
                color = White.copy(alpha = 0.55f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text          = "Ready to\nstudy today?",
                style         = MaterialTheme.typography.headlineLarge,
                color         = White,
                fontWeight    = FontWeight.ExtraBold,
                lineHeight    = 32.sp,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(20.dp))
            OutlinedButton(
                onClick     = onNavigateToGroups,
                shape       = RoundedCornerShape(10.dp),
                border      = BorderStroke(1.dp, White.copy(alpha = 0.25f)),
                colors      = ButtonDefaults.outlinedButtonColors(
                    containerColor = White.copy(alpha = 0.1f),
                    contentColor   = White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Search, null, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(6.dp))
                Text("Find Groups", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
            }
        }
        Icon(
            imageVector    = Icons.Default.Groups,
            contentDescription = null,
            tint           = White.copy(alpha = 0.06f),
            modifier       = Modifier
                .size(120.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 12.dp)
        )
    }
}

// ── Stats Row (all values are now parameters, no hardcoding) ──────────────────
@Composable
private fun StatsRow(
    myGroupsCount: Int,
    availableCount: Int,
    notifCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            label    = "My Groups",
            value    = myGroupsCount.toString(),
            icon     = Icons.Default.Groups,
            color    = Ink900,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label    = "Available",
            value    = availableCount.toString(),
            icon     = Icons.Default.Explore,
            color    = Ink900,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label    = "Alerts",
            value    = notifCount.toString(),
            icon     = Icons.Default.Notifications,
            color    = Ink900,
            modifier = Modifier.weight(1f)
        )
    }
}