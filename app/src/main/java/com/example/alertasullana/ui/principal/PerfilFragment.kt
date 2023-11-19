package com.example.alertasullana.ui.principal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.alertasullana.R


/**
 * A simple [Fragment] subclass.
 * Use the [PerfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PerfilFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        // Obtener referencia al botón de salir perfil
        val btnSalir = view.findViewById<Button>(R.id.btn_salirPerfil)
        // Agregar listener al botón
        btnSalir.setOnClickListener {
            // Obtener el manejador de fragmentos
            val fragmentManager = requireActivity().supportFragmentManager
            // Obtener referencia al fragmento actual
            val fragment = fragmentManager.findFragmentById(R.id.perfil_fragment_id)
            // Si hay un fragmento
            if (fragment != null) {
                // Comenzar transacción
                val transaction = fragmentManager.beginTransaction()
                // Ocultar el fragmento
                transaction.hide(fragment)
                // Confirmar cambios
                transaction.commit()
            }
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PerfilFragment.
         */
        // TODO: Rename and change types and number of parameters
    }
}