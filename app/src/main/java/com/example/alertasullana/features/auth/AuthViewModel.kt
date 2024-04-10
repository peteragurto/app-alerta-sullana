package com.example.alertasullana.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alertasullana.data.repository.AuthRepositoryImpl
import com.example.alertasullana.data.repository.FirebaseAuthRepositoryImpl
import com.example.alertasullana.data.repository.GoogleAuthRepositoryImpl
import com.example.alertasullana.features.auth.state.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl,
    private val googleAuthRepositoryImpl: GoogleAuthRepositoryImpl,
    private val authRepositoryImpl: AuthRepositoryImpl
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            if (authRepositoryImpl.isUserLoggedIn()) {
                _authState.value = AuthState.Success
                _userId.value = authRepositoryImpl.getUserId()
            } else {
                _authState.value = AuthState.Idle
                _userId.value = null
            }
        }
    }


    fun isUserLoggedIn(): Boolean {
        return runBlocking {
            authRepositoryImpl.isUserLoggedIn()
        }
    }

    fun getCurrentUserId(): String? {
        return runBlocking {
            authRepositoryImpl.getUserId()
        }
    }

    fun logout() {
        viewModelScope.launch {
            firebaseAuthRepositoryImpl.logout()
            googleAuthRepositoryImpl.logout()
            authRepositoryImpl.setUserLoggedIn(false)
            authRepositoryImpl.setUserId(null)
        }
    }

}