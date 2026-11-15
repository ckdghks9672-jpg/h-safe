package com.example.myapplication.shared.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.shared.model.Incident
import com.example.myapplication.shared.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sagohanjang")

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

class ProfileRepository(private val context: Context) {
    private val key = stringPreferencesKey("user_profile")

    val profile: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        prefs[key]?.let { runCatching { json.decodeFromString<UserProfile>(it) }.getOrNull() }
            ?: UserProfile()
    }

    suspend fun save(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[key] = json.encodeToString(profile)
        }
    }

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(key)
        }
    }
}

class IncidentRepository(private val context: Context) {
    private val activeKey = stringPreferencesKey("active_incident")
    private val historyKey = stringPreferencesKey("incident_history")

    val activeIncident: Flow<Incident?> = context.dataStore.data.map { prefs ->
        prefs[activeKey]?.let { runCatching { json.decodeFromString<Incident>(it) }.getOrNull() }
    }

    suspend fun saveActive(incident: Incident) {
        context.dataStore.edit { prefs ->
            prefs[activeKey] = json.encodeToString(incident)
            val history = prefs[historyKey]
                ?.let { runCatching { json.decodeFromString<List<Incident>>(it) }.getOrNull() }
                .orEmpty()
                .filterNot { it.id == incident.id }
                .toMutableList()
            history.add(0, incident)
            prefs[historyKey] = json.encodeToString(history.take(20))
        }
    }

    suspend fun clearActive() {
        context.dataStore.edit { prefs ->
            prefs.remove(activeKey)
        }
    }
}
