package com.example.hanaparalgroup.data.models

data class StudyGroup(
    val groupId: String = "",
    val groupName: String = "",
    val description: String = "",
    val adminId: String = "",        // Requirement #3 (Creator is admin)
    val members: List<String> = emptyList(), // Requirement #4 (Join list)
    val createdAt: Long = System.currentTimeMillis()
)