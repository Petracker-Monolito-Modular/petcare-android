package com.example.petracker.feature_pets.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petracker.core.util.UiState
import com.example.petracker.common.model.Pet
import com.example.petracker.feature_pets.data.PetsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PetsViewModel(private val repo: PetsRepository): ViewModel() {
    private val _state = MutableStateFlow<UiState<List<Pet>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Pet>>> = _state

    fun load() {
        viewModelScope.launch {
            val r = repo.list()
            _state.value = r.fold(
                onSuccess = { UiState.Success(it.items) },
                onFailure = { UiState.Error(it.message ?: "Error cargando mascotas") }
            )
        }
    }
}