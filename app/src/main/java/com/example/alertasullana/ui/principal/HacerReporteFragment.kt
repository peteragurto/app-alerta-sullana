package com.example.alertasullana.ui.principal

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.alertasullana.R


class HacerReporteFragment : Fragment() {

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
        val imageView: ImageView = view.findViewById(R.id.imagen_delito)
        // Recuperar la imagen de los argumentos
        val imageBitmap = arguments?.getParcelable<Bitmap>("image")
        imageView.setImageBitmap(imageBitmap)


        // Inflate the layout for this fragment
        return view
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