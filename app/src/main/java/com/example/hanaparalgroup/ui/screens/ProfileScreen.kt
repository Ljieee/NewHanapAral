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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    // Placeholder data – replaced by Firestore stream (Aropo)
    val name = "Alex Aropo"
    val course = "Bachelor of Science in Computer Science"
    val yearLevel = "3rd Year"
    val email = "alex.aropo@uoc.edu.ph"
    val groupsJoined = 2
    val groupsCreated = 1

    Scaffold(
        topBar = {
            HanapAralTopBar(
                title = "My Profile",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = Surface)
                    }
                }
            )
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Hero header ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.horizontalGradient(listOf(BrandDark, Brand, GradientEnd))
                    )
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawCircle(
                        color = Surface.copy(alpha = 0.07f),
                        radius = 120f,
                        center = Offset(size.width * 0.9f, size.height * 0.2f)
                    )
                }
            }

            // ── Avatar (overlapping header) ──────────────────────────────────
            Box(
                modifier = Modifier
                    .offset(y = (-50).dp)
                    .size(100.dp)
                    .border(4.dp, Surface, CircleShape)
                    .shadow(8.dp, CircleShape)
            ) {
                AvatarInitials(
                    name = name,
                    size = 100.dp,
                    backgroundColor = Brand
                )
            }

            Spacer(Modifier.height(-40.dp)) // compensate offset

            Text(
                text = name,
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.School, contentDescription = null, tint = Action, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(yearLevel, style = MaterialTheme.typography.bodySmall, color = Action, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(20.dp))

            // ── Stats strip ──────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .background(Surface, RoundedCornerShape(20.dp))
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(value = groupsJoined.toString(), label = "Groups Joined")
                VerticalDivider(modifier = Modifier.height(40.dp), color = Divider)
                ProfileStat(value = groupsCreated.toString(), label = "Groups Created")
                VerticalDivider(modifier = Modifier.height(40.dp), color = Divider)
                ProfileStat(value = "A+", label = "Study Rating")
            }

            Spacer(Modifier.height(24.dp))

            // ── Info Card ────────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "Student Information",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(20.dp))

                    ProfileInfoRow(
                        icon = Icons.Default.Person,
                        label = "Full Name",
                        value = name
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Divider)
                    ProfileInfoRow(
                        icon = Icons.Default.Email,
                        label = "Email Address",
                        value = email
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Divider)
                    ProfileInfoRow(
                        icon = Icons.Default.School,
                        label = "Course / Program",
                        value = course
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = Divider)
                    ProfileInfoRow(
                        icon = Icons.Default.DateRange,
                        label = "Year Level",
                        value = yearLevel
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Enrolled Groups Card ─────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "My Study Groups",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(16.dp))
                    listOf(
                        "Algorithm Avengers" to "Data Structures",
                        "DB Detectives" to "Database Management"
                    ).forEach { (name, subject) ->
                        GroupMiniRow(name = name, subject = subject)
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Edit Profile Button ──────────────────────────────────────────
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
            style = MaterialTheme.typography.headlineLarge,
            color = Brand,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
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
                .size(36.dp)
                .background(Brand.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Brand, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun GroupMiniRow(name: String, subject: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceAlt, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Action.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Groups, contentDescription = null, tint = Action, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
            Text(subject, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextHint, modifier = Modifier.size(18.dp))
    }
}