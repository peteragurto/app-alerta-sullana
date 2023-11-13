package com.example.alertasullana.ui.view.principal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.alertasullana.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mGoogleMap:GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicia el fragmento del mapa
        val mapaFragment = supportFragmentManager.findFragmentById(R.id.mapaSullana) as SupportMapFragment
        mapaFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Cambia el estilo del mapa a oscuro
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.drawable.map_style_dark))

        // Mueve el mapa a la ciudad de Sullana en Perú
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-6.8987693, -80.6909099), 15f))
    }
}


