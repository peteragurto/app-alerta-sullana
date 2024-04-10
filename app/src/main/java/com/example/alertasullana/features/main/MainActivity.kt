package com.example.alertasullana.features.main


import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.alertasullana.R
import com.example.alertasullana.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    //Controlador de navegacion
    private lateinit var navController: NavController

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initNavigation()
    }

    private fun initNavigation() {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerViewMain) as NavHostFragment
        navController = navHost.navController
        binding.bottomNavBar.setupWithNavController(navController)
    }

}


