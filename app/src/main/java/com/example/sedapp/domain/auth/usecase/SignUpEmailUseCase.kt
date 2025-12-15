package com.example.sedapp.domain.auth.usecase

import com.example.sedapp.domain.auth.repository.AuthRepository
import javax.inject.Inject

class SignUpEmailUseCase @Inject constructor(private val repo: AuthRepository) {
    suspend operator fun invoke(name: String, email: String, password: String) = runCatching {
        validateEmail(email)
        repo.signUpWithEmailAndPassword(name, email, password).getOrThrow()
    }

    private fun validateEmail(email: String) {
        if (!email.endsWith("@gmail.com")) {
            throw IllegalArgumentException("Email should end with @gmail.com")
        }
    }
}