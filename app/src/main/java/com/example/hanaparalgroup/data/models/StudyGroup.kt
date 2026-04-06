package com.example.hanaparalgroup.data.models

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class StudyGroup(
    var groupId: String = "",
    val name: String = "",
    val description: String = "",
    val adminId: String = "",
    val adminName: String = "",
    val members: MutableList<String> = mutableListOf(),
    val maxMembers: Int = 5, // Default, Cativo will override this with Remote Config
    val members: List<String> = emptyList(),
    val maxMembers: Int = 15,           // used by Remote Config / CreateGroup
    val createdAt: Long = System.currentTimeMillis()
)