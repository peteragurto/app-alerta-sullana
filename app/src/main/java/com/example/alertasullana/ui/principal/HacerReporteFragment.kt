package com.example.alertasullana.ui.principal

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.alertasullana.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*

class HacerReporteFragment : Fragment() {
    //creamos variantes donde se almacenan datos
    private lateinit var imageView: ImageView
    private lateinit var desEditText: EditText
    private lateinit var btn_subir: Button
    private lateinit var Ubicacion: FusedLocationProviderClient
    //creamos variantes para activar servicios
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private var currentLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hacer_reporte, container, false)

        // ImageView
        imageView= view.findViewById(R.id.imagen_delito)
        //TextInPut
        desEditText = view.findViewById(R.id.txt_Descripcion_Delito)
        //button
        btn_subir= view.findViewById(R.id.btn_reporte)

        // Inicializar Firebase
        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        //Inicia servivio de localizacion
        Ubicacion = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Recuperar la imagen de los argumentos
        val imageBitmap = arguments?.getParcelable<Bitmap>("image")
        imageView.setImageBitmap(imageBitmap)

        // Solicitar permisos de ubicación si no están concedidos
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Obtener la ubicación del usuario
            getCurrentLocation()
        }

        btn_subir.setOnClickListener {
            val uid = arguments?.getString("uid")
            // Subir la imagen a Firebase Storage
            val imageUrl = SubirImg(imageBitmap)

            // Subir los datos a Firestore
            SubirDatos(imageUrl, uid)
        }

        // Inflate the layout for this fragment
        return view
    }
    private fun getCurrentLocation() {
        Ubicacion.lastLocation
            .addOnSuccessListener { location: Location? ->
                currentLocation = location
            }
    }
    //subir imagen a Storage y generar id imagen
    private fun SubirImg(bitmap: Bitmap?): String {
        // Convertir la imagen a un formato que pueda ser subido a Storage
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        // Generar un nombre único para la imagen
        val imgNombre = "${UUID.randomUUID()}.jpg"

        // Subir la imagen a Firebase Storage
        val imageRef = storageReference.child("images/$imgNombre")
        imageRef.putBytes(data)
            .addOnSuccessListener {
                // Manejar éxito
            }
            .addOnFailureListener { e ->
                // Manejar errores durante la subida de la imagen
            }

        return imgNombre
    }
    //subir datos a FireStore
    private fun SubirDatos(imageUrl: String, uid: String?) {
        val descripcionDelito = desEditText.text.toString()

        // Crear un nuevo documento en Firestore para el reporte
        val reporte = hashMapOf(
            "descripcionDelito" to descripcionDelito,
            "imageUrl" to imageUrl,
            "fecha" to FieldValue.serverTimestamp(),
            "ubicacion" to currentLocation?.let {
                hashMapOf(
                    "latitud" to it.latitude,
                    "longitud" to it.longitude
                )
            },
            "uid" to uid // Agregar el uid del usuario al documento
        )

        firestore.collection("reportes").add(reporte)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
            }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }

}