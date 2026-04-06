package com.example.hanaparalgroup.data.models

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val course: String = "",
    val yearLevel: String = "",
    val profilePictureUrl: String = "",
    val fcmToken: String = ""   // kept here so Alora's FCM token stays in the same document
)