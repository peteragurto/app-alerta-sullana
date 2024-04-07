package com.example.alertasullana.features.viewmodel

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.alertasullana.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Date

class DetallesBottomSheetFragment : BottomSheetDialogFragment() {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private lateinit var descripcionSheetTextView: TextView
    private lateinit var fechaSheetTextView: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detalles_bottom_sheet, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        descripcionSheetTextView = view.findViewById(R.id.descripcion_sheet)
        fechaSheetTextView = view.findViewById(R.id.fecha_sheet)

        // Llamar al método cargarReportes
        cargarReportes()
        }
    fun cargarReportes() {
        // Obtener la referencia de la colección "reportes" en Firestore
        db.collection("reportes")
            .get()
            .addOnSuccessListener { querySnapshot -> procesarReportes(querySnapshot) }
            .addOnFailureListener { e -> /* Manejar error */ }
    }
    private fun procesarReportes(querySnapshot: QuerySnapshot?) {
        querySnapshot?.documents?.forEach { document ->
            val descripcionDelito = document.getString("descripcionDelito") ?: ""
            val fecha = document.getDate("fecha") ?: Date()
            val imageUrl = document.getString("imageUrl") ?: ""

            // Obtener la imagen desde Firebase Storage
            val storageRef: StorageReference = storage.reference.child("images/$imageUrl")
            storageRef.getBytes(Long.MAX_VALUE)
                .addOnSuccessListener { bytes ->
                    // Convertir bytes a bitmap o cualquier otra representación según tu necesidad
                    // Puedes utilizar la biblioteca Glide para cargar imágenes directamente en ImageView
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }
                .addOnFailureListener { e -> /* Manejar error de descarga de imagen */ }
            //asignacion de fragmento a datos de bd
            descripcionSheetTextView.text = descripcionDelito
            fechaSheetTextView.text = fecha.toString()

        }
    }

}