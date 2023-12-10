package com.example.alertasullana.ui.viewmodel

import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alertasullana.data.model.Reporte
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Date

class ReporteViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val reportes = MutableLiveData<List<Reporte>>()

    // LiveData para observar la lista de reportes
    fun getReportes(): LiveData<List<Reporte>> {
        return reportes
    }

    // Método para obtener los reportes desde Firestore
    fun cargarReportes() {
        // Obtener la referencia de la colección "reportes" en Firestore
        db.collection("reportes")
            .get()
            .addOnSuccessListener { querySnapshot -> procesarReportes(querySnapshot) }
            .addOnFailureListener { e -> /* Manejar error */ }
    }

    // Método para procesar el resultado de la consulta y obtener los datos de los reportes
    private fun procesarReportes(querySnapshot: QuerySnapshot?) {
        val listaReportes = mutableListOf<Reporte>()
        querySnapshot?.documents?.forEach { document ->
            val descripcionDelito = document.getString("descripcionDelito") ?: ""
            val fecha = document.getDate("fecha") ?: Date()
            val imageUrl = document.getString("imageUrl") ?: ""
            val latitud = document.getDouble("ubicacion.latitud") ?: 0.0
            val longitud = document.getDouble("ubicacion.longitud") ?: 0.0
            val userId = document.getString("uid") ?: ""

            // Obtener la imagen desde Firebase Storage
            val storageRef: StorageReference = storage.reference.child("images/$imageUrl")
            storageRef.getBytes(Long.MAX_VALUE)
                .addOnSuccessListener { bytes ->
                    // Convertir bytes a bitmap o cualquier otra representación según tu necesidad
                    // Puedes utilizar la biblioteca Glide para cargar imágenes directamente en ImageView
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    // Crear el objeto Reporte
                    val reporte = Reporte(descripcionDelito, fecha, imageUrl, latitud, longitud, userId, bitmap)
                    listaReportes.add(reporte)

                    // Actualizar LiveData con la nueva lista de reportes
                    reportes.value = listaReportes
                }
                .addOnFailureListener { e -> /* Manejar error de descarga de imagen */ }
        }
    }


}
