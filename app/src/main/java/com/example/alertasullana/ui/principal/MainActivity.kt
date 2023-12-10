package com.example.alertasullana.ui.principal


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.alertasullana.R
import com.example.alertasullana.data.services.CameraResultListener
import com.example.alertasullana.data.services.OnMapSheetActionListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity(), CameraResultListener, OnMapSheetActionListener {
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

        // Crear el canal de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        //Suscribirse al tópico de notificaciones
        val firebaseMessaging = FirebaseMessaging.getInstance()
        firebaseMessaging.subscribeToTopic("new_report")
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
        // Seleccionar un elemento por defecto en BottomNavigationView
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

    override fun onMapAction(latitud: Double, longitud: Double) {
        Log.d("MainActivity", "onMapAction called with latitud: $latitud, longitud: $longitud")

        // Crear un Bundle para pasar la latitud y la longitud
        val bundle = Bundle()
        bundle.putDouble("latitud", latitud)
        bundle.putDouble("longitud", longitud)

        // Crear una instancia de HomeFragment y pasar el Bundle como argumento
        val homeFragment = HomeFragment()
        homeFragment.arguments = bundle

        // Seleccionar un elemento por defecto en BottomNavigationView
        bottomNavigationView.selectedItemId = R.id.bottom_home

        // Reemplazar el fragmento actual con HomeFragment
        replaceFragment(homeFragment)

    }

}


