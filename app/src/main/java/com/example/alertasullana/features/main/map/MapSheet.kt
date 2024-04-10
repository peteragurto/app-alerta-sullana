package com.example.alertasullana.features.main.map

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.alertasullana.R
import com.example.alertasullana.data.services.OnMapSheetActionListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MapSheet : BottomSheetDialogFragment() {
    private lateinit var tituloTextView: TextView
    private lateinit var imgMarcador: ImageView
    private lateinit var fechaSheet: TextView
    private lateinit var descripcionSheet: TextView
    private lateinit var botonVerDelitoEnMapa: MaterialButton
    var listener: OnMapSheetActionListener? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map_sheet, container, false)

        // Referencias a las vistas
        tituloTextView = view.findViewById(R.id.tituloTextView)
        imgMarcador = view.findViewById(R.id.imgMarcador)
        fechaSheet = view.findViewById(R.id.fecha_sheet)
        descripcionSheet = view.findViewById(R.id.descripcion_sheet)
        botonVerDelitoEnMapa = view.findViewById(R.id.boton_ver_en_mapa)

        // Obtener los datos del Bundle
        val descripcionDelito = arguments?.getString("descripcionDelito")
        val fecha = arguments?.getLong("fecha")
        val date = fecha?.let { Date(it) }
        val bitmap = arguments?.getParcelable<Bitmap>("bitmap")

        // Llenar las vistas con los datos
        tituloTextView.text = descripcionDelito
        descripcionSheet.text = descripcionDelito

        Glide.with(this)
            .load(bitmap)
            .into(imgMarcador)

        val dateFormat = SimpleDateFormat("dd 'de' MMMM 'del' yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        val formattedDate = date?.let { dateFormat.format(it) }
        val formattedTime = date?.let { timeFormat.format(it) }

        fechaSheet.text = "La fecha de este delito fue el $formattedDate a las $formattedTime"

        botonVerDelitoEnMapa.setOnClickListener {
            // Obtener la latitud y la longitud del Bundle
            val latitud = arguments?.getDouble("ubicacion.latitud")
            val longitud = arguments?.getDouble("ubicacion.longitud")

            // Llamar a onMapAction con la latitud y la longitud
            listener?.onMapAction(latitud ?: 0.0, longitud ?: 0.0)

            // Cerrar el BottomSheetDialogFragment
            dismiss()
        }

        return view
    }

}

