package com.example.hanaparalgroup

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.hanaparalgroup.ui.navigation.HanapAralNavGraph
import com.example.hanaparalgroup.ui.theme.HanapAralGroupTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class MainActivity : FragmentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.d("MainActivity", "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // 1. Initialize Firebase Remote Config
        setupRemoteConfig()
        
        // 2. FCM logic
        askNotificationPermission()
        saveFcmToken()

        setContent {
            HanapAralGroupTheme {
                HanapAralNavGraph()
            }
        }
    }

    private fun setupRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // Fetch every hour
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Set default values matching SuperuserScreen
        val defaults = mapOf(
            "groupCreationEnabled" to true,
            "maxMembersOverride" to 15L,
            "announcementHeader" to "📢 Study Smart, Excel Together!",
            "notificationsEnabled" to true,
            "maintenanceMode" to false
        )
        remoteConfig.setDefaultsAsync(defaults)

        // Fetch and activate
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d("MainActivity", "Remote Config params updated: $updated")
                } else {
                    Log.d("MainActivity", "Remote Config fetch failed")
                }
            }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun saveFcmToken() {
        val userId = Firebase.auth.currentUser?.uid ?: return
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("MainActivity", "FCM Token: $token")
            
            Firebase.firestore.collection("users").document(userId)
                .update("fcmToken", token)
                .addOnFailureListener {
                    val user = hashMapOf("fcmToken" to token)
                    Firebase.firestore.collection("users").document(userId)
                        .set(user, com.google.firebase.firestore.SetOptions.merge())
                }
        }
    }
}
