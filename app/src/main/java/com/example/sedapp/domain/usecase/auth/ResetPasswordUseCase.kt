package com.example.sedapp.domain.usecase.auth

import com.example.sedapp.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String) = repo.sendPasswordResetEmail(email)
}