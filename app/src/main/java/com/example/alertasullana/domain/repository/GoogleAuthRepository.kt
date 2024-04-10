package com.example.alertasullana.domain.repository

import com.google.firebase.auth.FirebaseUser

interface GoogleAuthRepository {

    suspend fun loginWithGoogle(idToken: String): Result<FirebaseUser>

    fun isUserLoggedIn(): Boolean

    fun getCurrentUserId(): String?

    fun logout()
}