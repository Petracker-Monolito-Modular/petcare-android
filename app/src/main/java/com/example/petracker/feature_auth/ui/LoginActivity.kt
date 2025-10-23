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
import com.example.petracker.feature_pets.ui.PetsListActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity: ComponentActivity() {

    private lateinit var vm: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tokenStore = TokenStore(this)
        val retrofit = RetrofitClient.create(tokenStore)
        val repo = AuthRepository(retrofit.create(AuthApi::class.java), tokenStore)
        vm = LoginViewModel(repo)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPass  = findViewById<EditText>(R.id.etPassword)
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            vm.login(etEmail.text.toString(), etPass.text.toString())
        }

        lifecycleScope.launch {
            vm.state.collectLatest { s ->
                when (s) {
                    is UiState.Success -> {
                        startActivity(Intent(this@LoginActivity, PetsListActivity::class.java))
                        finish()
                    }
                    is UiState.Error -> Toast.makeText(this@LoginActivity, s.message, Toast.LENGTH_SHORT).show()
                    else -> Unit
                }
            }
        }
    }
}
