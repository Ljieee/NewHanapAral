package com.example.hanaparalgroup.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// ── Local model ────────────────────────────────────────────────────────────────
private data class NotifItem(
    val id: String,
    val title: String,
    val body: String,
    val timeAgo: String,
    val type: NotificationType,
    val isRead: Boolean
)

@Composable
fun NotificationsScreen(onNavigateBack: () -> Unit) {
    val currentUid = Firebase.auth.currentUser?.uid ?: ""

    // FIX: Track read IDs in a separate remembered Set so it is NEVER reset
    // when Firestore pushes new data. This is the source of truth for read state.
    val readIds = remember { mutableStateOf(emptySet<String>()) }

    var rawItems  by remember { mutableStateOf<List<NotifItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // ── Real-time Firestore listener ───────────────────────────────────────────
    DisposableEffect(currentUid) {
        val listener = Firebase.firestore
            .collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    rawItems = snapshot.documents.mapNotNull { doc ->
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

                            // FIX: isRead is derived from readIds Set, NOT set to false here
                            NotifItem(
                                id      = doc.id,
                                title   = title,
                                body    = body,
                                timeAgo = formatTimeAgo(timestamp),
                                type    = type,
                                isRead  = doc.id in readIds.value
                            )
                        } catch (_: Exception) { null }
                    }
                    isLoading = false
                }
            }
        onDispose { listener.remove() }
    }

    // Derive final list with up-to-date isRead from the readIds Set
    val notifications = rawItems.map { it.copy(isRead = it.id in readIds.value) }
    val unreadCount   = notifications.count { !it.isRead }

    Scaffold(
        topBar = {
            HanapAralTopBar(
                title = "Notifications",
                onNavigateBack = onNavigateBack,
                actions = {
                    if (unreadCount > 0) {
                        TextButton(onClick = {
                            // FIX: add ALL current IDs to readIds — never resets
                            readIds.value = readIds.value + notifications.map { it.id }.toSet()
                        }) {
                            Text(
                                "Mark all read",
                                color = White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            )
        },
        containerColor = Ink50
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Ink900) }
            }

            notifications.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon     = Icons.Outlined.NotificationsOff,
                        title    = "All caught up",
                        subtitle = "No notifications yet. We'll alert you when something happens."
                    )
                }
            }

            else -> {
                val unread = notifications.filter { !it.isRead }
                val read   = notifications.filter { it.isRead }

                LazyColumn(
                    modifier       = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    // Summary strip
                    if (unreadCount > 0) {
                        item {
                            NotifSummaryStrip(
                                unreadCount = unreadCount,
                                modifier    = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                    }

                    // Unread section
                    if (unread.isNotEmpty()) {
                        item {
                            Text(
                                "New",
                                style      = MaterialTheme.typography.labelMedium,
                                color      = Ink400,
                                fontWeight = FontWeight.SemiBold,
                                modifier   = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                        items(unread, key = { it.id }) { notif ->
                            NotificationItem(
                                title    = notif.title,
                                body     = notif.body,
                                timeAgo  = notif.timeAgo,
                                type     = notif.type,
                                isRead   = notif.isRead,
                                // FIX: mark as read by adding to the persistent readIds Set
                                onClick  = {
                                    readIds.value = readIds.value + notif.id
                                },
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Read section
                    if (read.isNotEmpty()) {
                        item {
                            Text(
                                "Earlier",
                                style      = MaterialTheme.typography.labelMedium,
                                color      = Ink400,
                                fontWeight = FontWeight.SemiBold,
                                modifier   = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                        items(read, key = { it.id }) { notif ->
                            NotificationItem(
                                title    = notif.title,
                                body     = notif.body,
                                timeAgo  = notif.timeAgo,
                                type     = notif.type,
                                isRead   = notif.isRead,
                                onClick  = {},
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Helpers ────────────────────────────────────────────────────────────────────
private fun formatTimeAgo(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60_000       -> "Just now"
        diff < 3_600_000    -> "${diff / 60_000}m ago"
        diff < 86_400_000   -> "${diff / 3_600_000}h ago"
        else                -> "${diff / 86_400_000}d ago"
    }
}

@Composable
private fun NotifSummaryStrip(unreadCount: Int, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = AccentSoft),
        border    = BorderStroke(1.dp, Accent.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PulsingDot(color = Accent)
            Spacer(Modifier.width(10.dp))
            Text(
                "$unreadCount unread notification${if (unreadCount > 1) "s" else ""}",
                style      = MaterialTheme.typography.labelMedium,
                color      = Accent,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}