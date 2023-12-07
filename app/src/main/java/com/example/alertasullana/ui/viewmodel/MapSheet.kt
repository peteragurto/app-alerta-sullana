package com.example.alertasullana.ui.viewmodel

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.alertasullana.R
import com.example.alertasullana.data.model.Reporte
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Locale


class MapSheet : BottomSheetDialogFragment() {
    private lateinit var tituloTextView: TextView
    private lateinit var imgMarcador: ImageView
    private lateinit var fechaSheet: TextView
    private lateinit var descripcionSheet: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map_sheet, container, false)

        // Inicializar vistas
        tituloTextView = view.findViewById(R.id.tituloTextView)
        imgMarcador = view.findViewById(R.id.imgMarcador)
        fechaSheet = view.findViewById(R.id.fecha_sheet)
        descripcionSheet = view.findViewById(R.id.descripcion_sheet)

        return view
    }

    fun mostrarReporte(reporte: Reporte) {
        tituloTextView.text = "Reporte"
        imgMarcador.setImageBitmap(reporte.bitmap)
        fechaSheet.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(reporte.fecha)
        descripcionSheet.text = reporte.descripcionDelito
    }
}

