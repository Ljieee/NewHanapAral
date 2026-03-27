package com.example.hanaparalgroup.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Joined", "Open", "Full")

    val allGroups = listOf(
        GroupPreview("1", "Algorithm Avengers", "Data Structures & Algorithms", 8, 15, "Juan dela Cruz", true),
        GroupPreview("2", "Web Wizards", "Web Development", 12, 15, "Maria Santos", false),
        GroupPreview("3", "DB Detectives", "Database Management", 5, 10, "Carlos Reyes", true),
        GroupPreview("4", "OOP Olympians", "Object-Oriented Programming", 14, 15, "Ana Gomez", false),
        GroupPreview("5", "Network Ninjas", "Computer Networks", 15, 15, "Ben Flores", false),
        GroupPreview("6", "Logic Lords", "Discrete Math", 3, 12, "Ina Cruz", false),
        GroupPreview("7", "Mobile Makers", "Mobile Development", 7, 10, "Ray Tan", false),
    )

    val filtered = allGroups.filter { group ->
        val matchesSearch = searchQuery.isEmpty() ||
                group.name.contains(searchQuery, ignoreCase = true) ||
                group.subject.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Joined" -> group.isJoined
            "Open"   -> !group.isJoined && group.memberCount < group.maxMembers
            "Full"   -> group.memberCount >= group.maxMembers
            else     -> true
        }
        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = {
            HanapAralTopBar(
                title = "Study Groups",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = onNavigateToCreate) {
                        Icon(Icons.Default.Add, contentDescription = "Create Group", tint = Surface)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = Action,
                contentColor = Surface,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Group")
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
            // Search bar
            item {
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search groups or subjects…", color = TextHint) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Brand) },
                    trailingIcon = {
                        AnimatedVisibility(visible = searchQuery.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null, tint = TextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Brand,
                        unfocusedBorderColor = Divider,
                        focusedContainerColor = Surface,
                        unfocusedContainerColor = Surface
                    )
                )
            }

            // Filter chips
            item {
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { filter ->
                        val isSelected = selectedFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, style = MaterialTheme.typography.labelLarge) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Brand,
                                selectedLabelColor = Surface,
                                selectedLeadingIconColor = Surface,
                                containerColor = Surface,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = Brand,
                                borderColor = Divider
                            )
                        )
                    }
                }
            }

            // Count label
            item {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filtered.size} group${if (filtered.size != 1) "s" else ""} found",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    if (searchQuery.isNotEmpty() || selectedFilter != "All") {
                        TextButton(onClick = { searchQuery = ""; selectedFilter = "All" }) {
                            Text("Clear filters", color = Alert, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            // Groups
            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            icon = Icons.Outlined.SearchOff,
                            title = "No Groups Found",
                            subtitle = "Try adjusting your search or filters.",
                            action = "Clear Filters",
                            onAction = { searchQuery = ""; selectedFilter = "All" }
                        )
                    }
                }
            } else {
                items(filtered, key = { it.id }) { group ->
                    StudyGroupCard(
                        groupName = group.name,
                        subject = group.subject,
                        memberCount = group.memberCount,
                        maxMembers = group.maxMembers,
                        adminName = group.adminName,
                        isJoined = group.isJoined,
                        onClick = { onNavigateToDetail(group.id) },
                        onJoinClick = { /* Balanag: join logic */ },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}