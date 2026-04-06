package com.example.hanaparalgroup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hanaparalgroup.data.repository.AppConfigRepository
import com.example.hanaparalgroup.data.repository.AppConfigRepository.AppConfig
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Shared ViewModel that exposes live Firestore-backed app config.
 * Use viewModel() in any screen that needs these values.
 */
class AppConfigViewModel : ViewModel() {

    private val _config = MutableStateFlow(AppConfig())
    val config: StateFlow<AppConfig> = _config.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var listener: ListenerRegistration? = null

    init {
        startListening()
    }

    private fun startListening() {
        listener = AppConfigRepository.listenToConfig(
            onUpdate = { config ->
                _config.value  = config
                _isLoading.value = false
            },
            onError = {
                _isLoading.value = false
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}