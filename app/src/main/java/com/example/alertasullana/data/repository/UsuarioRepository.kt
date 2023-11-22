package com.example.alertasullana.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseUser

class UsuarioRepository(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("nombre_pref", Context.MODE_PRIVATE)

    fun guardarDatosEnSharedPreferences(currentUser: FirebaseUser) {
        val editor = sharedPreferences.edit()
        editor.putString("nombreUsuario", currentUser.displayName)
        editor.putString("correoUsuario", currentUser.email)
        editor.putString("urlFotoPerfil", currentUser.photoUrl?.toString())
        editor.apply()
    }
}