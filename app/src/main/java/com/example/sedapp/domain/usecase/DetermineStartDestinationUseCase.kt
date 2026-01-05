package com.example.sedapp.domain.usecase

import com.example.sedapp.domain.model.AppStartDestination
import com.example.sedapp.domain.repository.AuthRepository
import com.example.sedapp.domain.repository.PreferencesRepository
import javax.inject.Inject

class DetermineStartDestinationUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(): AppStartDestination {
        return when {
            preferencesRepository.isFirstLaunch() -> AppStartDestination.Onboarding
            authRepository.isLoggedIn() -> AppStartDestination.Dashboard
            else -> AppStartDestination.Auth
        }
    }
}
