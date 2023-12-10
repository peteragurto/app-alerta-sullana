package com.example.alertasullana.data.model

import android.graphics.Bitmap
import java.util.Date

data class Reporte(
    val descripcionDelito: String,
    val fecha: Date,
    val imageUrl: String,
    val latitud: Double,
    val longitud: Double,
    val userId: String,
    val bitmap: Bitmap
)
