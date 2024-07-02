package com.example.aplikasistoryapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.aplikasistoryapp.api.ApiConfig
import com.example.aplikasistoryapp.data.StoryRepository
import com.example.aplikasistoryapp.preference.LoginPreference

val Context.dataStore: DataStore<Preferences> by preferencesDataStore("storiesin")

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val preferences = LoginPreference(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(preferences, apiService)
    }
}