package com.example.alertasullana.ui.principal


import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.alertasullana.R
import com.example.alertasullana.data.services.CameraResultListener
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity(), CameraResultListener {
    //Inicializador de la barra de navegacion
    private lateinit var bottomNavigationView: BottomNavigationView
    //Controlador de navegacion
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Objetos
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.bottom_home ->{
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.bottom_noti ->{
                    replaceFragment(ReporteFragment())
                    true
                }
                R.id.bottom_users ->{
                    replaceFragment(PerfilFragment())
                    true
                }
                else -> false
            }
        }
        replaceFragment(HomeFragment())
    }
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCameraResult(imageBitmap: Bitmap) {
        // Cambia al nuevo fragmento y pasa la imagen
        val newFragment = HacerReporteFragment()
        val bundle = Bundle()
        bundle.putParcelable("image", imageBitmap)
        newFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, newFragment)
            .addToBackStack(null)
            .commit()
    }


}


