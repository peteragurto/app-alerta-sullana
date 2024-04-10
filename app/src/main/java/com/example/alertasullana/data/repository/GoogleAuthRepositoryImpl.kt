package com.example.alertasullana.data.repository

import com.example.alertasullana.domain.repository.GoogleAuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleAuthRepositoryImpl @Inject constructor(
    private val googleSignInClient: GoogleSignInClient,
    private val firebaseAuth: FirebaseAuth
) : GoogleAuthRepository {

    override suspend fun loginWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            Result.success(authResult.user!!)
        } catch (e:Exception) {
            Result.failure(e)
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override fun logout() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
    }
}