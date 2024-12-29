package com.example.alertasullana.ui


import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.alertasullana.R
import com.example.alertasullana.data.services.CameraResultListener
import com.example.alertasullana.data.services.OnMapSheetActionListener
import com.example.alertasullana.databinding.ActivityMainBinding
import com.example.alertasullana.ui.principal.HacerReporteFragment
import com.example.alertasullana.ui.principal.HomeFragment

class MainActivity : AppCompatActivity(), CameraResultListener, OnMapSheetActionListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

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
        val navHost = supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment
        navController = navHost.navController
        binding.mainBottomNavBar.setupWithNavController(navController)
    }

    override fun onCameraResult(imageBitmap: Bitmap) {
        // Cambia al nuevo fragmento y pasa la imagen
        val newFragment = HacerReporteFragment()
        val bundle = Bundle()
        bundle.putParcelable("image", imageBitmap)
        newFragment.arguments = bundle


    }

    override fun onMapAction(latitud: Double, longitud: Double) {
        Log.d("MainActivity", "onMapAction called with latitud: $latitud, longitud: $longitud")

        // Crear un Bundle para pasar la latitud y la longitud
        val bundle = Bundle()
        bundle.putDouble("latitud", latitud)
        bundle.putDouble("longitud", longitud)

        // Crear una instancia de HomeFragment y pasar el Bundle como argumento
        val homeFragment = HomeFragment()
        homeFragment.arguments = bundle
    }

}


