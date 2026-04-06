package com.example.hanaparalgroup.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*
import com.example.hanaparalgroup.ui.viewmodel.ProfileUiState
import com.example.hanaparalgroup.ui.viewmodel.SaveUiState
import com.example.hanaparalgroup.ui.viewmodel.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    onNavigateBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: UserProfileViewModel = viewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    val saveState    by viewModel.saveState.collectAsState()

    var name      by remember { mutableStateOf("") }
    var course    by remember { mutableStateOf("") }
    var yearLevel by remember { mutableStateOf("3rd Year") }
    var email     by remember { mutableStateOf("") }
    var imageUrl  by remember { mutableStateOf("") }

    var nameError   by remember { mutableStateOf("") }
    var courseError by remember { mutableStateOf("") }

    val yearOptions          = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")
    var yearDropdownExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Image Picker
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadProfilePicture(it, context) }
    }

    LaunchedEffect(profileState) {
        if (profileState is ProfileUiState.Success) {
            val p = (profileState as ProfileUiState.Success).profile
            name      = p.name
            course    = p.course
            yearLevel = p.yearLevel.ifEmpty { "3rd Year" }
            email     = p.email
            imageUrl  = p.profilePictureUrl
        }
    }

    LaunchedEffect(saveState) {
        if (saveState is SaveUiState.Saved) {
            viewModel.resetSaveState()
            onSaved()
        }
    }

    val isSaving  = saveState is SaveUiState.Saving
    val saveError = if (saveState is SaveUiState.Error) (saveState as SaveUiState.Error).message else ""

    fun validate(): Boolean {
        nameError   = if (name.isBlank()) "Name is required" else ""
        courseError = if (course.isBlank()) "Course is required" else ""
        return nameError.isEmpty() && courseError.isEmpty()
    }

    Scaffold(
        topBar = {
            HanapAralTopBar(
                title = "Edit Profile",
                onNavigateBack = onNavigateBack,
                actions = {
                    TextButton(onClick = { if (validate()) viewModel.saveProfile(name, course, yearLevel) }) {
                        Text("Save", color = White, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    }
                }
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
                // Avatar
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border    = BorderStroke(1.dp, Ink200)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { launcher.launch("image/*") }
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            AvatarInitials(
                                name = name.ifEmpty { "?" },
                                imageUrl = imageUrl,
                                size = 80.dp,
                                backgroundColor = Ink900
                            )
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(White, CircleShape)
                                    .border(1.dp, Ink200, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Ink600, modifier = Modifier.size(13.dp))
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Text("Tap to update photo", style = MaterialTheme.typography.labelSmall, color = Ink400)
                    }
                }

                // Personal Info
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border    = BorderStroke(1.dp, Ink200)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Personal Information", style = MaterialTheme.typography.titleSmall, color = Ink400, fontWeight = FontWeight.SemiBold)

                        BrandedTextField(
                            value         = name,
                            onValueChange = { name = it; if (nameError.isNotEmpty()) nameError = "" },
                            label         = "Full Name *",
                            leadingIcon   = Icons.Default.Person,
                            isError       = nameError.isNotEmpty(),
                            errorMessage  = nameError,
                            placeholder   = "e.g. Juan dela Cruz"
                        )

                        BrandedTextField(
                            value         = email,
                            onValueChange = {},
                            label         = "Email Address",
                            leadingIcon   = Icons.Default.Email,
                            readOnly      = true,
                            enabled       = false,
                            trailingIcon  = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Ink300, modifier = Modifier.size(16.dp))
                            }
                        )
                        Text("Email is managed by your Google account", style = MaterialTheme.typography.labelSmall, color = Ink300, modifier = Modifier.padding(start = 4.dp))
                    }
                }

                // Academic Info
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border    = BorderStroke(1.dp, Ink200)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Academic Information", style = MaterialTheme.typography.titleSmall, color = Ink400, fontWeight = FontWeight.SemiBold)

                        BrandedTextField(
                            value         = course,
                            onValueChange = { course = it; if (courseError.isNotEmpty()) courseError = "" },
                            label         = "Course / Program *",
                            leadingIcon   = Icons.Default.School,
                            isError       = courseError.isNotEmpty(),
                            errorMessage  = courseError,
                            placeholder   = "e.g. BS Computer Science",
                            singleLine    = false,
                            maxLines      = 2
                        )

                        ExposedDropdownMenuBox(
                            expanded          = yearDropdownExpanded,
                            onExpandedChange  = { yearDropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value       = yearLevel,
                                onValueChange = {},
                                readOnly    = true,
                                label       = { Text("Year Level", style = MaterialTheme.typography.bodySmall) },
                                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Ink400, modifier = Modifier.size(18.dp)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearDropdownExpanded) },
                                modifier    = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                shape       = RoundedCornerShape(10.dp),
                                colors      = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor   = Ink900,
                                    unfocusedBorderColor = Ink200,
                                    focusedLabelColor    = Ink900,
                                    unfocusedLabelColor  = Ink400
                                )
                            )
                            ExposedDropdownMenu(
                                expanded         = yearDropdownExpanded,
                                onDismissRequest = { yearDropdownExpanded = false }
                            ) {
                                yearOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text        = { Text(option, style = MaterialTheme.typography.bodySmall) },
                                        onClick     = { yearLevel = option; yearDropdownExpanded = false },
                                        leadingIcon = {
                                            if (yearLevel == option)
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Ink900, modifier = Modifier.size(16.dp))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                if (saveError.isNotEmpty()) {
                    Text(saveError, color = Danger, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 4.dp))
                }

                PrimaryButton(
                    text      = "Save Changes",
                    onClick   = { if (validate()) viewModel.saveProfile(name, course, yearLevel) },
                    icon      = Icons.Default.Check,
                    isLoading = isSaving,
                    modifier  = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))
            }

            if (isSaving) LoadingOverlay(message = "Saving profile...")
        }
    }
}