package com.example.alertasullana.ui.principal

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.alertasullana.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID


class HacerReporteFragment : Fragment() {
    //creamos variantes donde se almacenan datos
    private lateinit var imageView: ImageView
    private lateinit var desEditText: EditText
    private lateinit var btn_subir: Button
    //creamos variantes para activar servicios
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference

    //Imagen cargada del anterior fragmento
    //private var imageUri: Uri? = null
    //Clase para recibir argumentos
    //private val args:HacerReporteFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //imageUri = Uri.parse(args.imageUri)


    }

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

        // Recuperar la imagen de los argumentos
        val imageBitmap = arguments?.getParcelable<Bitmap>("image")
        imageView.setImageBitmap(imageBitmap)

        btn_subir.setOnClickListener {
            // Subir la imagen a Firebase Storage
            val imageUrl = SubirImg(imageBitmap)

            // Subir los datos a Firestore
            SubirDatos(imageUrl)
        }

        // Inflate the layout for this fragment
        return view
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
    private fun SubirDatos(imageUrl: String) {
        val descripcionDelito = desEditText.text.toString()

        // Crear un nuevo documento en Firestore para el reporte
        val reporte = hashMapOf(
            "descripcionDelito" to descripcionDelito,
            "imageUrl" to imageUrl,
            "fecha" to FieldValue.serverTimestamp()
            // Puedes agregar más campos según tus necesidades
        )

        firestore.collection("reportes").add(reporte)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
            }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HacerReporteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HacerReporteFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}