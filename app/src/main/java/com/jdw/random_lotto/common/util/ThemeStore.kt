package com.jdw.random_lotto.common.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("settings")

object ThemeStore {
    private val KEY = stringPreferencesKey("theme_mode")
    fun themeModeFlow(context: Context): Flow<ThemeMode> =
        context.dataStore.data.map { prefs ->
            when (prefs[KEY]) {
                "LIGHT" -> ThemeMode.LIGHT
                "DARK" -> ThemeMode.DARK
                else -> ThemeMode.FOLLOW_SYSTEM
            }
        }
    suspend fun saveThemeMode(context: Context, mode: ThemeMode) {
        context.dataStore.edit { it[KEY] = mode.name }
    }
}

enum class ThemeMode {
    LIGHT, DARK, FOLLOW_SYSTEM
}