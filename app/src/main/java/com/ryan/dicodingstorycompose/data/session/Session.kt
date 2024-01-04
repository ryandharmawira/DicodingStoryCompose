package com.ryan.dicodingstorycompose.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("app_preferences")

class Session @Inject constructor(@ApplicationContext appContext: Context) {
    private val dataStore = appContext.dataStore
    private val TOKEN_KEY = stringPreferencesKey("token_key")

    fun getTokenFlow(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    suspend fun saveToken(value: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = value
        }
    }
}