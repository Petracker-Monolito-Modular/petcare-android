package com.example.petracker.feature_auth.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.petracker.R
import com.example.petracker.core.network.RetrofitClient
import com.example.petracker.core.storage.TokenStore
import com.example.petracker.core.util.UiState
import com.example.petracker.feature_auth.data.AuthApi
import com.example.petracker.feature_auth.data.AuthRepository
import com.example.petracker.feature_menu.ui.MenuActivity
import com.example.petracker.feature_pets.ui.PetsListActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterActivity: ComponentActivity() {
    private lateinit var vm: RegisterViewModel
    private lateinit var btn: Button
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var etConfirm: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val tokenStore = TokenStore(this)
        val retrofit = RetrofitClient.create(tokenStore)
        val repo = AuthRepository(retrofit.create(AuthApi::class.java), tokenStore)
        vm = RegisterViewModel(repo)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPass = findViewById(R.id.etPass)
        etConfirm = findViewById(R.id.etConfirm)
        btn = findViewById(R.id.btnRegister)

        btn.setOnClickListener {
            clearFieldErrors()
            val err = validateFields()
            if (err != null) {
                showFieldErrors(err)
                return@setOnClickListener
            }

            vm.register(
                name = etName.text.toString().trim(),
                email = etEmail.text.toString().trim(),
                pass = etPass.text.toString(),
                confirm = etConfirm.text.toString()
            )
        }

        lifecycleScope.launch {
            vm.state.collect { s ->
                when (s) {
                    is UiState.Loading -> {
                        btn.isEnabled = false
                    }
                    is UiState.Success -> {
                        btn.isEnabled = true
                        Toast.makeText(this@RegisterActivity, "Cuenta creada", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, MenuActivity::class.java))
                        finish()
                    }
                    is UiState.Error -> {
                        btn.isEnabled = true
                        // si vino un error de backend, muéstralo bonito
                        Toast.makeText(this@RegisterActivity, s.message, Toast.LENGTH_SHORT).show()
                    }
                    is UiState.Idle -> {
                        btn.isEnabled = true
                    }
                }
            }
        }
    }

    private data class FieldError(
        val name: String? = null,
        val email: String? = null,
        val pass: String? = null,
        val confirm: String? = null
    )

    private fun validateFields(): FieldError? {
        var nameErr: String? = null
        var emailErr: String? = null
        var passErr: String? = null
        var confirmErr: String? = null

        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val pass = etPass.text.toString()
        val confirm = etConfirm.text.toString()

        if (name.isBlank()) nameErr = "Ingresa tu nombre"
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) emailErr = "Email inválido"
        if (pass.length < 8) passErr = "La contraseña debe tener al menos 8 caracteres"
        if (confirm != pass) confirmErr = "Las contraseñas no coinciden"

        return if (nameErr != null || emailErr != null || passErr != null || confirmErr != null)
            FieldError(nameErr, emailErr, passErr, confirmErr) else null
    }

    private fun showFieldErrors(e: FieldError) {
        e.name?.let { etName.error = it }
        e.email?.let { etEmail.error = it }
        e.pass?.let { etPass.error = it }
        e.confirm?.let { etConfirm.error = it }
    }

    private fun clearFieldErrors() {
        etName.error = null
        etEmail.error = null
        etPass.error = null
        etConfirm.error = null
    }
}