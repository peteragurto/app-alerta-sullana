package com.example.alertasullana.ui.viewmodel

import android.graphics.Bitmap  // Importante agregar esta línea
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.alertasullana.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("UNREACHABLE_CODE")
class MapSheet : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_sheet, container, false)
        // Obtener datos del Bundle
        val descripcion = arguments?.getString("descripcion", "")
        val fecha = arguments?.getSerializable("fecha") as Date?
        val bitmap = arguments?.getParcelable("bitmap") as Bitmap?

        Log.d("MapSheet", "Descripcion: $descripcion, Fecha: $fecha, Bitmap: $bitmap")

        val tituloTextView: TextView? = view?.findViewById(R.id.tituloTextView)
        val imgMarcador: ImageView? = view?.findViewById(R.id.imgMarcador)
        val fechaSheet: TextView? = view?.findViewById(R.id.fecha_sheet)
        val descripcionSheet: TextView? = view?.findViewById(R.id.descripcion_sheet)

        tituloTextView?.text = descripcion
        imgMarcador?.setImageBitmap(bitmap)
        fechaSheet?.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(fecha)
        descripcionSheet?.text = descripcion

        return view
    }
    companion object {
        fun newInstance(descripcion: String, fecha: Date, bitmap: Bitmap?): MapSheet {
            val args = Bundle()
            args.putString("descripcion", descripcion)
            args.putSerializable("fecha", fecha)
            args.putParcelable("bitmap", bitmap)
            val fragment = MapSheet()
            fragment.arguments = args
            return fragment
        }
    }

}

