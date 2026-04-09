package com.example.hanaparalgroup.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import com.example.hanaparalgroup.data.models.StudyGroup
import com.example.hanaparalgroup.ui.components.*
import com.example.hanaparalgroup.ui.theme.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    var searchQuery    by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Joined", "Open", "Full")

    var groups    by remember { mutableStateOf<List<StudyGroup>>(emptyList()) }
    var adminNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var joiningIds by remember { mutableStateOf<Set<String>>(emptySet()) }

    val currentUid = Firebase.auth.currentUser?.uid ?: ""
    val scope = rememberCoroutineScope()

    // ── Real-time listener ─────────────────────────────────────────────────────
    DisposableEffect(Unit) {
        val listener = Firebase.firestore.collection("groups")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    groups = snapshot.documents.mapNotNull {
                        it.toObject(StudyGroup::class.java)
                    }
                    isLoading = false
                }
            }
        onDispose { listener.remove() }
    }

    // ── Load admin names when groups change ─────────────────────────────────────
    LaunchedEffect(groups) {
        val names = mutableMapOf<String, String>()
        for (group in groups) {
            if (!names.containsKey(group.adminId)) {
                try {
                    val userDoc = Firebase.firestore.collection("users").document(group.adminId).get().await()
                    val name = userDoc.getString("name") ?: "Unknown Admin"
                    names[group.adminId] = name
                } catch (_: Exception) {
                    names[group.adminId] = "Unknown Admin"
                }
            }
        }
        adminNames = names
    }

    // ── Filtered list ──────────────────────────────────────────────────────────
    val filtered = groups.filter { group ->
        val isJoined     = group.members.contains(currentUid)
        val matchesSearch = searchQuery.isEmpty() ||
                group.groupName.contains(searchQuery, ignoreCase = true) ||
                group.description.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Joined" -> isJoined
            "Open"   -> !isJoined && group.members.size < group.maxMembers
            "Full"   -> group.members.size >= group.maxMembers
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
                        Icon(Icons.Default.Add, contentDescription = "Create Group", tint = White)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onNavigateToCreate,
                containerColor = Ink900,
                contentColor   = White,
                shape          = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Group")
            }
        },
        containerColor = Ink50
    ) { padding ->
        if (isLoading) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = Ink900) }
        } else {
            LazyColumn(
                modifier       = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Search bar
                item {
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value        = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder  = {
                            Text(
                                "Search groups…",
                                color = Ink300,
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        leadingIcon  = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint     = Ink400,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            AnimatedVisibility(
                                visible = searchQuery.isNotEmpty(),
                                enter   = fadeIn(),
                                exit    = fadeOut()
                            ) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = null,
                                        tint     = Ink400,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape      = RoundedCornerShape(12.dp),
                        modifier   = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = Ink900,
                            unfocusedBorderColor = Ink200,
                            focusedContainerColor   = White,
                            unfocusedContainerColor = White
                        )
                    )
                }

                // Filter chips
                item {
                    Spacer(Modifier.height(12.dp))
                    LazyRow(
                        contentPadding        = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filters) { filter ->
                            val isSelected = selectedFilter == filter
                            FilterChip(
                                selected = isSelected,
                                onClick  = { selectedFilter = filter },
                                label    = {
                                    Text(filter, style = MaterialTheme.typography.labelMedium)
                                },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Ink900,
                                    selectedLabelColor     = White,
                                    selectedLeadingIconColor = White,
                                    containerColor = White,
                                    labelColor     = Ink400
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled             = true,
                                    selected            = isSelected,
                                    selectedBorderColor = Ink900,
                                    borderColor         = Ink200
                                )
                            )
                        }
                    }
                }

                // Result count + clear
                item {
                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text  = "${filtered.size} group${if (filtered.size != 1) "s" else ""} found",
                            style = MaterialTheme.typography.labelSmall,
                            color = Ink400
                        )
                        if (searchQuery.isNotEmpty() || selectedFilter != "All") {
                            TextButton(onClick = {
                                searchQuery    = ""
                                selectedFilter = "All"
                            }) {
                                Text("Clear", color = Danger, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }

                // Empty state
                if (filtered.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyState(
                                icon     = Icons.Outlined.SearchOff,
                                title    = "No groups found",
                                subtitle = "Try adjusting your search or filters, or create a new group.",
                                action   = "Create Group",
                                onAction = onNavigateToCreate
                            )
                        }
                    }
                } else {
                    items(filtered, key = { it.groupId }) { group ->
                        val isJoined   = group.members.contains(currentUid)
                        val isJoiningThisGroup = joiningIds.contains(group.groupId)
                        val adminName = adminNames[group.adminId] ?: "Loading..."

                        StudyGroupCard(
                            groupName   = group.groupName,
                            subject     = group.description,
                            memberCount = group.members.size,
                            maxMembers  = group.maxMembers,
                            adminName   = adminName,
                            isJoined    = isJoined,
                            onClick     = { onNavigateToDetail(group.groupId) },
                            onJoinClick = {
                                if (!isJoiningThisGroup && !isJoined) {
                                    joiningIds = joiningIds + group.groupId
                                    scope.launch {
                                        try {
                                            Firebase.firestore
                                                .collection("groups")
                                                .document(group.groupId)
                                                .update(
                                                    "members",
                                                    FieldValue.arrayUnion(currentUid)
                                                )
                                                .await()

                                            Firebase.firestore
                                                .collection("notifications")
                                                .add(
                                                    mapOf(
                                                        "type"      to "NEW_MEMBER",
                                                        "groupId"   to group.groupId,
                                                        "groupName" to group.groupName,
                                                        "userId"    to currentUid,
                                                        "timestamp" to System.currentTimeMillis()
                                                    )
                                                )
                                                .await()
                                        } catch (_: Exception) {
                                            // Firestore listener will revert the UI automatically
                                        } finally {
                                            joiningIds = joiningIds - group.groupId
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}