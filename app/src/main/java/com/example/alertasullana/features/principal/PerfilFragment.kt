package com.example.alertasullana.features.principal

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.alertasullana.R
import com.example.alertasullana.data.repository.FirebaseAuthRepositoryImpl
import com.example.alertasullana.data.repository.UsuarioRepository
import com.example.alertasullana.features.viewmodel.PerfilViewModel
import com.example.alertasullana.features.viewmodel.PerfilViewModelFactory
import com.google.android.material.materialswitch.MaterialSwitch


class PerfilFragment : Fragment() {

    //Importando ViewModel de Perfil
    private lateinit var perfilViewModel: PerfilViewModel


    // Declara switchNotificaciones como una propiedad de PerfilFragment
    private lateinit var switchNotificaciones: MaterialSwitch
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

        //SWITCH-------------------------------------------------------------------------
        // En tu Fragment o Activity, obtén las preferencias compartidas
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)

        // Obtén el estado actual del switch desde las preferencias compartidas
        val switchState = sharedPreferences.getBoolean("switchState", true)

        // Asigna el estado al MaterialSwitch
        switchNotificaciones = view.findViewById(R.id.sw_notificaciones)
        switchNotificaciones.isChecked = switchState
        //SWITCH-------------------------------------------------------------------------


        //-------------------------------------------------------RECUPERACIÓN DE DATOS DE USUARIO
        // Inicializar ViewModel con el repositorio
        perfilViewModel = ViewModelProvider(
            this,
            PerfilViewModelFactory(FirebaseAuthRepositoryImpl(), usuarioRepository)
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
        view?.findViewById<Button>(R.id.btnSalirApp)?.setOnClickListener {
            perfilViewModel.cerrarSesionYNavegarARegistro()
        }
        //-----------------------------------------CERRAR SESION Y NAVEGAR A REGISTRO


        //SWITCH FUNCIONALIDAD------------------------------------------------------------
        // Agrega un Listener para el cambio de estado del switch
        switchNotificaciones.setOnCheckedChangeListener { _, isChecked ->
            // Solo mostrar el AlertDialog si el usuario cambió el estado del switch
            if (switchNotificaciones.isPressed) {
                // Mostrar AlertDialog para confirmar la acción
                val message = if (isChecked) "¿Desea activar las notificaciones?" else "¿Desea desactivar las notificaciones?"

                AlertDialog.Builder(requireContext())
                    .setMessage(message)
                    .setPositiveButton("Sí") { _, _ ->
                        // Usuario hizo clic en "Sí", guardar el nuevo estado y realizar acciones
                        with(sharedPreferences.edit()) {
                            putBoolean("switchState", isChecked)
                            apply()
                        }

                        // Realizar acciones según el estado del switch
                        if (isChecked) {
                            // El switch está activado, navegar a la configuración de notificaciones
                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                            }
                            startActivity(intent)
                        } else {
                            // El switch está desactivado, navegar a la configuración de notificaciones
                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                            }
                            startActivity(intent)
                        }
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        // Usuario hizo clic en "No", restaurar el estado anterior del switch
                        switchNotificaciones.isChecked = !isChecked
                        // Cerrar el AlertDialog
                        dialog.dismiss()
                    }
                    .show()
            }
        }


        return view
    }

    override fun onResume() {
        super.onResume()

        // Obtén una instancia de NotificationManagerCompat
        val notificationManager = NotificationManagerCompat.from(requireContext())

        // Comprueba si las notificaciones están activadas
        val areNotificationsEnabled = notificationManager.areNotificationsEnabled()

        // Actualiza el estado del switch para reflejar el estado actual de las notificaciones
        switchNotificaciones.isChecked = areNotificationsEnabled
    }

    companion object {

    }
}