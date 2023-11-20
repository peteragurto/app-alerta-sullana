package com.example.alertasullana.ui.principal

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.alertasullana.R
import com.example.alertasullana.data.repository.FirebaseRepository
import com.example.alertasullana.ui.registro.RegistroUsuarioActivity
import com.example.alertasullana.ui.viewmodel.PerfilViewModel
import com.example.alertasullana.ui.viewmodel.PerfilViewModelFactory


/**
 * A simple [Fragment] subclass.
 * Use the [PerfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PerfilFragment : Fragment() {

    //Importando ViewModel de Perfil
    private lateinit var perfilViewModel: PerfilViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        // Obtener referencias
        val btnSalir = view.findViewById<Button>(R.id.btn_salirPerfil)//Minimizar perfil
        val btnCerrarSesion = view.findViewById<Button>(R.id.btnSalirApp)//Cerrar sesión

        //-------------------------------------------------------RECUPERACIÓN DE DATOS DE USUARIO
        // Inicializar ViewModel con el repositorio
        perfilViewModel = ViewModelProvider(this, PerfilViewModelFactory(FirebaseRepository())).get(PerfilViewModel::class.java)
        // Observar cambios en los datos del usuario y actualizar la interfaz
        perfilViewModel.nombreUsuario.observe(viewLifecycleOwner) { nombre ->
            view?.findViewById<TextView>(R.id.nombreUser)?.text = nombre
        }
        perfilViewModel.correoUsuario.observe(viewLifecycleOwner) { correo ->
            view?.findViewById<TextView>(R.id.correoUser)?.text = correo
        }
        perfilViewModel.urlFotoPerfil.observe(viewLifecycleOwner) { urlFoto ->
            // Utilizar Glide para cargar la imagen desde la URL
            view?.findViewById<ImageView>(R.id.imagenProfile)?.let {
                Glide.with(requireContext())
                    .load(urlFoto)
                    .placeholder(R.drawable.usuario) // Recurso por defecto si no hay URL de foto
                    .into(it)
            }
        }
        //---------------------------------------------------RECUPERACIÓN DE DATOS DE USUARIO


        //-------------------------------------------CERRAR SESION Y NAVEGAR A REGISTRO
        // Observar cambios en la navegación y realizar la acción correspondiente
        perfilViewModel.navegarARegistro.observe(viewLifecycleOwner) { navegar ->
            if (navegar) {
                // Navegar a la actividad de registro
                val intent = Intent(activity, RegistroUsuarioActivity::class.java)
                startActivity(intent)
                // Limpiar la pila de actividades para que el usuario no pueda volver atrás
                requireActivity().finishAffinity()
            }
        }
        view?.findViewById<Button>(R.id.btnSalirApp)?.setOnClickListener {
            perfilViewModel.cerrarSesionYNavegarARegistro()
        }
        //-----------------------------------------CERRAR SESION Y NAVEGAR A REGISTRO


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