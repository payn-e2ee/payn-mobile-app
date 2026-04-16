package com.example.payn.core.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import java.io.IOException

private const val DATASTORE_NAME = "app_prefs"

val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class KeyValueStorage(private val context: Context) {
    private val dataStore = context.dataStore

    private fun <T> readValue(
        key: Preferences.Key<T>,
        default: T
    ): Flow<T> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { prefs -> prefs[key] ?: default }
    }

    private suspend fun <T> writeValue(
        key: Preferences.Key<T>,
        value: T
    ) {
        dataStore.edit { prefs ->
            prefs[key] = value
        }
    }

    fun getString(key: String, default: String = ""): Flow<String> {
        return readValue(stringPreferencesKey(key), default)
    }

    suspend fun putString(key: String, value: String) {
        writeValue(stringPreferencesKey(key), value)
    }

    fun getInt(key: String, default: Int = 0): Flow<Int> {
        return readValue(intPreferencesKey(key), default)
    }

    suspend fun putInt(key: String, value: Int) {
        writeValue(intPreferencesKey(key), value)
    }

    fun getBoolean(key: String, default: Boolean = false): Flow<Boolean> {
        return readValue(booleanPreferencesKey(key), default)
    }

    suspend fun putBoolean(key: String, value: Boolean) {
        writeValue(booleanPreferencesKey(key), value)
    }

    fun getLong(key: String, default: Long = 0L): Flow<Long> {
        return readValue(longPreferencesKey(key), default)
    }

    suspend fun putLong(key: String, value: Long) {
        writeValue(longPreferencesKey(key), value)
    }

    fun getFloat(key: String, default: Float = 0f): Flow<Float> {
        return readValue(floatPreferencesKey(key), default)
    }

    suspend fun putFloat(key: String, value: Float) {
        writeValue(floatPreferencesKey(key), value)
    }

    suspend fun remove(key: String) {
        dataStore.edit { prefs ->
            prefs.remove(stringPreferencesKey(key))
            prefs.remove(intPreferencesKey(key))
            prefs.remove(booleanPreferencesKey(key))
            prefs.remove(longPreferencesKey(key))
            prefs.remove(floatPreferencesKey(key))
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}