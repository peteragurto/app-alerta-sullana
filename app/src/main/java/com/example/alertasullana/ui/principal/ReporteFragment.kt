package com.example.alertasullana.ui.principal

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.alertasullana.R
import com.example.alertasullana.data.model.Reporte
import com.example.alertasullana.ui.viewmodel.MapSheet
import com.example.alertasullana.ui.viewmodel.ReporteViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReporteFragment : Fragment() {

    // Obtener una instancia del ViewModel
    private val reporteViewModel: ReporteViewModel by viewModels()
    // Adaptador para el ListView
    private lateinit var reporteListAdapter: ReporteListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reporte, container, false)

        // Configurar el ListView
        val listViewReportes: ListView = view.findViewById(R.id.listViewReportes)
        reporteListAdapter = ReporteListAdapter(requireContext(), R.layout.lista_reporte_item, mutableListOf())
        listViewReportes.adapter = reporteListAdapter

        // Observar la lista de reportes en el ViewModel
        reporteViewModel.getReportes().observe(viewLifecycleOwner, Observer { reportes ->
            // Actualizar el adaptador con la nueva lista de reportes
            reporteListAdapter.clear()
            reporteListAdapter.addAll(reportes)
            reporteListAdapter.notifyDataSetChanged()
        })

        // Cargar los reportes desde Firestore
        reporteViewModel.cargarReportes()

        // Agregar un listener al ListView para manejar clics en los reportes
        listViewReportes.setOnItemClickListener(AdapterView.OnItemClickListener { _, _, position, _ ->
            // Aquí puedes manejar el clic en un reporte específico si es necesario
            val reporteSeleccionado = reporteListAdapter.getItem(position)
            // Puedes abrir un nuevo fragmento o actividad para mostrar los detalles del reporte
            // Crea y muestra el BottomSheet
            mostrarBottomSheet(reporteSeleccionado)

        })

        return view
    }
    private fun mostrarBottomSheet(reporte: Reporte?) {
        reporte?.let {
            Log.d("BottomSheet", "Descripcion: ${reporte.descripcionDelito}, Fecha: ${reporte.fecha}, Bitmap: ${reporte.bitmap}")
            val bottomSheetFragment = MapSheet.newInstance(reporte.descripcionDelito, reporte.fecha, reporte.bitmap)
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }
    }
}


class ReporteListAdapter(context: Context, resource: Int, objects: List<Reporte>) :

    ArrayAdapter<Reporte>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.lista_reporte_item, parent, false)

        // Obtener el item actual
        val reporte = getItem(position)

        // Referencias a las vistas del diseño
        val imageViewDelito: ImageView = itemView.findViewById(R.id.imageViewDelito)
        val textViewTituloDelito: TextView = itemView.findViewById(R.id.textViewTituloDelito)
        val textFechaDelito: TextView = itemView.findViewById(R.id.textFechaDelito)

        // Asignar valores a las vistas
        reporte?.let {
            // Cargar la imagen utilizando Glide
            Glide.with(context)
                .load(reporte.bitmap)
                .placeholder(R.drawable.googleicon) // Placeholder mientras se carga la imagen
                .into(imageViewDelito)

            textViewTituloDelito.text = reporte.descripcionDelito

            // Formatear y mostrar la fecha del delito
            val formattedDate = formatDate(reporte.fecha)
            textFechaDelito.text = formattedDate
        }

        return itemView
    }

    // Método para formatear la fecha del delito
    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }
}
