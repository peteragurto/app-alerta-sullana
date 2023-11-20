package com.example.alertasullana.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseRepository {
    private val mAuth = FirebaseAuth.getInstance()

    // Método para obtener el usuario actual
    fun obtenerUsuarioActual(): FirebaseUser? {
        return mAuth.currentUser
    }

    // Método para cerrar sesión
    fun cerrarSesion() {
        mAuth.signOut()
    }
}