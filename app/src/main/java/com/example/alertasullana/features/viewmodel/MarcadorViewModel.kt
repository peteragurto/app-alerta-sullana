package com.example.alertasullana.features.viewmodel

// Importa las bibliotecas necesarias
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alertasullana.R
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class MarcadorViewModel : ViewModel() {

    // Instancia de FirebaseFirestore para acceder a la base de datos
    private val db = FirebaseFirestore.getInstance()

    private val _marcadores = MutableLiveData<List<MarkerOptions>>()
    val marcadores: LiveData<List<MarkerOptions>> get() = _marcadores

    fun cargarReportes() {
        db.collection("reportes").addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Manejar el error si es necesario
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val marcadores = snapshot.documents.mapNotNull { document ->
                    val latitud = document.getDouble("ubicacion.latitud") ?: return@mapNotNull null
                    val longitud = document.getDouble("ubicacion.longitud") ?: return@mapNotNull null
                    val titulo = document.getString("descripcionDelito") ?: "Sin título"

                    // Crear un BitmapDescriptor a partir de tu recurso de imagen
                    val iconoMarcador = BitmapDescriptorFactory.fromResource(R.drawable.senal)

                    MarkerOptions()
                        .position(LatLng(latitud, longitud))
                        .title(titulo)
                        .icon(iconoMarcador)    // Establecer el icono del marcador
                }
                _marcadores.value = marcadores
            }
        }
    }
}
