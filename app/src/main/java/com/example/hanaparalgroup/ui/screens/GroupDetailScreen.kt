package com.example.hanaparalgroup.ui.screens

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
    // Placeholder data – replaced by Firestore (Balanag)
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
    var showLeaveDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            HanapAralTopBar(
                title = group.name,
                onNavigateBack = onNavigateBack,
                actions = {
                    if (group.isJoined) {
                        IconButton(onClick = { showLeaveDialog = true }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Leave Group", tint = AlertLight)
                        }
                    }
                    IconButton(onClick = { /* share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Surface)
                    }
                }
            )
        },
        floatingActionButton = {
            if (group.isJoined) {
                FloatingActionButton(
                    onClick = { /* Alora: send announcement trigger */ },
                    containerColor = Action,
                    contentColor = Surface,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = "Announce")
                }
            }
        },
        containerColor = Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ── Hero card ────────────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.horizontalGradient(listOf(BrandDark, Brand, GradientEnd))
                        )
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawCircle(
                            color = Surface.copy(alpha = 0.07f),
                            radius = 150f,
                            center = Offset(size.width * 0.85f, size.height * 0.2f)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TagChip(text = group.subject, color = ActionLight)
                            Spacer(Modifier.width(8.dp))
                            if (group.isJoined) TagChip(text = "✓ Joined", color = SuccessLight)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            group.name,
                            style = MaterialTheme.typography.displaySmall,
                            color = Surface,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = Surface.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Admin: ${group.adminName}", style = MaterialTheme.typography.bodySmall, color = Surface.copy(alpha = 0.7f))
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailStatChip(
                        icon = Icons.Default.Group,
                        value = "${group.memberCount}/${group.maxMembers}",
                        label = "Members",
                        color = Brand,
                        modifier = Modifier.weight(1f)
                    )
                    DetailStatChip(
                        icon = Icons.Default.Campaign,
                        value = announcements.size.toString(),
                        label = "Announcements",
                        color = Action,
                        modifier = Modifier.weight(1f)
                    )
                    DetailStatChip(
                        icon = Icons.Default.CalendarToday,
                        value = "Active",
                        label = "Status",
                        color = Success,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── Join button (if not joined) ──────────────────────────────────
            if (!group.isJoined) {
                item {
                    ActionButton(
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
                val tabs = listOf("Announcements", "Members")
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .background(SurfaceAlt, RoundedCornerShape(14.dp))
                        .padding(4.dp)
                ) {
                    tabs.forEachIndexed { idx, label ->
                        val isSelected = selectedTab == idx
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Brand else Color.Transparent)
                                .clickable { selectedTab = idx }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) Surface else TextSecondary
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
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
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
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
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }

    // Leave confirmation dialog
    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Alert) },
            title = { Text("Leave Group?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to leave \"${group.name}\"? You can rejoin later if there's space.") },
            confirmButton = {
                Button(
                    onClick = { showLeaveDialog = false; onNavigateBack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Alert)
                ) { Text("Leave") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLeaveDialog = false }) { Text("Cancel") }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun DetailStatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary, textAlign = TextAlign.Center)
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Brand.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Campaign, null, tint = Brand, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("$author · $timeAgo", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(body, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
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
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarInitials(
                name = name,
                size = 40.dp,
                backgroundColor = if (role == "Admin") Brand else Action.copy(alpha = 0.7f)
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Text("Joined $joinedDate", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
            TagChip(
                text = role,
                color = if (role == "Admin") Brand else Action
            )
        }
    }
}