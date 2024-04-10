package com.example.alertasullana.features.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertasullana.data.repository.AuthRepositoryImpl
import com.example.alertasullana.data.repository.FirebaseAuthRepositoryImpl
import com.example.alertasullana.features.auth.state.AuthState
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl,
    private val authRepositoryImpl: AuthRepositoryImpl
) : ViewModel() {

    private val _authRegisterStateFlow = MutableStateFlow<AuthState>(AuthState.Loading)
    val authRegisterStateFlow: StateFlow<AuthState> = _authRegisterStateFlow.asStateFlow()

    fun registerWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _authRegisterStateFlow.value = AuthState.Loading
            try {
                firebaseAuthRepositoryImpl.registerWithEmail(email, password)
                val currentUserUid = firebaseAuthRepositoryImpl.getCurrentUserId()
                if (currentUserUid != null) {
                    authRepositoryImpl.setUserLoggedIn(true)
                    authRepositoryImpl.setUserId(currentUserUid)
                    _authRegisterStateFlow.value = AuthState.Success
                } else {
                    _authRegisterStateFlow.value = AuthState.Error("Error desconocido")
                }
            } catch (e: FirebaseAuthWeakPasswordException) {
                _authRegisterStateFlow.value = AuthState.Error("La contraseña es muy débil")
            } catch (e: FirebaseAuthUserCollisionException) {
                _authRegisterStateFlow.value = AuthState.Error("Este correo ya está en uso")
            } catch (e: FirebaseAuthEmailException) {
                _authRegisterStateFlow.value = AuthState.Error("Este correo no es válido")
            } catch (e: Exception) {
                _authRegisterStateFlow.value = AuthState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
