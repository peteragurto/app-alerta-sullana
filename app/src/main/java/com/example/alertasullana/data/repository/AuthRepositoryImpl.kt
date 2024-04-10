package com.example.alertasullana.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.alertasullana.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val preferencesDataStore: DataStore<Preferences>
): AuthRepository {

    private companion object {
        val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    override suspend fun setUserLoggedIn(isLogged: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_KEY] = isLogged
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return preferencesDataStore.data.first()[IS_LOGGED_IN_KEY] ?: false
    }

    override suspend fun setUserId(userId: String?) {
        preferencesDataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId ?: ""
        }
    }

    override suspend fun getUserId(): String? {
        return preferencesDataStore.data.first()[USER_ID_KEY]
    }
}