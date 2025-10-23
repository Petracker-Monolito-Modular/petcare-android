package com.example.petracker.feature_pets.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.petracker.R
import com.example.petracker.common.model.Pet
import com.example.petracker.core.network.RetrofitClient
import com.example.petracker.core.storage.TokenStore
import com.example.petracker.core.util.UiState
import com.example.petracker.feature_pets.data.PetsApi
import com.example.petracker.feature_pets.data.PetsRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PetsListActivity: ComponentActivity() {
    private lateinit var vm: PetsViewModel
    private lateinit var list: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pets_list)

        list = findViewById(R.id.listPets)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        list.adapter = adapter

        val retrofit = RetrofitClient.create(TokenStore(this))
        val repo = PetsRepository(retrofit.create(PetsApi::class.java))
        vm = PetsViewModel(repo)

        lifecycleScope.launch {
            vm.state.collectLatest { s ->
                when (s) {
                    is UiState.Success -> render(s.data)
                    is UiState.Error -> Toast.makeText(this@PetsListActivity, s.message, Toast.LENGTH_SHORT).show()
                    else -> Unit
                }
            }
        }
        vm.load()
    }

    private fun render(items: List<Pet>) {
        val rows = items.map { "${it.name} • ${it.species}${if (it.breed!=null) " (${it.breed})" else ""}" }
        adapter.clear()
        adapter.addAll(rows)
    }
}