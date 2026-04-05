package com.example.hanaparalgroup.ui.viewmodel

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

    // ── Profile state (real-time) ─────────────────────────────────────────────
    private val _profileState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    // ── Save state (for ProfileEditScreen) ────────────────────────────────────
    private val _saveState = MutableStateFlow<SaveUiState>(SaveUiState.Idle)
    val saveState: StateFlow<SaveUiState> = _saveState.asStateFlow()

    // Holds the Firestore listener so we can remove it when ViewModel is cleared
    private var listenerRegistration: ListenerRegistration? = null

    init {
        startListening()
    }

    // ── Start real-time listener ──────────────────────────────────────────────
    // Called once in init. Any change to the Firestore document instantly
    // pushes a new Success state, which all observing screens pick up immediately.
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

    // ── Save editable fields ──────────────────────────────────────────────────
    // After a successful save, Firestore triggers the listener above automatically,
    // so ProfileScreen updates with zero extra calls.
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

    // ── Reset save state (call after navigating back from edit screen) ────────
    fun resetSaveState() {
        _saveState.value = SaveUiState.Idle
    }

    // ── Clean up listener when ViewModel is destroyed ─────────────────────────
    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}