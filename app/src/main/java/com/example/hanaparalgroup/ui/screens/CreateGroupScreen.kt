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
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    onNavigateBack: () -> Unit,
    onGroupCreated: () -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var maxMembers by remember { mutableStateOf(15) }
    var isCreating by remember { mutableStateOf(false) }

    var groupNameError by remember { mutableStateOf("") }
    var subjectError by remember { mutableStateOf("") }

    val subjectOptions = listOf(
        "Data Structures & Algorithms", "Web Development", "Database Management",
        "Object-Oriented Programming", "Computer Networks", "Discrete Mathematics",
        "Mobile Development", "Operating Systems", "Software Engineering", "Other"
    )
    var subjectDropdownExpanded by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        groupNameError = when {
            groupName.isBlank() -> "Group name is required"
            groupName.length < 3 -> "Name must be at least 3 characters"
            groupName.length > 50 -> "Name must be under 50 characters"
            else -> ""
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
        containerColor = Background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Illustration banner ──────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Brand)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Start a Study Group",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Surface,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "You'll automatically become the group admin",
                                style = MaterialTheme.typography.bodySmall,
                                color = Surface.copy(alpha = 0.75f)
                            )
                        }
                        Icon(
                            Icons.Default.Groups,
                            contentDescription = null,
                            tint = Surface.copy(alpha = 0.2f),
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                // ── Form card ────────────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(
                            "Group Details",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        // Group name
                        BrandedTextField(
                            value = groupName,
                            onValueChange = { groupName = it; groupNameError = "" },
                            label = "Group Name *",
                            leadingIcon = Icons.Default.Groups,
                            isError = groupNameError.isNotEmpty(),
                            errorMessage = groupNameError,
                            placeholder = "e.g. Algorithm Avengers"
                        )
                        // Character counter
                        Text(
                            "${groupName.length}/50",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (groupName.length > 50) Alert else TextHint,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(end = 4.dp)
                        )

                        // Subject dropdown
                        ExposedDropdownMenuBox(
                            expanded = subjectDropdownExpanded,
                            onExpandedChange = { subjectDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = subject,
                                onValueChange = { subject = it; subjectError = "" },
                                label = { Text("Subject / Topic *") },
                                leadingIcon = {
                                    Icon(Icons.Default.Book, null, tint = if (subjectError.isNotEmpty()) Alert else Brand)
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectDropdownExpanded)
                                },
                                isError = subjectError.isNotEmpty(),
                                placeholder = { Text("Select or type a subject", color = TextHint) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Brand,
                                    unfocusedBorderColor = Divider,
                                    errorBorderColor = Alert,
                                    focusedLabelColor = Brand
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = subjectDropdownExpanded,
                                onDismissRequest = { subjectDropdownExpanded = false }
                            ) {
                                subjectOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, style = MaterialTheme.typography.bodyMedium) },
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
                            Text(subjectError, color = Alert, style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(start = 16.dp))
                        }

                        // Description (optional)
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

                // ── Settings Card ────────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            "Group Settings",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(20.dp))

                        // Max members slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Group, null, tint = Brand, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Max Members", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Brand.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    "$maxMembers members",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Brand,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }
                        Text(
                            "Controlled by Remote Config – override available for Superuser",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextHint,
                            modifier = Modifier.padding(start = 26.dp, top = 2.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Slider(
                            value = maxMembers.toFloat(),
                            onValueChange = { maxMembers = it.toInt() },
                            valueRange = 5f..30f,
                            steps = 24,
                            colors = SliderDefaults.colors(
                                thumbColor = Brand,
                                activeTrackColor = Brand,
                                inactiveTrackColor = SurfaceAlt
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("5", style = MaterialTheme.typography.labelSmall, color = TextHint)
                            Text("30", style = MaterialTheme.typography.labelSmall, color = TextHint)
                        }
                    }
                }

                // ── Admin badge notice ───────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Action.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, Action.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, null, tint = Action, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("You'll be the Admin", style = MaterialTheme.typography.titleSmall, color = Action, fontWeight = FontWeight.Bold)
                            Text("Admins can post announcements and manage members.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }

                // ── Create Button ────────────────────────────────────────────
                ActionButton(
                    text = "Create Group",
                    onClick = {
                        if (validate()) {
                            isCreating = true
                            // Balanag: Firestore create logic
                            onGroupCreated()
                        }
                    },
                    icon = Icons.Default.Add,
                    isLoading = isCreating,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(32.dp))
            }

            if (isCreating) LoadingOverlay(message = "Creating your group…")
        }
    }
}