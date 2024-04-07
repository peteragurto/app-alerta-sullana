package com.example.alertasullana.features.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alertasullana.data.repository.FirebaseAuthRepositoryImpl
import com.example.alertasullana.data.repository.UsuarioRepository

class PerfilViewModelFactory(private val firebaseRepositoryImpl: FirebaseAuthRepositoryImpl, private val usuarioRepository: UsuarioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            return PerfilViewModel(firebaseRepositoryImpl, usuarioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}