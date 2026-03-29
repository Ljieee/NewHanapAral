package com.example.hanaparalgroup.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hanaparalgroup.data.models.UserProfile
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*
import com.example.hanaparalgroup.ui.viewmodel.ProfileUiState
import com.example.hanaparalgroup.ui.viewmodel.UserProfileViewModel

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    viewModel: UserProfileViewModel = viewModel()
) {
    val uiState by viewModel.profileState.collectAsState()

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
        when (val state = uiState) {

            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Ink900) }
            }

            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = Danger, style = MaterialTheme.typography.bodyMedium)
                }
            }

            is ProfileUiState.Success -> {
                ProfileContent(
                    profile          = state.profile,
                    padding          = padding,
                    onNavigateToEdit = onNavigateToEdit
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: UserProfile,
    padding: PaddingValues,
    onNavigateToEdit: () -> Unit
) {
    // Balanag: replace these with real Firestore group counts
    val groupsJoined  = 2
    val groupsCreated = 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Header ───────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Ink900)
                .padding(bottom = 52.dp, top = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AvatarInitials(
                    name            = profile.name.ifEmpty { "?" },
                    size            = 76.dp,
                    backgroundColor = White.copy(alpha = 0.12f),
                    textColor       = White
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    text       = profile.name.ifEmpty { "—" },
                    style      = MaterialTheme.typography.headlineMedium,
                    color      = White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = null, tint = White.copy(alpha = 0.4f), modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(profile.yearLevel.ifEmpty { "—" }, style = MaterialTheme.typography.bodySmall, color = White.copy(alpha = 0.4f))
                }
            }
        }

        // ── Stats strip ───────────────────────────────────────────────────────
        Card(
            modifier  = Modifier.fillMaxWidth().offset(y = (-24).dp).padding(horizontal = 20.dp),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(0.dp),
            border    = BorderStroke(1.dp, Ink200)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(value = groupsJoined.toString(),  label = "Joined")
                VerticalDivider(modifier = Modifier.height(36.dp), color = Ink200)
                ProfileStat(value = groupsCreated.toString(), label = "Created")
                VerticalDivider(modifier = Modifier.height(36.dp), color = Ink200)
                ProfileStat(value = "A+", label = "Rating")
            }
        }

        Spacer(Modifier.height(-12.dp))

        // ── Student Info Card ─────────────────────────────────────────────────
        Card(
            modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(0.dp),
            border    = BorderStroke(1.dp, Ink200)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Student Information", style = MaterialTheme.typography.titleSmall, color = Ink400, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(16.dp))
                ProfileInfoRow(Icons.Default.Person,    "Full Name",  profile.name.ifEmpty { "—" })
                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Ink100)
                ProfileInfoRow(Icons.Default.Email,     "Email",      profile.email.ifEmpty { "—" })
                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Ink100)
                ProfileInfoRow(Icons.Default.School,    "Course",     profile.course.ifEmpty { "—" })
                HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Ink100)
                ProfileInfoRow(Icons.Default.DateRange, "Year Level", profile.yearLevel.ifEmpty { "—" })
            }
        }

        Spacer(Modifier.height(14.dp))

        // ── My Study Groups — Balanag: replace with live Firestore data ───────
        Card(
            modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            shape     = RoundedCornerShape(16.dp),
            colors    = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(0.dp),
            border    = BorderStroke(1.dp, Ink200)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("My Study Groups", style = MaterialTheme.typography.titleSmall, color = Ink400, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(14.dp))
                listOf("Algorithm Avengers" to "Data Structures", "DB Detectives" to "Database Management")
                    .forEach { (gName, subject) ->
                        GroupMiniRow(name = gName, subject = subject)
                        Spacer(Modifier.height(8.dp))
                    }
            }
        }

        Spacer(Modifier.height(14.dp))

        PrimaryButton(
            text     = "Edit Profile",
            onClick  = onNavigateToEdit,
            icon     = Icons.Default.Edit,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = Ink900, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Ink400)
    }
}

@Composable
private fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(34.dp).background(Ink100, RoundedCornerShape(10.dp)),
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
            modifier = Modifier.size(34.dp).background(Ink100, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Groups, contentDescription = null, tint = Ink600, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name,    style = MaterialTheme.typography.labelMedium, color = Ink900, fontWeight = FontWeight.SemiBold)
            Text(subject, style = MaterialTheme.typography.labelSmall,  color = Ink400)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Ink300, modifier = Modifier.size(16.dp))
    }
}