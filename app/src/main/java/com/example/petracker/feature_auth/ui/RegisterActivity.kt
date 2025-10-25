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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val tokenStore = TokenStore(this)
        val retrofit = RetrofitClient.create(tokenStore)
        val repo = AuthRepository(retrofit.create(AuthApi::class.java), tokenStore)
        vm = RegisterViewModel(repo)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPass = findViewById<EditText>(R.id.etPass)
        val etConfirm = findViewById<EditText>(R.id.etConfirm)
        val btn = findViewById<Button>(R.id.btnRegister)

        btn.setOnClickListener {
            btn.isEnabled = false
            vm.register(
                name = etName.text.toString().trim(),
                email = etEmail.text.toString().trim(),
                pass = etPass.text.toString(),
                confirm = etConfirm.text.toString()
            )
        }

        lifecycleScope.launch {
            vm.state.collectLatest { s ->
                when (s) {
                    is UiState.Success -> {
                        Toast.makeText(this@RegisterActivity, "Cuenta creada", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, MenuActivity::class.java))
                        finish()
                    }
                    is UiState.Error -> {
                        btn.isEnabled = true
                        Toast.makeText(this@RegisterActivity, s.message, Toast.LENGTH_SHORT).show()
                    }
                    is UiState.Loading -> { /* mostrar un progress */ }
                    else -> Unit
                }
            }
        }
    }
}