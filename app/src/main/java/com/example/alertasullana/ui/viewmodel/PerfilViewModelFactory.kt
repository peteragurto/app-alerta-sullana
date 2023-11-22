package com.example.alertasullana.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.alertasullana.data.repository.FirebaseRepository
import com.example.alertasullana.data.repository.UsuarioRepository

class PerfilViewModelFactory(private val firebaseRepository: FirebaseRepository, private val usuarioRepository: UsuarioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            return PerfilViewModel(firebaseRepository, usuarioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}