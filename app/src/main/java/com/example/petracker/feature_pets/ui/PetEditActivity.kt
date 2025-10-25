package com.example.petracker.feature_pets.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.petracker.R
import com.example.petracker.common.model.Pet
import com.example.petracker.core.network.RetrofitClient
import com.example.petracker.core.storage.TokenStore
import com.example.petracker.feature_pets.data.PetCreate
import com.example.petracker.feature_pets.data.PetsApi
import com.example.petracker.feature_pets.data.PetsRepository
import kotlinx.coroutines.launch
import java.util.*
import com.google.android.material.appbar.MaterialToolbar
import androidx.activity.addCallback
import android.app.AlertDialog
import android.widget.*

class PetEditActivity : ComponentActivity() {

    private lateinit var pet: Pet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_edit)

        // 1) obtener el Pet a editar
        pet = intent.getParcelableExtra("pet") ?: run {
            Toast.makeText(this, "Mascota no encontrada", Toast.LENGTH_SHORT).show()
            finish(); return
        }

        // 2) referencias UI
        val etName = findViewById<EditText>(R.id.etName)
        val spSpecies = findViewById<Spinner>(R.id.spSpecies)
        val spSex = findViewById<Spinner>(R.id.spSex)
        val etBreed = findViewById<EditText>(R.id.etBreed)
        val etWeight = findViewById<EditText>(R.id.etWeight)
        val etBirth = findViewById<EditText>(R.id.etBirth)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar).apply {
            title = "Editar mascota"
            navigationIcon = null
            setNavigationOnClickListener(null)
        }

        val btnCancel = findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener { handleCancel() }


        onBackPressedDispatcher.addCallback(this) { handleCancel() }


        // 3) llenar spinners
        spSpecies.adapter = ArrayAdapter.createFromResource(
            this, R.array.species_values, android.R.layout.simple_spinner_dropdown_item
        )
        spSex.adapter = ArrayAdapter.createFromResource(
            this, R.array.sex_values, android.R.layout.simple_spinner_dropdown_item
        )

        // helper para seleccionar el índice igual al valor actual
        fun Spinner.setSelectionByValue(value: String) {
            val adapter = this.adapter as ArrayAdapter<*>
            val idx = (0 until adapter.count).firstOrNull { adapter.getItem(it).toString() == value } ?: 0
            this.setSelection(idx)
        }

        // 4) prellenar campos con los datos actuales
        etName.setText(pet.name)
        spSpecies.setSelectionByValue(pet.species)   // "felino","canino","otro"
        spSex.setSelectionByValue(pet.sex)           // "macho","hembra","desconocido"
        etBreed.setText(pet.breed ?: "")
        etWeight.setText(pet.weight_kg?.toString() ?: "")
        etBirth.setText(pet.birth_date ?: "")

        // 5) datepicker
        etBirth.setOnClickListener {
            val cal = Calendar.getInstance()
            val y0 = pet.birth_date?.split("-")?.getOrNull(0)?.toIntOrNull() ?: cal.get(Calendar.YEAR)
            val m0 = pet.birth_date?.split("-")?.getOrNull(1)?.toIntOrNull()?.minus(1) ?: cal.get(Calendar.MONTH)
            val d0 = pet.birth_date?.split("-")?.getOrNull(2)?.toIntOrNull() ?: cal.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(this, { _, y, m, d ->
                etBirth.setText(String.format("%04d-%02d-%02d", y, m+1, d))
            }, y0, m0, d0).show()
        }

        // 6) repo
        val repo = PetsRepository(RetrofitClient.create(TokenStore(this)).create(PetsApi::class.java))

        // 7) guardar cambios (PUT)
        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isEmpty()) { toast("Nombre requerido"); return@setOnClickListener }

            val species = spSpecies.selectedItem.toString()
            val sex = spSex.selectedItem.toString()
            val breed = etBreed.text.toString().ifBlank { null }
            val weight = etWeight.text.toString().toDoubleOrNull()
            val birth = etBirth.text.toString().ifBlank { null }

            val body = PetCreate(
                name = name, species = species, sex = sex,
                breed = breed, weight_kg = weight, birth_date = birth
            )

            btnSave.isEnabled = false
            lifecycleScope.launch {
                val r = repo.update(pet.id, body)
                btnSave.isEnabled = true
                r.onSuccess {
                    toast("Cambios guardados")
                    finish() // volver a la lista; onResume recarga
                }.onFailure {
                    toast("Error: ${it.message}")
                }
            }
        }
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    private fun handleCancel() {
        if (hasUnsavedChanges()) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.discard_changes_title))
                .setMessage(getString(R.string.discard_changes_msg))
                .setPositiveButton(getString(R.string.yes)) { _, _ -> finish() }
                .setNegativeButton(getString(R.string.no), null)
                .show()
        } else {
            finish()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        // Campos actuales
        val name = findViewById<EditText>(R.id.etName).text.toString().trim()
        val species = findViewById<Spinner>(R.id.spSpecies).selectedItem.toString()
        val sex = findViewById<Spinner>(R.id.spSex).selectedItem.toString()
        val breed = findViewById<EditText>(R.id.etBreed).text.toString().ifBlank { null }
        val weight = findViewById<EditText>(R.id.etWeight).text.toString().toDoubleOrNull()
        val birth = findViewById<EditText>(R.id.etBirth).text.toString().ifBlank { null }

        // Datos originales (de tu `pet` parcelable)
        val p = pet  // asegúrate de tener `lateinit var pet: Pet` y de setearlo desde el intent

        // Compara cada campo (cuidando nulls / tipos)
        if (name != p.name) return true
        if (species != p.species) return true
        if (sex != p.sex) return true
        if ((breed ?: "") != (p.breed ?: "")) return true
        if (weight != p.weight_kg) return true
        if ((birth ?: "") != (p.birth_date ?: "")) return true

        return false
    }

}