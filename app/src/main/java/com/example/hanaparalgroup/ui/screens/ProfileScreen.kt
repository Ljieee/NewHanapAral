package com.example.hanaparalgroup.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.example.hanaparalgroup.data.models.UserProfile
import com.example.hanaparalgroup.data.repository.UserProfileRepository
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    // ── State ─────────────────────────────────────────────────────────────────
    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // ── Real-time listener — removed when screen leaves composition ───────────
    DisposableEffect(Unit) {
        val uid = UserProfileRepository.currentUid
        if (uid == null) {
            isLoading = false
            return@DisposableEffect onDispose {}
        }

        val registration = UserProfileRepository.listenToProfile(
            uid      = uid,
            onUpdate = { updatedProfile ->
                profile   = updatedProfile
                isLoading = false
            },
            onError  = {
                isLoading = false
            }
        )

        onDispose { registration.remove() }
    }

    // ── Derived display values (fallback to empty string while loading) ────────
    val name       = profile?.name      ?: ""
    val course     = profile?.course    ?: ""
    val yearLevel  = profile?.yearLevel ?: ""
    val email      = profile?.email     ?: ""

    // Placeholder stats — Balanag will wire real group counts from Firestore
    val groupsJoined  = 2
    val groupsCreated = 1

    Scaffold(
        topBar = {
            HanapAralTopBar(
                title = "Profile",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = White)
                    }
                }
            )
        },
        containerColor = Ink50
    ) { padding ->

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Ink900)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Header ───────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Ink900)
                    .padding(bottom = 52.dp, top = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AvatarInitials(
                        name = name.ifEmpty { "?" },
                        size = 76.dp,
                        backgroundColor = White.copy(alpha = 0.12f),
                        textColor = White
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = name.ifEmpty { "—" },
                        style = MaterialTheme.typography.headlineMedium,
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, tint = White.copy(alpha = 0.4f), modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(yearLevel.ifEmpty { "—" }, style = MaterialTheme.typography.bodySmall, color = White.copy(alpha = 0.4f))
                    }
                }
            }

            // ── Stats strip ──────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(0.dp),
                border = BorderStroke(1.dp, Ink200)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStat(value = groupsJoined.toString(), label = "Joined")
                    VerticalDivider(modifier = Modifier.height(36.dp), color = Ink200)
                    ProfileStat(value = groupsCreated.toString(), label = "Created")
                    VerticalDivider(modifier = Modifier.height(36.dp), color = Ink200)
                    ProfileStat(value = "A+", label = "Rating")
                }
            }

            Spacer(Modifier.height(-12.dp))

            // ── Info Card ────────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(0.dp),
                border = BorderStroke(1.dp, Ink200)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Student Information",
                        style = MaterialTheme.typography.titleSmall,
                        color = Ink400,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(16.dp))

                    ProfileInfoRow(icon = Icons.Default.Person,    label = "Full Name",   value = name.ifEmpty { "—" })
                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Ink100)
                    ProfileInfoRow(icon = Icons.Default.Email,     label = "Email",       value = email.ifEmpty { "—" })
                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Ink100)
                    ProfileInfoRow(icon = Icons.Default.School,    label = "Course",      value = course.ifEmpty { "—" })
                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Ink100)
                    ProfileInfoRow(icon = Icons.Default.DateRange, label = "Year Level",  value = yearLevel.ifEmpty { "—" })
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── My Study Groups Card ─────────────────────────────────────────
            // Balanag: replace hardcoded list with real Firestore group data
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(0.dp),
                border = BorderStroke(1.dp, Ink200)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "My Study Groups",
                        style = MaterialTheme.typography.titleSmall,
                        color = Ink400,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(14.dp))
                    listOf(
                        "Algorithm Avengers" to "Data Structures",
                        "DB Detectives"      to "Database Management"
                    ).forEach { (gName, subject) ->
                        GroupMiniRow(name = gName, subject = subject)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            PrimaryButton(
                text = "Edit Profile",
                onClick = onNavigateToEdit,
                icon = Icons.Default.Edit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = Ink900,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Ink400
        )
    }
}

@Composable
private fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(Ink100, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Ink400, modifier = Modifier.size(17.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Ink400)
            Spacer(Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.bodySmall, color = Ink900, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun GroupMiniRow(name: String, subject: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Ink50, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(Ink100, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Groups, contentDescription = null, tint = Ink600, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.labelMedium, color = Ink900, fontWeight = FontWeight.SemiBold)
            Text(subject, style = MaterialTheme.typography.labelSmall, color = Ink400)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Ink300, modifier = Modifier.size(16.dp))
    }
}