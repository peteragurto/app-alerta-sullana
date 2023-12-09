package com.example.alertasullana.ui.viewmodel

// Importa las bibliotecas necesarias
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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



                    MarkerOptions()
                        .position(LatLng(latitud, longitud))
                        .title(titulo)
                }
                _marcadores.value = marcadores
            }
        }
    }
}
