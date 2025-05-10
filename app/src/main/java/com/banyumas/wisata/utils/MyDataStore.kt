package com.banyumas.wisata.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MyDataStore(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val tokenKey = stringPreferencesKey("auth_token")
    }

    val token: Flow<String?> = dataStore.data.map { preference ->
        preference[tokenKey]
    }

    suspend fun saveTokenKey(token: String) {
        dataStore.edit { preference ->
            preference[tokenKey] = token
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preference ->
            preference.remove(tokenKey)
        }
    }
}

