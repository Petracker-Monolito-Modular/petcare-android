package com.example.petracker.feature_auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petracker.core.util.UiState
import com.example.petracker.feature_auth.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repo: AuthRepository): ViewModel() {
    private val _state = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val state: StateFlow<UiState<Unit>> = _state

    fun login(email: String, pass: String) {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val r = repo.login(email, pass)
            _state.value = r.fold(
                onSuccess = { UiState.Success(Unit) },
                onFailure = { UiState.Error(it.message ?: "Login failed") }
            )
        }
    }
}