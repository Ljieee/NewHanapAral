package com.example.hanaparalgroup.data.models

data class StudyGroup(
    val groupId: String = "",
    val groupName: String = "",
    val description: String = "",
    val adminId: String = "",
    val members: List<String> = emptyList(),
    val maxMembers: Int = 15,           // used by Remote Config / CreateGroup
    val createdAt: Long = System.currentTimeMillis()
)