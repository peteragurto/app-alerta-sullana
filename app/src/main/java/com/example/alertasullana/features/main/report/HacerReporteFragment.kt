package com.example.alertasullana.features.main.report

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
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
import com.example.alertasullana.features.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
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

    //creamos variable para almacenar el uid del usuario
    private lateinit var uid: String

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

        // Inicializar Firebase(Referencias de Firestore y Storage)
        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        //======================================================================================
        // Obtener la instancia de FirebaseAuth
        val auth = FirebaseAuth.getInstance()
        // Obtener el UID del usuario actual
        uid = auth.currentUser?.uid ?: ""
        //======================================================================================

        //Inicia servivio de localizacion
        Ubicacion = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Recuperar la imagen de los argumentos
        val imageBitmap = arguments?.getParcelable<Bitmap>("image")

        // Crear una matriz de rotación
        val matrix = Matrix()
        matrix.postRotate(90f) // Rotar 90 grados

        // Crear un nuevo Bitmap que esté rotado
        val rotatedBitmap = imageBitmap?.let { Bitmap.createBitmap(it, 0, 0, imageBitmap.width, imageBitmap.height, matrix, true) }

        // Establecer el Bitmap rotado en el ImageView
        imageView.setImageBitmap(rotatedBitmap)

        //======================================================================================
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

        //Boton para subir datos
        btn_subir.setOnClickListener {
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

    //Método para subir datos a FireStore
    private fun SubirDatos(imageUrl: String, uid: String?) {
        val descripcionDelito = desEditText.text.toString()

        // Crear un ProgressDialog para mostrar el progreso de la carga
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Subiendo reporte...")
        progressDialog.setCancelable(false)
        progressDialog.show()

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
            "uid" to this.uid // Agregar el uid del usuario al documento
        )

        firestore.collection("reportes").add(reporte)
            .addOnSuccessListener {
                // Ocultar el ProgressDialog en caso de éxito
                progressDialog.dismiss()

                // Mostrar un AlertDialog de éxito
                MostrarDialogExito("Éxito", "Reporte subido correctamente.") {
                    // Manejar la acción después del éxito (por ejemplo, ir a MainActivity)
                    // Puedes poner aquí el código para ir a la actividad principal
                    // o simplemente cerrar el fragmento
                    requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
                }
            }
            .addOnFailureListener { e ->
                // Ocultar el ProgressDialog en caso de fallo
                progressDialog.dismiss()

                // Mostrar un AlertDialog de fallo
                MostrarDialogError("Error", "No se pudo subir el reporte. ¿Deseas intentar de nuevo?") {
                    // Manejar la acción después del fallo (por ejemplo, reintentar o cerrar el fragmento)
                    // Puedes poner aquí el código para manejar las opciones después del fallo
                }
            }
    }

    // Función para mostrar un AlertDialog de fallo
    private fun MostrarDialogError(titulo: String, mensaje: String, accion: () -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Sí") { dialog, _ ->
                // Ejecutar la acción si el usuario selecciona "Sí"
                accion.invoke()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                // Iniciar la MainActivity si el usuario selecciona "Cancelar"
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish() // Opcional: cerrar la actividad actual
                dialog.dismiss()

            }
            .create()
            .show()
    }

    // Función para mostrar un AlertDialog de éxito
    private fun MostrarDialogExito(titulo: String, mensaje: String, accion: () -> Unit) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Continuar") { dialog, _ ->
                // Iniciar la MainActivity después del éxito
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish() // Opcional: cerrar la actividad actual
                dialog.dismiss()
            }
            .create()
            .show()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }

}