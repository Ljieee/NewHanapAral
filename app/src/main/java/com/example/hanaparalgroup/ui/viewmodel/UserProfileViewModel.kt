package com.example.hanaparalgroup.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hanaparalgroup.data.models.UserProfile
import com.example.hanaparalgroup.data.repository.UserProfileRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ── UI State ─────────────────────────────────────────────────────────────────
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val profile: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

sealed class SaveUiState {
    object Idle : SaveUiState()
    object Saving : SaveUiState()
    object Saved : SaveUiState()
    data class Error(val message: String) : SaveUiState()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────
class UserProfileViewModel : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    private val _saveState = MutableStateFlow<SaveUiState>(SaveUiState.Idle)
    val saveState: StateFlow<SaveUiState> = _saveState.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        startListening()
    }

    private fun startListening() {
        val uid = UserProfileRepository.currentUid
        if (uid == null) {
            _profileState.value = ProfileUiState.Error("User not logged in.")
            return
        }

        _profileState.value = ProfileUiState.Loading

        listenerRegistration = UserProfileRepository.listenToProfile(
            uid      = uid,
            onUpdate = { profile ->
                _profileState.value = ProfileUiState.Success(profile)
            },
            onError  = { exception ->
                _profileState.value = ProfileUiState.Error(
                    exception.message ?: "Failed to load profile."
                )
            }
        )
    }

    fun saveProfile(name: String, course: String, yearLevel: String) {
        val uid = UserProfileRepository.currentUid ?: return
        _saveState.value = SaveUiState.Saving

        viewModelScope.launch {
            val result = UserProfileRepository.updateProfile(
                uid       = uid,
                name      = name.trim(),
                course    = course.trim(),
                yearLevel = yearLevel
            )
            _saveState.value = if (result.isSuccess) {
                SaveUiState.Saved
            } else {
                SaveUiState.Error("Failed to save. Please try again.")
            }
        }
    }

    fun uploadProfilePicture(uri: Uri, context: Context) {
        val uid = UserProfileRepository.currentUid ?: return
        _saveState.value = SaveUiState.Saving

        viewModelScope.launch {
            val result = UserProfileRepository.uploadProfilePicture(uid, uri, context)
            _saveState.value = if (result.isSuccess) {
                SaveUiState.Idle 
            } else {
                SaveUiState.Error("Failed to upload image.")
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveUiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}