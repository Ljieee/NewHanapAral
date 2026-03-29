package com.example.hanaparalgroup.data.repository

import android.util.Log
import com.example.hanaparalgroup.data.models.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.tasks.await

object UserProfileRepository {

    private const val TAG = "UserProfileRepo"
    private const val COLLECTION = "users"

    // ── Get current logged-in UID ─────────────────────────────────────────────
    val currentUid: String?
        get() = Firebase.auth.currentUser?.uid

    // ── Create or Overwrite a profile document ────────────────────────────────
    suspend fun createProfile(profile: UserProfile): Result<Unit> {
        return try {
            Firebase.firestore
                .collection(COLLECTION)
                .document(profile.uid)
                .set(profile)
                .await()
            Log.d(TAG, "Profile created for uid=${profile.uid}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create profile", e)
            Result.failure(e)
        }
    }

    // ── Update FCM Token ──────────────────────────────────────────────────────
    suspend fun updateFcmToken(uid: String) {
        try {
            val token = Firebase.messaging.token.await()
            Firebase.firestore
                .collection(COLLECTION)
                .document(uid)
                .update("fcmToken", token)
                .await()
            Log.d(TAG, "FCM Token updated for uid=$uid")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update FCM token", e)
        }
    }

    // ── Fetch a profile once (not real-time) ──────────────────────────────────
    suspend fun getProfile(uid: String): Result<UserProfile?> {
        return try {
            val snapshot = Firebase.firestore
                .collection(COLLECTION)
                .document(uid)
                .get()
                .await()
            val profile = snapshot.toObject(UserProfile::class.java)
            Result.success(profile)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch profile", e)
            Result.failure(e)
        }
    }

    // ── Update only editable fields (name, course, yearLevel) ─────────────────
    suspend fun updateProfile(uid: String, name: String, course: String, yearLevel: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "name"      to name,
                "course"    to course,
                "yearLevel" to yearLevel
            )
            Firebase.firestore
                .collection(COLLECTION)
                .document(uid)
                .update(updates)
                .await()
            Log.d(TAG, "Profile updated for uid=$uid")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update profile", e)
            Result.failure(e)
        }
    }

    // ── Real-time listener ────────────────────────────────────────────────────
    fun listenToProfile(
        uid: String,
        onUpdate: (UserProfile) -> Unit,
        onError: (Exception) -> Unit
    ): com.google.firebase.firestore.ListenerRegistration {
        return Firebase.firestore
            .collection(COLLECTION)
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Snapshot listener error", error)
                    onError(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val profile = snapshot.toObject(UserProfile::class.java)
                    profile?.let { onUpdate(it) }
                }
            }
    }
}