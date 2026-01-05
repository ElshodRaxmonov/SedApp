package com.example.sedapp.domain.usecase.auth

import com.example.sedapp.domain.repository.AuthRepository
import javax.inject.Inject

class SignInEmailUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = runCatching {
        validateEmail(email)
        repo.signInWithEmailAndPassword(email, password).getOrThrow()
    }

    private fun validateEmail(email: String) {
        if (!email.endsWith("@gmail.com")) {
            throw IllegalArgumentException("Email should end with @gmail.com")
        }
    }
}