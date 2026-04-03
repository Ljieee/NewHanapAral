package com.example.hanaparalgroup.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.hanaparalgroup.R
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RemoteConfigViewModel : ViewModel() {
    private val remoteConfig = Firebase.remoteConfig

    private val _groupCreationEnabled = MutableStateFlow(remoteConfig.getBoolean("is_group_creation_enabled"))
    val groupCreationEnabled = _groupCreationEnabled.asStateFlow()

    private val _maxMembersLimit = MutableStateFlow(remoteConfig.getLong("max_member_per_group").toInt())
    val maxMembersLimit = _maxMembersLimit.asStateFlow()

    private val _announcementHeader = MutableStateFlow(remoteConfig.getString("announcement_header"))
    val announcementHeader = _announcementHeader.asStateFlow()

    private val _maintenanceMode = MutableStateFlow(remoteConfig.getBoolean("maintenance_mode"))
    val maintenanceMode = _maintenanceMode.asStateFlow()

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 hour for production
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        fetchAndActivate()
        setupRealtimeUpdates()
    }

    fun fetchAndActivate() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateState()
                    Log.d("RemoteConfig", "Config params updated")
                } else {
                    Log.d("RemoteConfig", "Fetch failed")
                }
            }
    }

    private fun setupRealtimeUpdates() {
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnCompleteListener {
                    updateState()
                }
            }
            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w("RemoteConfig", "Config update error", error)
            }
        })
    }

    private fun updateState() {
        _groupCreationEnabled.value = remoteConfig.getBoolean("is_group_creation_enabled")
        _maxMembersLimit.value = remoteConfig.getLong("max_member_per_group").toInt()
        _announcementHeader.value = remoteConfig.getString("announcement_header")
        _maintenanceMode.value = remoteConfig.getBoolean("maintenance_mode")
    }
}
