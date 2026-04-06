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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

// ── Local model for announcements subcollection ────────────────────────────────
private data class Announcement(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val authorName: String = "",
    val authorId: String = "",          // ← NEW: needed to look up author's photo
    val timestamp: Long = 0L
)

// ── Holds both name and profile picture URL for a member ─────────────────────
private data class MemberInfo(
    val name: String = "",
    val profilePictureUrl: String = ""
)

@Composable
fun GroupDetailScreen(
    groupId: String,
    onNavigateBack: () -> Unit
) {
    val currentUid = Firebase.auth.currentUser?.uid ?: ""
    val scope = rememberCoroutineScope()

    // ── State ──────────────────────────────────────────────────────────────────
    var group           by remember { mutableStateOf<StudyGroup?>(null) }
    // KEY CHANGE: store MemberInfo (name + photo) instead of just name
    var memberInfoMap   by remember { mutableStateOf<Map<String, MemberInfo>>(emptyMap()) }
    var announcements   by remember { mutableStateOf<List<Announcement>>(emptyList()) }
    var isLoading       by remember { mutableStateOf(true) }
    var isJoining       by remember { mutableStateOf(false) }
    var isLeaving       by remember { mutableStateOf(false) }
    var showLeaveDialog by remember { mutableStateOf(false) }
    var selectedTab     by remember { mutableIntStateOf(0) }
    val tabs = listOf("Announcements", "Members")

    // ── Announcement dialog state ──────────────────────────────────────────────
    var showPostDialog by remember { mutableStateOf(false) }
    var annTitle       by remember { mutableStateOf("") }
    var annBody        by remember { mutableStateOf("") }
    var isPosting      by remember { mutableStateOf(false) }
    var postError      by remember { mutableStateOf("") }

    // ── Real-time listeners ────────────────────────────────────────────────────
    DisposableEffect(groupId) {
        val groupListener = Firebase.firestore
            .collection("groups")
            .document(groupId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val g = snapshot.toObject(StudyGroup::class.java)
                    if (g != null) {
                        group = g
                        isLoading = false
                        // Fetch name AND profilePictureUrl for every member
                        scope.launch {
                            val infoMap = mutableMapOf<String, MemberInfo>()
                            g.members.forEach { uid ->
                                try {
                                    val doc = Firebase.firestore
                                        .collection("users")
                                        .document(uid)
                                        .get()
                                        .await()
                                    val fetchedName = doc.getString("name")
                                        ?: doc.getString("displayName")
                                        ?: "User (${uid.take(6)})"
                                    val fetchedPhoto = doc.getString("profilePictureUrl") ?: ""
                                    infoMap[uid] = MemberInfo(
                                        name = fetchedName,
                                        profilePictureUrl = fetchedPhoto
                                    )
                                } catch (_: Exception) {
                                    infoMap[uid] = MemberInfo(name = "User (${uid.take(6)})")
                                }
                            }
                            memberInfoMap = infoMap
                        }
                    }
                } else {
                    isLoading = false
                }
            }

        // Announcements subcollection listener — also store authorId
        val announcementsListener = Firebase.firestore
            .collection("groups")
            .document(groupId)
            .collection("announcements")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    announcements = snapshot.documents.mapNotNull { doc ->
                        try {
                            Announcement(
                                id         = doc.id,
                                title      = doc.getString("title") ?: "",
                                body       = doc.getString("body") ?: "",
                                authorName = doc.getString("authorName") ?: "Unknown",
                                authorId   = doc.getString("authorId") ?: "",
                                timestamp  = doc.getLong("timestamp") ?: 0L
                            )
                        } catch (_: Exception) { null }
                    }
                }
            }

        onDispose {
            groupListener.remove()
            announcementsListener.remove()
        }
    }

    // ── Derived state ──────────────────────────────────────────────────────────
    val isJoined = group?.members?.contains(currentUid) == true
    val isAdmin  = group?.adminId == currentUid

    // ── Join ───────────────────────────────────────────────────────────────────
    fun joinGroup() {
        val g = group ?: return
        if (g.members.size >= g.maxMembers) return
        isJoining = true
        scope.launch {
            try {
                Firebase.firestore.collection("groups").document(groupId)
                    .update("members", FieldValue.arrayUnion(currentUid))
                    .await()
                Firebase.firestore.collection("notifications").add(
                    mapOf(
                        "type"      to "NEW_MEMBER",
                        "groupId"   to groupId,
                        "groupName" to g.groupName,
                        "userId"    to currentUid,
                        "timestamp" to System.currentTimeMillis()
                    )
                ).await()
            } catch (_: Exception) {
            } finally {
                isJoining = false
            }
        }
    }

    // ── Leave ──────────────────────────────────────────────────────────────────
    fun leaveGroup() {
        isLeaving = true
        scope.launch {
            try {
                Firebase.firestore.collection("groups").document(groupId)
                    .update("members", FieldValue.arrayRemove(currentUid))
                    .await()
            } catch (_: Exception) {
            } finally {
                isLeaving = false
                showLeaveDialog = false
                onNavigateBack()
            }
        }
    }

    // ── Post Announcement ──────────────────────────────────────────────────────
    fun postAnnouncement() {
        if (annTitle.isBlank()) { postError = "Title is required."; return }
        if (annBody.isBlank())  { postError = "Message body is required."; return }
        isPosting = true
        postError = ""
        val authorName = memberInfoMap[currentUid]?.name ?: "Admin"
        scope.launch {
            try {
                val annData = mapOf(
                    "title"      to annTitle.trim(),
                    "body"       to annBody.trim(),
                    "authorName" to authorName,
                    "authorId"   to currentUid,      // ← saved so we can load their photo
                    "timestamp"  to System.currentTimeMillis()
                )
                Firebase.firestore
                    .collection("groups")
                    .document(groupId)
                    .collection("announcements")
                    .add(annData)
                    .await()

                Firebase.firestore.collection("notifications").add(
                    mapOf(
                        "type"      to "ANNOUNCEMENT",
                        "groupId"   to groupId,
                        "groupName" to (group?.groupName ?: ""),
                        "userId"    to currentUid,
                        "message"   to annTitle.trim(),
                        "timestamp" to System.currentTimeMillis()
                    )
                ).await()

                annTitle = ""
                annBody  = ""
                showPostDialog = false
            } catch (e: Exception) {
                postError = "Failed to post: ${e.localizedMessage}"
            } finally {
                isPosting = false
            }
        }
    }

    // ── UI ─────────────────────────────────────────────────────────────────────
    Scaffold(
        topBar = {
            HanapAralTopBar(
                title = group?.groupName ?: "Group",
                onNavigateBack = onNavigateBack,
                actions = {
                    if (isJoined) {
                        IconButton(onClick = { showLeaveDialog = true }) {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = "Leave Group",
                                tint = White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick        = { showPostDialog = true },
                    containerColor = Ink900,
                    contentColor   = White,
                    shape          = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = "Post Announcement")
                }
            }
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

            group == null -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Group not found.", color = Danger, style = MaterialTheme.typography.bodyMedium)
                }
            }

            else -> {
                val g = group!!
                val isFull = g.members.size >= g.maxMembers

                LazyColumn(
                    modifier       = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    // ── Hero ───────────────────────────────────────────────────
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Ink900)
                                .padding(horizontal = 24.dp, vertical = 28.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = White.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = g.description.substringBefore(" —")
                                                .ifEmpty { g.description },
                                            style    = MaterialTheme.typography.labelSmall,
                                            color    = White.copy(alpha = 0.8f),
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                    if (isJoined) {
                                        Spacer(Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = Positive.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                "✓ Joined",
                                                style    = MaterialTheme.typography.labelSmall,
                                                color    = PositiveLight,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    g.groupName,
                                    style         = MaterialTheme.typography.headlineLarge,
                                    color         = White,
                                    fontWeight    = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                )
                                Spacer(Modifier.height(6.dp))
                                // Show admin's avatar + name in hero
                                val adminInfo = memberInfoMap[g.adminId]
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (adminInfo != null) {
                                        AvatarInitials(
                                            name     = adminInfo.name,
                                            imageUrl = adminInfo.profilePictureUrl,
                                            size     = 20.dp,
                                            backgroundColor = White.copy(alpha = 0.2f),
                                            textColor = White
                                        )
                                        Spacer(Modifier.width(6.dp))
                                    } else {
                                        Icon(
                                            Icons.Default.Person, null,
                                            tint     = White.copy(alpha = 0.45f),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                    }
                                    Text(
                                        "Admin: ${adminInfo?.name ?: "Loading…"}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = White.copy(alpha = 0.45f)
                                    )
                                }
                            }
                        }
                    }

                    // ── Stats strip ────────────────────────────────────────────
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DetailStatChip(
                                icon  = Icons.Default.Group,
                                value = "${g.members.size}/${g.maxMembers}",
                                label = "Members",
                                modifier = Modifier.weight(1f)
                            )
                            DetailStatChip(
                                icon  = Icons.Default.Campaign,
                                value = announcements.size.toString(),
                                label = "Posts",
                                modifier = Modifier.weight(1f)
                            )
                            DetailStatChip(
                                icon  = Icons.Default.CalendarToday,
                                value = "Active",
                                label = "Status",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // ── Join button ────────────────────────────────────────────
                    if (!isJoined) {
                        item {
                            PrimaryButton(
                                text      = if (isFull) "Group is Full" else "Join This Group",
                                onClick   = { if (!isFull) joinGroup() },
                                enabled   = !isFull && !isJoining,
                                isLoading = isJoining,
                                icon      = if (!isFull) Icons.Default.Add else null,
                                modifier  = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // ── Tabs ───────────────────────────────────────────────────
                    item {
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            tabs.forEachIndexed { idx, tab ->
                                val sel = selectedTab == idx
                                Surface(
                                    onClick  = { selectedTab = idx },
                                    modifier = Modifier.weight(1f),
                                    shape    = RoundedCornerShape(10.dp),
                                    color    = if (sel) Ink900 else White,
                                    border   = BorderStroke(1.dp, if (sel) Ink900 else Ink200)
                                ) {
                                    Text(
                                        text       = tab,
                                        modifier   = Modifier.padding(vertical = 10.dp),
                                        style      = MaterialTheme.typography.labelMedium,
                                        color      = if (sel) White else Ink400,
                                        fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                                        textAlign  = TextAlign.Center
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                    }

                    // ── Announcements tab ──────────────────────────────────────
                    if (selectedTab == 0) {
                        if (announcements.isEmpty()) {
                            item {
                                EmptyState(
                                    icon     = Icons.Outlined.Campaign,
                                    title    = "No Announcements",
                                    subtitle = if (isAdmin)
                                        "Tap the megaphone button to post an announcement."
                                    else
                                        "Group admins can post announcements here.",
                                    modifier = Modifier.padding(40.dp)
                                )
                            }
                        } else {
                            items(announcements, key = { it.id }) { ann ->
                                // Look up author's profile picture from memberInfoMap
                                val authorInfo = memberInfoMap[ann.authorId]
                                AnnouncementCard(
                                    title      = ann.title,
                                    body       = ann.body,
                                    author     = ann.authorName,
                                    authorPhoto = authorInfo?.profilePictureUrl ?: "",
                                    timeAgo    = formatTimeAgo(ann.timestamp),
                                    modifier   = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // ── Members tab ────────────────────────────────────────────
                    if (selectedTab == 1) {
                        item {
                            MemberAvatarStack(
                                names    = g.members.map { memberInfoMap[it]?.name ?: "?" },
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        items(g.members, key = { it }) { uid ->
                            val info = memberInfoMap[uid]
                            val role = if (uid == g.adminId) "Admin" else "Member"
                            MemberRow(
                                name        = info?.name ?: "Loading…",
                                profileUrl  = info?.profilePictureUrl ?: "",
                                role        = role,
                                modifier    = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Post Announcement Dialog ───────────────────────────────────────────────
    if (showPostDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isPosting) {
                    showPostDialog = false
                    annTitle  = ""
                    annBody   = ""
                    postError = ""
                }
            },
            icon  = { Icon(Icons.Default.Campaign, null, tint = Ink900) },
            title = { Text("Post Announcement", fontWeight = FontWeight.Bold, color = Ink900) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value         = annTitle,
                        onValueChange = { annTitle = it; postError = "" },
                        label         = { Text("Title", style = MaterialTheme.typography.bodySmall) },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(10.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = Ink900,
                            unfocusedBorderColor = Ink200
                        )
                    )
                    OutlinedTextField(
                        value         = annBody,
                        onValueChange = { annBody = it; postError = "" },
                        label         = { Text("Message", style = MaterialTheme.typography.bodySmall) },
                        singleLine    = false,
                        maxLines      = 5,
                        minLines      = 3,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(10.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = Ink900,
                            unfocusedBorderColor = Ink200
                        )
                    )
                    if (postError.isNotEmpty()) {
                        Text(postError, color = Danger, style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick  = { postAnnouncement() },
                    enabled  = !isPosting,
                    colors   = ButtonDefaults.buttonColors(containerColor = Ink900),
                    shape    = RoundedCornerShape(10.dp)
                ) {
                    if (isPosting) {
                        CircularProgressIndicator(color = White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Post")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showPostDialog = false; annTitle = ""; annBody = ""; postError = "" },
                    enabled = !isPosting,
                    shape   = RoundedCornerShape(10.dp),
                    border  = BorderStroke(1.dp, Ink200)
                ) { Text("Cancel", color = Ink700) }
            },
            shape          = RoundedCornerShape(18.dp),
            containerColor = White
        )
    }

    // ── Leave confirmation dialog ──────────────────────────────────────────────
    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            icon  = { Icon(Icons.Default.ExitToApp, null, tint = Danger) },
            title = { Text("Leave Group?", fontWeight = FontWeight.Bold, color = Ink900) },
            text  = {
                Text(
                    "Are you sure you want to leave \"${group?.groupName}\"? You can rejoin later if there's space.",
                    color = Ink400,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            confirmButton = {
                Button(
                    onClick  = { leaveGroup() },
                    enabled  = !isLeaving,
                    colors   = ButtonDefaults.buttonColors(containerColor = Danger),
                    shape    = RoundedCornerShape(10.dp)
                ) {
                    if (isLeaving) {
                        CircularProgressIndicator(color = White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Leave")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLeaveDialog = false },
                    shape   = RoundedCornerShape(10.dp),
                    border  = BorderStroke(1.dp, Ink200)
                ) { Text("Cancel", color = Ink700) }
            },
            shape          = RoundedCornerShape(18.dp),
            containerColor = White
        )
    }
}

// ── Helpers ────────────────────────────────────────────────────────────────────
private fun formatTimeAgo(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60_000     -> "Just now"
        diff < 3_600_000  -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        else              -> "${diff / 86_400_000}d ago"
    }
}

// ── Private composables ────────────────────────────────────────────────────────
@Composable
private fun DetailStatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(0.dp),
        border    = BorderStroke(1.dp, Ink200)
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

// ── AnnouncementCard now accepts authorPhoto ───────────────────────────────────
@Composable
private fun AnnouncementCard(
    title: String,
    body: String,
    author: String,
    authorPhoto: String,        // ← NEW
    timeAgo: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(0.dp),
        border    = BorderStroke(1.dp, Ink200)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Shows actual profile picture if available
                AvatarInitials(
                    name     = author,
                    imageUrl = authorPhoto,
                    size     = 36.dp,
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

// ── MemberRow now accepts profileUrl ──────────────────────────────────────────
@Composable
private fun MemberRow(
    name: String,
    profileUrl: String,         // ← NEW
    role: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(0.dp),
        border    = BorderStroke(1.dp, Ink200)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shows actual profile picture if available
            AvatarInitials(
                name            = name,
                imageUrl        = profileUrl,
                size            = 38.dp,
                backgroundColor = if (role == "Admin") Ink900 else Ink200,
                textColor       = if (role == "Admin") White else Ink700
            )
            Spacer(Modifier.width(12.dp))
            Text(
                name,
                style      = MaterialTheme.typography.labelLarge,
                color      = Ink900,
                fontWeight = FontWeight.SemiBold,
                modifier   = Modifier.weight(1f)
            )
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (role == "Admin") Ink900 else Ink100
            ) {
                Text(
                    role,
                    style      = MaterialTheme.typography.labelSmall,
                    color      = if (role == "Admin") White else Ink600,
                    fontWeight = FontWeight.Medium,
                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}