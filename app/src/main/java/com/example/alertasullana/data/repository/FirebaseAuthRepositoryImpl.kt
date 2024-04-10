package com.example.alertasullana.data.repository

import com.example.alertasullana.domain.repository.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : FirebaseAuthRepository {

    override suspend fun registerWithEmail(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun loginWithEmail(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
}