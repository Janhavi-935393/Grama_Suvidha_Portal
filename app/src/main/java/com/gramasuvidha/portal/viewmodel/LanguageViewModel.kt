package com.gramasuvidha.portal.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "settings")

class LanguageViewModel(private val context: Context) : ViewModel() {

    private val LANGUAGE_KEY = stringPreferencesKey("language")

    val currentLanguage: StateFlow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LANGUAGE_KEY] ?: "en"
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    fun toggleLanguage() {
        viewModelScope.launch {
            context.dataStore.edit { preferences ->
                val current = preferences[LANGUAGE_KEY] ?: "en"
                preferences[LANGUAGE_KEY] = if (current == "en") "kn" else "en"
            }
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LanguageViewModel::class.java)) {
                return LanguageViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
