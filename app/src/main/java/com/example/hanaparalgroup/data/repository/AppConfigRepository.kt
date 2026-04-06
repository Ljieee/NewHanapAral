package com.example.hanaparalgroup.data.repository

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Central config document: Firestore → config/app_settings
 *
 * Fields:
 *   group_creation_enabled : Boolean
 *   max_members_per_group  : Long
 *   announcement_header    : String
 *   notifications_enabled  : Boolean
 *   maintenance_mode       : Boolean
 */
object AppConfigRepository {

    private const val TAG        = "AppConfigRepo"
    private const val COLLECTION = "config"
    private const val DOC_ID     = "app_settings"

    data class AppConfig(
        val groupCreationEnabled : Boolean = true,
        val maxMembersPerGroup   : Int     = 15,
        val announcementHeader   : String  = "📢 Study Smart, Excel Together!",
        val notificationsEnabled : Boolean = true,
        val maintenanceMode      : Boolean = false
    )

    // ── Real-time listener ────────────────────────────────────────────────────
    fun listenToConfig(
        onUpdate : (AppConfig) -> Unit,
        onError  : (Exception) -> Unit
    ): com.google.firebase.firestore.ListenerRegistration {
        return Firebase.firestore
            .collection(COLLECTION)
            .document(DOC_ID)
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    Log.e(TAG, "Config listener error", error)
                    onError(error)
                    return@addSnapshotListener
                }
                if (snap != null && snap.exists()) {
                    val config = AppConfig(
                        groupCreationEnabled = snap.getBoolean("group_creation_enabled") ?: true,
                        maxMembersPerGroup   = (snap.getLong("max_members_per_group") ?: 15L).toInt(),
                        announcementHeader   = snap.getString("announcement_header")
                            ?: "📢 Study Smart, Excel Together!",
                        notificationsEnabled = snap.getBoolean("notifications_enabled") ?: true,
                        maintenanceMode      = snap.getBoolean("maintenance_mode") ?: false
                    )
                    onUpdate(config)
                } else {
                    // Document doesn't exist yet — use defaults
                    onUpdate(AppConfig())
                }
            }
    }

    // ── One-shot fetch ────────────────────────────────────────────────────────
    suspend fun getConfig(): Result<AppConfig> {
        return try {
            val snap = Firebase.firestore
                .collection(COLLECTION)
                .document(DOC_ID)
                .get()
                .await()

            val config = if (snap.exists()) {
                AppConfig(
                    groupCreationEnabled = snap.getBoolean("group_creation_enabled") ?: true,
                    maxMembersPerGroup   = (snap.getLong("max_members_per_group") ?: 15L).toInt(),
                    announcementHeader   = snap.getString("announcement_header")
                        ?: "📢 Study Smart, Excel Together!",
                    notificationsEnabled = snap.getBoolean("notifications_enabled") ?: true,
                    maintenanceMode      = snap.getBoolean("maintenance_mode") ?: false
                )
            } else AppConfig()

            Result.success(config)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch config", e)
            Result.failure(e)
        }
    }

    // ── Write (superuser only) ────────────────────────────────────────────────
    suspend fun saveConfig(config: AppConfig): Result<Unit> {
        return try {
            val data = mapOf(
                "group_creation_enabled" to config.groupCreationEnabled,
                "max_members_per_group"  to config.maxMembersPerGroup.toLong(),
                "announcement_header"    to config.announcementHeader,
                "notifications_enabled"  to config.notificationsEnabled,
                "maintenance_mode"       to config.maintenanceMode
            )
            Firebase.firestore
                .collection(COLLECTION)
                .document(DOC_ID)
                .set(data)
                .await()
            Log.d(TAG, "Config saved: $data")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save config", e)
            Result.failure(e)
        }
    }
}