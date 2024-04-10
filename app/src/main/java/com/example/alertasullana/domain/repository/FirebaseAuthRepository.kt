package com.example.alertasullana.domain.repository

interface FirebaseAuthRepository {
    // Registro de usuario con correo electrónico y contraseña
    suspend fun registerWithEmail(email: String, password: String)

    // Inicio de sesión con correo electrónico y contraseña
    suspend fun loginWithEmail(email: String, password: String)

    // Cierre de sesión
    fun logout()

    // Verificación de si el usuario actual está autenticado
    fun isUserLoggedIn(): Boolean

    // Obtener el ID de usuario del usuario actualmente autenticado
    fun getCurrentUserId(): String?
}