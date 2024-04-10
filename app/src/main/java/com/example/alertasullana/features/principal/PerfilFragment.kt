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
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.alertasullana.R
import com.example.alertasullana.data.repository.UsuarioRepository
import com.example.alertasullana.features.auth.AuthActivity
import com.example.alertasullana.features.auth.AuthViewModel
import com.example.alertasullana.features.viewmodel.PerfilViewModel
import com.google.android.material.materialswitch.MaterialSwitch


class PerfilFragment : Fragment() {

    //Importando ViewModel de Perfil
    private lateinit var perfilViewModel: PerfilViewModel
    private lateinit var authViewModel: AuthViewModel


    // Declara switchNotificaciones como una propiedad de PerfilFragment
    private lateinit var switchNotificaciones: MaterialSwitch
    private lateinit var btnSalir: Button
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

        //---------------------------------------------------RECUPERACIÓN DE DATOS DE USUARIO


        //-------------------------------------------CERRAR SESION Y NAVEGAR A REGISTRO



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

        btnSalir = view.findViewById(R.id.btnSalirApp)
        btnSalir.setOnClickListener {
            authViewModel.logout()
            goToAuth()
        }


        return view
    }

    private fun goToAuth() {
        val intentSalir = Intent(context, AuthActivity::class.java)
        startActivity(intentSalir)
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


}