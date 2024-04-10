package com.example.alertasullana.domain.repository

interface AuthRepository {
    suspend fun setUserLoggedIn(isLogged: Boolean)
    suspend fun isUserLoggedIn() : Boolean
    suspend fun setUserId(userId: String?)
    suspend fun getUserId() : String?
}