package com.example.alertasullana.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UsuarioEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val correo: String,
    val urlFotoPerfil: String?
)