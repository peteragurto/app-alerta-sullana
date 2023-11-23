package com.example.alertasullana.ui.principal

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.alertasullana.R


class HacerReporteFragment : Fragment() {

    //Imagen cargada del anterior fragmento
    private var imageUri: Uri? = null
    //Clase para recibir argumentos
    private val args:HacerReporteFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageUri = Uri.parse(args.imageUri)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //AAqui cargo la imagen
        // ImageView
        val imagenDelito: ImageView = requireView().findViewById(R.id.imagen_delito)
        // Cargar la imagen con Glide
        imageUri?.let {
            Glide.with(this)
                .load(it)
                .into(imagenDelito)
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hacer_reporte, container, false)
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