package com.example.petracker.feature_pets.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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

class PetsListActivity : ComponentActivity() {
    private lateinit var vm: PetsViewModel
    private lateinit var list: ListView
    private lateinit var adapterPets: PetRowAdapter
    private lateinit var repo: PetsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pets_list)

        list = findViewById(R.id.listPets)

        val retrofit = RetrofitClient.create(TokenStore(this))
        repo = PetsRepository(retrofit.create(PetsApi::class.java))
        vm = PetsViewModel(repo)

        adapterPets = PetRowAdapter(
            this,
            mutableListOf(),
            onEdit = { pet ->
                val i = Intent(this, PetEditActivity::class.java)
                i.putExtra("pet", pet)
                startActivity(i)
            },
            onDelete = { pet ->
                confirmDelete(pet)    // 👈 usa el diálogo
            }
        )
        list.adapter = adapterPets

        lifecycleScope.launch {
            vm.state.collectLatest { s ->
                when (s) {
                    is UiState.Success -> render(s.data)
                    is UiState.Error -> toast(s.message)
                    else -> Unit
                }
            }
        }
        vm.load()
    }
    private fun render(items: List<Pet>) {
        adapterPets.setData(items)
    }

    // Recargar al volver de edición
    override fun onResume() {
        super.onResume()
        vm.load()
    }

    private fun confirmDelete(pet: Pet) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar")
            .setMessage("¿Eliminar a ${pet.name}?")
            .setPositiveButton("Sí") { _, _ -> deletePet(pet) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deletePet(pet: Pet) {
        lifecycleScope.launch {
            val r = repo.delete(pet.id)
            r.onSuccess {
                adapterPets.removeItem(pet)
                toast("Eliminado")
            }.onFailure {
                toast("Error: ${it.message}")
            }
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
