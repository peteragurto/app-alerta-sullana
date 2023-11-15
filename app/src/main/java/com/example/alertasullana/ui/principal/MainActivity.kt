package com.example.alertasullana.ui.principal


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
    //Inicializador de mapa
    private lateinit var mapa:GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        crearFragmento()

    }
    //Función para declarar el fragmento
    private fun crearFragmento() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapaSullana) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    //Función heredada para crear el mapa
    override fun onMapReady(googleMap: GoogleMap) {
        mapa = googleMap
        definirEstiloOscuro()
        apuntarSullanaCiudad()
    }
    //Función para apuntar a la ciudad de Sullana
    private fun apuntarSullanaCiudad() {
        // Coordenadas de Sullana, Perú
        val sullana = LatLng(-4.8901, -80.6874)
        // Mover la cámara a Sullana y darle zoom
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(sullana, 15f))
    }

    //Función para definir el tema oscuro del mapa
    private fun definirEstiloOscuro() {
        val estiloOscuro = MapStyleOptions.loadRawResourceStyle(this, R.raw.estilo_oscuro)
        mapa.setMapStyle(estiloOscuro)
    }
}


