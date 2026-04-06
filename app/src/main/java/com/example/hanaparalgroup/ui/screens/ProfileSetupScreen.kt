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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*
import com.example.hanaparalgroup.ui.viewmodel.ProfileUiState
import com.example.hanaparalgroup.ui.viewmodel.SaveUiState
import com.example.hanaparalgroup.ui.viewmodel.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: UserProfileViewModel = viewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    val saveState    by viewModel.saveState.collectAsState()

    var name      by remember { mutableStateOf("") }
    var course    by remember { mutableStateOf("") }
    var yearLevel by remember { mutableStateOf("1st Year") }
    
    var nameError   by remember { mutableStateOf("") }
    var courseError by remember { mutableStateOf("") }

    val yearOptions          = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")
    var yearDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(profileState) {
        if (profileState is ProfileUiState.Success) {
            val p = (profileState as ProfileUiState.Success).profile
            if (name.isEmpty()) name = p.name
            if (course.isEmpty()) course = p.course
            if (p.yearLevel.isNotEmpty()) yearLevel = p.yearLevel
        }
    }

    LaunchedEffect(saveState) {
        if (saveState is SaveUiState.Saved) {
            viewModel.resetSaveState()
            onSetupComplete()
        }
    }

    val isSaving  = saveState is SaveUiState.Saving
    val saveError = if (saveState is SaveUiState.Error) (saveState as SaveUiState.Error).message else ""

    fun validate(): Boolean {
        nameError   = if (name.isBlank()) "Name is required" else ""
        courseError = if (course.isBlank()) "Course is required" else ""
        return nameError.isEmpty() && courseError.isEmpty()
    }

    Box(modifier = Modifier.fillMaxSize().background(Ink50)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            
            // Header
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Ink900, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.School, null, tint = White, modifier = Modifier.size(32.dp))
            }
            
            Spacer(Modifier.height(24.dp))
            
            Text(
                text = "Welcome to HanapAral!",
                style = MaterialTheme.typography.headlineSmall,
                color = Ink900,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Let's set up your academic profile to help you find the best study groups.",
                style = MaterialTheme.typography.bodyMedium,
                color = Ink400,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(0.dp),
                border = BorderStroke(1.dp, Ink200)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BrandedTextField(
                        value = name,
                        onValueChange = { name = it; nameError = "" },
                        label = "Full Name",
                        leadingIcon = Icons.Default.Person,
                        isError = nameError.isNotEmpty(),
                        errorMessage = nameError
                    )

                    BrandedTextField(
                        value = course,
                        onValueChange = { course = it; courseError = "" },
                        label = "Course / Program",
                        leadingIcon = Icons.Default.AutoStories,
                        isError = courseError.isNotEmpty(),
                        errorMessage = courseError,
                        placeholder = "e.g. BS Computer Science"
                    )

                    ExposedDropdownMenuBox(
                        expanded = yearDropdownExpanded,
                        onExpandedChange = { yearDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = yearLevel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Year Level", style = MaterialTheme.typography.bodySmall) },
                            leadingIcon = { Icon(Icons.Default.HistoryEdu, null, tint = Ink400, modifier = Modifier.size(18.dp)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearDropdownExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Ink900,
                                unfocusedBorderColor = Ink200,
                                focusedLabelColor = Ink900,
                                unfocusedLabelColor = Ink400
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = yearDropdownExpanded,
                            onDismissRequest = { yearDropdownExpanded = false }
                        ) {
                            yearOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, style = MaterialTheme.typography.bodySmall) },
                                    onClick = { yearLevel = option; yearDropdownExpanded = false }
                                )
                            }
                        }
                    }
                }
            }

            if (saveError.isNotEmpty()) {
                Text(
                    text = saveError,
                    color = Danger,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            PrimaryButton(
                text = "Complete Setup",
                onClick = { if (validate()) viewModel.saveProfile(name, course, yearLevel) },
                icon = Icons.Default.Check,
                isLoading = isSaving,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(Modifier.height(40.dp))
        }

        if (isSaving) LoadingOverlay(message = "Finishing setup...")
    }
}
