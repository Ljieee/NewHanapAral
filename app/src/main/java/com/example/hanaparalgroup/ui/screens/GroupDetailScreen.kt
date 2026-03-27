package com.example.hanaparalgroup.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*

private data class Member(val name: String, val role: String, val joinedDate: String)
private data class Announcement(val title: String, val body: String, val author: String, val timeAgo: String)

@Composable
fun GroupDetailScreen(
    groupId: String,
    onNavigateBack: () -> Unit
) {
    val group = GroupPreview(
        id = groupId,
        name = "Algorithm Avengers",
        subject = "Data Structures & Algorithms",
        memberCount = 8,
        maxMembers = 15,
        adminName = "Juan dela Cruz",
        isJoined = true
    )

    val members = listOf(
        Member("Juan dela Cruz", "Admin", "Mar 1"),
        Member("Alex Aropo", "Member", "Mar 5"),
        Member("Maria Santos", "Member", "Mar 7"),
        Member("Carlos Reyes", "Member", "Mar 10"),
        Member("Ana Gomez", "Member", "Mar 12"),
        Member("Ben Flores", "Member", "Mar 15"),
        Member("Ina Cruz", "Member", "Mar 18"),
        Member("Ray Tan", "Member", "Mar 20"),
    )

    val announcements = listOf(
        Announcement("Study Session Tomorrow", "We'll be meeting at the library, Room 201 at 3 PM. Please bring your notes on sorting algorithms.", "Juan dela Cruz", "2h ago"),
        Announcement("New Resource Shared", "I've uploaded the slide deck for Week 5 on graph traversal. Check the shared folder!", "Alex Aropo", "1d ago"),
        Announcement("Quiz Reminder", "We have our DSA midterm this Friday. Let's do a group review session on Thursday evening.", "Juan dela Cruz", "2d ago"),
    )

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Announcements", "Members")
    var showLeaveDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            HanapAralTopBar(
                title = group.name,
                onNavigateBack = onNavigateBack,
                actions = {
                    if (group.isJoined) {
                        IconButton(onClick = { showLeaveDialog = true }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Leave Group", tint = White.copy(alpha = 0.6f))
                        }
                    }
                    IconButton(onClick = { /* share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = White)
                    }
                }
            )
        },
        floatingActionButton = {
            if (group.isJoined) {
                FloatingActionButton(
                    onClick = { /* Alora: send announcement trigger */ },
                    containerColor = Ink900,
                    contentColor = White,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = "Announce")
                }
            }
        },
        containerColor = Ink50
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ── Hero ─────────────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Ink900)
                        .padding(horizontal = 24.dp, vertical = 28.dp)
                ) {
                    Column {
                        // Chips
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = White.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = group.subject,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = White.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                            if (group.isJoined) {
                                Spacer(Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Positive.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "✓ Joined",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PositiveLight,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            group.name,
                            style = MaterialTheme.typography.headlineLarge,
                            color = White,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = White.copy(alpha = 0.45f), modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Admin: ${group.adminName}", style = MaterialTheme.typography.labelSmall, color = White.copy(alpha = 0.45f))
                        }
                    }
                }
            }

            // ── Stats strip ──────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DetailStatChip(
                        icon = Icons.Default.Group,
                        value = "${group.memberCount}/${group.maxMembers}",
                        label = "Members",
                        modifier = Modifier.weight(1f)
                    )
                    DetailStatChip(
                        icon = Icons.Default.Campaign,
                        value = announcements.size.toString(),
                        label = "Posts",
                        modifier = Modifier.weight(1f)
                    )
                    DetailStatChip(
                        icon = Icons.Default.CalendarToday,
                        value = "Active",
                        label = "Status",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── Join button (if not joined) ──────────────────────────────────
            if (!group.isJoined) {
                item {
                    PrimaryButton(
                        text = "Join This Group",
                        onClick = { /* Balanag: join logic */ },
                        icon = Icons.Default.Add,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            // ── Tab selector ─────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .background(Ink100, RoundedCornerShape(10.dp))
                        .padding(3.dp)
                ) {
                    tabs.forEachIndexed { idx, label ->
                        val isSelected = selectedTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) White else Color.Transparent)
                                .clickable { selectedTab = idx }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Ink900 else Ink400,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            // ── Announcements tab ────────────────────────────────────────────
            if (selectedTab == 0) {
                if (announcements.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.Outlined.Campaign,
                            title = "No Announcements",
                            subtitle = "Group admins can post announcements here.",
                            modifier = Modifier.padding(40.dp)
                        )
                    }
                } else {
                    items(announcements) { ann ->
                        AnnouncementCard(
                            title = ann.title,
                            body = ann.body,
                            author = ann.author,
                            timeAgo = ann.timeAgo,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // ── Members tab ──────────────────────────────────────────────────
            if (selectedTab == 1) {
                item {
                    MemberAvatarStack(
                        names = members.map { it.name },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                }
                items(members) { member ->
                    MemberRow(
                        name = member.name,
                        role = member.role,
                        joinedDate = member.joinedDate,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Danger) },
            title = { Text("Leave Group?", fontWeight = FontWeight.Bold, color = Ink900) },
            text = { Text("Are you sure you want to leave \"${group.name}\"? You can rejoin later if there's space.", color = Ink400, style = MaterialTheme.typography.bodySmall) },
            confirmButton = {
                Button(
                    onClick = { showLeaveDialog = false; onNavigateBack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Danger),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Leave") }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLeaveDialog = false },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Ink200)
                ) { Text("Cancel", color = Ink700) }
            },
            shape = RoundedCornerShape(18.dp),
            containerColor = White
        )
    }
}

@Composable
private fun DetailStatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Ink200)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = Ink400, modifier = Modifier.size(18.dp))
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleSmall, color = Ink900, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = Ink400, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun AnnouncementCard(
    title: String,
    body: String,
    author: String,
    timeAgo: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Ink200)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarInitials(
                    name = author,
                    size = 36.dp,
                    backgroundColor = Ink900
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(author, style = MaterialTheme.typography.labelMedium, color = Ink900, fontWeight = FontWeight.SemiBold)
                    Text(timeAgo, style = MaterialTheme.typography.labelSmall, color = Ink300)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, color = Ink900, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(body, style = MaterialTheme.typography.bodySmall, color = Ink600, lineHeight = 20.sp)
        }
    }
}

@Composable
private fun MemberRow(
    name: String,
    role: String,
    joinedDate: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Ink200)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarInitials(
                name = name,
                size = 38.dp,
                backgroundColor = if (role == "Admin") Ink900 else Ink200,
                textColor = if (role == "Admin") White else Ink700
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.labelLarge, color = Ink900, fontWeight = FontWeight.SemiBold)
                Text("Joined $joinedDate", style = MaterialTheme.typography.labelSmall, color = Ink400)
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (role == "Admin") Ink900 else Ink100
            ) {
                Text(
                    text = role,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (role == "Admin") White else Ink600,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}