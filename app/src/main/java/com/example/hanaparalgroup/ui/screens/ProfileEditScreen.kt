package com.example.hanaparalgroup.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit
) {
    // Form state – populated from Firestore on real implementation
    var name by remember { mutableStateOf("Alex Aropo") }
    var course by remember { mutableStateOf("Bachelor of Science in Computer Science") }
    var yearLevel by remember { mutableStateOf("3rd Year") }
    var email by remember { mutableStateOf("alex.aropo@uoc.edu.ph") }

    var nameError by remember { mutableStateOf("") }
    var courseError by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val yearOptions = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")
    var yearDropdownExpanded by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        nameError = if (name.isBlank()) "Name is required" else ""
        courseError = if (course.isBlank()) "Course is required" else ""
        return nameError.isEmpty() && courseError.isEmpty()
    }

    Scaffold(
        topBar = {
            HanapAralTopBar(
                title = "Edit Profile",
                onNavigateBack = onNavigateBack,
                actions = {
                    TextButton(onClick = {
                        if (validate()) {
                            // Aropo: Firestore save logic here
                            onSaved()
                        }
                    }) {
                        Text("Save", color = ActionLight, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
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
                // ── Avatar section ───────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            AvatarInitials(name = name.ifEmpty { "?" }, size = 90.dp)
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Action, CircleShape)
                                    .border(2.dp, Surface, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Surface,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Tap to update photo",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Text(
                            "Profile initials auto-generated from name",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextHint
                        )
                    }
                }

                // ── Form Card ────────────────────────────────────────────────
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
                            "Personal Information",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        // Full name
                        BrandedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                                if (nameError.isNotEmpty()) nameError = ""
                            },
                            label = "Full Name *",
                            leadingIcon = Icons.Default.Person,
                            isError = nameError.isNotEmpty(),
                            errorMessage = nameError,
                            placeholder = "e.g. Juan dela Cruz"
                        )

                        // Email (read-only, from Google)
                        BrandedTextField(
                            value = email,
                            onValueChange = {},
                            label = "Email Address",
                            leadingIcon = Icons.Default.Email,
                            readOnly = true,
                            enabled = false,
                            trailingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = TextHint, modifier = Modifier.size(18.dp))
                            }
                        )
                        Text(
                            "Email is managed by your Google account",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextHint,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                // ── Academic Info Card ───────────────────────────────────────
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
                            "Academic Information",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        // Course
                        BrandedTextField(
                            value = course,
                            onValueChange = {
                                course = it
                                if (courseError.isNotEmpty()) courseError = ""
                            },
                            label = "Course / Program *",
                            leadingIcon = Icons.Default.School,
                            isError = courseError.isNotEmpty(),
                            errorMessage = courseError,
                            placeholder = "e.g. BS Computer Science",
                            singleLine = false,
                            maxLines = 2
                        )

                        // Year level dropdown
                        ExposedDropdownMenuBox(
                            expanded = yearDropdownExpanded,
                            onExpandedChange = { yearDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = yearLevel,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Year Level") },
                                leadingIcon = {
                                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Brand)
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearDropdownExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Brand,
                                    unfocusedBorderColor = Divider,
                                    focusedLabelColor = Brand,
                                    unfocusedLabelColor = TextSecondary
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = yearDropdownExpanded,
                                onDismissRequest = { yearDropdownExpanded = false }
                            ) {
                                yearOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, style = MaterialTheme.typography.bodyMedium) },
                                        onClick = {
                                            yearLevel = option
                                            yearDropdownExpanded = false
                                        },
                                        leadingIcon = {
                                            if (yearLevel == option) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Action)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Save Button ──────────────────────────────────────────────
                PrimaryButton(
                    text = "Save Changes",
                    onClick = {
                        if (validate()) {
                            isSaving = true
                            // Aropo: save to Firestore
                            onSaved()
                        }
                    },
                    icon = Icons.Default.Save,
                    isLoading = isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                // ── Danger zone ──────────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Alert.copy(alpha = 0.06f)),
                    border = BorderStroke(1.dp, Alert.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Danger Zone",
                            style = MaterialTheme.typography.titleSmall,
                            color = Alert,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { /* Galang: sign-out logic */ },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Alert),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Alert)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Sign Out", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
            }

            if (isSaving) LoadingOverlay(message = "Saving profile...")
        }
    }
}