package com.example.petracker.feature_auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petracker.core.util.UiState
import com.example.petracker.feature_auth.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Patterns

class RegisterViewModel(private val repo: AuthRepository): ViewModel() {
    private val _state = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val state: StateFlow<UiState<Unit>> = _state

    fun register(name: String, email: String, pass: String, confirm: String) {
        // Validaciones rápidas en UI layer
        val err = validate(name, email, pass, confirm)
        if (err != null) {
            _state.value = UiState.Error(err)
            return
        }

        _state.value = UiState.Loading
        viewModelScope.launch {
            val result = repo.registerAndLogin(name, email, pass)
            _state.value = result.fold(
                onSuccess = { UiState.Success(Unit) },
                onFailure = {
                    val msg = it.message ?: "No se pudo registrar"
                    UiState.Error(msg)
                }
            )
        }
    }

    private fun validate(name: String, email: String, pass: String, confirm: String): String? {
        if (name.isBlank()) return "Ingresa tu nombre"
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Email inválido"
        if (pass.length < 8) return "La contraseña debe tener al menos 8 caracteres"
        if (pass != confirm) return "Las contraseñas no coinciden"
        return null
    }
}