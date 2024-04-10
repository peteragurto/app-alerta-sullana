package com.example.alertasullana.features.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertasullana.data.repository.AuthRepositoryImpl
import com.example.alertasullana.data.repository.FirebaseAuthRepositoryImpl
import com.example.alertasullana.data.repository.GoogleAuthRepositoryImpl
import com.example.alertasullana.features.auth.state.AuthState
import com.google.android.gms.auth.GoogleAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl,
    private val googleAuthRepositoryImpl: GoogleAuthRepositoryImpl,
    private val authRepositoryImpl: AuthRepositoryImpl
): ViewModel() {

    private val _authStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    fun signUpWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _authStateFlow.value = AuthState.Loading
            try {
                firebaseAuthRepositoryImpl.loginWithEmail(email, password)
                val currentUserUid = firebaseAuthRepositoryImpl.getCurrentUserId()
                if (currentUserUid != null) {
                    authRepositoryImpl.setUserLoggedIn(true)
                    authRepositoryImpl.setUserId(currentUserUid)
                    _authStateFlow.value = AuthState.Success
                } else {
                    _authStateFlow.value = AuthState.Error("Error desconocido")
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _authStateFlow.value = AuthState.Error("Credenciales inválidas")
            } catch (e: FirebaseAuthInvalidUserException) {
                _authStateFlow.value = AuthState.Error("Usuario no encontrado")
            } catch (e: Exception) {
                _authStateFlow.value = AuthState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun signUpWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authStateFlow.value = AuthState.Loading
            try {
                googleAuthRepositoryImpl.loginWithGoogle(idToken)
                val currentUserUid = googleAuthRepositoryImpl.getCurrentUserId()
                if (currentUserUid != null) {
                    authRepositoryImpl.setUserLoggedIn(true)
                    authRepositoryImpl.setUserId(currentUserUid)
                    _authStateFlow.value = AuthState.Success
                } else {
                    _authStateFlow.value = AuthState.Error("Error desconocido")
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is GoogleAuthException -> e.localizedMessage ?: "Error con Google Sign-In"
                    else -> e.message ?: "Error desconocido"
                }
                _authStateFlow.value = AuthState.Error(errorMessage)
            }
        }
    }

    fun setAuthState(state: AuthState) {
        _authStateFlow.value = state
    }

    fun isUserLoggedIn() : Boolean {
        return runBlocking {
            authRepositoryImpl.isUserLoggedIn()
        }
    }

    fun getCurrentUserId() : String? {
        return runBlocking {
            authRepositoryImpl.getUserId()
        }
    }

    fun logOut() {
        viewModelScope.launch {
            firebaseAuthRepositoryImpl.logout()
            googleAuthRepositoryImpl.logout()
            authRepositoryImpl.setUserLoggedIn(false)
            authRepositoryImpl.setUserId(null)
        }
    }
}
