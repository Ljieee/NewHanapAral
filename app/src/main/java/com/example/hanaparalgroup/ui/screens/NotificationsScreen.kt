package com.example.hanaparalgroup.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*

private data class NotifItem(
    val id: Int,
    val title: String,
    val body: String,
    val timeAgo: String,
    val type: NotificationType,
    var isRead: Boolean
)

@Composable
fun NotificationsScreen(onNavigateBack: () -> Unit) {
    val notifications = remember {
        mutableStateListOf(
            NotifItem(1, "New Member Joined", "Carlo Dela Cruz joined Algorithm Avengers.", "2m ago", NotificationType.NEW_MEMBER, false),
            NotifItem(2, "Study Reminder", "DB Detectives session starts in 30 minutes. Room 201, Library.", "28m ago", NotificationType.REMINDER, false),
            NotifItem(3, "Group Announcement", "Web Wizards: New study material has been uploaded by Maria Santos.", "3h ago", NotificationType.ANNOUNCEMENT, false),
            NotifItem(4, "New Member Joined", "Ben Flores joined DB Detectives.", "5h ago", NotificationType.NEW_MEMBER, true),
            NotifItem(5, "Study Reminder", "Algorithm Avengers review session tomorrow at 2 PM.", "1d ago", NotificationType.REMINDER, true),
            NotifItem(6, "Group Announcement", "Algorithm Avengers: Midterm review notes uploaded.", "2d ago", NotificationType.ANNOUNCEMENT, true),
            NotifItem(7, "New Member Joined", "Ina Cruz joined Web Wizards.", "3d ago", NotificationType.NEW_MEMBER, true),
        )
    }

    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        topBar = {
            HanapAralTopBar(
                title = "Notifications",
                onNavigateBack = onNavigateBack,
                actions = {
                    if (unreadCount > 0) {
                        TextButton(onClick = { notifications.forEach { it.isRead = true } }) {
                            Text("Mark all read", color = ActionLight, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            )
        },
        containerColor = Background
    ) { padding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Outlined.NotificationsOff,
                    title = "All Caught Up!",
                    subtitle = "No notifications yet. We'll alert you when something happens."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                // Summary strip
                item {
                    if (unreadCount > 0) {
                        NotifSummaryStrip(
                            unreadCount = unreadCount,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                }

                // Unread section
                val unread = notifications.filter { !it.isRead }
                val read = notifications.filter { it.isRead }

                if (unread.isNotEmpty()) {
                    item {
                        Text(
                            "New",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                    items(unread, key = { it.id }) { notif ->
                        NotificationItem(
                            title = notif.title,
                            body = notif.body,
                            timeAgo = notif.timeAgo,
                            type = notif.type,
                            isRead = notif.isRead,
                            onClick = { notifications[notifications.indexOfFirst { it.id == notif.id }].isRead = true },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                        )
                    }
                }

                if (read.isNotEmpty()) {
                    item {
                        Text(
                            "Earlier",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                    items(read, key = { it.id }) { notif ->
                        NotificationItem(
                            title = notif.title,
                            body = notif.body,
                            timeAgo = notif.timeAgo,
                            type = notif.type,
                            isRead = notif.isRead,
                            onClick = {},
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotifSummaryStrip(unreadCount: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Action.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Action.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PulsingDot(color = Action)
            Spacer(Modifier.width(10.dp))
            Text(
                "$unreadCount unread notification${if (unreadCount > 1) "s" else ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = Action,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}