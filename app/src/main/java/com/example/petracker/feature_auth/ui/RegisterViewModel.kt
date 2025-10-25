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

        _state.value = UiState.Loading
        viewModelScope.launch {
            val result = repo.registerAndLogin(name, email, pass)
            _state.value = result.fold(
                onSuccess = { UiState.Success(Unit) },
                onFailure = { UiState.Error(mapToUserMessage(it)) }
            )
        }
    }

    private fun mapToUserMessage(t: Throwable): String {
        return when (t) {
            is retrofit2.HttpException -> when (t.code()) {
                409 -> "Ese correo ya está registrado"
                400, 422 -> "Datos inválidos. Revisa los campos"
                401 -> "Credenciales inválidas"
                500 -> "Error del servidor. Inténtalo más tarde"
                else -> "Error (${t.code()}). Inténtalo de nuevo"
            }
            is java.io.IOException -> "Sin conexión. Revisa tu internet"
            else -> t.message ?: "Ocurrió un error inesperado"
        }
    }
}
