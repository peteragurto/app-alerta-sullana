package com.example.alertasullana.features.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.alertasullana.R
import com.example.alertasullana.databinding.ActivityAuthBinding
import com.example.alertasullana.features.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var navController: NavController

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isLoggedIn = runBlocking { authViewModel.isUserLoggedIn() }
        if (isLoggedIn) {
            navigateToMainScreen()
        } else {
            initUI()
        }
    }

    private fun initUI() {
        initNavigation()
    }

    private fun initNavigation() {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerViewAuth) as NavHostFragment
        navController = navHost.navController
    }

    private fun navigateToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        // Elimina el historial de navegación de las pantallas de autenticación
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}