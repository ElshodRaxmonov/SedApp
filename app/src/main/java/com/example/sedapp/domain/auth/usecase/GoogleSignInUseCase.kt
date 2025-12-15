package com.example.sedapp.domain.auth.usecase

import com.example.sedapp.domain.auth.repository.AuthRepository
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(token: String) = repo.signInWithGoogle(token)
}