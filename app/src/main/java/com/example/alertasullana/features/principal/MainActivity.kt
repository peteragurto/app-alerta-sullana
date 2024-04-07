package com.example.alertasullana.features.principal


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.example.alertasullana.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity(){
    //Inicializador de la barra de navegacion
    private lateinit var bottomNavigationView: BottomNavigationView
    //Controlador de navegacion
    private lateinit var navController: NavController
    companion object {
        const val CHANNEL_ID = "channel_id"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}


