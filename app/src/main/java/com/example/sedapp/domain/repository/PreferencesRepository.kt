package com.example.sedapp.domain.repository

interface PreferencesRepository {
    suspend fun isFirstLaunch(): Boolean
    suspend fun setFirstLaunchCompleted()
}