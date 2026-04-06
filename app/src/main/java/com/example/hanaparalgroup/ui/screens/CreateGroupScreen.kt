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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.hanaparalgroup.data.models.StudyGroup
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    onNavigateBack: () -> Unit,
    onGroupCreated: () -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var groupNameError by remember { mutableStateOf("") }
    var subjectError by remember { mutableStateOf("") }

    // Read max members from Remote Config (default 15)
    val remoteConfig = Firebase.remoteConfig
    val maxMembersFromConfig = remoteConfig.getLong("max_members_per_group").toInt()
        .let { if (it <= 0) 15 else it }
    var maxMembers by remember { mutableStateOf(maxMembersFromConfig) }

    // Read group creation toggle from Remote Config
    val groupCreationEnabled = remoteConfig.getBoolean("group_creation_enabled")
        .let { true } // default true if not set

    val scope = rememberCoroutineScope()

    val subjectOptions = listOf(
        "Data Structures & Algorithms", "Web Development", "Database Management",
        "Object-Oriented Programming", "Computer Networks", "Discrete Mathematics",
        "Mobile Development", "Operating Systems", "Software Engineering", "Other"
    )
    var subjectDropdownExpanded by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        groupNameError = when {
            groupName.isBlank()     -> "Group name is required"
            groupName.length < 3    -> "Name must be at least 3 characters"
            groupName.length > 50   -> "Name must be under 50 characters"
            else                    -> ""
        }
        subjectError = if (subject.isBlank()) "Please select or enter a subject" else ""
        return groupNameError.isEmpty() && subjectError.isEmpty()
    }

    Scaffold(
        topBar = {
            HanapAralTopBar(
                title = "Create Group",
                onNavigateBack = onNavigateBack
            )
        },
        containerColor = Ink50
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Ink900, RoundedCornerShape(16.dp))
                        .padding(horizontal = 24.dp, vertical = 22.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Start a Study Group",
                                style = MaterialTheme.typography.titleMedium,
                                color = White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                "You'll automatically become the group admin",
                                style = MaterialTheme.typography.bodySmall,
                                color = White.copy(alpha = 0.5f)
                            )
                        }
                        Icon(
                            Icons.Default.Groups,
                            contentDescription = null,
                            tint = White.copy(alpha = 0.15f),
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }

                // Form card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border = BorderStroke(1.dp, Ink200)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Group Details",
                            style = MaterialTheme.typography.titleSmall,
                            color = Ink400,
                            fontWeight = FontWeight.SemiBold
                        )

                        BrandedTextField(
                            value = groupName,
                            onValueChange = { groupName = it; groupNameError = "" },
                            label = "Group Name *",
                            leadingIcon = Icons.Default.Groups,
                            isError = groupNameError.isNotEmpty(),
                            errorMessage = groupNameError,
                            placeholder = "e.g. Algorithm Avengers"
                        )
                        Text(
                            "${groupName.length}/50",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (groupName.length > 50) Danger else Ink300,
                            modifier = Modifier.align(Alignment.End)
                        )

                        ExposedDropdownMenuBox(
                            expanded = subjectDropdownExpanded,
                            onExpandedChange = { subjectDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = subject,
                                onValueChange = { subject = it; subjectError = "" },
                                label = { Text("Subject / Topic *", style = MaterialTheme.typography.bodySmall) },
                                leadingIcon = {
                                    Icon(Icons.Default.Book, null, tint = if (subjectError.isNotEmpty()) Danger else Ink400, modifier = Modifier.size(18.dp))
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectDropdownExpanded)
                                },
                                isError = subjectError.isNotEmpty(),
                                placeholder = { Text("Select or type a subject", color = Ink300, style = MaterialTheme.typography.bodySmall) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryEditable),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Ink900,
                                    unfocusedBorderColor = Ink200,
                                    errorBorderColor = Danger,
                                    focusedLabelColor = Ink900
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = subjectDropdownExpanded,
                                onDismissRequest = { subjectDropdownExpanded = false }
                            ) {
                                subjectOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, style = MaterialTheme.typography.bodySmall) },
                                        onClick = {
                                            subject = option
                                            subjectDropdownExpanded = false
                                            subjectError = ""
                                        }
                                    )
                                }
                            }
                        }
                        if (subjectError.isNotEmpty()) {
                            Text(subjectError, color = Danger, style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(start = 14.dp))
                        }

                        BrandedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = "Description (optional)",
                            leadingIcon = Icons.Default.Description,
                            singleLine = false,
                            maxLines = 4,
                            placeholder = "What will this group study? Any specific goals?"
                        )
                    }
                }

                // Settings Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border = BorderStroke(1.dp, Ink200)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Group Settings",
                            style = MaterialTheme.typography.titleSmall,
                            color = Ink400,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Group, null, tint = Ink400, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Max Members", style = MaterialTheme.typography.labelMedium, color = Ink900, fontWeight = FontWeight.Medium)
                            }
                            Surface(shape = RoundedCornerShape(20.dp), color = Ink100) {
                                Text(
                                    "$maxMembers members",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Ink700,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(3.dp))
                        Text(
                            "Controlled by Remote Config — Superuser can override",
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink300,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                        Spacer(Modifier.height(10.dp))
                        Slider(
                            value = maxMembers.toFloat(),
                            onValueChange = { maxMembers = it.toInt() },
                            valueRange = 5f..maxMembersFromConfig.toFloat().coerceAtLeast(30f),
                            steps = (maxMembersFromConfig.coerceAtLeast(30) - 5 - 1).coerceAtLeast(0),
                            colors = SliderDefaults.colors(
                                thumbColor = Ink900,
                                activeTrackColor = Ink900,
                                inactiveTrackColor = Ink200
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("5", style = MaterialTheme.typography.labelSmall, color = Ink300)
                            Text("$maxMembersFromConfig", style = MaterialTheme.typography.labelSmall, color = Ink300)
                        }
                    }
                }

                // Admin notice
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AccentSoft),
                    border = BorderStroke(1.dp, Accent.copy(alpha = 0.2f)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, null, tint = Accent, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("You'll be the Admin", style = MaterialTheme.typography.labelMedium, color = Accent, fontWeight = FontWeight.Bold)
                            Text("Admins can post announcements and manage members.", style = MaterialTheme.typography.labelSmall, color = Accent.copy(alpha = 0.7f))
                        }
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = Danger, style = MaterialTheme.typography.labelSmall)
                }

                // Create Button
                PrimaryButton(
                    text = "Create Group",
                    onClick = {
                        if (validate()) {
                            isCreating = true
                            errorMessage = ""
                            val uid = Firebase.auth.currentUser?.uid ?: return@PrimaryButton
                            val groupId = Firebase.firestore.collection("groups").document().id
                            val newGroup = StudyGroup(
                                groupId     = groupId,
                                groupName   = groupName.trim(),
                                description = subject.trim() + if (description.isNotBlank()) " — ${description.trim()}" else "",
                                adminId     = uid,
                                members     = listOf(uid),
                                maxMembers  = maxMembers,
                                createdAt   = System.currentTimeMillis()
                            )
                            scope.launch {
                                try {
                                    Firebase.firestore.collection("groups")
                                        .document(groupId)
                                        .set(newGroup)
                                        .await()
                                    isCreating = false
                                    onGroupCreated()
                                } catch (e: Exception) {
                                    errorMessage = "Failed to create group: ${e.localizedMessage}"
                                    isCreating = false
                                }
                            }
                        }
                    },
                    icon = Icons.Default.Add,
                    isLoading = isCreating,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))
            }

            if (isCreating) LoadingOverlay(message = "Creating your group…")
        }
    }
}