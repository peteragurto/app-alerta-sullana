package com.example.alertasullana.features.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alertasullana.data.repository.FirebaseAuthRepositoryImpl
import com.example.alertasullana.data.repository.UsuarioRepository

class PerfilViewModel(private val firebaseRepositoryImpl: FirebaseAuthRepositoryImpl,//LLamar a Usuario Repository
                      private val usuarioRepository: UsuarioRepository
) : ViewModel() {
    private val _nombreUsuario = MutableLiveData<String>()
    private val _correoUsuario = MutableLiveData<String>()
    private val _urlFotoPerfil = MutableLiveData<String>()
    //Para navegación
    private val _navegarARegistro = MutableLiveData<Boolean>()


    val nombreUsuario: LiveData<String> get() = _nombreUsuario
    val correoUsuario: LiveData<String> get() = _correoUsuario
    val urlFotoPerfil: LiveData<String> get() = _urlFotoPerfil
    val navegarARegistro: LiveData<Boolean> get() = _navegarARegistro

    init {

    }

    // Método para actualizar los datos del usuario
    fun actualizarDatosUsuario(nombre: String, correo: String, urlFoto: String) {
        _nombreUsuario.value = nombre
        _correoUsuario.value = correo
        _urlFotoPerfil.value = urlFoto
    }

}