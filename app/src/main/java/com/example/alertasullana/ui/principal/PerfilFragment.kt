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
import com.example.alertasullana.data.repository.UsuarioRepository
import com.example.alertasullana.ui.splash.IntroActivity
import com.example.alertasullana.ui.viewmodel.PerfilViewModel
import com.example.alertasullana.ui.viewmodel.PerfilViewModelFactory


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

        // Crear el repositorio de usuario
        val usuarioRepository = UsuarioRepository(requireContext()) // Puedes ajustar esto según tus necesidades


        //-------------------------------------------------------RECUPERACIÓN DE DATOS DE USUARIO
        // Inicializar ViewModel con el repositorio
        perfilViewModel = ViewModelProvider(
            this,
            PerfilViewModelFactory(FirebaseRepository(), usuarioRepository)
        ).get(PerfilViewModel::class.java)
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
                val intent = Intent(activity, IntroActivity::class.java)
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

    }
}